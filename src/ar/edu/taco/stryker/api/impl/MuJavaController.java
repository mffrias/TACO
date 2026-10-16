package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import ar.edu.taco.Java7ClassLibrary;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.stryker.api.impl.input.MuJavaFeedback;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInputWrapper;
import ar.edu.taco.stryker.exceptions.FatalStrykerStageException;
import ar.edu.taco.utils.FileUtils;
import mujava.OpenJavaException;
import mujava.api.MutantsInformationHolder;
import mujava.api.Mutation;
import mujava.api.MutationOperator;
import mujava.app.MutantInfo;
import mujava.app.MutationRequest;
import mujava.app.Mutator;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;

/**
 * Main mutation stage of Stryker: explores the (higher-order) mutants of every base
 * program, choosing which sibling to generate next from the feedback computed for
 * the previous one.
 *
 * <p><b>Inputs (queue 1 of {@link StrykerStage#weedQueue}).</b> The worker thread takes
 * items with {@code takeFrom1()} and acknowledges them with {@code ack(0)}. An item is
 * one of:
 * <ul>
 * <li>a new base sent by {@link UnskippableMuJavaController} (the original program or one
 *     combination of "unskippable" left-hand-side mutations). It has no
 *     {@link MuJavaFeedback} and becomes a first-generation father;</li>
 * <li>a mutant sent back by {@link OpenJMLController} (RAC) or {@link DarwinistController}
 *     (TACO) with a {@link MuJavaFeedback} telling whether to queue its next sibling
 *     ({@code isGetSibling()}), from which position to advance
 *     ({@code getSkipUntilMutID()}) and whether it may become a father itself
 *     ({@code isFatherable()});</li>
 * <li>a feedback request ({@link MuJavaInput#isComputateFeedback()}): this thread runs
 *     {@link DarwinistController#computateFeedback} (variabilization) and then handles the
 *     resulting mutant as in the previous case.</li>
 * </ul>
 *
 * <p><b>Outputs.</b> Generated mutants go to the RAC stage through queue 2
 * ({@code enqueue2}), one at a time per father: {@link #fatherize} queues the first child
 * and every later sibling is queued by {@link #queueNextRelevantSibling} only when the
 * outcome of the previous one comes back to queue 1.
 *
 * <p><b>Mutation space.</b> Only mutations that affect a single line of the method and
 * are accepted by {@link #isSkippeableLeftMutation} are used here; the complementary
 * left-hand-side mutations are enumerated by {@link UnskippableMuJavaController}. Mutable
 * lines are the lines MuJava mutates (those annotated {@code //mutGenLimit N} with
 * {@code N > 0} in the subject); there is no automatic fault localization. A child of a
 * father is identified by an {@code Integer[] lineMutationIndexes} with one position per
 * mutable line: position 0 is the bottom-most mutable line and the least significant
 * position; value 0 leaves the line unchanged and a value {@code v > 0} selects
 * {@code row[row.length - v]} of that line's row in the mutation matrix built by
 * {@link #getMutatorsList} (rows are in ascending line order, so position {@code i} uses
 * row {@code n - 1 - i}). Siblings are visited by advancing this array like a counter
 * with carry, possibly jumping ahead according to the feedback (thesis, Chapter 3,
 * "Pruning along mutations traversal"); see
 * {@link #calculateNextRelevantSonMutationsLists}.
 *
 * <p><b>Batches and files.</b> Children are materialized in batches of
 * {@link #batchSize} by {@link #buildNextBatchSiblingsFile}: MuJava writes each mutant, the
 * batch is merged into one wrapper class whose methods are the mutated versions of the
 * method under repair, copied into a JML4C package and compiled with an external
 * {@code java -jar lib/stryker/jml4c.jar} process; non-compiling methods are dropped.
 * The copy of each new base, the output of each MuJava run and each batch wrapper get their
 * own directory under {@code java.io.tmpdir} (see {@link #createWorkingDirectory()}). Apart
 * from duplicate mutants, nothing is deleted during the run (the deletion code is commented
 * out), so these files accumulate.
 *
 * <p><b>Threading and lifecycle.</b> Singleton ({@link #getInstance()}) started by
 * {@link SimpleStrykerAPI#start()}, with one worker thread ({@link #getQtyOfThreads()}).
 * The {@link AbstractBaseController} queue inherited by this class is not read.
 * After each item the thread acks queue 1; when the ack reports that nothing is queued
 * or being processed in any of the three queues it shuts all controllers down, which is
 * how a run ends once the search space is exhausted. {@link UnskippableMuJavaController}
 * calls {@link #buildNextBatchSiblingsFile} on this instance from its own thread; the
 * fields it uses ({@link #jmlInputs}, {@link #filesHash}, {@link #classToMutate},
 * {@link #privateI}) are not synchronized, but in the normal flow that controller has
 * finished generating before this one receives its first input. {@link #fathers} and
 * {@link #filesHash} are never pruned and grow for the whole run.
 *
 * <p><b>Configuration.</b> {@link #feedbackOn}, {@link #fatherizationPruningOn} and
 * {@link #finishOnFirstFix} are public static flags, mostly read by the other
 * controllers. They are not bound to any configuration property: change them in code.
 *
 * <p><b>Quirks.</b> The worker loop only catches {@link InterruptedException}: any other
 * exception ends the thread without acking the current item, after which the run can no
 * longer end by exhaustion. Paths are assumed to use '/' ({@link #obtainClassNameFromFileName},
 * and {@code FILE_SEP} is used as a regular expression).
 */
public class MuJavaController extends AbstractBaseController<MuJavaInput> {

    //	private static AtomicInteger compilationFailCount = new AtomicInteger(0);

    /**
     * If {@code true}, a mutant that fails RAC ({@link OpenJMLController}), or for which TACO
     * finds a counterexample ({@link DarwinistController}), comes back to queue 1 as a
     * feedback request, so its next sibling is chosen with variabilization-based pruning. If
     * {@code false}, it comes back with plain settings (no skip position, fatherable), so
     * siblings are visited one by one. Also printed in the report header.
     */
    public static boolean feedbackOn = true;

    /**
     * If {@code true}, {@link DarwinistController} uses the variabilization result to decide
     * whether a rejected mutant may still become a father
     * ({@link MuJavaFeedback#setFatherable(boolean)}); if {@code false}, every such mutant is
     * marked fatherable.
     */
    public static boolean fatherizationPruningOn = true;

    /**
     * If {@code true}, {@link DarwinistController} shuts all controllers down as soon as the
     * first fix is found. If {@code false}, the fix is sent back to queue 1 (fatherable,
     * requesting its next sibling) and the search continues.
     */
    public static boolean finishOnFirstFix = true;

    private static final String FILE_SEP = System.getProperty("file.separator");

    // If it is set to false then it will be assumed that if two hashes are
    // equal then that means that the two files are equal. Which of course
    // it is not necessarily true.
    private static final boolean EXTRA_DUPLICATES_CHECK = false;

    private static final int NOT_PRESENT = -1;

    /**
     * Maximum number of non-duplicate mutants merged into one batch wrapper file by
     * {@link #buildNextBatchSiblingsFile}. {@link UnskippableMuJavaController} has its own
     * constant with the same value.
     */
    private static final int batchSize = 100;

    private static MuJavaController instance;

    private static Logger log = Logger.getLogger(MuJavaController.class);

    /** Counter used to name the {@code aOpenJMLInWrap<i>} batch directories. */
    private int privateI = 0;

    //    private int ownI = 0;

    /**
     * Only used as the initial capacity of {@link #jmlInputs} when the singleton is
     * constructed, so {@link #setMaxMethodsInFile(int)} has no effect.
     */
    private int maxMethodsInFile = 1;

    //    private Map<String, Integer> filenameToMutatedLine = Maps.newConcurrentMap();
    /**
     * MD5 digests of every mutant registered so far, across all fathers of this controller
     * and of {@link UnskippableMuJavaController}, used by {@link #mutateWithoutCompiling} to
     * discard duplicates. Values are file paths only when {@link #EXTRA_DUPLICATES_CHECK} is
     * on, and empty strings otherwise.
     */
    private Map<MsgDigest, String> filesHash = Maps.newHashMap();

    /** Scratch list holding the mutants of the batch being built. */
    protected List<OpenJMLInput> jmlInputs = new ArrayList<OpenJMLInput>(maxMethodsInFile);

    /** Scratch field: simple class name of the file currently being mutated. */
    private String classToMutate;

    /**
     * Every father registered by {@link #fatherize}, in registration order; entries are never
     * removed. {@link MuJavaFeedback#getFatherIndex()} of a mutant is its father's index in this
     * list ({@link OpenJMLController}, {@link WeedQueue} and the final report read it too).
     */
    private List<MuJavaInput> fathers = Lists.newArrayList();

    /**
     * Has no effect: {@code maxMethodsInFile} is only read when the singleton is constructed.
     * Called by {@link SimpleStrykerAPI#fixBug}.
     */
    public void setMaxMethodsInFile(int maxMethodsInFile) {
        this.maxMethodsInFile = maxMethodsInFile;
    }

    public synchronized static MuJavaController getInstance() {
        if (instance == null) {
            instance = new MuJavaController();
        }
        return instance;
    }

    private MuJavaController() {

    }

    /**
     * Returns the worker loop. For each item taken from queue 1 it:
     * <ol>
     * <li>if the item is a feedback request ({@link MuJavaInput#isComputateFeedback()}),
     *     replaces it with the result of {@link DarwinistController#computateFeedback}, run in
     *     this thread (the method is synchronized on the {@link DarwinistController} instance,
     *     so it never overlaps with that controller's candidate validation);</li>
     * <li>if the item's feedback requests a sibling, calls {@link #queueNextRelevantSibling};</li>
     * <li>if the item has no feedback (a new base) or is fatherable, calls {@link #fatherize};
     *     otherwise increments {@link StrykerStage#prunedFathers};</li>
     * <li>acks queue 1 and, if no item is left queued or being processed in any queue, shuts
     *     down all four controllers.</li>
     * </ol>
     * The loop ends when the controller shuts down (the blocking take is interrupted).
     * If {@code computateFeedback} throws, the exception is only logged and the placeholder
     * input (no feedback, null file name) goes on to {@link #fatherize} as if it were a new
     * base, which appears to end the thread with a {@link NullPointerException}.
     */
    @Override
    protected Runnable getRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    MuJavaInput input = StrykerStage.weedQueue.takeFrom1();

                    while (!willShutdown.get()) {
                        if (input.isComputateFeedback()) {
                            try {
                                input = DarwinistController.getInstance().computateFeedback(input.getInputForFeedback());
                            } catch (Exception e) {
                                log.debug("Exception e: "+ e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        }
                        if (input.getMuJavaFeedback() != null && input.getMuJavaFeedback().isGetSibling()) {
                            queueNextRelevantSibling(input);
                        }
                        //						                        if (input.getMuJavaFeedback() == null) {
                        if (input.getMuJavaFeedback() == null || input.getMuJavaFeedback().isFatherable()) {
                            fatherize(input, input.getMuJavaFeedback() == null);
                        } else if (input.getMuJavaFeedback() != null && !input.getMuJavaFeedback().isFatherable()) {
                            StrykerStage.prunedFathers++;
                            try {
//                                new File(input.getFilename()).delete();
                            } catch (Exception e) {}
                        }

                        if (StrykerStage.weedQueue.ack(0) == 0) {
                            UnskippableMuJavaController.getInstance().shutdownNow();
                            OpenJMLController.getInstance().shutdownNow();
                            DarwinistController.getInstance().shutdownNow();
                            MuJavaController.getInstance().shutdownNow();
                        }
                        input = StrykerStage.weedQueue.takeFrom1();
                    }
                } catch (InterruptedException e1) {
                    //e1.printStackTrace();
                }
            }
        };
    }

    /** One worker thread: the traversal state kept in this instance is not thread-safe. */
    @Override
    protected int getQtyOfThreads() {
        return 1;
    }

    /**
     * Returns the index where the package of a generated batch file starts.
     * Batch files live in {@code <tmp>/<n>/a<n>/aOpenJMLInWrap<i>/...}, and the package
     * starts at the {@code a<n>} directory. Looking for the first "/a" in the whole
     * path breaks when java.io.tmpdir itself has a directory starting with "a"
     * (e.g. /var/folders/a3/... on macOS), so search backwards from the
     * "aOpenJMLInWrap" directory instead.
     */
    static int batchPackageStart(String filename) {
        int wrapperDir = filename.indexOf(FILE_SEP + "aOpenJMLInWrap");
        if (wrapperDir == NOT_PRESENT) {
            return filename.indexOf(FILE_SEP + "a") + 1;
        }
        return filename.lastIndexOf(FILE_SEP + "a", wrapperDir - 1) + 1;
    }

    /**
     * Returns the simple class name of a source file path: the part after the last '/'
     * up to the last '.' (for example {@code /tmp/x/Foo.java} gives {@code Foo}). Only '/'
     * is recognized as a separator. Also used by {@link OpenJMLController} and
     * {@link DarwinistController}.
     */
    protected static String obtainClassNameFromFileName(String fileName) {
        int lastBackslash = fileName.lastIndexOf("/");
        int lastDot = fileName.lastIndexOf(".");

        if (lastBackslash == NOT_PRESENT) {
            lastBackslash = 0;
        } else {
            lastBackslash += 1;
        }
        if (lastDot == NOT_PRESENT) {
            lastDot = fileName.length();
        }

        return fileName.substring(lastBackslash, lastDot);
    }

    /**
     * Counts the combinations skipped by a jump from {@code prevLMI} to
     * {@code lineMutationIndexes} and adds that number to {@link StrykerStage#prunedMutations}.
     * Both arrays are read as mixed-radix numbers: position 0 is the least significant one
     * and the radix of position {@code i} is the number of mutations of its line plus one
     * (row {@code n - 1 - i} of {@code mutatorsList}). The skipped count is
     * {@code |next - prev| - 1}, so a move to the immediate successor counts 0. The
     * arithmetic uses {@code int} and can overflow for very large mutation spaces.
     *
     * @param prevLMI index array before the move
     * @param lineMutationIndexes index array after the move
     * @param mutatorsList the father's mutation matrix (rows in ascending line order)
     * @return {@code true} if at least one combination was skipped
     */
    public static boolean calculatePrunedMutations(Integer prevLMI[], Integer lineMutationIndexes[], Mutation mutatorsList[][]) {
        int prev = 0;
        for (int i = prevLMI.length - 1; i >= 0; --i) {
            int mult = 1;
            for (int j = i - 1 ; j >= 0; --j) {
                mult *= (mutatorsList[prevLMI.length - 1 - j].length + 1);
            }
            prev += prevLMI[i] * mult;
        }

        int next = 0;
        for (int i = lineMutationIndexes.length - 1; i >= 0; --i) {
            int mult = 1;
            for (int j = i - 1 ; j >= 0; --j) {
                mult *= (mutatorsList[lineMutationIndexes.length - 1 - j].length + 1);
            }
            next += lineMutationIndexes[i] * mult;
        }

        StrykerStage.prunedMutations += (Math.abs(next - prev) - 1);

        return (Math.abs(next - prev) - 1) > 0;
    }

    /**
     * Converts the feedback of a variabilization into the position of the current index
     * array at which the traversal must advance (the value {@code k} returned by
     * {@code getFeedback} in the thesis, Chapter 3).
     *
     * @param mutID 0-based index into {@code mutableLines}. {@link DarwinistController} stores
     *        {@code lastVariablizedMutID - 1} in {@link MuJavaFeedback#getSkipUntilMutID()}
     *        because the mutation IDs written into the variabilized source are 1-based
     *        (negative IDs mark lines that must not be variabilized)
     * @param curLineNumbersList the current father's mutable lines in descending order, as
     *        returned by {@link #getMutatorsList} (element {@code i} is the line of position
     *        {@code i})
     * @param mutableLines mutable lines of the first-generation father, in ascending order
     * @return the number of the current father's mutable lines below that line: its position
     *         when it is mutable in the current father, and otherwise the position of the next
     *         line above (the advance carries). Until 2026 this used {@code indexOf}, whose -1 for
     *         a line with no mutations left made the same candidate be queued again
     */
    private int getFeedbackIndex(Integer mutID, List<Integer> curLineNumbersList, List<Integer> mutableLines) {
        int lineNumber = mutableLines.get(mutID);

        // Positions count from the bottom-most line, so a line's position is the number of
        // current mutable lines below it. Unlike indexOf, this also works when the line has no
        // mutations left in this father (indexOf returned -1 and the same candidate was queued
        // again); the jump then carries to the next line above.
        int position = 0;
        for (Integer line : curLineNumbersList) {
            if (line > lineNumber) {
                position++;
            }
        }
        return position;
    }

    /**
     * Computes the next sibling to visit in a father's mutation space. The index array is
     * advanced like a counter with carry starting at position {@code feedback}, and every
     * lower position is reset to 0. This is the
     * {@code setToZeroAllPositionsInRange(0, k - 1)} / {@code advanceOneStartingAtIndex(k)}
     * step of the thesis' "Pruning along mutations traversal" (Chapter 3). With
     * {@code feedback == 0} it yields the immediate successor.
     *
     * <p>Before advancing:
     * <ul>
     * <li>the highest position {@code i} whose current value is recorded in
     *     {@code nonCompilableIndexes} (if any) raises the advance position to {@code i}, so
     *     every remaining combination that keeps that choice is skipped;</li>
     * <li>if {@code mutateRight == gaveUNSAT}, the starting value at the advance position is
     *     raised to {@code R}, skipping the line's remaining "right" mutations;</li>
     * <li>otherwise, if {@code !mutateRight && gaveUNSAT}, it is raised to {@code R + S},
     *     skipping its remaining "right" and skippable "left" mutations.</li>
     * </ul>
     * ({@code R} and {@code S} come from {@code sideChangeIndexes}, see
     * {@link #getMutatorsList}). While advancing, values recorded as non-compilable for the
     * position are skipped (each one also increments {@link StrykerStage#prunedMutations}),
     * and running past the last value of a position carries into the next one.
     *
     * <p>Side effects: adds the number of skipped combinations to
     * {@link StrykerStage#prunedMutations} through {@link #calculatePrunedMutations} (so the
     * non-compilable values skipped above are counted twice) and increments
     * {@link StrykerStage#relevantFeedbacksFound} when the jump skipped something.
     *
     * @param lineMutationIndexes the current index array; it is modified in place and
     *        returned, so callers pass a clone
     * @param mutatorsList the father's mutation matrix (rows in ascending line order;
     *        position {@code i} uses row {@code n - 1 - i})
     * @param feedback position (0-based) at which to advance
     * @param sideChangeIndexes per position, the pair {@code (R + S, R)} built by
     *        {@link #getMutatorsList}
     * @param mutateRight {@link MuJavaFeedback#isMutateRight()} of the previous sibling, set
     *        from the variabilization result
     * @param gaveUNSAT {@link MuJavaFeedback#isUNSAT()} of the previous sibling
     * @param nonCompilableIndexes per position, the values known not to compile (kept in the
     *        father's {@link MuJavaFeedback})
     * @return the mutations to apply (one per non-zero position) and the new index array, or
     *         {@code null} when the father's space is exhausted (the remaining combinations
     *         are then counted as pruned). {@link ArrayIndexOutOfBoundsException} and
     *         {@link NullPointerException} are logged and a pair with the partially updated
     *         array, possibly with no mutations, is returned
     */
    protected static ImmutablePair<List<Mutation>, Integer[]> calculateNextRelevantSonMutationsLists(
            Integer[] lineMutationIndexes, 
            Mutation[][] mutatorsList,
            int feedback,
            List<Pair<Integer, Integer>> sideChangeIndexes,
            boolean mutateRight, 
            boolean gaveUNSAT,
            Map<Integer, Set<Integer>> nonCompilableIndexes) {
        List<Mutation> ret = Lists.newArrayList();

        //TODO si se acaban tooodos los indices, que hacemos?? Creo que esto es cuando retorno null
        Integer prevLMI[] = lineMutationIndexes.clone();
        try {
            for (int i = nonCompilableIndexes.size() - 1; i >= 0; --i) {
                if (nonCompilableIndexes.get(i) != null && nonCompilableIndexes.get(i).contains(lineMutationIndexes[i])) {
                    feedback = feedback > i ? feedback : i;
                    break;
                }
            }

            int curIndex = lineMutationIndexes[feedback];

            if (((!mutateRight && !gaveUNSAT) || (mutateRight && gaveUNSAT))
                    && sideChangeIndexes.get(feedback) != null
                    && curIndex < sideChangeIndexes.get(feedback).getRight()) {
                curIndex = sideChangeIndexes.get(feedback).getRight();
            } else if (!mutateRight && sideChangeIndexes.get(feedback) != null && gaveUNSAT 
                    && curIndex < sideChangeIndexes.get(feedback).getLeft()) {
                curIndex = sideChangeIndexes.get(feedback).getLeft();
            }

            while (true) {
                while (curIndex + 1 > mutatorsList[lineMutationIndexes.length - feedback - 1].length) { //si me paso de rosca de la linea
                    if (++feedback >= lineMutationIndexes.length) {
                        Integer newLMI[] = new Integer[prevLMI.length];

                        for (int i = 0; i < newLMI.length; ++i) {
                            newLMI[i] = mutatorsList[newLMI.length - i - 1].length;
                        }

                        MuJavaController.calculatePrunedMutations(prevLMI, newLMI, mutatorsList);
                        StrykerStage.prunedMutations++; //Porque el calculador no ve el ultimo que se saltea en este caso

                        return null;
                    }
                    curIndex = lineMutationIndexes[feedback];
                }
                while (!(curIndex + 1 > mutatorsList[lineMutationIndexes.length - feedback - 1].length) 
                        && nonCompilableIndexes.get(feedback) != null && nonCompilableIndexes.get(feedback).contains(curIndex + 1)) {
                    //Mientras el siguiente no se pase de rosca y no compile
                    curIndex++;
                    StrykerStage.prunedMutations++;
                }
                if (!(curIndex + 1 > mutatorsList[lineMutationIndexes.length - feedback - 1].length) 
                        && !nonCompilableIndexes.get(feedback).contains(curIndex + 1)) {
                    //El siguiente no se pasa de rosca y compila
                    break;
                }
            }
            lineMutationIndexes[feedback] = curIndex + 1;
            for (int i = feedback - 1; i >= 0; --i) {
                lineMutationIndexes[i] = 0;
            }
            for (int i = 0; i < lineMutationIndexes.length; ++i) {
                if (lineMutationIndexes[i] > 0) {
                    ret.add(mutatorsList[lineMutationIndexes.length - i - 1][mutatorsList[lineMutationIndexes.length - i - 1].length - lineMutationIndexes[i]]);
                }
            }

            if (calculatePrunedMutations(prevLMI, lineMutationIndexes, mutatorsList)) {
                StrykerStage.relevantFeedbacksFound++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("CalculateNext: Threw ArrayIndexOutOfBoundsException");
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.error("CalculateNext: Null Pointer Exception");
        }

        return new ImmutablePair<List<Mutation>, Integer[]>(ret, lineMutationIndexes);
    }

    /**
     * Returns the predecessor of {@code lineMutationIndexes} in traversal order (decrement
     * with borrow; an all-zero array wraps around to the last combination). Callers use it
     * so that the next call to {@link #calculateNextRelevantSonMutationsLists} with feedback 0
     * yields {@code lineMutationIndexes} itself, e.g. to start a batch at a given sibling.
     * The argument is not modified.
     */
    Integer[] getPreviousIndexes(Integer[] lineMutationIndexes, Mutation[][] mutatorsList) {

        //TODO si se acaban tooodos los indices, que hacemos?? Creo que esto es cuando retorno null
        Integer prevLMI[] = lineMutationIndexes.clone();

        for (int i = 0; i < lineMutationIndexes.length; ++i) {
            if (lineMutationIndexes[i] == 0) {
                prevLMI[i] = mutatorsList[prevLMI.length - i - 1].length;
            } else {
                prevLMI[i]--;
                break;
            }
        }

        return prevLMI;
    }

    /**
     * Decides which mutation-stage controller handles a mutation. Every operator other than
     * PRVOL/PRVOL_SMART (the left-hand-side operators) is skippable; a PRVOL/PRVOL_SMART
     * mutation is skippable only if it just changes the tail of the left-hand side of an
     * assignment ({@code Mutation.isMutantATailChangeOfTheLeftSideOfAnAssignmentExpression()}).
     * Skippable mutations are explored by this controller, where feedback may skip them;
     * the rest are enumerated, without feedback, by {@link UnskippableMuJavaController},
     * which filters with the negation of this predicate.
     */
    protected static boolean isSkippeableLeftMutation(Mutation identifier) {
        if (!identifier.getMutOp().equals(MutationOperator.PRVOL) && !identifier.getMutOp().equals(MutationOperator.PRVOL_SMART)) {
            return true;
        } else {
            return identifier.isMutantATailChangeOfTheLeftSideOfAnAssignmentExpression();
        }
    }

    //<[][], <[i1, i2, i3], [<j1,k1>, <j2,k2>...]>>

    /**
     * Groups mutations by affected line and lays them out as the mutation matrix used by the
     * traversal.
     *
     * <p>There is one row per affected line, in ascending line order. Each row holds, in this
     * order: the unskippable "left" mutations (PRVOL/PRVOL_SMART rejected by
     * {@link #isSkippeableLeftMutation}), the skippable "left" ones, and the "right" ones
     * (every other operator, guard mutations included). Since an index value {@code v > 0}
     * selects {@code row[row.length - v]}, values {@code 1..R} select "right" mutations,
     * {@code R+1..R+S} skippable "left" ones and higher values unskippable "left" ones, where
     * {@code R} and {@code S} are the sizes of those groups.
     *
     * @param mutantIdentifiers the mutations to lay out (callers pass single-line mutations
     *        already filtered by skippability)
     * @return a pair whose left element is the matrix and whose right element is a pair of:
     *         the mutable line numbers in <em>descending</em> order (element {@code i} is the
     *         line of index-array position {@code i}), and, per position in the same order,
     *         the pair {@code (R + S, R)} used as {@code sideChangeIndexes} by
     *         {@link #calculateNextRelevantSonMutationsLists}
     */
    protected static ImmutablePair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> getMutatorsList(List<Mutation> mutantIdentifiers) {
        Map<Integer, Pair<Pair<List<Mutation>, List<Mutation>>, List<Mutation>>> theMap = Maps.newTreeMap();

        for (Mutation mutantIdentifier : mutantIdentifiers) {
            Integer affectedLine = mutantIdentifier.getAffectedLine();
            Pair<Pair<List<Mutation>, List<Mutation>>, List<Mutation>> theList = theMap.get(affectedLine);
            if (theList != null && theList.getRight() != null) {
                if (mutantIdentifier.getMutOp().equals(MutationOperator.PRVOL) || mutantIdentifier.getMutOp().equals(MutationOperator.PRVOL_SMART)) {
                    if (isSkippeableLeftMutation(mutantIdentifier)) {
                        theList.getLeft().getRight().add(mutantIdentifier);
                    } else {
                        theList.getLeft().getLeft().add(mutantIdentifier);
                    }
                } else if (mutantIdentifier.isGuardMutation()) {
                    theList.getRight().add(mutantIdentifier);
                } else {
                    theList.getRight().add(mutantIdentifier);
                }
            } else {
                List<Mutation> newRightList = Lists.newLinkedList();
                List<Mutation> newLeftSkippableList = Lists.newLinkedList();
                List<Mutation> newLeftUnskippableList = Lists.newLinkedList();
                if (mutantIdentifier.getMutOp().equals(MutationOperator.PRVOL) || mutantIdentifier.getMutOp().equals(MutationOperator.PRVOL_SMART)) {
                    if (isSkippeableLeftMutation(mutantIdentifier)) {
                        newLeftSkippableList.add(mutantIdentifier);
                    } else {
                        newLeftUnskippableList.add(mutantIdentifier);
                    }
                } else if (mutantIdentifier.isGuardMutation()) {
                    newRightList.add(mutantIdentifier);
                } else {
                    newRightList.add(mutantIdentifier);
                }

                theMap.put(affectedLine, new ImmutablePair<Pair<List<Mutation>, List<Mutation>>, List<Mutation>>(
                        new ImmutablePair<List<Mutation>, List<Mutation>>(newLeftUnskippableList, newLeftSkippableList), newRightList));
            }
        }

        Mutation[][] mutantIdentifiersList = new Mutation[theMap.size()][];
        List<Pair<Pair<List<Mutation>, List<Mutation>>, List<Mutation>>> theEntrySet = Lists.newLinkedList(theMap.values());
        List<Pair<Integer, Integer>> leftIndexList = Lists.newArrayList();
        LinkedList<Pair<Integer, Integer>> correctLeftIndexList = Lists.newLinkedList();


        List<List<Mutation>> theLists = Lists.newArrayList();
        for (Pair<Pair<List<Mutation>, List<Mutation>>, List<Mutation>> pair : theEntrySet) {
            List<Mutation> theList = Lists.newArrayList();

            List<Mutation> rightIdentifiers = pair.getRight();
            List<Mutation> leftSkippableIdentifiers = pair.getLeft().getRight();
            List<Mutation> leftUnskippableIdentifiers = pair.getLeft().getLeft();

            leftIndexList.add(new ImmutablePair<Integer, Integer>(rightIdentifiers.size() + leftSkippableIdentifiers.size(), rightIdentifiers.size()));

            theList.addAll(leftUnskippableIdentifiers);
            theList.addAll(leftSkippableIdentifiers);
            theList.addAll(rightIdentifiers);

            theLists.add(theList);
        }

        for (Pair<Integer, Integer> integer : leftIndexList) {
            correctLeftIndexList.addFirst(integer);
        }

        LinkedList<Integer> correctLineNumbersList = Lists.newLinkedList();

        for (Integer integer : theMap.keySet()) {
            correctLineNumbersList.addFirst(integer);
        }

        int i = 0;

        for (List<Mutation> theList : theLists) {
            Mutation[] curArray = new Mutation[theList.size()];
            int j = 0;
            for (Mutation mutantIdentifier : theList) {
                curArray[j] = mutantIdentifier;
                ++j;
            }
            mutantIdentifiersList[i] = curArray;
            ++i;
        }

        return new ImmutablePair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>>(mutantIdentifiersList, new ImmutablePair<List<Integer>, List<Pair<Integer, Integer>>>(correctLineNumbersList, correctLeftIndexList));
    }

    /**
     * Registers {@code input} as a father, i.e. a program whose own mutants will be explored
     * (higher-order mutation), and queues its first child to the RAC stage.
     *
     * <ol>
     * <li>If {@code first}, removes the {@code //mutGenLimit 0} markers from the input file in
     *     place ({@link StrykerJavaFileInstrumenter#cleanMutGenLimit0}) and copies the file into
     *     a new temporary directory; the copy is the father's file. Otherwise the father's file
     *     is the input file itself, after
     *     {@link StrykerJavaFileInstrumenter#decrementUnmutatedLimits} has decremented in place
     *     the {@code //mutGenLimit} counters of the mutable lines that the input's last mutation
     *     did not touch.</li>
     * <li>Runs MuJava on the input file and the method under repair with the input's operators
     *     (sorted by name), writing into another new temporary directory; the time is added to
     *     {@link StrykerStage#muJavaMillis}. Keeps the single-line mutations accepted by
     *     {@link #isSkippeableLeftMutation} and builds the matrix with {@link #getMutatorsList}.
     *     If nothing is left the input is a leaf and the method returns.</li>
     * <li>Gives the father a {@link MuJavaFeedback} with an all-zero index array.
     *     {@code mutableLines} are the first-generation father's lines (inherited through the
     *     input's feedback when not {@code first}) and {@code curMutableLines} this father's
     *     own. Appends it to {@link #fathers} (its index there becomes the father index of its
     *     children) and builds its first batch with {@link #buildNextBatchSiblingsFile}.</li>
     * <li>Stores the batch data in the father and calls {@link #queueNextRelevantSibling} with
     *     a placeholder sibling positioned just before the first mutant of the batch, which
     *     sends that mutant to queue 2.</li>
     * </ol>
     * {@link ClassNotFoundException}, {@link OpenJavaException} and {@link ParseTreeException}
     * are printed and swallowed, and I/O errors while copying are ignored. If MuJava returns
     * no entry for the method, a {@link NullPointerException} escapes.
     *
     * @param input the base or mutant to fatherize
     * @param first {@code true} for a new base, which has no {@link MuJavaFeedback}
     */
    public void fatherize(MuJavaInput input, boolean first) {
        File firstDir = null;
        File firstFile = null;
        if (first) {
            StrykerJavaFileInstrumenter.cleanMutGenLimit0(input);
            try {
                firstDir = createWorkingDirectory();
                File old = new File(input.getFilename());
                firstFile = new File(firstDir.getAbsolutePath() + input.getFilename().substring(input.getFilename().lastIndexOf(FILE_SEP)));
                firstFile.createNewFile();
                Files.copy(old, firstFile);
            } catch (IOException e) {
                // Handle Exceptions
            }
        } else {
            StrykerJavaFileInstrumenter.decrementUnmutatedLimits(input);
        }
        MuJavaInput inputAsFather = new MuJavaInput(first ? firstFile.getAbsolutePath() : input.getFilename(), 
                input.getMethod(), 
                input.getMutantsToApply(), input.getQtyOfGenerations(), input.getConfigurationFile(), 
                input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                input.getFullyQualifiedFileName(), input.getMethodUnderAnalysis());
        try {
            File fileToMutate;
            String methodToCheck;
            TreeSet<MutationOperator> mutOps;
            MuJavaInput muJavaInput;

            fileToMutate = new File(input.getFilename());
            if (!fileToMutate.exists()) {
                throw new IllegalStateException("The file " + input.getFilename() + " doesn't exist. Can't continue.");
                //              return Lists.newArrayList();
            }
            methodToCheck = input.getMethod();
            mutOps = Sets.newTreeSet(new Comparator<MutationOperator>() {
                @Override
                public int compare(MutationOperator o1, MutationOperator o2) {
                    return o1.name().compareTo(o2.name());
                }
            });
            mutOps.addAll(input.getMutantsToApply());
            classToMutate = obtainClassNameFromFileName(input.getFilename());
            muJavaInput = inputAsFather;

            File tmpDir = createWorkingDirectory();

            log.debug("Generating mutants...");

            String[] methods1 = new String[] {methodToCheck};
            MutationOperator[] mutops1 = new MutationOperator[mutOps.size()];
            mutOps.toArray(mutops1);
            MutationRequest req1 = new MutationRequest(classToMutate, methods1, mutops1, 
                    fileToMutate.getParent() + FILE_SEP, tmpDir.getAbsolutePath() + FILE_SEP);
            Mutator mut = new Mutator(req1);

            long nanoPrev = System.currentTimeMillis();
            Map<String, MutantsInformationHolder> mutantsInformationHoldersMap = mut.obtainMutants();
            StrykerStage.muJavaMillis += System.currentTimeMillis() - nanoPrev;
            MutantsInformationHolder mutantsInformationHolder = null;
            for (Entry<String, MutantsInformationHolder> mutant : mutantsInformationHoldersMap.entrySet()) {
                if (mutant.getKey().equalsIgnoreCase(input.getMethod())) {
                    mutantsInformationHolder = mutant.getValue();
                }
            }
            List<Mutation> mutantIdentifiers = mutantsInformationHolder.getMutantsIdentifiers();
            //Me quedo solo con los mutant identifiers que afectan solo 1 linea en el metodo en cuestion.
            mutantIdentifiers = new LinkedList<Mutation>(Collections2.filter(mutantIdentifiers, new Predicate<Mutation>() {
                public boolean apply(Mutation arg0) {
                    return arg0.isOneLineInMethodOp() && isSkippeableLeftMutation(arg0);
                };
            }));

            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData = getMutatorsList(mutantIdentifiers);
            Mutation[][] mutatorsList = mutatorsData.getLeft();
//            for (Mutation[] mutations : mutatorsList) {
//				System.out.println("Nueva linea");
//            	for (Mutation mutation : mutations) {
//					System.out.println(mutation);
//				}
//			}
            if (mutatorsList.length == 0) {
                return; //No tiene más mutaciones posibles, es una hoja del arbol de mutaciones.
            }

            List<Integer> mutableLines, curMutableLines;
            List<Integer> invertedMutableLinesListForFirst = mutatorsData.getRight().getLeft();
            LinkedList<Integer> straightMutableLinesListForFirst = Lists.newLinkedList();
            for (Integer integer : invertedMutableLinesListForFirst) {
                straightMutableLinesListForFirst.addFirst(integer);
            }
            curMutableLines = Lists.newArrayList(straightMutableLinesListForFirst);
            if (first) {
                mutableLines = curMutableLines;
            } else {
                mutableLines = input.getMuJavaFeedback().getMutableLines();
            }

            Integer[] lineMutationIndexes = new Integer[mutatorsList.length];
            for (int i = 0; i < lineMutationIndexes.length; ++i) {
                lineMutationIndexes[i] = 0;
            }//inicializar todo en 0 si no lo hace

            //            if (first) {
            //                lineMutationIndexes[0] = 9;
            //                lineMutationIndexes[1] = 3;
            //                lineMutationIndexes[2] = 0;
            //                lineMutationIndexes[3] = 0;
            //            }


            MuJavaFeedback newFeedback = new MuJavaFeedback(
                    StrykerJavaFileInstrumenter.parseMethodStartLine(
                            muJavaInput.getFilename(), muJavaInput.getMethod()),
                            lineMutationIndexes, mutatorsList, null, mutableLines, curMutableLines);
            newFeedback.setSkipUntilMutID(null);
            //            if (input.getMuJavaFeedback() != null) {
            //                newFeedback.setMutantsInformationHolder(input.getMuJavaFeedback().getMutantsInformationHolder());
            //                newFeedback.setMut(input.getMuJavaFeedback().getMut());
            //                //                newFeedback.setLastMutatedLines(input.getMuJavaFeedback().getLastMutatedLines());
            //            }

            newFeedback.setMut(mut);
            newFeedback.setMutantsInformationHolder(mutantsInformationHolder);
            muJavaInput.setMuJavaFeedback(newFeedback);
            muJavaInput.setMutatorsData(mutatorsData);


            fathers.add(muJavaInput);//se agrega el nuevo padre a la lista de padres

            OpenJMLInputWrapper wrapper = buildNextBatchSiblingsFile(muJavaInput, fathers.size() - 1, batchSize, lineMutationIndexes, false, true);

            if (wrapper == null) {
                log.warn("MJC: A father with no children, skipping.");
                return;
            }

            MuJavaInput baseSibling = new MuJavaInput(muJavaInput.getFilename(), muJavaInput.getMethod(), 
                    muJavaInput.getMutantsToApply(), muJavaInput.getQtyOfGenerations(), muJavaInput.getConfigurationFile(), 
                    muJavaInput.getOverridingProperties(), muJavaInput.getOriginalFilename(), muJavaInput.getSyncObject(),
                    muJavaInput.getFullyQualifiedFileName(), muJavaInput.getMethodUnderAnalysis());


            MuJavaFeedback baseSiblingFeedback = new MuJavaFeedback(
                    StrykerJavaFileInstrumenter.parseMethodStartLine(
                            baseSibling.getFilename(), baseSibling.getMethod()),
                            getPreviousIndexes(wrapper.getNextRelevantSiblingsMutationsLists().getRight(), mutatorsList), 
                            muJavaInput.getMuJavaFeedback().getLineMutatorsList(), new ArrayList<Integer>(), 
                            muJavaInput.getMuJavaFeedback().getMutableLines(), muJavaInput.getMuJavaFeedback().getCurMutableLines());

            baseSiblingFeedback.setMut(mut);
            baseSiblingFeedback.setMutantsInformationHolder(mutantsInformationHolder);
            baseSiblingFeedback.setFatherIndex(fathers.size() - 1);
            baseSiblingFeedback.setMutateRight(true);
            baseSibling.setMuJavaFeedback(baseSiblingFeedback);

            muJavaInput.setIndexesToMethod(wrapper.getIndexesToMethod());
            muJavaInput.setUncompilableChildrenMethodNames(wrapper.getUncompilableMethods());
            muJavaInput.setChildrenFilename(wrapper.getFilename());
            muJavaInput.setJml4cFilename(wrapper.getJml4cFilename());
            muJavaInput.setJml4cPackage(wrapper.getJml4cPackage());
            muJavaInput.setPresentIndexes(Sets.newHashSet(wrapper.getIndexesToMethod().keySet()));
            muJavaInput.setDuplicateMethodIndexes(wrapper.getDuplicateMethodIndexes());

            queueNextRelevantSibling(baseSibling);

        } catch (ClassNotFoundException | OpenJavaException e) {
            e.printStackTrace();
            // Handle Exceptions
        } catch (ParseTreeException e) {
            e.printStackTrace();
            // Handle Exceptions
        }
    }

    /**
     * Adds a mutant already written by MuJava to the batch being built. Despite the name,
     * nothing is mutated or compiled here (the "Compilation succeeded" log message is
     * misleading).
     *
     * <p>Always increments {@link StrykerStage#mutationsGenerated}. If the mutant's MD5 digest
     * is already in {@link #filesHash} it is a duplicate: its file is deleted,
     * {@link StrykerStage#duplicateMutations} is incremented and {@code null} is returned.
     * Otherwise the digest is recorded and the file is wrapped in an {@link OpenJMLInput}
     * carrying a new {@link MuJavaFeedback} for the child (its index array and mutated lines,
     * the father's matrix and mutable lines, {@code fatherIndex}), which is appended to
     * {@link #jmlInputs} and returned.
     *
     * @param mutantIdentifier the mutant written by MuJava (path and MD5 digest)
     * @param fileToMutate the father's file (only logged)
     * @param muJavaInput the father
     * @param fatherIndex index of the father in the calling controller's fathers list
     * @param childLineMutationIndexes index array of the child
     * @param mih MuJava data of the father, stored in the child's feedback
     * @param mut MuJava mutator of the father, stored in the child's feedback
     * @param lastMutatedLines lines changed by the child's mutations
     * @return the new input, or {@code null} for a duplicate
     */
    @SuppressWarnings("unused")
    protected OpenJMLInput mutateWithoutCompiling(
            MutantInfo mutantIdentifier, 
            File fileToMutate, 
            MuJavaInput muJavaInput, 
            int fatherIndex, 
            Integer[] childLineMutationIndexes,
            MutantsInformationHolder mih, 
            Mutator mut, 
            List<Integer> lastMutatedLines) {
        StrykerStage.mutationsGenerated++;

        if (muJavaInput.getMuJavaFeedback().getLineMutationIndexes().length < childLineMutationIndexes.length) {
            log.error("MJC: Error muJavaInput.getMuJavaFeedback().getLineMutationIndexes().length < childLineMutationIndexes.length");
        }
        log.debug("Generation finished. Generated mutants: 1");
        log.debug("Creating files for mutants");
        log.debug("Check that mutant is unique: "+ mutantIdentifier);
        File tempFile = new File(mutantIdentifier.getPath());

        MsgDigest msgDigest = null;
        DigestOutputStream dos;
        File duplicatesTempFile = null;
        if (EXTRA_DUPLICATES_CHECK) {
            try {
                String content = FileUtils.readFile(mutantIdentifier.getPath());
                String tunedContent = "";
                String lines[] = content.split("\n");
                for (int i = 0; i < lines.length; ++i) {
                    String line = lines[i];
                    if (line.contains("//mutGenLimit")) {
                        tunedContent += line.substring(0, line.indexOf("//mutGenLimit")) + "\n";
                    } else {
                        tunedContent += line + "\n";
                    }
                }
                duplicatesTempFile = File.createTempFile("forDuplicates", null);
                dos = new DigestOutputStream(new FileOutputStream(duplicatesTempFile, false), MessageDigest.getInstance("MD5"));
                dos.write(tunedContent.getBytes());
                dos.flush();
                dos.close();
                byte[] digest = dos.getMessageDigest().digest();
                msgDigest = new MsgDigest(digest);
            } catch (Exception e) {
                // Handle Exceptions
            }
        } else {
            msgDigest = new MsgDigest(mutantIdentifier.getMD5digest());
            duplicatesTempFile = tempFile;
        }
        log.debug("fileToMutate= "+fileToMutate);
        log.trace("fileToMutate.getAbsolutePath()= "+fileToMutate.getAbsolutePath());
        if (filesHash.containsKey(msgDigest)) {
            log.debug("filesHash.containsKey(msgDigest) = "+filesHash.containsKey(msgDigest));
            if (EXTRA_DUPLICATES_CHECK && isFalseDuplicate(filesHash.get(msgDigest), duplicatesTempFile)) {
                // If it is a false duplicate we don't have to delete the file
                log.debug("False duplicated file");
                //                }
            } else {
                // We have to delete this new mutant since it will be a duplicate
                log.debug("Duplicated file");
                if (!tempFile.delete()) {
                    log.error("Couldn't remove file " + tempFile.getName());
                }
                StrykerStage.duplicateMutations++;
                return null;
            }
        }

        log.info("Compilation succeeded. Adding this file");

        if (EXTRA_DUPLICATES_CHECK)
            filesHash.put(msgDigest, duplicatesTempFile.getAbsolutePath());
        else
            filesHash.put(msgDigest, "");

        MuJavaFeedback newFeedback = new MuJavaFeedback(
                StrykerJavaFileInstrumenter.parseMethodStartLine(
                        duplicatesTempFile.getAbsolutePath(), muJavaInput.getMethod()), childLineMutationIndexes, 
                        muJavaInput.getMuJavaFeedback().getLineMutatorsList(), lastMutatedLines, 
                        muJavaInput.getMuJavaFeedback().getMutableLines(), muJavaInput.getMuJavaFeedback().getCurMutableLines());
        newFeedback.setMut(mut);
        newFeedback.setMutantsInformationHolder(mih);
        newFeedback.setFatherIndex(fatherIndex);
        OpenJMLInput output = new OpenJMLInput(duplicatesTempFile.getAbsolutePath(),
                muJavaInput.getMethod(),
                muJavaInput.getConfigurationFile(),
                muJavaInput.getOverridingProperties(),
                muJavaInput.getOriginalFilename(),
                newFeedback,
                muJavaInput.getMutantsToApply(),
                muJavaInput.getSyncObject(),
                muJavaInput.getFullyQualifiedFileName(),
                muJavaInput.getMethodUnderAnalysis());
        log.debug("Adding task to the list");
        jmlInputs.add(output);
        //            StrykerStage.mutationsQueuedToOJMLC++;
        return output;
    }

    public static final String PATH_SEP = System.getProperty("path.separator");

    /**
     * Package suffix of the JML4C copy of a batch file. The full package is
     * {@code a<d>.aOpenJMLInWrap<i>.ar.edu.itba.stryker.mutants} (see
     * {@link #createJMLInputWrapper}).
     */
    public static final String MUTANTS_DEST_PACKAGE = "ar.edu.itba.stryker.mutants";

    //    private static final String CLASSPATH = System.getProperty("java.class.path");


    /**
     * Writes a copy of a batch wrapper file into the given package directory, adapted so that
     * JML4C can compile it and the RAC stage can run it:
     * <ul>
     * <li>the first line containing "package" is replaced by
     *     {@code package <packageToWrite>;}. If a line containing "import" comes first, the
     *     package line is written followed by the line after it, so that import line is lost.
     *     Lines before either one are dropped;</li>
     * <li>{@link StringsToWriteInFile#reachMethod}, a runtime implementation of {@code reach},
     *     is inserted right after the opening brace of the first class;</li>
     * <li>JML {@code \reach(o, T, f)} calls are rewritten as calls to that method,
     *     {@code reach(o, T.class, "f")} (three arguments without nested parentheses are
     *     assumed);</li>
     * <li>the fully qualified name of the class under analysis (the {@code classToCheck}
     *     setting with '_' replaced by '.') is rewritten to
     *     {@code <packageToWrite>.<SimpleName>}, except on lines where it is followed by
     *     "Node" (helper classes in the same package, e.g. {@code BinomialHeapNode}).</li>
     * </ul>
     *
     * @param filename the batch wrapper file
     * @param tempFilename destination directory, with a trailing separator; created if missing
     * @param packageToWrite package of the copy
     * @return the path of the copy (destination directory plus file name), returned even if
     *         writing failed (I/O errors are only printed)
     */
    protected static String adaptSiblingsFileToJML4C(String filename, String tempFilename, String packageToWrite) {
        String packageSentence = "package "+packageToWrite+";\n";
        String oldFullyQualifiedClassName = TacoConfigurator.getInstance().getClassToCheck().replace('_', '.');
        try {
            File destFile = new File(tempFilename);
            destFile.mkdirs();
            tempFilename += filename.substring(filename.lastIndexOf(FILE_SEP) + 1);

            destFile = new File(tempFilename);
            destFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(destFile);

            Scanner scan = new Scanner(new File(filename));
            scan.useDelimiter("\n");
            while(scan.hasNext()){
                String str = scan.next();
                if( str.contains("package")){
                    fos.write(packageSentence.getBytes(Charset.forName("UTF-8")));
                    break;
                } else if (str.contains("import")) {
                    fos.write(packageSentence.getBytes(Charset.forName("UTF-8")));
                    fos.write((scan.next() + "\n").getBytes(Charset.forName("UTF-8")));
                    break;
                } 
            }

            boolean reachAlreadyWritten = false;
            boolean classFound = false;
            while(scan.hasNext()){
                boolean lineAlreadyWritten = false;
                String str = scan.next();
                if(!reachAlreadyWritten && str.contains(" class ")) {
                    classFound = true;
                    if(str.contains("{")) {
                        int index = str.indexOf("{");
                        String beforeBrace = str.substring(0, index);
                        String brace = str.substring(index, index + 1);
                        String afterBrace = str.substring(index + 1, str.length());

                        fos.write((beforeBrace + "\n").getBytes(Charset.forName("UTF-8")));
                        fos.write((brace + "\n").getBytes(Charset.forName("UTF-8")));
                        reachAlreadyWritten = true;
                        lineAlreadyWritten = true;
                        fos.write((StringsToWriteInFile.reachMethod + "\n").getBytes(Charset.forName("UTF-8")));
                        fos.write((afterBrace + "\n").getBytes(Charset.forName("UTF-8")));
                    }
                }
                if(!reachAlreadyWritten && classFound && str.contains("{")) {
                    int index = str.indexOf("{");
                    String beforeBrace = str.substring(0, index);
                    String brace = str.substring(index, index + 1);
                    String afterBrace = str.substring(index + 1, str.length());

                    fos.write((beforeBrace + "\n").getBytes(Charset.forName("UTF-8")));
                    fos.write((brace + "\n").getBytes(Charset.forName("UTF-8")));
                    reachAlreadyWritten = true;
                    lineAlreadyWritten = true;
                    fos.write((StringsToWriteInFile.reachMethod + "\n").getBytes(Charset.forName("UTF-8")));
                    fos.write((afterBrace + "\n").getBytes(Charset.forName("UTF-8")));
                }
                if(str.contains("\\reach")) {
                    String[] eachReach = str.split("\\\\reach");
                    for(int i = 0; i < eachReach.length; i++) {
                        /*
                         * I can be sure of this, because since the line starts always with @
                         * it is wrong to append \\reach to the first string...
                         */
                        if(i != 0) {
                            eachReach[i] = "\\reach" + eachReach[i] + " ";
                        }
                    }

                    for(int i = 0; i < eachReach.length; i++) {
                        String each = eachReach[i];

                        if(each.contains("\\reach") == false) {
                            continue;
                        }

                        String beforeReplacement = "\\reach";
                        String afterReplacement = "reach"; 
                        each = each.replace(beforeReplacement, afterReplacement);

                        int openBracketIndex = each.indexOf(afterReplacement) + afterReplacement.length() + 1;
                        int closeBracketIndex = each.substring(openBracketIndex).indexOf(")") + openBracketIndex;

                        String beforeArgs = each.substring(0, openBracketIndex);
                        String afterArgs = each.substring(closeBracketIndex, each.length());
                        String args = each.substring(openBracketIndex, closeBracketIndex);

                        String[] splittedArgs = args.split(",");

                        String modifiedString = "";
                        modifiedString += beforeArgs;
                        modifiedString += splittedArgs[0];
                        modifiedString += ",";
                        modifiedString += splittedArgs[1] + ".class";
                        modifiedString += ",";
                        modifiedString += "\"" + splittedArgs[2] + "\"";
                        modifiedString += afterArgs;

                        eachReach[i] = modifiedString;
                    }

                    String lineToWrite = "";
                    for(String each : eachReach) {
                        lineToWrite += each;
                    }

                    lineAlreadyWritten = true;
                    fos.write((lineToWrite + "\n").getBytes(Charset.forName("UTF-8")));
                }
                if(!lineAlreadyWritten) {
                	if (str.contains(oldFullyQualifiedClassName)) {
                    	String[] classToCheckeSplit = TacoConfigurator.getInstance().getClassToCheck().split("_");
                    	String classUnderAnalysis = classToCheckeSplit[classToCheckeSplit.length - 1];
                    	if (!str.matches(".*" + oldFullyQualifiedClassName + "[^\\n]*Node.*")) {
                    	    str = str.replace(oldFullyQualifiedClassName, packageToWrite + "." + classUnderAnalysis);
                    	}
                    	fos.write((str+ "\n").getBytes(Charset.forName("UTF-8")));
                    } else {
                    	fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    }
                }
            }
            fos.close();
            scan.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFilename;
    }

    /**
     * Debugging helper: lists the canonical names of the classes loaded by
     * {@code classLoader}, read by reflection from the private {@code ClassLoader.classes}
     * field. Not called anywhere.
     *
     * @return the class names, or {@code null} if the field cannot be read
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<String> getLoadedClasses(final ClassLoader classLoader) {
        List<String> classNames = null;
        try {
            Field f = ClassLoader.class.getDeclaredField("classes");
            f.setAccessible(true);
            List<Class> classes = new ArrayList<>((Vector<Class>) f.get(classLoader));
            classNames = new ArrayList<>(classes.size());
            for (Class c : classes) {
                classNames.add(c.getCanonicalName());
            }
            return classNames;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return classNames;
    }

    /**
     * Generates, merges and compiles the next batch of children of a father.
     *
     * <ol>
     * <li><b>Generation.</b> Starting after {@code fromLineMutationIndexes}, repeatedly takes the
     *     successor index array ({@link #calculateNextRelevantSonMutationsLists} with feedback 0,
     *     which skips the father's known non-compilable indexes), has MuJava write the mutant
     *     that applies all its mutations to a fresh copy of the father's AST, and registers it
     *     with {@link #mutateWithoutCompiling}, which discards MD5 duplicates. Stops after
     *     {@code batchSize} non-duplicate mutants or when the space is exhausted. The index keys
     *     of duplicates are recorded so that callers skip them.</li>
     * <li><b>Compilation.</b> Merges the batch into one class with
     *     {@link #createJMLInputWrapper}, copies it into a JML4C package with
     *     {@link #adaptSiblingsFileToJML4C} and compiles the copy in an external process,
     *     {@code <java.home>/bin/java -jar <user.dir>/lib/stryker/jml4c.jar}: it uses the same
     *     Java installation as Stryker, and the working directory must be the project root. The
     *     classpath is jml4c.jar, the batch root and the current classpath minus the running
     *     JDK's jars ({@link ar.edu.taco.Java7ClassLibrary#withoutJdkEntries}) and the Eclipse,
     *     MuJava, Guava, Commons, ANTLR, javassist, recoder, reflections and jml-release jars.
     *     JML4C reads the Java 7 class library ({@code -bootclasspath}, see
     *     {@link ar.edu.taco.Java7ClassLibrary}) and gets {@code -XX:MaxPermSize} only on Java 7.
     *     The time is added to {@link StrykerStage#compilationMillis}.</li>
     * <li><b>Non-compiling mutants.</b> If compilation fails, the {@code "(at line N)"} errors
     *     on stderr that mention the class are mapped to the wrapper method containing them, and
     *     those mutants are removed from the batch. If an error's line, taken relative to the
     *     start of its method, is one of the father's {@code curMutableLines} and the error is
     *     not "Unreachable code", the value that mutant uses at that line's position is added to
     *     the father's {@code nonCompilableIndexes}, so later traversals skip it. Then a new
     *     wrapper is built (in a new temporary directory) from the remaining mutants and
     *     compiled again. If no mutant is left, a new batch is started recursively, or
     *     {@code null} is returned if the space was exhausted.</li>
     * </ol>
     * Statistics updated: {@link StrykerStage#mutationsGenerated} and
     * {@link StrykerStage#duplicateMutations} (through {@link #mutateWithoutCompiling}),
     * {@link StrykerStage#nonCompilableMutations}, {@link StrykerStage#compilationMillis} and
     * {@link StrykerStage#nonCompilableMutationIndexesFound}. The last one is increased by the
     * total size of all the father's non-compilable sets on every failed compilation, so it
     * over-counts.
     *
     * <p>{@link UnskippableMuJavaController} also calls this method on this instance. Every
     * exception is printed and swallowed; the method then returns {@code null} or the last
     * wrapper built, which may be incomplete.
     *
     * @param father the father whose children are generated (must already have its mutators
     *        data and {@link MuJavaFeedback})
     * @param fatherIndex index of {@code father} in the calling controller's fathers list,
     *        stored in the children's feedback
     * @param batchSize maximum number of non-duplicate mutants in the batch
     * @param fromLineMutationIndexes the batch starts at the successor of this index array
     * @param fullBatch if {@code true}, keeps generating until the space is exhausted (no
     *        caller passes {@code true})
     * @param shouldDelete would delete the father's files once it has no children left, but
     *        the deletions are commented out, so it has no effect
     * @return the wrapper with the batch's map from index key ({@code "[ i0 i1 ... ]"}) to
     *         {@link OpenJMLInput}, the keys of non-compilable and duplicate mutants, the JML4C
     *         file and package, and the first mutant of the batch
     *         ({@code getNextRelevantSiblingsMutationsLists()}); {@code null} if no mutant could
     *         be produced
     */
    public OpenJMLInputWrapper buildNextBatchSiblingsFile(MuJavaInput father, int fatherIndex, int batchSize, Integer[] fromLineMutationIndexes, boolean fullBatch, boolean shouldDelete) {
        OpenJMLInputWrapper wrapper = null;
        try {
            Mutation[][] mutatorsList = father.getMuJavaFeedback().getLineMutatorsList();

            Integer[] lineMutationIndexes = fromLineMutationIndexes;

            File fileToMutate;
            String methodToCheck;

            fileToMutate = new File(father.getFilename());
            if (!fileToMutate.exists()) {
                throw new IllegalStateException("The file " + father.getFilename() + " doesn't exist. Can't continue.");
            }
            methodToCheck = father.getMethod();
            classToMutate = obtainClassNameFromFileName(father.getFilename());

            //Encolo el hijo
            Map<String, OpenJMLInput> indexesToInput = Maps.newTreeMap();

            log.debug("Generating mutants...");

            Mutator mut = father.getMuJavaFeedback().getMut();

            long nanoPrev = System.currentTimeMillis();
            MutantsInformationHolder mutantsInformationHolder = father.getMuJavaFeedback().getMutantsInformationHolder();

            CompilationUnit backup = mutantsInformationHolder.getCompUnit();

            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData = father.getMutatorsData();
            mutatorsList = mutatorsData.getLeft();
            if (mutatorsList.length == 0) {
                //TODO revisar esto
                return null;
            }
            jmlInputs.clear();
            ImmutablePair<List<Mutation>, Integer[]> firstOfBatch = null;
            Set<String> duplicateMethods = Sets.newHashSet();
            String indexes = "[ ";
            for (Integer lineMutationIndex : lineMutationIndexes) {
                indexes += lineMutationIndex + " ";
            }
            indexes += "]";

            ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingMutationsLists = null;

            boolean shouldEnd = false;
            while (jmlInputs.isEmpty()) {
                log.warn("MJC: Generating a batch from father index: " + fatherIndex + " starting from indexes: " + indexes + "...");
                shouldEnd = false;
                boolean firstSet = false;
                for (int i = 0; i < batchSize; ++i) {
                    nextRelevantSiblingMutationsLists = 
                            calculateNextRelevantSonMutationsLists(lineMutationIndexes.clone(), mutatorsList, 0, 
                                    mutatorsData.getRight().getRight(), true, false, father.getMuJavaFeedback().getNonCompilableIndexes());
                    if (nextRelevantSiblingMutationsLists == null) {
                        shouldEnd = true;
                        break;
                    } else if (nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length) {
                        log.error("MJC: Error nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length");
                    } else if (nextRelevantSiblingMutationsLists.getLeft().size() == 0) {
                        log.error("MJC: Error nextRelevantSiblingMutationsLists.getLeft().size() == 0");
                    }

                    lineMutationIndexes = nextRelevantSiblingMutationsLists.getRight();

                    if (!Mutator.checkCompatibility(nextRelevantSiblingMutationsLists.getLeft())) {
                        log.error("MJC: Generated a list of mutant identifiers with at least 2 mutations affecting the same line");
                        throw new IllegalArgumentException();
                    }

                    List<Integer> mutatedLines = Lists.newArrayList();

                    for (Mutation identifier : nextRelevantSiblingMutationsLists.getLeft()) {
                        Integer affectedLine = identifier.getAffectedLine();
                        mutatedLines.add(affectedLine);
                    }

                    List <Mutation> curIdentifiers = mutantsInformationHolder.getMutantsIdentifiers();
                    curIdentifiers.clear();
                    curIdentifiers.addAll(nextRelevantSiblingMutationsLists.getLeft());
                    mutantsInformationHolder.setCompilationUnit((CompilationUnit) backup.makeRecursiveCopy_keepOriginalID());
                    List<MutantInfo> mil = mut.writeMutants(father.getMethod(), mutantsInformationHolder, true);
                    MutantInfo mutantInfo = mil.get(0);
                    mut.resetMutantFolders();

                    OpenJMLInput jmlInput =  mutateWithoutCompiling(mutantInfo, fileToMutate, father, 
                            fatherIndex, lineMutationIndexes, 
                            mutantsInformationHolder, mut, mutatedLines);
                    //                System.out.println(ownI++);
                    indexes = "[ ";
                    for (Integer lineMutationIndex : lineMutationIndexes) {
                        indexes += lineMutationIndex + " ";
                    }
                    indexes += "]";

                    if (jmlInput != null) {
                        if (i == 0 || !firstSet) {
                            firstOfBatch = nextRelevantSiblingMutationsLists;
                            firstSet = true;
                        }
                        indexesToInput.put(indexes, jmlInput);
                        if (fullBatch) {
                            --i;
                        }
                    } else {
                        duplicateMethods.add(indexes);
                        --i;
                    }
                }
                if (shouldEnd) {
                    log.warn("MJC: No more children for father index " + fatherIndex);
                    if (shouldDelete && !father.getOriginalFilename().equals(father.getFilename())) {
//                        new File(father.getFilename()).delete();
                    }
                    if (shouldDelete && father.getChildrenFilename() != null) {
//                        new File(father.getChildrenFilename()).delete();
                    }
                    if (shouldDelete && father.getJml4cFilename() != null) {
                        String wrapperDirPath = father.getJml4cFilename().substring(0, father.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1);
                        File wrapperFile = new File(wrapperDirPath); //Limpio el wrapper
                        if (wrapperFile.exists()) {
//                            for(File file: wrapperFile.listFiles()) {
//                                file.delete();
//                            }
//                            wrapperFile.delete();
                        }
                    }
                    break;
                } else if (jmlInputs.isEmpty()) {
                    log.warn("MJC: Found batch full of duplicates");
                }
            }

            mutantsInformationHolder.setCompilationUnit(backup);

            if (jmlInputs.isEmpty()) {
                //                System.out.println("Vacio el jmlInputs");
                return null;
            }

            log.warn("MJC: Batch generated. Total: " + jmlInputs.size());
            //            System.out.println("Y en indexesToInput hay: " + indexesToInput.size());

            Set<String> uncompilableMethods = Sets.newHashSet();
            Set<String> uncompilableMethodIndexes = Sets.newHashSet();

            while (true) {
                wrapper = createJMLInputWrapper(jmlInputs, classToMutate);

                String filename = wrapper.getFilename();
                String tempFilename = filename.substring(0, filename.lastIndexOf(FILE_SEP)+1) + 
                        MUTANTS_DEST_PACKAGE.replaceAll("\\.", FILE_SEP) + FILE_SEP;
                String packageToWrite = filename.substring(batchPackageStart(filename),
                        filename.lastIndexOf(FILE_SEP)+1).replaceAll(FILE_SEP, ".")+MUTANTS_DEST_PACKAGE;
                tempFilename = adaptSiblingsFileToJML4C(filename, tempFilename, packageToWrite);

                if (tempFilename == null) {
                    log.error("MJC: Didn't adapt for JML4C!");
                }

                wrapper.setJml4cFilename(tempFilename);
                wrapper.setJml4cPackage(packageToWrite);
                wrapper.setNextRelevantSiblingsMutationsLists(firstOfBatch);
                //////////////////////////////////////////////////////////////////////////////////
                String fileClasspath = tempFilename.substring(
                        0, tempFilename.lastIndexOf(packageToWrite.replaceAll("\\.", FILE_SEP)));

                //                String outputPath = wrapper.getFilename().substring(0, wrapper.getFilename().lastIndexOf(FILE_SEP) + 1);

                String[] systemClassPathsToFilter = Java7ClassLibrary.withoutJdkEntries(System.getProperty("java.class.path")).split(PATH_SEP);

                String filteredSystemClasspath = "";

                for (int k = 0 ; k < systemClassPathsToFilter.length ; ++k) {
                    if (systemClassPathsToFilter[k].contains("org.eclipse.jdt.core") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.text") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.equinox.common") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.equinox.preferences") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.osgi") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.core.contenttype") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.core.jobs") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.core.resources") ||
                            systemClassPathsToFilter[k].contains("org.eclipse.core.runtime") || 
                            systemClassPathsToFilter[k].contains("recoder") ||
                            systemClassPathsToFilter[k].contains("mujava") ||
                            systemClassPathsToFilter[k].contains("javassist") ||
                            systemClassPathsToFilter[k].contains("commons") ||
                            systemClassPathsToFilter[k].contains("antlr") ||
                            systemClassPathsToFilter[k].contains("guava") ||
                            systemClassPathsToFilter[k].contains("jml-release") ||
                            systemClassPathsToFilter[k].contains("antlr") ||
                            systemClassPathsToFilter[k].contains("antlr") ||
                            systemClassPathsToFilter[k].contains("javassist") ||
                            systemClassPathsToFilter[k].contains("reflections")) {
                        continue;
                    }
                    filteredSystemClasspath += systemClassPathsToFilter[k] + PATH_SEP;
                }

                String currentClasspath = System.getProperty("user.dir")+FILE_SEP+"lib/stryker/jml4c.jar"+
                        PATH_SEP+fileClasspath+
                        PATH_SEP+filteredSystemClasspath;

                // One argument per element: Runtime.exec(String) splits the command on
                // whitespace, which broke every batch when a path contained a space.
                List<String> jml4cCommand = Lists.newArrayList(System.getProperty("java.home") + "/bin/java", "-Xmx2048m");
                if ("1.7".equals(System.getProperty("java.specification.version"))) {
                    jml4cCommand.add("-XX:MaxPermSize=512m");
                }
                jml4cCommand.addAll(Arrays.asList("-jar", System.getProperty("user.dir") + FILE_SEP + "lib/stryker/jml4c.jar",
                        "-nowarn", "-maxProblems", "9999999"));
                // JML4C cannot read Java 8+ class files either: give it the Java 7 class library.
                jml4cCommand.addAll(Arrays.asList("-bootclasspath", Java7ClassLibrary.path()));
                jml4cCommand.addAll(Arrays.asList("-cp", currentClasspath, tempFilename));
                ProcessBuilder jml4c = new ProcessBuilder(jml4cCommand);
                nanoPrev = System.currentTimeMillis();
                Process p = jml4c.start();
                String errors = CharStreams.toString(new InputStreamReader(p.getErrorStream()));
                p.waitFor();
                StrykerStage.compilationMillis += System.currentTimeMillis() - nanoPrev;
                int exitValue = p.exitValue();

                if (exitValue == 0) {
                    log.warn("MJC: Batch compiled and the amount of non-compilable mutants was: " + uncompilableMethods.size());
                    log.debug(errors);
                    if (uncompilableMethods.size() > 0) {
                        StrykerStage.nonCompilableMutations += uncompilableMethods.size();
                    }
                    wrapper.setUncompilableMethods(uncompilableMethodIndexes);
                    wrapper.setIndexesToMethod(indexesToInput);
                    wrapper.setDuplicateMethods(duplicateMethods);
                    break;
                } else {
                    Map<String, Pair<Integer, Integer>> methodsLineNumbers = 
                            StrykerJavaFileInstrumenter.parseMethodsLineNumbers(tempFilename, methodToCheck);
                    log.warn("MJC: Didn't compile, identifying non-compilable methods to remove. " + tempFilename);
//                    File wrapperFile = new File(wrapper.getJml4cFilename().substring(0, wrapper.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1)); //Limpio el wrapper
//                    for(File file: wrapperFile.listFiles()) {
//                        file.delete();
//                    }
//                    wrapperFile.delete();
//                    new File(wrapper.getFilename()).delete(); //Limpio el wrapper
                    Map<String, List<Pair<Integer, Boolean>>> curUncompilableMethods = Maps.newTreeMap();
                    String errorClusters[] = errors.split("----------\n");
                    for (String errorCluster : errorClusters) {
                        //Si es error de unreachable code no puedo asumir nada
                        String errorLines[] = errorCluster.split("\n");
                        //Buscar en el mapa de lineas qué métodos son y agregarlos a la lista

                        for (String string : errorLines) {
                            if (string.contains(classToMutate) && string.contains("at line") && string.contains("ERROR")) {
                                String errorLine = string.substring(string.indexOf("(at line"));
                                int errorLineNumber = Integer.valueOf(errorLine.substring(9, errorLine.length() - 1)); //salteo el "(at line " y el  ")" del final
                                for (Entry<String, Pair<Integer, Integer>> entry : methodsLineNumbers.entrySet()) {
                                    Pair<Integer, Integer> lineNumbers = entry.getValue();
                                    if (errorLineNumber >= lineNumbers.getLeft() && errorLineNumber <= lineNumbers.getRight()) {
                                        errorLineNumber = errorLineNumber - lineNumbers.getLeft(); //adapto a las lineas iniciales
                                        if (curUncompilableMethods.containsKey(entry.getKey())) {
                                            curUncompilableMethods.get(entry.getKey()).add(new ImmutablePair<Integer, Boolean>(errorLineNumber, !errorCluster.contains("Unreachable code")));
                                        } else {
                                            List<Pair<Integer, Boolean>> theList = new LinkedList<Pair<Integer, Boolean>>();
                                            theList.add(new ImmutablePair<Integer, Boolean>(errorLineNumber, !errorCluster.contains("Unreachable code")));
                                            curUncompilableMethods.put(entry.getKey(), theList);
                                        }
                                    }
                                }
                            }
                        }
                    }                    

                    List<OpenJMLInput> toRemoveJMLInputs = Lists.newArrayList();
                    List<String> toRemoveIndexes = Lists.newArrayList();

                    for (Entry<String, OpenJMLInput> entry : indexesToInput.entrySet()) {
                        if (curUncompilableMethods.containsKey(entry.getValue().getRacMethod())) {
                            for (Pair<Integer, Boolean> errorLineNumber : curUncompilableMethods.get(entry.getValue().getRacMethod())) {
                                Integer errorLineIndex = father.getMuJavaFeedback().getCurMutableLines().indexOf(errorLineNumber.getLeft());
                                if (errorLineIndex < 0 || !errorLineNumber.getRight()) {
                                    continue;
                                }
                                int indexToSkip = entry.getValue().getFeedback().getLineMutationIndexes()[entry.getValue().getFeedback().getLineMutationIndexes().length - errorLineIndex - 1];
                                father.getMuJavaFeedback().getNonCompilableIndexes().get(entry.getValue().getFeedback().getLineMutationIndexes().length - errorLineIndex - 1).add(indexToSkip);
                            }
                            toRemoveIndexes.add(entry.getKey());
                            toRemoveJMLInputs.add(entry.getValue());
                        }
                    }

                    for (Set<Integer> theSet : father.getMuJavaFeedback().getNonCompilableIndexes().values()) {
                        StrykerStage.nonCompilableMutationIndexesFound += theSet.size();
                    }

                    jmlInputs.removeAll(toRemoveJMLInputs);

                    for (String index : toRemoveIndexes) {
                        indexesToInput.remove(index);
                        uncompilableMethodIndexes.add(index);
                    }

                    if (jmlInputs.isEmpty()) {
                        log.warn("MJC: Found Batch full of non-compilable methods");
                        log.warn("MJC: Compilation errors: \n" + errors);
                        if (uncompilableMethods.size() > 0) {
                            StrykerStage.nonCompilableMutations += uncompilableMethods.size();
                        }
                        if (!shouldEnd) {
                            return buildNextBatchSiblingsFile(father, fatherIndex, batchSize, lineMutationIndexes, fullBatch, shouldDelete);
                        } else {
                            return null;
                        }
                    }
                    uncompilableMethods.addAll(curUncompilableMethods.keySet());
                }
            }
            //            log.info("Creating output for OpenJMLController");
            //            OpenJMLController.getInstance().enqueueTask(wrapper);
            //            log.debug("Adding task to the OpenJMLController");

            jmlInputs.clear();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //        } catch (InstantiationException e) {
            //            e.printStackTrace();
            //        } catch (IllegalAccessException e) {
            //            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //        } catch (InvocationTargetException e) {
            //            e.printStackTrace();
            //        } catch (NoSuchMethodException e) {
            //            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OpenJavaException e) {
            e.printStackTrace();
        } catch (ParseTreeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    /**
     * Sends the next child of a father to the RAC stage (queue 2), applying the feedback
     * carried by {@code input}. At most one child is queued per call.
     *
     * <p>{@code input} is the previous sibling (or the placeholder created by
     * {@link #fatherize}). Its feedback provides the father index, the current index array, the
     * advance position (from {@code getSkipUntilMutID()} through {@link #getFeedbackIndex}, or 0
     * when it is {@code null}) and the {@code isMutateRight()}/{@code isUNSAT()} flags. The next
     * index array is computed with {@link #calculateNextRelevantSonMutationsLists}, and then:
     * <ul>
     * <li>if the father's space is exhausted, the method returns;</li>
     * <li>if the array is a duplicate in the father's batch, the next one is computed;</li>
     * <li>if it is not in the father's current batch, a new batch starting at it is built with
     *     {@link #buildNextBatchSiblingsFile} and stored in the father, and the method continues
     *     with the first mutant of that batch (or returns if no batch can be built);</li>
     * <li>if it did not compile, the next one is computed. The code that would send it to
     *     queue 1 as a fatherable input only runs if the batch map still has it, but
     *     {@link #buildNextBatchSiblingsFile} removes non-compiling mutants from that map;</li>
     * <li>otherwise the batch's {@link OpenJMLInput} gets a new {@link MuJavaFeedback} (index
     *     array, mutated lines, father index) and is enqueued to queue 2, incrementing
     *     {@link StrykerStage#mutationsQueuedToOJMLC}.</li>
     * </ul>
     * The advance position and the {@code isMutateRight()}/{@code isUNSAT()} jumps are applied
     * only to the first computation, from {@code input}'s candidate; each "next one is computed"
     * step after a skipped sibling is a plain increment. (Until 2026 every step re-applied the
     * same jump, skipping siblings that nothing had ruled out.) The list of skipped index arrays
     * built here is only used by file deletions that are commented out.
     *
     * @param input the previous sibling, whose {@link MuJavaFeedback} must be set
     */
    public void queueNextRelevantSibling(MuJavaInput input) {
        MuJavaInput father = fathers.get(input.getMuJavaFeedback().getFatherIndex());
        Mutation[][] mutatorsList = father.getMuJavaFeedback().getLineMutatorsList();

        Integer[] lineMutationIndexes = input.getMuJavaFeedback().getLineMutationIndexes();

        classToMutate = obtainClassNameFromFileName(father.getFilename());

        // The feedback of the failed candidate justifies one jump, from that candidate. If the
        // sibling it leads to is skipped below (duplicate or non-compiling), continue from it
        // with a plain increment: re-applying the jump would also skip siblings that nothing
        // has ruled out.
        boolean applyFeedback = true;
        //Encolo el hijo
        while (true) {
            log.debug("Generating mutants...");

            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData = father.getMutatorsData();
            mutatorsList = mutatorsData.getLeft();
            if (mutatorsList.length == 0) {
                return; //No tiene mas mutaciones posibles, es una hoja del arbol de mutaciones.
            }

            int feedbackIndex = (!applyFeedback || input.getMuJavaFeedback() == null || input.getMuJavaFeedback().getSkipUntilMutID() == null) ? 0 : getFeedbackIndex(
                    input.getMuJavaFeedback().getSkipUntilMutID(), mutatorsData.getRight().getLeft(), input.getMuJavaFeedback().getMutableLines());

            ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingMutationsLists = 
                    calculateNextRelevantSonMutationsLists(lineMutationIndexes.clone(), mutatorsList, 
                            feedbackIndex, mutatorsData.getRight().getRight(),
                            applyFeedback ? input.getMuJavaFeedback().isMutateRight() : true,
                            applyFeedback && input.getMuJavaFeedback().isUNSAT(), father.getMuJavaFeedback().getNonCompilableIndexes());
            applyFeedback = false;

            //tengo que borrar los que se saltearon. Los computo primero:
            List<Integer[]> skipped = Lists.newArrayList();

            Integer[] curIndexes = lineMutationIndexes.clone();
            Integer[] lastIndexes = null;

            if (nextRelevantSiblingMutationsLists != null) {
                lastIndexes = nextRelevantSiblingMutationsLists.getRight();
            } else {
                lastIndexes = new Integer[curIndexes.length];

                for (int i = 0; i < lastIndexes.length; i++) {
                    lastIndexes[i] = mutatorsList[lineMutationIndexes.length - i - 1].length;
                }
            }

            int col = 0;
            boolean shouldEnd = false;
            while (!shouldEnd) {
                while (curIndexes[col] + 1 <= mutatorsList[curIndexes.length - col - 1].length) { 
                    curIndexes[col]++;
                    boolean sameIndexes = true;
                    for (int i = 0; i < curIndexes.length; ++i) {
                        if (curIndexes[i] != lastIndexes[i]) {
                            sameIndexes = false;
                        }
                    }
                    if (sameIndexes) {
                        shouldEnd = true;
                        break;
                    }
                    skipped.add(curIndexes.clone());
                }

                while (!shouldEnd && curIndexes[col] + 1 > mutatorsList[curIndexes.length - col - 1].length) { //si me paso de rosca de la linea
                    if (++col >= curIndexes.length) {
                        shouldEnd = true;
                    }
                }

                if (shouldEnd) {
                    break;
                }
                for (int i = col - 1; i >= 0; --i) {
                    curIndexes[i] = 0;
                }

                skipped.add(curIndexes.clone());
            }

            //TODO Si saltea, pedirle al indexestomethod todos los JMLInput y borrar el archivo
            Map<String, OpenJMLInput> toSearchIndexesToMethod = father.getIndexesToMethod();
            for (Integer[] indexList: skipped) {
                String indexes = "[ ";
                for (Integer index : indexList) {
                    indexes += index + " ";
                }
                indexes += "]";
                if (toSearchIndexesToMethod.containsKey(indexes)) {
                    try {
//                        new File(toSearchIndexesToMethod.get(indexes).getFilename()).delete();
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            if (nextRelevantSiblingMutationsLists == null) {
                log.warn("MJC: No more children for father index " + input.getMuJavaFeedback().getFatherIndex());
                if (father.getJml4cFilename() != null) {
//                    String wrapperDirPath = father.getJml4cFilename().substring(0, father.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1);
//                    File wrapperFile = new File(wrapperDirPath); //Limpio el wrapper
//                    for(File file: wrapperFile.listFiles()) {
//                        file.delete();
//                    }
//                    wrapperFile.delete();
                }
                if (father.getChildrenFilename() != null) {
//                    new File(father.getChildrenFilename()).delete(); //El padre ya no sirve mas
                }
                if (!father.getOriginalFilename().equals(father.getFilename())) {
//                    new File(father.getFilename()).delete(); //El padre ya no sirve mas
                }
                return;
            } else if (nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length) {
                log.error("MJC: Error nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length");
            } else if (nextRelevantSiblingMutationsLists.getLeft().size() == 0) {
                log.error("MJC: Error nextRelevantSiblingMutationsLists.getLeft().size() == 0");
            }

            lineMutationIndexes = nextRelevantSiblingMutationsLists.getRight();
            String indexes = "[ ";
            for (Integer index : lineMutationIndexes) {
                indexes += index + " ";
            }
            indexes += "]";

            log.warn("MJC: About to generate case Father index " + input.getMuJavaFeedback().getFatherIndex() + " - " + indexes);
            String identifiers = "[ ";
            for (Mutation identifier : nextRelevantSiblingMutationsLists.getLeft()) {
                identifiers += " " + identifier.toString();
            }
            identifiers += " ]";
            log.warn("MJC: And it's mutant identifiers are: " + identifiers);

            Set<String> presentIndexes = father.getPresentIndexes();
            if (father.getDuplicateMethodIndexes().contains(indexes)) {
                continue;
            }
            if (!presentIndexes.contains(indexes) && !father.getUncompilableChildrenMethodNames().contains(indexes)) {
                //actualizar batch
//                new File(father.getChildrenFilename()).delete();
//                File wrapperFile = new File(father.getJml4cFilename().substring(0, father.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1)); //Limpio el wrapper
//                for(File file: wrapperFile.listFiles()) {
//                    file.delete();
//                }
//                wrapperFile.delete();

                OpenJMLInputWrapper wrapper = buildNextBatchSiblingsFile(father, input.getMuJavaFeedback().getFatherIndex(), batchSize, getPreviousIndexes(lineMutationIndexes, mutatorsList), false, true);

                if (wrapper == null) {
                    log.warn("MJC: A father with no batches left");
                    if (!father.getOriginalFilename().equals(father.getFilename())) {
//                        new File(father.getFilename()).delete(); //El padre ya no sirve mas
                    }

                    return;
                }

                father.setIndexesToMethod(wrapper.getIndexesToMethod());
                father.setUncompilableChildrenMethodNames(wrapper.getUncompilableMethods());
                father.setChildrenFilename(wrapper.getFilename());
                father.setJml4cFilename(wrapper.getJml4cFilename());
                father.setJml4cPackage(wrapper.getJml4cPackage());
                father.setPresentIndexes(Sets.newHashSet(wrapper.getIndexesToMethod().keySet()));
                father.setDuplicateMethodIndexes(wrapper.getDuplicateMethodIndexes());

                nextRelevantSiblingMutationsLists = wrapper.getNextRelevantSiblingsMutationsLists();
                lineMutationIndexes = nextRelevantSiblingMutationsLists.getRight();
                indexes = "[ ";
                for (Integer index : lineMutationIndexes) {
                    indexes += index + " ";
                }
                indexes += "]";
            }
            Map<String, OpenJMLInput> indexesToMethod = father.getIndexesToMethod();
            if (father.getUncompilableChildrenMethodNames().contains(indexes)) {
                log.warn("MJC: Omitted mutation for non-compiling");
                OpenJMLInput jmlInput = indexesToMethod.get(indexes);
                if (jmlInput == null) {
                    continue;
                }
                //TODO revisar estos argumentos
                MuJavaInput mujavainput = new MuJavaInput(jmlInput.getFilename(), input.getMethod(), 
                        input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), 
                        input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                        input.getFullyQualifiedFileName(), input.getMethodUnderAnalysis());
                MuJavaFeedback newFeedback = new MuJavaFeedback(
                        StrykerJavaFileInstrumenter.parseMethodStartLine(mujavainput.getFilename(), 
                                mujavainput.getMethod()), lineMutationIndexes, father.getMuJavaFeedback().getLineMutatorsList(), 
                                jmlInput.getFeedback().getLastMutatedLines(), father.getMuJavaFeedback().getMutableLines(), 
                                father.getMuJavaFeedback().getCurMutableLines());

                newFeedback.setMut(father.getMuJavaFeedback().getMut());
                newFeedback.setMutantsInformationHolder(father.getMuJavaFeedback().getMutantsInformationHolder());
                newFeedback.setFatherIndex(input.getMuJavaFeedback().getFatherIndex());

                newFeedback.setFatherable(true);
                newFeedback.setGetSibling(false);
                newFeedback.setSkipUntilMutID(null);
                mujavainput.setMuJavaFeedback(newFeedback);

                StrykerStage.weedQueue.enqueue1(mujavainput);
                continue;
            } else {
                List<Integer> mutatedLines = Lists.newArrayList();

                for (Mutation identifier : nextRelevantSiblingMutationsLists.getLeft()) {
                    Integer affectedLine = identifier.getAffectedLine();
                    mutatedLines.add(affectedLine);
                }

                OpenJMLInput output = indexesToMethod.get(indexes);

                if (output != null) {
                    MuJavaFeedback newFeedback = new MuJavaFeedback(
                            StrykerJavaFileInstrumenter.parseMethodStartLine(
                                    output.getFilename(), output.getMethod()),
                                    lineMutationIndexes, father.getMuJavaFeedback().getLineMutatorsList(), 
                                    mutatedLines, father.getMuJavaFeedback().getMutableLines(), father.getMuJavaFeedback().getCurMutableLines());
                    newFeedback.setMut(father.getMuJavaFeedback().getMut());
                    newFeedback.setMutantsInformationHolder(father.getMuJavaFeedback().getMutantsInformationHolder());
                    newFeedback.setFatherIndex(input.getMuJavaFeedback().getFatherIndex());

                    output.setFeedback(newFeedback);
                    log.debug("Adding task to the OpenJMLController");
                    StrykerStage.weedQueue.enqueue2(output);
                    StrykerStage.mutationsQueuedToOJMLC++;
                    break;
                }
            }
        }
    }

    /**
     * Merges a batch of mutants into a single source file, so that they are compiled together
     * and run by the RAC stage from one class.
     *
     * <p>The file is {@code <dir>/a<d>/aOpenJMLInWrap<i>/<classToMutate>.java}, where
     * {@code <dir>} is a new temporary directory (see {@link #createWorkingDirectory()}),
     * {@code <d>} its name without '-' and {@code <i>} the counter {@link #privateI}. It is a copy
     * of the first mutant's file, whose method keeps its name, with the method of every other
     * mutant inserted after the class declaration and renamed to {@code <method><k>}
     * ({@code k = 1, 2, ...}). Renaming replaces every occurrence of the method name in the
     * method's text, including calls and longer identifiers that contain it. Each input's
     * {@code racMethod} is set to the name of its method in the wrapper.
     *
     * <p>Side effect: {@code jmlInputs} is refilled with the inputs actually included, in method
     * name (string) order. Inputs whose method cannot be extracted are silently left out.
     *
     * @param jmlInputs the batch; modified as described above
     * @param classToMutate simple class name, used as the file name
     * @return the wrapper, whose map goes from wrapper method name to input
     * @throws IllegalArgumentException if {@code jmlInputs} is empty
     * @throws IllegalStateException if the file cannot be created or an I/O error occurs
     */
    protected OpenJMLInputWrapper createJMLInputWrapper(
            List<OpenJMLInput> jmlInputs, String classToMutate) {
        log.debug("jmlInputs: " + jmlInputs.toString());
        if( jmlInputs.isEmpty() ){
            throw new IllegalArgumentException("You must provide at least one OpenJMLInput.");
        }
        OpenJMLInput oji = jmlInputs.remove(0);

        String originalMethod = oji.getMethod();
        File newDir = createWorkingDirectory();
        String dirString = newDir.getAbsolutePath();
        String newPath = "a"+dirString.substring(dirString.lastIndexOf(FILE_SEP)+1).replaceAll("-", "")+(FILE_SEP+"aOpenJMLInWrap" + privateI++);
        File newDir2 = new File(newDir, newPath);

        Map<String,OpenJMLInput> map = Maps.newTreeMap();
        int index = 0;
        try {
            newDir2.mkdirs();
            File newFile = new File(newDir2, classToMutate + ".java");
            if(!newFile.createNewFile()){
                throw new IllegalStateException("Couldn't create the file");
            }
            File from = new File(oji.getFilename());
            String methodName = oji.getMethod();
            index++;
            oji.setRacMethod(methodName);
            map.put(methodName, oji);
            Files.copy(from, newFile);
            List<Pair<String, String>> methodCodePairs = Lists.newArrayList();

            for (OpenJMLInput input: jmlInputs) {
                try {
                    methodName = input.getMethod() + (index++);
                    String codeToAdd = getMethod(input.getFilename(), input.getMethod());
                    //                    if (codeToAdd.contains("invariant")) {
                    //                        codeToAdd = codeToAdd.replaceAll("\\/\\*@.*\\n.*invariant.*\\n.*@\\*\\/", "");
                    //                    }
                    methodCodePairs.add(new ImmutablePair<String, String>(methodName, codeToAdd));
                    input.setRacMethod(methodName);
                    map.put(methodName, input);
                } catch (NoSuchElementException e) {

                }
            }
            insertNewMethod(originalMethod, methodCodePairs, newFile.getAbsolutePath());

            OpenJMLInputWrapper ojiw = new OpenJMLInputWrapper(newFile.getPath(), oji.getConfigurationFile(), 
                    oji.getOverridingProperties(), originalMethod, map, oji.getOriginalFilename());
            jmlInputs.clear();
            jmlInputs.addAll(map.values());
            return ojiw;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Extracts the source text of a method with a line-based brace-counting heuristic. The
     * method starts at the first line containing {@code " " + methodName} and ends when the
     * braces are balanced again. The class-level lines collected since the last line with a
     * brace before it (typically its JML specification) are included.
     *
     * @throws NoSuchElementException if nothing was collected
     */
    private String getMethod(String filename, String methodName) throws FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        Scanner scan = new Scanner(new File(filename));
        scan.useDelimiter("\n");
        boolean methodFound = false;
        boolean canBreak = true;
        int balance = -1;
        //		int j = 0;
        while(scan.hasNext()){
            String str = scan.next();
            if(str.contains(" " + methodName)) {
                methodFound = true;
                if(!str.contains("{")) {
                    canBreak = false;
                }
            }
            if(str.contains("{")) {
                //				System.out.println("1: "+((" "+str+" ").split("\\{").length - 1));
                balance += ((" "+str+" ").split("\\{").length - 1);
                if(!methodFound) {
                    builder = new StringBuilder();
                }
                canBreak = true;
            }
            if(str.contains("}")) {
                //				System.out.println("1: "+((" "+str+" ").split("\\}").length - 1));
                balance -= ((" "+str+" ").split("\\}").length - 1);
                if(!methodFound) {
                    builder = new StringBuilder();
                }
                canBreak = true;
            }
            if((balance == 0 && !str.contains("}")) || methodFound) {
                builder.append(str+"\n");
                if(balance == 0 && methodFound && canBreak) {
                    break;
                }
            }
        }
        scan.close();
        if(builder.length() == 0) {
            throw new NoSuchElementException("The method name was not found in this file");
        }
        return builder.toString();
    }

    /**
     * Rewrites {@code filename} with the given methods inserted after the first line containing
     * " class " (and after the following line too, if the class line has no '{'). In each
     * method's code, occurrences of {@code originalMethodName} are replaced with its new name.
     * The file is rebuilt through {@code <filename>_temp}.
     *
     * @param methodCodePairs pairs of (new method name, method source text)
     */
    private void insertNewMethod(String originalMethodName, List<Pair<String, String>> methodCodePairs, String filename) throws IOException {
        String tempFileName = filename + "_temp";
        File destFile = new File(tempFileName);
        destFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(destFile);
        Scanner scan = new Scanner(new File(filename));
        scan.useDelimiter("\n");
        boolean classFound = false;
        while(scan.hasNext()){
            String str = scan.next();
            if(!classFound && str.contains(" class ")) {
                classFound = true;
                fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                if(!str.contains("{")) {
                    fos.write((scan.next() + "\n").getBytes(Charset.forName("UTF-8")));
                }
            } else if (classFound){
                for (Pair<String, String> pair : methodCodePairs) {
                    fos.write(pair.getValue().replace(originalMethodName, pair.getKey())
                            .getBytes(Charset.forName("UTF-8")));
                }
                fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                break;
            } else {
                fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
            }
        }

        while(scan.hasNext()){
            fos.write((scan.next() + "\n").getBytes(Charset.forName("UTF-8")));
        }
        fos.close();
        scan.close();
        File originalFile = new File(filename);
        originalFile.delete();

        File newFile = new File(filename);
        newFile.createNewFile();

        Files.move(destFile, newFile);
    }

    /**
     * Used only when {@link #EXTRA_DUPLICATES_CHECK} is on (it is off). When two hashes are
     * equal this method is called to make sure it is not a false duplicate (equal hash but
     * different file). Returns {@code true} if the files differ (a false duplicate), and
     * {@code false} if they are equal or cannot be read.
     */
    private boolean isFalseDuplicate(String originalFileName, File tempFile)
            throws IllegalStateException {
        File original = new File(originalFileName);
        try {
            if (!Files.equal(original, tempFile)) {
                log.error("FALSE DUPICATE");
                log.error("Original:" + original.getAbsolutePath());
                log.error("False duplicate:" + tempFile.getAbsolutePath());
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
            //			throw new IllegalStateException(
            //					"An error occured while opening files " + originalFileName
            //							+ " and " + tempFile.getName());
        }

    }

    /**
     * Creates a new, empty temporary directory under {@code java.io.tmpdir} with Guava's
     * {@code Files.createTempDir()}, whose name has the form
     * {@code <currentTimeMillis>-<counter>}. Used for father copies, MuJava output and batch
     * wrappers, also by {@link UnskippableMuJavaController}. The directory is not deleted.
     *
     * @throws IllegalStateException if the directory cannot be created
     */
    protected static File createWorkingDirectory() throws IllegalStateException {
        File tmpDir = null;
        if ((tmpDir = Files.createTempDir()) == null) {
            throw new IllegalStateException(
                    "Couldn't create work environment: failed"
                            + "to create temp directory");
        }

        log.trace("Using directory " + tmpDir + " as working directory.");
        return tmpDir;
    }

    /** Accepts file names starting with {@code gen<genNum>_}. Not used anywhere. */
    public class GenFileFilter implements FilenameFilter {

        private int genNum;

        public GenFileFilter(int genNum) {
            this.genNum = genNum;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith("gen" + genNum + "_");
        }
    }

    /**
     * byte[] wrapper so as to be able to add digest to collections.
     * Equality and hash code are based on the array contents. Used as the key of
     * {@link MuJavaController#filesHash} and by other classes that deduplicate files by digest.
     */
    public static class MsgDigest {

        private byte[] digest;

        public MsgDigest(byte[] digest) {
            this.digest = digest;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(digest);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MsgDigest other = (MsgDigest) obj;
            if (!Arrays.equals(digest, other.digest))
                return false;
            return true;
        }
    }

    public List<MuJavaInput> getFathers() {
        return fathers;
    }
}
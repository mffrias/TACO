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

public class MuJavaController extends AbstractBaseController<MuJavaInput> {

    //	private static AtomicInteger compilationFailCount = new AtomicInteger(0);

    public static boolean feedbackOn = true;

    public static boolean fatherizationPruningOn = true;

    public static boolean finishOnFirstFix = true;

    private static final String FILE_SEP = System.getProperty("file.separator");

    // If it is set to false then it will be assumed that if two hashes are
    // equal then that means that the two files are equal. Which of course
    // it is not necessarily true.
    private static final boolean EXTRA_DUPLICATES_CHECK = false;

    private static final int NOT_PRESENT = -1;

    private static final int batchSize = 100;

    private static MuJavaController instance;

    private static Logger log = Logger.getLogger(MuJavaController.class);

    private int privateI = 0;

    //    private int ownI = 0;

    private int maxMethodsInFile = 1;

    //    private Map<String, Integer> filenameToMutatedLine = Maps.newConcurrentMap();
    private Map<MsgDigest, String> filesHash = Maps.newHashMap();

    protected List<OpenJMLInput> jmlInputs = new ArrayList<OpenJMLInput>(maxMethodsInFile);

    private String classToMutate;

    private List<MuJavaInput> fathers = Lists.newArrayList();

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

    @Override
    protected Runnable getRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    MuJavaInput input = queue.take();

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

                        input = queue.take();
                    }
                } catch (InterruptedException e1) {
                    //e1.printStackTrace();
                }
            }
        };
    }

    @Override
    protected int getQtyOfThreads() {
        return 1;
    }

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

    private int getFeedbackIndex(Integer mutID, List<Integer> curLineNumbersList, List<Integer> mutableLines) {
        int lineNumber = mutableLines.get(mutID);

        return curLineNumbersList.indexOf(lineNumber);
    }

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

    protected static boolean isSkippeableLeftMutation(Mutation identifier) {
        if (!identifier.getMutOp().equals(MutationOperator.PRVOL) && !identifier.getMutOp().equals(MutationOperator.PRVOL_SMART)) {
            return true;
        } else {
            return identifier.isMutantATailChangeOfTheLeftSideOfAnAssignmentExpression();
        }
    }

    //<[][], <[i1, i2, i3], [<j1,k1>, <j2,k2>...]>>

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

    public static final String MUTANTS_DEST_PACKAGE = "ar.edu.itba.stryker.mutants";

    //    private static final String CLASSPATH = System.getProperty("java.class.path");


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
                    	str = str.replace(oldFullyQualifiedClassName, packageToWrite + "." + classUnderAnalysis);
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
                String packageToWrite = filename.substring(filename.indexOf(FILE_SEP+"a")+1, 
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

                String[] systemClassPathsToFilter = System.getProperty("java.class.path").split(PATH_SEP);

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

                String command = System.getProperty("java.home") + "/bin/java -Xmx2048m -XX:MaxPermSize=512m -jar " + 
                        System.getProperty("user.dir") + FILE_SEP + "lib/stryker/jml4c.jar "
                		+ "-nowarn " + "-maxProblems " + "9999999 " + "-cp " + currentClasspath + " " + tempFilename;
                nanoPrev = System.currentTimeMillis();
                Process p = Runtime.getRuntime().exec(command);
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

    public void queueNextRelevantSibling(MuJavaInput input) {
        MuJavaInput father = fathers.get(input.getMuJavaFeedback().getFatherIndex());
        Mutation[][] mutatorsList = father.getMuJavaFeedback().getLineMutatorsList();

        Integer[] lineMutationIndexes = input.getMuJavaFeedback().getLineMutationIndexes();

        classToMutate = obtainClassNameFromFileName(father.getFilename());

        //Encolo el hijo
        while (true) {
            log.debug("Generating mutants...");

            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData = father.getMutatorsData();
            mutatorsList = mutatorsData.getLeft();
            if (mutatorsList.length == 0) {
                return; //No tiene mas mutaciones posibles, es una hoja del arbol de mutaciones.
            }

            int feedbackIndex = (input.getMuJavaFeedback() == null || input.getMuJavaFeedback().getSkipUntilMutID() == null) ? 0 : getFeedbackIndex(
                    input.getMuJavaFeedback().getSkipUntilMutID(), mutatorsData.getRight().getLeft(), input.getMuJavaFeedback().getMutableLines());

            ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingMutationsLists = 
                    calculateNextRelevantSonMutationsLists(lineMutationIndexes.clone(), mutatorsList, 
                            feedbackIndex, mutatorsData.getRight().getRight(), input.getMuJavaFeedback().isMutateRight(), 
                            input.getMuJavaFeedback().isUNSAT(), father.getMuJavaFeedback().getNonCompilableIndexes());

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

                MuJavaController.getInstance().enqueueTask(mujavainput);
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
                    OpenJMLController.getInstance().enqueueTask(output);
                    StrykerStage.mutationsQueuedToOJMLC++;
                    break;
                }
            }
        }
    }

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
     * Used in case the EXTRA_CHECK flag is on. In cases were two hashes are
     * equal this method is called so as to assure that it is not a false
     * duplicate (equal hash but different file). Returns true if files are
     * equal or false if otherwise.
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
     * Sets up the necessary work environment prior to generating the mutants.
     * 
     * @throws FatalStrykerStageException
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
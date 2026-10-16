package ar.edu.taco.stryker.api.impl.input;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mujava.api.MutantsInformationHolder;
import mujava.api.Mutation;
import mujava.app.Mutator;

import com.google.common.collect.Maps;

/**
 * Per-candidate search state of Stryker's exploration of the mutation tree.
 * <p>
 * A father is a program version kept in the {@code fathers} list of
 * {@link ar.edu.taco.stryker.api.impl.MuJavaController MuJavaController} (or of
 * {@link ar.edu.taco.stryker.api.impl.UnskippableMuJavaController UnskippableMuJavaController}). It has a
 * set of mutable lines, each with an array of candidate mutations ({@link #getLineMutatorsList()}). A
 * child (sibling) of that father is identified by {@link #getFatherIndex()} plus
 * {@link #getLineMutationIndexes()}, which selects at most one mutation per mutable line. Siblings are
 * enumerated like a mixed-radix counter over {@code lineMutationIndexes} (see
 * {@code MuJavaController.calculateNextRelevantSonMutationsLists}).
 * <p>
 * MuJavaController and UnskippableMuJavaController create a feedback object for every father and every
 * child. The object travels with the candidate inside an {@link OpenJMLInput} (queue 2) and a
 * {@link DarwinistInput} (queue 3), and comes back to MuJavaController inside a {@link MuJavaInput}
 * (queue 1). On the way, OpenJMLController and DarwinistController set the steering fields
 * ({@link #isFatherable()}, {@link #isGetSibling()}, {@link #isMutateRight()}, {@link #isUNSAT()} and
 * {@link #getSkipUntilMutID()}). These fields decide which sibling MuJavaController generates next and
 * whether this candidate becomes a father. The object is mutated in place, not copied, as it moves
 * between stages.
 */
public class MuJavaFeedback {

    // English summary of the Spanish notes below: per-line count of applied mutations (the input's
    // equivalence class); per-line index of the applied mutation; per-line matrix of mutators;
    // feedback line number.
    /*
     *  -Vector de cantidad de mutaciones aplicadas por línea. Es la clase de equivalencia de ese input.
     *  -Vector de índice de mutaciones aplicado por línea en este input
     *  -Matriz (vector de vectores) de mutadores por línea para ese input
     *  -Feedback Line Number
     */
    
    /**
     * Variablization feedback, or {@code null} when there is none. Otherwise it is a 0-based index into
     * {@link #getMutableLines()}: the line at which the variablization in
     * {@code DarwinistController.computateFeedback} stopped. That method stores
     * {@code lastVariablizedMutID - 1} because the mutIDs written by
     * {@link ar.edu.taco.stryker.api.impl.StrykerJavaFileInstrumenter#insertMutIDs} start at 1. When no
     * mutID was variablized, it stores the last index of {@code mutableLines}.
     * <p>
     * {@code MuJavaController.getFeedbackIndex} turns this value into a position in
     * {@link #getLineMutationIndexes()} (through the father's descending line list). The next sibling
     * is obtained by advancing that position and resetting every less significant position to 0, which
     * skips the remaining siblings that differ from the current one only in lines below it.
     * {@code null} means advancing from position 0. UnskippableMuJavaController ignores this field.
     */
    private Integer skipUntilMutID = null;
    /**
     * Which mutation is applied on each mutable line of the father. Position {@code i} refers to line
     * {@code lineMutatorsList[length - 1 - i]}. Position 0 is therefore the bottom-most mutable line and
     * the least significant digit when enumerating siblings. Value 0 leaves the line unmutated. A value
     * {@code k > 0} applies {@code lineMutatorsList[line][lineMutatorsList[line].length - k]}, counting
     * from the end of that line's array.
     */
    private Integer[] lineMutationIndexes;
    /**
     * Line numbers, as given by {@code Mutation.getAffectedLine()}, that this candidate mutates with
     * respect to its father. {@code null} in a father's own feedback, and empty for the placeholder
     * "base sibling" from which the enumeration of siblings starts.
     */
    private List<Integer> lastMutatedLines;
    /**
     * The father's candidate mutations, with one array per mutable line in ascending line order. Each
     * array lists the unskippable left-hand-side ({@code PRVOL}/{@code PRVOL_SMART}) mutations first,
     * then the skippable left-hand-side ones, then the remaining ("right") ones (see
     * {@code MuJavaController.getMutatorsList}). The array is shared with the father.
     */
    private Mutation[][] lineMutatorsList;
    /**
     * Index of this candidate's father in the {@code getFathers()} list of the controller that
     * generated it: MuJavaController, or UnskippableMuJavaController for the inputs on that controller's
     * own queue and for {@link #isStop() stop} candidates. OpenJMLController picks the list according to
     * {@link #isStop()}.
     */
    private int fatherIndex;
    /**
     * muJava state of the father (from {@code Mutator.obtainMutants}). It is reused to write any
     * combination of the father's mutations.
     */
    private MutantsInformationHolder mutantsInformationHolder;
    /**
     * muJava {@link Mutator} of the father, which {@code MuJavaController.buildNextBatchSiblingsFile}
     * reuses.
     */
    private Mutator mut;
    /**
     * Line numbers of the mutable lines of the first-generation father, in ascending order. Later
     * generations inherit this list unchanged. These are the lines, annotated {@code //mutGenLimit N}
     * with {@code N > 0} in the subject, for which muJava produced at least one mutation. The k-th
     * {@code //mutGenLimit} comment of the method is assumed to correspond to element {@code k - 1}
     * (mutID {@code k}; see {@link ar.edu.taco.stryker.api.impl.StrykerJavaFileInstrumenter#insertMutIDs}).
     */
    private List<Integer> mutableLines = null;
    /**
     * Line numbers, in ascending order, of the lines that are still mutable in this candidate's father.
     * In later generations these can be fewer than {@link #getMutableLines()}, because
     * {@code StrykerJavaFileInstrumenter.decrementUnmutatedLimits} lowers the {@code //mutGenLimit}
     * values. Used to map JML4C compilation errors back to positions of {@link #getLineMutationIndexes()}.
     */
    private List<Integer> curMutableLines = null;
    /**
     * Whether this candidate may become a father when it returns to MuJavaController. If not,
     * MuJavaController counts it as a pruned father. Defaults to {@code true}. DarwinistController may
     * clear it based on variablization results (when {@code MuJavaController.fatherizationPruningOn}),
     * and UnskippableMuJavaController clears it on some of the inputs it re-enqueues to itself.
     */
    private boolean fatherable;
    /**
     * Whether the last expression variablized for the feedback line was a right-hand side (from
     * {@code VariablizationData.isLastVariablizedMutIDRight()}). Together with {@link #isUNSAT()}, it
     * decides whether {@code MuJavaController.calculateNextRelevantSonMutationsLists} jumps over the
     * remaining "right" (or also left-hand-side) mutations of that line. The controllers set it to
     * {@code true} when there is no variablization feedback.
     */
    private boolean mutateRight;
    /**
     * When {@code true} (the default), MuJavaController generates and enqueues the next relevant sibling
     * of this candidate when the candidate returns to queue 1. MuJavaController clears it on the inputs
     * it re-enqueues itself for non-compiling children.
     */
    private boolean getSibling = true;
    /**
     * Set by {@code DarwinistController.computateFeedback}. {@code true} when no TACO check during
     * variablization returned SAT, or when variablization was not possible. Used together with
     * {@link #isMutateRight()} to choose the next sibling.
     */
    private boolean UNSAT = false;
    /**
     * Marks a validate-only candidate. UnskippableMuJavaController sends its unskippable mutants to
     * OpenJMLController with this flag, so that they are checked as possible fixes but never fed back.
     * OpenJMLController resolves the father in UnskippableMuJavaController's list and uses
     * {@link #getUnskippableOJML4CFilename()} and {@link #getUnskippableOJML4CPackage()}. Neither
     * OpenJMLController nor DarwinistController requests feedback for such a candidate or re-enqueues it
     * on queue 1.
     */
    private boolean stop = false;
    /**
     * Result of {@code StrykerJavaFileInstrumenter.parseMethodStartLine} for the candidate's file (the
     * method's first line minus one). Nothing reads it.
     */
    private int methodFirstLine;
    /**
     * Meaningful in a father's feedback. Maps each position of {@link #getLineMutationIndexes()} to the
     * index values of that line that made a child fail JML4C compilation.
     * {@code MuJavaController.buildNextBatchSiblingsFile} fills it, and
     * {@code calculateNextRelevantSonMutationsLists} skips those values. The constructor creates an
     * empty set for every position.
     */
    private Map<Integer, Set<Integer>> nonCompilableIndexes = Maps.newHashMap();
    /**
     * For {@link #isStop() stop} candidates: the JML4C batch file of the UnskippableMuJavaController
     * father. OpenJMLController uses it instead of the MuJavaController father's file.
     */
    private String unskippableOJML4CFilename;
    /** For {@link #isStop() stop} candidates: the package of {@link #getUnskippableOJML4CFilename()}. */
    private String unskippableOJML4CPackage;

    /**
     * Creates a feedback with {@code fatherable == true} and an empty non-compilable set for every
     * position. It prints {@code PROBLEMMMM} if {@code lineMutationIndexes} is longer than
     * {@code lineMutatorsList}, which indicates an inconsistent state.
     *
     * @param methodFirstLine see {@link #getMethodFirstLine()}
     * @param lineMutationIndexes see {@link #getLineMutationIndexes()}
     * @param lineMutatorsList the father's mutations (see {@link #getLineMutatorsList()})
     * @param lastMutatedLines see {@link #getLastMutatedLines()}
     * @param mutableLines see {@link #getMutableLines()}
     * @param curMutableLines see {@link #getCurMutableLines()}
     */
    public MuJavaFeedback(int methodFirstLine, Integer[] lineMutationIndexes, Mutation[][] lineMutatorsList, 
            List<Integer> lastMutatedLines, List<Integer> mutableLines, List<Integer> curMutableLines) {
        super();
        if (lineMutationIndexes.length > lineMutatorsList.length) {
            System.out.println("PROBLEMMMM");
        }
        this.methodFirstLine = methodFirstLine;
        this.lineMutationIndexes = lineMutationIndexes;
        this.lineMutatorsList = lineMutatorsList;
        this.fatherable = true;
        this.lastMutatedLines = lastMutatedLines;
        this.mutableLines = mutableLines;
        this.curMutableLines = curMutableLines;
        for (int i = 0 ; i < lineMutationIndexes.length; ++i) {
            nonCompilableIndexes.put(i, new HashSet<Integer>());
        }
    }
    
    public List<Integer> getCurMutableLines() {
        return curMutableLines;
    }
    
    public Map<Integer, Set<Integer>> getNonCompilableIndexes() {
        return nonCompilableIndexes;
    }
    
    public int getMethodFirstLine() {
        return methodFirstLine;
    }

    public List<Integer> getMutableLines() {
        return mutableLines;
    }
    
    public Integer getSkipUntilMutID() {
        return skipUntilMutID;
    }
    
    public void setSkipUntilMutID(Integer skipUntilMutID) {
        this.skipUntilMutID = skipUntilMutID;
    }
    
    public Integer[] getLineMutationIndexes() {
        return lineMutationIndexes;
    }
    
    public Mutation[][] getLineMutatorsList() {
        return lineMutatorsList;
    }
    
    public void setLineMutationIndexes(Integer[] lineMutationIndexes) {
        this.lineMutationIndexes = lineMutationIndexes;
    }
    
    public void setLineMutatorsList(Mutation[][] lineMutatorsList) {
        this.lineMutatorsList = lineMutatorsList;
    }
    
    public boolean isUNSAT() {
        return UNSAT;
    }
    
    public void setUNSAT(boolean uNSAT) {
        UNSAT = uNSAT;
    }
    
    public int getFatherIndex() {
        return fatherIndex;
    }
    
    public void setFatherIndex(int fatherIndex) {
        this.fatherIndex = fatherIndex;
    }
    
    public Mutator getMut() {
        return mut;
    }
    
    public void setMut(Mutator mut) {
        this.mut = mut;
    }
    
    public MutantsInformationHolder getMutantsInformationHolder() {
        return mutantsInformationHolder;
    }
    
    public void setMutantsInformationHolder(MutantsInformationHolder mutantsInformationHolder) {
        this.mutantsInformationHolder = mutantsInformationHolder;
    }

    public boolean isFatherable() {
        return fatherable;
    }
    
    public void setFatherable(boolean fatherable) {
        this.fatherable = fatherable;
    }
    
    public List<Integer> getLastMutatedLines() {
        return lastMutatedLines;
    }
    
    public void setLastMutatedLines(List<Integer> lastMutatedLines) {
        this.lastMutatedLines = lastMutatedLines;
    }
    
    public boolean isMutateRight() {
        return mutateRight;
    }
    
    public void setMutateRight(boolean mutateRight) {
        this.mutateRight = mutateRight;
    }
    
    public boolean isGetSibling() {
        return getSibling;
    }
    
    public void setGetSibling(boolean getSibling) {
        this.getSibling = getSibling;
    }

    public boolean isStop() {
        return stop;
    }
    
    public void setStop(boolean stop) {
        this.stop = stop;
    }
    
    public String getUnskippableOJML4CFilename() {
        return unskippableOJML4CFilename;
    }
    
    public void setUnskippableOJML4CFilename(String unskippableOJML4CFilename) {
        this.unskippableOJML4CFilename = unskippableOJML4CFilename;
    }
    
    public String getUnskippableOJML4CPackage() {
        return unskippableOJML4CPackage;
    }
    
    public void setUnskippableOJML4CPackage(String unskippableOJML4CPackage) {
        this.unskippableOJML4CPackage = unskippableOJML4CPackage;
    }
    
    /** Based only on {@code fatherIndex}, {@code lastMutatedLines} and {@code lineMutationIndexes}. */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fatherIndex;
        result = prime * result + ((lastMutatedLines == null) ? 0 : lastMutatedLines.hashCode());
        result = prime * result + Arrays.hashCode(lineMutationIndexes);
        return result;
    }

    /** Compares only {@code fatherIndex}, {@code lastMutatedLines} and {@code lineMutationIndexes}. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MuJavaFeedback other = (MuJavaFeedback) obj;
        if (fatherIndex != other.fatherIndex) return false;
        if (lastMutatedLines == null) {
            if (other.lastMutatedLines != null) return false;
        } else if (!lastMutatedLines.equals(other.lastMutatedLines)) return false;
        if (!Arrays.equals(lineMutationIndexes, other.lineMutationIndexes)) return false;
        return true;
    }

}

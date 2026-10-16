package ar.edu.taco.stryker.api.impl.input;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import mujava.api.MutationOperator;
import mujava.api.Mutation;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Sets;


/**
 * Message handled by the mutation stages of Stryker:
 * {@link ar.edu.taco.stryker.api.impl.UnskippableMuJavaController UnskippableMuJavaController} (through
 * its own task queue) and {@link ar.edu.taco.stryker.api.impl.MuJavaController MuJavaController}
 * (through queue 1 of {@link ar.edu.taco.engine.StrykerStage#weedQueue}).
 * <p>
 * The same class plays several roles:
 * <ul>
 * <li><b>Initial request.</b> {@link ar.edu.taco.stryker.api.impl.SimpleStrykerAPI#fixBug
 * SimpleStrykerAPI.fixBug} creates one for the subject file and enqueues it on
 * UnskippableMuJavaController. Both mutation controllers treat an input whose
 * {@link #getMuJavaFeedback() feedback} is {@code null} as a first-generation father.</li>
 * <li><b>Candidate coming back.</b> OpenJMLController and DarwinistController send a mutant back to
 * queue 1 together with its {@link MuJavaFeedback}. MuJavaController then queues the next relevant
 * sibling of the mutant (if {@link MuJavaFeedback#isGetSibling()}) and turns the mutant into a new
 * father (if {@link MuJavaFeedback#isFatherable()}).</li>
 * <li><b>Feedback request.</b> When {@link #isComputateFeedback()} is {@code true}, every other field
 * is {@code null}. MuJavaController's thread calls
 * {@code DarwinistController.computateFeedback(getInputForFeedback())} and carries on with the
 * {@code MuJavaInput} that call returns.</li>
 * <li><b>Father record.</b> The instances kept in the controllers' {@code fathers} lists also hold the
 * bookkeeping of the father's current batch of children (see {@link OpenJMLInputWrapper}):
 * {@link #getMutatorsData()}, {@link #getIndexesToMethod()}, {@link #getPresentIndexes()},
 * {@link #getDuplicateMethodIndexes()}, {@link #getUncompilableChildrenMethodNames()},
 * {@link #getChildrenFilename()}, {@link #getJml4cFilename()} and {@link #getJml4cPackage()}.</li>
 * </ul>
 * The batch bookkeeping identifies a child by an "index string": its
 * {@link MuJavaFeedback#getLineMutationIndexes()} rendered as {@code "[ i0 i1 ... ]"} (position 0
 * first).
 */
public class MuJavaInput {

    /**
     * Path of the Java source file this input refers to. For the initial request this is the subject
     * file. For a first-generation father it is a copy in a temporary working directory, and for a
     * later father or a candidate it is the mutant's own file. {@code null} in feedback requests.
     */
    private String filename;

    /**
     * Name of the method under repair: the plain name of TACO's {@code methodToCheck}
     * ({@link ar.edu.taco.stryker.MethodToCheck#methodName}), as computed by
     * {@link ar.edu.taco.engine.StrykerStage}.
     */
    private String method;

    /**
     * muJava mutation operators applied when MuJavaController fatherizes this input.
     * UnskippableMuJavaController ignores this set for its own mutations (it uses only
     * {@code PRVOL_SMART}). The inputs it forwards to MuJavaController carry a fixed operator set that
     * is hard-coded in that class, so the operators passed to {@code SimpleStrykerAPI.fixBug} never
     * reach MuJavaController. In practice this makes no difference, because StrykerStage currently
     * passes the same set.
     */
    private Collection<MutationOperator> mutantsToApply;

    /**
     * Number of generations requested by the caller. It is only copied between inputs (often replaced
     * by {@code 0} or {@code null}) and is never consulted.
     */
    private AtomicInteger qtyOfGenerations;

    /** TACO configuration file, which DarwinistController eventually passes to {@code TacoMain.run}. */
    private String configFile;

    /**
     * TACO overriding properties. Inputs derived from a feedback request carry the modified copy built
     * for it (see {@link DarwinistInput#getOverridingProperties()}).
     */
    private Properties overridingProperties;

    /**
     * Path of the subject source file originally given to Stryker; it stays the same along the whole
     * pipeline. DarwinistController temporarily overwrites this file with a candidate (keeping a backup
     * in {@code originalFilename + "_temp"}) so that TACO analyses the candidate.
     */
    private String originalFilename;

    /**
     * DarwinistController copies this from {@link DarwinistInput#getOldFilename()}, which is always
     * {@code null} in the current code. Nothing reads it.
     */
    private String oldFilename;

    /**
     * Object created once per run by {@code SimpleStrykerAPI.fixBug} and propagated through every
     * message. Nothing currently synchronizes on it.
     */
    private Object syncObject;

    /**
     * Search state of this input. {@code null} marks a first-generation input (see the class
     * comment). For a father, this is the feedback holding its mutation lists, its mutable lines and
     * its non-compilable indexes.
     */
    private MuJavaFeedback muJavaFeedback;

    /** Father only: path of the current batch file (see {@link OpenJMLInputWrapper#getFilename()}). */
    private String childrenFilename;

    /**
     * Father only. Despite its name, this set holds index strings (not method names): the children of
     * the current batch that JML4C could not compile. Copied from
     * {@link OpenJMLInputWrapper#getUncompilableMethods()}.
     */
    private Set<String> uncompilableChildrenMethodNames;

    /**
     * Father only: maps the index string of each child of the current batch that compiled and is not a
     * duplicate to its {@link OpenJMLInput}.
     */
    private Map<String, OpenJMLInput> indexesToMethod;

    /**
     * Father only: the result of {@code MuJavaController.getMutatorsList} for this father.
     * <ul>
     * <li>Left: the father's mutations, with one array per mutable line in ascending line order (the
     * same array as {@link MuJavaFeedback#getLineMutatorsList()}).</li>
     * <li>Right-left: the mutable line numbers in descending order, which matches the positions of
     * {@link MuJavaFeedback#getLineMutationIndexes()}.</li>
     * <li>Right-right: for each line, in the same descending order, a pair {@code (r + s, r)}, where
     * {@code r} is the number of non-PRVOL ("right") mutations of the line and {@code s} the number of
     * skippable left-hand-side ones. Index values count from the end of the line's array, so values
     * {@code 1..r} select the "right" mutations.
     * {@code MuJavaController.calculateNextRelevantSonMutationsLists} uses these pairs to jump between
     * the two groups.</li>
     * </ul>
     */
    private Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData;

    /**
     * Father only: path of the current batch file after it has been adapted for JML4C and compiled
     * with jml4c. The adapted file is moved to a package ending in {@code ar.edu.itba.stryker.mutants}
     * and has its JML {@code \reach} expressions rewritten (see
     * {@link ar.edu.taco.stryker.api.impl.StringsToWriteInFile#reachMethod}). OpenJMLController runs RAC
     * on the class compiled from it.
     */
    private String jml4cFilename;

    /**
     * Father only: the package declared in {@link #getJml4cFilename()}. OpenJMLController derives the
     * classpath root and the qualified class name from it.
     */
    private String jml4cPackage;

    /**
     * Father only: the keys of {@link #getIndexesToMethod()} at the time the batch was built. If the
     * next relevant sibling is neither here nor among the uncompilable children, a new batch is built
     * starting from it.
     */
    private Set<String> presentIndexes;

    /**
     * Father only: index strings of the children discarded while building the current batch because
     * muJava produced a file identical (same MD5 digest) to an earlier mutant. They are skipped when
     * looking for the next sibling.
     */
    private Set<String> duplicateMethodIndexes;

    /**
     * Marks a feedback request. Before processing it, MuJavaController replaces this input with the
     * result of {@code DarwinistController.computateFeedback(getInputForFeedback())}. OpenJMLController
     * and DarwinistController set this flag.
     */
    private boolean computateFeedback = false;

    /**
     * For feedback requests: the {@link DarwinistInput} (with {@code forSeqProcessing == true}) to
     * compute feedback for.
     */
    private DarwinistInput inputForFeedback;

    /**
     * Set only on the inputs that UnskippableMuJavaController collects for MuJavaController. It holds
     * the {@link OpenJMLInput} of the same unskippable mutant. When UnskippableMuJavaController's queue
     * goes idle, it also sends that OpenJMLInput to OpenJMLController (queue 2) as a validate-only
     * candidate (see {@link MuJavaFeedback#isStop()}).
     */
    private OpenJMLInput unskippableOJMLInput;
    
    /**
     * JML4C batch file of the UnskippableMuJavaController father that produced
     * {@link #getUnskippableOJMLInput()}. It is copied into the validate-only candidate's feedback.
     */
    private String unskippableOJML4CFilename;

    /** Package of {@link #getUnskippableOJML4CFilename()}; copied like it. */
    private String unskippableOJML4CPackage;
    
    /**
     * Initialised from the {@code classToCheck} overriding property by {@code SimpleStrykerAPI.fixBug}.
     * Inputs that DarwinistController re-creates receive {@link DarwinistInput#getFullyQualifiedClassName()}
     * instead, which can be the JML4C batch class name or {@code null}. It is only propagated and never
     * used for decisions. Exposed by {@link #getFullyQualifiedFileName()} (sic).
     */
    private String fullyQualifiedClassName;

	/**
	 * Initialised from the {@code methodToCheck} overriding property by {@code SimpleStrykerAPI.fixBug};
	 * DarwinistController sets it to {@link #getMethod()}. It is only propagated and never used for
	 * decisions.
	 */
	private String methodUnderAnalysis;

    /**
     * Creates a MuJavaInput.
     * 
     * @param filename the source file this input refers to (see {@link #getFilename()})
     * @param method the name of the method under repair
     * @param mutantsToApply the muJava mutation operators to apply
     * @param qtyOfGenerations the requested number of generations (never consulted)
     * @param configFile the TACO configuration file
     * @param overridingProperties the TACO overriding properties
     * @param originalFilename the original subject file containing the bug to fix
     * @param syncObject per-run object propagated through all messages (not used for synchronization)
     * @param fullyQualifiedClassName see {@link #getFullyQualifiedFileName()}
     * @param methodUnderAnalysis see {@link #getMethodUnderAnalysis()}
     */
    public MuJavaInput(String filename, String method,
            Collection<MutationOperator> mutantsToApply, AtomicInteger qtyOfGenerations, String configFile, 
            Properties overridingProperties, String originalFilename, Object syncObject,
            String fullyQualifiedClassName, String methodUnderAnalysis) {
        super();
        this.filename = filename;
        this.method = method;
        this.mutantsToApply = mutantsToApply;
        this.qtyOfGenerations = qtyOfGenerations;
        this.configFile = configFile;
        this.overridingProperties = overridingProperties;
        this.originalFilename = originalFilename;
        this.syncObject = syncObject;
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.methodUnderAnalysis = methodUnderAnalysis;
    }

    public Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> getMutatorsData() {
        return mutatorsData;
    }

    public void setMutatorsData(
            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData) {
        this.mutatorsData = mutatorsData;
    }

    public Set<String> getUncompilableChildrenMethodNames() {
        return uncompilableChildrenMethodNames;
    }

    public void setUncompilableChildrenMethodNames(Set<String> uncompilableChildrenMethodNames) {
        this.uncompilableChildrenMethodNames = uncompilableChildrenMethodNames;
    }

    public Map<String, OpenJMLInput> getIndexesToMethod() {
        return indexesToMethod;
    }

    public void setIndexesToMethod(Map<String, OpenJMLInput> indexesToMethod) {
        this.indexesToMethod = indexesToMethod;
    }

    public String getChildrenFilename() {
        return childrenFilename;
    }

    public void setChildrenFilename(String childrenFilename) {
        this.childrenFilename = childrenFilename;
    }

    /**
     * @return the path of the source file this input refers to (see the field comment)
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the name of the method under repair
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the muJava mutation operators to apply when this input is fatherized
     */
    public Collection<MutationOperator> getMutantsToApply() {
        return mutantsToApply;
    }

    /**
     * @return the requested number of generations (never consulted by the controllers)
     */
    public AtomicInteger getQtyOfGenerations() {
        return qtyOfGenerations;
    }

    /**
     * @return The TACO configuration file.
     */
    public String getConfigurationFile() {
        return configFile;
    }

    public Properties getOverridingProperties() {
        return overridingProperties;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    /**
     * @return the per-run object propagated through all messages (nothing synchronizes on it)
     */
    public Object getSyncObject() {
        return syncObject;
    }

    public MuJavaFeedback getMuJavaFeedback() {
        return muJavaFeedback;
    }

    public void setMuJavaFeedback(MuJavaFeedback muJavaFeedback) {
        this.muJavaFeedback = muJavaFeedback;
    }

    public String getOldFilename() {
        return oldFilename;
    }

    public void setOldFilename(String oldFilename) {
        this.oldFilename = oldFilename;
    }

    public String getJml4cFilename() {
        return jml4cFilename;
    }

    public void setJml4cFilename(String jml4cFilename) {
        this.jml4cFilename = jml4cFilename;
    }

    public String getJml4cPackage() {
        return jml4cPackage;
    }

    public void setJml4cPackage(String jml4cPackage) {
        this.jml4cPackage = jml4cPackage;
    }

    public Set<String> getPresentIndexes() {
        return presentIndexes;
    }

    public void setPresentIndexes(Set<String> presentIndexes) {
        this.presentIndexes = presentIndexes;
    }

    public Set<String> getDuplicateMethodIndexes() {
        return duplicateMethodIndexes;
    }

    public void setDuplicateMethodIndexes(Set<String> duplicateMethodIndexes) {
        this.duplicateMethodIndexes = duplicateMethodIndexes;
    }

    public boolean isComputateFeedback() {
        return computateFeedback;
    }

    public void setComputateFeedback(boolean computateFeedback) {
        this.computateFeedback = computateFeedback;
    }

    public DarwinistInput getInputForFeedback() {
        return inputForFeedback;
    }

    public void setInputForFeedback(DarwinistInput inputForFeedback) {
        this.inputForFeedback = inputForFeedback;
    }
    
    public String getUnskippableOJML4CFilename() {
        return unskippableOJML4CFilename;
    }

    public String getUnskippableOJML4CPackage() {
        return unskippableOJML4CPackage;
    }
    
    public OpenJMLInput getUnskippableOJMLInput() {
        return unskippableOJMLInput;
    }
    
    public void setUnskippableOJMLInput(OpenJMLInput unskippableOJMLInput) {
        this.unskippableOJMLInput = unskippableOJMLInput;
    }
    public void setUnskippableOJML4CFilename(String unskippableOJML4CFilename) {
        this.unskippableOJML4CFilename = unskippableOJML4CFilename;
    }
    
    public void setUnskippableOJML4CPackage(String unskippableOJML4CPackage) {
        this.unskippableOJML4CPackage = unskippableOJML4CPackage;
    }

	/**
	 * Despite its name, returns the {@code fullyQualifiedClassName} field (see the field comment).
	 *
	 * @return the class name propagated with this input, possibly {@code null}
	 */
	public String getFullyQualifiedFileName() {
		return fullyQualifiedClassName;
	}

	public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
		this.fullyQualifiedClassName = fullyQualifiedClassName;
	}

	public void setMethodUnderAnalysis(String method2) {
		this.methodUnderAnalysis = method2;
	}
	
	public String getMethodUnderAnalysis(){
		return this.methodUnderAnalysis;
	}

}

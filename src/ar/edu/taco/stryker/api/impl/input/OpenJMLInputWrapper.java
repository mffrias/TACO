package ar.edu.taco.stryker.api.impl.input;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import mujava.api.Mutation;

import org.apache.commons.lang3.tuple.ImmutablePair;


/**
 * Describes one batch of sibling mutants that are compiled together with JML4C.
 * <p>
 * To amortise compilation, {@code MuJavaController.createJMLInputWrapper} copies the file of the first
 * mutant of the batch and inserts into it the method under repair of every other mutant, renamed to
 * the method name followed by a number (each mutant's {@link OpenJMLInput#getRacMethod()}).
 * {@code MuJavaController.buildNextBatchSiblingsFile}, which UnskippableMuJavaController also uses,
 * proceeds as follows:
 * <ol>
 * <li>generates up to {@code batchSize} non-duplicate children of a father and builds this file;</li>
 * <li>adapts the file for JML4C and compiles it;</li>
 * <li>drops the mutants whose methods do not compile and rebuilds the file;</li>
 * <li>returns this object.</li>
 * </ol>
 * <p>
 * The object does not travel through any queue, and OpenJMLController never sees it (the old
 * enqueue call is commented out). The caller copies its bookkeeping into the father
 * {@link MuJavaInput}: the index-to-input map, the uncompilable and duplicate index strings, the file
 * names and the package.
 * <p>
 * Children are identified by index strings: their {@link MuJavaFeedback#getLineMutationIndexes()}
 * rendered as {@code "[ i0 i1 ... ]"}.
 */
public class OpenJMLInputWrapper {

	/**
	 * Every mutant placed in the batch file, keyed by its {@link OpenJMLInput#getRacMethod() racMethod}.
	 * Only the unused {@code StrykerJavaFileInstrumenter.instrumentForSequentialOutput} reads it.
	 */
	private Map<String, OpenJMLInput> map;

	/**
	 * Maps the index string of each child of the batch that compiled to its {@link OpenJMLInput}. Set
	 * once compilation succeeds.
	 */
	private Map<String, OpenJMLInput> indexesToMethod;
	
	/**
	 * Despite its name, this set holds index strings (not method names): the children removed from the
	 * batch because JML4C reported errors in their methods.
	 */
	private Set<String> uncompilableMethods;

    /**
     * Index strings of the children skipped while generating the batch because they duplicated an
     * earlier mutant (same MD5 digest).
     */
    private Set<String> duplicateMethods;

	/**
	 * Path of the batch file, {@code <tmp>/<n>/a<n>/aOpenJMLInWrap<i>/<Class>.java}, where {@code <n>}
	 * is a fresh temporary directory name (dashes removed in the {@code a<n>} part).
	 */
	private String filename;

	/**
	 * Path of the JML4C-adapted copy of the batch file, in a package ending in
	 * {@code ar.edu.itba.stryker.mutants} (see {@code MuJavaController.adaptSiblingsFileToJML4C}).
	 */
	private String jml4cFilename;

	/** Package written into {@link #getJml4cFilename()}. */
	private String jml4cPackage;

	/** TACO configuration file, taken from the first mutant of the batch. */
	private String configFile;
	
	/** Name of the method under repair (the original, non-renamed name). */
	private String method;
	
	/** TACO overriding properties, taken from the first mutant of the batch. */
	private Properties overridingProperties;
	
	/** Legacy: only the unused {@code StrykerJavaFileInstrumenter.instrumentForSequentialOutput} sets it. */
	private String seqFilesPrefix; //Used for instrumentation

	/** Legacy: never set. */
	private String variablizedFilename; //Used for instrumentation

	/** Path of the original subject file. */
	private String originalFilename; //Used for instrumentation

	/** Legacy: only the unused {@code StrykerJavaFileInstrumenter.instrumentForSequentialOutput} sets it. */
	private String oldFilename; //Used for instrumentation
	
	/** Legacy: never set or read outside this class. */
	private boolean forSeqProcessing;
	
	/**
	 * The mutations and {@code lineMutationIndexes} of the first non-duplicate child generated for this
	 * batch. Callers use it as the next sibling to enqueue, or to derive the base sibling.
	 */
	private ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingsMutationsLists;
	
	/**
	 * Creates an OpenJMLInputWrapper.
	 * 
	 * @param filename path of the batch file
	 * @param configFile the TACO configuration file
	 * @param overridingProperties the TACO overriding properties
	 * @param method name of the method under repair
	 * @param map the mutants in the batch, keyed by racMethod
	 * @param originalFilename the original subject file
	 */	
	public OpenJMLInputWrapper(String filename, String configFile, Properties overridingProperties, 
	        String method, Map<String,OpenJMLInput> map, String originalFilename) {
		super();
		this.filename = filename;
		this.configFile = configFile;
		this.overridingProperties = overridingProperties;
		this.method = method;
		this.map = map;
		this.originalFilename = originalFilename;
	}

	public ImmutablePair<List<Mutation>, Integer[]> getNextRelevantSiblingsMutationsLists() {
        return nextRelevantSiblingsMutationsLists;
    }
	
	public void setNextRelevantSiblingsMutationsLists(
            ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingsMutationsLists) {
        this.nextRelevantSiblingsMutationsLists = nextRelevantSiblingsMutationsLists;
    }
	
	/**
	 * @return the path of the batch file
	 */
	public String getFilename() {
		return filename;
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
	
	/**
	 * @return The TACO configuration file.
	 */
	public String getConfigurationFile() {
		return configFile;
	}

	public Properties getOverridingProperties() {
		return overridingProperties;
	}
	
	public Map<String, OpenJMLInput> getMap() {
		return map;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setSeqFilesPrefix(String sequentialMethodsFolder) {
        this.seqFilesPrefix = sequentialMethodsFolder;
    }
	
	public String getSeqFilesPrefix() {
        return seqFilesPrefix;
    }
	
	public void setOldFilename(String oldFilename) {
        this.oldFilename = oldFilename;
    }
	
	public String getOldFilename() {
        return oldFilename;
    }
	
	public void setVariablizedFilename(String variablizedFilename) {
        this.variablizedFilename = variablizedFilename;
    }
	
	public String getVariablizedFilename() {
        return variablizedFilename;
    }
	
	public String getOriginalFilename() {
        return originalFilename;
    }
	
	public boolean isForSeqProcessing() {
        return forSeqProcessing;
    }
	
	public void setForSeqProcessing(boolean forSeqProcessing) {
        this.forSeqProcessing = forSeqProcessing;
    }
	
	public Set<String> getUncompilableMethods() {
        return uncompilableMethods;
    }
	
	public void setUncompilableMethods(Set<String> uncompilableMethods) {
        this.uncompilableMethods = uncompilableMethods;
    }
	
	public Map<String, OpenJMLInput> getIndexesToMethod() {
        return indexesToMethod;
    }
	
	public void setIndexesToMethod(Map<String, OpenJMLInput> indexesToMethod) {
        this.indexesToMethod = indexesToMethod;
    }
	
	/**
	 * @return the index strings of duplicate children (backed by the {@code duplicateMethods} field)
	 */
	public Set<String> getDuplicateMethodIndexes() {
        return duplicateMethods;
    }
	
	public void setDuplicateMethods(Set<String> duplicateMethods) {
        this.duplicateMethods = duplicateMethods;
    }
	
}

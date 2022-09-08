package ar.edu.taco.stryker.api.impl.input;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import mujava.api.Mutation;

import org.apache.commons.lang3.tuple.ImmutablePair;


public class OpenJMLInputWrapper {

	private Map<String, OpenJMLInput> map;

	private Map<String, OpenJMLInput> indexesToMethod;
	
	private Set<String> uncompilableMethods;

    private Set<String> duplicateMethods;

	private String filename;

	private String jml4cFilename;

	private String jml4cPackage;

	private String configFile;
	
	private String method;
	
	private Properties overridingProperties;
	
	private String seqFilesPrefix; //Used for instrumentation

	private String variablizedFilename; //Used for instrumentation

	private String originalFilename; //Used for instrumentation

	private String oldFilename; //Used for instrumentation
	
	private boolean forSeqProcessing;
	
	private ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingsMutationsLists;
	
	/**
	 * Creates a OpenJMLInput.
	 * 
	 * @param filename The filename that contains the class that has failed.
	 * @param junitFile The file that contains all the statements that will make the class fail.
	 * @param configFile The TACO configuration file.
	 * @param overridingProperties The overriding properties
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
	 * @return The filename that contains the class that has failed.
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
	
	public Set<String> getDuplicateMethodIndexes() {
        return duplicateMethods;
    }
	
	public void setDuplicateMethods(Set<String> duplicateMethods) {
        this.duplicateMethods = duplicateMethods;
    }
	
}

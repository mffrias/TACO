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


public class MuJavaInput {

    private String filename;

    private String method;

    private Collection<MutationOperator> mutantsToApply;

    private AtomicInteger qtyOfGenerations;

    private String configFile;

    private Properties overridingProperties;

    private String originalFilename;

    private String oldFilename;

    private Object syncObject;

    private MuJavaFeedback muJavaFeedback;

    private String childrenFilename;

    private Set<String> uncompilableChildrenMethodNames;

    private Map<String, OpenJMLInput> indexesToMethod;

    private Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData;

    private String jml4cFilename;

    private String jml4cPackage;

    private Set<String> presentIndexes;

    private Set<String> duplicateMethodIndexes;

    private boolean computateFeedback = false;

    private DarwinistInput inputForFeedback;

    private OpenJMLInput unskippableOJMLInput;
    
    private String unskippableOJML4CFilename;

    private String unskippableOJML4CPackage;
    
    private String fullyQualifiedClassName;

	private String methodUnderAnalysis;

    /**
     * Creates a MuJavaInput.
     * 
     * @param filename The filename that contains the class that has failed.
     * @param method The method that has failed.
     * @param junitFile The file that contains all the statements that will make the class fail.
     * @param mutantsToApply The list of mutants to apply.
     * @param qtyOfGenerations The quantity of generations that must be created.
     * @param configFile The TACO configuration file.
     * @param overridingProperties The TACO overriding properties
     * @param originalFilename The original filename with the bug to solve
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
     * @return The filename of the class that has failed
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return The method that failed
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return All the mutants that must be applied
     */
    public Collection<MutationOperator> getMutantsToApply() {
        return mutantsToApply;
    }

    /**
     * @return The quantity of generations to create
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
     * @return the syncObject
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

package ar.edu.taco.stryker.api.impl.input;

import java.util.Collection;
import java.util.Properties;

import mujava.api.MutationOperator;


public class OpenJMLInput {

    private String filename;

    private String method;

    private String racMethod;

    private String configFile;

    private Properties overridingProperties;

    private String originalFilename;

    private MuJavaFeedback feedback;

    private Collection<MutationOperator> mutantsToApply;

    private Object syncObject;
    
    private String fullyQualifiedClassName;
    
    private String methodUnderAnalysis;

    /**
     * Creates a OpenJMLInput.
     * 
     * @param filename The filename that contains the class that has failed.
     * @param method The method that has failed.
     * @param junitFile The file that contains all the statements that will make the class fail.
     * @param configFile The TACO configuration file.
     * @param overridingProperties The overriding properties
     * @param originalFilename The original filename
     */	
    public OpenJMLInput(String filename, String method, String configFile, 
            Properties overridingProperties, String originalFilename, MuJavaFeedback feedback, Collection<MutationOperator> mutantsToApply, Object syncObject,
            String fullyQualifiedClassName, String methodUnderAnalysis) {
        super();
        this.filename = filename;
        this.method = method;
        this.configFile = configFile;
        this.overridingProperties = overridingProperties;
        this.originalFilename = originalFilename;
        this.feedback = feedback;
        this.mutantsToApply = mutantsToApply;
        this.syncObject = syncObject;
    }

    /**
     * @return The method that has failed.
     */
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getRacMethod() {
        return racMethod;
    }
    
    public void setRacMethod(String racMethod) {
        this.racMethod = racMethod;
    }

    /**
     * @return The filename that contains the class that has failed.
     */
    public String getFilename() {
        return filename;
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

    /**
     * @return the originalFilename
     */
    public String getOriginalFilename() {
        return originalFilename;
    }

    public MuJavaFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(MuJavaFeedback feedback) {
        this.feedback = feedback;
    }

    public Collection<MutationOperator> getMutantsToApply() {
        return mutantsToApply;
    }

    public void setMutantsToApply(Collection<MutationOperator> mutantsToApply) {
        this.mutantsToApply = mutantsToApply;
    }

    public Object getSyncObject() {
        return syncObject;
    }

    public void setSyncObject(Object syncObject) {
        this.syncObject = syncObject;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((feedback == null) ? 0 : feedback.hashCode());
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((originalFilename == null) ? 0 : originalFilename.hashCode());
        result = prime * result + ((racMethod == null) ? 0 : racMethod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        OpenJMLInput other = (OpenJMLInput) obj;
        if (feedback == null) {
            if (other.feedback != null) return false;
        } else if (!feedback.equals(other.feedback)) return false;
        if (filename == null) {
            if (other.filename != null) return false;
        } else if (!filename.equals(other.filename)) return false;
        if (method == null) {
            if (other.method != null) return false;
        } else if (!method.equals(other.method)) return false;
        if (originalFilename == null) {
            if (other.originalFilename != null) return false;
        } else if (!originalFilename.equals(other.originalFilename)) return false;
        if (racMethod == null) {
            if (other.racMethod != null) return false;
        } else if (!racMethod.equals(other.racMethod)) return false;
        return true;
    }

	public String getFullyQualifiedClassName() {
		return fullyQualifiedClassName;
	}

	public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
		this.fullyQualifiedClassName = fullyQualifiedClassName;
	}

	public String getMethodUnderAnalysis() {
		return methodUnderAnalysis;
	}

	public void setMethodUnderAnalysis(String methodUnderAnalysis) {
		this.methodUnderAnalysis = methodUnderAnalysis;
	}

    
}

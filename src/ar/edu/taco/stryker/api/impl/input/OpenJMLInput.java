package ar.edu.taco.stryker.api.impl.input;

import java.util.Collection;
import java.util.Properties;

import mujava.api.MutationOperator;


/**
 * A single mutant (a child of some father) that
 * {@link ar.edu.taco.stryker.api.impl.OpenJMLController OpenJMLController} checks with JML runtime
 * assertion checking (RAC) against the stored JUnit inputs.
 * <p>
 * {@code MuJavaController.mutateWithoutCompiling} creates one for every non-duplicate mutant written
 * while building a batch (see {@link OpenJMLInputWrapper}). The input is kept in the father's
 * {@link MuJavaInput#getIndexesToMethod()} until {@code MuJavaController.queueNextRelevantSibling}
 * selects it as the next relevant sibling. That method attaches a fresh {@link MuJavaFeedback} and puts
 * the input on queue 2 of {@link ar.edu.taco.engine.StrykerStage#weedQueue}. UnskippableMuJavaController
 * also puts validate-only inputs there ({@link MuJavaFeedback#isStop()}).
 * <p>
 * OpenJMLController invokes the {@code @Test} method of up to nine stored JUnit inputs, passing them the
 * classpath and qualified name of the father's JML4C-compiled batch class and {@link #getRacMethod()}
 * as the method to run. If the mutant passes them all, it is wrapped in a
 * {@link DarwinistInput} and sent to queue 3. If it fails and is not a stop candidate,
 * OpenJMLController sends a feedback request to queue 1, or a plain {@link MuJavaInput} when feedback
 * is disabled.
 */
public class OpenJMLInput {

    /** Path of this mutant's own source file, as written by muJava. */
    private String filename;

    /** Name of the method under repair. */
    private String method;

    /**
     * Name of this mutant's copy of the method inside the father's batch class.
     * {@code MuJavaController.createJMLInputWrapper} assigns it: the first mutant of a batch keeps
     * {@link #getMethod()}, and the others get the method name followed by a number. OpenJMLController
     * passes it to the JUnit inputs as the method to run, and MuJavaController uses it to map JML4C
     * compilation errors back to mutants.
     */
    private String racMethod;

    /** TACO configuration file. */
    private String configFile;

    /** TACO overriding properties. */
    private Properties overridingProperties;

    /** Path of the original subject file (see {@link MuJavaInput#getOriginalFilename()}). */
    private String originalFilename;

    /**
     * Search state of this mutant. It is set at creation and replaced with a fresh one when the mutant
     * is enqueued on queue 2.
     */
    private MuJavaFeedback feedback;

    /** muJava mutation operators, carried along for later generations. */
    private Collection<MutationOperator> mutantsToApply;

    /** Per-run object propagated through all messages; nothing synchronizes on it. */
    private Object syncObject;
    
    /**
     * Not initialised by the constructor, which ignores the matching parameter. Only
     * UnskippableMuJavaController sets it (to {@link MuJavaInput#getFullyQualifiedFileName()}). It is
     * only propagated.
     */
    private String fullyQualifiedClassName;
    
    /**
     * Not initialised by the constructor, which ignores the matching parameter. Only
     * UnskippableMuJavaController sets it (to {@link MuJavaInput#getMethodUnderAnalysis()}). It is only
     * propagated.
     */
    private String methodUnderAnalysis;

    /**
     * Creates an OpenJMLInput.
     * 
     * @param filename path of the mutant's own source file
     * @param method name of the method under repair
     * @param configFile the TACO configuration file
     * @param overridingProperties the TACO overriding properties
     * @param originalFilename the original subject file
     * @param feedback the mutant's search state
     * @param mutantsToApply the muJava mutation operators carried along
     * @param syncObject per-run object propagated through all messages
     * @param fullyQualifiedClassName ignored: this constructor does not store it
     * @param methodUnderAnalysis ignored: this constructor does not store it
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
     * @return the name of the method under repair
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
     * @return the path of this mutant's own source file
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

    /**
     * Based on {@code feedback}, {@code filename}, {@code method}, {@code originalFilename} and
     * {@code racMethod}.
     */
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

    /**
     * Compares {@code feedback}, {@code filename}, {@code method}, {@code originalFilename} and
     * {@code racMethod}. MuJavaController relies on it when removing non-compiling mutants from a
     * batch ({@code List.removeAll}).
     */
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

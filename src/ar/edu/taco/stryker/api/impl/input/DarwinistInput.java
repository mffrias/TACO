package ar.edu.taco.stryker.api.impl.input;

import java.util.Collection;
import java.util.Properties;
import java.util.Stack;

import mujava.api.MutationOperator;

/**
 * Unit of work for {@link ar.edu.taco.stryker.api.impl.DarwinistController DarwinistController}, the
 * stage that analyses candidates with TACO (bounded verification). It is used in two ways.
 * <ul>
 * <li><b>Candidate validation</b> ({@code forSeqProcessing == false}). OpenJMLController creates it
 * when a mutant passes RAC and puts it on queue 3 of {@link ar.edu.taco.engine.StrykerStage#weedQueue}.
 * DarwinistController copies {@link #getFilename()} over {@link #getOriginalFilename()}, compiles it and
 * runs TACO. If TACO reports UNSAT, DarwinistController re-runs every stored JUnit input with
 * {@link #getParametersFromOpenJML()} and reports a fix if they all pass.</li>
 * <li><b>Feedback computation</b> ({@code forSeqProcessing == true}). Created by OpenJMLController
 * (RAC failure) and by DarwinistController (a TACO counterexample, or a failing re-run of the stored
 * inputs). The creator wraps it in a {@link MuJavaInput} with {@link MuJavaInput#isComputateFeedback()}
 * set and puts it on queue 1. MuJavaController's thread then calls
 * {@code DarwinistController.computateFeedback}, which:
 * <ol>
 * <li>writes a copy at {@link #getSeqFilesPrefix()} with mutIDs inserted and loops unrolled;</li>
 * <li>variablizes the mutable lines of that copy;</li>
 * <li>checks it with TACO on the failing input {@link #getSeqMethodInput()};</li>
 * <li>updates {@link #getFeedback()} with the outcome.</li>
 * </ol>
 * </li>
 * </ul>
 * DarwinistController's loop also accepts {@code forSeqProcessing} inputs from queue 3 and re-wraps
 * them for queue 1, although no producer currently sends them there. When OpenJMLController stops, it
 * enqueues on queue 3 an instance whose fields are all {@code null} (with
 * {@code forSeqProcessing == false}).
 */
public class DarwinistInput {
	
	/** Path of the candidate mutant's own source file. */
	private String filename;
	
	/**
	 * Path of the subject source file. DarwinistController temporarily overwrites it with the candidate
	 * (or with its variablized version), compiles and analyses it, and then restores it from the backup
	 * {@code originalFilename + "_temp"}.
	 */
	private String originalFilename;
	
	/** TACO configuration file passed to {@code TacoMain.run}. */
	private String configFile;
	
	/** Name of the method under repair. */
	private String method;

	/**
	 * Name of the candidate's method inside the JML4C batch class (see
	 * {@link OpenJMLInput#getRacMethod()}). Only propagated.
	 */
	private String racMethod;

	/**
	 * TACO overriding properties. OpenJMLController and {@code DarwinistController.queueFalseCandidate}
	 * build a modified copy for feedback requests. Among the keys already present,
	 * {@code attemptToCorrectBug}, {@code generateUnitTestCase} and {@code generateRun} are set to
	 * {@code false} and {@code generateCheck} to {@code true}; {@code methodToCheck} is kept.
	 */
	private Properties overridingProperties;

	/**
	 * For candidates from OpenJMLController: the qualified name of the JML4C-compiled batch class. It is
	 * {@code null} in the feedback requests that OpenJMLController builds. Only propagated (into the
	 * {@link MuJavaInput}s that DarwinistController creates).
	 */
	private String fullyQualifiedClassName;
	
	/**
	 * The arguments with which OpenJMLController invoked the {@code @Test} method of the stored JUnit
	 * inputs: {@code {classpath of the batch class (plus jml4c.jar), its qualified name, racMethod}}.
	 * DarwinistController reuses them to re-run every stored input on a candidate that TACO found
	 * UNSAT. {@code null} in feedback requests.
	 */
	private Object[] parametersFromOpenJML;
	
	/** {@code true} for feedback requests, {@code false} for candidate validation (see class comment). */
	private Boolean forSeqProcessing;
	
	/**
	 * Path of the working copy that {@code computateFeedback} instruments: the candidate's path with
	 * {@code sequential/} inserted before the file name. Despite its name, this is a file path and not a
	 * prefix. Only the unused StrykerJavaFileInstrumenter methods derive {@code <seqFilesPrefix>_<method>}
	 * trace files from it.
	 */
	private String seqFilesPrefix; //Used for instrumentation

	/**
	 * Name of the method to process (the same as {@link #getMethod()}). Only the currently unused
	 * {@code StrykerJavaFileInstrumenter.replaceMethodBodies} reads it.
	 */
	private String seqMethod;

	/**
	 * Path of the JUnit file holding the input that exposes the failure. This is either the stored input
	 * that failed RAC (set by OpenJMLController) or the counterexample test that DarwinistController
	 * generated from a SAT result of TACO. When DarwinistController's final re-run of the stored inputs
	 * ends in an exception, it overwrites this field with the input being run. {@code computateFeedback}
	 * runs TACO fixed to this input; when it is {@code null}, the first stored input is used.
	 */
	private String seqMethodInput;
	
	/**
	 * File that variablization rewrites and that DarwinistController then compiles and analyses.
	 * {@code computateFeedback} sets it to {@link #getSeqFilesPrefix()}.
	 */
	private String seqVariablizedFilename;
	
    /**
     * Always {@code null}, because every constructor call site passes {@code null}. It is copied to
     * {@link MuJavaInput#setOldFilename(String)}.
     */
    private String oldFilename; //Used for instrumentation

    /**
     * Search state of the candidate. {@code computateFeedback} and the validation paths update it in
     * place before handing it back to MuJavaController.
     */
    private MuJavaFeedback feedback;
    
    /** muJava mutation operators, carried along for later generations. */
    private Collection<MutationOperator> mutantsToApply;
    
    /** Per-run object propagated through all messages; nothing synchronizes on it. */
    private Object syncObject;

	/**
	 * Creates a DarwinistInput.
	 * 
	 * @param filename path of the candidate mutant's own source file
	 * @param originalFilename path of the original subject file
	 * @param configFile the TACO configuration file
	 * @param method name of the method under repair
	 * @param overridingProperties the TACO overriding properties
	 * @param fullyQualifiedClassName see {@link #getFullyQualifiedClassName()}
	 * @param parametersFromOpenJML see {@link #getParametersFromOpenJML()}
	 * @param forSeqProcessing {@code true} for a feedback request
	 * @param seqMethod see {@link #getSeqMethod()}
	 * @param seqMethodInput see {@link #getSeqMethodInput()}
	 * @param seqFilesPrefix see {@link #getSeqFilesPrefix()} (callers pass {@code null};
	 *        {@code computateFeedback} sets it)
	 * @param seqVariablizedFilename see {@link #getSeqVariablizedFilename()} (callers pass {@code null})
	 * @param oldFilename always {@code null} at the current call sites
	 * @param feedback the candidate's search state
	 * @param mutantsToApply the muJava mutation operators carried along
	 * @param syncObject per-run object propagated through all messages
	 */
	public DarwinistInput(String filename, String originalFilename, String configFile, String method, 
	        Properties overridingProperties, String fullyQualifiedClassName, 
	        Object[] parametersFromOpenJML, Boolean forSeqProcessing, String seqMethod, 
	        String seqMethodInput, String seqFilesPrefix, String seqVariablizedFilename, String oldFilename,
	        MuJavaFeedback feedback, Collection<MutationOperator> mutantsToApply, Object syncObject) {
		super();
		this.filename = filename;
		this.originalFilename = originalFilename;
		this.configFile = configFile;
		this.method = method;
		this.overridingProperties = overridingProperties;
		this.fullyQualifiedClassName = fullyQualifiedClassName;
		this.parametersFromOpenJML = parametersFromOpenJML;
		this.forSeqProcessing = forSeqProcessing;
		this.seqMethod = seqMethod;
		this.seqMethodInput = seqMethodInput;
		this.seqFilesPrefix = seqFilesPrefix;
		this.seqVariablizedFilename = seqVariablizedFilename;
		this.oldFilename = oldFilename;
		this.feedback = feedback;
		this.mutantsToApply = mutantsToApply;
		this.syncObject = syncObject;
	}

	/**
	 * Tells whether this input is a feedback request. Such a request is variablized by
	 * {@code DarwinistController.computateFeedback}, whose outcome tells MuJavaController which siblings
	 * are not worth generating. Otherwise the input is a candidate to validate.
	 *
	 * @return {@code true} for a feedback request, {@code false} for candidate validation
	 */
	public Boolean isForSeqProcessing() {
        return forSeqProcessing;
    }

	/**
	 * This is the seq method to be used for seq processing. Only the currently unused
	 * {@code StrykerJavaFileInstrumenter.replaceMethodBodies} reads it.
	 *
	 * @return the name of the method to process (the same as {@link #getMethod()})
	 */
	public String getSeqMethod() {
        return seqMethod;
    }
	
	public String getSeqMethodInput() {
        return seqMethodInput;
    }
	
	public void setSeqMethodInput(String seqMethodInput) {
        this.seqMethodInput = seqMethodInput;
    }
	
	public String getRacMethod() {
        return racMethod;
    }
	
	public void setRacMethod(String racMethod) {
        this.racMethod = racMethod;
    }
	
	public String getOldFilename() {
        return oldFilename;
    }
	
	public String getSeqFilesPrefix() {
        return seqFilesPrefix;
    }
	
	public void setSeqFilesPrefix(String seqFilesPrefix) {
        this.seqFilesPrefix = seqFilesPrefix;
    }
	
	public String getSeqVariablizedFilename() {
        return seqVariablizedFilename;
    }
	
	public void setSeqVariablizedFilename(String seqVariablizedFilename) {
        this.seqVariablizedFilename = seqVariablizedFilename;
    }
	
	/**
	 * @return the path of the candidate mutant's own source file
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return The original filename that contains the bug that is intended to solve
	 */
	public String getOriginalFilename() {
		return originalFilename;
	}
	
	/**
	 * @return The TACO configuration file.
	 */
	public String getConfigurationFile() {
		return configFile;
	}

	/**
	 * @return the name of the method under repair
	 */
	public String getMethod() {
		return method;
	}

	public Properties getOverridingProperties() {
		return overridingProperties;
	}

	public String getFullyQualifiedClassName() {
		return fullyQualifiedClassName;
	}	
	
	public Object[] getParametersFromOpenJML(){
		return this.parametersFromOpenJML;
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

}

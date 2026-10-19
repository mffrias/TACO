package ar.edu.taco.engine;

import java.util.HashMap;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;
import org.multijava.mjc.JCompilationUnitType;

import ar.edu.taco.TacoAnalysisResult;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.junit.InformationRecoveryManager;
import ar.edu.taco.junit.RecoveredInformation;
import ar.edu.taco.snapshot.SnapshotBuilder;
import ar.edu.taco.stryker.api.impl.NullOutputStream;

/**
 * Reads a counterexample back as Java values: the first half of turning it into a JUnit
 * test, before {@link JUnitStage} (or {@link JUnitStageFinalState}). From the compilation
 * unit of the class under analysis it recovers the names of the fields, static fields and the
 * method's parameters ({@code InformationRecoveryManager}); then, for a SAT {@code Check},
 * {@code SnapshotBuilder} evaluates the Alloy solution into objects: the receiver, static
 * fields and parameters in the pre-state ({@code createSnapshot}), or, with an input to fix,
 * in the post-state ({@code createSnapshotFinalState}). The result is a
 * {@link RecoveredInformation}.
 *
 * <p>Used by {@code TacoMain.run} after a SAT analysis, and by Stryker's
 * {@code DarwinistController} for the counterexamples of refuted candidates. Holds no static
 * state.
 */
public class SnapshotStage implements ITacoStage {
	private static Logger log = Logger.getLogger(SnapshotStage.class);

	private String classToCheck;
	private String methodToCheck;
	private List<JCompilationUnitType> asts;
	private TacoAnalysisResult tacoAnalysisResult;
	private Object inputToFix = null;
	//	private Map<String, Object> snapshot;
	private RecoveredInformation recoveredInformation;

	private boolean noVerify = false;
	private ClassLoader loader = Thread.currentThread().getContextClassLoader();
	/**
	 * Sets the class loader that {@code SnapshotBuilder} loads the analysed classes with; by
	 * default, the context class loader of the thread that created this stage. No caller.
	 *
	 * @param loader the class loader
	 */
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}
	/**
	 * With {@code true}, {@link #execute()} reads the counterexample even when the
	 * configuration's {@code noVerify} is set. No caller.
	 *
	 * @param value whether to ignore {@code noVerify}
	 */
	public void setNoVerify(boolean value) {
		this.noVerify = value;
	}

	//	/**
	//	 * @return the snapshot
	//	 */
	//	public Map<String, Object> getSnapshot() {
	//		return snapshot;
	//	}

	/**
	 * 
	 * @param asts
	 * @param classToCheck
	 * @param methodToCheck
	 */
	public SnapshotStage(List<JCompilationUnitType> asts,
			TacoAnalysisResult tacoAnalysisResult, String classToCheck,
			String methodToCheck) {
		this.asts = asts;
		this.classToCheck = classToCheck;
		this.methodToCheck = methodToCheck.substring(0, methodToCheck.indexOf('('));
		this.tacoAnalysisResult = tacoAnalysisResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ar.edu.taco.engine.ITacoStage#execute()
	 */
	/**
	 * Builds the {@link RecoveredInformation} ({@link #getRecoveredInformation()}). The names
	 * are recovered only from the unit whose package and file name equal the class to check;
	 * if none does, the information stays invalid and no test is written. Unless
	 * {@code noVerify} is set (and {@link #setNoVerify(boolean)} was not used), it then marks
	 * the information invalid if the analysis is not SAT or its command is not a
	 * {@code Check}, and otherwise fills its snapshot from the Alloy solution. Assumes the
	 * analysis ran: a {@code null} Alloy result fails.
	 *
	 * @throws TacoException wrapping any exception, e.g. when a class cannot be loaded or a
	 *         value cannot be built
	 */
	@Override
	public void execute() throws TacoException {
		try {
			recoveredInformation = new RecoveredInformation();
			recoveredInformation.setClassToCheck(classToCheck);
			recoveredInformation.setMethodToCheck(methodToCheck);

			// INFORMATION RECOVERY
			InformationRecoveryManager informationRecoveryManager = new InformationRecoveryManager(
					methodToCheck, recoveredInformation);

			for (JCompilationUnitType aJCompilationUnitType : this.asts) {
				String unitTypeName = aJCompilationUnitType.packageNameAsString()
						.replace("/", ".") + aJCompilationUnitType.fileNameIdent();

				if (unitTypeName.equals(classToCheck)) {					
					informationRecoveryManager
					.processTypeDeclaration(aJCompilationUnitType);
				}
			}
			// END INFORMATION RECOVERY

			if (!TacoConfigurator.getInstance().getNoVerify() || noVerify) {
				if (!tacoAnalysisResult.get_alloy_analysis_result().isSAT() || 
						!tacoAnalysisResult.get_alloy_analysis_result().getAlloy_solution().getOriginalCommand().startsWith("Check")) {

					if (!tacoAnalysisResult.get_alloy_analysis_result().isSAT()) {
						log.info("****** JUnit generation: not SAT ******");
					}
					recoveredInformation.setValidInformation(false);

				} else {			
					SnapshotBuilder snapshotBuilder = new SnapshotBuilder(
							recoveredInformation, this.tacoAnalysisResult, this.inputToFix);
					snapshotBuilder.setLoader(loader);
					if (this.inputToFix == null) {
						snapshotBuilder.createSnapshot();
					} else {
						snapshotBuilder.createSnapshotFinalState();
					}
				}
			}

		} catch (Exception e) {
			throw new TacoException(e.getMessage(), e);
		}

	}

	/**
	 * Returns the information built by the last {@link #execute()}, to be given to
	 * {@link JUnitStage} or {@link JUnitStageFinalState} (after
	 * {@code setFileNameSuffix(StrykerStage.fileSuffix)}).
	 *
	 * @return the recovered information, or {@code null} before {@link #execute()}
	 */
	public RecoveredInformation getRecoveredInformation() {
		return recoveredInformation;
	}

	/**
	 * Sets the input to fix. With {@code null} (the default), {@link #execute()} reads the
	 * pre-state; otherwise it reads the post-state, as {@code TacoMain.run} does for the
	 * final-state test when computing the specified output. The value is also passed to
	 * {@code SnapshotBuilder}. See docs/SPEC-OUTPUT.md.
	 *
	 * @param inputToFix the input to fix, or {@code null}
	 */
	public void setInputToFix(Object inputToFix) {
		this.inputToFix = inputToFix;
	}
}

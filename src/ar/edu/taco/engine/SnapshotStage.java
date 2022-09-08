package ar.edu.taco.engine;

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

public class SnapshotStage implements ITacoStage {
	private static Logger log = Logger.getLogger(SnapshotStage.class);

	private String classToCheck;
	private String methodToCheck;
	private List<JCompilationUnitType> asts;
	private TacoAnalysisResult tacoAnalysisResult;

	//	private Map<String, Object> snapshot;
	private RecoveredInformation recoveredInformation;

	private boolean noVerify = false;
	private ClassLoader loader = Thread.currentThread().getContextClassLoader();
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}
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
							recoveredInformation, this.tacoAnalysisResult);
					snapshotBuilder.setLoader(loader);
					snapshotBuilder.createSnapshot();				
				}
			}

		} catch (Exception e) {
			throw new TacoException(e.getMessage(), e);
		}

	}

	public RecoveredInformation getRecoveredInformation() {
		return recoveredInformation;
	}

}

package ar.edu.taco.engine;

import edu.mit.csail.sdg.alloy4.A4Reporter;

import java.util.HashSet;

import ar.edu.taco.TacoException;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisException;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalyzer;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

/**
 * The solving stage, after {@link DynalloyStage}: solves the Alloy model that it wrote
 * ({@code output/output.als}) with dynalloy4's {@code AlloyAnalyzer}, which runs the first
 * command of the model (with {@code generateCheck}, the default, the {@code check} of the
 * method's assertion) on Kodkod, with the SAT solver chosen by the system property
 * {@code dynalloy.solver} ({@code auto}, the default,
 * {@code minisat} or {@code sat4j}; see dynalloy4's docs/SOLVERS.md). For a check, SAT means
 * a counterexample: the method violates its contract for some input within the bounds.
 * {@code TacoMain.run} runs it unless {@code noVerify} is set. Holds no static state.
 */
public class AlloyStage implements ITacoStage {

	private String alloy_filename;
	private AlloyAnalysisResult analysis_result;
	

	/**
	 * Creates the stage for one Alloy file.
	 *
	 * @param alloy_filename path of the Alloy model to solve
	 *        ({@link DynalloyStage#get_alloy_filename()})
	 */
	public AlloyStage(String alloy_filename) {
		this.alloy_filename = alloy_filename;
	}

	/**
	 * Returns the result of the last {@link #execute()}: whether the command was satisfiable,
	 * and the Alloy world, command and solution, which the snapshot and JUnit stages read the
	 * counterexample from.
	 *
	 * @return the analysis result; {@code null} before {@link #execute()}, and also after it
	 *         when dynalloy4's {@code AlloyAnalyzer} got no answer from the translation to
	 *         Kodkod
	 */
	public AlloyAnalysisResult get_analysis_result() {
		return this.analysis_result;
	}

	/**
	 * Parses and type-checks the Alloy file, translates its first command to Kodkod and
	 * solves it.
	 *
	 * @throws TacoException if dynalloy4 reports an {@code AlloyAnalysisException} (e.g. a
	 *         model that does not type-check, or one with no command) or a {@code VizException};
	 *         an {@link IllegalStateException} from dynalloy4, when
	 *         {@code dynalloy.solver=minisat} and MiniSat 2.2 cannot be loaded, is not caught
	 */
	@Override
	public void execute() {
		AlloyAnalyzer alloyVerificationRunner = new AlloyAnalyzer(alloy_filename, new A4Reporter());
		try {
			analysis_result = alloyVerificationRunner.analyzeCommand(null, AlloyAnalyzer.build_A4Options());
		} catch (VizException e) {
			throw new TacoException(e.getMessage());
		} catch (AlloyAnalysisException e) {
			throw new TacoException(e.getMessage());
		}

	}

}

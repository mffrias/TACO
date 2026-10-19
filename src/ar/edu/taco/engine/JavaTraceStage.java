package ar.edu.taco.engine;

import java.io.IOException;

import ar.edu.taco.TacoException;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;
import ar.uba.dc.rfm.dynalloy.trace.DynAlloySolution;
import ar.uba.dc.rfm.dynalloy.trace.DynAlloySolutionBuilder;
import ar.uba.dc.rfm.dynalloy.xlator.SpecContext;
import edu.mit.csail.sdg.alloy4.Err;

/**
 * Builds the execution trace of a counterexample: dynalloy4's {@code DynAlloySolution},
 * the steps of the DynAlloy program (nested for calls) with the pre- and postcondition and an
 * evaluator over the Alloy solution. {@code TacoMain.run} creates it when
 * {@code buildJavaTrace} is set and the analysis is SAT, and does not read the result; as the
 * code stands, {@code TacoMain.run} fails with a {@link NullPointerException} before getting
 * here (it has already cleared the {@link DynalloyStage} whose compiler it asks for). Holds no
 * static state.
 */
public class JavaTraceStage implements ITacoStage {

	private SpecContext specContext;
	private AlloyAnalysisResult analysisResult;
	private boolean options_skolemize;
	private DynAlloySolution dynAlloySolution;
	
	/**
	 * Creates the stage.
	 *
	 * @param specContext the DynAlloy compiler's context of the translation
	 *        ({@code DynAlloyCompiler.getSpecContext()}), which maps Alloy back to DynAlloy
	 * @param analysisResult the SAT result of {@link AlloyStage}; its world and command are
	 *        read here
	 * @param optionsSkolemize passed to {@code DynAlloySolutionBuilder.buildSolution};
	 *        {@code TacoMain} passes {@code false}
	 */
	public JavaTraceStage(SpecContext specContext, AlloyAnalysisResult analysisResult, boolean optionsSkolemize) {
		super();
		this.specContext = specContext;
		this.analysisResult = analysisResult;
		this.options_skolemize = optionsSkolemize;
	}

	/**
	 * Builds the trace ({@link #getDynAlloySolution()}).
	 *
	 * @throws TacoException if dynalloy4 throws an Alloy {@code Err} or an
	 *         {@link IOException}
	 */
	@Override
	public void execute() {
		DynAlloySolutionBuilder dynAlloySolutionBuilder = new DynAlloySolutionBuilder(
																specContext, 
																analysisResult.getWorld(), 
																analysisResult.getCommand()
														);
		
		try {
			dynAlloySolution = dynAlloySolutionBuilder.buildSolution(analysisResult, options_skolemize);
		} catch (Err e) {
			throw new TacoException(e.getMessage());
		} catch (IOException e) {
			throw new TacoException(e.getMessage());
		}
	}

	/**
	 * Returns the trace built by the last {@link #execute()}.
	 *
	 * @return the trace, or {@code null} before {@link #execute()}
	 */
	public DynAlloySolution getDynAlloySolution() {
		return dynAlloySolution;
	}
}

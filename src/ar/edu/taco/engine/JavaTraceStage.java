package ar.edu.taco.engine;

import java.io.IOException;

import ar.edu.taco.TacoException;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;
import ar.uba.dc.rfm.dynalloy.trace.DynAlloySolution;
import ar.uba.dc.rfm.dynalloy.trace.DynAlloySolutionBuilder;
import ar.uba.dc.rfm.dynalloy.xlator.SpecContext;
import edu.mit.csail.sdg.alloy4.Err;

public class JavaTraceStage implements ITacoStage {

	private SpecContext specContext;
	private AlloyAnalysisResult analysisResult;
	private boolean options_skolemize;
	private DynAlloySolution dynAlloySolution;
	
	public JavaTraceStage(SpecContext specContext, AlloyAnalysisResult analysisResult, boolean optionsSkolemize) {
		super();
		this.specContext = specContext;
		this.analysisResult = analysisResult;
		this.options_skolemize = optionsSkolemize;
	}

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

	public DynAlloySolution getDynAlloySolution() {
		return dynAlloySolution;
	}
}

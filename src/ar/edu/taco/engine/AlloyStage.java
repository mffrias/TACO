package ar.edu.taco.engine;

import edu.mit.csail.sdg.alloy4.A4Reporter;

import java.util.HashSet;

import ar.edu.taco.TacoException;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisException;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalyzer;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class AlloyStage implements ITacoStage {

	private String alloy_filename;
	private AlloyAnalysisResult analysis_result;
	

	public AlloyStage(String alloy_filename) {
		this.alloy_filename = alloy_filename;
	}

	public AlloyAnalysisResult get_analysis_result() {
		return this.analysis_result;
	}

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

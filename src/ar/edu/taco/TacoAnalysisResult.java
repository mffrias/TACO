package ar.edu.taco;

import org.multijava.mjc.JCompilationUnitType;

import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;

public class TacoAnalysisResult {

	private AlloyAnalysisResult alloy_analysis_result;
	private JCompilationUnitType theCompilationUnit;

	public TacoAnalysisResult(AlloyAnalysisResult alloy_analysis_result) {
		this.alloy_analysis_result = alloy_analysis_result;
		theCompilationUnit = null;

	}

	public TacoAnalysisResult(AlloyAnalysisResult alloy_analysis_result, JCompilationUnitType jUnit) {
		this.alloy_analysis_result = alloy_analysis_result;
		this.theCompilationUnit = jUnit;
	}

	public TacoAnalysisResult() {
		// TODO Auto-generated constructor stub
	}

	public AlloyAnalysisResult get_alloy_analysis_result() {
		return alloy_analysis_result;
	}

	public JCompilationUnitType get_compilation_unit() {
		return theCompilationUnit;
	}

	public void setCompilationUnit(JCompilationUnitType cu) {
		this.theCompilationUnit = cu;
	}

	public void setAlloyAnalysisResult(AlloyAnalysisResult alloy_analysis_result) {
		this.alloy_analysis_result = alloy_analysis_result;

	}

}

package ar.edu.taco.simplejml.helpers;

import java.util.HashMap;
import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.xlator.JType;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class PackedListOfJDynAlloyModule_InvariantVarsAndPreds {
	
	private List<JDynAlloyModule> modules;
	private AlloyTyping varsRE;
	private List<AlloyFormula> predsRE;
	private AlloyTyping varsOI;
	private List<AlloyFormula> predsOI;


	public PackedListOfJDynAlloyModule_InvariantVarsAndPreds(List<JDynAlloyModule> ms, AlloyTyping vsOI, List<AlloyFormula> psOI){
		this.modules = ms;
		this.varsOI = vsOI;
		this.predsOI = psOI;
		
	}
	
	public List<JDynAlloyModule> getModules(){
		return modules;
	}
	
	public AlloyTyping getVars(){
		return varsOI;
	}

	public List<AlloyFormula> getPreds(){
		return predsOI;
	}
	
	
}

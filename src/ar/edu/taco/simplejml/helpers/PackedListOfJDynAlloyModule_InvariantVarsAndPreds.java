package ar.edu.taco.simplejml.helpers;

import java.util.HashMap;
import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.xlator.JType;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

/**
 * Holds the JDynAlloy modules of a compilation unit with the variables and predicates that
 * encode the arithmetic of their object invariants ({@code OI}); the fields for those of
 * {@code requires} and {@code ensures} ({@code RE}) are never set. Only commented-out code in
 * {@code JavaToJDynAlloyManager.processCompilationUnit} creates one: the class is unused.
 */
public class PackedListOfJDynAlloyModule_InvariantVarsAndPreds {
	
	private List<JDynAlloyModule> modules;
	private AlloyTyping varsRE;
	private List<AlloyFormula> predsRE;
	private AlloyTyping varsOI;
	private List<AlloyFormula> predsOI;


	/**
	 * Stores the three values as they are.
	 *
	 * @param ms the modules
	 * @param vsOI the variables of the object invariants' arithmetic, with their types
	 * @param psOI the predicates that define those variables
	 */
	public PackedListOfJDynAlloyModule_InvariantVarsAndPreds(List<JDynAlloyModule> ms, AlloyTyping vsOI, List<AlloyFormula> psOI){
		this.modules = ms;
		this.varsOI = vsOI;
		this.predsOI = psOI;
		
	}
	
	/**
	 * Returns the modules.
	 *
	 * @return the modules given to the constructor
	 */
	public List<JDynAlloyModule> getModules(){
		return modules;
	}
	
	/**
	 * Returns the variables of the object invariants' arithmetic.
	 *
	 * @return the typing given to the constructor
	 */
	public AlloyTyping getVars(){
		return varsOI;
	}

	/**
	 * Returns the predicates of the object invariants' arithmetic.
	 *
	 * @return the predicates given to the constructor
	 */
	public List<AlloyFormula> getPreds(){
		return predsOI;
	}
	
	
}

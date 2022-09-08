/**
 * 
 */
package ar.edu.taco.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.multijava.mjc.JCompilationUnitType;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JClassConstraint;
import ar.edu.jdynalloy.ast.JClassInvariant;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JObjectConstraint;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.ast.JSignature;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.JavaToJDynAlloyManager;
import ar.edu.taco.simplejml.SimpleJmlToJDynAlloyContext;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;
import ar.edu.taco.simplejml.helpers.PackedListOfJDynAlloyModule_InvariantVarsAndPreds;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;

/**
 * @author ggasser
 * 
 */
public class SimpleJmlStage implements ITacoStage {

	public static final String OUTPUT_JDYNALLOY_EXTENSION = ".djals";

	private List<JCompilationUnitType> compilation_units;
	private List<JDynAlloyModule> modules;
	private SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext = null;
	private AlloyTyping varsEncodingValueOfArithmeticOperationsInInvariants = new AlloyTyping();
	private List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInInvariants = new ArrayList<AlloyFormula>();
	
	
	
	public AlloyTyping getVarsEncodingValueOfArithmeticOperationsInInvariants(){
		return varsEncodingValueOfArithmeticOperationsInInvariants;
	}
	
	public List<AlloyFormula> getPredsEncodingValueOfArithmeticOperationsInInvariants(){
		return predsEncodingValueOfArithmeticOperationsInInvariants;
	}
	
	public SimpleJmlStage(List<JCompilationUnitType> compilation_units) {
		this.compilation_units = compilation_units;
		this.modules = new ArrayList<JDynAlloyModule>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ar.edu.taco.pipeline.DynJML4AlloyPipeline#execute()
	 */
	@Override
	public void execute() {
		// parse java modules
		JavaToJDynAlloyManager aJavaToDynJAlloyManager = new JavaToJDynAlloyManager(this.compilation_units);
		for (JCompilationUnitType unit : this.compilation_units) {
			List<JDynAlloyModule> result = aJavaToDynJAlloyManager.processCompilationUnit(unit);
			for (JDynAlloyModule aDynJAlloyModule : result) {
				this.modules.add(aDynJAlloyModule);
			}
		}

		// Build implementors for each Interface in modules
		Map<String, Set<String>> interfaceImplementor = new HashMap<String, Set<String>>();
		for (JDynAlloyModule aDynJAlloyModule : this.modules) {
			Set<String> interfaces = aDynJAlloyModule.getSignature().getSuperInterfaces();
			for (String aInterface : interfaces) {
				if (!interfaceImplementor.containsKey(aInterface)) {
					interfaceImplementor.put(aInterface, new HashSet<String>());
				}

				interfaceImplementor.get(aInterface).add(aDynJAlloyModule.getModuleId());
			}
		}

		for (String aInterface : interfaceImplementor.keySet()) {
			Set<String> implementors = interfaceImplementor.get(aInterface);

			AlloyExpression left = new ExprConstant(null, aInterface);

			StringBuffer sumImplementors = new StringBuffer();
			Iterator<String> itImplementors = implementors.iterator();
			while (itImplementors.hasNext()) {
				sumImplementors.append(itImplementors.next());
				if (itImplementors.hasNext()) {
					sumImplementors.append(" + ");
				}
			}

			AlloyExpression right = new ExprConstant(null, sumImplementors.toString());

			AlloyFormula fact = new EqualsFormula(left, right);

			JSignature interfaceSignatureId = JSignatureFactory.buildInterface(aInterface, Collections.<String> emptySet(), Collections.singleton(fact));
			JDynAlloyModule interfaceModule = new JDynAlloyModule(aInterface, interfaceSignatureId, null, null, Collections.<JField> emptyList(), Collections
					.<JClassInvariant> emptySet(), Collections.<JClassConstraint> emptySet(), Collections.<JObjectInvariant> emptySet(), Collections
					.<JObjectConstraint> emptySet(), Collections.<JRepresents> emptySet(), Collections.<JProgramDeclaration> emptySet(), 
					new AlloyTyping(), new ArrayList<AlloyFormula>(), false);

			this.modules.add(interfaceModule);

		}

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			// integer literals
			this.modules.addAll(JavaPrimitiveIntegerValue.getInstance().get_integer_literal_modules());

			// long literals
			this.modules.addAll(JavaPrimitiveLongValue.getInstance().get_long_literal_modules());

			// float literals
			this.modules.addAll(JavaPrimitiveFloatValue.getInstance().get_float_literal_modules());

			// char literals
			this.modules.addAll(JavaPrimitiveCharValue.getInstance().get_char_literal_modules());

		}

		simpleJmlToJDynAlloyContext = aJavaToDynJAlloyManager.getSimpleJmlToJDynAlloyContext();

	}

	public List<JDynAlloyModule> getModules() {
		return this.modules;
	}

	public SimpleJmlToJDynAlloyContext getSimpleJmlToJDynAlloyContext() {
		return this.simpleJmlToJDynAlloyContext;
	}

}

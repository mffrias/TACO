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

	/** Extension of the JDynAlloy files that {@link JDynAlloyPrinterStage} writes. */
	public static final String OUTPUT_JDYNALLOY_EXTENSION = ".djals";

	private List<JCompilationUnitType> compilation_units;
	private List<JDynAlloyModule> modules;
	private SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext = null;
	private AlloyTyping varsEncodingValueOfArithmeticOperationsInInvariants = new AlloyTyping();
	private List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInInvariants = new ArrayList<AlloyFormula>();
	private Object inputToFix = null;


	/**
	 * Returns a field that nothing fills: the variables from arithmetic constraints in
	 * invariants are kept per module ({@code JDynAlloyModule}) instead. No caller.
	 *
	 * @return an empty typing
	 */
	public AlloyTyping getVarsEncodingValueOfArithmeticOperationsInInvariants(){
		return varsEncodingValueOfArithmeticOperationsInInvariants;
	}
	
	/**
	 * Returns a field that nothing fills, like
	 * {@link #getVarsEncodingValueOfArithmeticOperationsInInvariants()}. No caller.
	 *
	 * @return an empty list
	 */
	public List<AlloyFormula> getPredsEncodingValueOfArithmeticOperationsInInvariants(){
		return predsEncodingValueOfArithmeticOperationsInInvariants;
	}
	
	/**
	 * Creates the stage that translates simplified Java + JML into JDynAlloy, the second
	 * after parsing in {@code TacoMain.run}.
	 *
	 * @param compilation_units the simplified units
	 *        ({@link JmlStage#get_simplified_compilation_units()})
	 */
	public SimpleJmlStage(List<JCompilationUnitType> compilation_units) {
		this.compilation_units = compilation_units;
		this.modules = new ArrayList<JDynAlloyModule>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ar.edu.taco.pipeline.DynJML4AlloyPipeline#execute()
	 */
	/**
	 * Translates the compilation units into JDynAlloy modules ({@link #getModules()}):
	 * <ol>
	 * <li>each unit with {@code JavaToJDynAlloyManager.processCompilationUnit}
	 *     ({@code JDynAlloyASTVisitor}), with the input to fix, if any: the modules of its
	 *     classes, with their fields, invariants and programs (named as in
	 *     docs/METHOD-NAMING.md);</li>
	 * <li>for each interface implemented by one of these modules, a module whose signature
	 *     has the fact {@code Interface = Impl1 + Impl2 + ...} over the implementing modules;</li>
	 * <li>with {@code useJavaArithmetic}, every integer, long, float and char literal module
	 *     that the {@code JavaPrimitive*Value} singletons (in {@code lib/jdynalloy.jar}) hold.
	 *     Those singletons
	 *     are never reset, so the list also holds the literals of earlier runs in the same
	 *     JVM.</li>
	 * </ol>
	 * Also keeps the translation's {@link SimpleJmlToJDynAlloyContext}.
	 */
	@Override
	public void execute() {
		// parse java modules
		JavaToJDynAlloyManager aJavaToDynJAlloyManager = new JavaToJDynAlloyManager(this.compilation_units, this.inputToFix);
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

	/**
	 * Returns the modules built by {@link #execute()}; {@code TacoMain} passes this list to
	 * the JFSL stage, which may add literal modules to it, then prints and translates it.
	 *
	 * @return the modules, in the order described in {@link #execute()}; empty before it
	 */
	public List<JDynAlloyModule> getModules() {
		return this.modules;
	}

	/**
	 * Returns the context of the translation, which {@code TacoMain} passes to the JFSL stage.
	 *
	 * @return the context of the last {@link #execute()}, or {@code null} before it
	 */
	public SimpleJmlToJDynAlloyContext getSimpleJmlToJDynAlloyContext() {
		return this.simpleJmlToJDynAlloyContext;
	}

	/**
	 * Sets the input to fix, passed to the translation ({@code JDynAlloyASTVisitor},
	 * {@code JmlExpressionVisitor}), which adds it as a precondition, to the spec cases that
	 * have a {@code requires}. {@code TacoMain.run} sets it only when computing the specified
	 * output ({@code isCheckAndRunSpecAssertionSupport}); Stryker's fixed inputs are applied by
	 * jDynAlloy instead. See docs/SPEC-OUTPUT.md ("inputToFix").
	 *
	 * @param inputToFix a {@code HashMap<String, Object>} from {@code "thiz"} and the
	 *        parameter names to their values, or {@code null}; call before {@link #execute()}
	 */
	public void setInputToFix(Object inputToFix) {
		this.inputToFix = inputToFix;
	}
}

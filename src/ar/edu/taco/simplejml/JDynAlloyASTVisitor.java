/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.simplejml;

import static ar.uba.dc.rfm.alloy.AlloyVariable.buildAlloyVariable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlCompilationUnit;
import org.jmlspecs.checker.JmlConstraint;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlInGroupClause;
import org.jmlspecs.checker.JmlInterfaceDeclaration;
import org.jmlspecs.checker.JmlInvariant;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlRepresentsDecl;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.jmlspecs.checker.JmlTypeDeclaration;
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CStdType;
import org.multijava.mjc.CVoidType;
import org.multijava.mjc.JCompilationUnit;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JFieldDeclarationType;
import org.multijava.mjc.JTypeDeclarationType;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.buffer.DynJMLAlloyModuleBuffer;
import ar.edu.jdynalloy.buffer.Represents;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.JmlBaseExpressionVisitor.Instant;
import ar.edu.taco.simplejml.JmlBaseExpressionVisitor.JmlClassDeclarationResult;
import ar.edu.taco.simplejml.JmlBaseExpressionVisitor.JmlRepresentsData;
import ar.edu.taco.simplejml.builtin.JObject;
//import ar.edu.taco.simplejml.helpers.ArithmeticExceptionSolver;
import ar.edu.taco.simplejml.helpers.CTypeAdapter;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;
import ar.edu.taco.simplejml.helpers.MethodDeclarationSolver;
import ar.edu.taco.simplejml.helpers.NullDerefSolver;
import ar.edu.taco.utils.jml.JmlAstTransverseStatementVisitor;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;

/**
 * Translates a simplified compilation unit into JDynAlloy modules, one per class or interface:
 * the core of {@code engine.SimpleJmlStage}, driven by
 * {@link JavaToJDynAlloyManager#processCompilationUnit}. For each type declaration it fills a new
 * {@code DynJMLAlloyModuleBuffer} with
 * <ul>
 * <li>the signature: its name ({@code helpers.JavaClassNameNormalizer}, dots as underscores), its
 *     parent ({@code java_lang_Object} if none) and interfaces, abstract or not;</li>
 * <li>the fields, static ones apart, and an invariant {@code thiz.f != null} for each non-static,
 *     non-model field declared {@code non_null};</li>
 * <li>the class's invariants, constraints, model fields and {@code represents} clauses,
 *     translated by a {@link JmlExpressionVisitor};</li>
 * <li>one program per method and constructor: its contract from
 *     {@code helpers.MethodDeclarationSolver} (named as docs/METHOD-NAMING.md says), and its body
 *     from a {@link BlockStatementsVisitor}, after
 *     {@code throw := null; var exit_stmt_reached; exit_stmt_reached := false}.</li>
 * </ul>
 * Inner classes are translated before the class around them. What the modules mean is in
 * jDynAlloy's docs/LANGUAGE.md.
 *
 * <p>The statement and expression translators ({@link BlockStatementsVisitor},
 * {@link ExpressionVisitor}, {@link JmlExpressionVisitor}) extend this class only to share its
 * fields; they are created with the constructor without arguments.
 *
 * <p>Static state: {@link #getCurrentModuleName()} and the types of the current module's fields
 * are static, shared by every visitor, and keep the last class translated between runs in one
 * JVM.
 */
public class JDynAlloyASTVisitor extends JmlAstTransverseStatementVisitor {

	private static Logger log = Logger.getLogger(JDynAlloyASTVisitor.class);

	protected static final String OBJECT_STATE_STRING = "objectState";
	protected static final String WIDLCARD_STRING = "*";
	private Object inputToFix = null;

	protected JavaAndJmlPrettyPrint2 prettyPrint = new JavaAndJmlPrettyPrint2();

	private List<JDynAlloyModule> modules = new ArrayList<JDynAlloyModule>();
	private Map<String, List<String>> modulesObjectState;
	private Map<String, List<String>> modulesNoStaticFields;
	private DynJMLAlloyModuleBuffer buffer = new DynJMLAlloyModuleBuffer();

	private final SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext;
	public AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
	public List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new ArrayList<AlloyFormula>();
	public AlloyTyping varsEncodingValueOfArithmeticOperationsInObjectInvariants = new AlloyTyping();
	public ArrayList<AlloyFormula> predsEncodingValueOfArithmeticOperationsInObjectInvariants = new ArrayList<AlloyFormula>();
	public AlloyTyping varsAndTheirTypeFromMathOperations = new AlloyTyping();
	public List<AlloyFormula> predsFromMathOperations = new ArrayList<AlloyFormula>();

	private List<JCompilationUnitType> compilationUnits;

	private static String currentModuleName;


	/**
	 * Creates a visitor with no context, no maps of fields and no compilation units. The
	 * statement and expression translators that extend this class use it; such a visitor cannot
	 * translate a type declaration (it fails on the missing maps).
	 */
	public JDynAlloyASTVisitor(){
		this.simpleJmlToJDynAlloyContext = null;
	}


	/**
	 * Sets the input to fix that every later method and constructor translation hands to
	 * {@code helpers.MethodDeclarationSolver}, which keeps it only for the method under analysis
	 * ({@code methodToCheck} contains the method's name) and passes it to
	 * {@link JmlExpressionVisitor}, where it becomes a {@code requires}. See docs/SPEC-OUTPUT.md.
	 *
	 * @param inputToFix a {@code HashMap<String, Object>} (receiver under {@code "thiz"}, one
	 *     entry per parameter), or {@code null}
	 */
	public void setInputToFix(Object inputToFix){
		this.inputToFix = inputToFix;
	}


	/**
	 * Returns the typing of the fresh variables that stand for the results of arithmetic
	 * operations in JML expressions translated with {@code useJavaArithmetic}:
	 * {@link JmlExpressionVisitor} replaces each operation by such a variable and adds a
	 * predicate that defines it
	 * ({@link #getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()}). Here they are
	 * those of {@code requires} and {@code ensures} clauses; the statement translators collect
	 * those of JML expressions in method bodies. After {@link #visitJmlMethodDeclaration} the
	 * field refers to the typing of the program just translated.
	 *
	 * @return the typing (not a copy)
	 */
	public AlloyTyping getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(){
		return this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}

	/**
	 * Returns the predicates that define the variables of
	 * {@link #getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()}. After
	 * {@link #visitJmlMethodDeclaration} the field refers to the list of the program just
	 * translated.
	 *
	 * @return the list (not a copy)
	 */
	public List<AlloyFormula> getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(){
		return this.predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}

	/**
	 * Returns the typing of the variables that encode arithmetic operations in the object
	 * invariants of the last class or interface translated: for each fresh variable {@code v} of
	 * an invariant, {@code v_0} (pre-state) and {@code v_1} (post-state), which are also added as
	 * fields of the class. Replaced by a new typing for each type declaration.
	 *
	 * @return the typing (not a copy)
	 */
	public AlloyTyping getVarsEncodingValueOfArithmeticOperationsInObjectInvariants(){
		return this.varsEncodingValueOfArithmeticOperationsInObjectInvariants;
	}

	/**
	 * Returns the predicates that define the variables of
	 * {@link #getVarsEncodingValueOfArithmeticOperationsInObjectInvariants()}, for the last class
	 * or interface translated.
	 *
	 * @return the list (not a copy)
	 */
	public List<AlloyFormula> getPredsEncodingValueOfArithmeticOperationsInObjectInvariants(){
		return this.predsEncodingValueOfArithmeticOperationsInObjectInvariants;
	}



	/**
	 * @param varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures the varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures to set
	 */
	public void setVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(
			AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures) {
		this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}

	/**
	 * @param predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures the predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures to set
	 */
	public void setPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(
			ArrayList<AlloyFormula> predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures) {
		this.predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}

	/**
	 * @param varsEncodingValueOfArithmeticOperationsInObjectInvariants the varsEncodingValueOfArithmeticOperationsInObjectInvariants to set
	 */
	public void setVarsEncodingValueOfArithmeticOperationsInObjectInvariants(
			AlloyTyping varsEncodingValueOfArithmeticOperationsInObjectInvariants) {
		this.varsEncodingValueOfArithmeticOperationsInObjectInvariants = varsEncodingValueOfArithmeticOperationsInObjectInvariants;
	}

	/**
	 * @param predsEncodingValueOfArithmeticOperationsInObjectInvariants the predsEncodingValueOfArithmeticOperationsInObjectInvariants to set
	 */
	public void setPredsEncodingValueOfArithmeticOperationsInObjectInvariants(
			ArrayList<AlloyFormula> predsEncodingValueOfArithmeticOperationsInObjectInvariants) {
		this.predsEncodingValueOfArithmeticOperationsInObjectInvariants = predsEncodingValueOfArithmeticOperationsInObjectInvariants;
	}



	private static class FieldTypes {
		private List<String> keys = new LinkedList<String>();
		private List<JType> values =new LinkedList<JType>();

		public JType get(String key) {
			if (keys.contains(key)) {
				int index = keys.indexOf(key);
				return values.get(index);
			}
			return null;
		}

		public void put(String field_name, JType type) {
			if (keys.contains(field_name)) {
				int index = keys.indexOf(field_name);
				values.remove(index);
				values.add(index, type);
			}
		}

	}

	private static FieldTypes currentModuleFieldsTypes;


	/**
	 * Creates the visitor of one compilation unit ({@link JavaToJDynAlloyManager}).
	 *
	 * @param modulesObjectState for each class translated so far (normalized name), its fields in
	 *     the data group {@code objectState}; this visitor adds an entry per type declaration,
	 *     and {@link JmlExpressionVisitor} reads it for {@code assignable} clauses
	 * @param modulesNoStaticFields for each class translated so far, its non-static fields; read
	 *     for {@code assignable x.*} and for the {@code modifies} of constructors
	 * @param simpleJmlToJDynAlloyContext where each declaration and its module or program are
	 *     recorded
	 * @param varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures the typing that the
	 *     first method translated adds to; see
	 *     {@link #getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()}
	 * @param predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures the list that the first
	 *     method translated adds to
	 * @param varsEncodingValueOfArithmeticOperationsInObjectInvariants ignored: the visitor keeps
	 *     its own typing
	 * @param predsEncodingValueOfArithmeticOperationsInObjectInvariants ignored: the visitor
	 *     keeps its own list
	 * @param compilationUnits every simplified compilation unit of the run, passed on to
	 *     {@code helpers.MethodDeclarationSolver}
	 */
	public JDynAlloyASTVisitor(Map<String, List<String>> modulesObjectState, 
			Map<String, List<String>> modulesNoStaticFields,
			SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext, 
			AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures, 
			List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures,
			AlloyTyping varsEncodingValueOfArithmeticOperationsInObjectInvariants,
			List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInObjectInvariants, 
			List<JCompilationUnitType> compilationUnits) {

		this.modulesObjectState = modulesObjectState;
		this.modulesNoStaticFields = modulesNoStaticFields;
		this.simpleJmlToJDynAlloyContext = simpleJmlToJDynAlloyContext;
		this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
		this.predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
		this.compilationUnits = compilationUnits;
	}

	/**
	 * Returns the modules translated so far, one per class or interface, an inner class before
	 * the class around it.
	 *
	 * @return the visitor's own list (not a copy)
	 */
	public List<JDynAlloyModule> getModules() {
		return this.modules;
	}

	/**
	 * Returns the normalized name ({@code pkg_Class}) of the class or interface being translated,
	 * set when its translation starts, after its inner classes. Static: shared by all visitors
	 * and kept between runs in one JVM. {@code helpers.MethodDeclarationSolver} reads it to build
	 * the {@code modifies} of a constructor.
	 *
	 * @return the name, or {@code null} if no type declaration was translated yet in this JVM
	 */
	public static String getCurrentModuleName() {
		return currentModuleName;
	}

	/**
	 * Returns the recorded type of a field of the class being translated. The field types are
	 * static, like {@link #getCurrentModuleName()}. As written it always returns {@code null}:
	 * the private map only replaces the types of names it already has, and nothing adds names.
	 * Nothing calls it.
	 *
	 * @param varName the field's name
	 * @return {@code null}
	 */
	public static JType getVariableTypeOnCurrentModule(String varName) {
		return currentModuleFieldsTypes.get(varName);
	}

	/**
	 * @return the buffer
	 */
	protected DynJMLAlloyModuleBuffer getBuffer() {
		return buffer;
	}

	/**
	 * @param buffer
	 *            the buffer to set
	 */
	public void setBuffer(DynJMLAlloyModuleBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * @return the modulesObjectState
	 */
	public Map<String, List<String>> getModulesObjectState() {
		return modulesObjectState;
	}

	/**
	 * @param modulesObjectState
	 *            the modulesObjectState to set
	 */
	public void setModulesObjectState(Map<String, List<String>> modulesObjectState) {
		this.modulesObjectState = modulesObjectState;
	}

	/**
	 * @return the modulesNoStaticFields
	 */
	public Map<String, List<String>> getModulesNoStaticFields() {
		return modulesNoStaticFields;
	}

	/**
	 * @param modulesNoStaticFields
	 *            the modulesNoStaticFields to set
	 */
	public void setModulesNoStaticFields(Map<String, List<String>> modulesNoStaticFields) {
		this.modulesNoStaticFields = modulesNoStaticFields;
	}

	/**
	 * Translates a constructor into a program of the current module: its contract from
	 * {@code MethodDeclarationSolver.getConstructorDeclaration} (with the input to fix, see
	 * {@link #setInputToFix}), its body from a new {@link BlockStatementsVisitor}, after
	 * {@code throw := null; var exit_stmt_reached; exit_stmt_reached := false}. With
	 * {@code checkNullDereference} the body goes through {@code helpers.NullDerefSolver}
	 * ({@link NullDerefVisitor}). The program is marked as a constructor and added to the buffer;
	 * the declaration and the program are recorded in the context.
	 *
	 * <p>Unlike {@link #visitJmlMethodDeclaration}, the body's translator does not get the
	 * {@code useJavaArithmetic} setting (its program buffer uses it only for {@code do} loops),
	 * and the arithmetic encodings of JML expressions in the body are not collected.
	 *
	 * @param jmlConstructorDeclaration the constructor, with a body
	 */
	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration jmlConstructorDeclaration) {
		jmlConstructorDeclaration.accept(prettyPrint);
		log.debug("Visiting: " + jmlConstructorDeclaration.getClass().getName());
		log.debug("Constructor: \n" + this.prettyPrint.getPrettyPrint());

//		this.varsEncodingValueOfArithmeticOperationsInObjectInvariants = new AlloyTyping();
//		this.predsEncodingValueOfArithmeticOperationsInObjectInvariants = new ArrayList<AlloyFormula>();
		JProgramDeclaration programDeclaration = MethodDeclarationSolver.getConstructorDeclaration(jmlConstructorDeclaration, this.buffer, 
				this.modulesObjectState, this.modulesNoStaticFields, this.varsEncodingValueOfArithmeticOperationsInObjectInvariants, 
				this.predsEncodingValueOfArithmeticOperationsInObjectInvariants, this.compilationUnits, inputToFix);

		
//		JProgramDeclaration programDeclaration = MethodDeclarationSolver.getConstructorDeclaration(jmlConstructorDeclaration, this.buffer,
//				this.modulesObjectState, this.modulesNoStaticFields, this.compilationUnits);

		JStatement initializeThrow = JDynAlloyFactory.initializeThrow();
		// Declare exit statement used to indicate that a Throw or Return
		// statement was reached in the code.
		CTypeAdapter cTypeAdapter = new CTypeAdapter();
		JType variableType = cTypeAdapter.translate(CStdType.Boolean);
		JStatement exitStmtReachedDeclaration = new JVariableDeclaration(JExpressionFactory.EXIT_REACHED_VARIABLE, variableType);
		JStatement exitStmtReachedExpression = JDynAlloyFactory.initializeExitStatementReached();

		BlockStatementsVisitor blockScopeTranslator = new BlockStatementsVisitor();
		jmlConstructorDeclaration.body().accept(blockScopeTranslator);
		JStatement programBody = blockScopeTranslator.getJAlloyProgram();
		JStatement program = JDynAlloyFactory.block(initializeThrow, exitStmtReachedDeclaration, exitStmtReachedExpression, programBody);

		if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.CHECK_NULL_DE_REFERENCE_FIELD) == true) {
			List<JStatement> nullDerefHeader = NullDerefSolver.buildNullDerefHeader();
			JStatement nullDeRefBody = NullDerefSolver.buildNullDerefBody(program);
			JStatement nullDeRefTail = NullDerefSolver.buildNullDerefTail();

			List<JStatement> stmtList = new LinkedList<JStatement>();
			stmtList.addAll(nullDerefHeader);
			stmtList.add(nullDeRefBody);
			stmtList.add(nullDeRefTail);

			program = new JBlock(stmtList);
//			programDeclaration.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
//			for (AlloyVariable av : this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()){
//				programDeclaration.getVarsResultOfArithmeticOperationsInRequiresAndEnsures().put(av, this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av));
//			}

		}

		programDeclaration = new JProgramDeclaration(
				programDeclaration.isAbstract(), 
				true,
				programDeclaration.isPure(),
				programDeclaration.getSignatureId(), 
				programDeclaration.getProgramId(),
				programDeclaration.getParameters(), 
				programDeclaration.getSpecCases(), 
				program, 
				programDeclaration.getVarsResultOfArithmeticOperationsInRequiresAndEnsures(), 
				programDeclaration.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());

		buffer.getPrograms().add(programDeclaration);

		this.simpleJmlToJDynAlloyContext.record_simpleJml_to_JDynAlloy_mapping(jmlConstructorDeclaration, programDeclaration);
	}

	/**
	 * Translates a class into a module: its inner classes first, each into its own module, then
	 * its signature, fields, class-level JML and methods (see the class comment). The variables
	 * that encode arithmetic in its programs' contracts are added as fields of the class. Adds
	 * the module to {@link #getModules()} and records it in the context.
	 *
	 * @param jmlClassDeclaration the class
	 */
	@Override
	public void visitJmlClassDeclaration(
			/* @non_null */JmlClassDeclaration jmlClassDeclaration) {
		jmlClassDeclaration.accept(prettyPrint);  //mfrias-mffrias-24-09-2012-Este puede ser el lugar para eliminar comas.
		log.debug("Visiting: " + jmlClassDeclaration.getClass().getName());
		log.debug("Class: \n" + this.prettyPrint.getPrettyPrint());

		translateTypeDeclaration(jmlClassDeclaration);
		for (JProgramDeclaration prog : this.buffer.getPrograms()){
			for (AlloyVariable av : prog.getVarsResultOfArithmeticOperationsInRequiresAndEnsures().varSet()){
				JType theVarType = new JType(prog.getVarsResultOfArithmeticOperationsInRequiresAndEnsures().get(av));
				this.buffer.getFields().put(av, theVarType);
			}
		}

		JDynAlloyModule module = this.buffer.getModule();
		this.modules.add(module);
		this.simpleJmlToJDynAlloyContext.record_simpleJml_to_JDynAlloy_mapping(jmlClassDeclaration, module);
	}



	/**
	 * Adds a field to the current module's buffer, as a static field or a field, with its type
	 * translated by {@code helpers.CTypeAdapter}, and to the data groups its {@code in} clauses
	 * name. Also called for model fields.
	 *
	 * @param jmlFieldDeclaration the field
	 */
	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration jmlFieldDeclaration) {
		jmlFieldDeclaration.accept(prettyPrint);
		log.debug("Visiting: " + jmlFieldDeclaration.getClass().getName());
		log.debug("Field: " + this.prettyPrint.getPrettyPrint());

		// Create an AlloyVariable from variable name
		AlloyVariable alloyVariable = buildAlloyVariable(jmlFieldDeclaration.ident());

		// extract the variable type and convert it to and Alloy variable type.
		CTypeAdapter cTypeAdapter = new CTypeAdapter();
		JType variableType = cTypeAdapter.translate(jmlFieldDeclaration.getType());

		// if (jmlFieldDeclaration.getField().isDeclaredNonNull()) {
		// variableType.setAsNonNull();
		// }

		JDynAlloyASTVisitor.currentModuleFieldsTypes.put(alloyVariable.getVariableId().getString(), variableType);

		if (jmlFieldDeclaration.getField().isFieldStatic()) {
			this.buffer.getStaticFields().put(alloyVariable, variableType);
		} else {
			this.buffer.getFields().put(alloyVariable, variableType);
		}

		// Here we are going to assign each variable into its corresponding
		// groupClause
		for (JmlInGroupClause jmlInGroupClause : jmlFieldDeclaration.getCombinedInGroupClauses()) {
			for (JmlStoreRefExpression jmlStoreRefExpression : jmlInGroupClause.groupList()) {
				String groupName = jmlStoreRefExpression.getName();
				if (!this.buffer.getInGroupClauses().containsKey(groupName)) {
					this.buffer.getInGroupClauses().put(groupName, new ArrayList<AlloyVariable>());
				}
				this.buffer.getInGroupClauses().get(groupName).add(alloyVariable);
			}
		}
	}



	/**
	 * Translates an interface into a module, as {@link #visitJmlClassDeclaration} does a class
	 * (its programs are those of the methods it declares, without bodies), adds it to
	 * {@link #getModules()} and records it in the context.
	 *
	 * @param jmlInterfaceDeclaration the interface
	 */
	@Override
	public void visitJmlInterfaceDeclaration(JmlInterfaceDeclaration jmlInterfaceDeclaration) {
		jmlInterfaceDeclaration.accept(prettyPrint);
		log.debug("Visiting: " + jmlInterfaceDeclaration.getClass().getName());
		log.debug("Class: \n" + this.prettyPrint.getPrettyPrint());

		translateTypeDeclaration(jmlInterfaceDeclaration);

		JDynAlloyModule module = this.buffer.getModule();
		this.modules.add(module);

		this.simpleJmlToJDynAlloyContext.record_simpleJml_to_JDynAlloy_mapping(jmlInterfaceDeclaration, module);
	}



	/**
	 * Translates a method into a program of the current module: its contract from
	 * {@code MethodDeclarationSolver.getMethodDeclaration} (with the input to fix, see
	 * {@link #setInputToFix}), named {@code <declaring class>_<method>} (docs/METHOD-NAMING.md);
	 * its body, if it has one ({@code skip} otherwise), from a new
	 * {@link BlockStatementsVisitor}, after
	 * {@code throw := null; var exit_stmt_reached; exit_stmt_reached := false}. With
	 * {@code checkNullDereference} the body goes through {@code helpers.NullDerefSolver}. The
	 * program is added to the buffer, and the declaration and the program are recorded in the
	 * context.
	 *
	 * <p>Side effect on the arithmetic encodings: the body's are added to the typing and list
	 * this visitor holds, and then those fields are replaced by the new program's own (so the
	 * next method's body encodings are added to this program's).
	 *
	 * @param jmlMethodDeclaration the method
	 */
	@Override
	public void visitJmlMethodDeclaration(
			/* @non_null */JmlMethodDeclaration jmlMethodDeclaration) {

		
//		this.varsEncodingValueOfArithmeticOperationsInObjectInvariants = new AlloyTyping();
//		this.predsEncodingValueOfArithmeticOperationsInObjectInvariants = new ArrayList<AlloyFormula>();
		JProgramDeclaration programDeclaration = MethodDeclarationSolver.getMethodDeclaration(jmlMethodDeclaration, this.buffer, this.modulesObjectState,
				this.modulesNoStaticFields, this.varsEncodingValueOfArithmeticOperationsInObjectInvariants, 
				this.predsEncodingValueOfArithmeticOperationsInObjectInvariants, this.compilationUnits, inputToFix);

		JStatement initializeThrow = JDynAlloyFactory.initializeThrow();

		// Declare exit statement used to indicate that a Throw or Return
		// statement was reached in the code.
		CTypeAdapter cTypeAdapter = new CTypeAdapter();
		JType variableType = cTypeAdapter.translate(CStdType.Boolean);
		JStatement exitStmtReachedDeclaration = new JVariableDeclaration(JExpressionFactory.EXIT_REACHED_VARIABLE, variableType);
		JStatement exitStmtReachedExpression = JDynAlloyFactory.initializeExitStatementReached();

		// Parse method body, if exists.
		JStatement programBody = new JSkip();
		if (jmlMethodDeclaration.body() != null) {
			/////////////////////
			BlockStatementsVisitor blockScopeTranslator = new BlockStatementsVisitor();
			blockScopeTranslator.programBuffer.setJavaArithmetic(TacoConfigurator.getInstance().getUseJavaArithmetic());
//			for (AlloyVariable av : programDeclaration.getVarsResultOfArithmeticOperationsInRequiresAndEnsures().getVarsInTyping()){
//				blockScopeTranslator.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(av, programDeclaration.getVarsResultOfArithmeticOperationsInRequiresAndEnsures().get(av));
//			}
//			blockScopeTranslator.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(programDeclaration.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
			blockScopeTranslator.methodReturnValue = !(jmlMethodDeclaration.returnType() instanceof CVoidType);
			jmlMethodDeclaration.body().accept(blockScopeTranslator);
			this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(blockScopeTranslator.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
			for (AlloyVariable av : blockScopeTranslator.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().getVarsInTyping())
				this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(av, blockScopeTranslator.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av));
			programBody = blockScopeTranslator.getJAlloyProgram();
		}

		JStatement program = null;
		program = JDynAlloyFactory.block(initializeThrow, exitStmtReachedDeclaration, exitStmtReachedExpression, programBody);

		if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.CHECK_NULL_DE_REFERENCE_FIELD) == true) {

			List<JStatement> nullDerefHeader = NullDerefSolver.buildNullDerefHeader();

			JStatement nullDeRefBody = NullDerefSolver.buildNullDerefBody(program);
			JStatement nullDeRefTail = NullDerefSolver.buildNullDerefTail();

			List<JStatement> stmtList = new LinkedList<JStatement>();
			stmtList.addAll(nullDerefHeader);
			stmtList.add(nullDeRefBody);
			stmtList.add(nullDeRefTail);

			program = new JBlock(stmtList);
		}


		programDeclaration = new JProgramDeclaration(
				programDeclaration.isAbstract(), 
				false,
				programDeclaration.isPure(),
				programDeclaration.getSignatureId(), 
				programDeclaration.getProgramId(),
				programDeclaration.getParameters(), 
				programDeclaration.getSpecCases(), 
				program,
				programDeclaration.varsResultOfArithmeticOperationsInRequiresAndEnsures,
				programDeclaration.predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures
				);

		buffer.getPrograms().add(programDeclaration);

		this.simpleJmlToJDynAlloyContext.record_simpleJml_to_JDynAlloy_mapping(jmlMethodDeclaration, programDeclaration);
		this.predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = programDeclaration.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures();
		this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = programDeclaration.getVarsResultOfArithmeticOperationsInRequiresAndEnsures();

	}

	@SuppressWarnings("unchecked")
	private void translateTypeDeclaration(JmlTypeDeclaration jmlTypeDeclaration) {

		// Transverse Inner Classes
		if (jmlTypeDeclaration.inners() != null) {
			for (JmlClassDeclaration innerClassDeclaration : (ArrayList<JmlClassDeclaration>) jmlTypeDeclaration.inners()) {
				innerClassDeclaration.accept(this);

			}
		}

		buffer = new DynJMLAlloyModuleBuffer();

		JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(jmlTypeDeclaration.getCClass().getJavaName());
		JDynAlloyASTVisitor.currentModuleName = classNameNormalizer.getQualifiedClassName();

		// process supper interfaces
		for (CClassType superInterface : jmlTypeDeclaration.getCClass().getInterfaces()) {
			JavaClassNameNormalizer interfaceNameNormalizer = new JavaClassNameNormalizer(superInterface.getCClass().getJavaName());
			buffer.getSuperInterfaces().add(interfaceNameNormalizer.getQualifiedClassName());
		}

		buffer.setInterface(jmlTypeDeclaration.getCClass().isInterface());

		if (jmlTypeDeclaration.getCClass().getSuperType() == null) {
			buffer.setSuperClassSignatureId(JObject.getInstance().getModule().getSignature().getSignatureId());
		} else {
			JavaClassNameNormalizer classNameNormalizer2 = new JavaClassNameNormalizer(jmlTypeDeclaration.getCClass().getSuperType().getCClass().getJavaName());
			buffer.setSuperClassSignatureId(classNameNormalizer2.getQualifiedClassName());
		}

		String name = classNameNormalizer.getQualifiedClassName();
		buffer.setSignatureId(name);
		buffer.setAbstract(jmlTypeDeclaration.getCClass().isAbstract());
		buffer.setSignatureId(name);
		buffer.setThisType(new JType(name));

		JDynAlloyASTVisitor.currentModuleFieldsTypes = new FieldTypes();

		// Transverse Fields
		if (jmlTypeDeclaration.fields() != null) {
			List<AlloyFormula> nullityFieldsInvariants = new ArrayList<AlloyFormula>();

			List<String> modelFieldList = extractFieldsName(jmlTypeDeclaration.getModelFields());
			List<String> objectStateFieldsNames = new ArrayList<String>();
			List<String> noStaticFieldsNames = new ArrayList<String>();
			for (JFieldDeclarationType jFieldDeclarationType : jmlTypeDeclaration.fields()) {
				jFieldDeclarationType.accept(this);

				boolean isModelField = modelFieldList.contains(jFieldDeclarationType.ident());

				if (jFieldDeclarationType.getField().isDeclaredNonNull() && !jFieldDeclarationType.getField().isStatic() && !isModelField) {
					AlloyExpression varExpression = new ExprVariable(new AlloyVariable(jFieldDeclarationType.ident()));
					varExpression = new ExprJoin(JExpressionFactory.THIS_EXPRESSION, varExpression);
					AlloyFormula nullityFormula = new NotFormula(new EqualsFormula(varExpression, JExpressionFactory.NULL_EXPRESSION));
					nullityFieldsInvariants.add(nullityFormula);
				}

				if (jFieldDeclarationType instanceof JmlFieldDeclaration) {
					JmlFieldDeclaration jmlFieldDeclaration = (JmlFieldDeclaration) jFieldDeclarationType;

					if (!jmlFieldDeclaration.getField().isStatic()) {
						noStaticFieldsNames.add(jFieldDeclarationType.ident());
					}

					for (int x = 0; x < jmlFieldDeclaration.getCombinedInGroupClauses().length; x++) {
						if (jmlFieldDeclaration.getCombinedInGroupClauses()[x].equals(OBJECT_STATE_STRING)) {
							objectStateFieldsNames.add(jFieldDeclarationType.ident());
						} else {
							for (JmlStoreRefExpression jmlStoreRefExpression : jmlFieldDeclaration.getCombinedInGroupClauses()[x].groupList()) {
								if (jmlStoreRefExpression.getName().equals(OBJECT_STATE_STRING)) {
									objectStateFieldsNames.add(jFieldDeclarationType.ident());
								}
							}
						}
					}
				}
			}

			this.modulesObjectState.put(name, objectStateFieldsNames);
			this.modulesNoStaticFields.put(name, noStaticFieldsNames);
			if (nullityFieldsInvariants.size() > 0) {
				buffer.getInvariants().addAll(nullityFieldsInvariants);
			}
		}

		// Traverse JML annotations
		translateJmlAnnotations(jmlTypeDeclaration); 


		// Traverse Methods
		for (JmlMethodDeclaration methodDeclaration : (ArrayList<JmlMethodDeclaration>) jmlTypeDeclaration.methods()) {
			methodDeclaration.accept(this);
		}
	}

	private List<String> extractFieldsName(JFieldDeclarationType[] modelFields) {
		List<String> retValues = new ArrayList<String>();
		for (JFieldDeclarationType fieldDeclarationType : modelFields) {
			retValues.add(fieldDeclarationType.ident());
		}
		return retValues;
	}

	private void translateJmlAnnotations(JmlTypeDeclaration jmlTypeDeclaration) {

		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(Instant.PRE_INSTANT);
		jmlExpressionVisitor.setClassSpecification(true);

		if (jmlTypeDeclaration.getModelFields() != null) {
			for (JFieldDeclarationType jFieldDeclarationType : jmlTypeDeclaration.getModelFields()) {
				jFieldDeclarationType.accept(this);
			}
		}

		// TODO: aca hay que parsear la definicion de metodos del modelo
		// (metodos definidos en JML)

		this.varsEncodingValueOfArithmeticOperationsInObjectInvariants = new AlloyTyping();
		this.predsEncodingValueOfArithmeticOperationsInObjectInvariants = new ArrayList<AlloyFormula>();
		jmlExpressionVisitor.setContractTranslation(true); //mfrias4-place to fix so that variables and operations are saved when traversing object invariant
		if (jmlTypeDeclaration.invariants() != null) {
			for (JmlInvariant jmlInvariant : jmlTypeDeclaration.invariants()) {

				jmlExpressionVisitor.setVarsAndTheirTypeFromMathOperations(new AlloyTyping());
				jmlExpressionVisitor.setPredsFromMathOperations(new ArrayList<AlloyFormula>());
				jmlExpressionVisitor.setInstant(Instant.PRE_INSTANT);
				jmlInvariant.accept(jmlExpressionVisitor);


				//I will add the newly created variables as class fields. This will prevent semantic errors down the road.
				for (AlloyVariable av : jmlExpressionVisitor.getVarsAndTheirTypeFromMathOperations().varSet()){
					AlloyVariable av_pre = new AlloyVariable(av.getVariableId().getString()+"_0",false);
					av_pre.setIsVariableFromContract();
					av.setIsVariableFromContract();
					buffer.getFields().put(av_pre, new JType(jmlExpressionVisitor.getVarsAndTheirTypeFromMathOperations().get(av)));
					this.varsEncodingValueOfArithmeticOperationsInObjectInvariants.put(av_pre, jmlExpressionVisitor.getVarsAndTheirTypeFromMathOperations().get(av));

					AlloyVariable av_pos = new AlloyVariable(av.getVariableId().getString()+"_1",true);
					av_pos.setIsVariableFromContract();
					buffer.getFields().put(av_pos, new JType(jmlExpressionVisitor.getVarsAndTheirTypeFromMathOperations().get(av)));
					this.varsEncodingValueOfArithmeticOperationsInObjectInvariants.put(av_pos, jmlExpressionVisitor.getVarsAndTheirTypeFromMathOperations().get(av));
				}

				this.predsEncodingValueOfArithmeticOperationsInObjectInvariants.addAll(jmlExpressionVisitor.getPredsFromMathOperations());
			}
		}


		if (jmlTypeDeclaration.constraints() != null) {
			for (JmlConstraint jmlConstraint : jmlTypeDeclaration.constraints()) {
				jmlConstraint.accept(jmlExpressionVisitor);
			}
		}

		jmlExpressionVisitor.setContractTranslation(false);



		if (jmlTypeDeclaration.representsDecls() != null) {
			for (JmlRepresentsDecl jmlRepresentsDecl : jmlTypeDeclaration.representsDecls()) {
				jmlRepresentsDecl.accept(jmlExpressionVisitor);
			}
		}

		JmlClassDeclarationResult jmlDeclarationResult = jmlExpressionVisitor.getJmlClassDeclarationResult();

		for (Iterator<JDynAlloyTyping.Entry> it = jmlDeclarationResult.modelFields(); it.hasNext();) {
			JDynAlloyTyping.Entry modelField = it.next();
			buffer.getFields().put(modelField.getVariable(), modelField.getType());
		}

		for (Iterator<JDynAlloyTyping.Entry> it = jmlDeclarationResult.staticModelFields(); it.hasNext();) {
			JDynAlloyTyping.Entry staticModelField = it.next();
			buffer.getFields().put(staticModelField.getVariable(), staticModelField.getType());
		}

		for (Iterator<AlloyFormula> it = jmlDeclarationResult.invariants(); it.hasNext();) {
			buffer.getInvariants().add(it.next());
		}

		for (Iterator<AlloyFormula> it = jmlDeclarationResult.staticInvariants(); it.hasNext();) {
			buffer.getStaticInvariants().add(it.next());
		}

		for (Iterator<AlloyFormula> it = jmlDeclarationResult.constraints(); it.hasNext();) {
			buffer.getConstraints().add(it.next());
		}

		for (Iterator<AlloyFormula> it = jmlDeclarationResult.staticConstraints(); it.hasNext();) {
			buffer.getStaticConstraints().add(it.next());
		}

		for (Iterator<JmlRepresentsData> it = jmlDeclarationResult.represents(); it.hasNext();) {
			JmlRepresentsData entry = it.next();
			buffer.getRepresents().add(new Represents(entry.expression, entry.expressionType, entry.formula));
		}
	}

	private void visitCompilationUnitType(JCompilationUnitType n) {
		JTypeDeclarationType[] typeDeclarations = n.typeDeclarations();
		for (int i = 0; i < typeDeclarations.length; i++) {
			JTypeDeclarationType jTypeDeclarationType = typeDeclarations[i];
			jTypeDeclarationType.accept(this);
			this.modules.get(i).setVarsEncodingValueOfArithmeticOperationsInObjectInvariants(varsEncodingValueOfArithmeticOperationsInObjectInvariants);
			this.modules.get(i).setPredsEncodingValueOfArithmeticOperationsInObjectInvariants(predsEncodingValueOfArithmeticOperationsInObjectInvariants);
		}
	}

	/**
	 * Translates every type declaration of a MultiJava compilation unit; see
	 * {@link #visitJmlCompilationUnit}.
	 *
	 * @param n the compilation unit
	 */
	@Override
	public void visitCompilationUnit(JCompilationUnit n) {
		visitCompilationUnitType(n);
	}

	/**
	 * Returns a shallow copy ({@code Object.clone}; MultiJava's {@code Utils} is
	 * {@code Cloneable}), which shares the buffer, the modules and the maps. Nothing calls it.
	 *
	 * @return the copy
	 * @throws CloneNotSupportedException never, as the class is {@code Cloneable}
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	/**
	 * Translates every type declaration of the unit into modules. After each one, the module with
	 * the same index in {@link #getModules()} gets the encodings of arithmetic in object
	 * invariants of the last class translated. Modules and type declarations are paired by index,
	 * which holds only for a unit without inner classes, whose modules come first.
	 *
	 * @param n the compilation unit
	 */
	@Override
	public void visitJmlCompilationUnit(JmlCompilationUnit n) {
		visitCompilationUnitType(n);
	}

}

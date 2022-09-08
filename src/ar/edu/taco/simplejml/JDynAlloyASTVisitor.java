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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
import ar.edu.taco.TacoException;
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
import ar.uba.dc.rfm.alloy.util.VarSubstitutor;

public class JDynAlloyASTVisitor extends JmlAstTransverseStatementVisitor {

	private static Logger log = Logger.getLogger(JDynAlloyASTVisitor.class);

	protected static final String OBJECT_STATE_STRING = "objectState";
	protected static final String WIDLCARD_STRING = "*";

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


	public JDynAlloyASTVisitor(){
		this.simpleJmlToJDynAlloyContext = null;
	}


	public AlloyTyping getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(){
		return this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}

	public List<AlloyFormula> getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(){
		return this.predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}

	public AlloyTyping getVarsEncodingValueOfArithmeticOperationsInObjectInvariants(){
		return this.varsEncodingValueOfArithmeticOperationsInObjectInvariants;
	}

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

	public List<JDynAlloyModule> getModules() {
		return this.modules;
	}

	public static String getCurrentModuleName() {
		return currentModuleName;
	}

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

	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration jmlConstructorDeclaration) {
		jmlConstructorDeclaration.accept(prettyPrint);
		log.debug("Visiting: " + jmlConstructorDeclaration.getClass().getName());
		log.debug("Constructor: \n" + this.prettyPrint.getPrettyPrint());

		this.varsEncodingValueOfArithmeticOperationsInObjectInvariants = new AlloyTyping();
		this.predsEncodingValueOfArithmeticOperationsInObjectInvariants = new ArrayList<AlloyFormula>();
		JProgramDeclaration programDeclaration = MethodDeclarationSolver.getConstructorDeclaration(jmlConstructorDeclaration, this.buffer, 
				this.modulesObjectState, this.modulesNoStaticFields, this.varsEncodingValueOfArithmeticOperationsInObjectInvariants, 
				this.predsEncodingValueOfArithmeticOperationsInObjectInvariants, this.compilationUnits);

		
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
			programDeclaration.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
			for (AlloyVariable av : this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()){
				programDeclaration.getVarsResultOfArithmeticOperationsInRequiresAndEnsures().put(av, this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av));
			}

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



	@Override
	public void visitJmlMethodDeclaration(
			/* @non_null */JmlMethodDeclaration jmlMethodDeclaration) {

		
		this.varsEncodingValueOfArithmeticOperationsInObjectInvariants = new AlloyTyping();
		this.predsEncodingValueOfArithmeticOperationsInObjectInvariants = new ArrayList<AlloyFormula>();
		JProgramDeclaration programDeclaration = MethodDeclarationSolver.getMethodDeclaration(jmlMethodDeclaration, this.buffer, this.modulesObjectState,
				this.modulesNoStaticFields, this.varsEncodingValueOfArithmeticOperationsInObjectInvariants, 
				this.predsEncodingValueOfArithmeticOperationsInObjectInvariants, this.compilationUnits);

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
		jmlExpressionVisitor.setContractTranslation(true);
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

	@Override
	public void visitCompilationUnit(JCompilationUnit n) {
		visitCompilationUnitType(n);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public void visitJmlCompilationUnit(JmlCompilationUnit n) {
		visitCompilationUnitType(n);
	}

}

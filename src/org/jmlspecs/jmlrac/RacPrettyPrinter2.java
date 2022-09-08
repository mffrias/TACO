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
package org.jmlspecs.jmlrac;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.jmlspecs.checker.*;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CField;
import org.multijava.mjc.CStdType;
import org.multijava.mjc.CType;
import org.multijava.mjc.Debug;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JBinaryExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JCharLiteral;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JClassOrGFImportType;
import org.multijava.mjc.JCompilationUnit;
import org.multijava.mjc.JCompoundAssignmentExpression;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JPackageImportType;
import org.multijava.mjc.JPackageName;
import org.multijava.mjc.JPhylum;
import org.multijava.mjc.JPostfixExpression;
import org.multijava.mjc.JPrefixExpression;
import org.multijava.mjc.JShiftExpression;
import org.multijava.mjc.JTypeDeclarationType;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.MjcPrettyPrinter;
import org.multijava.util.compiler.TabbedPrintWriter;

public class RacPrettyPrinter2 extends MjcPrettyPrinter implements RacVisitor {

	public static final String NEW_LINE;
	static {
		String nl = System.getProperty("line.separator");
		NEW_LINE = (nl == null) ? "\n" : nl;
	}

	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Constructs a pretty printer object for JML specifications
	 * 
	 * @param writer
	 *            the object to which java code be written
	 * @param modUtil
	 *            the modifier utility to print Java and JML modifiers
	 */
	public RacPrettyPrinter2(java.io.Writer writer, JmlModifier modUtil) {
		super(writer, modUtil);
		jmlModUtil = modUtil;
	}

	/**
	 * Constructs a pretty printer object for JML specifications
	 * 
	 * @param fileName
	 *            the file into which the code is to be printed
	 * @param modUtil
	 *            the modifier utility to print Java and JML modifiers
	 */
	public RacPrettyPrinter2(String fileName, JmlModifier modUtil) {
		this(new File(fileName), modUtil);
	}

	/**
	 * Constructs a pretty printer object for JML specifications
	 * 
	 * @param file
	 *            the file into which the code is to be printed
	 * @param modUtil
	 *            the modifier utility to print Java and JML modifiers
	 */
	public RacPrettyPrinter2(File file, JmlModifier modUtil) {
		super(file, modUtil);
		jmlModUtil = modUtil;
	}

	/**
	 * Constructs a pretty printer object for JML specifications
	 * 
	 * @param writer
	 *            the file into which the code is to be printed
	 */
	public RacPrettyPrinter2(TabbedPrintWriter writer) {
		super(writer);
	}

	// ----------------------------------------------------------------------
	// COMPILATION UNIT
	// ----------------------------------------------------------------------

	/** Prints a JML compilation unit. */
	public void visitJmlCompilationUnit(JmlCompilationUnit self) {
		self.acceptDelegee(this);

		JTypeDeclarationType[] tdecls = self.combinedTypeDeclarations();
		for (int i = 0; i < tdecls.length; i++) {
			newLine();
			tdecls[i].accept(this);
			newLine();
		}
	}

	/**
	 * Prints a compilation unit. This method is overridden here to not print
	 * type declarations in <code>self</code>, the reason being that, due to
	 * refinement, combined type declarations should be printed and this can
	 * only be done at JML level.
	 */
	public void visitCompilationUnit(JCompilationUnit self) {
		JPackageName packageName = self.packageName();
		JPackageImportType[] importedPackages = self.importedPackages();
		JClassOrGFImportType[] importedClasses = self.importedClasses();
		JClassOrGFImportType[] importedGFs = self.importedGFs();
		@SuppressWarnings("rawtypes")
		ArrayList tlMethods = self.tlMethods();

		if (packageName.getName().length() > 0) {
			packageName.accept(this);
			if (importedPackages.length + importedClasses.length > 0) {
				newLine();
			}
		}

		for (int i = 0; i < importedPackages.length; i++) {
			if (!importedPackages[i].getName().equals("java/lang")) {
				importedPackages[i].accept(this);
				newLine();
			}
		}

		for (int i = 0; i < importedClasses.length; i++) {
			importedClasses[i].accept(this);
			newLine();
		}

		for (int i = 0; i < importedGFs.length; i++) {
			importedGFs[i].accept(this);
			newLine();
		}

		acceptAll(tlMethods);

	}

	// ----------------------------------------------------------------------
	// TYPE DECLARATION
	// ----------------------------------------------------------------------

	/** Prints a JML class declaration. */
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {
		long modifiers = self.modifiers();
		String ident = self.ident();
		//		String orig_ident = ident;
		String superName = self.superName();
		CClassType[] interfaces = self.interfaces();

		newLine();
		mayStartAnnotation(hasFlag(modifiers, ACC_MODEL));
		print(modUtil.asString(modifiers));
		print("class " + ident);

		if (superName != null) {
			print(" extends " + superName.replace('/', '.'));
			// print(" extends " +
			// self.getCClass().getSuperClass().qualifiedName().replace('/',
			// '.'));
			if (self.isWeakSubtype())
				printJml("weakly");
		}

		if (interfaces.length != 0 || self.isRacable()) {
			print(" implements ");
			boolean interfaceWeaklyFlags[] = self.interfaceWeaklyFlags();
			for (int i = 0; i < interfaces.length; i++) {
				print(i == 0 ? "" : ", ");
				// print(toString(interfaces[i]));
				print(interfaces[i].toString());
				// WMD TODO 5.2.2008: toString used here, but not in
				// MjcPrettyPrinter.
				// why? Like this the main modifier is gone. But probably also
				// the
				// argument modifiers... investigate.

				if (interfaceWeaklyFlags[i])
					printJml("weakly");
			}

			// implement the interface JMLCheckable
			if (self.isRacable()) {
				print((interfaces.length != 0) ? "," : "");
				print(TN_JMLCHECKABLE);
			}
		}

		print(" ");
		visitClassBody(self);

		mayEndAnnotation(hasFlag(modifiers, ACC_MODEL));
	}

	/** Prints a JML type declaration. */
	protected void visitClassBody(JmlTypeDeclaration self) {
		print("{");
		pos += TAB_SIZE;

		newLine();

		JPhylum[] fieldsAndInits = self.fieldsAndInits();
		for (int i = 0; i < fieldsAndInits.length; i++) {
			fieldsAndInits[i].accept(this);
		}

		acceptAll(self.axioms());
		acceptAll(self.invariants());
		acceptAll(self.constraints());
		// acceptAll( self.dependsDecls() );
		acceptAll(self.representsDecls());
		acceptAll(self.inners());
		acceptAll(self.methods());

		pos -= TAB_SIZE;
		newLine();
		print("}");
	}

	/** Prints a JML interface declaration. */
	public void visitJmlInterfaceDeclaration(JmlInterfaceDeclaration self) {
		long modifiers = self.modifiers();
		String ident = self.ident();
		CClassType[] interfaces = self.interfaces();

		newLine();
		mayStartAnnotation(hasFlag(modifiers, ACC_MODEL));
		print(modUtil.asString(modifiers));
		print("interface " + ident);

		if (interfaces.length != 0) {
			print(" extends ");
			boolean interfaceWeaklyFlags[] = self.interfaceWeaklyFlags();
			for (int i = 0; i < interfaces.length; i++) {
				print((i == 0 ? "" : ", ") + interfaces[i].qualifiedName().replace('/', '.'));
				if (interfaceWeaklyFlags[i])
					printJml("weakly");
			}
		}

		print(" ");
		visitClassBody(self);

		mayEndAnnotation(hasFlag(modifiers, ACC_MODEL));
	}

	//	private void visitNotRACMethod(/* @non_null@ */JMethodDeclarationType self) {
	//		long modifiers = self.modifiers();
	//		CType returnType = self.returnType();
	//		String ident = self.ident();
	//		JFormalParameter[] parameters = self.parameters();
	//		CClassType[] exceptions = self.getExceptions();
	//		JBlock body = self.body();
	//
	//		newLine();
	//		print(modUtil.asString(modifiers));
	//		print(returnType);
	//		print(" ");
	//		print(ident);
	//		print("(");
	//		int count = 0;
	//
	//		for (int i = 0; i < parameters.length; i++) {
	//			if (count != 0) {
	//				print(", ");
	//			}
	//
	//			if (!parameters[i].isGenerated()) {
	//				parameters[i].accept(this);
	//				count++;
	//			}
	//		}
	//		print(")");
	//
	//		for (int i = 0; i < exceptions.length; i++) {
	//			if (i != 0) {
	//				print(", ");
	//			} else {
	//				print(" throws ");
	//			}
	//			print(exceptions[i].toString());
	//		}
	//
	//		print(" ");
	//		if (body != null) {
	//			body.accept(this);
	//		} else {
	//			print(";");
	//		}
	//		newLine();
	//	}

	/** Prints a JML method declaration. */
	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
		newLine();
		if (self.hasSpecification()) {
			mayStartAnnotation();
			self.methodSpecification().accept(this);
			mayEndAnnotation();
		}

		boolean isRacMethod = self.isRacMethod();
		boolean isModel = self.isModel();

		mayStartAnnotation(isModel);
		if (isRacMethod) {
			// System.out.println("Visiting "+self.stringRepresentation());
			visitRacJmlMethodDeclaration(self);
		} else {
			self.acceptDelegee(this);
			// visitNotRACMethod(self);
		}
		mayEndAnnotation(isModel);
	}

	/**
	 * Prints a JML method declaration in Racc. As the Mjcvisitor can't deal
	 * with \bigint, this method is create.
	 * 
	 * @see MjcPrettyPrinter#visitMethodDeclaration()
	 */
	public void visitRacJmlMethodDeclaration(/* @non_null@ */JmlMethodDeclaration self) {
		long modifiers = self.modifiers();
		CType returnType = self.returnType();
		String ident = self.ident();
		JFormalParameter[] parameters = self.parameters();
		CClassType[] exceptions = self.getExceptions();
		JBlock body = self.body();

		newLine();
		print(modUtil.asString(modifiers));
		print(toString(returnType));
		print(" ");
		print(ident);
		print("(");
		int count = 0;

		for (int i = 0; i < parameters.length; i++) {
			if (count != 0) {
				print(", ");
			}

			if (!parameters[i].isGenerated()) {
				parameters[i].accept(this);
				count++;
			}
		}
		print(")");

		for (int i = 0; i < exceptions.length; i++) {
			if (i != 0) {
				print(", ");
			} else {
				print(" throws ");
			}
			print(exceptions[i].toString());
		}

		print(" ");
		if (body != null) {
			body.accept(this);
		} else {
			print(";");
		}
		newLine();
	}

	/**
	 * Translates a \bigint literal.
	 */
	protected void visitBigintLiteral(long value) {
		print("java.math.BigInteger.valueOf(" + value + "L)");
	}

	/**
	 * prints a binary expression with the given operator and binary exps.
	 */
	protected void applyBinaryExpression(/* @non_null@ */JExpression left, JExpression right, String oper) {
		left.accept(this);
		String operTrim = oper.trim();
		if (left.getApparentType() == JmlStdType.Bigint) {
			String[] strArray = TransUtils.applyBigintBinOperator(operTrim);
			print(strArray[0]);
			right.accept(this);
			print(strArray[1]);
		} else { // FIXME, here should take care of real
			print(" ");
			print(oper);
			print(" ");
			right.accept(this);
		}
	}

	/**
	 * prints a binary expression with the given operator
	 */
	protected void visitBinaryExpression(/* @non_null@ */JBinaryExpression self, String oper) {
		JExpression left = self.left();
		JExpression right = self.right();

		applyBinaryExpression(left, right, oper);
	}

	/**
	 * prints an equality expression
	 */
	public void visitEqualityExpression(/* @non_null@ */JEqualityExpression self) {
		int oper = self.oper();
		JExpression left = self.left();
		JExpression right = self.right();

		String strOper;
		if (oper == OPE_EQ) {
			strOper = " == ";
		} else {
			strOper = " != ";
		}
		applyBinaryExpression(left, right, strOper);
	}

	/**
	 * prints a shift expression
	 */
	public void visitShiftExpression(/* @non_null@ */JShiftExpression self) {
		int oper = self.oper();
		JExpression left = self.left();
		JExpression right = self.right();

		String strOper;
		if (oper == OPE_SL) {
			strOper = " << ";
		} else if (oper == OPE_SR) {
			strOper = " >> ";
		} else {
			strOper = " >>> ";
		}

		applyBinaryExpression(left, right, strOper);
	}

	/**
	 * prints a bitwise expression
	 */
	public void visitBitwiseExpression(/* @non_null@ */JBitwiseExpression self) {
		int oper = self.oper();
		JExpression left = self.left();
		JExpression right = self.right();

		String strOper = "";
		switch (oper) {
		case OPE_BAND:
			strOper = " & ";
			break;
		case OPE_BOR:
			strOper = " | ";
			break;
		case OPE_BXOR:
			strOper = " ^ ";
			break;
		default:
			fail();
		}

		applyBinaryExpression(left, right, strOper);
	}

	/**
	 * prints an unary expression
	 */
	public void visitUnaryExpression(/* @non_null@ */JUnaryExpression self) {
		// The tailing space after the operator (e.g., "+ ") is
		// crucial not to print expressions like "+ ++x" as "+++x",
		// where the first is legal Java expression while the second
		// is not. Thank to Joe Kiniry for observing this.
		int oper = self.oper();
		JExpression expr = self.expr();
		CType type = expr.getApparentType();
		switch (oper) {
		case OPE_PLUS:
			print("+ ");
			expr.accept(this);
			break;

		case OPE_LNOT:
			print("! ");
			expr.accept(this);
			break;

		case OPE_MINUS:
			if (type == JmlStdType.Bigint) {
				expr.accept(this);
				print(TransUtils.applyBigintUnOperator("- "));
			} else {
				print("- ");
				expr.accept(this);
			}
			break;

		case OPE_BNOT:
			if (type == JmlStdType.Bigint) {
				expr.accept(this);
				print(TransUtils.applyBigintUnOperator("~ "));
			} else {
				print("~ ");
				expr.accept(this);
			}
			break;

		default:
			fail();
		}
	}

	/**
	 * prints an array length expression
	 */
	public void visitArrayAccessExpression(/* @non_null@ */JArrayAccessExpression self) {
		JExpression prefix = self.prefix();
		JExpression accessor = self.accessor();
		CType cType = accessor.getApparentType();

		if (cType == null) {
			cType = CStdType.Integer;
		}

		String[] strArray = TransUtils.applyBigintToNumber(cType, CStdType.Integer);
		String[] strArrayAssertion = TransUtils.createBigintConvertionAssertion(cType, CStdType.Integer);

		if (accessor.getApparentType() == JmlStdType.Bigint) {
			print(strArrayAssertion[0]);
			accessor.accept(this);
			print(strArrayAssertion[1]);
		}

		prefix.accept(this);
		print("[");
		print(strArray[0]);
		accessor.accept(this);
		print(strArray[1]);
		print("]");
	}

	/**
	 * prints a field expression, if it's not a model field then invoke the
	 * method in super class
	 */
	public void visitFieldExpression(/* @non_null@ */JClassFieldExpression self) {
		CField field = self.getField().getField();
		// FIXME, here might need to check if the accessor generated.
		if (self.sourceName() != null && org.multijava.util.Utils.hasFlag(field.modifiers(), ACC_MODEL)) {
			JExpression prefix = self.prefix();
			if (prefix != null) {
				prefix.accept(this);
				print('.');
			}
			print(MN_MODEL + field.ident() + "$" + field.owner().ident() + "()");
			return;
		} else {
			super.visitFieldExpression(self);
		}
	}

	/**
	 * prints a compound assignment expression
	 */
	public void visitCompoundAssignmentExpression(/* @non_null@ */JCompoundAssignmentExpression self) {
		int oper = self.oper();
		JExpression left = self.left();
		JExpression right = self.right();

		if (left.getApparentType() != JmlStdType.Bigint) {
			super.visitCompoundAssignmentExpression(self);
			return;
		}

		String strOpr = "";
		switch (oper) {
		case OPE_STAR:
			strOpr = "*";
			break;
		case OPE_SLASH:
			strOpr = "/";
			break;
		case OPE_PERCENT:
			strOpr = "%";
			break;
		case OPE_PLUS:
			strOpr = "+";
			break;
		case OPE_MINUS:
			strOpr = "-";
			break;
		case OPE_SL:
			strOpr = "<<";
			break;
		case OPE_SR:
			strOpr = ">>";
			break;
		case OPE_BSR:
			strOpr = ">>>";
			break;
		case OPE_BAND:
			strOpr = "&";
			break;
		case OPE_BOR:
			strOpr = "|";
			break;
		case OPE_BXOR:
			strOpr = "^";
			break;
		}

		String[] strArray = TransUtils.applyBigintBinOperator(strOpr);
		left.accept(this);
		print(" = ");
		left.accept(this);
		print(strArray[0]);
		right.accept(this);
		print(strArray[1]);
	}

	/**
	 * prints a prefix expression
	 */
	public void visitPrefixExpression(/* @non_null@ */JPrefixExpression self) {
		int oper = self.oper();
		JExpression expr = self.expr();

		if (expr.getApparentType() != JmlStdType.Bigint) {
			super.visitPrefixExpression(self);
		} else {
			// FIXME! if this expression has side-effect sub-expression, there
			// will be an error
			// A statement in a pair of parentheses is an expression, not
			// statement anymore.
			print("JMLRacBigIntegerUtils.value(");
			expr.accept(this);
			print(" = ");
			expr.accept(this);
			if (oper == OPE_PREINC) {
				print(".add(java.math.BigInteger.ONE)");
			} else {
				print(".subtract(java.math.BigInteger.ONE)");
			}
			// A statement in a pair of parentheses is an expression, not
			// statement anymore.
			print(")");
		}
	}

	/**
	 * prints a postfix expression
	 */
	public void visitPostfixExpression(/* @non_null@ */JPostfixExpression self) {
		int oper = self.oper();
		JExpression expr = self.expr();

		if (expr.getApparentType() != JmlStdType.Bigint) {
			super.visitPostfixExpression(self);
		} else {
			// FIXME! if this expression has side-effect sub-expression, there
			// will be an error
			print("JMLRacBigIntegerUtils.first(");
			expr.accept(this);
			print(", ");
			expr.accept(this);
			print(" = ");
			expr.accept(this);
			if (oper == OPE_POSTINC) {
				print(".add(java.math.BigInteger.ONE)");
			} else {
				print(".subtract(java.math.BigInteger.ONE)");
			}
			print(")");
		}
	}

	/**
	 * Prints the given variable definition, <code>self</code>. If the argument,
	 * <code>typeAndMod</code> is true, the type and modifiers are also printed;
	 * otherwise, they are not printed.
	 */
	private void printVariableDefinition(/* @non_null@ */JVariableDefinition self, boolean typeAndMod) {
		if (typeAndMod) {
			long modifiers = self.modifiers();
			CType type = self.getType();
			print(modUtil.asString(modifiers));
			print(toString(type));
			print(" ");
		}

		String ident = self.ident();
		JExpression expr = self.expr();
		print(ident);
		if (expr != null) {
			print(" = ");
			expr.accept(this);
		}
	}

	/**
	 * prints a variable declaration statement
	 */
	public void visitVariableDeclarationStatement(/* @non_null@ */JVariableDeclarationStatement self) {
		JVariableDefinition[] vars = self.getVars();

		// @ assume vars.length > 0;
		printVariableDefinition(vars[0], true); // with type and modifiers

		for (int i = 1; i < vars.length; i++) {
			print(", ");
			printVariableDefinition(vars[i], false); // no type and modifier
		}
		print(";");
		visitComments(self.getComments());
	}

	public void visitFormalParameters(JFormalParameter self) {
		print(self.modToString());
		if (self.isSpecialized()) {
			print(toString(self.staticType()));
			print(self.dynamicType().specializerSymbol());
		}
		print(toString(self.dynamicType()));
		print(self.paramToString());
	}

	/** Prints a JML constructor declaration. */
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration self) {
		newLine();
		if (self.hasSpecification()) {
			mayStartAnnotation();
			self.methodSpecification().accept(this);
			mayEndAnnotation();
		}
		boolean isModel = self.isModel();
		mayStartAnnotation(isModel);
		self.acceptDelegee(this);
		mayEndAnnotation(isModel);
	}

	/** Prints a JML predicate. */
	public void visitJmlPredicate(JmlPredicate self) {
		self.specExpression().accept(this);
	}

	public void visitJmlPredicateKeyword(JmlPredicateKeyword self) {
		if (self.isSameKeyword()) {
			print("\\same ");
		} else {
			print("\\not_specified ");
		}
	}

	/** Prints a RAC predicate. */
	public void visitRacPredicate(RacPredicate self) {
		self.specExpression().accept(this);
	}

	/** Prints a JML specification expression. */
	public void visitJmlSpecExpression(JmlSpecExpression self) {
		self.expression().accept(this);
	}

	/**
	 * Prints a given RacNode. A RacNode is a special AST node for storing
	 * generated runtime assertion check code. It is a sequence of intermixed
	 * <code>RacParser.END_OF_LINE</code>, String, and AST nodes.
	 * 
	 * @see RacNode
	 */
	public void visitRacNode(/* @ non_null @ */RacNode self) {
		final int deltaPos = self.indent() * TAB_SIZE;
		pos += deltaPos;

		@SuppressWarnings("rawtypes")
		final Iterator iter = self.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o == null)
				continue;
			else if (o == RacParser.END_OF_LINE)
				newLine();
			else if (o instanceof String)
				print(o);
			else
				((JPhylum) o).accept(this);
		}
		pos -= deltaPos;
	}

	/**
	 * Prints multi-lined code with proper line-breaking and indentation.
	 */
	protected void println(String code) {
		StringTokenizer st = new StringTokenizer(code, NEW_LINE);
		while (st.hasMoreTokens()) {
			print(st.nextToken());
			newLine();
		}
	}

	/** Prints a JML abrupt spec body. */
	public void visitJmlAbruptSpecBody(JmlAbruptSpecBody self) {
	}

	/** Prints a JML abrupt spec case. */
	public void visitJmlAbruptSpecCase(JmlAbruptSpecCase self) {
	}

	/** Prints a JML accessible clause. */
	public void visitJmlAccessibleClause(JmlAccessibleClause self) {
		Debug.msg("In JmlAccessibleClause");

		newLineOffset();
		print(self.isRedundantly() ? "accessible_redundantly " : "accessible ");
		JmlStoreRef[] storeRefs = self.storeRefs();
		for (int i = 0; i < storeRefs.length; i++) {
			if (i != 0)
				print(", ");
			storeRefs[i].accept(this);
		}
		print(";");
	}

	/**
	 * Prints a JML assignable clause. If runtime assertion check code has been
	 * generated, it is also printed.
	 */
	public void visitJmlAssertStatement(JmlAssertStatement self) {
		mayStartAnnotation(true);
		print(self.isRedundantly() ? "assert_redundantly " : "assert ");
		self.predicate().accept(this);
		if (self.hasThrowMessage()) {
			print(" : ");
			self.throwMessage().accept(this);
		}
		print(";");
		mayEndAnnotation(true);

		// print RAC code if exists
		if (self.hasAssertionCode()) {
			newLine();
			self.assertionCode().accept(this);
		}
	}

	/** Prints a JML assignable clause. */
	public void visitJmlAssignableClause(JmlAssignableClause self) {
		Debug.msg("In JmlAssignableClause");

		newLineOffset();
		print(self.isRedundantly() ? "assignable_redundantly " : "assignable ");
		JmlStoreRef[] storeRefs = self.storeRefs();
		for (int i = 0; i < storeRefs.length; i++) {
			if (i != 0)
				print(", ");
			storeRefs[i].accept(this);
		}
		print(";");
	}

	/**
	 * Prints a JML assume statement. If runtime assertion check code has been
	 * generated, it is also printed.
	 */
	public void visitJmlAssumeStatement(JmlAssumeStatement self) {
		mayStartAnnotation();
		print(self.isRedundantly() ? "assume_redundantly " : "assume ");
		self.predicate().accept(this);
		if (self.hasThrowMessage()) {
			print(" : ");
			self.throwMessage().accept(this);
		}
		print(";");
		mayEndAnnotation();

		// print RAC code if exists
		if (self.hasAssertionCode()) {
			newLine();
			self.assertionCode().accept(this);
		}
	}

	/** Prints a JML axim. */
	public void visitJmlAxiom(JmlAxiom self) {
		mayStartAnnotation();
		print("axiom ");
		self.predicate().accept(this);
		print(";");
		mayEndAnnotation();
	}

	/** Prints a JML behavior spec. */
	public void visitJmlBehaviorSpec(JmlBehaviorSpec self) {
		Debug.msg("In JmlBehaviorSpec");

		newLineOffset();
		print(modUtil.asString(self.privacy()));
		print("behavior");
		offset += TAB_SIZE;
		self.specCase().accept(this);
		offset -= TAB_SIZE;
	}

	/** Prints a JML break clause. */
	public void visitJmlBreaksClause(JmlBreaksClause self) {
	}

	/** Prints a JML callable clause. */
	public void visitJmlCallableClause(JmlCallableClause self) {
		Debug.msg("In JmlCallableClause");

		newLineOffset();
		print(self.isRedundantly() ? "callable_redundantly " : "callable ");
		JmlMethodNameList methodNames = self.methodNames();
		methodNames.accept(this);
		print(";");
	}

	public void visitJmlMethodNameList(JmlMethodNameList self) {
		if (self.isStoreRefKeyword()) {
			self.storeRefKeyword().accept(this);
		} else {
			print(self.toString());
		}
	}

	/** Prints a JML captures clause. */
	public void visitJmlCapturesClause(JmlCapturesClause self) {
		Debug.msg("In JmlCapturesClause");

		newLineOffset();
		print(self.isRedundantly() ? "captures_redundantly " : "captures ");
		JmlStoreRef[] storeRefs = self.storeRefs();
		for (int i = 0; i < storeRefs.length; i++) {
			if (i != 0)
				print(", ");
			storeRefs[i].accept(this);
		}
		print(";");
	}

	/** Prints static or instance initializer */
	public void visitJmlClassBlock(JmlClassBlock self) {
		visitClassBlock(self);
	}

	/** Prints a JML classOrGFImport clause. */
	public void visitJmlClassOrGFImport(JmlClassOrGFImport self) {
		if (self.isModel()) {
			print("//@ model ");
		}
		String name = self.getName();
		print("import " + name.replace('/', '.') + ";");
	}

	/** Prints a JML code contract. */
	public void visitJmlCodeContract(JmlCodeContract self) {
		newLineOffset();
		print("code_contract");
		offset += TAB_SIZE;
		JmlSpecBodyClause[] subclassingContract = self.accessibleClauses();
		for (int i = 0; i < subclassingContract.length; i++) {
			subclassingContract[i].accept(this);
		}
		subclassingContract = self.callableClauses();
		for (int i = 0; i < subclassingContract.length; i++) {
			subclassingContract[i].accept(this);
		}
		subclassingContract = self.measuredClauses();
		for (int i = 0; i < subclassingContract.length; i++) {
			subclassingContract[i].accept(this);
		}
		offset -= TAB_SIZE;
	}

	/** Prints a JML constraint clause. */
	public void visitJmlConstraint(JmlConstraint self) {
		JmlMethodNameList methodNames = self.methodNames();
		JmlPredicate predicate = self.predicate();
		boolean isForEverything = self.isForEverything();
		boolean isRedundantly = self.isRedundantly();

		mayStartAnnotation();
		print(isRedundantly ? "constraint_redundantly " : "constraint ");
		predicate.accept(this);
		if (isForEverything) {
			print(" for ");
			print("\\everything");
		} else {
			if (!methodNames.toString().equals("")) {
				print(" for ");
				print(methodNames.toString());
			}
		}
		print(";");
		mayEndAnnotation();
	}

	/** Prints a JML constructor name. */
	public void visitJmlConstructorName(JmlConstructorName self) {
		print("new " + toString(self.ownerType()));
		print("(");
		if (self.hasParamDisambigList()) {
			CType[] params = self.paramDisambigList();
			for (int i = 0; i < params.length; i++) {
				if (i != 0)
					print(", ");
				print(params[i]);
			}
		}
		print(")");
	}

	/** Prints a JML continues statement. */
	public void visitJmlContinuesClause(JmlContinuesClause self) {
	}

	/** Prints a JML debug statement. */
	public void visitJmlDebugStatement(JmlDebugStatement self) {
		mayStartAnnotation();
		print("debug ");
		self.expression().accept(this);
		print(";");
		mayEndAnnotation();

		// print RAC code if exists
		if (self.hasAssertionCode()) {
			newLine();
			self.assertionCode().accept(this);
		}
	}

	/** Prints a JML diverges clause. */
	public void visitJmlDivergesClause(JmlDivergesClause self) {
		Debug.msg("In JmlDivergesClause");
		visitSpecBodyClause(self, "diverges");
	}

	/** Prints a JML <code>elemtype</code> expression. */
	public void visitJmlElemTypeExpression(JmlElemTypeExpression self) {
		print("\\elemtype(");
		self.specExpression().accept(this);
		print(")");
	}

	/** Prints a JML ensures clause. */
	public void visitJmlEnsuresClause(JmlEnsuresClause self) {
		Debug.msg("In JmlEnsuresClause");
		visitSpecBodyClause(self, "ensures");
	}

	/** Prints a JML example. */
	public void visitJmlExample(JmlExample self) {
		Debug.msg("In JmlExample");
		newLineOffset();
		print(modUtil.asString(self.privacy()));
		print("example");
		offset += TAB_SIZE;
		visitExample(self);
		offset -= TAB_SIZE;
	}

	/** Prints a JML exceptional behavior specification. */
	public void visitJmlExceptionalBehaviorSpec(JmlExceptionalBehaviorSpec self) {
		Debug.msg("In JmlExceptionalBehaviorSpec");

		newLineOffset();
		print(modUtil.asString(self.privacy()));
		print("exceptional_behavior");
		offset += TAB_SIZE;
		self.specCase().accept(this);
		offset -= TAB_SIZE;
	}

	/** Prints a JML exceptional example. */
	public void visitJmlExceptionalExample(JmlExceptionalExample self) {
		Debug.msg("In JmlExceptionalExample");

		newLineOffset();
		print(modUtil.asString(self.privacy()));
		print("exceptional_example");
		offset += TAB_SIZE;
		visitExample(self);
		offset -= TAB_SIZE;
	}

	/** Prints a JML exceptional spec body. */
	public void visitJmlExceptionalSpecBody(JmlExceptionalSpecBody self) {
		Debug.msg("In JmlExceptionalSpecBody");
		visitSpecBody(self);
	}

	/** Prints a JML exceptional spec case. */
	public void visitJmlExceptionalSpecCase(JmlExceptionalSpecCase self) {
		Debug.msg("In JmlExceptionalSpecCase");
		visitGeneralSpecCase(self);
	}

	/** Prints a JML method extending specification. */
	public void visitJmlExtendingSpecification(JmlExtendingSpecification self) {
		Debug.msg("In JmlExtendingSpecification");
		visitMethodSpecification(self);
	}

	/** Prints a JML method extending specification. */
	private void visitMethodSpecification(JmlSpecification self) {
		JmlSpecCase[] specCases = self.specCases();
		if (specCases != null) {
			if (specCases.length > 1) {
				newLineOffset();
				offset += TAB_SIZE;
			}
			for (int i = 0; i < specCases.length; i++) {
				if (i != 0) {
					offset -= TAB_SIZE;
					newLineOffset();
					print("also");
					offset += TAB_SIZE;
				}
				specCases[i].accept(this);
			}
			if (specCases.length > 1)
				offset -= TAB_SIZE;
		}

		if (self.redundantSpec() != null)
			self.redundantSpec().accept(this);
	}

	/**
	 * Prints the given JML field declaration. If this field declaration has a
	 * generated initializer block (e.g., one for a ghost field), it is also
	 * printed.
	 */
	public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {
		JVariableDefinition variable = self.variable();
		long modifiers = variable.modifiers();
		CType type = variable.getType();
		String ident = variable.ident();
		JExpression expr = variable.getValue();

		if (ident.indexOf("$") != -1) {
			return; // don't print generated elements
		}

		newLine();
		final boolean startAnnotation = hasFlag(modifiers, ACC_MODEL) || hasFlag(modifiers, ACC_GHOST);
		mayStartAnnotation(startAnnotation);
		print(modUtil.asString(modifiers));
		print(toString(type));
		print(" ");
		print(ident);
		if (expr != null) {
			print(" = ");
			expr.accept(this);
		}
		print(";");
		mayEndAnnotation(startAnnotation);

		// print assertion check code, if any, e.g., private variable
		// declarations for ghost variables and their initializers
		// (see TransClass.ghostFieldAccessors).
		if (self.hasAssertionCode()) {
			self.assertionCode().accept(this);
		}
	}

	/** Prints a JML forall variable declaration. */
	public void visitJmlForAllVarDecl(JmlForAllVarDecl self) {
		newLineOffset();
		print("forall ");
		visitVarDecls(self.quantifiedVarDecls());
		print(";");
	}

	/** Prints a Java variable delcaration. */
	private void visitVarDecls(JVariableDefinition[] varDefs) {
		for (int i = 0; i < varDefs.length; i++) {
			JExpression expr = varDefs[i].expr();
			if (i == 0) {
				print(modUtil.asString(varDefs[i].modifiers()));
				print(toString(varDefs[i].getType()));
				print(" ");
			} else {
				print(", ");
			}
			print(varDefs[i].ident());
			if (expr != null) {
				print(" = ");
				expr.accept(this);
			}
		}
	}

	/** Prints a JML formal parameter. */
	public void visitJmlFormalParameter(JmlFormalParameter self) {
		CType actualType = self.getType();
		String ident = self.ident();

		String modifiersAsString = self.modifiersAsString();
		modifiersAsString = modifiersAsString.replace("nullable", "/*@ nullable @*/");
		modifiersAsString = modifiersAsString.replace("non_null", "/*@ non_null @*/");

		print(modifiersAsString);
		print(toString(actualType));
		if (ident.indexOf("$") == -1) {
			print(" ");
			print(ident);
		}
	}

	/** Prints a JML fresh expression. */
	public void visitJmlFreshExpression(JmlFreshExpression self) {
		Debug.msg("In JmlFreshExpression");

		print("\\fresh(");
		JmlSpecExpression[] specExps = self.specExpressionList();
		for (int i = 0; i < specExps.length; i++) {
			if (i != 0)
				print(", ");
			specExps[i].accept(this);
		}
		print(")");
	}

	/** Prints a JML generic spec body. */
	public void visitJmlGenericSpecBody(JmlGenericSpecBody self) {
		Debug.msg("In JmlGenericSpecBody");
		visitSpecBody(self);
	}

	/** Prints a JML generic spec case. */
	public void visitJmlGenericSpecCase(JmlGenericSpecCase self) {
		Debug.msg("In JmlGenericSpecCase");

		visitGeneralSpecCase(self);
	}

	/** Prints a JML guarded statement. */
	public void visitJmlGuardedStatement(JmlGuardedStatement self) {
	}

	/** Prints a JML henceby statement. */
	public void visitJmlHenceByStatement(JmlHenceByStatement self) {
		mayStartAnnotation();
		print(self.isRedundantly() ? "hence_by_redundantly " : "hence_by ");
		self.predicate().accept(this);
		print(";");
		mayEndAnnotation();

		// print RAC code if exists
		if (self.hasAssertionCode()) {
			newLine();
			self.assertionCode().accept(this);
		}
	}

	/** Prints a JML in data group clause. */
	public void visitJmlInGroupClause(JmlInGroupClause self) {
		print("visitJmlInGroupClause not implemented in RacPrettyPrinter");
	}

	/** Prints a JML informal expression. */
	public void visitJmlInformalExpression(JmlInformalExpression self) {
		print("(* ");
		print(self.text());
		print(" *)");
	}

	/** Prints a JML informal store reference. */
	public void visitJmlInformalStoreRef(JmlInformalStoreRef self) {
		print("(* ");
		print(self.text());
		print(" *)");
	}

	/** Prints a JML initially variable assertion. */
	public void visitJmlInitiallyVarAssertion(JmlInitiallyVarAssertion self) {
	}

	/** Prints a JML invariant. */
	public void visitJmlInvariant(JmlInvariant self) {
		mayStartAnnotation();
		print(self.isRedundantly() ? "invariant_redundantly " : "invariant ");
		self.predicate().accept(this);
		print(";");
		mayEndAnnotation();
	}

	/** Prints a JML invariant for expression. */
	public void visitJmlInvariantForExpression(JmlInvariantForExpression self) {
		print("\\invariant_for(");
		self.specExpression().accept(this);
		print(")");
	}

	/** Prints a JML invariant statement. */
	public void visitJmlInvariantStatement(JmlInvariantStatement self) {
	}

	/** Prints a JML isinitialized expression. */
	public void visitJmlIsInitializedExpression(JmlIsInitializedExpression self) {
		print("\\is_initialized(" + toString(self.referenceType()) + ")");
	}

	/** Prints a JML label expression. */
	public void visitJmlLabelExpression(JmlLabelExpression self) {
		print(self.isPosLabel() ? "(\\lblpos " : "(\\lblneg ");
		print(self.ident() + " ");
		self.specExpression().accept(this);
		print(")");
	}

	/** Prints a JML old variable declaration. */
	public void visitJmlLetVarDecl(JmlLetVarDecl self) {
		newLineOffset();
		print("old ");
		visitVarDecls(self.specVariableDeclarators());
		print(";");
	}

	/** Prints a JML variable declaration. */
	public void visitJmlVariableDefinition(JmlVariableDefinition self) {
		visitVariableDefinition(self);
	}

	/** Prints a JML lockset expression. */
	public void visitJmlLockSetExpression(JmlLockSetExpression self) {
		print("\\lockset");
	}

	/** Prints a JML loop invariant. */
	public void visitJmlLoopInvariant(JmlLoopInvariant self) {
		mayStartAnnotation();
		print(self.isRedundantly() ? "maintaining_redundantly " : "maintaining ");
		self.predicate().accept(this);
		print(";");
		mayEndAnnotation();
	}

	/**
	 * Prints a JML loop statement. A JML loop statement is a Java loop
	 * statement with optional loop invariants and variants.
	 */
	public void visitJmlLoopStatement(JmlLoopStatement self) {
		JmlLoopInvariant[] invs = self.loopInvariants();
		for (int i = 0; i < invs.length; i++) {
			invs[i].accept(this);
		}

		JmlVariantFunction[] vars = self.variantFunctions();
		for (int i = 0; i < vars.length; i++) {
			vars[i].accept(this);
		}

		if (invs.length > 0 || vars.length > 0) {
			newLineOffset();
		}

		if (self.hasAssertionCode()) {
			// print instrumeneted code
			newLine();
			self.assertionCode().accept(this);
		} else {
			self.stmt().accept(this);
		}
	}

	/** Prints a JML working space clause. */
	public void visitJmlWorkingSpaceClause(JmlWorkingSpaceClause self) {
		Debug.msg("In JmlWorkingSpaceClause");

		String keyword = self.isRedundantly() ? "working_space_redundantly " : "working_space ";
		newLineOffset();
		if (self.isNotSpecified()) {
			print(keyword + "\\not_specified;");
		} else {
			print(keyword);
			self.specExp().accept(this);
			if (self.predOrNot() != null) {
				print(" if ");
				self.predOrNot().accept(this);
			}
			print(";");
		}
	}

	/** Prints a JML duration clause. */
	public void visitJmlDurationClause(JmlDurationClause self) {
		Debug.msg("In JmlDurationClause");

		String keyword = self.isRedundantly() ? "duration_redundantly " : "duration ";
		newLineOffset();
		if (self.isNotSpecified()) {
			print(keyword + "\\not_specified;");
		} else {
			print(keyword);
			self.specExp().accept(this);
			if (self.predOrNot() != null) {
				print(" if ");
				self.predOrNot().accept(this);
			}
			print(";");
		}
	}

	/** Prints a JML maps into data group clause. */
	public void visitJmlMapsIntoClause(JmlMapsIntoClause self) {
		print("visitJmlMapsIntoClause not implemented in RacPrettyPrinter");
	}

	/** Prints a JML measured clause. */
	public void visitJmlMeasuredClause(JmlMeasuredClause self) {
		Debug.msg("In JmlMeasuredClause");

		String keyword = self.isRedundantly() ? "measured_by_redundantly " : "measured_by ";
		newLineOffset();
		if (self.isNotSpecified()) {
			print(keyword + "\\not_specified;");
		} else {
			print(keyword);
			self.specExp().accept(this);
			if (self.predOrNot() != null) {
				print(" if ");
				self.predOrNot().accept(this);
			}
			print(";");
		}
	}

	/** Prints a JML method name. */
	public void visitJmlMethodName(JmlMethodName self) {
		JmlName[] subnames = self.subnames();
		for (int i = 0; i < subnames.length; i++) {
			if (i != 0)
				print(".");
			subnames[i].accept(this);
		}
		print("(");
		if (self.hasParamDisambigList()) {
			CType[] params = self.paramDisambigList();
			for (int i = 0; i < params.length; i++) {
				if (i != 0)
					print(", ");
				print(toString(params[i]));
			}
		}
		print(")");
	}

	/** Prints a JML model program. */
	public void visitJmlModelProgram(JmlModelProgram self) {
	}

	/** Prints a JML MonitorsFor variable assertion. */
	public void visitJmlMonitorsForVarAssertion(JmlMonitorsForVarAssertion self) {
	}

	/** Prints a JML name. */
	public void visitJmlName(JmlName self) {
		if (self.isIdent())
			print(self.ident());
		else if (self.isThis())
			print("this");
		else if (self.isSuper())
			print("super");
		else if (self.isAll())
			print("[*]");
		else if (self.isPos()) {
			print("[");
			self.start().accept(this);
			print("]");
		} else if (self.getName().equals("*")) {
			print("*");
		} else { // self.isRange()
			print("[");
			self.start().accept(this);
			print(" .. ");
			self.end().accept(this);
			print("]");
		}

	}

	/** Prints a JML <code>\nonnullelements</code> expression. */
	public void visitJmlNonNullElementsExpression(JmlNonNullElementsExpression self) {
		print("\\nonnullelements(");
		self.specExpression().accept(this);
		print(")");
	}

	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
		self.assignmentStatement().accept(this);
	}

	/** Prints a JML nondeterministic choice statement. */
	public void visitJmlNondetChoiceStatement(JmlNondetChoiceStatement self) {
	}

	/** Prints a JML nondeterministic if statement. */
	public void visitJmlNondetIfStatement(JmlNondetIfStatement self) {
	}

	/** Prints a JML normal behavior specification. */
	public void visitJmlNormalBehaviorSpec(JmlNormalBehaviorSpec self) {
		newLineOffset();
		print(modUtil.asString(self.privacy()));
		if (self.isCodeSpec()) {
			print("code ");
		}
		print("normal_behavior");
		offset += TAB_SIZE;
		self.specCase().accept(this);
		offset -= TAB_SIZE;
	}

	/** Prints a JML normal example. */
	public void visitJmlNormalExample(JmlNormalExample self) {
		newLineOffset();
		print(modUtil.asString(self.privacy()));
		print("normal_example");
		offset += TAB_SIZE;
		visitExample(self);
		offset -= TAB_SIZE;
	}

	/** Prints a JML normal spec body. */
	public void visitJmlNormalSpecBody(JmlNormalSpecBody self) {
		Debug.msg("In JmlNormalSpecBody");
		visitSpecBody(self);
	}

	/** Prints a JML normal spec case. */
	public void visitJmlNormalSpecCase(JmlNormalSpecCase self) {
		Debug.msg("In JmlExceptionalBehaviorSpec");
		visitGeneralSpecCase(self);
	}

	/** Prints a JML <code>\not_modified</code> expression. */
	public void visitJmlNotModifiedExpression(JmlNotModifiedExpression self) {
		print("\\not_modified(");
		JmlStoreRef[] storeRefs = self.storeRefList();
		for (int i = 0; i < storeRefs.length; i++) {
			if (i != 0)
				print(", ");
			storeRefs[i].accept(this);
		}
		print(")");
	}

	/** Prints a JML <code>\not_assigned</code> expression. */
	public void visitJmlNotAssignedExpression(JmlNotAssignedExpression self) {
		print("\\not_assigned(");
		visitJmlStoreRefListWrapper(self);
		print(")");
	}

	/** Prints a JML <code>\only_accessed</code> expression. */
	public void visitJmlOnlyAccessedExpression(JmlOnlyAccessedExpression self) {
		print("\\only_accessed(");
		visitJmlStoreRefListWrapper(self);
		print(")");
	}

	/** Prints a JML <code>\only_assigned</code> expression. */
	public void visitJmlOnlyAssignedExpression(JmlOnlyAssignedExpression self) {
		print("\\only_assigned(");
		visitJmlStoreRefListWrapper(self);
		print(")");
	}

	/** Prints a JML <code>\only_called</code> expression. */
	public void visitJmlOnlyCalledExpression(JmlOnlyCalledExpression self) {
		print("\\only_called(");
		JmlMethodNameList methodNames = self.methodNames();
		methodNames.accept(this);
		print(")");
	}

	/** Prints a JML <code>\only_captured</code> expression. */
	public void visitJmlOnlyCapturedExpression(JmlOnlyCapturedExpression self) {
		print("\\only_captured(");
		visitJmlStoreRefListWrapper(self);
		print(")");
	}

	public void visitJmlStoreRefListWrapper(JmlStoreRefListWrapper self) {
		JmlStoreRef[] storeRefs = self.storeRefList();
		for (int i = 0; i < storeRefs.length; i++) {
			if (i != 0)
				print(", ");
			storeRefs[i].accept(this);
		}
	}

	/** Prints a JML <code>\old</code> expression. */
	public void visitJmlOldExpression(JmlOldExpression self) {
		print("\\old(");
		self.specExpression().accept(this);
		if (self.label() != null && !self.label().equals("")) {
			print(", ");
			print(self.label());
		}
		print(")");
	}

	/** Prints a JML <code>\pre</code> expression. */
	public void visitJmlPreExpression(JmlPreExpression self) {
		print("\\pre(");
		self.specExpression().accept(this);
		print(")");
	}

	/** Prints a JML <code>\max</code> expression. */
	public void visitJmlMaxExpression(JmlMaxExpression self) {
		print("\\max(");
		self.expression().accept(this);
		print(")");
	}

	/** Prints a JML <code>\duration</code> expression. */
	public void visitJmlDurationExpression(JmlDurationExpression self) {
		print("\\duration(");
		self.expression().accept(this);
		print(")");
	}

	/** Prints a JML <code>\working_space</code> expression. */
	public void visitJmlWorkingSpaceExpression(JmlWorkingSpaceExpression self) {
		print("\\working_space(");
		self.expression().accept(this);
		print(")");
	}

	/** Prints a JML <code>\space</code> expression. */
	public void visitJmlSpaceExpression(JmlSpaceExpression self) {
		print("\\space(");
		self.specExpression().accept(this);
		print(")");
	}

	/** Prints a JML package import statement. */
	public void visitJmlPackageImport(JmlPackageImport self) {
		if (self.isModel()) {
			print("//@ model ");
		}
		String name = self.getName();
		print("import " + name.replace('/', '.') + ".*;");
	}

	/** Prints a JML reach expression. */
	public void visitJmlReachExpression(JmlReachExpression self) {
		CType type = self.referenceType();
		JmlStoreRefExpression[] storeRef = self.storeRefExpressions(); //mfrias-mffrias-24-09-2012: Recovered the array rather than a single storeRefExpression

		print("\\reach(");
		self.specExpression().accept(this);
		if (type != null)
			print(", " + toString(type));
		for (int i=0; i<storeRef.length; i++){
			if (i==0){
				print(", ");
			} else {
				print(" + ");
			}	
			storeRef[i].accept(this);	
		}
		print(")");
	}

	/** Prints a JML readableif variable assertion. */
	public void visitJmlReadableIfVarAssertion(JmlReadableIfVarAssertion self) {
	}

	// FIXME - these two assertions are not checked.
	public void visitJmlWritableIfVarAssertion(JmlWritableIfVarAssertion self) {
	}

	/**
	 * Prints a JML redundant specifications including <code>implies_that</code>
	 * and <code>for_example</code>.
	 */
	public void visitJmlRedundantSpec(JmlRedundantSpec self) {
		JmlSpecCase[] implications = self.implications();
		if (implications != null) {
			newLineOffset();
			print("implies_that");
			offset += TAB_SIZE;
			for (int i = 0; i < implications.length; i++) {
				if (i != 0) {
					offset -= TAB_SIZE;
					newLineOffset();
					print("also");
					offset += TAB_SIZE;
				}
				implications[i].accept(this);
			}
			offset -= TAB_SIZE;
		}

		JmlExample[] examples = self.examples();
		if (examples != null) {
			newLineOffset();
			print("for_example");
			offset += TAB_SIZE;
			for (int i = 0; i < examples.length; i++) {
				if (i != 0) {
					offset -= TAB_SIZE;
					newLineOffset();
					print("also");
					offset += TAB_SIZE;
				}
				examples[i].accept(this);
			}
			offset -= TAB_SIZE;
		}
	}

	/** Prints a JML refine prefix. */
	public void visitJmlRefinePrefix(JmlRefinePrefix self) {
	}

	/** Prints a JML relational expression. */
	public void visitJmlRelationalExpression(JmlRelationalExpression self) {
		JExpression left = self.left();
		JExpression right = self.right();

		applyBinaryExpression(left, right, self.opString());
	}

	/** Prints a JML <code>represents</code> declaration. */
	public void visitJmlRepresentsDecl(JmlRepresentsDecl self) {
		JmlStoreRef storeRef = self.storeRef();

		mayStartAnnotation();
		print(self.isRedundantly() ? "represents_redundantly " : "represents ");
		storeRef.accept(this);
		if (self.usesAbstractionFunction()) {
			print(" <- ");
			self.specExpression().accept(this);
		} else {
			print(" \\such_that ");
			self.predicate().accept(this);
		}
		print(";");
		mayEndAnnotation();
	}

	/** Prints a JML requires clause. */
	public void visitJmlRequiresClause(JmlRequiresClause self) {
		Debug.msg("In JmlRequiresClause");

		visitSpecBodyClause(self, "requires");
	}

	/** Prints a JML <code>\result</code> expression. */
	public void visitJmlResultExpression(JmlResultExpression self) {
		print("\\result");
	}

	/** Prints a JML return clause. */
	public void visitJmlReturnsClause(JmlReturnsClause self) {
	}

	/** Prints a JML set comprehension expression. */
	public void visitJmlSetComprehension(JmlSetComprehension self) {
		String ident = self.ident();
		CType type = self.getApparentType();
		CType memberType = self.memberType();
		JExpression supersetPred = self.supersetPred();
		JmlPredicate predicate = self.predicate();

		print("new ");
		print(toString(type));
		print(" ");
		print("{");
		print(toString(memberType));
		print(" ");
		print(ident);
		print(" | ");
		supersetPred.accept(this);
		print(" && ");
		predicate.accept(this);
		print("}");
	}

	/** Prints the given JML set statement. */
	public void visitJmlSetStatement(JmlSetStatement self) {
		// print annotation
		mayStartAnnotation(true);
		print("set ");
		self.assignmentExpression().accept(this);
		print(";");
		mayEndAnnotation(true);

		// print runtime assertion check code if exists
		if (self.hasAssertionCode()) {
			newLine();
			self.assertionCode().accept(this);
		}
	}

	/** Prints the given JML refining statement. */
	public void visitJmlRefiningStatement(JmlRefiningStatement self) {
		// print annotation
		mayStartAnnotation(true);
		print("refining ");
		self.specStatement().accept(this);
		mayEndAnnotation(true);

		self.body().accept(this);

		// print runtime assertion check code if exists
		if (self.hasAssertionCode()) {
			newLine();
			self.assertionCode().accept(this);
		}
	}

	/** Prints a JML signals_only clause */
	public void visitJmlSignalsOnlyClause(JmlSignalsOnlyClause self) {
		Debug.msg("In JmlSignalsOnlyClause");

		String keyword = null;

		if (!self.isRedundantly()) {
			keyword = "signals_only ";
		} else {
			keyword = "signals_only_redundantly ";
		}
		newLineOffset();
		print(keyword);
		if (self.isNothing()) {
			print("\\nothing");
		} else {
			CClassType[] exceptions = self.exceptions();
			for (int i = 0; i < exceptions.length; i++) {
				if (i != 0)
					print(", ");
				print(exceptions[i]);
			}
		}
		print(";");
	}

	/** Prints a JML signals clause */
	public void visitJmlSignalsClause(JmlSignalsClause self) {
		Debug.msg("In JmlSignalsClause");

		String keyword = self.isRedundantly() ? "signals_redundantly " : "signals ";
		String ident = self.ident() == null ? "" : self.ident();
		newLineOffset();
		print(keyword + "(" + toString(self.type()) + " " + ident + ") ");
		if (self.isNotSpecified()) {
			print("\\not_specified");
		} else if (self.hasPredicate()) {
			self.predOrNot().accept(this);
		}
		print(";");
	}

	/** Prints a JML specification quantified expression. */
	public void visitJmlSpecQuantifiedExpression(JmlSpecQuantifiedExpression self) {
		print("(");
		if (self.isForAll())
			print("\\forall ");
		else if (self.isExists())
			print("\\exists ");
		else if (self.isMax())
			print("\\max ");
		else if (self.isMin())
			print("\\min ");
		else if (self.isNumOf())
			print("\\num_of ");
		else if (self.isProduct())
			print("\\product ");
		else if (self.isSum())
			print("\\sum ");
		else
			fail();
		visitVarDecls(self.quantifiedVarDecls());
		print("; ");
		if (self.hasPredicate())
			self.predicate().accept(this);
		print("; ");
		self.specExpression().accept(this);
		print(")");
	}

	/** Prints a JML specification statement. */
	public void visitJmlSpecStatement(JmlSpecStatement self) {
	}

	/** Prints a JML specification. */
	public void visitJmlSpecification(JmlSpecification self) {
		Debug.msg("In JmlSpecification");
		visitMethodSpecification(self);
	}

	/** Prints a JML store reference expression. */
	public void visitJmlStoreRefExpression(JmlStoreRefExpression self) {
		JmlName[] names = self.names();
		for (int i = 0; i < names.length; i++) {
			if (i != 0 && !names[i].isSpecArrayRefExpr())
				print(".");
			names[i].accept(this);
		}
	}

	/**
	 * Prints a JML store reference keywords such as <code>\everything</code>,
	 * <code>\nothing</code>, and <code>\not_specified</code>.
	 */
	public void visitJmlStoreRefKeyword(JmlStoreRefKeyword self) {
		if (self.isEverything())
			print("\\everything");
		else if (self.isNothing())
			print("\\nothing");
		else
			print("\\not_specified");
	}

	/** Prints a JML <code>\type</code> expression. */
	public void visitJmlTypeExpression(JmlTypeExpression self) {
		print("\\type(" + toString(self.type()) + ")");
	}

	/** Prints a JML <code>\typeof</code> expression. */
	public void visitJmlTypeOfExpression(JmlTypeOfExpression self) {
		print("\\typeof(");
		self.specExpression().accept(this);
		print(")");
	}

	/** Prints a JML unreachable statement. */
	public void visitJmlUnreachableStatement(JmlUnreachableStatement self) {
		mayStartAnnotation();
		print("unreachable;");
		mayEndAnnotation();

		// print RAC code if exists
		if (self.hasAssertionCode()) {
			newLine();
			self.assertionCode().accept(this);
		}
	}

	/** Prints a JML variant function. */
	public void visitJmlVariantFunction(JmlVariantFunction self) {
		mayStartAnnotation();
		print(self.isRedundantly() ? "decreasing_redundantly " : "decreasing ");
		self.specExpression().accept(this);
		print(";");
		mayEndAnnotation();
	}

	/** Prints a JML when clause. */
	public void visitJmlWhenClause(JmlWhenClause self) {
		Debug.msg("In JmlWhenClause");
		visitSpecBodyClause(self, "when");
	}

	// ----------------------------------------------------------------------
	// OVERRIDDEN METHODS
	// ----------------------------------------------------------------------

	// It might be worth changing the MjcPrettyPrinter to make call toString()
	// rather than doing it here ...
	/**
	 * Prints a cast expression.
	 */
	public void visitCastExpression(JCastExpression self) {
		JExpression expr = self.expr();
		CType dest = self.getType();

		if (expr.getApparentType() == CStdType.Null) {
			print("(");
			print(toString(dest));
			print(")null");
		} else {
			String[] strArray = TransUtils.applyBigintCast(dest, expr.getApparentType(), toString(dest));
			print(strArray[0]);
			expr.accept(this);
			print(strArray[1]);
		}
	}

	/**
	 * prints a unaryPrompte expression
	 */
	public void visitUnaryPromoteExpression(/* @non_null@ */JUnaryPromote self) {
		JExpression expr = self.expr();
		CType type = self.getApparentType();

		/*
		 * We need to consider two cases in e1 op= e2: 1) the type of e1 is
		 * \bigint 2) the type of e2 is \bigint and e1 is numeric. Note that
		 * according to the JLS 15.26.2 the above expr is equivalent to e1 =
		 * (type_of_e1)(e1 op e2).
		 * 
		 * also, we need to consider cases like: (\bigint[])null...
		 */
		if ((type == JmlStdType.Bigint) || (type.isNumeric() && expr.getApparentType() == JmlStdType.Bigint)) {
			String[] strArray = TransUtils.applyBigintCast(type, expr.getApparentType(), toString(type));
			print(strArray[0]);
			expr.accept(this);
			print(strArray[1]);
		} else if (TransUtils.isBigintArray(type)) {
			print("(");
			print("(");
			print(toString(type));
			print(")(");
			expr.accept(this);
			print("))");
		} else {
			super.visitUnaryPromoteExpression(self);
		}
	}

	/**
	 * Prints an object allocator expression.
	 */
	public void visitNewObjectExpression(JNewObjectExpression self) {
		CType type = self.getType();
		JExpression[] params = self.params();
		print("new " + toString(type) + "(");

		visitArgs(params);

		print(")");
	}

	/**
	 * Prints an array allocator expression.
	 */
	// WMD: why was this overwritten???
	/*
	 * public void visitNewArrayExpression( JNewArrayExpression self ) { CType
	 * type = self.getType(); JArrayDimsAndInits dims = self.dims(); if (type
	 * instanceof CArrayType) { print("new " +
	 * toString(((CArrayType)type).getBaseType())); } else { print("new " +
	 * toString(type)); } dims.accept(this); }
	 */

	/**
	 * Prints an ordinal literal.
	 */
	public void visitOrdinalLiteral( /* @ non_null @ */JOrdinalLiteral self) {
		// If you change this method, you may also want change
		// TransExpression.visitOrdinalLiteral.
		Number value = self.numberValue();
		CType type = self.getApparentType();
		assertTrue(value != null);

		if (type == CStdType.Byte) {
			visitByteLiteral(value.byteValue());
		} else if (type == CStdType.Integer) {
			visitIntLiteral(value.intValue());
		} else if (type == CStdType.Long) {
			visitLongLiteral(value.longValue());
		} else if (type == CStdType.Short) {
			visitShortLiteral(value.shortValue());
		} else if (type == CStdType.Char) {
			print("(char)" + value.intValue()); // quick fix for "(char)10"
		} else if (type == JmlStdType.Bigint) {
			visitBigintLiteral(value.longValue());
		} else {
			fail();
		}
	}

	/**
	 * Prints a character literal. This method is overridden here to specially
	 * handle non-printable characters such as unicode chars.
	 */
	public void visitCharLiteral( /* @ non_null @ */JCharLiteral self) {
		// If you change this method, you may also want change
		// TransExpression.visitCharLiteral.
		Character chValue = (Character) self.getValue();
		char value;
		if (chValue != null) {
			value = chValue.charValue();
		} else {
			value = self.image().charAt(0); // !!! false
		}
		print("'" + TransUtils.toPrintableString(value) + "'");
	}

	/**
	 * Prints a double literal. This method is overridden here to take care of
	 * special values such as INFINITY, NaN, MIN_VALUE, and MAX_VALUE.
	 */
	protected void visitDoubleLiteral(double value) {
		// If you change this method, you may also want change
		// TransExpression.visitDoubleLiteral.
		print(TransUtils.toString(value));
	}

	/**
	 * Prints a float literal. This method is overridden here to take care of
	 * special values such as Float.POSTIVE_INFINITY, Float.NaN, etc.
	 */
	protected void visitFloatLiteral(float value) {
		// If you change this method, you may also want change
		// TransExpression.visitFloatLiteral.
		print(TransUtils.toString(value));
	}

	protected String toString(/* @non_null@ */CType type) {
		if (type instanceof JmlNumericType || TransUtils.isBigintArray(type)) {
			return TransUtils.toString(type);
		} else {
			if (!((type.getIdent()).equals("java.lang.RuntimeException"))){
				return super.toString(type.getErasure());
			} else {
				return type.getIdent();
			}
			
		}
		//		return (type instanceof JmlNumericType || TransUtils.isBigintArray(type)) ? TransUtils.toString(type) : super.toString(type.getErasure());
	}

	// ----------------------------------------------------------------------
	// HELPER METHODS
	// ----------------------------------------------------------------------

	/** Prints the given general spec case. */
	public void visitGeneralSpecCase(JmlGeneralSpecCase self) {
		Debug.msg("In helper visitSpecCase");

		JmlSpecVarDecl[] specVarDecls = self.specVarDecls();
		JmlRequiresClause[] specHeader = self.specHeader();
		JmlSpecBody specBody = self.specBody();

		if (specVarDecls != null) {
			for (int i = 0; i < specVarDecls.length; i++) {
				specVarDecls[i].accept(this);
			}
		}

		if (specHeader != null) {
			for (int i = 0; i < specHeader.length; i++) {
				specHeader[i].accept(this);
			}
		}

		if (specBody != null)
			specBody.accept(this);
	}

	/** Prints spec cases. */
	protected void visitSpecCases(JmlSpecCase[] specCases) {
		newLineOffset();
		print("{|");
		offset += TAB_SIZE;
		for (int i = 0; i < specCases.length; i++) {
			if (i != 0) {
				offset -= TAB_SIZE;
				newLineOffset();
				print("also");
				offset += TAB_SIZE;
			}
			specCases[i].accept(this);
		}
		offset -= TAB_SIZE;
		newLineOffset();
		print("|}");
	}

	/** Prints example specifications. */
	protected void visitExample(JmlExample self) {
		Debug.msg("In JmlExample");

		JmlSpecVarDecl[] specVarDecls = self.specVarDecls();
		JmlRequiresClause[] specHeader = self.specHeader();
		JmlSpecBodyClause[] specBody = self.specBody();

		if (specVarDecls != null) {
			for (int i = 0; i < specVarDecls.length; i++) {
				specVarDecls[i].accept(this);
			}
		}
		if (specHeader != null) {
			for (int i = 0; i < specHeader.length; i++) {
				specHeader[i].accept(this);
			}
		}
		if (specBody != null) {
			for (int i = 0; i < specBody.length; i++) {
				specBody[i].accept(this);
			}
		}
	}

	/** Prints specification body. */
	protected void visitSpecBody(JmlSpecBody self) {
		Debug.msg("In helper visitSpecBody");

		JmlSpecBodyClause[] specClauses = self.specClauses();
		JmlGeneralSpecCase[] specCases = self.specCases();

		if (specClauses != null) {
			for (int i = 0; i < specClauses.length; i++) {
				specClauses[i].accept(this);
			}
		}
		if (specCases != null)
			visitSpecCases(specCases);
	}

	/** Prints specfication body clause. */
	public void visitSpecBodyClause(JmlPredicateClause self, String keyword) {
		Debug.msg("In helpe visitSpecBodyClause");

		if (self.isRedundantly())
			keyword = keyword + "_redundantly";

		newLineOffset();
		if (self.isNotSpecified()) {
			print(keyword + " \\not_specified;");
		} else {
			print(keyword + " ");
			self.predOrNot().accept(this);
			print(";");
		}
	}

	/**
	 * Starts the annotation printing mode if not already in; also prints the
	 * opening annotation marker <code>/*@</code>.
	 * 
	 * <pre>
	 * &lt;jml&gt;
	 * modifies annotationDepth, inAnnotation, jmlModUtil, atMarkerPos;
	 * ensures annotationDepth == \old(annotationDepth) + 1;
	 * ensures_redundantly inAnnotation &amp;&amp; jmlModUtil.withoutMarkers();
	 * &lt;/jml&gt;
	 * </pre>
	 * 
	 * @see #mayEndAnnotation()
	 * @see #mayStartAnnotation(boolean)
	 */
	protected void mayStartAnnotation() {
		mayStartAnnotation(true);
	}

	/**
	 * Stops the annotation printing mode if currently in that mode; also prints
	 * the closing annotation marker <code>@&#042;&#047;</code>.
	 * 
	 * <pre>
	 * &lt;jml&gt;
	 * modifies annotationDepth, inAnnotation, jmlModUtil;
	 * ensures annotationDepth == \old(annotationDepth) - 1;
	 * &lt;/jml&gt;
	 * </pre>
	 * 
	 * @see #mayStartAnnotation()
	 * @see #mayEndAnnotation(boolean)
	 */
	protected void mayEndAnnotation() {
		mayEndAnnotation(true);
	}

	/**
	 * Starts the annotation printing mode if <code>flag</code> is
	 * <code>true </code> and not already in the annotation mode; also prints
	 * the opening annotation marker <code>/*@</code>.
	 * 
	 * <pre>
	 * &lt;jml&gt;
	 * modifies annotationDepth, inAnnotation, jmlModUtil, atMarkerPos;
	 * ensures flag ==&gt; (annotationDepth == \old(annotationDepth) + 1);
	 * ensures_redundantly flag ==&gt; 
	 *   (inAnnotation &amp;&amp; jmlModUtil.withoutMarkers());
	 * &lt;/jml&gt;
	 * </pre>
	 * 
	 * @see #mayEndAnnotation(boolean)
	 */
	protected void mayStartAnnotation(boolean flag) {
		if (flag) {
			annotationDepth++;
			if (annotationDepth == 1) { // not already in annotation?
				newLine();
				print("/*@ ");
				inAnnotation = true;
				jmlModUtil.setWithoutMarkers(true);
				atMarkerPos = pos;
			}
		}
	}







	/**
	 * Stops the annotation printing mode if the <code>flag</code> is
	 * <code>true </code> and currently in the annotation mode; also prints the
	 * closing annotation marker <code>@&#042;&#047;</code>.
	 * 
	 * <pre>
	 * &lt;jml&gt;
	 * modifies annotationDepth, inAnnotation, jmlModUtil;
	 * ensures flag ==&gt; (annotationDepth == \old(annotationDepth) - 1);
	 * &lt;/jml&gt;
	 * </pre>
	 * 
	 * @see #mayStartAnnotation(boolean)
	 */
	protected void mayEndAnnotation(boolean flag) {
		if (flag) {
			annotationDepth--;
			if (annotationDepth == 0) {
				newLine();
				inAnnotation = false;
				jmlModUtil.setWithoutMarkers(false);
				print("*/");
			}
		}
	}



	/**
	 * Prints a new line marker. If in the annotation mode, also print the
	 * annotation marker at the appropriate position.
	 */
	protected void newLine() {
		p.println();
		if (inAnnotation) {
			p.setPos(atMarkerPos);
			p.print("  @");
		}
	}

	/**
	 * Prints a new line marker. If in the annotation mode, also print the
	 * annotation marker at the appropriate position and advance to the distance
	 * of the current offset.
	 */
	protected void newLineOffset() {
		p.println();
		if (inAnnotation) {
			p.setPos(atMarkerPos);
			p.print("  @");
			p.setPos(pos + offset + 4); // 4 for the width of "/*@ ".
			p.print("");
		}
	}

	/** Prints a string. */
	protected void print(String s) {
		// 4 for the width of "/*@ ".
		p.setPos(inAnnotation ? pos + 4 : pos);
		p.print(s);
	}

	/** Prints a type. */
	protected void print(CType t) {
		// print(t.getErasure().toString());
		// WMD: the above didn't make sense to me...
		print(toString(t));
	}

	/**
	 * Prints a string enclosed in the JML annotation markers. I.e., prints
	 * <code>&#047;&#042;@ ... @&#042;&#047;</code>.
	 */
	protected void printJml(String s) {
		// 4 for the width of "/*@ ".
		p.setPos(inAnnotation ? pos + 4 : pos);
		p.print(inAnnotation ? s : " /*@ " + s + " @*/");
	}

	/** Visits each element of the argument. */
	protected void acceptAll(JPhylum[] all) {
		if (all != null) {
			for (int i = 0; i < all.length; i++)
				all[i].accept(this);
		}
	}

	public void close() {
		p.close();
	}

	// ----------------------------------------------------------------------
	// NOT NEEDED, BUT DEFINED IN JmlVisitor
	// ----------------------------------------------------------------------
	public void visitJmlDeclaration(JmlDeclaration self) {
	}

	public void visitJmlExpression(JmlExpression self) {
	}

	public void visitJmlGeneralSpecCase(JmlGeneralSpecCase self) {
	}

	public void visitJmlMethodSpecification(JmlMethodSpecification self) {
	}

	public void visitJmlNode(JmlNode self) {
	}

	public void visitJmlSpecBody(JmlSpecBody self) {
	}

	public void visitJmlSpecVarDecl(JmlSpecVarDecl self) {
	}

	public void visitJmlStoreRef(JmlStoreRef self) {
	}

	// ----------------------------------------------------------------------
	// PROTECTED AND PRIVATE DATA MEMBERS
	// ----------------------------------------------------------------------

	/** the space from '@' to the current print position for specification. */
	protected int offset;

	/**
	 * indicates if currently printing inside a JML annotation marker.
	 * 
	 * <pre>
	 * &lt;jml&gt;
	 * private invariant inAnnotation &lt;==&gt; annotationDepth &gt; 0;
	 * &lt;/jml&gt;
	 * </pre>
	 */
	/* @ spec_protected @ */private boolean inAnnotation;


	public void setInAnnotation(boolean b){
		inAnnotation = b;
	}

	public boolean getInAnnotation(){
		return inAnnotation;
	}


	/**
	 * the depth of annotations.
	 * 
	 * <pre>
	 * &lt;jml&gt;
	 * private invariant annotationDepth &gt;= 0;
	 * &lt;/jml&gt;
	 * </pre>
	 */
	/* @ spec_protected @ */private int annotationDepth = 0;

	/** the column number where annotation markers were written. */
	/* @ spec_protected @ */private int atMarkerPos;

	public void setAtMarkerPos(int p){
		atMarkerPos = p;
	}

	public int getAtMarkerPos(){
		return atMarkerPos;
	}


	/**
	 * Modifier utility to manipulating modifiers, e.g., to get string
	 * representations of modifiers from bit mask encoding.
	 * 
	 * <pre>
	 * &lt;jml&gt;
	 * private invariant jmlModUtil != null &amp;&amp; jmlModUtil == modUtil &amp;&amp;
	 *           inAnnotation &lt;==&gt; jmlModUtil.withoutMarkers();
	 * &lt;/jml&gt;
	 * </pre>
	 * */
	/* @ spec_protected @ */private JmlModifier jmlModUtil;

	public void setJmlModUtil(JmlModifier jm){
		jmlModUtil = jm;
	}

	public JmlModifier getJmlModUtil(){
		return jmlModUtil;
	}


	public void setAnnotationDepth(int i){
		annotationDepth = i;
	}

	public int getAnnotationDepth(){
		return annotationDepth;
	}


	// ******************** DPD Modifications ************************//
	/**
	 * prints a method call expression
	 */
	public void visitMethodCallExpression(/* @non_null@ */JMethodCallExpression self) {
		String ident = self.ident();
		JExpression[] args = self.args();

		// ignore the generated methods
		if (ident != null && ident.equals(JAV_INIT)) {
			return;
		}

		JExpression prefix = self.prefix();
		//		JNameExpression sourceName = self.sourceName();

		// if possible, print as it appeared in the source file.

		// if (sourceName != null) {
		// sourceName.accept(this);
		// } else {
		if (prefix != null) {
			prefix.accept(this);
			print('.');
		}
		print(ident);
		// }

		print("(");
		visitArgs(args);
		print(")");
	}
}

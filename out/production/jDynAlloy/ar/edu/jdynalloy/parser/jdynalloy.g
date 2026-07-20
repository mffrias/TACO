header {
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
	
package ar.edu.jdynalloy.parser;

import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprFunction;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprOverride;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprProduct;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.OrFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.ImpliesFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.*;
import ar.uba.dc.rfm.alloy.ast.formulas.QuantifiedFormula.Operator;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntersection;
import ar.uba.dc.rfm.alloy.parser.ExpressionParser;
import ar.uba.dc.rfm.alloy.parser.FormalParametersDeclaration;
import ar.uba.dc.rfm.alloy.parser.IAlloyExpressionParseContext;	
import ar.edu.jdynalloy.parser.JDynAlloyParsingException;
import ar.edu.jdynalloy.parser.LabelUtils;

import ar.edu.jdynalloy.ast.*;

import ar.edu.jdynalloy.xlator.JType;

import ar.edu.jdynalloy.buffer.Represents;
import ar.edu.jdynalloy.buffer.JDynAlloyParserModuleBuffer;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import java.util.Collections;

}

class JDynAlloyParser extends Parser;

options {
	k =7;
	buildAST = false;
	defaultErrorHandler=false;
	ASTLabelType = "ar.edu.jdynalloy.parser.JDynAlloyAST";
}

{	
	
		JDynAlloyParserModuleBuffer buffer;
	
		//private AlloyTyping fields = new AlloyTyping();
		//private JDynAlloyProgramParseContext classCtx = new JDynAlloyProgramParseContext(Collections.<AlloyVariable>emptySet(),Collections.<AlloyVariable>emptySet(),true);
		List<JDynAlloyModule> modules = new ArrayList<JDynAlloyModule>();
		
	public static AlloyExpression buildTerm(IAlloyExpressionParseContext ctx, Token t, Token aliasModuleIdToken) {
		String text = t.getText();
		if (ctx.isIntLiteral(text))
		    return new ExprIntLiteral(ctx.getIntLiteral(text));
		if (ctx.isVariableName(text))
			return new ExprVariable(ctx.getAlloyVariable(text));
		else
			return new ExprConstant(aliasModuleIdToken==null? null: aliasModuleIdToken.getText(), text); 
	}
	
	
	public static void extendAlloyTyping(FormalParametersDeclaration at, List<String> vars, String t) {
		for (String var : vars) {
			at.put(new AlloyVariable(var), t); 
		}
	}
	
	public static String join(String s1, String s2)
	{
		if ( s1 == null || s1.length() == 0 )
			return s2;
		else if ( s2 == null || s2.length() == 0 )
			return s1;
		else
			return s1 + " " + s2;
	}
	
	public static String relationType(String type1, String mult1, String mult2, String type2)
	{
		return ExpressionParser.join(type1, mult1) + " -> " + ExpressionParser.join(mult2, type2); 
	}		
		
	private AlloyFormula buildImpliesFormula(AlloyFormula... fs) {
		AlloyFormula result = null;
		for (int i = 0; i < fs.length; i++) {
			AlloyFormula f = fs[i];
			if (result == null)
				result = f;
			else
				result = new ImpliesFormula(result, f);
		}
		return result;
	}		
				
	private JDynAlloyProgramParseContext globalCtx = new JDynAlloyProgramParseContext(Collections.<AlloyVariable>singletonList(AlloyVariable.buildAlloyVariable("thiz")),Collections.<AlloyVariable>emptySet(),true);
	
	public void setGlobalCtx(JDynAlloyProgramParseContext ctx) {
		globalCtx = ctx;
	}
	
		
}
dynJmlAlloyModules returns [List<JDynAlloyModule> r]
	{ 
		r = new ArrayList<JDynAlloyModule>();
		JDynAlloyModule dynJmlAlloyModule1;
		JDynAlloyProgramParseContext ctx = new JDynAlloyProgramParseContext(globalCtx);
		
		   
	}
	: 
	(
		dynJmlAlloyModule1 = dynJmlAlloyModule[ctx] {r.add(dynJmlAlloyModule1);}
			
	)+
	
	
	;

dynJmlAlloyModule[JDynAlloyProgramParseContext ctx] returns [JDynAlloyModule r]
{ 
		
			buffer = new JDynAlloyParserModuleBuffer();
			JField field;
			List<JField> fields = new ArrayList<JField>();
			JObjectInvariant objectInvariant;
			JClassInvariant classInvariant;
			JClassConstraint classConstraint;
			JObjectConstraint objectConstraint;
			
			JRepresents represents;
			JProgramDeclaration programDeclaration;
			
			ctx.addAlloyVariable(AlloyVariable.buildAlloyVariable("thiz"));
			
			AlloyFormula alloyFormula1 = null;
}
:
	"module" moduleId:IDENT
	//("one" {throw new RuntimeException("Not implemented Yet"); } )?
	("abstract" {buffer.setAbstract(true);} )?
	"sig" alloySignature:IDENT 
    (
        (
            "extends" superClassName:IDENT {buffer.setSuperClassSignatureId(superClassName.getText());} 
            | "in" inSuperSet:IDENT {buffer.setInSignatureId(inSuperSet.getText());}
        )?
        LBRACE RBRACE
    )?
	
	// ("one" "sig" classSingletonName:IDENT "extends" "Class" LBRACE RBRACE {throw new RuntimeException("Not implemented Yet") })?
	
	
	LBRACE (alloyFormula1 = alloyFormula[ctx] {buffer.setFact(alloyFormula1);} )? RBRACE
	
	//the fields must be first to represents fields, to avoid a second stage of parsing 
	(field = jField[ctx]	{ buffer.getFields().put(field.getFieldVariable(), field.getFieldType()); })*
		 
 	(
		  objectInvariant = jObjectInvariant[ctx] { buffer.getInvariants().add(objectInvariant.getFormula()); }
		| classInvariant = jClassInvariant[ctx] { buffer.getStaticInvariants().add(classInvariant.getFormula()); }
        | objectConstraint = jObjectConstraint[ctx] { buffer.getConstraints().add(objectConstraint.getFormula()); }
        | classConstraint = jClassConstraint[ctx] { buffer.getStaticConstraints().add(classConstraint.getFormula()); }
		| represents = jRepresents[ctx] { buffer.getRepresents().add(new Represents(represents.getExpression(), represents.getExpressionType(),represents.getFormula())); }
		| {	JDynAlloyProgramParseContext programCtx = new JDynAlloyProgramParseContext(ctx);}
		  programDeclaration = jProgramDeclaration[programCtx] { buffer.getPrograms().add(programDeclaration); } 

	)*
	
	{
		if (!moduleId.getText().equals(alloySignature.getText())) {
			throw new RuntimeException("Module name must equal Signature name"); 
		}
		
		buffer.setSignatureId(alloySignature.getText());
		buffer.setThisType(new JType(alloySignature.getText()));
		r = buffer.getModule();

	}
	
;
	
	
jField[JDynAlloyProgramParseContext ctx] returns [JField r]
{
	JType jType;
}
:
"field" fieldName:IDENT COLON jType=alloyType LBRACE RBRACE
{
	AlloyVariable alloyVariable = AlloyVariable.buildAlloyVariable(fieldName.getText());
	r = new JField(alloyVariable, jType);
	ctx.addAlloyField(alloyVariable);	 
}	
;


jObjectInvariant[JDynAlloyProgramParseContext ctx] returns [JObjectInvariant r]
{
	AlloyFormula alloyFormula1;
}
:
OBJECT_INVARIANT alloyFormula1=alloyFormula[ctx] {r = new JObjectInvariant(alloyFormula1);}
;

jClassInvariant[JDynAlloyProgramParseContext ctx] returns [JClassInvariant r]
{
    AlloyFormula alloyFormula1;
}
:
CLASS_INVARIANT alloyFormula1=alloyFormula[ctx] {r = new JClassInvariant(alloyFormula1);}
;


jObjectConstraint[JDynAlloyProgramParseContext ctx] returns [JObjectConstraint r]
{
    AlloyFormula alloyFormula1;
}
:
OBJECT_CONSTRAINT alloyFormula1=alloyFormula[ctx] {r = new JObjectConstraint(alloyFormula1);}
;

jClassConstraint[JDynAlloyProgramParseContext ctx] returns [JClassConstraint r]
{
    AlloyFormula alloyFormula1;
}
:
CLASS_CONSTRAINT alloyFormula1=alloyFormula[ctx] {r = new JClassConstraint(alloyFormula1);}
;

jRepresents[JDynAlloyProgramParseContext ctx] returns [JRepresents r] 
{
	AlloyFormula alloyFormula1;
	AlloyExpression expression1;
}
:
REPRESENTS expression1=termExpression[ctx] 
SUCH_THAT alloyFormula1=alloyFormula[ctx]
{ 
		AlloyVariable fieldName;
		
		if (expression1 instanceof ExprJoin ) {
			ExprJoin exprJoin = (ExprJoin) expression1; 
			if (!(exprJoin.getLeft() instanceof ExprVariable && ((ExprVariable) exprJoin.getLeft()).getVariable().getVariableId().getString().equals("thiz") )) {
				throw new JDynAlloyParsingException("Invalid 'Represents Field' expression. Only join expression are supported with 'thiz' in left expression");								
			}
			if (!(exprJoin.getRight() instanceof ExprVariable) ) {
				throw new JDynAlloyParsingException("Invalid 'Represents Field' expression. Only join expression are supported with field name in right expression");
			}
			ExprVariable exprVariable = (ExprVariable) exprJoin.getRight();
			fieldName = exprVariable.getVariable();  
		} else if (expression1 instanceof ExprVariable) {
			ExprVariable exprVariable = (ExprVariable) expression1;
			fieldName = exprVariable.getVariable();
		} else {
			throw new JDynAlloyParsingException("Invalid 'Represents Field' expression");
		}
		
		JType representsFieldType = buffer.getFields().getJAlloyType(fieldName);
		if (representsFieldType == null) {
			throw new JDynAlloyParsingException("Represent field " + fieldName + " has reference to field don't declared (field must be declared prior to represents fields");
		}
	
		r = new JRepresents(expression1, representsFieldType, alloyFormula1);

}
;	
 
//Program declaration
jProgramDeclaration[JDynAlloyProgramParseContext ctx] returns [JProgramDeclaration r]
{
	boolean isAbstract = false;
	boolean isConstructor = false;
	boolean isPure = false;
	String programId;
	String signatureId;
	List<JSpecCase> specCases = new ArrayList<JSpecCase>();
	List<JVariableDeclaration> parameters = new ArrayList<JVariableDeclaration>();
	JStatement body;
	
	JVariableDeclaration variableDeclaration1;
	JSpecCase specCase1;

}
:

(VIRTUAL {isAbstract = true;})? 
PROGRAM (PURE {isPure = true;})? moduleName:IDENT COLON COLON programName:IDENT 
LBRACKET
variableDeclaration1=jVariableDeclaration[ctx] {parameters.add(variableDeclaration1);} (COMMA variableDeclaration1=jVariableDeclaration[ctx] {parameters.add(variableDeclaration1);} )*
RBRACKET

(
SPECIFICATION
LBRACE 	(specCase1=jSpecCase[ctx] {specCases.add(specCase1);})* RBRACE 
)?
	
IMPLEMENTATION 
body=jBlock[ctx] 	
{
	signatureId = moduleName.getText();
	programId = programName.getText();
	isConstructor = programName.getText().equals("Constructor");
	r = new JProgramDeclaration(isAbstract, isConstructor, isPure, signatureId, programId, parameters, specCases, body, new AlloyTyping(), new ArrayList<AlloyFormula>());
}
;
//Program specification
jSpecCase[JDynAlloyProgramParseContext ctx] returns [JSpecCase r]
{
	JPrecondition precondition1;
	JPostcondition postcondition1; 
	JModifies modifies1;
	
	List<JPrecondition> requires = new ArrayList<JPrecondition>();
	List<JPostcondition> ensures = new ArrayList<JPostcondition>();
	List<JModifies> modifies = new ArrayList<JModifies>();
}
:
SPECCASE SPECCASE_ID 
LBRACE 
	
	(
		precondition1=jRequires[ctx] {requires.add(precondition1);}
		| postcondition1=jEnsures[ctx] {ensures.add(postcondition1);}
		| modifies1=jModifies[ctx] {modifies.add(modifies1);}
	)*

RBRACE
{
	r=new JSpecCase(requires, ensures, modifies);
}
;

jRequires[JDynAlloyProgramParseContext ctx] returns [JPrecondition r]
{
	AlloyFormula alloyFormula1;
}
:
REQUIRES LBRACE alloyFormula1=alloyFormula[ctx] RBRACE
{r=new JPrecondition(alloyFormula1);}
;

jEnsures[JDynAlloyProgramParseContext ctx] returns [JPostcondition r]
{
	AlloyFormula alloyFormula1;
}
:
ENSURES LBRACE alloyFormula1=alloyFormula[ctx] RBRACE
{r=new JPostcondition(alloyFormula1);}
;

jModifies[JDynAlloyProgramParseContext ctx] returns [JModifies r]
{
	AlloyExpression alloyExpression1;
}
:
MODIFIES LBRACE alloyExpression1=termExpression[ctx] RBRACE
{r=new JModifies(alloyExpression1);}
;

//Statements

jStatement[JDynAlloyProgramParseContext ctx] returns [JStatement r]
:
	r=jAssert[ctx]
|	r=jAssume[ctx]
|	r=jVariableDeclarationStatement[ctx]
|	r=jSkip[ctx]
|	r=jIfThenElse[ctx]
|	r=jCreateObject[ctx]
|	r=jAssignment[ctx]
|   r=jProgramCall[ctx]
|   r=jWhile[ctx]
|	r=jBlock[ctx]
|   r=jHavoc[ctx]
;

jAssert [JDynAlloyProgramParseContext ctx] returns [JAssert r]
{AlloyFormula alloyFormula1;}
:
ASSERT alloyFormula1=alloyFormula[ctx] SEMICOLON {r = new JAssert(alloyFormula1);}
;

jIfThenElse [JDynAlloyProgramParseContext ctx] returns [JIfThenElse r]
{
	AlloyFormula alloyFormula1;
	JStatement thenStatement;
	JStatement elseStatement;	
}
:
IF alloyFormula1=alloyFormula[ctx]
thenStatement=jBlock[ctx] 
ELSE
elseStatement=jBlock[ctx]
SEMICOLON
{
	
	r = new JIfThenElse(alloyFormula1,thenStatement,elseStatement,LabelUtils.nextIfLabel());
}
;

jAssume [JDynAlloyProgramParseContext ctx] returns [JAssume r]
{AlloyFormula alloyFormula1;}
:
ASSUME alloyFormula1=alloyFormula[ctx] SEMICOLON {r = new JAssume(alloyFormula1);}
;


jVariableDeclarationStatement[JDynAlloyProgramParseContext ctx] returns [JVariableDeclaration r]
:
	r = jVariableDeclaration[ctx] SEMICOLON
;

//variable declaration (for statement
jVariableDeclaration[JDynAlloyProgramParseContext ctx] returns [JVariableDeclaration r]
{JType alloyType;}
:
VAR id:IDENT COLON alloyType=alloyType  
{
	AlloyVariable variable = AlloyVariable.buildAlloyVariable(id.getText());
	r = new JVariableDeclaration(variable,alloyType);
	ctx.addAlloyVariable(variable);	 
}
; 

jSkip [JDynAlloyProgramParseContext ctx] returns [JSkip r]
{AlloyFormula alloyFormula1;}
:
SKIP SEMICOLON {r = new JSkip();}
;

jBlock [JDynAlloyProgramParseContext ctx] returns [JBlock r]
{
	List<JStatement> statementList = new ArrayList<JStatement>();
	JStatement aStatement;
}
:
	//LBRACE (aStatement=jStatementWithOutBlock[ctx] {statementList.add(aStatement);} )+ RBRACE  
	LBRACE (aStatement=jStatement[ctx] {statementList.add(aStatement);} )+ RBRACE
	{r = new JBlock(statementList.toArray(new JStatement[0]));}
;

jCreateObject [JDynAlloyProgramParseContext ctx] returns [JCreateObject r]
{}
:
	CREATE_OBJECT LT className:IDENT GT LBRACKET variableName:IDENT RBRACKET SEMICOLON	{r= new JCreateObject(className.getText(), AlloyVariable.buildAlloyVariable(variableName.getText()));}
;

jAssignment [JDynAlloyProgramParseContext ctx] returns [JAssignment r]
{
	AlloyExpression left;
	AlloyExpression right;
}
:
	left=termExpression[ctx] ASSIGNMENT right=termExpression[ctx] SEMICOLON {r = new JAssignment(left, right);} 
;

jProgramCall[JDynAlloyProgramParseContext ctx] returns [JProgramCall r] 
{
	AlloyExpression parameter;
	List<AlloyExpression> parameterList = new ArrayList<AlloyExpression>();
	boolean isSuper = false;
}
: (SUPER {isSuper = true; })? CALL programName:IDENT LBRACKET parameter=termExpression[ctx] {parameterList.add(parameter);} ( COMMA parameter=termExpression[ctx] {parameterList.add(parameter);} )* RBRACKET SEMICOLON 
{r = new JProgramCall(isSuper,programName.getText(),parameterList);}
;

jWhile[JDynAlloyProgramParseContext ctx] returns [JWhile r]
{
	AlloyFormula condition;
	JStatement body;
	AlloyFormula loopInvariantFormula = null;
	
}
:
WHILE condition=alloyFormula[ctx]
(LOOP_INVARIANT loopInvariantFormula=alloyFormula[ctx])?
body=jBlock[ctx] SEMICOLON
{
	JLoopInvariant loopInvariant;
	if (loopInvariantFormula == null) {
		loopInvariant = null;
	} else {
		loopInvariant = new JLoopInvariant(loopInvariantFormula);
	}
	r = new JWhile(condition,body,loopInvariant,LabelUtils.nextWhileLabel());	
}

;

jHavoc[JDynAlloyProgramParseContext ctx] 
returns [JHavoc r]
{
	AlloyExpression expression1;
}
:
	HAVOC expression1=termExpression[ctx] SEMICOLON
{
	r = new JHavoc( expression1 );
};

//JType a.k.a. AlloyType

alloyType returns [JType r]
{
	String s = null;
}
:
    s=typeName {r = JType.parse(s);}
;



//FORMULAS

// formula expressions
alloyFormula [JDynAlloyProgramParseContext ctx] returns [AlloyFormula r]
: 
 r=impliesFormula[ctx]
;

impliesFormula[JDynAlloyProgramParseContext ctx] returns [AlloyFormula r]
{
	AlloyFormula right = null;
	List<AlloyFormula> rightList = new ArrayList<AlloyFormula>();
}
:
	r=orFormula[ctx] {rightList.add(r);} (IMPLIES right=orFormula[ctx] {rightList.add(right);})*
	{
		if ( rightList.size() > 1 ) {
			r = this.buildImpliesFormula(rightList.toArray(new AlloyFormula[0]));
		} 		
	}
;

orFormula[JDynAlloyProgramParseContext ctx] returns [AlloyFormula r]
{
	AlloyFormula right; 
	List<AlloyFormula> rightList = new ArrayList<AlloyFormula>();
}
:
	r=andFormula[ctx] {rightList.add(r);} (OR right=andFormula[ctx] {rightList.add(right);})*
	{
		if ( rightList.size() > 1 ) {
			r = OrFormula.buildOrFormula(rightList.toArray(new AlloyFormula[0]));
		} 
	}
;

andFormula[JDynAlloyProgramParseContext ctx] returns [AlloyFormula r]
{
	AlloyFormula right; 
	List<AlloyFormula> rightList = new ArrayList<AlloyFormula>();
}
:
	r=atomFormula[ctx] {rightList.add(r);} (AND right=atomFormula[ctx] {rightList.add(right);})*
	{
		if ( rightList.size() > 1 ) {
			r = AndFormula.buildAndFormula(rightList.toArray(new AlloyFormula[0]));
		} 
	}
;
atomFormula [JDynAlloyProgramParseContext ctx] returns [AlloyFormula r]
:
r=predicateFormula[ctx]
| LPAREN r=alloyFormula[ctx] RPAREN
| r=callSpecFormula[ctx]
| r=quantifiedFormula[ctx]
| r=equalsFormula[ctx]
| NOT r=alloyFormula[ctx] {r = new NotFormula(r); }
;



//Quantifier Formula
quantifiedFormula [JDynAlloyProgramParseContext ctx] returns [QuantifiedFormula r]

{
	Operator operator;
	AlloyFormula alloyFormula1;
	JType alloyType1;
	
	List<String> names = new ArrayList<String>();
	List<AlloyExpression> sets = new ArrayList<AlloyExpression>();	
}
:
		(
		"some" {operator =   Operator.EXISTS;}
		| "all" {operator =  Operator.FOR_ALL;}
		| "lone" {operator = Operator.LONE;}
		| "no" {operator =   Operator.NONE;}
		| "one" {operator =  Operator.ONE;}
		)
		
		( id:IDENT COLON alloyType1=alloyType
			{ 	
				names.add(id.getText()); 
				sets.add(alloyType1.toAlloyExpr()); 
				AlloyVariable variable = AlloyVariable.buildAlloyVariable(id.getText());
				ctx.addAlloyVariable(variable);	
			} 

		)+
		
		PIPE
		
		LBRACE
		
		alloyFormula1=alloyFormula[ctx]
		
		RBRACE
		{ r = new QuantifiedFormula(operator, names, sets, alloyFormula1); }

;

callSpecFormula[JDynAlloyProgramParseContext ctx] returns [PredicateCallAlloyFormula  r]
{
	AlloyExpression parameter;
	List<AlloyExpression> parameterList = new ArrayList<AlloyExpression>();
	boolean isSuper = false;
	boolean isStatic = false;
}
: (SUPER {isSuper = true; })? (STATICCALLSPEC {isStatic = true; } | CALLSPEC {isStatic = false; }) programName:IDENT LBRACKET parameter=termExpression[ctx] {parameterList.add(parameter);} ( COMMA parameter=termExpression[ctx] {parameterList.add(parameter);} )* RBRACKET  
{r = new PredicateCallAlloyFormula(isSuper,programName.getText(),parameterList,isStatic);}
;

equalsFormula[JDynAlloyProgramParseContext ctx] returns [AlloyFormula r]
{
	AlloyExpression left; 
	AlloyExpression right; 
}
:
	left=termExpression[ctx] EQUALS right=termExpression[ctx]
	{
		r = new EqualsFormula(left, right); 
	}
;

// formal parameters declaration
variablesDeclaration returns [FormalParametersDeclaration r]
	{ r = new FormalParametersDeclaration(); }
	: (variablesDeclarationSingleType[r])? ( COMMA variablesDeclarationSingleType[r] )*   
	;

variablesDeclarationSingleType [FormalParametersDeclaration r]
	{ List<String> vars = null; String t = null; }
	: vars=variables COLON t=typeName
	{ ExpressionParser.extendAlloyTyping(r, vars, t); }
	;
	
variables returns [List<String> r] 	
	{ r = new LinkedList<String>(); }
	: id:IDENT { r.add(id.getText()); } ( COMMA id2:IDENT { r.add(id2.getText()); } )*
	;
	
// signature and relations type

typeName returns [String r]
	{ r = ""; String m=null; }
	: ( 
	       m=multiplicity {r=r+= " " + m + " "; } 
	       | LPAREN {r=r+="(";}
	       | RPAREN {r=r+=")";} 
	       | RARROW {r=r+="->";} 
	       | PLUS {r=r+="+";}
	       | id:IDENT {r=r+= id.getText();}
	   )+
	{
	}
	;

multiplicity returns [String r]
	{ r = null; }
	: "lone" { r = "lone"; }
	| "one"  { r = "one";  }
	| "some" { r = "some"; }
	| "seq" { r = "seq"; }	
	| "set" { r = "set"; }	
	;

// formula expressions

predicateFormula [IAlloyExpressionParseContext ctx] returns [PredicateFormula r]
	{ r = null; List<AlloyExpression> args = null; }
	: (aliasModuleId : IDENT SLASH)? pred:IDENT LBRACKET args=actualParams[ctx] RBRACKET { r = new PredicateFormula(aliasModuleId==null?null:aliasModuleId.getText(),pred.getText(), args); } 
	;

actualParams [IAlloyExpressionParseContext ctx] returns [List<AlloyExpression> r]
	{ r = new LinkedList<AlloyExpression>(); AlloyExpression p; }
	: ( p=termExpression[ctx] { r.add(p); } )? ( COMMA p=termExpression[ctx] { r.add(p); } )* 
	;

// term expressions

// precedence: union, join, product, override, 	
termExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; AlloyExpression other = null; }
	: r = joinExpression[ctx] ( PLUS other=termExpression[ctx] { r = new ExprUnion(r, other);} | EOF /* test */ )?	
	;

joinExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; AlloyExpression other = null; }
	: r = productExpression[ctx] ( DOT other=joinExpression[ctx] { r = new ExprJoin(r, other);} )? 
	;

productExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; AlloyExpression other = null; }
	: r = overrideExpression[ctx] ( RARROW other=productExpression[ctx] { r = new ExprProduct(r, other);} )? 
	;

overrideExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; AlloyExpression other = null; }
	: r = intersectionExpression[ctx] ( PLUSPLUS other=overrideExpression[ctx] { r = new ExprOverride(r, other);} )?
	;
 
intersectionExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; AlloyExpression other = null; }
	: r = atomicExpression[ctx] ( AMPERSTAND other=intersectionExpression[ctx] { r = new ExprIntersection(r, other);} )?
	;
	
atomicExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; }
	: id:IDENT { r = ExpressionParser.buildTerm(ctx, id, null); }
	| number:NUMBER { r = ExpressionParser.buildTerm(ctx, number, null); }
	| LPAREN r=termExpression[ctx] RPAREN
	| r=functionExpression[ctx]
	| TRUE {r = ExprConstant.buildExprConstant("true");}
	| FALSE {r = ExprConstant.buildExprConstant("false");} 
	;
	
functionExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; List<AlloyExpression> args = null; }
	: (aliasModuleId:IDENT SLASH)? functionId:IDENT LBRACKET args=actualParams[ctx] RBRACKET { r = new ExprFunction(aliasModuleId==null ? null : aliasModuleId.getText(),functionId.getText(), args); } 
	;
		
class JDynAlloyLexer extends Lexer;
options {
  testLiterals=false;
//  greedy=false;
  defaultErrorHandler=false;
  k=8;

}


DOT		:	'.'	;
COLON	:	':'	;
SEMICOLON		:	';'	;
EQUALS	:	'='	;
COMMA	:	','	;
PLUS	: 	'+' ;
PLUSPLUS: 	"++" ;
STAR	:	'*' ;
//MINUS	:	'-'	;
//EXCLAM	:	'!' ;
//DAMP	:	"&&";
AMPERSTAND		:	'&' ;
//DPIPE	:	"||";
PIPE	:	'|' ;
//NEWFLO	:	'~' ;
LPAREN	:	'('	;
RPAREN	:	')'	;
LBRACE	:	'{'	;
RBRACE	:	'}'	;
LBRACKET:	'['	;
RBRACKET:	']'	;
RARROW	:	"->";
QUESTION:	'?';
SLASH:      '/';
ASSIGNMENT:     ":=";
LT: "<";
GT: ">";
//program declaration
PROGRAM: "program";
VIRTUAL: "virtual";
SPECIFICATION: "Specification";
IMPLEMENTATION: "Implementation";

//program specification keyword
SPECCASE: "SpecCase";
SPECCASE_ID: "#" ('0'..'9')+;

REQUIRES: "requires";
ENSURES: "ensures";
MODIFIES: "modifies";
OBJECT_INVARIANT: "object_invariant"; 
CLASS_INVARIANT: "class_invariant"; 
OBJECT_CONSTRAINT: "object_constraint";
CLASS_CONSTRAINT: "class_constraint";
REPRESENTS: "represents";

//statements keyword
VAR: 	{LA(4)==' ' || LA(4)=='\n' || LA(4)=='\r' || LA(4)==EOF_CHAR}? "var" ;
ASSUME: "assume";
ASSERT: "assert";
SKIP: "skip";
CREATE_OBJECT: "createObject";
IF: "if";
ELSE: "else";
CALL: {LA(5)==' ' || LA(5)=='\n' || LA(5)=='\r' || LA(5)==EOF_CHAR}? "call";
SUPER: "super";
WHILE: "while";
HAVOC: "havoc";

LOOP_INVARIANT: "loop_invariant";

//represents keywords

SUCH_THAT: "such that";

//formula
AND:{LA(4)==' ' || LA(4)=='\n' || LA(4)=='\r' || LA(4)==EOF_CHAR}?  "and"; //the predicade make parser able parse identifier with "AND" prefix
NOT:{LA(4)==' ' || LA(4)=='\n' || LA(4)=='\r' || LA(4)==EOF_CHAR}?  "not"; //idem "and"
OR: {LA(3)==' ' || LA(3)=='\n' || LA(3)=='\r' || LA(3)==EOF_CHAR}? "or"; //idem "and"

TRUE:{LA(5)==' ' || LA(5)=='\n' || LA(5)=='\r' || LA(5)==EOF_CHAR}? "true"; //idem "and"
FALSE:{LA(5)==' ' || LA(5)=='\n' || LA(5)=='\r' || LA(5)==EOF_CHAR}? "false"; //idem "and"

CALLSPEC: "callSpec";
STATICCALLSPEC: "staticCallSpec";
IMPLIES: "implies";
// Comments -- ignored 
COMMENT 
	: "--" (~('\n'|'\r'))*
	{ _ttype = Token.SKIP; }
	;
	
COMMENT_SLASH_SLASH
  : "//" (~('\n'|'\r'))*
    { $setType(Token.SKIP); }
  ;
	
// multiple-line comments
COMMENT_ML
  : "/*"
    (               /* '\r' '\n' can be matched in one alternative or by matching
                       '\r' in one iteration and '\n' in another. I am trying to
                       handle any flavor of newline that comes in, but the language
                       that allows both "\r\n" and "\r" and "\n" to all be valid
                       newline is ambiguous. Consequently, the resulting grammar
                       must be ambiguous. I'm shutting this warning off.
                    */
      options {
        generateAmbigWarnings=false;
      }
      :  { LA(2)!='/' }? '*'
      | '\r' '\n' {newline();}
      | '\r' {newline();}
      | '\n' {newline();}
      | ~('*'|'\n'|'\r')
    )*
    "*/"
    {$setType(Token.SKIP);}
;

// Whitespace -- ignored
WS	:	(	' ' 
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	options {generateAmbigWarnings=false;}
			:	"\r\n"	// Evil DOS
			|	'\r'	// Macintosh
			|	'\n'	// Unix (the right way)
			)
			{ newline(); }
		)+
		{ _ttype = Token.SKIP; }
	;
    
IDENT
  options {
  	testLiterals=true;	
  }
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ('\'')?
  ;


NUMBER : ('0'..'9')+;

header {
package ar.uba.dc.rfm.alloy.parser;

import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;
import ar.uba.dc.rfm.alloy.ast.expressions.*;
import ar.uba.dc.rfm.alloy.AlloyVariable;

import ar.uba.dc.rfm.alloy.parser.FormalParametersDeclaration;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;	
}

class ExpressionParser extends Parser;

options {
	buildAST = false;
	k = 4;
}

{
	public static AlloyExpression buildTerm(IAlloyExpressionParseContext ctx, Token t, Token aliasModuleIdToken) {
		String text = t.getText();
		if (ctx.isIntLiteral(text))
		    return new ExprIntLiteral(ctx.getIntLiteral(text));
		if (ctx.isVariableName(text))
			return new ExprVariable(ctx.getAlloyVariable(text));
		else
			return new ExprConstant(aliasModuleIdToken==null? null: aliasModuleIdToken.getText(), text); 
	}
	
	public static AlloyExpression buildPreTerm(IAlloyExpressionParseContext ctx, Token t) {
    String text = t.getText();
	  return new ExprVariable(AlloyVariable.buildPreStateVariable(text));
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
	
}

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


/*
typeName returns [String r]
	{ r = null;  String r2=null; }
	:(r=multiplicity
	| LPAREN {r="(";}
	| RPAREN {r=")";}
	| ARROW {r="->";}
    | PLUS {r="+";}
	| id:IDENT {r=id.getText();} )
	(r2=typeName)?
	{if (r2!=null) 
	  r=r + " " + r2;
	}
	;
*/

typeName returns [String r]
	{ r = ""; String m=null; }
	: ( 
	       m=multiplicity {r=r+=" " + m; } 
	       | LPAREN {r=r+=" (";}
	       | RPAREN {r=r+=" )";} 
	       | RARROW {r=r+=" ->";} 
	       | PLUS {r=r+=" +";}
	       | id:IDENT {r=r+=" " + id.getText();} 
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
	: r = exprIntersectionExpression[ctx] ( PLUSPLUS other=overrideExpression[ctx] { r = new ExprOverride(r, other);} )?
	;

exprIntersectionExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; AlloyExpression other = null; }
	: r = atomicExpression[ctx] ( AMP other=exprIntersectionExpression[ctx] { r = new ExprIntersection(r, other);} )?
	;	
	
	
atomicExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; }
	: id:IDENT { r = ExpressionParser.buildTerm(ctx, id, null); }
	| number:NUMBER { r = ExpressionParser.buildTerm(ctx, number, null); }
	| LPAREN r=termExpression[ctx] RPAREN
	| r=functionOrIfExpression[ctx]
	| BACKSLASH_PRE LBRACKET id2:IDENT RBRACKET { r = ExpressionParser.buildPreTerm(ctx, id2); }
	;

functionOrIfExpression [IAlloyExpressionParseContext ctx] returns [AlloyExpression r]
	{ r = null; List<AlloyExpression> args = null; AlloyExpression thenPart = null; AlloyExpression elsePart = null; boolean isIfExpression = false;}
	: (aliasModuleId:IDENT SLASH)? functionId:IDENT LBRACKET args=actualParams[ctx] RBRACKET 
	( IF_EXPR thenPart=termExpression[ctx] ELSE elsePart=termExpression[ctx] {isIfExpression = true;})?   
	{ 
		if (isIfExpression) {
		  PredicateFormula pred = new PredicateFormula(aliasModuleId==null?null:aliasModuleId.getText(),functionId.getText(), args);
		  r = new ExprIfCondition(pred, thenPart, elsePart);
		} else {
			r = new ExprFunction(aliasModuleId==null ? null : aliasModuleId.getText(),functionId.getText(), args); 
		} 
	}
	;

class ExpressionLexer extends Lexer;
options {
  testLiterals=false;
  k=4;
}

VAL     :   '#' ;
DOT		:	'.'	;
COLON	:	':'	;
SEMICOLON		:	';'	;
EQUALS	:	'='	;
COMMA	:	','	;
PLUS	: 	'+' ;
PLUSPLUS: 	"++" ;
STAR	:	'*' ;
MINUS	:	'-'	;
//EXCLAM	:	'!' ;
//DAMP	:	"&&";
AMP		:	'&' ;
//DPIPE	:	"||";
//PIPE	:	'|' ;
//NEWFLO	:	'~' ;
LPAREN	:	'('	;
RPAREN	:	')'	;
LBRACE	:	'{'	;
RBRACE	:	'}'	;
LBRACKET:	'['	;
RBRACKET:	']'	;
RARROW	:	"->";
IF_EXPR     :   "=>";
ELSE    : "else";
SLASH   :   '/' ;
BACKSLASH_PRE :  "\\pre";

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
  options {testLiterals=true;}
  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ('\'')?
  ;
  
MODULE_ID
  options {testLiterals=true;}
  : IDENT ('/' IDENT)*
  ;
  
NUMBER : ('0'..'9')+;
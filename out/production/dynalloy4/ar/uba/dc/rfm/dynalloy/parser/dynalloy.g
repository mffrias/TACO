header {
package ar.uba.dc.rfm.dynalloy.parser;

import java.util.ArrayList;
import ar.uba.dc.rfm.alloy.VariableId;
import ar.uba.dc.rfm.dynalloy.ast.*;	
import ar.uba.dc.rfm.dynalloy.ast.programs.*;
import ar.uba.dc.rfm.dynalloy.parser.splitter.Position;
import ar.uba.dc.rfm.alloy.ast.expressions.*;
import ar.uba.dc.rfm.alloy.parser.*;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.AlloyCheckCommand;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;
import ar.uba.dc.rfm.dynalloy.TestPredicateLabel;
import java.io.StringReader;
import java.util.Collections;

import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;		
}

class DynAlloyANTLRParser extends ExpressionParser;

options {
	k = 4;
	buildAST = true;
	defaultErrorHandler=false;
	ASTLabelType = "ar.uba.dc.rfm.dynalloy.parser.DynalloyAST";
}

{
	private AlloyTyping fields = new AlloyTyping();
	private Position initialPosition;
	
	public void bindFields(AlloyTyping _fields) {
		fields = _fields;
	}
	
	public AlloyTyping getFields() {
		return fields;
	}
	
	private Position positionFromAST() {
		AST ast = getAST();
		return (new Position(ast.getLine(), ast.getColumn()))
			.add(initialPosition);
	}
	
	private Position ifTokenPosition() {
		AST ast = DynalloyAST.pollIfToken();
		return (new Position(ast.getLine(), ast.getColumn()))
			.add(initialPosition);
	}

		private Position whileTokenPosition() {
		AST ast = DynalloyAST.pollWhileToken();
		return (new Position(ast.getLine(), ast.getColumn()))
			.add(initialPosition);
	}
	
	public void setInitialPosition(Position initialPosition) {
		this.initialPosition = initialPosition;
	}
	
}

alloySignature[]
	{ FormalParametersDeclaration formalParams=null; }
	: ("abstract")? ("one")? ("sig" signatureId:IDENT (("extends"|"in") typeName)? LBRACE  formalParams=variablesDeclaration RBRACE)?
	{
		if (formalParams!=null) {
	  		for (AlloyVariable v: formalParams.getTyping()) {
	  	   	//if (fields.contains(v))
	  	   	//	throw new AmbiguosFieldDeclarationException("cannot handle multiple field declarations.");
	  	   	fields.put(v,signatureId.getText() + "->" + "(" + formalParams.getTyping().get(v) + ")");
	  		}
		}
	} 
 	;

alloyModule returns [String moduleId]
	{ }
	: "module" moduleId=modulePath
 	;

alloyOpen returns [OpenDeclaration r] 
	{ String moduleId = null; String alias = null; }
	: "open" moduleId=modulePath ("as" aliasModuleId:IDENT {alias = aliasModuleId.getText();})?  
	{
		r = new OpenDeclaration(moduleId, alias);
	} 
 	;

modulePath returns [String path]
	{ }
	: i:IDENT { path = i.getText(); } ( SLASH j:IDENT { path = path + "/" + j.getText(); } )* 
	;
		
typescope [AlloyCheckCommand ctx]
	{ boolean isExactly = false; }
	: ("exactly" {isExactly = true;})? scope:NUMBER signature:IDENT
	{ ctx.put(signature.getText(), isExactly, Integer.parseInt(scope.getText())); }
	;	 
	
dynalloyAction returns [ActionDeclaration r]
	{
		r = null;
		FormalParametersDeclaration formalParams;
	  	PredicateFormula pre = null, post = null;				
	}
	:
		("action"|"act") actionId:IDENT LBRACKET formalParams=variablesDeclaration RBRACKET
		{
			Set<AlloyVariable> variables = formalParams.getTyping().varSet();
		    IAlloyExpressionParseContext ctxPre = new DynalloyProgramParseContext(variables,Collections.<AlloyVariable>emptySet(),false);
		    IAlloyExpressionParseContext ctxPost = new DynalloyProgramParseContext(variables,Collections.<AlloyVariable>emptySet(),true);	  	 			
		}
		LBRACE
		  "pre" LBRACE pre=predicateFormula[ctxPre] RBRACE
		  "post" LBRACE post=predicateFormula[ctxPost] RBRACE
		RBRACE
	{
		r = new ActionDeclaration(actionId.getText(),
			 formalParams.getParameters(),
			 pre, post, formalParams.getTyping());
	}
	;
	
dynalloyProgram returns [ProgramDeclaration r]
	{
		r = null;
		FormalParametersDeclaration formalParams;
		FormalParametersDeclaration localVariables = null;
	  	DynalloyProgram prog;	
	  	Set<AlloyVariable> ctxVariables = null;			
	}
	:
		("program"|"prog") programId:IDENT LBRACKET formalParams=variablesDeclaration RBRACKET 
		("var" LBRACKET  localVariables=variablesDeclaration RBRACKET )? 
		{
			ctxVariables = new HashSet<AlloyVariable>();
			ctxVariables.addAll(formalParams.getTyping().varSet());
			if (localVariables!=null)
						ctxVariables.addAll(localVariables.getTyping().varSet());
	      IDynalloyProgramParseContext ctxProgram = new DynalloyProgramParseContext(ctxVariables,Collections.<AlloyVariable>emptySet(),false);
		}
		LBRACE
          prog=program[ctxProgram]
		RBRACE
	{
		AlloyTyping typing = new AlloyTyping();
		for(AlloyVariable v: formalParams.getTyping().varSet()) {
			typing.put(v,formalParams.getTyping().get(v));
		}
		if (localVariables!=null) {
		   for(AlloyVariable v: localVariables.getTyping().varSet()) {
			typing.put(v,localVariables.getTyping().get(v));
		   }
		}
		
		r = new ProgramDeclaration(programId.getText(),
			 formalParams.getParameters(),
			 localVariables == null ? Collections.<VariableId>emptyList() : localVariables.getParameters(),
			 prog, typing, new ArrayList<AlloyFormula>(), new AlloyTyping());
	}
	;	
	
dynalloyAssertion returns [AssertionDeclaration r]
	{ 
	  r = null; 
	  FormalParametersDeclaration formalParams;
	  PredicateFormula pre = null, post = null;
	  DynalloyProgram prog = null;
	}
	: "assertCorrectness" assertionId:IDENT LBRACKET formalParams=variablesDeclaration RBRACKET
	  {
	  	Set<AlloyVariable> variables = formalParams.getTyping().varSet();
	    IAlloyExpressionParseContext ctxPre = new DynalloyProgramParseContext(variables,Collections.<AlloyVariable>emptySet(),false);
	    IDynalloyProgramParseContext ctxProgram = new DynalloyProgramParseContext(variables,Collections.<AlloyVariable>emptySet(),false);
	    IAlloyExpressionParseContext ctxPost = new DynalloyProgramParseContext(variables,Collections.<AlloyVariable>emptySet(),true);
	    Position prePosition = null;
	    Position postPosition = null;	  	 
	  }
	  LBRACE
	  
	  "pre" (EQUALS)? LBRACE pre=predicateFormula[ctxPre] RBRACE { prePosition = positionFromAST(); }
	  "program" (EQUALS)? LBRACE prog=program[ctxProgram] RBRACE 
	  "post" (EQUALS)? LBRACE post=predicateFormula[ctxPost] RBRACE { postPosition = positionFromAST(); }
	  
	  RBRACE
	  {
	    pre.setPosition(prePosition);
	    post.setPosition(postPosition);
	  	r = new AssertionDeclaration(assertionId.getText(), formalParams.getTyping(), pre, prog, post);
	  } 
	;

program [IDynalloyProgramParseContext ctx] returns [DynalloyProgram r]
	{ r = null; }	
	: r=choiceProgram[ctx]
	  // for testing 
	  (EOF)?
	;
		
choiceProgram [IDynalloyProgramParseContext ctx] returns [DynalloyProgram r]
	{ r = null; DynalloyProgram other = null; }
	: r=compositionProgram[ctx] ( PLUS other=choiceProgram[ctx] {r = new Choice(r, other, positionFromAST()); } )?
	;

compositionProgram [IDynalloyProgramParseContext ctx] returns [DynalloyProgram r]
	{ r = null; DynalloyProgram other = null; }
	: r=closureProgram[ctx] ( SEMICOLON other=compositionProgram[ctx] {r = new Composition(r, other, positionFromAST()); } )?
	;
	
closureProgram [IDynalloyProgramParseContext ctx] returns [DynalloyProgram r]
	{ r = null; }
	: r=atomicPrograms[ctx] ( STAR {r = new Closure(r, positionFromAST());} )?
	;
	
testPredLabel returns [TestPredicateLabel r]
  {
    r = null;
    boolean isLblpos = false;
  }	
  : ("lblneg" {isLblpos = false; } | "lblpos" {isLblpos = true; })  labelId:IDENT
  {
    r = new TestPredicateLabel(isLblpos, labelId.getText());
  }
  ;
	
atomicPrograms [IDynalloyProgramParseContext ctx] returns [DynalloyProgram r]
	{ 
	  r = null; 
	  List<AlloyExpression> p; 
	  PredicateFormula pred;  
	  DynalloyProgram other = null; 
	  AlloyExpression expr=null; 
	  TestPredicateLabel label = null; 
	} 
	: skipProgram { r = new Skip(positionFromAST()); }
	| LBRACKET pred=predicateFormula[ctx] RBRACKET QUESTION { r = new TestPredicate(pred, positionFromAST()); }
	| (aliasModuleId:IDENT SLASH)? actionId:IDENT LBRACKET p=actualParams[ctx] RBRACKET { r = new InvokeAction(aliasModuleId==null ? null : aliasModuleId.getText(), actionId.getText(), p, positionFromAST()); }
	| "call" (aliasModuleId2:IDENT SLASH)? programId:IDENT LBRACKET p=actualParams[ctx] RBRACKET { r = new InvokeProgram(aliasModuleId2==null ? null : aliasModuleId2.getText(), programId.getText(), p, positionFromAST()); }
	| LPAREN r=choiceProgram[ctx] RPAREN
	| variableId:IDENT ASSIGNMENT expr=termExpression[ctx] { r = new Assigment(ExprVariable.buildExprVariable(variableId.getText()),expr,positionFromAST()); }
	| "assume" pred=predicateFormula[ctx] ( label=testPredLabel )? 
	       { 
	          TestPredicate t = new TestPredicate(pred, positionFromAST()); 
	          if (label!=null) {
	            t.setLabel(label);
	          }
	          r = t;
	       }
	| "while" pred=predicateFormula[ctx]  "do" LBRACE r=choiceProgram[ctx] RBRACE  {
	       Position while_pos = whileTokenPosition();
	       TestPredicate t1 = new TestPredicate(pred, while_pos);
	       TestPredicate t2 = new TestPredicate(pred, false, while_pos);
		   //r = new Composition(new Closure(new Composition(t1,r, positionFromAST()), positionFromAST()), t2, positionFromAST()); 
	       
		   r = new WhileProgram(pred, r, positionFromAST()); 
		}
	| "repeat" LBRACE r=choiceProgram[ctx] RBRACE { r = new Closure(r, positionFromAST()); }
	| "if" pred=predicateFormula[ctx] ( label=testPredLabel )? LBRACE r=choiceProgram[ctx] RBRACE ELSE LBRACE other=choiceProgram[ctx] RBRACE 
	       { 
	       	 Position ifPos = ifTokenPosition();
	       	 TestPredicate t1 = new TestPredicate(pred, ifPos);

            if (label!=null) {
              t1.setLabel(label);
            }

	       	 r = new Composition(t1,r, positionFromAST());
	       	  
	       	 TestPredicate t2 = new TestPredicate(pred, false, ifPos);

            if (label!=null) {
              TestPredicateLabel label_false;
              label_false = new TestPredicateLabel(!label.isLblpos(), label.getLabelId()); 
              t2.setLabel(label_false);
            }

	       	 other = new Composition(t2,other, positionFromAST());
	       	 
	       	 r = new Choice(r,other, positionFromAST());
	       }
	;

skipProgram 
: 
   "skip" 
;
	
class DynalloyLexer extends Lexer;
options {
  testLiterals=false;
//  greedy=false;
  k=4;
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
QUESTION:	'?';
SLASH:      '/';
ASSIGNMENT:     ":=";
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


NUMBER : ('0'..'9')+;

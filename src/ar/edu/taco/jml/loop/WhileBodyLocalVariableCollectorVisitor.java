package ar.edu.taco.jml.loop;

import java.util.ArrayList;

import org.multijava.mjc.JVariableDeclarationStatement;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class WhileBodyLocalVariableCollectorVisitor extends JmlAstClonerStatementVisitor {

	
	private ArrayList<JVariableDeclarationStatement> localVars = new ArrayList<JVariableDeclarationStatement>();
	
	public ArrayList<JVariableDeclarationStatement> getLocalVars(){
		return this.localVars;
	}
	
	public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
		this.localVars.add(self);
		this.getStack().push(self);

	}	
}

package ar.edu.taco.simplejml;

import java.util.LinkedList;
import java.util.List;

import org.jmlspecs.checker.JmlMethodDeclaration;
import org.multijava.mjc.CStdType;
import org.multijava.mjc.CVoidType;

import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.helpers.CTypeAdapter;
import ar.edu.taco.simplejml.helpers.MethodDeclarationSolver;
import ar.edu.taco.simplejml.helpers.NullDerefSolver;

/**
 * An empty class: its only member is a constructor that ignores its argument, and its imports
 * (program declarations, null-dereference checks, ...) are unused. Nothing in the three
 * repositories uses it (2026-10-19). No state.
 *
 * <p>Unclear: nothing uses it; whether it is still needed is an open question.
 */
public class JDynAlloyPureMethodVisitor  {

	
	public JDynAlloyPureMethodVisitor(JmlMethodDeclaration jmlMethodDeclaration){}
	
}

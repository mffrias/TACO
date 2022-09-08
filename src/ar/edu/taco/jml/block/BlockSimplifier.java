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
package ar.edu.taco.jml.block;

import org.jmlspecs.checker.JmlLoopStatement;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JForStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JWhileStatement;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * The simplifier motivation is avoid implement AST Visitor for Method Scope and
 * is used , as precondition, in ConditionSimplifier, that all statement are
 * contained inside a block
 * 
 * 
 */
public class BlockSimplifier extends JmlAstClonerStatementVisitor {

    //	private int forVariableCount = 0;

    /** visits the given JmlLoopStatement. Such a statement originates from a while loop with a variant function. */
    public void visitJmlLoopStatement(/* @non_null */JmlLoopStatement self) {
        self.loopStmt().accept(this);
        JStatement newStatement = (JStatement)this.getStack().pop();
        JmlLoopStatement newSelf = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), newStatement, self.getComments());
        this.getStack().push(newSelf);
    }


    /** Visits the given while statement. */
    public void visitWhileStatement(/* @non_null */JWhileStatement self) {

        self.body().accept(this);
        JBlock newBody;
        if (self.body() instanceof JBlock) {
            newBody = (JBlock) this.getStack().pop();
        } else {
            newBody = ASTUtils.createBlockStatement( (JStatement) this.getStack().pop() );
        }
        JWhileStatement newSelf = new JWhileStatement(self.getTokenReference(), self.cond(), ASTUtils.createBlockStatement(newBody), self.getComments());
        this.getStack().push(newSelf);
    }

    /** Visits the given if statement. */
    public void visitIfStatement(/* @non_null */JIfStatement self) {
        this.getStack().push(self);
        self.thenClause().accept(this);
        JStatement newThen = (JStatement) this.getStack().pop();
        JStatement newElse = null;
        if (self.elseClause() != null) {
            self.elseClause().accept(this);
            newElse = (JStatement) this.getStack().pop();
        }

        this.getStack().push(
                new JIfStatement(self.getTokenReference(), self.cond(), ASTUtils.createBlockStatement(newThen), ASTUtils.createBlockStatement(newElse), self
                        .getComments()));
    }

    /** Visits the given for statement. */
    public void visitForStatement(/* @non_null */JForStatement self) {

        JStatement newInit = null;
        self.init().accept(this);
        newInit = (JStatement) this.getStack().pop();

        JStatement newIncr;
        if (self.incr() == null) {
            newIncr = null;
        } else {
            self.incr().accept(this);
            newIncr = (JStatement) this.getStack().pop();
        }

        self.body().accept(this);
        JStatement newBody = (JStatement) this.getStack().pop();

        this.getStack().push(
                new JForStatement(self.getTokenReference(), newInit, self.cond(), newIncr, ASTUtils.createBlockStatement(newBody), self.getComments()));
    }


    @Override 
    public void visitDoStatement(JDoStatement self){
        self.body().accept(this);
        JBlock newBody;
        if (self.body() instanceof JBlock) {
            newBody = (JBlock) this.getStack().pop();
        } else {
            newBody = ASTUtils.createBlockStatement( (JStatement) this.getStack().pop() );
        }
        JDoStatement newSelf = new JDoStatement(self.getTokenReference(), self.cond(), newBody, self.getComments());
        this.getStack().push(newSelf);
    }



    @Override
    public void visitConstructorBlock(JConstructorBlock self) {	    
        super.visitConstructorBlock(self);
        JConstructorBlock visitedSelf = (JConstructorBlock) this.getStack().pop();
        JBlock block = ASTUtils.createBlockStatement(visitedSelf.body());
        JConstructorBlock newSelf = new JConstructorBlock(self.getTokenReference(), new JStatement[] {block});
        this.getStack().push(newSelf);

    }

    @Override
    public void visitAssertStatement(JAssertStatement self){
        JAssertStatement newSelf = new JAssertStatement(self.getTokenReference(), self.predicate(), null);
        this.getStack().push(newSelf);

    }

}
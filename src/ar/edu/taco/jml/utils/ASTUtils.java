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
package ar.edu.taco.jml.utils;

import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JCompoundStatement;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionListStatement;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JStatement;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;

/**
 * Static helpers that build MultiJava AST nodes for the JML simplification passes (the
 * subpackages of {@code jml/}) and for the cloners and visitors of {@code utils/jml/} and
 * {@code simplejml/}: {@code if} statements, assignments, expression statements and blocks. The
 * nodes get the token reference of one of the arguments and no comments, and are not type
 * checked. No state.
 */
public class ASTUtils {

    /**
     * Returns {@code if (condition) { thenStatement } else { elseStatement }}: each branch is
     * wrapped in a new block, even if it is one already, and there is no {@code else} branch
     * when {@code elseStatement} is {@code null}.
     *
     * @param condition the condition; its token reference is used for the new nodes
     * @param thenStatement the statement of the {@code then} branch
     * @param elseStatement the statement of the {@code else} branch, or {@code null}
     * @param comments ignored
     * @return the new {@code if} statement
     */
    public static JIfStatement createIfStatement(JExpression condition, JStatement thenStatement, JStatement elseStatement, JavaStyleComment[] comments) {
	JStatement newThenStatement = new JBlock(condition.getTokenReference(), new JStatement[] { thenStatement }, null);
	JStatement newElseStatement = elseStatement == null ? null : new JBlock(condition.getTokenReference(), new JStatement[] { elseStatement }, null);
	return new JIfStatement(condition.getTokenReference(), condition, newThenStatement, newElseStatement, null);
    }

    /**
     * Same as {@link #createIfStatement(JExpression, JStatement, JStatement, JavaStyleComment[])}.
     *
     * @param condition the condition
     * @param thenStatement the statement of the {@code then} branch
     * @param elseStatement the statement of the {@code else} branch, or {@code null}
     * @return the new {@code if} statement
     */
    public static JIfStatement createIfStatement(JExpression condition, JStatement thenStatement, JStatement elseStatement) {
	return createIfStatement(condition, thenStatement, elseStatement, null);
    }

    /**
     * Returns the statement {@code left = right;}.
     *
     * @param left the assigned expression
     * @param right the value
     * @return the new expression statement
     */
    public static JStatement createAssignamentStatement(JExpression left, JExpression right) {
	JExpression expression = createAssignamentExpression(left, right);
	return createExpressionStatement(expression);
    }

    /**
     * Returns the expression {@code left = right}.
     *
     * @param left the assigned expression
     * @param right the value
     * @return the new assignment
     */
    public static JExpression createAssignamentExpression(JExpression left, JExpression right) {
	return new JAssignmentExpression(left.getTokenReference(), left, right);
    }

    /**
     * Returns {@code expression;}, the expression as a statement.
     *
     * @param expression the expression
     * @return the new statement
     */
    public static JExpressionStatement createExpressionStatement(JExpression expression) {
	return new JExpressionStatement(expression.getTokenReference(), expression, null);
    }

    /**
     * Returns a block with one expression statement per expression of the list: {@code a, b;}
     * becomes {@code { a; b; }}.
     *
     * @param expressionListStatement the list (as in the parts of a {@code for} header)
     * @return the new block
     */
    public static JBlock createBlockStatement(JExpressionListStatement expressionListStatement) {
	JStatement[] newStatement = new JStatement[expressionListStatement.getExpressions().length];
	for (int i = 0; i < expressionListStatement.getExpressions().length; i++) {
	    JExpression expressionStatement = expressionListStatement.getExpressions()[i];
	    newStatement[i] = ASTUtils.createExpressionStatement(expressionStatement);
	}
	return ASTUtils.createBlockStatement(newStatement);
    }

    /**
     * Returns the statement as a block: {@code null} for {@code null}; for a block, the block
     * itself (not a copy); for an expression list or a compound statement, a new block of its
     * statements; for any other statement, a new block that holds it.
     *
     * @param statement the statement, or {@code null}
     * @return the block, or {@code null}
     */
    public static JBlock createBlockStatement(JStatement statement) {
	if (statement == null) {
	    return null;
	} else if (statement instanceof JBlock) {
	    return (JBlock) statement;
	} else if (statement instanceof JExpressionListStatement) {
	    return createBlockStatement((JExpressionListStatement) statement);
	} else if (statement instanceof JCompoundStatement) {
	    return createBlockStatement((JCompoundStatement) statement);
	} else {
	    return createBlockStatement(new JStatement[] { statement });
	}
    }

    /**
     * Returns one new block with the statements of both: each is first made a block by
     * {@link #createBlockStatement(JStatement)}, then the two are joined by
     * {@link #createBlockStatement(JBlock, JBlock)}. Neither may be {@code null}
     * ({@code NullPointerException}).
     *
     * @param statement1 the first statement
     * @param statement2 the second statement
     * @return the new block
     */
    public static JBlock createBlockStatement(JStatement statement1, JStatement statement2) {
	JBlock block1 = createBlockStatement(statement1);
	JBlock block2 = createBlockStatement(statement2);
	return createBlockStatement(block1, block2);
    }

    /**
     * Returns a new block with the statements of {@code statement1} followed by those of
     * {@code statement2}, one level deep: nested blocks stay as they are, and expression lists and
     * compound statements among them become blocks.
     *
     * @param statement1 the first block
     * @param statement2 the second block
     * @return the new block
     */
    public static JBlock createBlockStatement(JBlock statement1, JBlock statement2) {
	JStatement[] statements = new JStatement[statement1.body().length + statement2.body().length];
	int i = 0;
	for (int j = 0; j < statement1.body().length; j++) {
	    JStatement statement = statement1.body()[j];
	    if (statement instanceof JExpressionListStatement) {
		statements[i] = createBlockStatement((JExpressionListStatement) statement);
	    } else if (statement instanceof JCompoundStatement) {
		statements[i] = createBlockStatement((JCompoundStatement) statement);
	    } else {
		statements[i] = statement;
	    }
	    i++;
	}

	for (int j = 0; j < statement2.body().length; j++) {
	    JStatement statement = statement2.body()[j];
	    if (statement instanceof JExpressionListStatement) {
		statements[i] = createBlockStatement((JExpressionListStatement) statement);
	    } else if (statement instanceof JCompoundStatement) {
		statements[i] = createBlockStatement((JCompoundStatement) statement);
	    } else {
		statements[i] = statement;
	    }
	    i++;
	}	
	return createBlockStatement(statements);
    }

    /**
     * Returns a new block of the given statements (the array itself, not a copy), with the token
     * reference of the first statement, or {@code TokenReference.NO_REF} if there is none.
     *
     * @param statements the statements
     * @return the new block
     */
    public static JBlock createBlockStatement(JStatement[] statements) {
	TokenReference where;
	if (statements.length >= 1) {
	    where = statements[0].getTokenReference();
	} else {
	    where = TokenReference.NO_REF;
	}
	return new JBlock(where, statements, null);
    }

    /**
     * Returns a new block with the statements of a compound statement (its array, not a copy).
     *
     * @param statement the compound statement
     * @return the new block
     */
    public static JBlock createBlockStatement(JCompoundStatement statement) {
	return new JBlock(statement.getTokenReference(), statement.body(), null);
    }

}

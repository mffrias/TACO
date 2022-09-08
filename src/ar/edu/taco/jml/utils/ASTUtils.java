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

public class ASTUtils {

    public static JIfStatement createIfStatement(JExpression condition, JStatement thenStatement, JStatement elseStatement, JavaStyleComment[] comments) {
	JStatement newThenStatement = new JBlock(condition.getTokenReference(), new JStatement[] { thenStatement }, null);
	JStatement newElseStatement = elseStatement == null ? null : new JBlock(condition.getTokenReference(), new JStatement[] { elseStatement }, null);
	return new JIfStatement(condition.getTokenReference(), condition, newThenStatement, newElseStatement, null);
    }

    public static JIfStatement createIfStatement(JExpression condition, JStatement thenStatement, JStatement elseStatement) {
	return createIfStatement(condition, thenStatement, elseStatement, null);
    }

    public static JStatement createAssignamentStatement(JExpression left, JExpression right) {
	JExpression expression = createAssignamentExpression(left, right);
	return createExpressionStatement(expression);
    }

    public static JExpression createAssignamentExpression(JExpression left, JExpression right) {
	return new JAssignmentExpression(left.getTokenReference(), left, right);
    }

    public static JExpressionStatement createExpressionStatement(JExpression expression) {
	return new JExpressionStatement(expression.getTokenReference(), expression, null);
    }

    public static JBlock createBlockStatement(JExpressionListStatement expressionListStatement) {
	JStatement[] newStatement = new JStatement[expressionListStatement.getExpressions().length];
	for (int i = 0; i < expressionListStatement.getExpressions().length; i++) {
	    JExpression expressionStatement = expressionListStatement.getExpressions()[i];
	    newStatement[i] = ASTUtils.createExpressionStatement(expressionStatement);
	}
	return ASTUtils.createBlockStatement(newStatement);
    }

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

    public static JBlock createBlockStatement(JStatement statement1, JStatement statement2) {
	JBlock block1 = createBlockStatement(statement1);
	JBlock block2 = createBlockStatement(statement2);
	return createBlockStatement(block1, block2);
    }

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

    public static JBlock createBlockStatement(JStatement[] statements) {
	TokenReference where;
	if (statements.length >= 1) {
	    where = statements[0].getTokenReference();
	} else {
	    where = TokenReference.NO_REF;
	}
	return new JBlock(where, statements, null);
    }

    public static JBlock createBlockStatement(JCompoundStatement statement) {
	return new JBlock(statement.getTokenReference(), statement.body(), null);
    }

}

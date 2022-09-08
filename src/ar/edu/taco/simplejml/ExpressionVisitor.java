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
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlFormalParameter;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlSpecExpression;
import org.multijava.mjc.CArrayType;
import org.multijava.mjc.CType;
import org.multijava.mjc.Constants;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JCharLiteral;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JConditionalAndExpression;
import org.multijava.mjc.JConditionalExpression;
import org.multijava.mjc.JConditionalOrExpression;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JInstanceofExpression;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JNewArrayExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JRelationalExpression;
import org.multijava.mjc.JShiftExpression;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JSuperExpression;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.ast.AlloyIntArrayFactory;
import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JCreateObject;
import ar.edu.jdynalloy.ast.JProgramCall;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.ast.JavaPrimitiveIntValueArrayFactory;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.jdynalloy.xlator.JTypeHelper;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.edu.taco.simplejml.helpers.CTypeAdapter;
import ar.edu.taco.simplejml.helpers.ExpressionSolver;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;
import ar.edu.taco.simplejml.methodinfo.MethodInformation;
import ar.edu.taco.simplejml.methodinfo.MethodInformationBuilder;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprFunction;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIfCondition;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntersection;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprProduct;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.IProgramCall;
import ar.uba.dc.rfm.alloy.ast.formulas.OrFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.QuantifiedFormula;

/**
 * @author elgaby
 * 
 */
public class ExpressionVisitor extends BaseExpressionVisitor {
    private static Logger log = Logger.getLogger(ExpressionVisitor.class);



    ////// ARITHMETIC EXPRESSIONS	




    @Override
    public void visitAddExpression(JAddExpression jAddExpression) {
        jAddExpression.accept(prettyPrint);
        log.debug("Visiting: " + jAddExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression;
        binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jAddExpression, Constants.OPE_PLUS);

        super.getStack().push(binaryExpression);
    }


    @Override
    public void visitMinusExpression(JMinusExpression jMinusExpression) {
        jMinusExpression.accept(prettyPrint);
        log.debug("Visiting: " + jMinusExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression;

        binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jMinusExpression, Constants.OPE_MINUS);

        super.getStack().push(binaryExpression);
    }


    @Override
    public void visitMultExpression(JMultExpression jMultExpression) {
        jMultExpression.accept(prettyPrint);
        log.debug("Visiting: " + jMultExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jMultExpression, Constants.OPE_STAR);

        super.getStack().push(binaryExpression);
    }


    @Override
    public void visitDivideExpression(JDivideExpression jDivideExpression) {
        jDivideExpression.accept(prettyPrint);
        log.debug("Visiting: " + jDivideExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jDivideExpression, Constants.OPE_SLASH);

        super.getStack().push(binaryExpression);
    }


    @Override
    public void visitModuloExpression(JModuloExpression jModuloExpression) {
        jModuloExpression.accept(prettyPrint);
        log.debug("Visiting: " + jModuloExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jModuloExpression, Constants.OPE_PERCENT);

        super.getStack().push(binaryExpression);
    }


    @Override
    public void visitArrayAccessExpression(
            JArrayAccessExpression jArrayAccessExpression) {
        jArrayAccessExpression.accept(prettyPrint);
        log.debug("Visiting: " + jArrayAccessExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        jArrayAccessExpression.prefix().accept(this);
        AlloyExpression array_expression = this.getAlloyExpression();

        jArrayAccessExpression.accessor().accept(this);

        AlloyExpression array_index = this.getAlloyExpression();

        AlloyExpression expr;
        CType prefix_ctype = jArrayAccessExpression.prefix().getType();
        JType array_type = new CTypeAdapter().translate(prefix_ctype);
        if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
            expr = JavaPrimitiveIntValueArrayFactory.array_access(array_type,
                    array_expression, array_index);
        } else {
            expr = AlloyIntArrayFactory.arrayAccess(array_type, array_expression,
                    array_index);
        }
        super.getStack().push(expr);

    }

    @Override
    public void visitArrayLengthExpression(
            JArrayLengthExpression jArrayLengthExpression) {
        jArrayLengthExpression.prefix().accept(this);

        AlloyExpression array_expression = this.getAlloyExpression();
        CType prefix_ctype = jArrayLengthExpression.prefix().getType();
        JType array_type = new CTypeAdapter().translate(prefix_ctype);

        AlloyExpression e;
        if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
            e = JavaPrimitiveIntValueArrayFactory.array_length(array_type,
                    array_expression);

        } else {
            e = AlloyIntArrayFactory.arrayLength(array_type, 
                    array_expression);
        }
        super.getStack().push(e);
    }



    @Override
    public void visitBooleanLiteral(JBooleanLiteral jBooleanLiteral) {
        jBooleanLiteral.accept(prettyPrint);
        log.debug("Visiting: " + jBooleanLiteral.getClass().getName()
                + " Value: " + prettyPrint.getPrettyPrint());

        if (jBooleanLiteral.booleanValue()) {
            super.getStack().push(JExpressionFactory.TRUE_EXPRESSION);
        } else {
            super.getStack().push(JExpressionFactory.FALSE_EXPRESSION);
        }
    }



    @Override
    public void visitCastExpression(JCastExpression jCastExpression) {
        jCastExpression.expr().accept(this);
        AlloyExpression right = this.getAlloyExpression();
        if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true){
            //we will only consider narrowing casts, since the widening ones are considered in a prior step
            if (jCastExpression.expr().getApparentType().isPrimitive() && jCastExpression.getType().isPrimitive() && jCastExpression.expr().getApparentType() != jCastExpression.getType()){
                if (jCastExpression.expr().getApparentType().toString().equals("int") && jCastExpression.getType().toString().equals("char")){
                    right = (AlloyExpression) JExpressionFactory.fun_java_primitive_int_value_to_char_value(right);
                } else if (jCastExpression.expr().getApparentType().toString().equals("long") && jCastExpression.getType().toString().equals("char")){
                    right = (AlloyExpression) JExpressionFactory.fun_java_primitive_long_value_to_char_value(right);
                } else if (jCastExpression.expr().getApparentType().toString().equals("long") && jCastExpression.getType().toString().equals("int")) {
                    right = (AlloyExpression) JExpressionFactory.fun_java_primitive_long_value_to_int_value(right);
                } else {
                    throw new RuntimeException("Narrowing cast failed betweeb types " + jCastExpression.expr().getApparentType().toString() + " and " + jCastExpression.getType().toString());
                }
                super.getStack().push(right);
            } else {
                super.getStack().push(right);
            }
        } else {

            CTypeAdapter cTypeAdapter = new CTypeAdapter();
            JType jType = cTypeAdapter.translate(jCastExpression.getType());
            AlloyExpression left = ExprConstant.buildExprConstant(jType.toString());
            ExprIntersection exprIntersection = new ExprIntersection(left, right);
            super.getStack().push(exprIntersection);
        }
    }



    @Override
    public void visitCharLiteral(JCharLiteral jCharLiteral) {
        jCharLiteral.accept(prettyPrint);
        log.debug("Visiting: " + jCharLiteral.getClass().getName() + " Value: "
                + prettyPrint.getPrettyPrint());

        //		// handle overflow using alloy 'bitwidth' parameter
        //		TacoConfigurator configurator = (TacoConfigurator) JDynAlloyConfig
        //				.getInstance();
        //
        //		int strHashCode = jCharLiteral.toString().hashCode();
        //
        //		int boundedValue = ExpressionSolver.preventBitwidthOverflow(
        //				strHashCode, configurator.getBitwidth());
        //
        //		AlloyExpression hashCodeExpression = new ExprIntLiteral(boundedValue);
        //
        //		super.getStack().push(hashCodeExpression);

        char theCharLiteral = (char)jCharLiteral.getValue();
        AlloyExpression literalAlloyExpression = JavaPrimitiveCharValue
                .getInstance().toJavaPrimitiveCharLiteral(theCharLiteral, false);

        super.getStack().push(literalAlloyExpression);


    }

    @Override
    public void visitConditionalAndExpression(
            JConditionalAndExpression jConditionalAndExpression) {
        jConditionalAndExpression.left().accept(this);
        AlloyFormula left = null;
        if (this.isAlloyFormula()) {
            left = this.getAlloyFormula();
        } else {
            left = new EqualsFormula(this.getAlloyExpression(),
                    JExpressionFactory.TRUE_EXPRESSION);
        }

        jConditionalAndExpression.right().accept(this);
        AlloyFormula right = null;
        if (this.isAlloyFormula()) {
            right = this.getAlloyFormula();
        } else {
            right = new EqualsFormula(this.getAlloyExpression(),
                    JExpressionFactory.TRUE_EXPRESSION);
        }

        AlloyFormula and = AndFormula.buildAndFormula(left, right);
        super.getStack().push(and);
    }

    @Override
    public void visitConditionalExpression(
            JConditionalExpression jConditionalExpression) {
        jConditionalExpression.cond().accept(this);
        AlloyFormula cond = this.getAlloyFormula();

        jConditionalExpression.left().accept(this);
        AlloyExpression left = this.getAlloyExpression();

        jConditionalExpression.right().accept(this);
        AlloyExpression right = this.getAlloyExpression();

        ExprIfCondition exprIfCondition = new ExprIfCondition(cond, left, right);

        super.getStack().push(exprIfCondition);

    }

    @Override
    public void visitConditionalOrExpression(
            JConditionalOrExpression jConditionalOrExpression) {
        jConditionalOrExpression.left().accept(this);
        AlloyFormula left = this.getAlloyFormula();

        jConditionalOrExpression.right().accept(this);
        AlloyFormula right = this.getAlloyFormula();

        AlloyFormula or = OrFormula.buildOrFormula(left, right);
        super.getStack().push(or);
    }



    @Override
    public void visitEqualityExpression(JEqualityExpression jEqualityExpression) {
        jEqualityExpression.accept(prettyPrint);
        log.debug("Visiting: " + jEqualityExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jEqualityExpression, jEqualityExpression.oper());

        super.getStack().push(binaryExpression);
    }




    @Override
    public void visitFieldExpression(JClassFieldExpression jClassFieldExpression) {
        jClassFieldExpression.prefix().accept(this);
        AlloyExpression e1 = null;
        AlloyExpression e2 = null;
        if (this.isAlloyExpression()) {
            e1 = this.getAlloyExpression();
        } else {
            // if the prefix is a formula is because is a method call, so that
            // formula MUST be a QualifiedAlloyFormula
            QuantifiedFormula quantifiedAlloyFormula = (QuantifiedFormula) this
                    .getAlloyFormula();

            AlloyVariable qualifiedVariable = new AlloyVariable(
                    quantifiedAlloyFormula.getNames().get(0));

            this.getStack().push(quantifiedAlloyFormula);
            e1 = ExprVariable.buildExprVariable(qualifiedVariable);
        }

        if (jClassFieldExpression.getField().isStatic() && jClassFieldExpression.prefix() instanceof JTypeNameExpression) {
            String fieldName = e1.toString() + "_"
                    + jClassFieldExpression.ident();
            e2 = ExprVariable.buildExprVariable(fieldName);

            e1 = ar.edu.jdynalloy.factory.JExpressionFactory.CLASS_FIELDS;
        } 
        
        
        if (jClassFieldExpression.getField().isStatic() && !(jClassFieldExpression.prefix() instanceof JTypeNameExpression)) {
            String fieldName = jClassFieldExpression.getField().getType().toString().replace('.', '_') + "_"
                    + jClassFieldExpression.ident();
            e2 = ExprVariable.buildExprVariable(fieldName);
            e2 = new ExprProduct(new ExprConstant(null, "univ"), ExprJoin.join(ar.edu.jdynalloy.factory.JExpressionFactory.CLASS_FIELDS, e2));

        }
        
        if (!jClassFieldExpression.getField().isStatic()) {
            e2 = ExprVariable.buildExprVariable(jClassFieldExpression.ident());
        }

        ExprJoin exprJoin = ExprJoin.join(e1, e2);
        this.getStack().push(exprJoin);
    }



    @Override
    public void visitInstanceofExpression(
            JInstanceofExpression jInstanceofExpression) {
        jInstanceofExpression.accept(prettyPrint);
        log.debug("Visiting: " + jInstanceofExpression.getClass().getName()
                + " Value: " + prettyPrint.getPrettyPrint());

        jInstanceofExpression.expr().accept(this);
        AlloyExpression leftExpression = this.getAlloyExpression();

        CTypeAdapter cTypeAdapter = new CTypeAdapter();
        JType jType = cTypeAdapter.translate(jInstanceofExpression.dest());
        String signatureId = jType.toString();

        super.getStack().push(
                JPredicateFactory.instanceOf(leftExpression, signatureId));

    }



    @Override
    public void visitJmlFormalParameter(JmlFormalParameter jmlFormalParameter) {
        jmlFormalParameter.accept(prettyPrint);
        log.debug("Visiting: " + jmlFormalParameter.getClass().getName()
                + " Value: " + prettyPrint.getPrettyPrint());

        // Create an AlloyVariable from variable name
        AlloyVariable alloyVariableDeclaration = buildAlloyVariable(jmlFormalParameter
                .ident());

        // extract the variable type and convert it to and Alloy variable type.
        CTypeAdapter cTypeAdapter = new CTypeAdapter();
        JType variableType = cTypeAdapter.translate(jmlFormalParameter
                .getType());

        JStatement variableDeclaration = new JVariableDeclaration(
                alloyVariableDeclaration, variableType);
        super.getStack().push(variableDeclaration);
    }



    @Override
    public void visitJmlRelationalExpression(
            JmlRelationalExpression jmlRelationalExpression) {
        jmlRelationalExpression.accept(prettyPrint);
        log.debug("Visiting: " + jmlRelationalExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression;

        binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jmlRelationalExpression, jmlRelationalExpression.oper());

        super.getStack().push(binaryExpression);
    }



    @Override
    public void visitLocalVariableExpression(
            JLocalVariableExpression jLocalVariableExpression) {
        jLocalVariableExpression.accept(prettyPrint);
        log.debug("Visiting: " + jLocalVariableExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        String identifier = new String(jLocalVariableExpression.ident());
        // TODO: Ver si esta parte del codigo que esta comentado tiene que ir,
        // ya que no existe bindings o similar en mjc
        // if (singleNameReference.binding instanceof TypeBinding) {
        // super.getStack().push(new ExprConstant(null, identifier + "class"));
        // }
        // else {

        super.getStack().push(ExprVariable.buildExprVariable(identifier));
        // }

    }



    @Override
    public void visitMethodCallExpression(JMethodCallExpression jMethodCallExpression) {
        jMethodCallExpression.accept(prettyPrint);
        log.debug("Visiting: " + jMethodCallExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        List<AlloyExpression> argumentsList = new ArrayList<AlloyExpression>();
        if (jMethodCallExpression.args() != null) {
            for (JExpression expression : jMethodCallExpression.args()) {
                if (expression instanceof JNewObjectExpression) {
                    throw new UnsupportedOperationException("Operation: "
                            + prettyPrint.getPrettyPrint()
                            + " is not supported, please Split this statement");
                }

                expression.accept(this);
                argumentsList.add(this.getAlloyExpression());
            }
        }

        MethodInformation methodInformation = MethodInformationBuilder
                .getInstance().getMethodInformation(jMethodCallExpression);

        boolean isStatic = methodInformation.isStatic();
        boolean isConstructor = methodInformation.isConstructor();
        boolean hasReturnTypeOrReturnValue = methodInformation.hasReturnType();

        ArgEncoder convention = new ArgEncoder(isStatic, isConstructor,
                hasReturnTypeOrReturnValue, argumentsList.size());

        boolean isSuper = (jMethodCallExpression.prefix() instanceof JSuperExpression);

        // Resolve the left side of the call (e.g. this.something() or
        // Integer.something())
        AlloyExpression leftExpression = null;
        if (!isStatic) {
            leftExpression = ExpressionSolver.getLeftExpression(this,
                    jMethodCallExpression.prefix());
        }

        Vector<AlloyExpression> encodedArguments = convention.encode(
                leftExpression, JExpressionFactory.THROW_EXPRESSION, this
                .getLeftAssignmentExpression(), argumentsList);
        String methodName = jMethodCallExpression.ident();

        if (isStatic) {
            // JavaClassNameNormalizer classNameNormalizer = new
            // JavaClassNameNormalizer(jMethodCallExpression.method().receiverType().toVerboseString());
            // String classQualifiedName =
            // classNameNormalizer.getQualifiedClassName();
            // methodName = classQualifiedName + "_" + methodName;
            methodName = methodInformation.getQualifiedReceiverType() + "_"
                    + methodName;
        }

        IProgramCall jProgramCall = new JProgramCall(isSuper, methodName,
                encodedArguments);

        super.getStack().push(jProgramCall);
    }





    @Override
    public void visitNameExpression(JNameExpression jNameExpression) {
        jNameExpression.accept(prettyPrint);
        log.debug("Visiting: " + jNameExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        AlloyExpression variable = null;

        AlloyExpression identifier = ExprVariable
                .buildExprVariable(jNameExpression.getName());
        if (jNameExpression.getPrefix() != null) {
            jNameExpression.getPrefix().accept(this);

            AlloyExpression prefix = this.getAlloyExpression();
            variable = new ExprJoin(prefix, identifier);
        } else {
            variable = identifier;
        }

        // TODO: Ver si esta parte del codigo que esta comentado tiene que ir,
        // ya que no existe bindings o similar en mjc
        // if (singleNameReference.binding instanceof TypeBinding) {
        // super.getStack().push(new ExprConstant(null, identifier + "class"));
        // }
        // else {
        super.getStack().push(variable);
        // }
    }



    @Override
    public void visitNewArrayExpression(JNewArrayExpression jNewArrayExpression) {
        jNewArrayExpression.accept(prettyPrint);
        log.debug("Visiting: " + jNewArrayExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        ArgEncoder convention = new ArgEncoder(false, true, false, 1 /*
         * The
         * array
         * length
         */);

        String signatureId;
        CType array_type = jNewArrayExpression.getType();
        JType jtype = new CTypeAdapter().translate(array_type);

        if (jtype.equals(JSignatureFactory.INT_ARRAY_TYPE)) {
            signatureId = JSignatureFactory.INT_ARRAY_TYPE.singletonFrom();
        } else if (jtype.equals(JSignatureFactory.LONG_ARRAY_TYPE)) {	
            signatureId = JSignatureFactory.LONG_ARRAY_TYPE.singletonFrom();
        } else if (jtype.equals(JSignatureFactory.CHAR_ARRAY_TYPE)) {	
            signatureId = JSignatureFactory.CHAR_ARRAY_TYPE.singletonFrom();
        } else if (jtype.equals(JSignatureFactory.OBJECT_ARRAY_TYPE)) {
            signatureId = JSignatureFactory.OBJECT_ARRAY_TYPE.singletonFrom();
        } else {
            throw new RuntimeException("unsupported array type");
        }

        ExprVariable exprVariable = (ExprVariable) this
                .getLeftAssignmentExpression();
        AlloyVariable leftSideAlloyVariable = exprVariable.getVariable();
        JCreateObject newObject = new JCreateObject(signatureId,
                leftSideAlloyVariable);

        jNewArrayExpression.dims().dims()[0].accept(this);

        List<AlloyExpression> argumentsList = new ArrayList<AlloyExpression>();
        argumentsList.add(this.getAlloyExpression());

        Vector<AlloyExpression> encodedArguments = convention.encode(this
                .getLeftAssignmentExpression(),
                JExpressionFactory.THROW_EXPRESSION, this
                .getLeftAssignmentExpression(), argumentsList);

        JProgramCall call_constructor = new JProgramCall(false, "Constructor",
                encodedArguments);

        Vector<JStatement> array_stmts = new Vector<JStatement>();
        array_stmts.add(newObject);
        array_stmts.add(call_constructor);

        if (TacoConfigurator.getInstance().getUseJavaArithmetic() == false) {

            if (jNewArrayExpression.getType() instanceof CArrayType
                    && ((CArrayType) jNewArrayExpression.getType())
                    .getBaseType().isNumeric()) {
                AlloyExpression array_elements = AlloyIntArrayFactory
                        .arrayElements(this.getLeftAssignmentExpression());
                AlloyFormula initialization_condition = JPredicateFactory
                        .isSubset(array_elements, new ExprIntLiteral(0));
                JAssume assume_initialization = new JAssume(
                        initialization_condition);
                array_stmts.add(assume_initialization);
            }
        }

        JBlock new_array_block = JDynAlloyFactory.block(array_stmts
                .toArray(new JStatement[] {}));
        super.getStack().push(new_array_block);

    }



    @Override
    public void visitNewObjectExpression(
            JNewObjectExpression jNewObjectExpression) {
        jNewObjectExpression.accept(prettyPrint);
        log.debug("Visiting: " + jNewObjectExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        List<AlloyExpression> argumentsList = new ArrayList<AlloyExpression>();
        if (jNewObjectExpression.params() != null) {
            for (JExpression anArgument : jNewObjectExpression.params()) {
                anArgument.accept(this);
                argumentsList.add(this.getAlloyExpression());
            }
        }

        ArgEncoder convention = new ArgEncoder(false, true, false,
                argumentsList.size());
        CTypeAdapter cTypeAdapter = new CTypeAdapter();
        JType alloyType = cTypeAdapter
                .translate(jNewObjectExpression.getType());

        String newObjectType = JTypeHelper.getBaseType(alloyType).replaceAll(
                "\\.", "_");

        // QQ: We need to check if we are going to need this configuration. If
        // the answer is yes, then this code MUST be implemented
        // if (DynJAlloyConfig.getInstance().getUseClassSingletons() == true) {
        // signatureId = newObjectType + "Class";
        // } else {
        // signatureId = newObjectType;
        // }

        JStatement statement = null;
        if (JDynAlloyConfig.getInstance().getNewExceptionsAreLiterals()
                && ExpressionSolver
                .isDescendentOfException(jNewObjectExpression.getType())) {

            final ExprConstant exceptionLit = JExpressionFactory
                    .buildLiteralSingleton(newObjectType);

            statement = JDynAlloyFactory.assign((ExprVariable) this
                    .getLeftAssignmentExpression(), exceptionLit);

        } else {
            String signatureId = newObjectType;

            AlloyVariable leftSideAlloyVariable = ((ExprVariable) this
                    .getLeftAssignmentExpression()).getVariable();
            JCreateObject newObject = new JCreateObject(signatureId,
                    leftSideAlloyVariable);

            Vector<AlloyExpression> encodedArguments = convention.encode(this
                    .getLeftAssignmentExpression(),
                    JExpressionFactory.THROW_EXPRESSION, this
                    .getLeftAssignmentExpression(), argumentsList);

            JProgramCall call = new JProgramCall(false, "Constructor",
                    encodedArguments);

            statement = JDynAlloyFactory.block(newObject, call);
        }

        super.getStack().push(statement);

    }



    @Override
    public void visitNullLiteral(JNullLiteral jNullLiteral) {
        this.getStack().push(JExpressionFactory.NULL_EXPRESSION);
    }



    @Override
    public void visitOrdinalLiteral(JOrdinalLiteral jOrdinalLiteral) {
        jOrdinalLiteral.accept(prettyPrint);
        log.debug("Visiting: " + jOrdinalLiteral.getClass().getName()
                + " Value: " + prettyPrint.getPrettyPrint());

        if (jOrdinalLiteral.isLiteral()) {

            CType ctype = jOrdinalLiteral.getType();
            CTypeAdapter type_adapter = new CTypeAdapter();
            JType alloy_type = type_adapter.translate(ctype);

            AlloyExpression literalAlloyExpression;

            if (alloy_type
                    .equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE)) {

                int int_value = jOrdinalLiteral.numberValue().intValue();

                literalAlloyExpression = JavaPrimitiveIntegerValue
                        .getInstance().toJavaPrimitiveIntegerLiteral(int_value, false);

            } else if (alloy_type
                    .equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE)) {

                long long_value = jOrdinalLiteral.numberValue().longValue();

                literalAlloyExpression = JavaPrimitiveLongValue.getInstance()
                        .toJavaPrimitiveLongLiteral(long_value, false);

            } else if (alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE)) {

                char char_value = ((char) jOrdinalLiteral.numberValue().intValue());
                literalAlloyExpression = JavaPrimitiveCharValue.getInstance()
                        .toJavaPrimitiveCharLiteral(char_value, false);

            } else if (alloy_type.equals(JSignatureFactory.ALLOY_INT)) {

                int literalValue = jOrdinalLiteral.numberValue().intValue();

                if (literalValue < 0) {

                    literalAlloyExpression = JExpressionFactory
                            .alloy_int_negate(new ExprIntLiteral(Math
                                    .abs(literalValue)));
                } else {
                    literalAlloyExpression = new ExprIntLiteral(literalValue);
                }

            } else {
                throw new TacoException("unsupported ordinal type "
                        + alloy_type.toString());
            }

            super.getStack().push(literalAlloyExpression);

        } else if (jOrdinalLiteral.isBooleanLiteral()) {
            jOrdinalLiteral.getBooleanLiteral().accept(this);
        } else if (jOrdinalLiteral.isStringLiteral()) {
            jOrdinalLiteral.getStringLiteral().accept(this);
        } else {
            throw new TacoNotImplementedYetException(
                    "Please verify the type of the JOrdinalLiteral and code the corresponding solution");
        }
    }

    @Override
    public void visitRelationalExpression(
            JRelationalExpression jRelationalExpression) {
        jRelationalExpression.accept(prettyPrint);
        log.debug("Visiting: " + jRelationalExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jRelationalExpression, jRelationalExpression.oper());

        super.getStack().push(binaryExpression);
    }

    @Override
    public void visitShiftExpression(JShiftExpression jShiftExpression) {
        jShiftExpression.accept(prettyPrint);
        log.debug("Visiting: " + jShiftExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object binaryExpression = ExpressionSolver.getBinaryExpression(this,
                jShiftExpression, jShiftExpression.oper());

        super.getStack().push(binaryExpression);
    }

    @Override
    public void visitStringLiteral(JStringLiteral jStringLiteral) {
        jStringLiteral.accept(prettyPrint);
        log.debug("Visiting: " + jStringLiteral.getClass().getName()
                + " Value: " + prettyPrint.getPrettyPrint());

        //The commented code below assumes strings contain a hashCode, which
        //is used to represent the String. At this point we consider this
        //mostly useless, and will use a constructor to create a new String object.

        //		// handle overflow using alloy 'bitwidth' parameter
        //		TacoConfigurator configurator = (TacoConfigurator) JDynAlloyConfig
        //				.getInstance();
        //
        //		int strHashCode = jStringLiteral.stringValue().hashCode();
        //
        //		// VER ESTE TEMA, PORQUE EL ExpIntLiteral SOLO ACEPTA NUMEROS POSITIVOS
        //		if (strHashCode < 0) {
        //			strHashCode = strHashCode * (-1);
        //		}
        //
        //		int boundedValue = ExpressionSolver.preventBitwidthOverflow(
        //				strHashCode, configurator.getStringBitwidth());
        //
        //		AlloyExpression hashCodeExpression = new ExprIntLiteral(boundedValue);
        //		super.getStack().push(hashCodeExpression);


        ArgEncoder convention = new ArgEncoder(false, true, false, 0);
        CTypeAdapter cTypeAdapter = new CTypeAdapter();
        JType alloyType = cTypeAdapter
                .translate(jStringLiteral.getType());

        String newObjectType = JTypeHelper.getBaseType(alloyType).replaceAll(
                "\\.", "_");

        // QQ: We need to check if we are going to need this configuration. If
        // the answer is yes, then this code MUST be implemented
        // if (DynJAlloyConfig.getInstance().getUseClassSingletons() == true) {
        // signatureId = newObjectType + "Class";
        // } else {
        // signatureId = newObjectType;
        // }

        JStatement statement = null;

        String signatureId = newObjectType;

        AlloyVariable leftSideAlloyVariable = ((ExprVariable) this
                .getLeftAssignmentExpression()).getVariable();
        JCreateObject newObject = new JCreateObject(signatureId,
                leftSideAlloyVariable);

        Vector<AlloyExpression> encodedArguments = convention.encode(this
                .getLeftAssignmentExpression(),
                JExpressionFactory.THROW_EXPRESSION, this
                .getLeftAssignmentExpression(), new LinkedList<AlloyExpression>());

        JProgramCall call = new JProgramCall(false, "Constructor",
                encodedArguments);

        statement = JDynAlloyFactory.block(newObject, call);


        super.getStack().push(statement);




    }

    @Override
    public void visitThisExpression(JThisExpression jThisExpression) {
        this.getStack().push(
                ExprVariable
                .buildExprVariable(JExpressionFactory.THIS_VARIABLE));
    }

    @Override
    public void visitTypeNameExpression(JTypeNameExpression jTypeNameExpression) {
        jTypeNameExpression.accept(prettyPrint);
        log.debug("Visiting: " + jTypeNameExpression.getClass().getName()
                + " Value: " + jTypeNameExpression.qualifiedName());

        // I tried to search a better way to do it, but I coundn't find it
        // String[] SplitedQualifiedName =
        // jTypeNameExpression.qualifiedName().split("/");
        //
        // String leftVariableName = String.valueOf(SplitedQualifiedName[0]);
        // AlloyExpression leftExpression = buildExprVariable(leftVariableName);
        //
        // for (int x = 1; x < SplitedQualifiedName.length; x++) {
        // leftVariableName = String.valueOf(SplitedQualifiedName[x]);
        // leftExpression = new ExprJoin(leftExpression,
        // buildExprVariable(leftVariableName));
        // super.getStack().push(leftExpression);
        // }

        JavaClassNameNormalizer javaNormalizer = new JavaClassNameNormalizer(
                jTypeNameExpression.getType().getCClass().getJavaName());
        AlloyExpression expression = new ExprConstant(null, javaNormalizer
                .getQualifiedClassName());
        super.getStack().push(expression);

    }

    @Override
    public void visitUnaryExpression(JUnaryExpression jUnaryExpression) {
        jUnaryExpression.accept(prettyPrint);
        log.debug("Visiting: " + jUnaryExpression.getClass().getName());
        log.debug("Statement: " + prettyPrint.getPrettyPrint());

        Object unaryExpression = ExpressionSolver.getUnaryExpression(this,
                jUnaryExpression, jUnaryExpression.oper());

        super.getStack().push(unaryExpression);
    }

    @Override
    public void visitUnaryPromoteExpression(JUnaryPromote jUnaryPromote) {
        jUnaryPromote.expr().accept(this);
    }

    @Override
    public void visitRealLiteral(JRealLiteral jRealLiteral) {
        jRealLiteral.accept(prettyPrint);
        log.debug("Visiting: " + jRealLiteral.getClass().getName() + " Value: "
                + prettyPrint.getPrettyPrint());

        float float_literal = jRealLiteral.numberValue().floatValue();
        AlloyExpression literalAlloyExpression = JavaPrimitiveFloatValue
                .getInstance().toJavaPrimitiveFloatLiteral(float_literal, false);

        super.getStack().push(literalAlloyExpression);

    }

    @Override
    public void visitBitwiseExpression(JBitwiseExpression n) {
        Object bitwiseExpr = ExpressionSolver.getBinaryExpression(this, n, n.oper());
        super.getStack().push(bitwiseExpr);
    }


    @Override
    public void visitJmlSpecExpression(JmlSpecExpression arg0) {
        super.getStack().push(arg0);

    }


}

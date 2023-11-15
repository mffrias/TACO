package ar.edu.taco.jml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map.Entry;

import org.jmlspecs.checker.JFieldDeclarationWrapper;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlDataGroupAccumulator;
import org.jmlspecs.checker.JmlEnsuresClause;
import org.jmlspecs.checker.JmlEqualityExpression;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlFormalParameter;
import org.jmlspecs.checker.JmlGenericSpecBody;
import org.jmlspecs.checker.JmlInvariant;
import org.jmlspecs.checker.JmlMemberAccess;
import org.jmlspecs.checker.JmlMemberDeclaration;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlOldExpression;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlReachExpression;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlRequiresClause;
import org.jmlspecs.checker.JmlResultExpression;
import org.jmlspecs.checker.JmlSourceField;
import org.jmlspecs.checker.JmlSpecBodyClause;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlSpecQuantifiedExpression;
import org.jmlspecs.checker.JmlVarAssertion;
import org.jmlspecs.checker.JmlVariableDefinition;
import org.multijava.javadoc.JavadocComment;
import org.multijava.mjc.CClass;
import org.multijava.mjc.CMember;
import org.multijava.mjc.CSpecializedType;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JCharLiteral;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JConditionalAndExpression;
import org.multijava.mjc.JConditionalExpression;
import org.multijava.mjc.JConditionalOrExpression;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JFieldDeclaration;
import org.multijava.mjc.JFieldDeclarationType;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JInstanceofExpression;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMemberDeclaration;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JPhylum;
import org.multijava.mjc.JPostfixExpression;
import org.multijava.mjc.JPrefixExpression;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JRelationalExpression;
import org.multijava.mjc.JShiftExpression;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JmlClassDeclarationExtension;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.UnpositionedError;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class SpecMethodCallRemoverVisitor extends JmlAstClonerStatementVisitor {

	public static int newReturnParameterIndex = 0;

	public boolean translatingAnInvariant = false;

	private CClass classBeingCloned;

	public CClass getClassBeingCloned(){
		return this.classBeingCloned;
	}

	//	public SpecMethodCallCollector methodCallCollector = new SpecMethodCallCollector();
	public IdentityHashMap<JmlFormalParameter, JExpression> preconditionSpecMethodCallData = new IdentityHashMap<JmlFormalParameter, JExpression>();
	public IdentityHashMap<JmlFormalParameter, JExpression> posconditionSpecMethodCallData = new IdentityHashMap<JmlFormalParameter, JExpression>();
	public IdentityHashMap<JmlFormalParameter, JExpression> invariantSpecMethodCallData = new IdentityHashMap<JmlFormalParameter, JExpression>();
	public IdentityHashMap<JmlFormalParameter, JExpression> entriesBuffer = new IdentityHashMap<JmlFormalParameter, JExpression>();


	private String generateNewReturnParameterName(){
		String theName = "returnVarFromSpecMethodCall_" + newReturnParameterIndex;
		newReturnParameterIndex++;
		return theName;
	}


	public SpecMethodCallRemoverVisitor(){
		super();
	}

	@Override
	public void visitJmlResultExpression(JmlResultExpression self) {
		this.getStack().push(self);

	}


	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {
		this.classBeingCloned = self.getCClass();
		JmlInvariant[] theInvariants = self.invariants();
		JmlInvariant[] theNewInvariants = new JmlInvariant[theInvariants.length];
		int index = 0;
		for (JmlInvariant inv : theInvariants){
			this.translatingAnInvariant = true;
			inv.accept(this);
			this.translatingAnInvariant = false;
			this.invariantSpecMethodCallData.putAll(entriesBuffer);
			this.entriesBuffer = new IdentityHashMap<JmlFormalParameter, JExpression>();
			theNewInvariants[index] = (JmlInvariant)this.getStack().pop();
			index++;
		}
		ArrayList<JmlMethodDeclaration> theNewMethods = new ArrayList<JmlMethodDeclaration>();
		for (int methodIndex = 0; methodIndex < self.methods().size(); methodIndex++) {
			JmlMethodDeclaration decl = (JmlMethodDeclaration) self.methods().get(methodIndex);
			decl.accept(this);
			theNewMethods.add((JmlMethodDeclaration)this.getStack().pop());
		}

		ArrayList<JFieldDeclarationType> theModelFields = new ArrayList<JFieldDeclarationType>();
		for (int i = 0; i < self.fields().length; i++){
			theModelFields.add(self.fields()[i]);
		}

		ArrayList<JPhylum> updatedFields = new ArrayList<JPhylum>();

		for (int fieldIndex = 0; fieldIndex < self.getCombinedFields().length; fieldIndex++)
			updatedFields.add((JPhylum)self.getCombinedFields()[fieldIndex]);

		for (JmlFormalParameter fp : this.invariantSpecMethodCallData.keySet()){
			JmlMemberAccess access = new JmlMemberAccess(self.getCClass(), JmlMemberAccess.ACC_PUBLIC);
			JmlSourceField export = new JmlSourceField(access, fp.ident(), fp.getType(), false);
			long modifs = JVariableDefinition.ACC_PUBLIC;
			JVariableDefinition theVar = new JVariableDefinition(self.getTokenReference(), modifs, fp.getType(), fp.ident(), null);
			JFieldDeclaration theNewField = new JFieldDeclarationWrapper(self.getTokenReference(), theVar, new JavadocComment(null), new JavaStyleComment[0]);
			JmlFieldDeclaration theFieldDecl = JmlFieldDeclaration.makeInstance(theVar.getTokenReference(), theVar, new JavadocComment(null), new JavaStyleComment[0], new JmlVarAssertion[0], new JmlDataGroupAccumulator());

			//			export.setAST(theFieldDecl);
			//			JFieldDeclarationType theFDT = JFieldDeclarationType 


			Method setInterfaceMethod;
			try {

				setInterfaceMethod = JMemberDeclaration.class.getDeclaredMethod("setInterface", CMember.class);
				setInterfaceMethod.setAccessible(true);
				try {
					setInterfaceMethod.invoke(theNewField, export);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					setInterfaceMethod.setAccessible(false);
				}


			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

			try {
				Field delegee1 = JmlFieldDeclaration.class.getDeclaredField("delegee");
				Field delegee2 = JmlMemberDeclaration.class.getDeclaredField("delegee");
				delegee1.setAccessible(true);
				delegee2.setAccessible(true);
				try {
					delegee1.set(theFieldDecl, theNewField);
					delegee2.set(theFieldDecl, theNewField);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					delegee1.setAccessible(false);
					delegee2.setAccessible(false);
				}
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updatedFields.add(theFieldDecl);	
		}
		self.setFields(updatedFields.toArray(new JPhylum[0]));
		JmlClassDeclarationExtension newSelf = new JmlClassDeclarationExtension(self, theNewInvariants, self.combinedRepresentsDecls(), self.combinedConstraints(), theNewMethods, theModelFields, self.inners());
		this.getStack().push(newSelf);
	};


	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration self) {
		String fullyQualifiedConstructorName = this.getClassName() + self.ident();
		String declaredMethodToCheck = TacoConfigurator.getInstance().getMethodToCheck();
		declaredMethodToCheck = declaredMethodToCheck.substring(0, declaredMethodToCheck.indexOf('('));
		if (fullyQualifiedConstructorName.equals(declaredMethodToCheck)){
			ArrayList<JStatement> newBody = new ArrayList<JStatement>();
			newBody.add(self.body());

			if (self.methodSpecification() != null) {

				self.methodSpecification().accept(this);
				JmlMethodSpecification theNewSpec = (JmlMethodSpecification)this.getStack().pop();

				//BEGIN treatment of method calls in preconditions

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : preconditionSpecMethodCallData.entrySet()){
					addParameter(self,var_call_pair.getKey());
					JLocalVariableExpression theVarExpre = new JLocalVariableExpression(self.getTokenReference(), var_call_pair.getKey());
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theVarExpre, var_call_pair.getValue()); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					newBody.add(0, newAssume);
				}

				//END treatment of method calls in preconditions

				//BEGIN treatment of method calls in invariant, initial state

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : invariantSpecMethodCallData.entrySet()){
					JClassFieldExpression theFieldExpre = new JClassFieldExpression(self.getTokenReference(), var_call_pair.getKey().ident(), new JNameExpression(self.getTokenReference(), var_call_pair.getKey().ident()));
					JmlMemberAccess access = new JmlMemberAccess(((JMethodCallExpression)(var_call_pair.getValue())).prefix().getApparentType().getCClass(), JmlMemberAccess.ACC_PUBLIC);
					JmlSourceField theInternalField = new JmlSourceField(access, var_call_pair.getKey().ident(), var_call_pair.getValue().getApparentType(), false);
					theFieldExpre.setField(theInternalField);
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theFieldExpre, var_call_pair.getValue()); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					newBody.add(0,newAssume);
				}

				//END tratment of method calls in invariant, initial state

				//BEGIN treatment of method calls in invariant, final state

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : invariantSpecMethodCallData.entrySet()){
					JClassFieldExpression theFieldExpre = new JClassFieldExpression(self.getTokenReference(), var_call_pair.getKey().ident(), new JNameExpression(self.getTokenReference(), var_call_pair.getKey().ident()));
					JmlMemberAccess access = new JmlMemberAccess(((JMethodCallExpression)(var_call_pair.getValue())).prefix().getApparentType().getCClass(), JmlMemberAccess.ACC_PUBLIC);
					JmlSourceField theInternalField = new JmlSourceField(access, var_call_pair.getKey().ident(), var_call_pair.getValue().getApparentType(), false);
					theFieldExpre.setField(theInternalField);
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theFieldExpre, var_call_pair.getValue()); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					distribute(newAssume, newBody, self.returnType().isVoid());
				}

				//END treatment of method calls in invariant, final state

				//BEGIN treatment of method calls in postconditions. Notice that these formulas may refer to expressions in the initial 
				//state through the use of \old. We need to take this into account

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : posconditionSpecMethodCallData.entrySet()){

					JMethodCallExpression methodCall = (JMethodCallExpression)var_call_pair.getValue();
					addParameter(self, var_call_pair.getKey());
					JExpression[] newArgsForMethodCall = new JExpression[methodCall.args().length];
					int argIndex = 0;
					for (JExpression arg : methodCall.args()){
						OldExpressionReplacerVisitor oldRepVis = new OldExpressionReplacerVisitor();
						arg.accept(oldRepVis);
						for (JmlFormalParameter theVar : oldRepVis.buffer.keySet()){
							addParameter(self,theVar);
							JLocalVariableExpression theVarExpre = new JLocalVariableExpression(self.getTokenReference(), theVar);
							JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theVarExpre, oldRepVis.buffer.get(theVar)); 
							JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
							JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
							JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
							newBody.add(0, newAssume);
						}

						JExpression argPrime = (JExpression)oldRepVis.getStack().pop();
						newArgsForMethodCall[argIndex] = argPrime;
						argIndex++;	
					}

					//					JMethodCallExpression newMethodCall = new JMethodCallExpression(methodCall.getTokenReference(), methodCall.prefix(), fullyQualifiedMethodName, newArgsForMethodCall, false);
					//					JMethodCallExpression newMethodCall = new JMethodCallExpression(methodCall.getTokenReference(), methodCall.prefix(), newArgsForMethodCall);
					//					JMethodCallExpression newMethodCall = new JMethodCallExpression(methodCall.getTokenReference(), methodCall.sourceName(), newArgsForMethodCall);
					//This version has not been yet tested.
					//					JMethodCallExpression newMethodCall = new JMethodCallExpression(methodCall.getTokenReference(), methodCall.prefix(), methodCall.ident(), newArgsForMethodCall, false);
					JMethodCallExpression newMethodCall = (JMethodCallExpression) methodCall.clone();
					newMethodCall.setType(methodCall.getApparentType());
					JLocalVariableExpression expreFromParam = new JLocalVariableExpression(self.getTokenReference(), new JVariableDefinition(self.getTokenReference(), 0, var_call_pair.getKey().dynamicType(), var_call_pair.getKey().ident(), null));
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, expreFromParam, newMethodCall); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					distribute(newAssume, newBody, self.returnType().isVoid());
				}

				JConstructorBlock newConsBody = new JConstructorBlock(self.getTokenReference(), newBody.toArray(new JStatement[newBody.size()]));
				JmlConstructorDeclaration newSelf = JmlConstructorDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.ident(), self.parameters(), self.getExceptions(), newConsBody, self.javadocComment(), null, theNewSpec);
				this.getStack().push(newSelf);
			} else {
				this.getStack().push(self);
			}
		} else {
			this.getStack().push(self);
		}

	}


	private void distribute(JmlAssumeStatement newAssume, ArrayList<JStatement> newBody, boolean isVoid) {
		StatementDistributeAboveReturnVisitor returnVisitor = new StatementDistributeAboveReturnVisitor(newAssume);

		for (int index = 0; index < newBody.size(); index++){
			newBody.get(index).accept(returnVisitor);
			newBody.set(index, (JStatement)returnVisitor.getStack().pop());
		}
		if (isVoid){
			newBody.add(newAssume);
		}

	}


	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
		String fullyQualifiedMethodName = this.getClassName() + self.ident();
		String methodToCheck = TacoConfigurator.getInstance().getMethodToCheck();
		methodToCheck = methodToCheck.substring(0, methodToCheck.indexOf('('));
		if (fullyQualifiedMethodName.equals(methodToCheck)){
			ArrayList<JStatement> newBody = new ArrayList<JStatement>();
			newBody.add(self.body());

			if (self.methodSpecification() != null) {

				self.methodSpecification().accept(this);
				JmlMethodSpecification theNewSpec = (JmlMethodSpecification)this.getStack().pop();

				//BEGIN treatment of method calls in preconditions

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : preconditionSpecMethodCallData.entrySet()){
					addParameter(self,var_call_pair.getKey());
					JLocalVariableExpression theVarExpre = new JLocalVariableExpression(self.getTokenReference(), var_call_pair.getKey());
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theVarExpre, var_call_pair.getValue()); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					newBody.add(0, newAssume);
				}

				//END treatment of method calls in preconditions

				//BEGIN treatment of method calls in invariant, initial state

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : invariantSpecMethodCallData.entrySet()){
					JClassFieldExpression theFieldExpre = new JClassFieldExpression(self.getTokenReference(), var_call_pair.getKey().ident(), new JNameExpression(self.getTokenReference(), var_call_pair.getKey().ident()));
					JmlMemberAccess access = new JmlMemberAccess(((JMethodCallExpression)(var_call_pair.getValue())).prefix().getApparentType().getCClass(), JmlMemberAccess.ACC_PUBLIC);
					JmlSourceField theInternalField = new JmlSourceField(access, var_call_pair.getKey().ident(), var_call_pair.getValue().getApparentType(), false);
					theFieldExpre.setField(theInternalField);
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theFieldExpre, var_call_pair.getValue()); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					newBody.add(0,newAssume);
				}

				//END tratment of method calls in invariant, initial state

				//BEGIN treatment of method calls in invariant, final state

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : invariantSpecMethodCallData.entrySet()){
					JClassFieldExpression theFieldExpre = new JClassFieldExpression(self.getTokenReference(), var_call_pair.getKey().ident(), new JNameExpression(self.getTokenReference(), var_call_pair.getKey().ident()));
					JmlMemberAccess access = new JmlMemberAccess(((JMethodCallExpression)(var_call_pair.getValue())).prefix().getApparentType().getCClass(), JmlMemberAccess.ACC_PUBLIC);
					JmlSourceField theInternalField = new JmlSourceField(access, var_call_pair.getKey().ident(), var_call_pair.getValue().getApparentType(), false);
					theFieldExpre.setField(theInternalField);
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theFieldExpre, var_call_pair.getValue()); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					distribute(newAssume, newBody, self.returnType().isVoid());
				}

				//END treatment of method calls in invariant, final state

				//BEGIN treatment of method calls in postconditions. Notice that these formulas may refer to expressions in the initial 
				//state through the use of \old. We need to take this into account

				for (Entry<JmlFormalParameter, JExpression> var_call_pair : posconditionSpecMethodCallData.entrySet()){

					JMethodCallExpression methodCall = (JMethodCallExpression)var_call_pair.getValue();
					addParameter(self,var_call_pair.getKey());
					JExpression[] newArgsForMethodCall = new JExpression[methodCall.args().length];
					int argIndex = 0;
					for (JExpression arg : methodCall.args()){
						OldExpressionReplacerVisitor oldRepVis = new OldExpressionReplacerVisitor();
						arg.accept(oldRepVis);
						for (JmlFormalParameter theVar : oldRepVis.buffer.keySet()){
							addParameter(self,theVar);
							JLocalVariableExpression theVarExpre = new JLocalVariableExpression(self.getTokenReference(), theVar);
							JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, theVarExpre, oldRepVis.buffer.get(theVar)); 
							JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
							JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
							JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
							newBody.add(0, newAssume);
						}

						JExpression argPrime = (JExpression)oldRepVis.getStack().pop();
						newArgsForMethodCall[argIndex] = argPrime;
						argIndex++;	
					}

					JMethodCallExpression newMethodCall = (JMethodCallExpression) methodCall.clone();
					//							new JMethodCallExpression(methodCall.getTokenReference(), methodCall.prefix(), methodCall.ident(), newArgsForMethodCall, false);
					//					JMethodCallExpression newMethodCall = new JMethodCallExpression(methodCall.getTokenReference(), methodCall.prefix(), newArgsForMethodCall);
					//					newMethodCall.setType(methodCall.getApparentType());
					JLocalVariableExpression expreFromParam = new JLocalVariableExpression(self.getTokenReference(), new JVariableDefinition(self.getTokenReference(), 0, var_call_pair.getKey().dynamicType(), var_call_pair.getKey().ident(), null));
					JmlEqualityExpression theEqualsBetweenVarAndCall = new JmlEqualityExpression(self.getTokenReference(), JmlEqualityExpression.OPE_EQ, expreFromParam, newMethodCall); 
					JmlSpecExpression theActualRelationBetweenVarAndCall = new JmlSpecExpression(theEqualsBetweenVarAndCall);
					JmlPredicate relateVarWithCall = new JmlPredicate(theActualRelationBetweenVarAndCall);
					JmlAssumeStatement newAssume = new JmlAssumeStatement(self.getTokenReference(), ENV_DEBUG_MODE, relateVarWithCall, null, null);
					distribute(newAssume, newBody, self.returnType().isVoid());
				}

				//				JBlock newMethodBody = new JConstructorBlock(self.getTokenReference(), newBody.toArray(new JStatement[newBody.size()]));
				JBlock newMethodBody = new JBlock(self.getTokenReference(), newBody.toArray(new JStatement[newBody.size()]), null);
				JmlMethodDeclaration newSelf = JmlMethodDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.typevariables(), self.returnType(), self.ident(), self.parameters(), self.getExceptions(), newMethodBody, self.javadocComment(), new JavaStyleComment[0], theNewSpec);
				this.getStack().push(newSelf);
			} else {
				this.getStack().push(self);
			}
		} else {
			this.getStack().push(self);
		}
	}




	private void addParameter(JmlMethodDeclaration self, JmlFormalParameter key) {
		JFormalParameter[] oldParams = self.parameters();
		JFormalParameter[] newParams = new JFormalParameter[oldParams.length + 1];
		System.arraycopy(oldParams, 0, newParams, 0, oldParams.length);
		newParams[oldParams.length] = key;
		self.setParameters(newParams);
	}


	private String getClassName() {
		String cname = this.getClassBeingCloned().getJavaName();
		cname = cname.replace('.', '_');
		cname = cname + "_";
		return cname;
	}





	@Override
	public void visitJmlRequiresClause(JmlRequiresClause self) {
		if (self.isNotSpecified()) {
			throw new IllegalArgumentException("Requires clause is not specified.");
		}
		JmlPredicate thePred = self.predOrNot();
		thePred.accept(this);
		JmlPredicate newPred = (JmlPredicate)this.getStack().pop();
		this.preconditionSpecMethodCallData.putAll(entriesBuffer);
		this.entriesBuffer = new IdentityHashMap<JmlFormalParameter, JExpression>();
		JmlRequiresClause newSelf = new JmlRequiresClause(self.getTokenReference(), self.isRedundantly(), newPred);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitJmlGenericSpecBody(JmlGenericSpecBody self) {
		JmlGenericSpecBody newSelf;
		if (self.specClauses() != null){
			JmlSpecBodyClause[] newClauses = new JmlSpecBodyClause[self.specClauses().length];
			int index = 0;
			for (JmlSpecBodyClause specCase : self.simpleSpecBodies()){
				specCase.accept(this);
				newClauses[index] = (JmlSpecBodyClause)this.getStack().pop();
				this.posconditionSpecMethodCallData.putAll(this.entriesBuffer);
				this.entriesBuffer = new IdentityHashMap<JmlFormalParameter, JExpression>();
				index++;
			}
			newSelf = new JmlGenericSpecBody(newClauses);
		} else {
			newSelf = self;
		}
		this.getStack().push(newSelf);
	}


	@Override
	public void visitJmlInvariant(JmlInvariant self) {
		JmlPredicate thePred = self.predicate();
		thePred.accept(this);
		this.invariantSpecMethodCallData.putAll(this.entriesBuffer);
		this.entriesBuffer = new IdentityHashMap<JmlFormalParameter, JExpression>();
		JmlPredicate theNewPred = (JmlPredicate)this.getStack().pop();
		JmlInvariant newSelf = new JmlInvariant(self.getTokenReference(),self.modifiers(), self.isRedundantly(), theNewPred);
		this.getStack().push(newSelf);
	};


	@Override
	public void visitJmlEnsuresClause(JmlEnsuresClause self) {
		if (self.isNotSpecified()){
			throw new IllegalArgumentException("Ensures clause is not specified.");
		}
		JmlPredicate thePred = self.predOrNot();
		thePred.accept(this);
		JmlPredicate theNewPred = (JmlPredicate)this.getStack().pop();
		this.posconditionSpecMethodCallData.putAll(entriesBuffer);
		this.entriesBuffer = new IdentityHashMap<JmlFormalParameter, JExpression>();
		JmlEnsuresClause newSelf = new JmlEnsuresClause(self.getTokenReference(), self.isRedundantly(), theNewPred);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitJmlSpecQuantifiedExpression(JmlSpecQuantifiedExpression self) {
		JmlPredicate thePred = self.predicate();
		thePred.accept(this);
		JmlPredicate theNewPred = (JmlPredicate)this.getStack().pop();

		JmlSpecExpression theExpre = self.specExpression();
		theExpre.accept(this);
		JmlSpecExpression theNewExpre = (JmlSpecExpression)this.getStack().pop();

		JmlSpecQuantifiedExpression newSelf = new JmlSpecQuantifiedExpression(self.getTokenReference(), self.oper(), 
				self.quantifiedVarDecls(), theNewPred, theNewExpre );
		this.getStack().push(newSelf);
	}



	@Override
	public void visitJmlSpecExpression(JmlSpecExpression self) {
		JExpression theExpre = self.expression();
		theExpre.accept(this);
		JExpression theNewExpre = (JExpression)this.getStack().pop(); 
		JmlSpecExpression newSelf = new JmlSpecExpression(theNewExpre);
		this.getStack().push(newSelf);

	}


	@Override
	public void visitJmlPredicate(JmlPredicate self) {
		JmlSpecExpression theExpre = self.specExpression();
		JExpression theActualExpression = theExpre.expression();
		theActualExpression.accept(this);
		JExpression theNewActualExpression = (JExpression)this.getStack().pop();
		JmlSpecExpression theNewExpre = new JmlSpecExpression(theNewActualExpression); 
		JmlPredicate newSelf = new JmlPredicate(theNewExpre);
		this.getStack().push(newSelf);

	}


	@Override
	public void visitEqualityExpression(JEqualityExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		this.getStack().push(self);
	}


	@Override
	public void visitAddExpression(JAddExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		this.getStack().push(self);

	}

	@Override
	public void visitArrayAccessExpression(JArrayAccessExpression self) {
		self.prefix().accept(this);
		JExpression newPrefix = (JExpression)this.getStack().pop();

		self.accessor().accept(this);
		JExpression newAccessor = (JExpression)this.getStack().pop();
		JArrayAccessExpression newSelf = new JArrayAccessExpression(self.getTokenReference(), newPrefix, newAccessor);

		Field[] fields = newSelf.getClass().getDeclaredFields();
		Field typeField = null;
		for (Field f : fields){
			if (f.getName().equals("type")){
				f.setAccessible(true);
				typeField = f;
				break;
			}
		}
		try {
			typeField.set(newSelf, self.getType());
			typeField.setAccessible(false);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		newSelf.convertType(self.getType(), null);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitArrayLengthExpression(JArrayLengthExpression self) {
		self.prefix().accept(this);
		JExpression newPrefix = (JExpression)this.getStack().pop(); 
		JArrayLengthExpression newSelf = new JArrayLengthExpression(self.getTokenReference(), newPrefix);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitBitwiseExpression(JBitwiseExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitBooleanLiteral(JBooleanLiteral self) {
		this.getStack().push(self);
	}

	@Override
	public void visitCastExpression(JCastExpression self) {
		self.expr().accept(this);
		JExpression theExpre = (JExpression)this.getStack().pop();
		self.setExpr(theExpre);
		
		this.getStack().push(self);
	}


	@Override
	public void visitCharLiteral(JCharLiteral self) {
		this.getStack().push(self);
	}

	@Override
	public void visitConditionalAndExpression(JConditionalAndExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitConditionalExpression(JConditionalExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		JConditionalExpression newSelf = new JConditionalExpression(self.getTokenReference(), self.cond(), newLeft, newRight);
		newSelf.setType(self.getType());
		this.getStack().push(newSelf);
	}

	@Override
	public void visitConditionalOrExpression(JConditionalOrExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		this.getStack().push(self);
	}

	@Override
	public void visitDivideExpression(JDivideExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}


	@Override
	public void visitFieldExpression(JClassFieldExpression self) {
		this.getStack().push(self);
	}


	@Override
	public void visitInstanceofExpression(JInstanceofExpression self) {
		self.expr().accept(this);
		JExpression theExpre = (JExpression)this.getStack().pop();
		JInstanceofExpression newSelf = new JInstanceofExpression(self.getTokenReference(), theExpre, self.dest());
		this.getStack().push(newSelf);
	}

	//	@Override
	//	public void visitJmlFormalParameter(JmlFormalParameter self) {
	//		this.getStack().push(new JmlFormalParameter(self.getTokenReference(), self.modifiers(), self.getDescription(), self.specializedType(), self.ident()));
	//	}

	@Override
	public void visitLocalVariableExpression(JLocalVariableExpression self) {
		//		self.variable().accept(this);
		//		JLocalVariable theLocVar = (JLocalVariable)this.getStack().pop();
		//		JLocalVariableExpression newSelf = new JLocalVariableExpression(self.getTokenReference(), theLocVar);
		this.getStack().push(self);
	}

	@Override
	public void visitJmlVariableDefinition(JmlVariableDefinition self) {
		JExpression theInit = self.expr();
		if (theInit != null){
			theInit.accept(this);
			JExpression newInit = (JExpression)this.getStack().pop();
			JmlVariableDefinition newSelf = new JmlVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(), newInit);
			this.getStack().push(newSelf);
		} else {
			this.getStack().push(self);
		}
	}

	@Override
	public void visitJmlOldExpression(JmlOldExpression self) {
		JExpression theSpecExpre = self.specExpression();
		theSpecExpre.accept(this);
		JmlSpecExpression theNewSpecExpre = (JmlSpecExpression)this.getStack().pop();
		JmlOldExpression newSelf = new JmlOldExpression(self.getTokenReference(), theNewSpecExpre, "");
		this.getStack().push(newSelf);
	}

	@Override
	public void visitMethodCallExpression(JMethodCallExpression self) {
		if (!self.ident().equals("int_size") && !self.ident().equals("has")){
			String theVarName = this.generateNewReturnParameterName();
			CSpecializedType theReturnType = new CSpecializedType(self.method().returnType());
			JmlFormalParameter theExpre = new JmlFormalParameter(self.getTokenReference(), 0L, 0, theReturnType, theVarName);
			this.entriesBuffer.put(theExpre, self);
			JExpression newSelf = null;
			if (translatingAnInvariant){
				JThisExpression thisExpre = new JThisExpression(self.getTokenReference(), self.prefix().getApparentType().getCClass());
				JmlMemberAccess access = new JmlMemberAccess(self.prefix().getApparentType().getCClass(), JmlMemberAccess.ACC_PUBLIC);
				JmlSourceField theField = new JmlSourceField(access, theVarName, self.getType(), false);
				JClassFieldExpression theFieldExpre = new JClassFieldExpression(self.getTokenReference(), thisExpre, theVarName);
				theFieldExpre.setField(theField);
				newSelf = theFieldExpre;
			}
			else
				newSelf = new JLocalVariableExpression(self.getTokenReference(), theExpre);
			this.getStack().push(newSelf);
		} else {
			JExpression newPrefix;
			if (self.prefix() != null){
				self.prefix().accept(this);
				newPrefix = (JExpression) this.getStack().pop();
			} else {
				newPrefix = null;
			}
			int i = 0;
			for (JExpression arg : self.args()){
				arg.accept(this);
				self.args()[i++] = (JExpression)this.getStack().pop();
			}
			self.setPrefix(newPrefix);

			this.getStack().push(self);
		}
	}


	@Override
	public void visitMinusExpression(JMinusExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitModuloExpression(JModuloExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitMultExpression(JMultExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitNullLiteral(JNullLiteral self) {
		this.getStack().push(self);
	}

	@Override
	public void visitOrdinalLiteral(JOrdinalLiteral self) {
		this.getStack().push(self);
	}

	@Override
	public void visitParenthesedExpression(JParenthesedExpression self){
		JExpression theExpre = self.expr();
		theExpre.accept(this);
		JExpression theNewExpre = (JExpression) this.getStack().pop();
		JParenthesedExpression newSelf = new JParenthesedExpression(self.getTokenReference(), theNewExpre);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitPostfixExpression(JPostfixExpression self) {
		JExpression theExpre = self.expr();
		theExpre.accept(this);
		JExpression theNewExpre = (JExpression)this.getStack().pop();
		JPostfixExpression newSelf = new JPostfixExpression(self.getTokenReference(), self.oper(), theNewExpre);
		this.getStack().push(newSelf);
	}

	@Override
	public void visitPrefixExpression(JPrefixExpression self) {
		JExpression theExpre = self.expr();
		theExpre.accept(this);
		JExpression theNewExpre = (JExpression)this.getStack().pop();
		JPrefixExpression newSelf = new JPrefixExpression(self.getTokenReference(), self.oper(), theNewExpre);
		this.getStack().push(newSelf);

	}

	@Override
	public void visitRealLiteral(JRealLiteral self) {
		this.getStack().push(self);
	}


	/**
	 * If self is a JmlReachExpression, it must not be confused with a class method call.
	 * We assume the arguments of the reach expression do not contain method calls. 
	 * @param self
	 * 
	 */
	@Override
	public void	visitJmlReachExpression(JmlReachExpression self) {
		this.getStack().push(self);
	}



	@Override
	public void visitJmlRelationalExpression(JmlRelationalExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitRelationalExpression(JRelationalExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitShiftExpression(JShiftExpression self) {
		self.left().accept(this);
		JExpression newLeft = (JExpression)this.getStack().pop();
		self.right().accept(this);
		JExpression newRight = (JExpression)this.getStack().pop();
		self.setLeft(newLeft);
		self.setRight(newRight);
		
		this.getStack().push(self);
	}

	@Override
	public void visitStringLiteral(JStringLiteral self) {
		this.getStack().push(self);
	}

	@Override
	public void visitThisExpression(JThisExpression self) {
		this.getStack().push(self);
	}

	@Override
	public void visitTypeNameExpression(JTypeNameExpression self) {
		this.getStack().push(self);
	}

	@Override
	public void visitUnaryExpression(JUnaryExpression self) {
		JExpression theExpre = self.expr();
		theExpre.accept(this);
		JExpression theNewExpre = (JExpression)this.getStack().pop();
		self.setExpr(theNewExpre);
		
		this.getStack().push(self);
	}

	@Override
	public void visitUnaryPromoteExpression(JUnaryPromote self) {
		JExpression theExpre = self.expr();
		theExpre.accept(this);
		JExpression theNewExpre = (JExpression)this.getStack().pop();
		JUnaryPromote newSelf = new JUnaryPromote(theNewExpre, self.getType());
		this.getStack().push(newSelf);
	}


}

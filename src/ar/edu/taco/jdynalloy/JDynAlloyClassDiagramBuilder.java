package ar.edu.taco.jdynalloy;

import java.util.List;
import java.util.Set;

import ar.edu.jdynalloy.ast.JClassInvariant;
import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.ast.JSignature;
import ar.edu.jdynalloy.xlator.JType;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public abstract class JDynAlloyClassDiagramBuilder {

	public static JDynAlloyClassDiagram buildClassDiagram(List<JDynAlloyModule> jdynalloy_src) {

		JDynAlloyClassDiagram class_diagram = new JDynAlloyClassDiagram();

		for (JDynAlloyModule jDynAlloyModule : jdynalloy_src) {
			JSignature signature = jDynAlloyModule.getSignature();
			String signatureId = signature.getSignatureId();
			class_diagram.addSignatureId(signatureId);

			if (signature.isExtension()) {
				String extendedSignatureId = signature.getExtSigId();
				class_diagram.addExtension(signatureId, extendedSignatureId);
			}

			if (signature.getInSignatureId() != null) {
				String superSignatureId = signature.getInSignatureId();
				class_diagram.addSuperSignature(signatureId, superSignatureId);
			}

			Set<JClassInvariant> class_invariants = jDynAlloyModule.getClassInvariants();
			for (JClassInvariant class_invariant : class_invariants) {
				AlloyFormula formula = class_invariant.getFormula();
				class_diagram.addClassInvariant(signatureId, formula);
			}

			Set<JObjectInvariant> object_invariants = jDynAlloyModule.getObjectInvariants();
			for (JObjectInvariant object_invariant : object_invariants) {
				AlloyFormula formula = object_invariant.getFormula();
				class_diagram.addObjectInvariant(signatureId, formula);
			}

			List<JField> fields = jDynAlloyModule.getFields();
			for (JField field : fields) {
				String field_name = field.getFieldVariable().toString();
				JType field_type = field.getFieldType();
				if (field_type.isBinaryRelation() && !field_type.isSpecialType()) { //mfrias: here I am removing those fields pointing to special types.
					Set<String> target_signatures = field_type.to();
					class_diagram.addField(field_name, signatureId, target_signatures);
				}
			}
			
			for (JRepresents spec_field_decl : jDynAlloyModule.getRepresents()) {
				
				AlloyExpression expression = spec_field_decl.getExpression();
				if (expression instanceof ExprJoin) {
					ExprJoin exprJoin = (ExprJoin)expression;
					AlloyExpression left_expr = exprJoin.getRight();
					if (left_expr instanceof ExprVariable) {
						ExprVariable exprVariable = (ExprVariable)left_expr;
						String field_name = exprVariable.getVariable().toString();
						AlloyFormula abstract_pred = spec_field_decl.getFormula();
						class_diagram.addAbstractionPred(signatureId, field_name, abstract_pred);
					}
				}
			}
		}
		return class_diagram;
	}

}

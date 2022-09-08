package ar.edu.taco.alloy.bound;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoCustomScope;
import ar.edu.taco.alloy.AlloyCustomScope;
import ar.edu.taco.alloy.AlloyScope;
import ar.edu.taco.infer.Scope;
import ar.edu.taco.infer.InferredScope;
import ar.edu.taco.jdynalloy.JDynAlloyClassDiagram;
import ar.edu.taco.jdynalloy.JDynAlloyClassDiagramBuilder;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.dynalloy.plugin.AlloyASTPlugin;

public class UBoundPlugin implements AlloyASTPlugin {

	private List<JDynAlloyModule> src_jdynalloy_modules;

	@Override
	public AlloyModule transform(AlloyModule alloyModule) {

		JDynAlloyClassDiagram class_diagram = JDynAlloyClassDiagramBuilder.buildClassDiagram(src_jdynalloy_modules);

		TacoCustomScope taco_custom_scope = TacoConfigurator.getInstance().getTacoCustomScope();
		AlloyCustomScope alloyCustomScope = new AlloyCustomScope(taco_custom_scope);

		
		AlloyScope alloy_scope ;
		if (TacoConfigurator.getInstance().getInferScope() == true) {
			InferredScope inferredScope = InferredScope.getInstance();
			Scope inferred = inferredScope.getInferredScope();
			for (String signature_id : inferredScope.inferred_signature_set()){
				int scope_of;
				if (alloyCustomScope.getCustomAlloyTypes().contains(signature_id)) {
					scope_of = alloyCustomScope.getScopeForAlloySig(signature_id);
					inferred = inferredScope.getInferredScope();
					inferred.setInputScopeInteger(signature_id, scope_of);
				}	
			}
			InferredScope.initialize_inferred_scope(inferred, inferredScope.getInferredAlloyBitwidth(), inferredScope.getInferredConcreteInputScope(), inferredScope.getBoundedConcreteInputScope(), inferredScope.getInferredConcreteProgramScope());
			inferredScope = InferredScope.getInstance();
			alloy_scope = new AlloyScope(inferredScope);
		} else {
			TacoCustomScope taco_scope = TacoConfigurator.getInstance().getTacoCustomScope();
			AlloyCustomScope alloy_custom_scope = new AlloyCustomScope(taco_scope);
			alloy_scope = new AlloyScope(alloy_custom_scope );
		}

		List<UBound> upper_bounds = UBoundRepository.getInstance().getUpperBound(class_diagram, alloy_scope);

		if (upper_bounds != null) {

			Set<Integer> int_literals = new HashSet<Integer>();
			StringBuffer buff = new StringBuffer();

			for (UBound upper_bound : upper_bounds) {
				ExprConstant field_constant = (ExprConstant) upper_bound.getField().getRight();

				Set<AlloyVariable> fieldSet = alloyModule.getGlobalSig().getFields().varSet();
				if (containsField(fieldSet, field_constant.getConstantId())) {

					int_literals.addAll(collect_java_primitive_integer_literals(upper_bound));
					String upper_bound_str = pretty_print_upper_bound(upper_bound);
					buff.append(upper_bound_str);

				}

			}
			String ubounds_str = buff.toString();
			if (!ubounds_str.isEmpty()) {
				UBoundMutator mutator = new UBoundMutator(int_literals);
				mutator.setUpperBoundStr(ubounds_str);
				AlloyModule alloy_module = (AlloyModule) alloyModule.accept(mutator);
				return alloy_module;
			}
		}
		return alloyModule;
	}

	private Set<Integer> collect_java_primitive_integer_literals(UBound upper_bound) {
		ConstantExprCollector collector = new ConstantExprCollector();
		upper_bound.getUpperBoundExpression().accept(collector);
		Set<String> collected_constant_ids = collector.getCollectedConstants();

		Set<Integer> literals = new HashSet<Integer>();
		for (String constant_id : collected_constant_ids) {
			if (constant_id.startsWith("JavaPrimitiveIntegerLiteralMinus")) {
				String number = constant_id.substring("JavaPrimitiveIntegerLiteralMinus".length());
				literals.add(new Integer("-" + number));

			} else if (constant_id.startsWith("JavaPrimitiveIntegerLiteral")) {
				String number = constant_id.substring("JavaPrimitiveIntegerLiteral".length());
				literals.add(new Integer(number));

			}
		}
		return literals;
	}

	private boolean containsField(Set<AlloyVariable> fieldSet, String constantId) {
		for (AlloyVariable v : fieldSet) {
			if (v.toString().equals(constantId))
				return true;
		}
		return false;
	}

	private String pretty_print_upper_bound(UBound upper_bound) {
		StringBuffer buff = new StringBuffer();
		buff.append("//-----UPPER BOUND -----//" + "\n");
		buff.append("fact {" + "\n");
		buff.append("  " + upper_bound.getField().toString() + " in " + "\n");

		AlloyExpression upper_bound_expr = upper_bound.getUpperBoundExpression();
		String upper_bound_expr_str = (String) upper_bound_expr.accept(new UBoundPrettyPrinter());

		buff.append(upper_bound_expr_str + "\n");
		buff.append("}\n");
		return buff.toString();
	}

	public void setSourceJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		this.src_jdynalloy_modules = src_jdynalloy_modules;
	}

}

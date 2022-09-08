package ar.edu.taco.jfsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlTypeDeclaration;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JFormalParameter;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.engine.ITacoStage;
import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.simplejml.SimpleJmlToJDynAlloyContext;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;

public class JfslStage implements ITacoStage {

	private final List<JCompilationUnitType> jml_compilation_units;
	private final List<JDynAlloyModule> jdynalloy_modules;
	private final JfslToJDynAlloyEnv env;

	public JfslStage(List<JCompilationUnitType> simplifiedCompilationUnits, List<JDynAlloyModule> jdynalloy_asts, JmlToSimpleJmlContext jmlToSimpleJmlContext,
			SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext) {
		this.jml_compilation_units = simplifiedCompilationUnits;
		this.jdynalloy_modules = jdynalloy_asts;
		this.env = createJfslToJDynAlloyEnvironment(jmlToSimpleJmlContext, simpleJmlToJDynAlloyContext);
	}

	@Override
	public void execute() {

		for (JCompilationUnitType jml_compilation_unit : jml_compilation_units) {

			JfslTranslatorJmlVisitor translator = new JfslTranslatorJmlVisitor(env);
			jml_compilation_unit.accept(translator);

		}

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			// ineteger literals
			addNewModules(JavaPrimitiveIntegerValue.getInstance().get_integer_literal_modules());

			// long literals
			addNewModules(JavaPrimitiveLongValue.getInstance().get_long_literal_modules());

			// char literals
			addNewModules(JavaPrimitiveCharValue.getInstance().get_char_literal_modules());

			// float literals
			addNewModules(JavaPrimitiveFloatValue.getInstance().get_float_literal_modules());

		}

	}

	private void addNewModules(Collection<JDynAlloyModule> new_modules) {
		for (JDynAlloyModule new_module : new_modules) {
			if (!containsModule(new_module)) {
				this.jdynalloy_modules.add(new_module);
			}
		}
	}

	private boolean containsModule(JDynAlloyModule new_module) {
		for (JDynAlloyModule module : this.jdynalloy_modules) {
			if (module.getModuleId().equals(new_module.getModuleId()))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private JfslToJDynAlloyEnv createJfslToJDynAlloyEnvironment(JmlToSimpleJmlContext jmlToSimpleJmlContext,
			SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext) {

		JfslToJDynAlloyEnv env = new JfslToJDynAlloyEnv();

		for (String old_name : jmlToSimpleJmlContext.get_old_names()) {
			String new_name = jmlToSimpleJmlContext.getNewName(old_name);
			env.putRename(old_name, new_name);
		}

		for (JDynAlloyModule jdynalloy_module : this.jdynalloy_modules) {

			if (simpleJmlToJDynAlloyContext.contains_jdynalloy_node_map(jdynalloy_module)) {

				JmlTypeDeclaration jmlTypeDeclaration = (JmlTypeDeclaration) simpleJmlToJDynAlloyContext.get_simpleJml_node(jdynalloy_module);

				String old_name = jmlTypeDeclaration.getCClass().ident();
				String new_name = jdynalloy_module.getSignature().getSignatureId();

				env.putRename(old_name, new_name);
				env.addJmlTypename(old_name);
				env.addSimpleJmlTypename(new_name);

				env.putSimpleJmlToJDynAlloy(jmlTypeDeclaration, jdynalloy_module);

				ArrayList<JmlMethodDeclaration> methods = (ArrayList<JmlMethodDeclaration>)jmlTypeDeclaration.methods();

				String signatureId = jdynalloy_module.getSignature().getSignatureId();

				for (JmlMethodDeclaration method : methods) {
					JProgramDeclaration jprogramDeclaration = (JProgramDeclaration) simpleJmlToJDynAlloyContext.get_jdynalloy_node(method);

					env.putSimpleJmlToJDynAlloy(method, jprogramDeclaration);

					String methodStringRepresentation;
					if (method.isConstructor())
						methodStringRepresentation = "<init>";
					else
						methodStringRepresentation = method.stringRepresentation();

					Vector<String> parameterIds = new Vector<String>();
					for (int i = 0; i < method.parameters().length; i++) {
						JFormalParameter decl = method.parameters()[i];
						String parameterId = decl.ident();

						parameterIds.add(parameterId);
					}

					env.addMethod(signatureId, methodStringRepresentation, parameterIds);

				}

			}
		}

		return env;
	}

}

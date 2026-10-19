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
package ar.edu.taco.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.IJDynAlloyConfig;
import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.buffer.StaticFieldsModuleBuilder;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.JavaToJDynAlloyManager;
import ar.edu.taco.simplejml.builtin.IBuiltInModule;
import ar.edu.taco.simplejml.builtin.JArithmeticException;
import ar.edu.taco.simplejml.builtin.JAssertionError;
import ar.edu.taco.simplejml.builtin.JBoolean;
import ar.edu.taco.simplejml.builtin.JClass;
import ar.edu.taco.simplejml.builtin.JClassCastException;
import ar.edu.taco.simplejml.builtin.JError;
import ar.edu.taco.simplejml.builtin.JException;
import ar.edu.taco.simplejml.builtin.JIllegalArgumentException;
import ar.edu.taco.simplejml.builtin.JIndexOutOfBoundsException;
import ar.edu.taco.simplejml.builtin.JInteger;
import ar.edu.taco.simplejml.builtin.JNegativeArraySizeException;
import ar.edu.taco.simplejml.builtin.JNoSuchElementException;
import ar.edu.taco.simplejml.builtin.JNullPointerException;
import ar.edu.taco.simplejml.builtin.JObject;
import ar.edu.taco.simplejml.builtin.JPrintStream;
import ar.edu.taco.simplejml.builtin.JRuntimeException;
import ar.edu.taco.simplejml.builtin.JString;
import ar.edu.taco.simplejml.builtin.JSystem;
import ar.edu.taco.simplejml.builtin.JThrowable;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;
import ar.edu.taco.simplejml.builtin.models.JJMLObjectSequence;
import ar.edu.taco.simplejml.builtin.models.JJMLObjectSet;
import ar.edu.taco.utils.FileUtils;

/**
 * Adds TACO's built-in JDynAlloy modules, its models of JDK classes, to the analysis. Some
 * are built in Java ({@code ar.edu.taco.simplejml.builtin} and its {@code models}
 * subpackage, partly in {@code lib/jdynalloy.jar}: {@code java.lang.Object}, the
 * exceptions, {@code String}, {@code System}, {@code PrintStream}, the JML sets and
 * sequences, and, with {@code useJavaArithmetic}, the Java integer, long, float and char
 * values); the others are {@code .djals} files under
 * {@code src/ar/edu/taco/engine/precompiledmodules/}, which this stage only registers as
 * resources for {@link JDynAlloyParsingStage} to parse. The key {@code builtInModules}
 * chooses them (empty, the default, means all); {@code java.lang.Object} is always
 * included. {@code TacoMain.run} runs it after the translation from Java, and also asks it for
 * the static-fields module ({@link #generateStaticFieldsModule()}).
 *
 * <p>The modules come from singletons ({@code getInstance()} of each built-in class,
 * jDynAlloy's {@code StaticFieldsModuleBuilder}) that are never reset, so what they
 * accumulate (literals, static fields) carries over to later runs in the same JVM.
 */
public class PrecompiledModules implements ITacoStage {
	static final private String OUTPUT_DYNALLOY_EXTENSION = ".djals";

	private List<JDynAlloyModule> modules;

	/**
	 * Returns the built-in modules: those given by the constructor, then those added by
	 * {@link #execute()}. The modules registered as resources are not here; they come from
	 * {@link JDynAlloyParsingStage}.
	 *
	 * @return the modules
	 */
	public List<JDynAlloyModule> getModules() {
		return modules;
	}


	/**
	 * Creates the stage for a run with a fixed input, from Stryker's feedback
	 * ({@code TacoMain.run} does not use it when computing the specified output). With
	 * {@code useJavaArithmetic}, registers each {@code Integer}, {@code Long} and
	 * {@code Float} value of the map as a literal of jDynAlloy's
	 * {@code JavaPrimitiveIntegerValue}, {@code JavaPrimitiveLongValue} or
	 * {@code JavaPrimitiveFloatValue} singleton (pinned for Stryker), and starts the module
	 * list with every literal module those singletons hold, in no particular order (a
	 * {@code HashSet}). Without Java arithmetic the list starts empty.
	 *
	 * <p>Unclear: only the map's own values are looked at, not the fields of the objects in
	 * it, for which jDynAlloy's {@code processInputToFix} creates literals during the
	 * translation, after this list is built; whether those literals reach the model was not
	 * checked.
	 *
	 * @param inputToFix the input to fix: {@code "thiz"} and the parameter names to their
	 *        values. See docs/SPEC-OUTPUT.md ("inputToFix")
	 */
	public PrecompiledModules(HashMap<String, Object> inputToFix) {
		HashSet<JDynAlloyModule> mySet = new HashSet<JDynAlloyModule>();
		for (String key : inputToFix.keySet()){
			if (inputToFix.get(key) != null && inputToFix.get(key).getClass().equals(Integer.class)){
				if (TacoConfigurator.getInstance().getUseJavaArithmetic()){
					ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant num = JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral( ( (Integer)inputToFix.get(key) ).intValue(), true);
					mySet.addAll(JavaPrimitiveIntegerValue.getInstance().get_integer_literal_modules());
				} 
			} else if (inputToFix.get(key) != null && inputToFix.get(key).getClass().equals(Long.class)){
				if (TacoConfigurator.getInstance().getUseJavaArithmetic()){
					ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant num = JavaPrimitiveLongValue.getInstance().toJavaPrimitiveLongLiteral( ( (Long)inputToFix.get(key) ).longValue(), true);
					mySet.addAll(JavaPrimitiveLongValue.getInstance().get_long_literal_modules());
				}
			} else if (inputToFix.get(key) != null && inputToFix.get(key).getClass().equals(Float.class)){
				if (TacoConfigurator.getInstance().getUseJavaArithmetic()){
					ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant num = JavaPrimitiveFloatValue.getInstance().toJavaPrimitiveFloatLiteral( ( (Float)inputToFix.get(key) ).floatValue(), true);
					mySet.addAll(JavaPrimitiveFloatValue.getInstance().get_float_literal_modules());
				}
			}
		}
		this.modules = new ArrayList<JDynAlloyModule>();
		for (JDynAlloyModule jdm : mySet){
			this.modules.add(jdm);
		}
	}


	/** Creates the stage for a run without a fixed input; the module list starts empty. */
	public PrecompiledModules() {
		this.modules = new ArrayList<JDynAlloyModule>();
	}	

	/**
	 * Adds the built-in modules selected by {@code builtInModules} to {@link #getModules()}
	 * and writes each to {@code <output_dir>/<module id with every _ as />.djals}. Registers
	 * the chosen {@code .djals} resources with
	 * {@code TacoConfigurator.addDynAlloyParserInputResources}; some are chosen by
	 * {@code useJavaArithmetic} ({@code java_util_List_int.djals} or
	 * {@code java_util_List_JavaPrimitiveIntegerValue.djals}, {@code java_lang_AlloyIntArray}
	 * or {@code java_lang_IntArray}, ...). With {@code useClassSingletons}, the {@code Class}
	 * module is added twice, once here and once with the selected modules. Reads the
	 * configuration through both {@code JDynAlloyConfig.getInstance()} and
	 * {@code TacoConfigurator.getInstance()} (the same object in a TACO run).
	 *
	 * @throws java.util.NoSuchElementException if {@code useClassSingletons} is not set in
	 *         the configuration (it has no default)
	 */
	@Override
	public void execute() {
		List<IBuiltInModule> precompiledModules = getPrecompiledModules();

		if (TacoConfigurator.getInstance().getBoolean(
				TacoConfigurator.USE_CLASS_SINGLETONS_FIELD) == true)
			precompiledModules.add(JClass.getInstance());

		for (IBuiltInModule precompiledModule : precompiledModules) {
			JDynAlloyModule module = precompiledModule.getModule();
			modules.add(module);
			// print to file
			printToFile(module);
		}
	}

	/**
	 * Returns the {@code ClassFields} module, which declares the static fields of the
	 * translated classes and of the built-in modules (e.g. {@code Boolean}'s constants,
	 * {@code System.out}), with their static invariants and constraints, and writes it to
	 * {@code <output_dir>/ClassFields.djals}. It is built from jDynAlloy's
	 * {@code StaticFieldsModuleBuilder} singleton, which the translation from Java and the
	 * built-in modules fill and nothing empties, so it also holds the static fields seen by
	 * earlier runs in the same JVM. Call after the translation from Java and
	 * {@link #execute()}.
	 *
	 * @return a new module over the singleton's current fields
	 */
	public JDynAlloyModule generateStaticFieldsModule() {
		JDynAlloyModule module = StaticFieldsModuleBuilder.getInstance()
				.getModule();

		// print to file
		printToFile(module);

		return module;
	}

	private List<IBuiltInModule> getPrecompiledModules() {
		IJDynAlloyConfig config = JDynAlloyConfig.getInstance();

		// precompiled

		List<IBuiltInModule> precompiledModules = new ArrayList<IBuiltInModule>();
		// can't be removed
		precompiledModules.add(JObject.getInstance());

		boolean empty = config.getBuiltInModules().isEmpty();

		// java.lang
		if (empty || config.getBuiltInModules().contains("JString"))
			precompiledModules.add(JString.getInstance());

		if (empty || config.getBuiltInModules().contains("JSystem"))
			precompiledModules.add(JSystem.getInstance());

		if (empty || config.getBuiltInModules().contains("JThrowable"))
			precompiledModules.add(JThrowable.getInstance());

		if (empty || config.getBuiltInModules().contains("JException"))
			precompiledModules.add(JException.getInstance());

		if (empty || config.getBuiltInModules().contains("JRuntimeException"))
			precompiledModules.add(JRuntimeException.getInstance());

		if (empty
				|| config.getBuiltInModules().contains("JNullPointerException"))
			precompiledModules.add(JNullPointerException.getInstance());

		if (empty
				|| config.getBuiltInModules().contains("JArithmeticException"))
			precompiledModules.add(JArithmeticException.getInstance());

		if (empty
				|| config.getBuiltInModules().contains("JIndexOutOfBoundsException"))
			precompiledModules.add(JIndexOutOfBoundsException.getInstance());

		if (empty
				|| config.getBuiltInModules().contains("JError"))
			precompiledModules.add(JError.getInstance());

		if (empty
				|| config.getBuiltInModules().contains("JAssertionError"))
			precompiledModules.add(JAssertionError.getInstance());

		if (empty
				|| config.getBuiltInModules().contains("JNegativeArraySizeException"))
			precompiledModules.add(JNegativeArraySizeException.getInstance());

		if (empty
				|| config.getBuiltInModules().contains("JIllegalArgumentException"))
			precompiledModules.add(JIllegalArgumentException.getInstance());

		if (empty || config.getBuiltInModules().contains("JClassCastException"))
			precompiledModules.add(JClassCastException.getInstance());

		if (empty || config.getBuiltInModules().contains("JInteger"))
			precompiledModules.add(JInteger.getInstance());

		if (empty || config.getBuiltInModules().contains("JBoolean"))
			precompiledModules.add(JBoolean.getInstance());

		if (empty || config.getBuiltInModules().contains("JList")) {

			String resource_to_load;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				resource_to_load = "ar/edu/taco/engine/precompiledmodules/java_util_List_JavaPrimitiveIntegerValue.djals";
			} else {
				resource_to_load = "ar/edu/taco/engine/precompiledmodules/java_util_List_int.djals";
			}
			TacoConfigurator.getInstance().addDynAlloyParserInputResources(
					resource_to_load);
		}

		//java_io
		if (empty || config.getBuiltInModules().contains("java_io_PrintStream")){
			precompiledModules.add(JPrintStream.getInstance());
		}


		if (empty || config.getBuiltInModules().contains("java_util_ArrayList")){
			String resource_to_load = resource_to_load = "ar/edu/taco/engine/precompiledmodules/java_util_ArrayList.djals";

			TacoConfigurator.getInstance().addDynAlloyParserInputResources(
					resource_to_load);
		}	

		if (empty || config.getBuiltInModules().contains("JJMLObjectSequence"))
			precompiledModules.add(JJMLObjectSequence.getInstance());

		if (empty || config.getBuiltInModules().contains("JJMLObjectSet"))
			precompiledModules.add(JJMLObjectSet.getInstance());

		if (empty
				|| config.getBuiltInModules().contains(
						"JNoSuchElementException"))
			precompiledModules.add(JNoSuchElementException.getInstance());

		// BEGIN: Parsed jdals

		if (empty || config.getBuiltInModules().contains("JByte"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_Byte.djals");

		if (empty || config.getBuiltInModules().contains("JInteger")){
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == false) {
				TacoConfigurator
				.getInstance()
				.addDynAlloyParserInputResources(
						"ar/edu/taco/engine/precompiledmodules/java_lang_Integer.djals");
			} else {
				TacoConfigurator
				.getInstance()
				.addDynAlloyParserInputResources(
						"ar/edu/taco/engine/precompiledmodules/java_lang_Integer_JavaPrimitiveIntegerValue.djals");
			}
		}

		if (empty || config.getBuiltInModules().contains("JCharacter"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_Character.djals");

		if (empty || config.getBuiltInModules().contains("JMap")) {
			String resource_to_load;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
				resource_to_load = "ar/edu/taco/engine/precompiledmodules/java_util_Map.djals";
			else
				resource_to_load = "ar/edu/taco/engine/precompiledmodules/java_util_Map_int.djals";

			TacoConfigurator.getInstance().addDynAlloyParserInputResources(
					resource_to_load);
		}

		if (empty || config.getBuiltInModules().contains("JTreeMap"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_util_TreeMap.djals");

		if (empty || config.getBuiltInModules().contains("JSortedMap"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_util_SortedMap.djals");

		if (empty || config.getBuiltInModules().contains("JHashMap"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_util_HashMap.djals");

		if (empty || config.getBuiltInModules().contains("JSet")) {

			String resource_to_load;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				resource_to_load = "ar/edu/taco/engine/precompiledmodules/java_util_Set.djals";
			} else {
				resource_to_load = "ar/edu/taco/engine/precompiledmodules/java_util_Set_int.djals";
			}
			TacoConfigurator.getInstance().addDynAlloyParserInputResources(
					resource_to_load);
		}

		if (empty || config.getBuiltInModules().contains("JHashSet"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_util_HashSet.djals");

		if (empty || config.getBuiltInModules().contains("JIterator"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_util_Iterator.djals");

		if (empty || config.getBuiltInModules().contains("JDate"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_util_Date.djals");

		if (empty || config.getBuiltInModules().contains("JSource"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/javax_xml_transform_Source.djals");

		if (empty || config.getBuiltInModules().contains("JSAXSource"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/javax_xml_transform_sax_SAXSource.djals");

		if (empty || config.getBuiltInModules().contains("JAuditLogXMLReader"))
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/sos_koa_AuditLogXMLReader.djals");



		// END: Parsed jdals

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
			precompiledModules.add(JavaPrimitiveIntegerValue.getInstance());
			precompiledModules.add(JavaPrimitiveLongValue.getInstance());
			precompiledModules.add(JavaPrimitiveFloatValue.getInstance());
			precompiledModules.add(JavaPrimitiveCharValue.getInstance());

		}

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_IntArray.djals");
		else
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_AlloyIntArray.djals");

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_CharArray.djals");

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_LongArray.djals");

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_ObjectArray.djals");
		else
			TacoConfigurator
			.getInstance()
			.addDynAlloyParserInputResources(
					"ar/edu/taco/engine/precompiledmodules/java_lang_AlloyIntObjectArray.djals");

		if (config.getUseClassSingletons() == true)
			precompiledModules.add(JClass.getInstance());

		return precompiledModules;
	}

	private void printToFile(JDynAlloyModule module) {

		String output_dir = TacoConfigurator.getInstance().getOutputDir();

		String filePath = output_dir + java.io.File.separator
				+ module.getModuleId().replaceAll("_", "/")
				+ OUTPUT_DYNALLOY_EXTENSION;
		try {
			String moduleOutput = JavaToJDynAlloyManager
					.getModuleOutput(module);
			FileUtils.writeToFile(filePath, moduleOutput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

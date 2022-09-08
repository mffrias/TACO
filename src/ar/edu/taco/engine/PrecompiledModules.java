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
import ar.edu.taco.simplejml.builtin.JBoolean;
import ar.edu.taco.simplejml.builtin.JClass;
import ar.edu.taco.simplejml.builtin.JClassCastException;
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

public class PrecompiledModules implements ITacoStage {
	static final private String OUTPUT_DYNALLOY_EXTENSION = ".djals";

	private List<JDynAlloyModule> modules;

	public List<JDynAlloyModule> getModules() {
		return modules;
	}


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


	public PrecompiledModules() {
		this.modules = new ArrayList<JDynAlloyModule>();
	}	

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

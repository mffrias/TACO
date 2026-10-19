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
package ar.edu.taco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ar.edu.jdynalloy.IJDynAlloyConfig;
import ar.edu.jdynalloy.JDynAlloyConfig.LoopResolutionEnum;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.taco.alloy.AlloyCustomScope;
import ar.edu.taco.infer.InferredScope;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;

/**<p>Handles TACO analysis configuration information.</p>
 * <h3>Integers</h3>
 * <p>TACO can analyse code using Alloy integers or Java-like Integers.
 * In either case, the meaning of the bitwidth value is the same: a bound 
 * in the count of numbers TACO will deal with. In particular, it states that 
 * the range of integers used in the analysis include from -2^{bitwidth-1} 
 * to 2^{bitwidth-1}-1.</p>
 * <p>Besides that, TACO can try to infer the value of the scopes to be used
 * for the analysis. If the custom bitwidth is a non positive integer 
 * <b>and</b> the scope inferring feature is activated, the bitwidth is also 
 * inferred. Otherwise, the bitwidth value setted is used.</p>
 * 
 * @author elgaby
 * 
 */
public class TacoConfigurator extends PropertiesConfiguration implements
		IJDynAlloyConfig {

	private String externalMethodNameWithParameters = "";
	
	private static final String ABSTRACT_SIGNATURE_OBJECT_FIELD = "abstractSignatureObject";

	private static final boolean DEFAULT_ABSTRACT_SIGNATURE_OBJECT = true;

	public static final String USE_CUSTOM_RELATIONAL_OVERRIDE = "useCustomRelationalOverride";
	public static final String CLASSES_FIELD = "classes";
	public static final String RELEVANT_CLASSES = "relevantClasses";
	public static final String DYNAMIC_JAVA_LANG_FIELDS = "dynamicJavaLangFields";
	public static final String ASSERT_IS_ASSUME = "assertIsAssume";
	public static final String USE_CLASS_SINGLETONS_FIELD = "useClassSingletons";
	public static final String CLASS_EXTENDS_OBJECT_FIELD = "classExtendsObject";
	public static final String TYPE_SAFETY_FIELD = "typeSafety";
	public static final String CLASS_TO_CHECK_FIELD = "classToCheck";
	public static final String METHOD_TO_CHECK_FIELD = "methodToCheck";
	
	public static final String CHECK_NULL_DE_REFERENCE_FIELD = "checkNullDereference";
	
	public static final String CHECK_ARITHMETIC_EXCEPTION = "checkArithmeticException";
	private static final boolean DEFAULT_CHECK_ARITHMETIC_EXCEPTION = true;

	
	public static final String QUALIFIERS_INCLUDES_NULL = "quantifierIncludesNull";
	public static final String SKOLEMIZE_INSTANCE_INVARIANT = "skolemizeInstanceInvariant";// (boolean)
	private static final boolean SKOLEMIZE_INSTANCE_INVARIANT_DEFAULT = false;
	public static final String SKOLEMIZE_INSTANCE_ABSTRACTION = "skolemizeInstanceAbstraction";// (boolean)
	private static final boolean SKOLEMIZE_INSTANCE_ABSTRACTION_DEFAULT = false;

	public static final String BUILTIN_MODULES_FIELD = "builtInModules";

	public static final String DYNALLOY_TO_ALLOY_ENABLE = "dynalloy.toAlloy.enable";
	public static final String DYNALLOY_TO_ALLOY_LOOP_UNROLL = "dynalloy.toAlloy.loopUnroll";// (int)

	public static final int DEFAULT_DYNALLOY_TO_ALLOY_LOOP_UNROLL = 3;

	public static final String DYNALLOY_TO_ALLOY_STRICT_UNROLLING = "dynalloy.toAlloy.strictUnrolling";// (boolean)
	public static final String DYNALLOY_TO_ALLOY_REMOVE_QUANTIFIERS = "dynalloy.toAlloy.removeQuantifiers";// (bool)
	public static final boolean DEFAULT_DYNALLOY_TO_ALLOY_REMOVE_QUANTIFIERS = true;

	public static final String DYNALLOY_TO_ALLOY_APPLY_SIMPLIFICATIONS = "dynalloy.toAlloy.applySimplifications";// (bool)

	public static final String JMLPARSER_ENABLED = "jmlParser.enabled";
	public static final boolean JMLPARSER_ENABLED_DEFAULT = true;

	public static final String JMLPARSER_APP_CLASS_PATH = "jmlParser.appClassPath";
	public static final String JMLPARSER_SOURCE_PATH_STR = "jmlParser.sourcePathStr";

	private static final boolean DEFAULT_JML_OBJECT_SEQUENCE_TO_ALLOY_SEQUENCE = false;
	private static final String JML_OBJECT_SEQUENCE_TO_ALLOY_SEQUENCE = "JMLObjectSequenceToAlloySequence";

	private static final String NEW_EXCEPTIONS_ARE_LITERALS = "newExceptionsAreLiterals";
	private static final boolean DEFAULT_NEW_EXCEPTIONS_ARE_LITERALS = false;

	private static final String JML_OBJECT_SET_TO_ALLOY_SET = "JMLObjectSetToAlloySet";
	private static final boolean DEFAULT_JML_OBJECT_SET_TO_ALLOY_SET = false;

	public static final String JDYNALLOY_PARSER_ENABLED = "jdynalloy.parser.enabled";
	public static final boolean JDYNALLOY_PARSER_ENABLED_DEFAULT = true;

	public static final String JDYNALLOY_PARSER_INPUT_FILES = "jdynalloy.parser.inputFiles";
	public static final String JDYNALLOY_PARSER_INPUT_RESOURCES = "jdynalloy.parser.inputResources";
	/**
	 * Class-path resources of JDynAlloy modules registered during the run
	 * ({@link #addDynAlloyParserInputResources(String)}, by {@code PrecompiledModules}), parsed
	 * with those of {@code jdynalloy.parser.inputResources}. Per configuration object, so it
	 * starts empty in every run.
	 */
	public Set<String> dynAlloyParserInputResourcesExtra = new HashSet<String>();

	private static final String BUILD_JAVA_TRACE = "buildJavaTrace";
	
	private static final String NUMERIC_RANGE_QUANTIFICATION_LOWER = "numericRangeLower";
	private static final int DEFAULT_NUMERIC_RANGE_QUANTIFICATION_LOWER = -4;
	
	private static final String NUMERIC_RANGE_QUANTIFICATION_UPPER = "numericRangeUpper";
	private static final int DEFAULT_NUMERIC_RANGE_QUANTIFICATION_UPPER = 3;


	
	private static final boolean DEFAULT_BUILD_JAVA_TRACE = false;

	private TacoCustomScope tacoScope;

	/**
	 * Returns the scope built by the constructor from {@code int.bitwidth} (default 4),
	 * {@code objectScope} (default 3), {@code dynalloy.toAlloy.loopUnroll} (default 3),
	 * {@code useMaxSequenceLength} (default {@code true}: sequences of up to
	 * 2<sup>bitwidth-1</sup>-1 elements) and {@code type_scopes} ({@code Class:N,...}). Later
	 * changes to those keys, and {@link #overrideBitwidth(int)}, do not change it.
	 *
	 * @return the scope of this configuration
	 */
	public TacoCustomScope getTacoCustomScope() {
		return tacoScope;
	}

	public static final String ASSERTION_ARGUMENTS = "assertionArguments";

	public static final String GENERATE_CHECK = "generateCheck";
	private static final boolean DEFAULT_GENERATE_CHECK = true;

	public static final String GENERATE_RUN = "generateRun";
	private static final boolean DEFAULT_GENERATE_RUN = false;

	public static final String BITWIDTH = "int.bitwidth";
	private static final int DEFAULT_BITWIDTH = 4;

	private static final String STRING_BITWIDTH = "string.bitwidth";
	private static final int DEFAULT_STRING_BITWIDTH = 3;

	private static final String LOOP_RESOLUTION = "loopResolution";
	private static final String LOOP_RESOLUTION_DEFAULT = "AnnotatedLoop";

	private static final String ARRAY_IS_INT = "arrayIsInt";
	private static final boolean DEFAULT_ARRAY_IS_INT = false;

	public static final String OBJECT_SCOPE = "objectScope";
	private static final int OBJECT_SCOPE_DEFAULT = 3;

	public static final String MODULAR_REASONING = "modular_reasoning";
	private static final boolean MODULAR_REASONING_DEFAULT = false;

	public static final String INCLUDE_SIMULATION_PROGRAM_DECLARATION = "include_simulation_program_declaration";
	private static final boolean INCLUDE_SIMULATION_PROGRAM_DECLARATION_DEFAULT = false;

	private static final String OUTPUT_MODULAR_JDYNALLOY = "output_modular_JDynAlloy";
	private static final String OUTPUT_MODULAR_JDYNALLOY_DEFAULT = null;

	private static final String INCLUDE_BRANCH_LABELS = "include_branch_labels";
	private static final boolean INCLUDE_BRANCH_LABELS_DEFAULT = false;

	public static final String RELEVANCY_ANALYSIS = "relevancyAnalisys";
	private static final boolean RELEVANCY_ANALYSIS_DEFAULT = false;

	private static final String DISABLE_INTEGER_LITERAL_REDUCTION = "disableIntegerLiteralReduction";
	private static final boolean DISABLE_INTEGER_LITERAL_REDUCTION_DEFAULT = false;

	private static final String USE_MAX_SEQUENCE_LENGTH = "useMaxSequenceLength";
	private static final boolean DEFAULT_USE_MAX_SEQUENCE_LENGTH = true;

	private static final String NO_VERIFY = "noVerify";
	private static final boolean NO_VERIFY_DEFAULT = false;

	private static final String INCREASE_BITWIDTH_USING_LITERALS = "increaseBitwidthUsingLiterals";
	private static final boolean INCREASE_BITWIDTH_USING_LITERALS_DEFAULT = false;

	public static final String USE_JAVA_ARITHMETIC = "useJavaArithmetic";
	private static final boolean USE_JAVA_ARITHMETIC_DEFAULT = false;

	public static final String GENERATE_UNIT_TEST_CASE = "generateUnitTestCase";
	private static final boolean GENERATE_UNIT_TEST_CASE_DEFAULT = false;

	public static final String USE_JAVA_SBP = "useJavaSBP";
	private static final boolean USE_JAVA_SBP_DEFAULT = false;

	public static final String USE_TIGHT_UPPER_BOUNDS = "useTightUpperBounds";
	private static final boolean USE_TIGHT_UPPER_BOUNDS_DEFAULT = false;

	private static final String OUTPUT_DIR = "output_dir";
	private static final String OUTPUT_DIR_DEFAULT = "output";

	private static final String TYPE_SCOPES = "type_scopes";

	private static final String INFER_SCOPE = "inferScope";
	private static final boolean DEFAULT_INFER_SCOPE = false;

	private static final String REMOVE_EXIT_WHILE_GUARD = "removeExitWhileGuard";
	private static final boolean DEFAULT_REMOVE_EXIT_WHILE_GUARD = false;

	private static final String NESTED_LOOP_UNROLL = "nestedLoopUnroll";
	private static final boolean DEFAULT_NESTED_LOOP_UNROLL = true;

	public static final String ATTEMPT_TO_CORRECT_BUG = "attemptToCorrectBug";
	private static final boolean ATTEMPT_TO_CORRECT_BUG_DEFAULT = false;
	
	public static final String MAX_STRYKER_METHODS_FOR_FILE = "maxStrykerMethodForFile";
	private static final int DEFAULT_MAX_STRYKER_METHODS_FOR_FILE = 50;

	/**
	 * Static: classes that {@code JmlParser} does not look up a source file for, even when they
	 * are listed for parsing ({@code java.util.Set}). A public array, so it could be changed;
	 * nothing does.
	 */
	public static final String[] aux_classes = new String[]{"java.util.Set"};
	
	private static TacoConfigurator instance;

	/**
	 * Builds the configuration of one run and makes it the one {@link #getInstance()} returns.
	 * jDynAlloy's {@code JDynAlloyConfig.buildConfig} calls it by reflection, because the
	 * configuration file names this class in its key {@code DynJAlloyConfig.Class}, so the same
	 * object is also jDynAlloy's configuration; {@code TacoMain.run} builds one per run.
	 *
	 * <p>Loads {@code configurationFile}, then sets each overriding property on top, replacing
	 * the file's value. Values are split into lists at commas (Commons Configuration), so for
	 * {@code methodToCheck} the stored property has its commas turned into dots and its spaces
	 * removed (e.g. {@code pkg_C_m(int.int)}), while the value as given, without spaces, is kept
	 * for {@link #getMethodToCheck()}. Finally builds the {@link TacoCustomScope}
	 * ({@link #getTacoCustomScope()}).
	 *
	 * @param configurationFile path of the properties file (e.g.
	 *        {@code config/genericTest.properties})
	 * @param overridingProperties the driver's keys; {@code methodToCheck} must be a
	 *        {@code String}
	 * @throws TacoException if the file cannot be loaded
	 */
	public TacoConfigurator(String configurationFile,
			Properties overridingProperties) {
		super();
		try {
			super.load(configurationFile);
			for (Entry<Object, Object> entry : overridingProperties.entrySet()) {
				Object value = null;
				if (((String) entry.getKey()).equals("methodToCheck")){
					value = ((String)entry.getValue()).replace(',', '.');
					value = ((String)value).replaceAll(" ", "");
					externalMethodNameWithParameters = ((String)entry.getValue());
					externalMethodNameWithParameters = ((String)externalMethodNameWithParameters).replaceAll(" ", "");
				} else {
					value = entry.getValue();
				}
				this.setProperty((String) entry.getKey(), value);
			}
			TacoCustomScope tacoScope = buildTacoScope();
			this.tacoScope = tacoScope;
			instance = this;
		} catch (ConfigurationException e) {
			throw new TacoException(e);
		}
	}

	/**
	 * Returns a new set with the classes of {@link #aux_classes}.
	 *
	 * @return {@code {"java.util.Set"}}
	 */
	public static Set<String>get_aux_classes_set(){
		Set<String> ss = new HashSet<String>();
		for (String s : aux_classes){
			ss.add(s);
		}
		return ss;
	}
	
	private TacoCustomScope buildTacoScope() {
		TacoCustomScope taco_scope = new TacoCustomScope();
		taco_scope.setAlloyBitwidth(this.getBitwidth());
		taco_scope.setTopLevelScope(this.getObjectScope());
		taco_scope.setUnroll(this.getDynAlloyToAlloyLoopUnroll());

		if (this.getUseMaxSequenceLength()) {
			int max_alloy_seq_length = (int) Math.pow(2,
					taco_scope.getAlloyBitwidth() - 1) - 1;
			taco_scope.setMaxAlloySequenceLength(Math.max(0, max_alloy_seq_length));
		}

		String[] typeScopes = this.getTypeScopes();
		if (typeScopes != null) {
			for (int i = 0; i < typeScopes.length; i++) {
				String[] pair = typeScopes[i].split(":");
				String typename = pair[0].trim();
				String scope_str = pair[1];
				int scope = new Integer(scope_str);
				taco_scope.setTypeScope(typename, scope);
			}
		}
		return taco_scope;
	}

	/**
	 * Returns the configuration that was built last, the singleton every stage reads. It is a
	 * static field that only the constructor sets: {@code JDynAlloyConfig.reset()} does not
	 * clear it, so between runs, and in other threads during a run, this returns the last
	 * configuration built in the JVM.
	 *
	 * @return the last configuration built
	 * @throws IllegalStateException if no configuration has been built yet
	 */
	public static TacoConfigurator getInstance() {
		if (instance == null) {
			// instance = new DynJML4AlloyConfigurator();
			throw new IllegalStateException("Configuration has not been set");
		}
		return instance;
	}

	/**
	 * Returns {@code abstractSignatureObject} (default {@code true}): whether the Alloy
	 * signature {@code java_lang_Object} is abstract ({@code JObject}).
	 *
	 * @return the value
	 */
	@Override
	public boolean getAbstractSignatureObject() {
		return this.getBoolean(ABSTRACT_SIGNATURE_OBJECT_FIELD,
				DEFAULT_ABSTRACT_SIGNATURE_OBJECT);
	}

	/**
	 * Returns {@code assertIsAssume} (no default). No caller.
	 *
	 * @return the value
	 * @throws java.util.NoSuchElementException if the key is not set
	 */
	@Override
	public boolean getAssertIsAssume() {
		return this.getBoolean(ASSERT_IS_ASSUME);
	}

	/**
	 * Returns {@code builtInModules}: the names of the built-in modules to include
	 * ({@code JString}, {@code JList}, {@code java_util_ArrayList}, ...; see
	 * {@code PrecompiledModules}). Empty, the default, means all of them.
	 *
	 * @return the names, or an empty list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBuiltInModules() {
		return (List<String>) this.getList(BUILTIN_MODULES_FIELD);
	}

	/**
	 * Returns {@code checkNullDereference} (no default): whether a null dereference is an error
	 * of the method. No caller: TACO's {@code JDynAlloyASTVisitor} reads the key directly.
	 *
	 * @return the value
	 * @throws java.util.NoSuchElementException if the key is not set
	 */
	@Override
	public boolean getCheckNullDereference() {
		return this.getBoolean(CHECK_NULL_DE_REFERENCE_FIELD);
	}
	
	/**
	 * Returns {@code checkArithmeticException} (default {@code true}): whether integer division
	 * and remainder by zero are errors of the method ({@code AuxiliaryConstantsFactory}).
	 *
	 * @return the value
	 */
	@Override
	public boolean getCheckDivisionByZero() {
		return this.getBoolean(CHECK_ARITHMETIC_EXCEPTION, DEFAULT_CHECK_ARITHMETIC_EXCEPTION);
	}


	/**
	 * Returns {@code classExtendsObject} (no default), read by jDynAlloy's
	 * {@code JDynAlloyXlatorVisitor} when it translates the module {@code Class}.
	 *
	 * @return the value
	 * @throws java.util.NoSuchElementException if the key is not set
	 */
	@Override
	public boolean getClassExtendsObject() {
		return this.getBoolean(CLASS_EXTENDS_OBJECT_FIELD);
	}

	/**
	 * Returns {@code classToCheck} as an Alloy name: package dots as underscores, {@code $} as
	 * {@code _inner_} (e.g. {@code icse_singlylinkedlist_set1_SinglyLinkedListContains1Bug25D}).
	 *
	 * @return the normalized class name
	 * @throws TacoException if the key is not set
	 */
	@Override
	public String getClassToCheck() {
		if (this.getString(CLASS_TO_CHECK_FIELD) == null) {
			throw new TacoException(
					"Config key 'CLASS_TO_CHECK_FIELD' is mandatory");
		}

		JavaClassNameNormalizer classToCheckNormalizer = new JavaClassNameNormalizer(
				this.getString(CLASS_TO_CHECK_FIELD));
		return classToCheckNormalizer.getQualifiedClassName();
	}

	/**
	 * Returns the classes to parse and translate besides {@code classToCheck}: {@code classes}
	 * if the key is set, otherwise {@code relevantClasses}, as written (Java names with dots).
	 * With {@code relevancyAnalisys}, {@code JDynAlloyStage} replaces {@code relevantClasses}
	 * during the run, so later calls may return the computed classes.
	 *
	 * @return the class names; empty if neither key is set
	 */
	@Override
	public String[] getClasses() {
		Object[] classes;
		if (this.containsKey(CLASSES_FIELD)) {
			classes = this.getList(CLASSES_FIELD).toArray();
		} else {
			classes = this.getList(RELEVANT_CLASSES).toArray();
		}

		String[] returnValue = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			String elem = (String) classes[i];
			returnValue[i] = elem;
		}
		return returnValue;
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean getDynJAlloyAnnotations() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Returns {@code dynamicJavaLangFields} (no default), read by jDynAlloy's
	 * {@code PostconditionExprMutator}: when {@code false}, {@code intValue} is not primed in
	 * postconditions.
	 *
	 * @return the value
	 * @throws java.util.NoSuchElementException if the key is not set
	 */
	@Override
	public boolean getDynamicJavaLangFields() {
		return this.getBoolean(DYNAMIC_JAVA_LANG_FIELDS);
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean getIgnoreJmlAnnotations() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean getIgnoreJsflAnnotations() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Returns {@code loopResolution} (default {@code AnnotatedLoop}; also {@code Invariant} or
	 * {@code InvariantWithValidation}, in any case). No caller.
	 *
	 * @return the loop resolution
	 * @throws TacoNotImplementedYetException for any other value
	 */
	@Override
	public LoopResolutionEnum getLoopResolution() {
		String value = this.getString(LOOP_RESOLUTION, LOOP_RESOLUTION_DEFAULT);
		if (value.equalsIgnoreCase("AnnotatedLoop")) {
			return LoopResolutionEnum.AnnotatedLoop;
		} else if (value.equalsIgnoreCase("Invariant")) {
			return LoopResolutionEnum.Invariant;
		} else if (value.equalsIgnoreCase("InvariantWithValidation")) {
			return LoopResolutionEnum.InvariantWithValidation;
		}
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Returns the method under analysis as the driver named it, from the overriding properties
	 * given to the constructor, without spaces and with every dot as an underscore, e.g.
	 * {@code pkg_Class_contains(java_lang_Object)} or {@code pkg_Class_m(int,int)}; the commas
	 * between parameter types are kept. A {@code methodToCheck} in the
	 * configuration file is not used here. Read by TACO and jDynAlloy wherever they look for
	 * the method under analysis. See docs/METHOD-NAMING.md.
	 *
	 * @return the method name with its parameter types, or an empty string if the overriding
	 *         properties had no {@code methodToCheck}
	 */
	@Override
	public String getMethodToCheck() {
		String methodToCheck = this.externalMethodNameWithParameters;
		methodToCheck = methodToCheck.replace('.', '_');
		return methodToCheck;

	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public String getOutput() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public String getOutputDynJAlloy() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public String getProject() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Not implemented, although {@code config/genericTest.properties} sets
	 * {@code quantifierIncludesNull}. No caller.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean getQuantifierIncludesNull() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Returns {@code relevantClasses} as Alloy names (see {@link #getClassToCheck()}), plus
	 * {@code ClassFields}, the module of static fields. {@code JDynAlloyStage} slices the
	 * modules to these after the relevancy analysis; jDynAlloy's {@code JDynAlloyContext} reads
	 * them too.
	 *
	 * @return a new list; just {@code ClassFields} if the key is not set
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getRelevantClasses() {
		List<String> returnList = new ArrayList<String>();
		for (String aClassName : (List<String>) this.getList(RELEVANT_CLASSES)) {
			JavaClassNameNormalizer normalizer = new JavaClassNameNormalizer(
					aClassName);
			returnList.add(normalizer.getQualifiedClassName());
		}
		returnList.add(JExpressionFactory.CLASS_FIELDS.getConstantId());

		return returnList;
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean getSimPredicates() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Not implemented, although {@code config/genericTest.properties} sets {@code typeSafety}.
	 * jDynAlloy's {@code JDynAlloyXlatorVisitor} calls it for every module other than
	 * {@code Class} when {@code useClassSingletons} is on, so that option fails here.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean getTypeSafety() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Returns {@code useClassSingletons} (no default): whether each class gets a singleton
	 * {@code Class} object (the built-in module {@code JClass}). See {@link #getTypeSafety()}.
	 *
	 * @return the value
	 * @throws java.util.NoSuchElementException if the key is not set
	 */
	@Override
	public boolean getUseClassSingletons() {
		return this.getBoolean(USE_CLASS_SINGLETONS_FIELD);
	}

	/**
	 * Returns {@code useCustomRelationalOverride} (no default): whether jDynAlloy's
	 * {@code DynalloyFactory.updateField} encodes a field update with a custom override
	 * function instead of Alloy's {@code ++}.
	 *
	 * @return the value
	 * @throws java.util.NoSuchElementException if the key is not set
	 */
	@Override
	public boolean getUseCustomRelationalOverride() {
		return this.getBoolean(USE_CUSTOM_RELATIONAL_OVERRIDE);
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean hasClassToCheck() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Not implemented.
	 *
	 * @return never
	 * @throws TacoNotImplementedYetException always
	 */
	@Override
	public boolean hasMethodToCheck() {
		throw new TacoNotImplementedYetException();
	}

	/**
	 * Use "java_lang_Object" instead of "Object" and so.
	 */
	@Override
	public boolean getUseQualifiedNamesForJTypes() {
		return true;
	}

	/**
	 * Returns {@code skolemizeInstanceAbstraction} (default {@code false}; {@code true} in
	 * {@code config/genericTest.properties}), read by jDynAlloy's {@code JDynAlloyXlatorVisitor}
	 * when it translates program declarations.
	 *
	 * @return the value
	 */
	@Override
	public boolean getSkolemizeInstanceAbstraction() {
		return this.getBoolean(SKOLEMIZE_INSTANCE_ABSTRACTION,
				SKOLEMIZE_INSTANCE_ABSTRACTION_DEFAULT);
	}

	/**
	 * Returns {@code skolemizeInstanceInvariant} (default {@code false}; {@code true} in
	 * {@code config/genericTest.properties}), read by jDynAlloy's {@code JDynAlloyXlatorVisitor}
	 * when it translates program declarations and {@code havoc} statements.
	 *
	 * @return the value
	 */
	@Override
	public boolean getSkolemizeInstanceInvariant() {
		return this.getBoolean(SKOLEMIZE_INSTANCE_INVARIANT,
				SKOLEMIZE_INSTANCE_INVARIANT_DEFAULT);
	}

	/**
	 * Returns {@code JMLObjectSequenceToAlloySequence} (default {@code false}; {@code true} in
	 * {@code config/genericTest.properties}): whether a {@code JMLObjectSequence} is an Alloy
	 * {@code seq}, or an object of the built-in module {@code JJMLObjectSequence}.
	 *
	 * @return the value
	 */
	@Override
	public boolean getJMLObjectSequenceToAlloySequence() {
		return this.getBoolean(JML_OBJECT_SEQUENCE_TO_ALLOY_SEQUENCE,
				DEFAULT_JML_OBJECT_SEQUENCE_TO_ALLOY_SEQUENCE);
	}

	/**
	 * Returns {@code newExceptionsAreLiterals} (default {@code false}; {@code true} in
	 * {@code config/genericTest.properties}): whether {@code new} of an exception class assigns
	 * a literal singleton of that class instead of creating an object ({@code ExpressionVisitor}).
	 *
	 * @return the value
	 */
	@Override
	public boolean getNewExceptionsAreLiterals() {
		return this.getBoolean(NEW_EXCEPTIONS_ARE_LITERALS,
				DEFAULT_NEW_EXCEPTIONS_ARE_LITERALS);
	}

	/**
	 * Returns {@code JMLObjectSetToAlloySet} (default {@code false}; {@code true} in
	 * {@code config/genericTest.properties}): whether a {@code JMLObjectSet} is an Alloy set, or
	 * an object of the built-in module {@code JJMLObjectSet}.
	 *
	 * @return the value
	 */
	@Override
	public boolean getJMLObjectSetToAlloySet() {
		return this.getBoolean(JML_OBJECT_SET_TO_ALLOY_SET,
				DEFAULT_JML_OBJECT_SET_TO_ALLOY_SET);
	}

	/* (non-Javadoc)
	 * @see ar.edu.jdynalloy.IJDynAlloyConfig#getAssertionArguments()
	 */
	/**
	 * Returns the scope of the Alloy {@code check} and {@code run} commands, which
	 * {@code DynAlloyAppendCommandPlugin} appends to the model. It is computed, not read: the key
	 * {@code assertionArguments} (in {@code config/genericTest.properties}, set by
	 * {@link #setAssertionArguments(String)} and by {@code TacoMain}'s {@code -b}) is not used.
	 * <ul>
	 * <li>Without {@code inferScope}: {@code for <objectScope> but <bitwidth> int, <n> seq},
	 *     then {@code , <scope> <signature>} for each class of {@code type_scopes}; a bitwidth
	 *     of 0 or less becomes 4.</li>
	 * <li>With {@code inferScope}: {@code for 0 but exactly <n> <signature>, ...} for each
	 *     signature of the {@code InferredScope} singleton (just {@code <n>} for
	 *     {@code java_lang_Object}), then the bitwidth, the inferred one if {@code int.bitwidth}
	 *     is 0 or less. The {@code type_scopes} are not used: the branch that would use them
	 *     also requires {@code inferScope} to be off.</li>
	 * </ul>
	 *
	 * @return the scope, e.g. {@code for 3 but 4 int, 7 seq, 5 pkg_Node}
	 */
	@Override
	public String getAssertionArguments() {

		String assertionArguments;

		if (this.getInferScope() == true) {
			assertionArguments = build_inferred_scope();
		} else {
			assertionArguments = build_custom_scope();
		}

		return assertionArguments;

	}

	private final List<String> IGNORE_LIST = Arrays.<String> asList("$Root$",
			"java_lang_Throwable", "ClassFields");

	/**<p>Builds the scope specification string using inferred information.</p>
	 * <p>Regarding integers, the inferred bitwidth will be used only if 
	 * the custom bitwidth value is a non positive integer. Otherwise, the custom
	 * value is used instead.</p>
	 * @return
	 */
	private String build_inferred_scope() {
		StringBuffer result = new StringBuffer();
		result.append(" for 0 but ");
		InferredScope inferred_scope = InferredScope.getInstance();

		TacoCustomScope taco_custom_scope = TacoConfigurator.getInstance()
				.getTacoCustomScope();
		AlloyCustomScope alloyScope = new AlloyCustomScope(taco_custom_scope);

		boolean first_elem = true;
		for (String signature_id : inferred_scope.inferred_signature_set()) {

			if (!IGNORE_LIST.contains(signature_id)) {

				if (!first_elem) {
					result.append(",");
				}
				int scope_of;
				if (alloyScope.getCustomAlloyTypes().contains(signature_id) && !TacoConfigurator.getInstance().getInferScope()) {
					scope_of = alloyScope.getScopeForAlloySig(signature_id);
				} else {
					scope_of = inferred_scope.getInferredScope(signature_id);
				}

				if (!signature_id.equals("java_lang_Object"))
				    result.append(" exactly " + scope_of);
				else
				    result.append(" " + scope_of);
				result.append(" ");
				result.append(signature_id);

				first_elem = false;
			}
		}

		if (!first_elem) {
			result.append(",");
		}

		if (alloyScope.getAlloyBitwidth() <= 0) {  
			// mfrias: here we choose the inferred bitwidth (in case the custom 
			// bitwidth == 0), or otherwise we keep the custom bitwidth.
			result.append(String.format("%s int",
				inferred_scope.getInferredAlloyBitwidth()));
		} else {
			result.append(String.format("%s int",
				alloyScope.getAlloyBitwidth()));
		}
		return result.toString();
	}

	private String build_custom_scope() {
		String assertionArguments;
		AlloyCustomScope alloy_scope = new AlloyCustomScope(this.tacoScope);

		assertionArguments = "for " + alloy_scope.getTopLevelScope();

		assertionArguments += " but";

		if (alloy_scope.getAlloyBitwidth() <= 0) {
			assertionArguments += String.format(" %s int", DEFAULT_BITWIDTH); //mfrias: this allows us to use 0 as a valid bitwidth in the config file in order to signal the preference for the inferred bitwidth if scope inference is chosen.
		} else {
			assertionArguments += String.format(" %s int", alloy_scope.getAlloyBitwidth());
		}
		assertionArguments += String.format(", %s seq",
				alloy_scope.getAlloyMaxSequenceLength());

		for (String alloy_signature : alloy_scope.getCustomAlloyTypes()) {
			int signature_scope = alloy_scope
					.getScopeForAlloySig(alloy_signature);
			assertionArguments += String.format(", %s %s", signature_scope,
					alloy_signature);
		}
		return assertionArguments;
	}

	/**
	 * Returns {@code useMaxSequenceLength} (default {@code true}): whether the scope gives
	 * sequences 2<sup>bitwidth-1</sup>-1 elements ({@link #getTacoCustomScope()}).
	 *
	 * @return the value
	 */
	public boolean getUseMaxSequenceLength() {
		return this.getBoolean(USE_MAX_SEQUENCE_LENGTH,
				DEFAULT_USE_MAX_SEQUENCE_LENGTH);
	}

	/**
	 * Sets the property {@code assertionArguments}, with its commas escaped so that it is not
	 * split into a list. Nothing reads that property ({@link #getAssertionArguments()} computes
	 * the scope), and nothing calls this method.
	 *
	 * @param value the scope text, e.g. {@code for 3 but 4 int}
	 */
	public void setAssertionArguments(String value) {
		String v = value.replaceAll(",", "\\\\,");
		this.setProperty(ASSERTION_ARGUMENTS, v);
	}

	/**
	 * Returns {@code objectScope} (default 3): the scope of every signature without its own,
	 * the {@code for N} of the command; also the object limit of the scope inference ({@code
	 * ScopeInference}).
	 *
	 * @return the value
	 */
	public int getObjectScope() {
		return this.getInt(OBJECT_SCOPE, OBJECT_SCOPE_DEFAULT);
	}

	/**
	 * Returns {@code generateCheck} (default {@code true}): whether a {@code check} command is
	 * appended for each assertion ({@code DynAlloyAppendCommandPlugin}).
	 *
	 * @return the value
	 */
	@Override
	public boolean getGenerateCheck() {
		return this.getBoolean(GENERATE_CHECK, DEFAULT_GENERATE_CHECK);
	}

	/**
	 * Returns {@code generateRun} (default {@code false}): whether a {@code run} command is
	 * appended for the program whose id equals {@link #getMethodToCheck()} ({@code
	 * DynAlloyAppendCommandPlugin}). That name has the parameter types and program ids do not,
	 * so under the current naming no {@code run} is appended.
	 *
	 * @return the value
	 */
	@Override
	public boolean getGenerateRun() {
		return this.getBoolean(GENERATE_RUN, DEFAULT_GENERATE_RUN);
	}

	/**
	 * @return the current custom bitwidth. 
	 */
	public int getBitwidth() {
		if (bitwidth_override == null)
			return this.getInt(BITWIDTH, DEFAULT_BITWIDTH);
		else
			return bitwidth_override;
	}

	/**
	 * Returns {@code string.bitwidth} (default 3). No caller.
	 *
	 * @return the value
	 */
	public int getStringBitwidth() {
		return this.getInt(STRING_BITWIDTH, DEFAULT_STRING_BITWIDTH);
	}

	/**
	 * Returns the class-path resources that {@code JDynAlloyParsingStage} parses: those of
	 * {@code jdynalloy.parser.inputResources} (none by default) and those registered with
	 * {@link #addDynAlloyParserInputResources(String)}. Reads the key from
	 * {@link #getInstance()}, not from this object (the same one in a TACO run).
	 *
	 * @return a new set of resource paths
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getJDynAlloyParserInputResources() {
		Set<String> returnValue = new HashSet<String>();
		returnValue.addAll(TacoConfigurator.getInstance().getList(
				TacoConfigurator.JDYNALLOY_PARSER_INPUT_RESOURCES,
				new ArrayList<String>()));
		returnValue.addAll(this.dynAlloyParserInputResourcesExtra);
		return returnValue;
	}

	/**
	 * Registers a class-path resource for {@code JDynAlloyParsingStage} to parse;
	 * {@code PrecompiledModules.execute()} registers the built-in {@code .djals} modules this
	 * way. Changes this configuration object only, so it lasts for one run.
	 *
	 * @param string the resource path, e.g.
	 *        {@code ar/edu/taco/engine/precompiledmodules/java_util_Set_int.djals}
	 */
	public void addDynAlloyParserInputResources(String string) {
		this.dynAlloyParserInputResourcesExtra.add(string);
	}

	/**
	 * Returns {@code arrayIsInt} (default {@code false}). No caller.
	 *
	 * @return the value
	 */
	@Override
	public boolean getArrayIsInt() {
		return this
				.getBoolean(ARRAY_IS_INT, DEFAULT_ARRAY_IS_INT);
	}

	/**
	 * Returns {@code modular_reasoning} (default {@code false}): whether jDynAlloy's {@code
	 * JDynAlloyTranslator} creates spec programs for modular reasoning (also {@code
	 * TacoMain}'s {@code -mr}). See {@link #getOutputModularJDynalloy()}.
	 *
	 * @return the value
	 */
	public boolean getModularReasoning() {
		return this.getBoolean(MODULAR_REASONING, MODULAR_REASONING_DEFAULT);
	}

	/**
	 * Returns {@code include_simulation_program_declaration} (default {@code false}): whether
	 * jDynAlloy declares the {@code simulate_} programs and {@code
	 * DynAlloyAppendCommandPlugin} appends a {@code run} command for each (also {@code
	 * TacoMain}'s {@code -r}).
	 *
	 * @return the value
	 */
	@Override
	public boolean getIncludeSimulationProgramDeclaration() {
		return this.getBoolean(INCLUDE_SIMULATION_PROGRAM_DECLARATION,
				INCLUDE_SIMULATION_PROGRAM_DECLARATION_DEFAULT);
	}

	/**
	 * Returns {@code include_branch_labels} (default {@code false}). No caller.
	 *
	 * @return the value
	 */
	@Override
	public boolean getIncludeBranchLabels() {
		return this.getBoolean(INCLUDE_BRANCH_LABELS,
				INCLUDE_BRANCH_LABELS_DEFAULT);
	}

	/**
	 * Returns {@code output_modular_JDynAlloy} (default {@code null}): the file to which
	 * jDynAlloy's {@code JDynAlloyTranslator} writes the modules with spec programs, with
	 * {@code modular_reasoning}.
	 *
	 * @return the file name, or {@code null}
	 */
	@Override
	public String getOutputModularJDynalloy() {
		return this.getString(OUTPUT_MODULAR_JDYNALLOY,
				OUTPUT_MODULAR_JDYNALLOY_DEFAULT);
	}

	/**
	 * Returns {@code relevancyAnalisys} (default {@code false}) (sic): whether {@code
	 * JDynAlloyStage} computes the relevant classes, replacing {@code relevantClasses}, and
	 * slices the modules to them.
	 *
	 * @return the value
	 */
	public boolean getRelevancyAnalysis() {
		return this.getBoolean(RELEVANCY_ANALYSIS, RELEVANCY_ANALYSIS_DEFAULT);
	}

	/**
	 * Returns {@code disableIntegerLiteralReduction} (default {@code false}). No caller.
	 *
	 * @return the value
	 */
	@Override
	public boolean getDisableIntegerLiteralReduction() {
		return this.getBoolean(DISABLE_INTEGER_LITERAL_REDUCTION,
				DISABLE_INTEGER_LITERAL_REDUCTION_DEFAULT);
	}

	/**
	 * Returns {@code noVerify} (default {@code false}): whether {@code TacoMain.run} skips the
	 * Alloy analysis (the snapshot and JUnit stages then do nothing; see {@code TacoMain.run}
	 * for what fails).
	 *
	 * @return the value
	 */
	public boolean getNoVerify() {
		return this.getBoolean(NO_VERIFY, NO_VERIFY_DEFAULT);
	}

	/**
	 * Returns {@code increaseBitwidthUsingLiterals} (default {@code false}). No caller.
	 *
	 * @return the value
	 */
	public boolean getIncreaseBitwidthUsingLiterals() {
		return this.getBoolean(INCREASE_BITWIDTH_USING_LITERALS,
				INCREASE_BITWIDTH_USING_LITERALS_DEFAULT);
	}

	/**
	 * 
	 */
	Integer bitwidth_override = null;

	/**<p>Overrides the current custom bitwidth.</p>
	 * @param bitwidth the new custom bitwidth.
	 */
	public void overrideBitwidth(int bitwidth) {
		bitwidth_override = bitwidth;
	}

	/**
	 * @return true if the analysis must be done with Java arithmetic, false otherwise.
	 */
	public boolean getUseJavaArithmetic() {
		return this
				.getBoolean(USE_JAVA_ARITHMETIC, USE_JAVA_ARITHMETIC_DEFAULT);
	}

	/**
	 * Returns {@code useJavaSBP} (default {@code false}): whether {@code
	 * DynalloyToAlloyManager} adds TACO's symmetry-breaking predicates ({@code
	 * SymmBreakPredPlugin}).
	 *
	 * @return the value
	 */
	public boolean getUseJavaSBP() {
		return this.getBoolean(USE_JAVA_SBP, USE_JAVA_SBP_DEFAULT);
	}

	/**
	 * Returns {@code dynalloy.toAlloy.removeQuantifiers} (default {@code true}), passed to
	 * jDynAlloy and to dynalloy4's translation options.
	 *
	 * @return the value
	 */
	public boolean getRemoveQuantifiers() {
		return this.getBoolean(DYNALLOY_TO_ALLOY_REMOVE_QUANTIFIERS, DEFAULT_DYNALLOY_TO_ALLOY_REMOVE_QUANTIFIERS);
	}

	
	/**
	 * Returns {@code useTightUpperBounds} (default {@code false}): whether, with {@code
	 * useJavaSBP}, {@code DynalloyToAlloyManager} adds the precomputed upper bounds of {@code
	 * xml/} ({@code UBoundPlugin}).
	 *
	 * @return the value
	 */
	public boolean getUseTightUpperBounds() {
		return this.getBoolean(USE_TIGHT_UPPER_BOUNDS,
				USE_TIGHT_UPPER_BOUNDS_DEFAULT);
	}

	/**
	 * Returns {@code generateUnitTestCase} (default {@code false}): whether {@code
	 * TacoMain.run} turns a counterexample into a JUnit test ({@code attemptToCorrectBug} does
	 * too).
	 *
	 * @return the value
	 */
	public boolean getGenerateUnitTestCase() {
		return this.getBoolean(GENERATE_UNIT_TEST_CASE,
				GENERATE_UNIT_TEST_CASE_DEFAULT);
	}

	/**
	 * Returns {@code output_dir} (default {@code output}, relative to the working directory):
	 * the directory of the intermediate files ({@code .java}, {@code .djals}, {@code
	 * output.dals}, {@code output.als}).
	 *
	 * @return the value
	 */
	public String getOutputDir() {
		return this.getString(OUTPUT_DIR, OUTPUT_DIR_DEFAULT);
	}

	/**
	 * Returns {@code dynalloy.toAlloy.loopUnroll} (default 3): how many times loops are
	 * unrolled (also in {@code WhileRemoverSimplifier}) and recursive calls unfolded.
	 *
	 * @return the value
	 */
	public int getDynAlloyToAlloyLoopUnroll() {
		return this.getInt(DYNALLOY_TO_ALLOY_LOOP_UNROLL,
				DEFAULT_DYNALLOY_TO_ALLOY_LOOP_UNROLL);
	}

	/**
	 * Returns {@code type_scopes}: the scope of each class, as {@code Class:N} entries (the
	 * property is split at commas), read by the constructor.
	 *
	 * @return the entries; empty if the key is not set
	 */
	public String[] getTypeScopes() {
		return this.getStringArray(TYPE_SCOPES);
	}

	/**
	 * Returns {@code inferScope} (default {@code false}): whether scopes (and, with {@code
	 * int.bitwidth} 0 or less, the bitwidth) are inferred ({@code DynalloyToAlloyManager}'s
	 * plugins, {@link #getAssertionArguments()}).
	 *
	 * @return the value
	 */
	public boolean getInferScope() {
		return this.getBoolean(INFER_SCOPE, DEFAULT_INFER_SCOPE);
	}

	/**
	 * Returns {@code removeExitWhileGuard} (default {@code false}), passed to dynalloy4's loop
	 * unrolling ({@code ClosureRemover}).
	 *
	 * @return the value
	 */
	public boolean getRemoveExitWhileGuard() {
		return this.getBoolean(REMOVE_EXIT_WHILE_GUARD,
				DEFAULT_REMOVE_EXIT_WHILE_GUARD);
	}

	/**
	 * Returns {@code nestedLoopUnroll} (default {@code true}): whether jDynAlloy translates a
	 * loop as a DynAlloy {@code while} ({@code true}) or a {@code repeat}.
	 *
	 * @return the value
	 */
	public boolean getNestedLoopUnroll() {
		return this.getBoolean(NESTED_LOOP_UNROLL, DEFAULT_NESTED_LOOP_UNROLL);
	}

	/**
	 * Returns {@code buildJavaTrace} (default {@code false}): whether {@code TacoMain.run}
	 * builds the trace of a counterexample ({@code JavaTraceStage}).
	 *
	 * @return the value
	 */
	public boolean getBuildJavaTrace() {
		return this.getBoolean(BUILD_JAVA_TRACE, DEFAULT_BUILD_JAVA_TRACE);
	}

	/**
	 * Returns {@code attemptToCorrectBug} (default {@code false}): whether {@code
	 * TacoMain.run} runs Stryker when the check is SAT (and also writes the counterexample
	 * test). Stryker's own TACO calls turn it off.
	 *
	 * @return the value
	 */
	public boolean getAttemptToCorrectBug() {
		return this.getBoolean(ATTEMPT_TO_CORRECT_BUG,
				ATTEMPT_TO_CORRECT_BUG_DEFAULT);
	}
	
	/**
	 * Returns {@code maxStrykerMethodForFile} (default 50), passed to Stryker, which stores it
	 * but never uses it: it has no effect (docs/KNOWN-LIMITATIONS.md).
	 *
	 * @return the value
	 */
	public int getMaxStrykerMethodsForFile() {
		return this.getInt(MAX_STRYKER_METHODS_FOR_FILE,
				DEFAULT_MAX_STRYKER_METHODS_FOR_FILE);
	}
	
	
	/**
	 * Returns {@code numericRangeLower} (default -4): with {@code useJavaArithmetic}, the
	 * lowest integer literal that {@code JmlExpressionVisitor} adds for a quantification over
	 * integers.
	 *
	 * @return the value
	 */
	public int getLowerBound() {
		return this.getInt(NUMERIC_RANGE_QUANTIFICATION_LOWER, DEFAULT_NUMERIC_RANGE_QUANTIFICATION_LOWER);
	}
	
	/**
	 * Returns {@code numericRangeUpper} (default 3): the highest such literal (see {@link
	 * #getLowerBound()}).
	 *
	 * @return the value
	 */
	public int getUpperBound() {
		return this.getInt(NUMERIC_RANGE_QUANTIFICATION_UPPER, DEFAULT_NUMERIC_RANGE_QUANTIFICATION_UPPER);
	}


}

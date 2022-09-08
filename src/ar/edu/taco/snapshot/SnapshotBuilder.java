package ar.edu.taco.snapshot;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.util.collections.IdentityHashSet;

import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.taco.TacoAnalysisResult;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.junit.RecoveredInformation;
import ar.edu.taco.junit.RecoveredInformation.StaticFieldInformation;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprFunction;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.util.ExpressionPrinter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
import edu.mit.csail.sdg.alloy4compiler.translator.A4TupleSet;

public class SnapshotBuilder {

	//	private static final ExprConstant UNIV = ExprConstant.buildExprConstant("univ");
	//	private static final String OBJECT_ARRAY_VAR = "Object_Array";
	private static final String LIST_CONTAINS_VAR = "List_contains";
	private static final String SET_CONTAINS_VAR = "java_util_Set_elems";
	private static final String MAP_CONTAINS_VAR = "java_util_Map_entries";

	private static Logger log = Logger.getLogger(SnapshotBuilder.class);

	private static final String OUTPUT_COUNTEREXAMPLE_INSTANCES_XML = "output/counterexample-instances.xml";
	private static final String NULL0 = "null$0";
	private static final String THIZ_VAR = "thiz";
	private static final String THIZ_SNAPSHOT_KEY = "thiz_0";

	private TacoAnalysisResult tacoAnalysisResult;
	private RecoveredInformation recoveredInformation;

	private Map<String, Object> instances = new HashMap<String, Object>();

	private ClassLoader loader = Thread.currentThread().getContextClassLoader();
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}

	public SnapshotBuilder(RecoveredInformation recoveredInformation, TacoAnalysisResult tacoAnalysisResult) {
		super();
		this.tacoAnalysisResult = tacoAnalysisResult;		
		this.recoveredInformation = recoveredInformation;
	}





	public boolean methodToCheckIsConstructor(Class<?> clazz){
		boolean result = false;
		String methodToCheck = TacoConfigurator.getInstance().getMethodToCheck();
		methodToCheck = methodToCheck.substring(0, methodToCheck.indexOf('('));
		int posOfLastUnderscore = methodToCheck.lastIndexOf('_');
		String lastWordOfMethodToCheck = methodToCheck.substring(posOfLastUnderscore+1,methodToCheck.length());
		String classToCheck = TacoConfigurator.getInstance().getClassToCheck();
		if (classToCheck.endsWith(lastWordOfMethodToCheck)){
			result = true;
		}
		return result;
	}

	public void createSnapshot() {
		if (!this.recoveredInformation.isValidInformation()) {
			return;
		}
		// pre loaded instances value
		instances.put("null$0", null);

		instances.put("Q$0", new Object());

		instances.put("true$0", true);
		instances.put("false$0", true);

		instances.put("char$0", 'a');
		instances.put("char$1", 'b');
		instances.put("char$2", 'c');
		instances.put("char$3", 'd');
		instances.put("char$4", 'e');
		instances.put("char$5", 'f');
		instances.put("char$6", 'g');
		instances.put("char$7", 'h');
		instances.put("char$8", 'i');
		instances.put("char$9", 'j');
		instances.put("char$10", 'k');
		instances.put("char$11", 'l');
		instances.put("char$12", 'm');
		instances.put("char$13", 'n');
		instances.put("char$14", 'o');
		instances.put("char$15", 'p');

		A4Solution a4Solution = tacoAnalysisResult.get_alloy_analysis_result().getAlloy_solution();

		// add atoms to "world". They seem to be missing.
		// Code borrowed from: edu.mit.csail.sdg.alloy4whole.SimpleGUI, Computer
		// anonymous class implementation
		// (it's part of Alloy GUI evaluator)
		for (ExprVar a : a4Solution.getAllAtoms()) {
			tacoAnalysisResult.get_alloy_analysis_result().getWorld().addGlobal(a.label, a);
		}

		try {
			if (log.getLevel() == Level.DEBUG || log.getLevel() == Level.TRACE) {
				a4Solution.writeXML(OUTPUT_COUNTEREXAMPLE_INSTANCES_XML);
			}

		} catch (Err e1) {
			e1.printStackTrace();
		}

		// Obtain method
		Class<?> clazzToCheck = obtainClassToCheckClass();



		if (methodToCheckIsConstructor(clazzToCheck)){
			Constructor<?> constructorToCheck = obtainConstructorToCheck(clazzToCheck);

			// build this
			AlloyExpression thizExpression = prefixExprVariable(THIZ_VAR);
			if (!isPruned(THIZ_VAR)) {
				Object thizInstance = evaluate(thizExpression, clazzToCheck);
				this.recoveredInformation.getSnapshot().put(THIZ_SNAPSHOT_KEY, thizInstance);
			}

			// build static fields
			for (StaticFieldInformation staticFieldInfo : this.recoveredInformation.getStaticFields()) {
				try {
					Class<?> clazz = Class.forName(staticFieldInfo.getClassName(), true, loader);
					Field field = getField(clazz, staticFieldInfo.getFieldName());
					Object fieldValue = setFieldValueSupport(null, null, field);
					if (fieldValue != null) {
						setStaticFieldvalue(staticFieldInfo.getClassName(), staticFieldInfo.getFieldName(), fieldValue);
					}

				} catch (SecurityException e) {
					throw new TacoException(e);
				} catch (ClassNotFoundException e) {
					throw new TacoException(e);
				}

			}

			// build parameters
			for (int i = 0; i < constructorToCheck.getParameterTypes().length; i++) {
				Class<?> parameterType = constructorToCheck.getParameterTypes()[i];
				String parameterName = this.recoveredInformation.getMethodParametersNames().get(i);
				AlloyExpression parameterExpr = prefixExprVariable(parameterName);
				if (!isPruned(parameterName)) {
					Object value = evaluate(parameterExpr, parameterType);
					this.recoveredInformation.getSnapshot().put(parameterName + "_0", value);
				}

			}


		} else {

			Method methodToCheck = obtainMethodToCheckMethod(clazzToCheck);

			// build this
			boolean isStaticMethod = isStatic(methodToCheck.getModifiers());
			if (!isStaticMethod) {
				AlloyExpression thizExpression = prefixExprVariable(THIZ_VAR);
				if (!isPruned(THIZ_VAR)) {
					Object thizInstance = evaluate(thizExpression, clazzToCheck);
					this.recoveredInformation.getSnapshot().put(THIZ_SNAPSHOT_KEY, thizInstance);
				} 
			}

			// build static fields
			for (StaticFieldInformation staticFieldInfo : this.recoveredInformation.getStaticFields()) {
				try {
					Class<?> clazz = Class.forName(staticFieldInfo.getClassName(), true, loader);
					Field field = getField(clazz, staticFieldInfo.getFieldName());
					Object fieldValue = setFieldValueSupport(null, null, field);
					if (fieldValue != null) {
						setStaticFieldvalue(staticFieldInfo.getClassName(), staticFieldInfo.getFieldName(), fieldValue);
					}

				} catch (SecurityException e) {
					throw new TacoException(e);
				} catch (ClassNotFoundException e) {
					throw new TacoException(e);
				}

			}

			// build parameters
			for (int i = 0; i < methodToCheck.getParameterTypes().length; i++) {
				Class<?> parameterType = methodToCheck.getParameterTypes()[i];
				String parameterName = this.recoveredInformation.getMethodParametersNames().get(i);
				AlloyExpression parameterExpr = prefixExprVariable(parameterName);
				if (!isPruned(parameterName)) {
					Object value = evaluate(parameterExpr, parameterType);
					this.recoveredInformation.getSnapshot().put(parameterName + "_0", value);
				}

			}

		}


	}

	private Field getField(@SuppressWarnings("rawtypes") Class clazz, String fieldName) {
		Set<Field> set = collectAllFieldSet(clazz);
		Field aField = null;
		for (Field field : set) {
			if (field.getName().equals(fieldName)) {
				aField = field;
			}
		}
		if (aField == null) {
			throw new TacoException("Not such field: " + clazz.getName() + "." + fieldName);
		}
		return aField;
	}

	private void setStaticFieldvalue(String className, String fieldName, Object fieldValue) {
		Map<String, Object> secondLevelMap = this.recoveredInformation.getStaticFieldsValues().get(className);
		if (secondLevelMap == null) {
			secondLevelMap = new HashMap<String, Object>();
			this.recoveredInformation.getStaticFieldsValues().put(className, secondLevelMap);
		}
		secondLevelMap.put(fieldName, fieldValue);
		// this.snapshot.put(className + "|" + fieldName, fieldValue);
	}

	private boolean isStatic(int modifiers) {
		return (modifiers & Modifier.STATIC) != 0;
	}

	private boolean isAbstract(int modifiers) {
		return (modifiers & Modifier.ABSTRACT) != 0;
	}

	private Class<?> obtainClassToCheckClass() {
		//		String className = recoveredInformation.getClassToCheck().substring(recoveredInformation.getClassToCheck().lastIndexOf(".") + 1) + "Test";
		//		String methodName = recoveredInformation.getMethodToCheck();

		Class<?> clazz;
		try {
			clazz = Class.forName(recoveredInformation.getClassToCheck(), true, loader);
			return clazz;
		} catch (ClassNotFoundException e) {
			throw new TacoException("Snapshot error: " + e.getMessage(), e);
		}
	}


	private Constructor<?> obtainConstructorToCheck(Class<?> clazz) {
		String classToCheck = TacoConfigurator.getInstance().getClassToCheck();
		String constructorToCheck = TacoConfigurator.getInstance().getMethodToCheck();

		constructorToCheck = constructorToCheck.substring(classToCheck.length() + 1);
		constructorToCheck = constructorToCheck.substring(0, constructorToCheck.indexOf('('));
		String methodToCheckJavaName = constructorToCheck;
		//		int methodToCheckIndex = Integer.valueOf(methodToCheck.substring(postion + 1, methodToCheck.length()));
		Constructor<?>[] cons = collectAllConstructors(clazz);

		int i = 0;
		boolean methodFound = false;
		Constructor<?> aConstructor = null;
		while (!methodFound && i < cons.length) {
			aConstructor = cons[i];
			String aConstructorName = aConstructor.getName();
			String aConstructorJavaName = aConstructorName.substring(aConstructorName.lastIndexOf('.')+1, aConstructorName.length());
			if (aConstructorJavaName.equals(methodToCheckJavaName)) {
				//WARNING: we are not checking the parameters are the same. This may lead to errars in case of multiple constructors.
				methodFound = true;
			}
			i++;
		}

		if (!methodFound) {
			throw new TacoException("Constructor method not found or constructor method is not public: " + clazz.getName() + "." + methodToCheckJavaName);
		}

		return aConstructor;

	}

	private Method obtainMethodToCheckMethod(Class<?> clazz) {

		String classToCheck = TacoConfigurator.getInstance().getClassToCheck();
		String methodToCheck = TacoConfigurator.getInstance().getMethodToCheck();

		methodToCheck = methodToCheck.substring(classToCheck.length() + 1);
		methodToCheck = methodToCheck.substring(0, methodToCheck.indexOf('('));
		String methodToCheckJavaName = methodToCheck;
		//		int methodToCheckIndex = Integer.valueOf(methodToCheck.substring(postion + 1, methodToCheck.length()));
		Method[] methods = collectAllMethods(clazz);

		int i = 0;
		boolean methodFound = false;
		Method aMethod = null;
		while (!methodFound && i < methods.length) {
			aMethod = methods[i];

			String aMethodName = aMethod.getName();
			String aMethodJavaName = aMethodName.substring(aMethodName.lastIndexOf('.')+1, aMethodName.length());
			if (aMethodJavaName.equals(methodToCheckJavaName)) {
				methodFound = true;
			}
			i++;
		}

		if (!methodFound) {
			throw new TacoException("Method not found: " + clazz.getName() + "." + methodToCheckJavaName);
		}

		return aMethod;

	}

	private Set<Field> collectAllFieldSet(Class<?> clazz) {
		Set<Field> set = new HashSet<Field>();

		if (clazz.getSuperclass() != null) {
			Set<Field> parentMethod = collectAllFieldSet(clazz.getSuperclass());
			set.addAll(parentMethod);
		}

		for (int i = 0; i < clazz.getDeclaredFields().length; i++) {
			Field field = clazz.getDeclaredFields()[i];
			set.add(field);
		}

		return set;
	}

	private Method[] collectAllMethods(Class<?> clazz) {
		Set<Method> set = collectAllMethodsSet(clazz);
		Method[] array = new Method[set.size()];
		int i = 0;
		for (Iterator<Method> iterator = set.iterator(); iterator.hasNext();) {
			Method method = (Method) iterator.next();
			array[i] = method;
			i++;
		}
		return array;
	}

	private Constructor<?>[] collectAllConstructors(Class<?> clazz) {
		Set<Constructor<?>> set = collectAllConstructorsSet(clazz);
		Constructor<?>[] array = new Constructor<?>[set.size()];
		int i = 0;
		for (Iterator<Constructor<?>> iterator = set.iterator(); iterator.hasNext();) {
			Constructor<?> con = (Constructor<?>) iterator.next();
			array[i] = con;
			i++;
		}
		return array;
	}


	private Set<Constructor<?>> collectAllConstructorsSet(Class<?> clazz) {
		Set<Constructor<?>> set = new HashSet<Constructor<?>>();

		if (clazz.getSuperclass() != null) {
			Set<Constructor<?>> parentMethod = collectAllConstructorsSet(clazz.getSuperclass());
			set.addAll(parentMethod);
		}

		for (int i = 0; i < clazz.getConstructors().length; i++) {
			Constructor<?> con = clazz.getConstructors()[i];
			set.add(con);
		}


		return set;
	}

	private Set<Method> collectAllMethodsSet(Class<?> clazz) {
		Set<Method> set = new HashSet<Method>();

		if (clazz.getSuperclass() != null) {
			Set<Method> parentMethod = collectAllMethodsSet(clazz.getSuperclass());
			set.addAll(parentMethod);
		}

		for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
			Method method = clazz.getDeclaredMethods()[i];
			set.add(method);
		}


		return set;
	}

	/**
	 * 
	 * @param variable
	 * @return
	 */
	private AlloyExpression prefixExprVariable(String variable) {
		AlloyExpression prefixExpression = ExprConstant.buildExprConstant("QF");
		AlloyExpression exprVariable = new ExprVariable(new AlloyVariable(variable + "_0"));

		if (TacoConfigurator.getInstance().getRemoveQuantifiers()){
			return new ExprJoin(prefixExpression, exprVariable);
		} else {
			return exprVariable;
		}
	}

	@SuppressWarnings("unchecked")
	private Object instantiate(String instanceName, AlloyExpression instanceExpr, Type javaType) {

		Object returnValue;
		// String valueAsString = evaluate(expression);

		if (javaType instanceof Class<?>) {
			// create or obtain instance
			Class<?> clazz = (Class<?>) javaType;
			Class<?> componentType = clazz.getComponentType();
			if (instanceName.equals(NULL0)) {
				returnValue = null;
			} else if (instances.containsKey(instanceName)) {
				returnValue = instances.get(instanceName);

			} else if (clazz.isArray()) {
				if (TacoConfigurator.getInstance().getUseJavaArithmetic()) {

					if (componentType.equals(int.class)) {
						returnValue = evaluateSparseIntArray(instanceName, instanceExpr, componentType);
					} else if (componentType.equals(long.class)) {
						returnValue = evaluateSparseLongArray(instanceName, instanceExpr, componentType);
					} else if (componentType.equals(char.class)) {
						returnValue = evaluateSparseCharArray(instanceName, instanceExpr, componentType);
					} else if (componentType.isInstance(Object.class)) {
						returnValue = evaluateSparseObjectArray(instanceName, instanceExpr, componentType);
					} else {
						throw new TacoException("Unsupported array content: " + componentType.toString());
					}
				} else {
					returnValue = evaluateArray(instanceName, instanceExpr, componentType);
				}
			} else if (List.class.isAssignableFrom(clazz)) {

				if (clazz.getName().equals("java.util.List")) {
					returnValue = createNewInstance(ArrayList.class);
				} else if (!isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {
					returnValue = createNewInstance(clazz);
				} else {
					throw new TacoNotImplementedYetException("Snapshot not implement this kind of List: " + clazz.getName());
				}
				instances.put(instanceName, returnValue);

				if (!isPruned(LIST_CONTAINS_VAR)) {
					throw new TacoNotImplementedYetException("List contents are not supported yet");
				}

			} else if (Set.class.isAssignableFrom(clazz)) {

				returnValue = createNewInstance(IdentityHashSet.class);
				@SuppressWarnings("rawtypes")
				Set m = (Set) returnValue;
				instances.put(instanceName, returnValue);

				if (!isPruned(SET_CONTAINS_VAR)) {

					AlloyExpression entrySetExpr = ExprJoin.join(instanceExpr, prefixExprVariable(SET_CONTAINS_VAR));
					m.addAll(evaluateAlloySet(entrySetExpr));

				}

			} else if (Map.class.isAssignableFrom(clazz)) {

				returnValue = createNewInstance(IdentityHashMap.class);

				@SuppressWarnings("rawtypes")
				Map m = (Map) returnValue;
				instances.put(instanceName, returnValue);

				if (!isPruned(MAP_CONTAINS_VAR)) {

					AlloyExpression entrySetExpr = ExprJoin.join(instanceExpr, prefixExprVariable(MAP_CONTAINS_VAR));

					m.putAll(evaluateAlloyMap(entrySetExpr));

				}

			} else {
				Object instance;
				instance = createNewInstance(clazz);

				returnValue = instance;
				instances.put(instanceName, returnValue);

				for (Field aField : obtainAllFields(clazz)) {

					if (!isStatic(aField.getModifiers())) {
						setFieldValueSupport(instanceExpr, instance, aField);
					}
				}

			}

		} else {
			throw new TacoNotImplementedYetException("Instantiate not support " + javaType + " yet");
		}
		return returnValue;
	}

	private Object evaluateArray(String instanceName, AlloyExpression instanceExpr, Class<?> componentType) {
		Object returnValue;
		AlloyExpression arrayLengthExpr;
		int arrayLength;

		String lengthFieldName;
		String contentsFieldName;
		if (componentType.getName().equals("int")){
			lengthFieldName = "java_lang_IntArray_length";
			contentsFieldName = "java_lang_IntArray_contents";
		} else if (componentType.getName().equals("char")) {
			lengthFieldName = "java_lang_CharArray_length";
			contentsFieldName = "java_lang_CharArray_contents";
		} else if (componentType.getName().equals("long")) {
			lengthFieldName = "java_lang_LongArray_length";
			contentsFieldName = "java_lang_LongArray_contents";
		} else { //Here there is potential source for bugs. This analysis must be finer grained.
			lengthFieldName = "java_lang_ObjectArray_length";
			contentsFieldName = "java_lang_ObjectArray_contents";
		}

		arrayLengthExpr = ExprFunction.buildExprFunction("arrayLength", instanceExpr, prefixExprVariable(lengthFieldName));//mfrias
		if (isPruned(lengthFieldName)) {
			arrayLength = 0;
		} else {
			arrayLength = (Integer) evaluate(arrayLengthExpr, int.class);
		}


		if (0 > arrayLength || arrayLength > 10000) {// XXX: Hack to avoid java.lang.OutOfMemoryError
			System.out.println(" JUnit generation: WARNING. Array exceeding size 10,000 required for array variable storing " + componentType.getName()+".");
			System.out.println(" Will generate instead array of size 100 with the first 100 positions");
			System.out.println(" of the original array.");
			System.out.println(" The actual array length is " + arrayLength);
			arrayLength = 100;

		}

		returnValue = Array.newInstance(componentType, arrayLength);
		instances.put(instanceName, returnValue);
		if (this.tacoAnalysisResult.get_alloy_analysis_result().getAlloy_solution().toString().contains(contentsFieldName)) {
			for (int x = 0; x < arrayLength; x++) {
				AlloyExpression arrayAccess;
				arrayAccess = ExprFunction.buildExprFunction("arrayAccess", instanceExpr, prefixExprVariable(contentsFieldName), new ExprIntLiteral(x));//mfrias
				Class<?> inferredType = inferTypeOfExpression(arrayAccess);
				Object value = evaluate(arrayAccess, inferredType);

				if (inferredType != null && componentType.isAssignableFrom(inferredType)) {
					updateArrayValue(returnValue, inferredType, x, value);
				}

			}
		} else {
			for (int x = 0; x < arrayLength; x++) {
				if (componentType.isPrimitive()){
					String typeSimpleName = componentType.getSimpleName();
					if (typeSimpleName.equals("boolean")) {
						updateArrayValue(returnValue, componentType, x, false);
					} else if (typeSimpleName.endsWith("byte")) {
						byte val = 0;
						updateArrayValue(returnValue, componentType, x, val);
					} else if (typeSimpleName.endsWith("char")) {
						updateArrayValue(returnValue, componentType, x, '\u0000');
					} else if (typeSimpleName.endsWith("double")) {
						updateArrayValue(returnValue, componentType, x, 0.0d);
					} else if (typeSimpleName.endsWith("float")) {
						updateArrayValue(returnValue, componentType, x, 0.0f);
					} else if (typeSimpleName.endsWith("int")) {
						updateArrayValue(returnValue, componentType, x, 0);
					} else if (typeSimpleName.endsWith("long")) {
						updateArrayValue(returnValue, componentType, x, 0L);
					} else if (typeSimpleName.endsWith("short")) {
						updateArrayValue(returnValue, componentType, x, 0);
					} else {
						throw new TacoNotImplementedYetException();
					}
				} else {
					updateArrayValue(returnValue, componentType, x, null);
				}
			}
		}


		return returnValue;
	}

	private Class<?> inferTypeOfExpression(AlloyExpression arrayExpression) {
		Class<?> clazz;
		Object evlResult = null;
		try {
			evlResult = alloyEvaluateSupport(arrayExpression);
		} catch (Err e) {
			throw new TacoException(e);
		}

		if (evlResult != null) {
			String value = null;
			if (evlResult instanceof A4TupleSet) {
				A4TupleSet result = (A4TupleSet) evlResult;
				switch (result.arity()) {
				case 1:
					value = result.iterator().next().atom(0);
					break;

				case 2:
					value = result.iterator().next().atom(1);
					break;

				default:
					break;
				}
			} else {
				throw new IllegalArgumentException();
			}

			clazz = inferTypeOfExpression(value);
		} else {
			clazz = null;
		}
		return clazz;
	}

	private Class<?> inferTypeOfExpression(String value) {
		Class<?> clazz;

		int i = value.indexOf("$");

		if (value.equals(NULL0)) {
			return Object.class;
		} else if (value.equals("true$0") || value.equals("false$0")) {
			return Boolean.class;
		} else if (i >= 0) {

			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				if (value.startsWith("JavaPrimitiveIntegerLiteral") || value.startsWith("JavaPrimitiveIntegerValue")) {
					return int.class;
				} else if (value.startsWith("JavaPrimitiveCharLiteral") || value.startsWith("JavaPrimitiveCharValue")) {
					return char.class;
				} else if (value.startsWith("JavaPrimitiveLongLiteral") || value.startsWith("JavaPrimitiveLongValue")) {
					return long.class;
				}
			}

			String className = value.substring(0, i);

			// remove "_inner_" used for inner classes
			className = className.replaceAll("_inner_", "\\$");

			// remove "_NNN" from "xxxx_NNN".
			int lastUnderscore = className.lastIndexOf("_");
			if (lastUnderscore >= 0) {
				String stringBeforeLastUnderscore = className.substring(lastUnderscore + 1);
				if (stringBeforeLastUnderscore.matches("[0-9]+")) {
					className = className.substring(0, lastUnderscore);
				}
			}

			if (className.equals("char")) {
				return Character.class;
			} else if (className.equals("QF")) {
				return Object.class;
			} else if (className.equals("ClassFields")) {
				return Object.class;
			} else if (className.equals("java_lang_System")) {
				return Object.class;
			} else if (className.equals("org_jmlspecs_models_JMLObjectSet")) {
				return Object.class;
			} else if (className.equals("org_jmlspecs_models_JMLObjectSequence")) {
				return Object.class;
			} else if (className.equals("java_lang_ObjectArray")) {
				return Object[].class;
			} else if (className.equals("java_lang_IntArray")) {
				return int[].class;
			} else if (className.equals("java_lang_LongArray")) {
				return long[].class;
			} else if (className.equals("java_lang_CharArray")) {
				return char[].class;
			} else if (className.equals("java_util_Iterator")) {
				return ArrayListIterator.class;
			}

			// exception literal
			if ((className.startsWith("java_lang_") || className.startsWith("java_util_")) && className.endsWith("Lit")) {
				className = className.substring(0, className.length() - 3);
			}
			if (className.equals("AssertionFailureLit")) {
				className = "java.lang.Exception";
			}

			className = className.replaceAll("_", ".");
			className = className.replaceAll("_inner_", "$");
			try {
				clazz = Class.forName(className, true, loader);
			} catch (ClassNotFoundException e) {

				throw new TacoException(e);
			}

		} else if (isNumeric(value)) {
			clazz = int.class;
		} else {
			throw new TacoNotImplementedYetException();
		}
		return clazz;
	}

	private static boolean isNumeric(String s) {

		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}

	}

	private void updateArrayValue(Object array, Class<?> componentType, int x, Object value) {
		if (value == null) {

			Array.set(array, x, null);
		}
		if (componentType.isPrimitive()) {
			String typeSimpleName = componentType.getSimpleName();
			if (typeSimpleName.equals("boolean")) {
				Array.setBoolean(array, x, (Boolean) value);

			} else if (typeSimpleName.endsWith("byte")) {
				Array.setByte(array, x, (Byte) value);

			} else if (typeSimpleName.endsWith("char")) {
				Array.setChar(array, x, (Character) value);

			} else if (typeSimpleName.endsWith("double")) {
				Array.setDouble(array, x, (Double) value);

			} else if (typeSimpleName.endsWith("float")) {
				Array.setFloat(array, x, (Float) value);

			} else if (typeSimpleName.endsWith("int")) {
				Array.setInt(array, x, (Integer) value);

			} else if (typeSimpleName.endsWith("long")) {
				Array.setLong(array, x, (Long) value);

			} else if (typeSimpleName.endsWith("short")) {
				Array.setShort(array, x, (Short) value);

			} else {
				throw new TacoNotImplementedYetException();
			}
		} else {
			Array.set(array, x, value);
		}
	}

	private Object setFieldValueSupport(AlloyExpression expression, Object instance, Field aField) {
		String fieldSimplifiedName = simplifyFieldName(aField);
		// skip pruned fields



		if (!isSBPPruned(fieldSimplifiedName)) {
			AlloyExpression fieldExpression;
			if (isStatic(aField.getModifiers())) {
				fieldExpression = prefixStaticField(fieldSimplifiedName);
			} else {
				if (isSBPField(fieldSimplifiedName)) {
					fieldExpression = prefixSBPField(expression, fieldSimplifiedName);
				} else {
					fieldExpression = prefixField(expression, fieldSimplifiedName);
				}
			}

			log.debug("expression: " + expression);
			log.debug("field: " + aField.getName());			
			log.debug("type: " + aField.getType().getName());			

			Object fieldValue = evaluate(fieldExpression, aField.getType());

			log.debug("instance: " + instance);			
			Object returnValue = updateValue(instance, aField, fieldValue);
			return returnValue;
		}
		return null;
	}

	private boolean isSBPField(String fieldSimplifiedName) {
		return (isPruned(fieldSimplifiedName)) && (!isPruned("b" + fieldSimplifiedName) || !isPruned("f" + fieldSimplifiedName)); 
	}

	/**
	 * Obtains all public and private fields
	 * 
	 * @param clazz
	 * @return
	 */
	private List<Field> obtainAllFields(Class<?> clazz) {

		List<Field> myFields = new ArrayList<Field>();
		myFields.addAll(Arrays.asList(clazz.getDeclaredFields()));

		if (clazz.getSuperclass() != null) {
			myFields.addAll(obtainAllFields(clazz.getSuperclass()));
		}

		return myFields;
	}

	private AlloyExpression prefixSBPField(AlloyExpression prefixExpression, String fieldSimplifiedName) {
		AlloyExpression backwardFieldExpression = prefixExprVariable("b" + fieldSimplifiedName);
		AlloyExpression forwardFieldExpression = prefixExprVariable("f" + fieldSimplifiedName);
		AlloyExpression complete_field = ExprUnion.buildExprUnion(backwardFieldExpression, forwardFieldExpression);
		return new ExprJoin(prefixExpression, complete_field);
	}

	private AlloyExpression prefixField(AlloyExpression prefixExpression, String fieldSimplifiedName) {
		AlloyExpression fieldExpression = prefixExprVariable(fieldSimplifiedName);
		return new ExprJoin(prefixExpression, fieldExpression);
	}

	private AlloyExpression prefixStaticField(String field) {
		AlloyExpression fieldExpression = prefixExprVariable(field);

		return new ExprJoin(JExpressionFactory.CLASS_FIELDS, fieldExpression);

	}

	private String simplifyFieldName(Field aField) {
		JavaClassNameNormalizer javaClassNameNormalizer = new JavaClassNameNormalizer(aField.getDeclaringClass().getName());
		String module = javaClassNameNormalizer.getQualifiedClassName();

		return module + "_" + aField.getName();
	}


	private static HashMap<Class, Object> createdDefaultObjects = new HashMap<Class, Object>();

	private Object createNewInstance(@SuppressWarnings("rawtypes") Class clazz) {
		Object instance = null;
		try {
			Class<?> loadedClass = Class.forName(clazz.getName(), true, loader);
			Objenesis objenesis = new ObjenesisStd();
			ObjectInstantiator<?> classInstantiator = objenesis.getInstantiatorOf(loadedClass);
			instance = (Object)classInstantiator.newInstance();
			log.debug("created: " + instance.getClass() );
			log.debug("clazz: " + clazz );
		} catch (ClassNotFoundException e) {
			throw new TacoException("Error creating instance: " + clazz.getName(), e);
		}

//		Object instance = null;
//		Class<?>[] parTypes = null;
//		Object[] concretePars = null;
//		try {
//			instance = clazz.newInstance();
//		} catch (InstantiationException e) {
//
			//			In this case the class under analysis did not export a parameterless constructor. Therefore,
			//			there should be a constructor with parameters. We will get the first such constructor (since no
			//			information is available to choose a different one), and generate default values for its 
			//			arguments. We will use this constructor to build an instance. This may fail in case this constructor
			//			throws an exception when executed on default values.

//			Constructor<?>[] cons = clazz.getDeclaredConstructors();
//			if (cons.length == 0){
//				cons = clazz.getDeclaredConstructors();
//			}
//			Constructor<?> c = cons[0];
//			parTypes = c.getParameterTypes();
//			concretePars = new Object[parTypes.length]; 
//			int index = 0;
//			for (Class<?> cl : parTypes){
//				if (cl.isPrimitive()){
//					if (cl.getName().equals("byte"))
//						concretePars[index] = 0;
//					if (cl.getName().equals("short"))
//						concretePars[index] = 0;
//					if (cl.getName().equals("int"))
//						concretePars[index] = 0;
//					if (cl.getName().equals("long"))
//						concretePars[index] = 0L;
//					if (cl.getName().equals("float"))
//						concretePars[index] = 0.0f;
//					if (cl.getName().equals("double"))
//						concretePars[index] = 0.0d;
//					if (cl.getName().equals("char"))
//						concretePars[index] = '\u0000';
//					if (cl.getName().equals("boolean"))
//						concretePars[index] = false;
//				} else {
//					if (createdDefaultObjects.containsKey(cl)){
//						concretePars[index] = createdDefaultObjects.get(cl);
//					} else {
//						Object o = null;
//						if (!cl.isArray()){
//							o = createNewInstance(cl);
//							concretePars[index] = o;
//							createdDefaultObjects.put(cl, o);
//						} else {
//							if (!cl.getComponentType().isPrimitive()){
//								o = new Object[0];
//								concretePars[index] = o;
//								createdDefaultObjects.put(cl, o);
//							} else {
//
//								if (cl.getComponentType().getName().equals("byte"))
//									o = new byte[0];								
//								if (cl.getComponentType().getName().equals("short"))
//									o = new short[0];
//								if (cl.getComponentType().getName().equals("int"))
//									o = new int[0];
//								if (cl.getComponentType().getName().equals("long"))
//									o = new long[0];
//								if (cl.getComponentType().getName().equals("char"))
//									o = new char[0];
//								if (cl.getComponentType().getName().equals("float"))
//									o = new float[0];
//								if (cl.getComponentType().getName().equals("double"))
//									o = new double[0];
//								concretePars[index] = o;
//								createdDefaultObjects.put(cl, o);
//							}
//						}
//					}
//				}
//				index++;
//			}
//			try {
//				if (c.isAccessible())
//					instance = c.newInstance(concretePars);
//				else {
//					c.setAccessible(true);
//					instance = c.newInstance(concretePars);
//				}
//			} catch (InstantiationException ex){
//				e.printStackTrace();
//				instance = null;
//			} catch (IllegalAccessException ex){
//				e.printStackTrace();
//				instance = null;
//			} catch (IllegalArgumentException ex){
//				e.printStackTrace();
//				instance = null;
//			} catch (InvocationTargetException ex){
//				e.printStackTrace();
//			} catch (Exception ex) {
//				instance = null;
//				throw new RuntimeException("DYNJALLOY ERROR! Possibly the class does not provide a constructor than can run on its parameters default values.");
//			}
//
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
//		}

		return instance;
	}

	private boolean isSBPPruned(String fieldName) {

		return isPruned(fieldName) && isPruned("b" + fieldName) && isPruned("f" + fieldName); 

	}
	private boolean isPruned(String fieldName) {
		@SuppressWarnings("unused")
		Object evlResult = null;
		try {
			AlloyExpression fieldExpr = prefixExprVariable(fieldName);
			evlResult = alloyEvaluateSupport(fieldExpr);
		} catch (Err e) {
			return true;
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map evaluateAlloyMap(AlloyExpression expression) {
		Object evlResult = null;
		try {
			evlResult = alloyEvaluateSupport(expression);
		} catch (Err e) {
			throw new TacoException(e);
		}
		if (evlResult != null) {
			Map map = new HashMap();
			if (evlResult instanceof A4TupleSet) {
				A4TupleSet result = (A4TupleSet) evlResult;
				for (A4Tuple a4Tuple : result) {
					String key = a4Tuple.atom(0);
					String value = a4Tuple.atom(1);

					Class<?> infieredTypeKey = inferTypeOfExpression(key);
					Object keyObject = evaluate(ExprVariable.buildExprVariable(key), infieredTypeKey);

					Class<?> infieredTypeValue = inferTypeOfExpression(value);
					Object valueObject = evaluate(ExprVariable.buildExprVariable(value), infieredTypeValue);

					map.put(keyObject, valueObject);
				}
				return map;
			} else {
				throw new IllegalArgumentException();
			}

		} else {
			throw new TacoNotImplementedYetException();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Set evaluateAlloySet(AlloyExpression expression) {
		Object evlResult = null;
		try {
			evlResult = alloyEvaluateSupport(expression);
		} catch (Err e) {
			throw new TacoException(e);
		}
		if (evlResult != null) {
			Set set = new HashSet();
			if (evlResult instanceof A4TupleSet) {
				A4TupleSet result = (A4TupleSet) evlResult;
				for (A4Tuple a4Tuple : result) {
					String key = a4Tuple.atom(0);

					Class<?> infieredTypeKey = inferTypeOfExpression(key);
					Object keyObject = evaluate(ExprVariable.buildExprVariable(key), infieredTypeKey);

					set.add(keyObject);
				}
				return set;
			} else {
				throw new IllegalArgumentException();
			}

		} else {
			throw new TacoNotImplementedYetException();
		}
	}

	/**
	 * Perform the evaluation of an AlloyExpression
	 * 
	 * @param expression
	 * @return
	 */
	private Object evaluate(AlloyExpression expression, Class<?> clazz) {
		Object evlResult = null;
		try {
			evlResult = alloyEvaluateSupport(expression);
		} catch (Err e) {
			throw new TacoException(e);
		}

		if (evlResult != null) {
			String value = null;
			if (evlResult instanceof Integer) {
				return value;
			}
			if (evlResult instanceof Boolean) {
				return value;
			} else if (evlResult instanceof A4TupleSet) {
				A4TupleSet result = (A4TupleSet) evlResult;
				switch (result.arity()) {
				case 1:
					value = result.iterator().next().atom(0);
					break;

				case 2:
					value = result.iterator().next().atom(1);
					break;

				default:
					break;
				}
			} else {
				throw new IllegalArgumentException();
			}

			Object returnValue;
			// convert String to Type instance
			if (clazz.isPrimitive() || isAutoboxingClass(clazz)) {
				String typeSimpleName = clazz.getSimpleName();
				if (typeSimpleName.equals("boolean") || typeSimpleName.equals("Boolean")) {

					Boolean b;
					if (value.equals("false$0")) {
						b = false;
					} else if (value.equals("true$0")) {
						b = true;
					} else if (value.startsWith("java_lang_Boolean$")) {
						// DPD: I assume that specific boolean value isn't
						// important
						b = true;
					} else {
						throw new TacoException("Invalid value: " + value);
					}
					returnValue = b;
				} else if (typeSimpleName.endsWith("byte") || typeSimpleName.endsWith("Byte")) {
					Byte b = Byte.valueOf((String) value);
					returnValue = b;
				} else if (typeSimpleName.endsWith("char") || typeSimpleName.endsWith("Character")) {
					Character c;
					if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
						c = create_character(expression);
					} else {
						c = Character.valueOf(((String) value).charAt(0));
					}
					returnValue = c;
				} else if (typeSimpleName.endsWith("double") || typeSimpleName.endsWith("Double")) {
					Double d = Double.valueOf((String) value);
					returnValue = d;
				} else if (typeSimpleName.endsWith("float") || typeSimpleName.endsWith("Float")) {
					Float f;
					if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
						f = create_float(expression);
					} else {
						f = Float.valueOf((String) value);
					}
					returnValue = f;
				} else if (typeSimpleName.endsWith("int") || typeSimpleName.endsWith("Integer")) {
					Integer i;
					if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
						i = create_integer(expression);
					} else {
						i = Integer.valueOf((String) value);
					}
					returnValue = i;
				} else if (typeSimpleName.endsWith("long") || typeSimpleName.endsWith("Long")) {
					Long l;
					if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
						l = create_long(expression);
					} else {
						l = Long.valueOf((String) value);
					}
					returnValue = l;
				} else if (typeSimpleName.endsWith("short") || typeSimpleName.endsWith("Short")) {
					Short s = Short.valueOf((String) value);
					returnValue = s;
				} else {
					throw new TacoNotImplementedYetException();
				}
			} else {
				Class<?> instanceClass = inferTypeOfExpression(value);
				returnValue = instantiate(value, expression, instanceClass);
			}

			return returnValue;
		} else {
			throw new TacoNotImplementedYetException();
		}
	}

	private Float create_float(AlloyExpression expression) {
		Integer integer = create_integer(expression);
		return Float.intBitsToFloat(integer.intValue());
	}

	private Long create_long(AlloyExpression expression) {
		int size = Long.SIZE;
		boolean[] bit_vector = create_bit_vector(expression, size);
		StringBuffer sb = new StringBuffer(Arrays.toString(bit_vector).replace("true", "1").replace("false", "0").replaceAll("[^01]", ""));
		sb.reverse();
		return new BigInteger(sb.toString(), 2).longValue();
	}

	private Integer create_integer(AlloyExpression expression) {
		int size = Integer.SIZE;
		boolean[] bit_vector = create_bit_vector(expression, size);
		StringBuffer sb = new StringBuffer(Arrays.toString(bit_vector).replace("true", "1").replace("false", "0").replaceAll("[^01]", ""));
		sb.reverse();
		return new BigInteger(sb.toString(), 2).intValue();
	}

	private Character create_character(AlloyExpression expression) {
		int size = Character.SIZE;
		boolean[] bit_vector = create_bit_vector(expression, size);
		StringBuffer sb = new StringBuffer(Arrays.toString(bit_vector).replace("true", "1").replace("false", "0").replaceAll("[^01]", ""));
		sb.reverse();
		return (char)Integer.parseInt(sb.toString(),2);

	}

	private boolean[] create_bit_vector(AlloyExpression expression, int size) {
		boolean[] bit_vector = new boolean[size];
		for (int i = 0; i < size; i++) {
			boolean bit_value = bitVector_value(expression, i);
			bit_vector[i] = bit_value;
		}
		return bit_vector;
	}

	private boolean bitVector_value(AlloyExpression expression, int i) {
		String bit_field = "b" + (i < 10 ? "0" : "") + i;
		try {
			Object bit_field_result = alloyEvaluateSupport(new ExprJoin(expression, new ExprConstant(null, bit_field)));
			if (bit_field_result instanceof A4TupleSet) {
				A4TupleSet tuple_set = (A4TupleSet) bit_field_result;
				String bit_field_value = tuple_set.iterator().next().atom(0);
				if (bit_field_value.equals("true$0")) {
					return true;
				} else if (bit_field_value.equals("false$0")) {
					return false;
				}
			}
			throw new IllegalArgumentException("Unexpected field result: " + bit_field_result);

		} catch (Err e) {
			throw new TacoException(e);
		}

	}

	private boolean isAutoboxingClass(Class<?> clazz) {
		boolean ret_Value = false;

		ret_Value |= Boolean.class.isAssignableFrom(clazz);
		ret_Value |= Byte.class.isAssignableFrom(clazz);
		ret_Value |= Character.class.isAssignableFrom(clazz);
		ret_Value |= Double.class.isAssignableFrom(clazz);
		ret_Value |= Float.class.isAssignableFrom(clazz);
		ret_Value |= Integer.class.isAssignableFrom(clazz);
		ret_Value |= Long.class.isAssignableFrom(clazz);
		ret_Value |= Short.class.isAssignableFrom(clazz);

		return ret_Value;
	}

	private Object alloyEvaluateSupport(AlloyExpression expression) throws Err {
		Object evlResult;

		ExpressionPrinter expressionPrinter = new ExpressionPrinter();

		String expressionStr = (String) expression.accept(expressionPrinter);

		A4Solution a4Solution = tacoAnalysisResult.get_alloy_analysis_result().getAlloy_solution();

		Expr expr;

		expr = CompUtil.parseOneExpression_fromString(tacoAnalysisResult.get_alloy_analysis_result().getWorld(), expressionStr);
		evlResult = a4Solution.eval(expr);
		return evlResult;
	}

	public Object updateValue(Object instance, Field aField, Object value) {
		try {
			boolean accessibleOld = aField.isAccessible();
			aField.setAccessible(true);

			//			Class<?> type = aField.getType();

			if (aField.getType().isPrimitive()) {
				String typeSimpleName = aField.getType().getSimpleName();
				if (typeSimpleName.equals("boolean")) {
					aField.setBoolean(instance, (Boolean) value);
				} else if (typeSimpleName.endsWith("byte")) {
					aField.setByte(instance, (Byte) value);
				} else if (typeSimpleName.endsWith("char")) {
					aField.setChar(instance, (Character) value);
				} else if (typeSimpleName.endsWith("double")) {
					aField.setDouble(instance, (Double) value);
				} else if (typeSimpleName.endsWith("float")) {
					aField.setFloat(instance, (Float) value);
				} else if (typeSimpleName.endsWith("int")) {
					aField.setInt(instance, (Integer) value);
				} else if (typeSimpleName.endsWith("long")) {
					aField.setLong(instance, (Long) value);
				} else if (typeSimpleName.endsWith("short")) {
					aField.setShort(instance, (Short) value);
				} else {
					throw new TacoNotImplementedYetException();
				}
			} else if (
					(value != null && value.getClass().isArray() && aField.getType().isArray()) &&
					((value.getClass().getComponentType() != null && value.getClass().getComponentType().isPrimitive() && !aField.getType().getComponentType().isPrimitive()) ||
							(value.getClass().getComponentType() != null && !value.getClass().getComponentType().isPrimitive() && aField.getType().getComponentType().isPrimitive()))
					){
				int lenght = Array.getLength(value);
				log.debug("aField: " + aField);
				log.debug("aField.getClass(): " + aField.getClass());
				log.debug("getType().getComponentType(): " + aField.getType().getComponentType());

				Object aArray = Array.newInstance(aField.getType().getComponentType(), lenght);
				for(int i = 0; i<lenght; i++) {
					Object elem = Array.get(value, i);
					if (elem != null) {
						String typeSimpleName = elem.getClass().getName();
						if (typeSimpleName.equals("Boolean")) {
							Array.setBoolean(aArray, i, (Boolean) elem);
						} else if (typeSimpleName.endsWith("Byte")) {
							Array.setByte(aArray, i, (Byte) elem);
						} else if (typeSimpleName.endsWith("Character")) {
							Array.setChar(aArray, i, (Character) elem);
						} else if (typeSimpleName.endsWith("Double")) {
							Array.setDouble(aArray, i, (Double) elem);
						} else if (typeSimpleName.endsWith("Float")) {
							Array.setFloat(aArray, i, (Float) elem);
						} else if (typeSimpleName.endsWith("Integer")) {
							Array.setInt(aArray, i, (Integer) elem);
						} else if (typeSimpleName.endsWith("Long")) {
							Array.setLong(aArray, i, (Long) elem);
						} else if (typeSimpleName.endsWith("Short")) {
							Array.setShort(aArray, i, (Short) elem);
						} else {
							throw new TacoNotImplementedYetException();
						}
					}

				}
				aField.set(instance, aArray);

			} else {
				log.debug("-*--*--");
				log.debug("updateValue. Field: " + aField.getName());
				log.debug("updateValue. Field Type: " + aField.getType());

				log.debug("updateValue. Instance Type: " + instance);
				if (instance != null) {
					log.debug("updateValue. Instance Type: " + instance.getClass());
				}

				log.debug("updateValue. Value Type: " + value);				
				if (value !=null) {
					log.debug("updateValue. Value Type: " + value.getClass());
				}
				aField.set(instance, value);
			}

			aField.setAccessible(accessibleOld);

			return value;
		} catch (IllegalArgumentException e) {
			throw new TacoException(e);
		} catch (IllegalAccessException e) {
			throw new TacoException(e);
		}

	}

	private Object evaluateSparseIntArray(String instanceName, AlloyExpression instanceExpr, Class<?> componentType) {
		Object returnValue;
		AlloyExpression arrayLengthExpr;
		int arrayLength;

		if (isPruned("java_lang_IntArray_length")) {
			arrayLength = 0;
		} else {
			arrayLengthExpr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_IntArray_length"));
			arrayLength = (Integer) evaluate(arrayLengthExpr, int.class);
		}

		if (0 > arrayLength || arrayLength > 10000) {// XXX: Hack to avoid java.lang.OutOfMemoryError
			System.out.println(" JUnit generation: WARNING. Array exceeding size 10,000 required for array variable storing type "+componentType.getName()+".");
			System.out.println(" Will generate instead array of size 100 with the first 100 positions");
			System.out.println(" of the original array.");
			System.out.println(" The actual array length is " + arrayLength);
			arrayLength = 100;
		}
		returnValue = Array.newInstance(componentType, arrayLength);
		instances.put(instanceName, returnValue);

		if (!isPruned("java_lang_IntArray_contents")) {
			AlloyExpression contents_expr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_IntArray_contents"));

			Object evalResult;
			try {
				evalResult = alloyEvaluateSupport(contents_expr);
			} catch (Err e) {
				throw new TacoException(e);
			}

			if (evalResult instanceof A4TupleSet) {
				A4TupleSet array_cotents = (A4TupleSet) evalResult;
				for (A4Tuple a4Tuple : array_cotents) {
					String index = a4Tuple.atom(0);
					String value = a4Tuple.atom(1);

					ExprConstant index_constant = ExprConstant.buildExprConstant(index);
					ExprConstant value_constant = ExprConstant.buildExprConstant(value);

					int integer_index = create_integer(index_constant);

					if (integer_index >= 0 && integer_index < arrayLength) {

						Class<?> inferredType = inferTypeOfExpression(value_constant);
						if (inferredType.equals(int.class)) {
							int integer_value = create_integer(value_constant);
							updateArrayValue(returnValue, inferredType, integer_index, integer_value);
						}

					}
				}

			}
		}

		return returnValue;
	}


	private Object evaluateSparseCharArray(String instanceName, AlloyExpression instanceExpr, Class<?> componentType) {
		Object returnValue;
		AlloyExpression arrayLengthExpr;
		int arrayLength;

		if (isPruned("java_lang_CharArray_length")) {
			arrayLength = 0;
		} else {
			arrayLengthExpr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_CharArray_length"));
			arrayLength = (Integer) evaluate(arrayLengthExpr, int.class);
		}

		if (0 > arrayLength || arrayLength > 10000) {// XXX: Hack to avoid java.lang.OutOfMemoryError
			System.out.println(" JUnit generation: WARNING. Array exceeding size 10,000 required for array variable storing type "+componentType.getName()+".");
			System.out.println(" Will generate instead array of size 100 with the first 100 positions");
			System.out.println(" of the original array.");
			System.out.println(" The actual array length is " + arrayLength);
			arrayLength = 100;
		}
		returnValue = Array.newInstance(componentType, arrayLength);
		instances.put(instanceName, returnValue);

		if (!isPruned("java_lang_CharArray_contents")) {
			AlloyExpression contents_expr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_CharArray_contents"));

			Object evalResult;
			try {
				evalResult = alloyEvaluateSupport(contents_expr);
			} catch (Err e) {
				throw new TacoException(e);
			}

			if (evalResult instanceof A4TupleSet) {
				A4TupleSet array_cotents = (A4TupleSet) evalResult;
				for (A4Tuple a4Tuple : array_cotents) {
					String index = a4Tuple.atom(0);
					String value = a4Tuple.atom(1);

					ExprConstant index_constant = ExprConstant.buildExprConstant(index);
					ExprConstant value_constant = ExprConstant.buildExprConstant(value);

					int integer_index = create_integer(index_constant);

					if (integer_index >= 0 && integer_index < arrayLength) {

						Class<?> inferredType = inferTypeOfExpression(value_constant);
						if (inferredType.equals(char.class)) {
							char char_value = create_character(value_constant);
							updateArrayValue(returnValue, inferredType, integer_index, char_value);
						}

					}
				}

			}
		}

		return returnValue;
	}



	private Object evaluateSparseLongArray(String instanceName, AlloyExpression instanceExpr, Class<?> componentType) {
		Object returnValue;
		AlloyExpression arrayLengthExpr;
		int arrayLength;

		if (isPruned("java_lang_LongArray_length")) {
			arrayLength = 0;
		} else {
			arrayLengthExpr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_LongArray_length"));
			arrayLength = (Integer) evaluate(arrayLengthExpr, int.class);
		}

		if (0 > arrayLength || arrayLength > 10000) {// XXX: Hack to avoid java.lang.OutOfMemoryError
			System.out.println(" JUnit generation: WARNING. Array exceeding size 10,000 required for array variable storing type "+componentType.getName()+".");
			System.out.println(" Will generate instead array of size 100 with the first 100 positions");
			System.out.println(" of the original array.");
			System.out.println(" The actual array length is " + arrayLength);
			arrayLength = 100;
		}
		returnValue = Array.newInstance(componentType, arrayLength);
		instances.put(instanceName, returnValue);

		if (!isPruned("java_lang_LongArray_contents")) {
			AlloyExpression contents_expr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_LongArray_contents"));

			Object evalResult;
			try {
				evalResult = alloyEvaluateSupport(contents_expr);
			} catch (Err e) {
				throw new TacoException(e);
			}

			if (evalResult instanceof A4TupleSet) {
				A4TupleSet array_cotents = (A4TupleSet) evalResult;
				for (A4Tuple a4Tuple : array_cotents) {
					String index = a4Tuple.atom(0);
					String value = a4Tuple.atom(1);

					ExprConstant index_constant = ExprConstant.buildExprConstant(index);
					ExprConstant value_constant = ExprConstant.buildExprConstant(value);

					int integer_index = create_integer(index_constant);

					if (integer_index >= 0 && integer_index < arrayLength) {

						Class<?> inferredType = inferTypeOfExpression(value_constant);
						if (inferredType.equals(long.class)) {
							long long_value = create_long(value_constant);
							updateArrayValue(returnValue, inferredType, integer_index, long_value);
						}

					}
				}

			}
		}

		return returnValue;
	}

	private Object evaluateSparseObjectArray(String instanceName, AlloyExpression instanceExpr, Class<?> componentType) {
		Object returnValue;
		AlloyExpression arrayLengthExpr;
		int arrayLength;

		if (isPruned("java_lang_ObjectArray_length")) {
			arrayLength = 0;
		} else {
			arrayLengthExpr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_ObjectArray_length"));
			arrayLength = (Integer) evaluate(arrayLengthExpr, int.class);
		}

		if (0 > arrayLength || arrayLength > 10000) {// XXX: Hack to avoid java.lang.OutOfMemoryError
			System.out.println(" JUnit generation: WARNING. Array exceeding size 10,000 required for array variable storing type "+componentType.getName()+".");
			System.out.println(" Will generate instead array of size 100 with the first 100 positions");
			System.out.println(" of the original array.");
			System.out.println(" The actual array length is " + arrayLength);
			arrayLength = 100;

		}
		returnValue = Array.newInstance(componentType, arrayLength);
		instances.put(instanceName, returnValue);

		if (!isPruned("java_lang_ObjectArray_contents")) {
			AlloyExpression contents_expr = ExprJoin.join(instanceExpr, prefixExprVariable("java_lang_ObjectArray_contents"));

			Object evalResult;
			try {
				evalResult = alloyEvaluateSupport(contents_expr);
			} catch (Err e) {
				throw new TacoException(e);
			}

			if (evalResult instanceof A4TupleSet) {
				A4TupleSet array_cotents = (A4TupleSet) evalResult;
				for (A4Tuple a4Tuple : array_cotents) {
					String index = a4Tuple.atom(0);
					String value = a4Tuple.atom(1);

					ExprConstant index_constant = ExprConstant.buildExprConstant(index);
					ExprConstant value_constant = ExprConstant.buildExprConstant(value);

					int integer_index = create_integer(index_constant);

					if (integer_index >= 0 && integer_index < arrayLength) {

						Class<?> infieredType = inferTypeOfExpression(value_constant);
						if (infieredType.isAssignableFrom(Object.class) || infieredType.isAssignableFrom(Object[].class)) {
							Object value_object = evaluate(value_constant, infieredType);
							updateArrayValue(returnValue, infieredType, integer_index, value_object);
						}
					}
				}

			}
		}

		return returnValue;
	}
}

package ar.edu.taco.junit;

import static java.io.File.separator;

import ar.edu.taco.engine.StrykerStage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.junit.RecoveredInformation.StaticFieldInformation;
import ar.edu.taco.snapshot.SnapshotBuilder;


public class UnitTestBuilder {


    private static final String THIZ_0 = "thiz_0";
    private static Logger log = Logger.getLogger(UnitTestBuilder.class);
    static final private String FILE_SEPARATOR = File.separator;
    static final private String OUTPUT_DIR = "generated" + FILE_SEPARATOR;
    static final private String OUTPUT_SIMPLIFIED_JAVA_EXTENSION = ".java";
    private static final String PACKAGE_NAME = "ar.edu.generated.junit";
    private RecoveredInformation recoveredInformation;
    private ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private String outputPath = "";
    private String staticFieldNameFilter = "";
    // Keep the variables and objects that have already been created.
    // We use the identityHashCode of each object as the Key and the created variable name as Value
    private Map<Integer, String> createdInstances = new HashMap<Integer, String>();

    // Field parameterValues stores at least enough information to recover, for each formal parameter in the method
    // under analysis, the value the counterexample has determined.

    private Set<String> imports;
    private Map<Object, Integer> instancesIndex = new HashMap<Object, Integer>();
    // private List<String> parameterInstanceNames = new ArrayList<String>();

    public UnitTestBuilder(RecoveredInformation recoveredInformation/*, TacoAnalysisResult tacoAnalysisResult*/) {
        this.recoveredInformation = recoveredInformation;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void createUnitTest() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        if (!this.recoveredInformation.isValidInformation()) {
            return;
        }
        String className = recoveredInformation.getClassToCheck().substring(recoveredInformation.getClassToCheck().lastIndexOf(".") + 1) + "Test";
        String methodName = recoveredInformation.getMethodToCheck();
        int suffix = recoveredInformation.getFileNameSuffix();
        Class<?> clazz;
        try {
            clazz = Class.forName(recoveredInformation.getClassToCheck(), true, loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
        }

        Constructor constructorToCheck = null;
        Method methodToCheck = null;

        try {
            // Gets parameters types
            Class<?>[] parameterTypes = null;
            for (Method aMethod : clazz.getDeclaredMethods()) {
                if (aMethod.getName().equals(recoveredInformation.getMethodToCheck())) {
                    parameterTypes = aMethod.getParameterTypes();
                }
            }

            int positionlastDot = recoveredInformation.getClassToCheck().lastIndexOf('.');
            String classToCheck = recoveredInformation.getClassToCheck().substring(positionlastDot + 1);
            if (recoveredInformation.getMethodToCheck().equals(classToCheck)) {
                //is a constructor;
                constructorToCheck = clazz.getConstructors()[0];
            } else {
                methodToCheck = clazz.getDeclaredMethod(recoveredInformation.getMethodToCheck(), parameterTypes);
            }
        } catch (SecurityException e) {
            throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
        }
        List<String> objectDefinitionStatements = new ArrayList<String>();
        List<String> objectInitializationStatements = new ArrayList<String>();
        imports = new HashSet<String>();
        imports.add("org.junit.Test");
        imports.add("java.lang.reflect.Field");
        if (methodToCheck != null) { // it is not a constructor
            if (!Modifier.isStatic(methodToCheck.getModifiers())) { // it is not a static method


                // Get Initialized this
                Object thizInstance = null;
                Class<?>[] parTypes = null;
                Object[] concretePars = null;
//				try {
//					thizInstance = clazz.newInstance();
                SnapshotBuilder s = new SnapshotBuilder(recoveredInformation, null);
                thizInstance = s.createNewInstance(clazz);
//				} catch (InstantiationException e) {

                //          In this case the class under analysis did not export a parameterless constructor. Therefore,
                //          there should be a constructor with parameters. We will get the first such constructor (since no
                //          information is available to choose a different one), and generate default values for its
                //          arguments. We will use this constructor to build an instance. This may fail in case this constructor
                //          throws an exception when executed on default values.

                Constructor<?>[] cons = clazz.getConstructors();
                if (cons.length == 0)
                    cons = clazz.getDeclaredConstructors();
                Constructor<?> c = cons[0];

                parTypes = c.getParameterTypes();
                concretePars = new Object[parTypes.length];
                int index = 0;
                for (Class<?> cl : parTypes) {
                    if (cl.isPrimitive()) {
                        if (cl.getName().equals("byte"))
                            concretePars[index] = 0;
                        if (cl.getName().equals("short"))
                            concretePars[index] = 0;
                        if (cl.getName().equals("int"))
                            concretePars[index] = 0;
                        if (cl.getName().equals("long"))
                            concretePars[index] = 0L;
                        if (cl.getName().equals("float"))
                            concretePars[index] = 0.0f;
                        if (cl.getName().equals("double"))
                            concretePars[index] = 0.0d;
                        if (cl.getName().equals("char"))
                            concretePars[index] = '\u0000';
                        if (cl.getName().equals("boolean"))
                            concretePars[index] = false;
                    } else {
                        concretePars[index] = null;
                    }
                    index++;
                }
//					try {
//						if (c.isAccessible())
//							thizInstance = c.newInstance(concretePars);
//						else {
//							c.setAccessible(true);
//							thizInstance = c.newInstance(concretePars);
//						}
//					} catch (InstantiationException ex){
//						e.printStackTrace();
//					} catch (Exception ex) {
//						throw new RuntimeException("DYNJALLOY ERROR! Possibly the class does not provide a constructor than can run on its parameters default values.");
//					}
//				} catch (IllegalAccessException e) {
//					throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
//				}
                if (recoveredInformation.getSnapshot().get(THIZ_0) != null) //It may be null even if the method is not static in case the method does not use any
                    //attribute from this (Alloy will prune variable "thiz").
                    thizInstance = recoveredInformation.getSnapshot().get(THIZ_0);

                String instanceCreation = recoveredInformation.getClassToCheck() + " instance = new " + recoveredInformation.getClassToCheck() + "(";
                if (concretePars != null) {
                    for (int parindex = 0; parindex < concretePars.length; parindex++) {
                        if (parTypes[parindex].isPrimitive() || this.isAutoboxingClass(parTypes[parindex])) {
                            instanceCreation += concretePars[parindex].toString();
                            if (parTypes[parindex].getSimpleName().equals("float"))
                                instanceCreation += "f";
                            if (parTypes[parindex].getSimpleName().equals("double"))
                                instanceCreation += "d";
                        } else
                            instanceCreation += "null";
                        if (parindex < concretePars.length - 1)
                            instanceCreation += ",";
                    }
                }
                instanceCreation += ");";
                objectDefinitionStatements.add(instanceCreation);

                // relate the Object got from Thiz_0 to the variable instance;
                this.createdInstances.put(System.identityHashCode(thizInstance), "instance");

                // Fields initialization
                if (thizInstance != null) {
                    getFieldsInitializationStatements(clazz, thizInstance/*, "instance"*/, objectDefinitionStatements, objectInitializationStatements);
                    //          objectDefinitionStatements.addAll(fieldsInitializationStatements);
                }

                // Static Fields initialization
                //            getStaticFieldsInitializationStatements(clazz, "instance", objectDefinitionStatements, objectInitializationStatements);
                //      objectDefinitionStatements.addAll(staticFieldsInitializationStatements);
            } else { //it is a static method
                String instanceCreation = recoveredInformation.getClassToCheck() + " instance = null;";
                objectDefinitionStatements.add(instanceCreation);
                this.createdInstances.put(System.identityHashCode(null), "instance");
            }
        } else { //it is a constructor
            assert (constructorToCheck != null);
            String instanceCreation = recoveredInformation.getClassToCheck() + " instance = null;";
            objectDefinitionStatements.add(instanceCreation);
            this.createdInstances.put(System.identityHashCode(null), "instance");
        }

        // Parameters Initialization
        List<String> paramsNames = getParametersInitializationStatements(clazz, objectDefinitionStatements, objectInitializationStatements);

        // Method invocation
        objectDefinitionStatements.addAll(objectInitializationStatements);
        List<String> methodInvocationStatements = new ArrayList<String>();
        if (methodToCheck != null) {
            methodInvocationStatements = getMethodInvocationStatements(clazz, methodToCheck, paramsNames);
            objectDefinitionStatements.addAll(methodInvocationStatements);
        }

        // Write JUnit to File
        String outputClassName = className + "_" + methodName + "_" + suffix;
        if (methodToCheck != null)
            writeToFile(outputClassName, methodName, imports, objectDefinitionStatements, methodToCheck.isAccessible());
        else
            writeToFile(outputClassName, methodName, imports, objectDefinitionStatements, constructorToCheck.isAccessible());
        StrykerStage.fileSuffix++;
        log.info("****** JUnit generation finished. Produced JUnit: '" + PACKAGE_NAME + "." + outputClassName + "' on 'generated' source folder ******");
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setStaticFieldNameFilter(String filter) {
        this.staticFieldNameFilter = filter;
    }


    /**
     * Generate the statements with the initialization of each static field
     *
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void getStaticFieldsInitializationStatements(Class<?> clazz, String storedVariableName,
                                                         List<String> objectDefinitionStatements, List<String> objectInitializationStatements) throws IllegalArgumentException, IllegalAccessException {
        if (!clazz.isAssignableFrom(Integer.class) && !clazz.isAssignableFrom(Long.class) && !clazz.isAssignableFrom(Float.class)) {
            List<StaticFieldInformation> staticFields = recoveredInformation.getStaticFieldsNameForClass(recoveredInformation.getClassToCheck());
            List<String> shortFieldNames = new ArrayList<String>();
            String moduleName = getModuleName(clazz);
            for (StaticFieldInformation staticField : staticFields) {
                if (staticField.getFieldName().matches("(roops_goal|myRoopsArray).*")) // XXX: Hack in order to ignore goals added by fajita
                    continue;
                String shortFieldName = staticField.getFieldName().replace(moduleName + "_", "");
                if (!shortFieldName.matches(staticFieldNameFilter))
                    shortFieldNames.add(shortFieldName);
            }
            if (!shortFieldNames.isEmpty()) {
                objectDefinitionStatements.add("");
                objectDefinitionStatements.add("// Static Fields Initialization");
                for (String shortFieldName : shortFieldNames) {
                    Field field = null;
                    try {
                        field = clazz.getDeclaredField(shortFieldName);
                    } catch (SecurityException e) {
                        throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
                    }
                    if (field.getType().isPrimitive()) {
                        String value = getValueForPrimitiveTypeField(field, null);
                        String statementToAdd = "updateValue(instance, \"" + shortFieldName + "\", " + value + ");";
                        objectInitializationStatements.add(statementToAdd);
                    } else if (field.getType().isArray()) {
                        Class<?> componentType = field.getType().getComponentType();
                        field.setAccessible(true);
                        Object fieldValue = field.get(null);
                        if (fieldValue != null) {
                            if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                                objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", " + this.createdInstances.get(System.identityHashCode(fieldValue)) + ");");
                            } else {
                                String arrayObjectVariableName = generateVariableName(fieldValue);
                                int instanceLength = Array.getLength(fieldValue);
                                this.createdInstances.put(System.identityHashCode(fieldValue), arrayObjectVariableName);
                                String statement = field.getType().getCanonicalName() + " " + arrayObjectVariableName + " = new " + componentType.getName() + "[" + instanceLength + "];";
                                objectDefinitionStatements.add(statement);
                                objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", " + arrayObjectVariableName + ");");
                                getValueForArray(componentType, fieldValue, objectDefinitionStatements, objectInitializationStatements/*, buildName*/);
                            }
                        }
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        imports.add("java.util.List");
                        imports.add("java.util.ArrayList");
                        Object fieldValue = field.get(null);
                        //DPD VAR NAME fix;
                        //String buildVariable = buildVariableName + "_" + shortFieldName;
                        String buildVariable = generateVariableName(fieldValue);
                        if (fieldValue == null) {
                            objectDefinitionStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", null);");
                        } else {
                            // If this instance was already created, then use the created variable
                            if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                                //String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                                //                          String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                                //                          statements.add(buildStatement);
                            } else {
                                String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = new java.util.ArrayList();";
                                this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
                                objectDefinitionStatements.add(buildStatement);
                                getStatementsForCollection(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                            }
                            objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                        }
                    } else if (Set.class.isAssignableFrom(field.getType())) {
                        imports.add("java.util.Set");
                        imports.add("kodkod.util.collections.IdentityHashSet");
                        Object fieldValue = field.get(null);
                        //DPD VAR NAME fix;
                        //String buildVariable = buildVariableName + "_" + shortFieldName;
                        String buildVariable = generateVariableName(fieldValue);
                        if (fieldValue == null) {
                            objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", null);");
                        } else {
                            // If this instance was already created, then use the created variable
                            if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                                //                          String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                                //                          String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                                //                          statements.add(buildStatement);
                            } else {
                                String generatedVariableName = generateVariableName(fieldValue);
                                String buildStatement = field.getType().getCanonicalName() + " " + generatedVariableName + " = new kodkod.util.collections.IdentityHashSet();";
                                this.createdInstances.put(System.identityHashCode(fieldValue), generatedVariableName);
                                objectDefinitionStatements.add(buildStatement);
                                getStatementsForCollection(generatedVariableName, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                            }
                            objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                        }
                    } else if (Map.class.isAssignableFrom(field.getType())) {
                        imports.add("java.util.Map");
                        //imports.add("java.util.HashMap");
                        imports.add("java.util.IdentityHashMap");
                        Object fieldValue = field.get(null);
                        //DPD VAR NAME fix;
                        //String buildVariable = buildVariableName + "_" + shortFieldName;
                        String buildVariable = generateVariableName(fieldValue);
                        if (fieldValue == null) {
                            objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", null);");
                        } else {
                            // If this instance was already created, then use the created variable
                            if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                                //String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                                //                          String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                                //                          statements.add(buildStatement);
                            } else {
                                //DPD
                                String buildStatement = /*field.getType().getCanonicalName()*/ "Map" + " " + buildVariable + " = new java.util.IdentityHashMap();";
                                this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
                                objectDefinitionStatements.add(buildStatement);
                                getStatementsForMap(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                            }
                            objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                        }
                    } else if (Object.class.isAssignableFrom(field.getType().getClass())) {
                        Object fieldValue = field.get(null);
                        //DPD VAR NAME fix;
                        //String buildVariable = buildVariableName + "_" + shortFieldName;
                        String buildVariable = generateVariableName(fieldValue);
                        if (fieldValue == null) {
                            objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", null);");
                        } else {
                            // If this instance was already created, then use the created variable
                            if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                                //String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                                //                          String buildStatement = fieldValue.getClass().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                                //                          statements.add(buildStatement);
                            } else {
                                String buildStatement = fieldValue.getClass().getCanonicalName() + " " + buildVariable + " = new " + fieldValue.getClass().getCanonicalName() + "();";
                                this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
                                objectDefinitionStatements.add(buildStatement);
                                getFieldsInitializationStatements(field.getType(), fieldValue/*, buildVariableName*/, objectDefinitionStatements, objectInitializationStatements);
                            }
                            objectInitializationStatements.add("updateValue(" + storedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                        }
                    }
                }
            }
        }
    }

    /**
     * @param value
     * @return
     */
    private String generateVariableName(Object value) {
        if (value == null) {
            return "null_0";
        }
        if (this.createdInstances.containsKey(System.identityHashCode(value))) {
            return this.createdInstances.get(System.identityHashCode(value));
        }
        //      if (this.recoveredInformation.getSnapshot().containsValue(value)){
        //          for (Entry<String, Object> e : this.recoveredInformation.getSnapshot().entrySet()){
        //              if (e.getValue().equals(value)){
        //                  String varNameSubZero =  e.getKey();
        //                  String varName = varNameSubZero.substring(0, varNameSubZero.lastIndexOf("_"));
        //                  return varName;
        //              }
        //          }
        //      }
        int index;
        if (instancesIndex.containsKey(value.getClass())) {
            index = instancesIndex.get(value.getClass());
        } else {
            index = 0;
        }
        index++;
        instancesIndex.put(value.getClass(), index);
        //String retValue = buildVariableName + "_" + className + "_" + index;
        //      String instanceName = getAKeyforValue(value);
        String instanceName = "";
        // remove "_NNN" from "xxxx_NNN".
        int lastUnderscore = instanceName.lastIndexOf("_");
        if (lastUnderscore >= 0) {
            String stringBeforeLastUnderscore = instanceName.substring(lastUnderscore + 1);
            if (stringBeforeLastUnderscore.matches("[0-9]+")) {
                instanceName = instanceName.substring(0, lastUnderscore);
            }
        }
        String className = value.getClass().getSimpleName();
        className = className.replaceAll("\\[\\]", "\\_array");
        String retValue = instanceName + "_" + className + "_" + index;
        return retValue;
    }

    private String getAKeyforValue(Object value) {
        SortedSet<String> candidates = new TreeSet<String>();
        for (Entry<String, Object> entry : this.recoveredInformation.getSnapshot().entrySet()) {
            if (System.identityHashCode(entry.getValue()) == System.identityHashCode(value)) {
                candidates.add(entry.getKey());
            }
        }
        if (candidates.isEmpty()) {
            return "";
        } else {
            String retValue = candidates.first();
            return retValue;
        }
    }


    private void getFieldsInitializationStatements(Class<?> clazz, Object instance, /*, String buildName*/
                                                      	List<String> objectDefinitionStatements, List<String> objectInitializationStatements) throws IllegalArgumentException,
            											IllegalAccessException {
        Queue<ImmutablePair<Object,Class<?>>> objectsToInitialize = new LinkedList<>();
		ImmutablePair<Object,Class<?>> instancePair = new ImmutablePair<Object,Class<?>>(instance, clazz);
        objectsToInitialize.add(instancePair);
        while (!objectsToInitialize.isEmpty()) {
			ImmutablePair<Object,Class<?>> currPair = objectsToInitialize.poll();
			Object theObj = currPair.left;
			Class<?> theClass = currPair.right;
            if (theClass.getDeclaredFields().length > 0 && !theClass.isAssignableFrom(Integer.class) && !theClass.isAssignableFrom(Long.class) && !theClass.isAssignableFrom(Float.class)) {
				String objectGeneratedVariableName = generateVariableName(theObj);
				objectInitializationStatements.add("// Fields Initialization for '" + objectGeneratedVariableName + "'");
				for (Field field : theClass.getDeclaredFields()) {
//					field.setAccessible(true);
//					ImmutablePair<Object,Class<?>> fielsPlusType = new ImmutablePair<>(field.get(instance), )
					field.setAccessible(true);
					//                if (!Modifier.isStatic(field.getModifiers())) {
					String shortFieldName = field.getName();
					if (field.getType().isPrimitive() || this.isAutoboxingClass(field.getType())) {
						String value = getValueForPrimitiveTypeField(field, theObj);
						String statementToAdd = "updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", " + value + ");";
						objectInitializationStatements.add(statementToAdd);
					} else if (field.getType().isArray()) {
						Class<?> componentType = field.getType().getComponentType();
						Object fieldValue = field.get(theObj);
						//DPD
						if (fieldValue != null) {
							if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
								objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", " + this.createdInstances.get(System.identityHashCode(fieldValue)) + ");");
							} else {
								String arrayObjectVariableName = generateVariableName(fieldValue);
								int instanceLength = Array.getLength(fieldValue);
								this.createdInstances.put(System.identityHashCode(fieldValue), arrayObjectVariableName);
								String statement = field.getType().getCanonicalName() + " " + arrayObjectVariableName + " = new " + componentType.getName() + "[" + instanceLength + "];";
								objectDefinitionStatements.add(statement);
								objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", " + arrayObjectVariableName + ");");
								getValueForArray(componentType, fieldValue, objectDefinitionStatements, objectInitializationStatements/*, buildName*/);
								//                              String arrayObjectVariableName = generateVariableName(fieldValue);
								//                              this.createdInstances.put(System.identityHashCode(fieldValue), arrayObjectVariableName);
								//                              objectDefinitionStatements.add(field.getType().getCanonicalName() + " " + arrayObjectVariableName + " = new " + field.getType().getCanonicalName() + ";");
								//
								//                              getValueForArray(componentType, fieldValue, objectDefinitionStatements, objectInitializationStatements/*, buildName*/);
								//                              objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + arrayObjectVariableName + ");");
							}
						}
					} else if (List.class.isAssignableFrom(field.getType())) {
						imports.add("java.util.List");
						imports.add("java.util.ArrayList");
						Object fieldValue = field.get(theObj);
						//DPD VAR NAME fix;
						//String buildVariable = buildVariableName + "_" + shortFieldName;
						String buildVariable = generateVariableName(fieldValue);
						// If this instance was already created, then use the created variable
						if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
							//                          String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
							//                          String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
							//                          statements.add(buildStatement);
						} else {
							String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = new java.util.ArrayList();";
							this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
							objectDefinitionStatements.add(buildStatement);
							getStatementsForCollection(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
						}
						objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
					} else if (Set.class.isAssignableFrom(field.getType())) {
						imports.add("java.util.Set");
						imports.add("kodkod.util.collections.IdentityHashSet");
						Object fieldValue = field.get(theObj);
						//DPD VAR NAME fix;
						//String buildVariable = buildVariableName + "_" + shortFieldName;
						String buildVariable = generateVariableName(fieldValue);
						if (fieldValue == null) {
							objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", null);");
						} else {
							// If this instance was already created, then use the created variable
							if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
								//String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
								//                              String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
								//                              statements.add(buildStatement);
							} else {
								String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = new kodkod.util.collections.IdentityHashSet();";
								this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
								objectDefinitionStatements.add(buildStatement);
								getStatementsForCollection(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
							}
							objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
						}
					} else if (Map.class.isAssignableFrom(field.getType())) {
						imports.add("java.util.Map");
						imports.add("java.util.IdentityHashMap");
						Object fieldValue = field.get(theObj);
						//DPD VAR NAME fix;
						//String buildVariable = buildVariableName + "_" + shortFieldName;
						String buildVariable = generateVariableName(fieldValue);
						if (fieldValue == null) {
							objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", null);");
						} else {
							// If this instance was already created, then use the created variable
							if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
								//String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
								//                              String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
								//                              statements.add(buildStatement);
							} else {
								String buildStatement = /*field.getType().getCanonicalName()*/ "Map" + " " + buildVariable + " = new java.util.IdentityHashMap();";
								this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
								objectDefinitionStatements.add(buildStatement);
								getStatementsForMap(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
							}
							objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
						}
					} else if (Object.class.isAssignableFrom(field.getType())) {
						Object fieldValue = field.get(theObj);
						//DPD VAR NAME fix;
						//String buildVariable = buildVariableName + "_" + shortFieldName;
						String buildVariable = generateVariableName(fieldValue);
						if (fieldValue == null) {
							objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", null);");
						} else {
							// If this instance was already created, then use the created variable
							if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
								//String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
								//                              String buildStatement = fieldValue.getClass().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
								//                              statements.add(buildStatement);
							} else {
								Object thizInstance = null;
								//                              Constructor<?>[] cons = field.getType().getConstructors();
								Constructor<?>[] cons = fieldValue.getClass().getConstructors();
								Constructor<?> c = cons[0];
								Class<?>[] parTypes = null;
								parTypes = c.getParameterTypes();
								Object[] concretePars = new Object[parTypes.length];
								int index = 0;
								for (Class<?> cl : parTypes) {
									if (cl.isPrimitive()) {
										if (cl.getName().equals("byte"))
											concretePars[index] = 0;
										if (cl.getName().equals("short"))
											concretePars[index] = 0;
										if (cl.getName().equals("int"))
											concretePars[index] = 0;
										if (cl.getName().equals("long"))
											concretePars[index] = 0L;
										if (cl.getName().equals("float"))
											concretePars[index] = 0.0f;
										if (cl.getName().equals("double"))
											concretePars[index] = 0.0d;
										if (cl.getName().equals("char"))
											concretePars[index] = '\u0000';
										if (cl.getName().equals("boolean"))
											concretePars[index] = false;
									} else {
										try {
											concretePars[index] = cl.newInstance();
										} catch (InstantiationException ie) {
											concretePars[index] = null;
										}
									}
									index++;
								}
								try {
									thizInstance = c.newInstance(concretePars);
								} catch (InstantiationException ex) {
									ex.printStackTrace();
								} catch (Exception ex) {
									throw new RuntimeException("DYNJALLOY ERROR! Possibly the class does not provide a constructor than can run on its parameters default values.");
								}
								//                              if (recoveredInformation.getSnapshot().get(THIZ_0) != null) //It may be null even if the method is not static in case the method does not use any
								//                                  //attribute from this (Alloy will prune variable "thiz").
								//                                  thizInstance = recoveredInformation.getSnapshot().get(THIZ_0);
								String instanceCreation = thizInstance.getClass().getCanonicalName() + " " + buildVariable + " = new " + thizInstance.getClass().getCanonicalName() + "(";
								if (concretePars != null) {
									for (int idx = 0; idx < concretePars.length; idx++) {
										if (parTypes[idx].isPrimitive()) {
											instanceCreation += concretePars[idx].toString();
											if (parTypes[idx].getSimpleName().equals("float"))
												instanceCreation += "f";
											if (parTypes[idx].getSimpleName().equals("double"))
												instanceCreation += "d";
										} else
											instanceCreation += "(" + parTypes[idx].getSimpleName() + ")null";
										if (idx < concretePars.length - 1)
											instanceCreation += ",";
									}
								}
								instanceCreation += ");";
								this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
								objectDefinitionStatements.add(instanceCreation);
								objectsToInitialize.offer(new ImmutablePair<Object, Class<?>>(fieldValue, field.getType()));
							}
							objectInitializationStatements.add("updateValue(" + objectGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
						}
					}
				}
            }
        }

    }


    /**
     * @param clazz
     * @param instance
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void getFieldsInitializationStatementsOld(Class<?> clazz, Object instance, /*, String buildName*/
                                                   List<String> objectDefinitionStatements, List<String> objectInitializationStatements) throws IllegalArgumentException,
            IllegalAccessException {
        //      List<String> statements = new ArrayList<String>();
        //System.out.println("clazz_+ instance" + clazz + ":" + instance);
        if (clazz.getDeclaredFields().length > 0 && !clazz.isAssignableFrom(Integer.class) && !clazz.isAssignableFrom(Long.class) && !clazz.isAssignableFrom(Float.class)) {
            String instanceGeneratedVariableName = generateVariableName(instance);
            objectInitializationStatements.add("// Fields Initialization for '" + instanceGeneratedVariableName + "'");
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                //                if (!Modifier.isStatic(field.getModifiers())) {
                String shortFieldName = field.getName();
                if (field.getType().isPrimitive() || this.isAutoboxingClass(field.getType())) {
                    String value = getValueForPrimitiveTypeField(field, instance);
                    String statementToAdd = "updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + value + ");";
                    objectInitializationStatements.add(statementToAdd);
                } else if (field.getType().isArray()) {
                    Class<?> componentType = field.getType().getComponentType();
                    Object fieldValue = field.get(instance);
                    //DPD
                    if (fieldValue != null) {
                        if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                            objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + this.createdInstances.get(System.identityHashCode(fieldValue)) + ");");
                        } else {
                            String arrayObjectVariableName = generateVariableName(fieldValue);
                            int instanceLength = Array.getLength(fieldValue);
                            this.createdInstances.put(System.identityHashCode(fieldValue), arrayObjectVariableName);
                            String statement = field.getType().getCanonicalName() + " " + arrayObjectVariableName + " = new " + componentType.getName() + "[" + instanceLength + "];";
                            objectDefinitionStatements.add(statement);
                            objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + arrayObjectVariableName + ");");
                            getValueForArray(componentType, fieldValue, objectDefinitionStatements, objectInitializationStatements/*, buildName*/);
                            //                              String arrayObjectVariableName = generateVariableName(fieldValue);
                            //                              this.createdInstances.put(System.identityHashCode(fieldValue), arrayObjectVariableName);
                            //                              objectDefinitionStatements.add(field.getType().getCanonicalName() + " " + arrayObjectVariableName + " = new " + field.getType().getCanonicalName() + ";");
                            //
                            //                              getValueForArray(componentType, fieldValue, objectDefinitionStatements, objectInitializationStatements/*, buildName*/);
                            //                              objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + arrayObjectVariableName + ");");
                        }
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    imports.add("java.util.List");
                    imports.add("java.util.ArrayList");
                    Object fieldValue = field.get(instance);
                    //DPD VAR NAME fix;
                    //String buildVariable = buildVariableName + "_" + shortFieldName;
                    String buildVariable = generateVariableName(fieldValue);
                    // If this instance was already created, then use the created variable
                    if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                        //                          String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                        //                          String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                        //                          statements.add(buildStatement);
                    } else {
                        String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = new java.util.ArrayList();";
                        this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
                        objectDefinitionStatements.add(buildStatement);
                        getStatementsForCollection(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                    }
                    objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                } else if (Set.class.isAssignableFrom(field.getType())) {
                    imports.add("java.util.Set");
                    imports.add("kodkod.util.collections.IdentityHashSet");
                    Object fieldValue = field.get(instance);
                    //DPD VAR NAME fix;
                    //String buildVariable = buildVariableName + "_" + shortFieldName;
                    String buildVariable = generateVariableName(fieldValue);
                    if (fieldValue == null) {
                        objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", null);");
                    } else {
                        // If this instance was already created, then use the created variable
                        if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                            //String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                            //                              String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                            //                              statements.add(buildStatement);
                        } else {
                            String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = new kodkod.util.collections.IdentityHashSet();";
                            this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
                            objectDefinitionStatements.add(buildStatement);
                            getStatementsForCollection(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                        }
                        objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                    }
                } else if (Map.class.isAssignableFrom(field.getType())) {
                    imports.add("java.util.Map");
                    imports.add("java.util.IdentityHashMap");
                    Object fieldValue = field.get(instance);
                    //DPD VAR NAME fix;
                    //String buildVariable = buildVariableName + "_" + shortFieldName;
                    String buildVariable = generateVariableName(fieldValue);
                    if (fieldValue == null) {
                        objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", null);");
                    } else {
                        // If this instance was already created, then use the created variable
                        if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                            //String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                            //                              String buildStatement = field.getType().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                            //                              statements.add(buildStatement);
                        } else {
                            String buildStatement = /*field.getType().getCanonicalName()*/ "Map" + " " + buildVariable + " = new java.util.IdentityHashMap();";
                            this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
                            objectDefinitionStatements.add(buildStatement);
                            getStatementsForMap(buildVariable, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                        }
                        objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                    }
                } else if (Object.class.isAssignableFrom(field.getType())) {
                    Object fieldValue = field.get(instance);
                    //DPD VAR NAME fix;
                    //String buildVariable = buildVariableName + "_" + shortFieldName;
                    String buildVariable = generateVariableName(fieldValue);
                    if (fieldValue == null) {
                        objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", null);");
                    } else {
                        // If this instance was already created, then use the created variable
                        if (this.createdInstances.containsKey(System.identityHashCode(fieldValue))) {
                            //String createdVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
                            //                              String buildStatement = fieldValue.getClass().getCanonicalName() + " " + buildVariable + " = " + createdVariable + ";";
                            //                              statements.add(buildStatement);
                        } else {
                            Object thizInstance = null;
                            //                              Constructor<?>[] cons = field.getType().getConstructors();
                            Constructor<?>[] cons = fieldValue.getClass().getConstructors();
                            Constructor<?> c = cons[0];
                            Class<?>[] parTypes = null;
                            parTypes = c.getParameterTypes();
                            Object[] concretePars = new Object[parTypes.length];
                            int index = 0;
                            for (Class<?> cl : parTypes) {
                                if (cl.isPrimitive()) {
                                    if (cl.getName().equals("byte"))
                                        concretePars[index] = 0;
                                    if (cl.getName().equals("short"))
                                        concretePars[index] = 0;
                                    if (cl.getName().equals("int"))
                                        concretePars[index] = 0;
                                    if (cl.getName().equals("long"))
                                        concretePars[index] = 0L;
                                    if (cl.getName().equals("float"))
                                        concretePars[index] = 0.0f;
                                    if (cl.getName().equals("double"))
                                        concretePars[index] = 0.0d;
                                    if (cl.getName().equals("char"))
                                        concretePars[index] = '\u0000';
                                    if (cl.getName().equals("boolean"))
                                        concretePars[index] = false;
                                } else {
                                    try {
                                        concretePars[index] = cl.newInstance();
                                    } catch (InstantiationException ie) {
                                        concretePars[index] = null;
                                    }
                                }
                                index++;
                            }
                            try {
                                thizInstance = c.newInstance(concretePars);
                            } catch (InstantiationException ex) {
                                ex.printStackTrace();
                            } catch (Exception ex) {
                                throw new RuntimeException("DYNJALLOY ERROR! Possibly the class does not provide a constructor than can run on its parameters default values.");
                            }
                            //                              if (recoveredInformation.getSnapshot().get(THIZ_0) != null) //It may be null even if the method is not static in case the method does not use any
                            //                                  //attribute from this (Alloy will prune variable "thiz").
                            //                                  thizInstance = recoveredInformation.getSnapshot().get(THIZ_0);
                            String instanceCreation = thizInstance.getClass().getCanonicalName() + " " + buildVariable + " = new " + thizInstance.getClass().getCanonicalName() + "(";
                            if (concretePars != null) {
                                for (int idx = 0; idx < concretePars.length; idx++) {
                                    if (parTypes[idx].isPrimitive()) {
                                        instanceCreation += concretePars[idx].toString();
                                        if (parTypes[idx].getSimpleName().equals("float"))
                                            instanceCreation += "f";
                                        if (parTypes[idx].getSimpleName().equals("double"))
                                            instanceCreation += "d";
                                    } else
                                        instanceCreation += "(" + parTypes[idx].getSimpleName() + ")null";
                                    if (idx < concretePars.length - 1)
                                        instanceCreation += ",";
                                }
                            }
                            instanceCreation += ");";
                            this.createdInstances.put(System.identityHashCode(fieldValue), buildVariable);
                            objectDefinitionStatements.add(instanceCreation);
                            getFieldsInitializationStatements(field.getType(), fieldValue, objectDefinitionStatements, objectInitializationStatements);
                        }
                        objectInitializationStatements.add("updateValue(" + instanceGeneratedVariableName + ", \"" + shortFieldName + "\", " + buildVariable + ");");
                    }
                }
                //                }
            }
        }
    }

    /**
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private List<String> getParametersInitializationStatements(Class<?> clazz, List<String> objectDefinitionStatements,
                                                               List<String> objectInitializationStatements) throws InstantiationException, IllegalAccessException {
        List<String> paramsNames = new ArrayList<String>();
        if (recoveredInformation.getMethodParametersNames().size() > 0) {
            objectInitializationStatements.add("// Parameter Initialization");
            // Gets parameters types
            Class<?>[] parameterTypes = new Class<?>[recoveredInformation.getMethodParametersNames().size()];
            for (Method aMethod : clazz.getDeclaredMethods()) {
                if (aMethod.getName().equals(recoveredInformation.getMethodToCheck())) {
                    parameterTypes = aMethod.getParameterTypes();
                }
            }
            for (Constructor aConstructor : clazz.getDeclaredConstructors()) {
                String fullyQualifiedConstructorName = aConstructor.getName();
                int posLastDot = fullyQualifiedConstructorName.lastIndexOf('.');
                String constructorName = fullyQualifiedConstructorName.substring(posLastDot + 1);
                if (constructorName.equals(recoveredInformation.getMethodToCheck())) {
                    parameterTypes = aConstructor.getParameterTypes();
                }
            }
            for (int index = 0; index < parameterTypes.length; index++) {
                String aParameterName = recoveredInformation.getMethodParametersNames().get(index);
                Class<?> parameterType = parameterTypes[index];
                Object parameterInstance;
                if (recoveredInformation.getSnapshot().containsKey(aParameterName + "_0")) {
                    parameterInstance = recoveredInformation.getSnapshot().get(aParameterName + "_0");
                } else {
                    parameterInstance = defaultValue(parameterType);
                }
                //String generatedVariableName = generateVariableName(aParameterName, parameterInstance);
                String generatedName = createStatementsForParameter(parameterType, aParameterName, parameterInstance, objectDefinitionStatements, objectInitializationStatements);
                paramsNames.add(generatedName);
                String instanceName = createdInstances.get(System.identityHashCode(parameterInstance));
            }
        }
        return paramsNames;
    }

    private Object defaultValue(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Object value;
        if (clazz.isPrimitive()) {
            String typeSimpleName = clazz.getSimpleName();
            if (typeSimpleName.equals("boolean")) {
                value = false;
            } else if (typeSimpleName.endsWith("byte")) {
                value = 0;
            } else if (typeSimpleName.endsWith("char")) {
                value = "'a'";
            } else if (typeSimpleName.endsWith("double")) {
                value = 0;
            } else if (typeSimpleName.endsWith("float")) {
                value = 0;
            } else if (typeSimpleName.endsWith("int")) {
                value = 0;
            } else if (typeSimpleName.endsWith("long")) {
                value = 0L;
            } else if (typeSimpleName.endsWith("short")) {
                value = 0;
            } else {
                throw new TacoException("ERROR: Undefined in class UnitTestBuilder, method defaultValue");
            }
        } else {
            String name = clazz.getName();
            if (name.equals("java.lang.Boolean")) {
                value = false;
            } else if (name.endsWith("java.lang.Byte")) {
                value = 0;
            } else if (name.endsWith("java.lang.Character")) {
                value = "'a'";
            } else if (name.endsWith("java.lang.Double")) {
                value = 0;
            } else if (name.endsWith("java.lang.Float")) {
                value = 0;
            } else if (name.endsWith("java.lang.Integer")) {
                value = 0;
            } else if (name.endsWith("java.lang.Long")) {
                value = 0;
            } else if (name.endsWith("java.lang.Short")) {
                value = 0;
            } else {
                value = clazz.newInstance();
            }
        }
        return value;
    }

    /**
     * @param clazz
     * @param instance
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     */
    private String createStatementsForParameter(Class<?> clazz, String parameterName, Object instance,
                                                List<String> objectDefinitionStatements, List<String> objectInitializationStatements) throws IllegalArgumentException,
            IllegalAccessException, InstantiationException {
        //              String generatedVariableName = generateVariableName(instance);
        String generatedVariableName = parameterName;

        if (!this.createdInstances.containsKey(System.identityHashCode(instance))) {

            Object parameterValue = this.recoveredInformation.getSnapshot().get(parameterName + "_0");

            if (clazz.isPrimitive() || isAutoboxingClass(clazz)) {
                String value;
                if (parameterValue == null) {
                    value = String.valueOf(defaultValue(clazz));
                } else {
                    if (Character.class.isAssignableFrom(clazz)) {
                        value = "'" + String.valueOf(parameterValue) + "'";
                    } else {
                        if (parameterValue instanceof Long) {
                            value = String.valueOf(parameterValue);
                            instance = new Long(Long.parseLong(value));
                            value += "L";
                        } else if (parameterValue instanceof Float) {
                            if (((Float) parameterValue).isNaN()) {
                                value = "Float.NaN";
                                instance = Float.NaN;
                                ;
                            } else if (((Float) parameterValue).isInfinite() && (Float) parameterValue > 0f) {
                                value = "Float.POSITIVE_INFINITY";
                                instance = Float.POSITIVE_INFINITY;
                            } else if (((Float) parameterValue).isInfinite() && (Float) parameterValue < 0f) {
                                value = "Float.NEGATIVE_INFINITY";
                                instance = Float.NEGATIVE_INFINITY;
                            } else {
                                value = String.valueOf((Float) parameterValue) + "f";
                                instance = new Float(Float.parseFloat(value));
                            }
                        } else if (parameterValue instanceof Integer) {
                            value = String.valueOf(parameterValue);
                            instance = new Integer(Integer.parseInt(value));
                        } else
                            value = String.valueOf(parameterValue);
                    }

                }

                //DPD VAR NAME fix
                //statements.add(clazz.getCanonicalName() + " " + parameterName + " = " + value + ";");
                // this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                objectDefinitionStatements.add(clazz.getCanonicalName() + " " + generatedVariableName + " = " + value + ";");

            } else if (parameterValue == null) {
                //DPD NULL CASE
                //String statement = clazz.getCanonicalName() + " " + parameterName + " = null;";
                String statement = clazz.getCanonicalName() + " " + generatedVariableName + " = null;";
                this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                objectDefinitionStatements.add(statement);

            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                log.debug(clazz + ":" + parameterName + ":" + instance);
                int instanceLength = Array.getLength(instance);

                this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                //                              List<String> pendingStatements = new ArrayList<String>();
                getValueForArray(componentType, parameterValue, objectDefinitionStatements, objectInitializationStatements);

                //DPD VAR NAME fix
                //String statement = clazz.getCanonicalName() + " " + parameterName + " = new " + clazz.getCanonicalName() + values + ";";
                String statement = clazz.getCanonicalName() + " " + generatedVariableName + " = new " + componentType.getName() + "[" + instanceLength + "];";
                objectDefinitionStatements.add(statement);

            } else if (List.class.isAssignableFrom(clazz)) {
                imports.add("java.util.List");
                imports.add("java.util.ArrayList");

                Object fieldValue = instance;

                if (fieldValue == null) {
                    //DPD VAR NAME fix
                    //statements.add(clazz.getCanonicalName() + " " + parameterName + " = null;");
                    objectDefinitionStatements.add(clazz.getCanonicalName() + " " + generatedVariableName + " = null;");
                    this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                } else {
                    //DPD VAR NAME fix
                    //String buildStatement = clazz.getCanonicalName() + " " + parameterName + " = new java.util.ArrayList();";
                    String buildStatement = clazz.getCanonicalName() + " " + generatedVariableName + " = new java.util.ArrayList();";
                    this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                    objectDefinitionStatements.add(buildStatement);

                    //DPD VAR NAME fix
                    //List<String> initializationStatements = getStatementsForCollection(parameterName, fieldValue);
                    getStatementsForCollection(generatedVariableName, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                }

            } else if (Set.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Set");
                imports.add("kodkod.util.collections.IdentityHashSet");
                //String buildVariable = parameterName;
                Object fieldValue = instance;

                if (fieldValue == null) {
                    //DPD VAR NAME fix
                    //statements.add(clazz.getCanonicalName() + " " + parameterName + " = null;");
                    objectDefinitionStatements.add(clazz.getCanonicalName() + " " + generatedVariableName + " = null;");
                    this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                } else {
                    //DPD VAR NAME fix
                    //String buildStatement = clazz.getCanonicalName() + " " + parameterName + " = new kodkod.util.collections.IdentityHashSet();";
                    String buildStatement = clazz.getCanonicalName() + " " + generatedVariableName + " = new kodkod.util.collections.IdentityHashSet();";
                    this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                    //this.createdInstances.put(System.identityHashCode(instance), parameterName);
                    objectDefinitionStatements.add(buildStatement);

                    getStatementsForCollection(generatedVariableName, fieldValue, objectDefinitionStatements, objectInitializationStatements);
                }
            } else if (Map.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Map");
                imports.add("java.util.IdentityHashMap");
                //String buildVariable = parameterName;
                Object fieldValue = instance;

                if (fieldValue == null) {
                    //DPD VAR NAME fix
                    //statements.add(clazz.getCanonicalName() + " " + parameterName + " = null;");
                    objectDefinitionStatements.add(clazz.getCanonicalName() + " " + generatedVariableName + " = null;");
                    this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                } else {
                    //                              if (this.createdInstances.containsKey(System.identityHashCode(instance))) {
                    //                                      String createdVariable = this.createdInstances.get(System.identityHashCode(instance));
                    ////                                    String buildStatement = clazz.getCanonicalName() + " " + parameterName + " = (" + clazz.getCanonicalName() + ") " + createdVariable + ";";
                    ////                                    statements.add(buildStatement);
                    //
                    //                              } else {
                    //DPD VAR NAME fix
                    //String buildStatement = /*clazz.getCanonicalName() +*/ "Map " + parameterName + " = new java.util.IdentityHashMap();";
                    String buildStatement = /*clazz.getCanonicalName() +*/ "Map " + generatedVariableName + " = new java.util.IdentityHashMap();";
                    this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                    objectDefinitionStatements.add(buildStatement);

                    getStatementsForMap(generatedVariableName, fieldValue, objectDefinitionStatements, objectInitializationStatements);

                    //                              }
                }
            } else if (Object.class.isAssignableFrom(clazz)) {
                Object thizInstance = null;
                Constructor<?>[] cons = clazz.getConstructors();
                Constructor<?> c = cons[0];
                Class<?>[] parTypes = null;
                parTypes = c.getParameterTypes();
                Object[] concretePars = new Object[parTypes.length];
                int index = 0;
                for (Class<?> cl : parTypes) {
                    if (cl.isPrimitive()) {
                        if (cl.getName().equals("byte"))
                            concretePars[index] = 0;
                        if (cl.getName().equals("short"))
                            concretePars[index] = 0;
                        if (cl.getName().equals("int"))
                            concretePars[index] = 0;
                        if (cl.getName().equals("long"))
                            concretePars[index] = 0L;
                        if (cl.getName().equals("float"))
                            concretePars[index] = 0.0f;
                        if (cl.getName().equals("double"))
                            concretePars[index] = 0.0d;
                        if (cl.getName().equals("char"))
                            concretePars[index] = '\u0000';
                        if (cl.getName().equals("boolean"))
                            concretePars[index] = false;
                    } else {
                        if (clazz.getName().equals("java.lang.String") && cl.isArray()) {
                            concretePars[index] = new byte[0];
                        } else
                            concretePars[index] = null;
                    }
                    index++;
                }
                try {
                    thizInstance = c.newInstance(concretePars);
                } catch (InstantiationException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    throw new RuntimeException("DYNJALLOY ERROR! Possibly the class does not provide a constructor than can run on its parameters default values.");
                }

                if (recoveredInformation.getSnapshot().get(THIZ_0) != null) //It may be null even if the method is not static in case the method does not use any
                    //attribute from this (Alloy will prune variable "thiz").
                    thizInstance = recoveredInformation.getSnapshot().get(THIZ_0);


                String instanceCreation = clazz.getCanonicalName() + " " + generatedVariableName + " = new " + clazz.getCanonicalName() + "(";
                if (concretePars != null) {
                    for (int idx = 0; idx < concretePars.length; idx++) {
                        if (parTypes[idx].isPrimitive()) {
                            instanceCreation += concretePars[idx].toString();
                            if (parTypes[idx].getSimpleName().equals("float"))
                                instanceCreation += "f";
                            if (parTypes[idx].getSimpleName().equals("double"))
                                instanceCreation += "d";
                        } else {
                            if (clazz.getName().equals("java.lang.String")) {
                                instanceCreation += "";
                            } else
                                instanceCreation += "null";
                        }
                        if (idx < concretePars.length - 1)
                            instanceCreation += ",";
                    }
                }
                instanceCreation += ");";

                this.createdInstances.put(System.identityHashCode(instance), generatedVariableName);
                objectDefinitionStatements.add(instanceCreation);


                getFieldsInitializationStatements(clazz, instance, objectDefinitionStatements, objectInitializationStatements);


            } else {
                objectInitializationStatements.add("// Initialization for parameter '" + parameterName + "' not yet implemented. Type: " + clazz);
            }

        } else {
            String currentVarHoldingTheInstance = this.createdInstances.get(System.identityHashCode(instance));
            objectInitializationStatements.add(clazz.getCanonicalName() + " " + generatedVariableName + " = " + currentVarHoldingTheInstance + ";");
        }
        return generatedVariableName;
    }

    /**
     * @param fieldValue
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void getStatementsForCollection(String variableName, Object fieldValue,
                                            List<String> objectDefinitionStatements, List<String> objectInitializationStatements) throws IllegalArgumentException, IllegalAccessException {
        Collection<?> listFieldValue = (Collection<?>) fieldValue;
        @SuppressWarnings("unused")
        int index = 0;
        for (Object value : listFieldValue) {
            //Class<?> clazz = value.getClass();
            Class<?> clazz;
            if (value == null) {
                clazz = null;
            } else {
                clazz = value.getClass();
            }
            if (value == null) {
                objectInitializationStatements.add(variableName + ".add(null);");
            } else if (isAutoboxingClass(clazz)) {
                String contentValue;
                if (Character.class.isAssignableFrom(value.getClass())) {
                    contentValue = "'" + String.valueOf(value) + "'";
                } else {
                    contentValue = String.valueOf(value);
                }
                objectInitializationStatements.add(variableName + ".add(" + contentValue + ");");
            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(value);
                if (!this.createdInstances.containsKey(System.identityHashCode(value))) {
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    String statement = clazz.getCanonicalName() + " " + variableToCreate + " = new " + clazz.getCanonicalName() + ";";
                    objectDefinitionStatements.add(statement);
                    getValueForArray(componentType, value, objectDefinitionStatements, objectInitializationStatements);
                }
                objectInitializationStatements.add(variableName + ".add(" + variableToCreate + ");");
            } else if (List.class.isAssignableFrom(clazz)) {
                imports.add("java.util.List");
                imports.add("java.util.ArrayList");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(value);
                if (!this.createdInstances.containsKey(System.identityHashCode(value))) {
                    String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = new java.util.ArrayList();";
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForCollection(variableToCreate, value, objectDefinitionStatements, objectInitializationStatements);
                }
                objectInitializationStatements.add(variableName + ".add(" + variableToCreate + ");");
            } else if (Set.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Set");
                imports.add("kodkod.util.collections.IdentityHashSet");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(value);
                if (!this.createdInstances.containsKey(System.identityHashCode(value))) {
                    String buildStatement = value.getClass().getCanonicalName() + " " + variableToCreate + " = new kodkod.util.collections.IdentityHashSet();";
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForCollection(variableToCreate, value, objectDefinitionStatements, objectInitializationStatements);
                }
                objectInitializationStatements.add(variableName + ".add(" + variableToCreate + ");");
            } else if (Map.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Map");
                imports.add("java.util.IdentityHashMap");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(value);
                if (this.createdInstances.containsKey(System.identityHashCode(value))) {
                    //String createdVariable = this.createdInstances.get(System.identityHashCode(value));
                    //                  String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = (" + clazz.getCanonicalName() + ") " + createdVariable + ";";
                    //                  statements.add(buildStatement);
                } else {
                    String buildStatement = /*clazz.getCanonicalName() +*/ "Map " + variableToCreate + " = new java.util.IdentityHashMap();";
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForMap(variableToCreate, value, objectDefinitionStatements, objectInitializationStatements);
                }
                objectInitializationStatements.add(variableName + ".add(" + variableToCreate + ");");
            } else {
                if (!hasDefaultConstructor(value.getClass())) {
                    throw new RuntimeException("DYNJALLOY ERROR!: Type: " + value.getClass().getCanonicalName() + " has no default Constructor.");
                }
                //DPD VAR NAME fix;
                //String createdVariable = variableName + "_" + value.getClass().getSimpleName() + "_" + index;
                String createdVariable = generateVariableName(value);
                if (this.createdInstances.containsKey(System.identityHashCode(value))) {
                    //DPD BEGIN
                    //String previousCreatedVariable = this.createdInstances.get(System.identityHashCode(value));
                    //                  String buildStatement = value.getClass().getCanonicalName() + " " + createdVariable + " = " + previousCreatedVariable + ";";
                    //                  statements.add(buildStatement);
                    //DPD END
                } else {
                    String buildStatement = value.getClass().getCanonicalName() + " " + createdVariable + " = new " + value.getClass().getCanonicalName() + "();";
                    this.createdInstances.put(System.identityHashCode(value), createdVariable);
                    objectDefinitionStatements.add(buildStatement);
                    getFieldsInitializationStatements(value.getClass(), value, objectDefinitionStatements, objectInitializationStatements);
                }
                objectInitializationStatements.add(variableName + ".add(" + createdVariable + ");");
            }
            index++;
        }
    }

    /**
     * @param fieldValue
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void getStatementsForMap(String variableName, Object fieldValue,
                                     List<String> objectDefinitionStatements, List<String> objectInitializationStatements) throws IllegalArgumentException, IllegalAccessException {
        Map<?, ?> mapFieldValue = (Map<?, ?>) fieldValue;
        @SuppressWarnings("unused")
        int index = 0;
        //      List<String> pendingStatements = new ArrayList<String>();
        for (Entry<?, ?> anEntry : mapFieldValue.entrySet()) {
            // Analyze the key
            String keyString = null;
            Object keyValue = anEntry.getKey();
            Class<?> clazz;
            if (keyValue == null) {
                clazz = null;
            } else {
                clazz = keyValue.getClass();
            }
            if (keyValue == null) {
                keyString = "null";
            } else if (isAutoboxingClass(keyValue.getClass())) {
                if (Character.class.isAssignableFrom(keyValue.getClass())) {
                    keyString = "'" + String.valueOf(keyValue) + "'";
                } else {
                    keyString = String.valueOf(keyValue);
                }
            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(keyValue);
                if (!this.createdInstances.containsKey(System.identityHashCode(keyValue))) {
                    this.createdInstances.put(System.identityHashCode(keyValue), variableToCreate);
                    String statement = clazz.getCanonicalName() + " " + variableToCreate + " = new " + clazz.getCanonicalName() + ";";
                    objectDefinitionStatements.add(statement);
                    getValueForArray(componentType, keyValue, objectDefinitionStatements, objectInitializationStatements);
                }
                keyString = variableToCreate;
            } else if (List.class.isAssignableFrom(clazz)) {
                imports.add("java.util.List");
                imports.add("java.util.ArrayList");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(keyValue);
                if (!this.createdInstances.containsKey(System.identityHashCode(keyValue))) {
                    String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = new java.util.ArrayList();";
                    this.createdInstances.put(System.identityHashCode(keyValue), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForCollection(variableToCreate, keyValue, objectDefinitionStatements, objectInitializationStatements);
                }
                keyString = variableToCreate;
            } else if (Set.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Set");
                imports.add("kodkod.util.collections.IdentityHashSet");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(keyValue);
                if (!this.createdInstances.containsKey(System.identityHashCode(keyValue))) {
                    String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = new kodkod.util.collections.IdentityHashSet();";
                    this.createdInstances.put(System.identityHashCode(keyValue), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForCollection(variableToCreate, keyValue, objectDefinitionStatements, objectInitializationStatements);
                }
                keyString = variableToCreate;
            } else if (Map.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Map");
                imports.add("java.util.IdentityHashMap");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(keyValue);
                if (this.createdInstances.containsKey(System.identityHashCode(keyValue))) {
                    //                  String createdVariable = this.createdInstances.get(System.identityHashCode(keyValue));
                    //                  String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = (" + clazz.getCanonicalName() + ") " + createdVariable + ";";
                    //                  statements.add(buildStatement);
                } else {
                    String buildStatement = /*clazz.getCanonicalName() +*/ "Map " + variableToCreate + " = new java.util.IdentityHashMap();";
                    this.createdInstances.put(System.identityHashCode(keyValue), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForMap(variableToCreate, keyValue, objectDefinitionStatements, objectInitializationStatements);
                }
                keyString = variableToCreate;
            } else {
                if (!hasDefaultConstructor(keyValue.getClass())) {
                    throw new RuntimeException("DYNJALLOY ERROR!: Type: " + keyValue.getClass().getCanonicalName() + " has no default Constructor.");
                }
                //DPD VAR NAME fix;
                //  String createdVariable = variableName + "_" + keyValue.getClass().getSimpleName() + "_" + index + "_" + "key";
                String createdVariable = generateVariableName(keyValue);
                if (this.createdInstances.containsKey(System.identityHashCode(keyValue))) {
                    //DPD BEGIN
                    //                  String previousCreatedVariable = this.createdInstances.get(System.identityHashCode(keyValue));
                    //                  String buildStatement = keyValue.getClass().getCanonicalName() + " " + createdVariable + " = (" + keyValue.getClass().getCanonicalName() + ") " + previousCreatedVariable + ";";
                    //                  statements.add(buildStatement);
                    //DPD END
                } else {
                    String buildStatement = keyValue.getClass().getCanonicalName() + " " + createdVariable + " = new " + keyValue.getClass().getCanonicalName() + "();";
                    this.createdInstances.put(System.identityHashCode(keyValue), createdVariable);
                    objectDefinitionStatements.add(buildStatement);
                    getFieldsInitializationStatements(keyValue.getClass(), keyValue, objectDefinitionStatements, objectInitializationStatements);
                }
                keyString = createdVariable;
            }
            // Analyze the Value
            String valueString = null;
            Object value = anEntry.getValue();
            if (value == null) {
                clazz = null;
            } else {
                clazz = value.getClass();
            }
            if (value == null) {
                valueString = "null";
            } else if (isAutoboxingClass(clazz)) {
                //valueString = String.valueOf(value);
                if (Character.class.isAssignableFrom(value.getClass())) {
                    valueString = "'" + String.valueOf(value) + "'";
                } else {
                    valueString = String.valueOf(value);
                }
            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                //DPD VAR NAME fix;
                //  String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(value);
                if (!this.createdInstances.containsKey(System.identityHashCode(value))) {
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    String statement = clazz.getCanonicalName() + " " + variableToCreate + " = new " + clazz.getCanonicalName() + ";";
                    objectDefinitionStatements.add(statement);
                    getValueForArray(componentType, value, objectDefinitionStatements, objectInitializationStatements);
                }
                valueString = variableToCreate;
            } else if (List.class.isAssignableFrom(clazz)) {
                imports.add("java.util.List");
                imports.add("java.util.ArrayList");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(value);
                if (!this.createdInstances.containsKey(System.identityHashCode(value))) {
                    String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = new java.util.ArrayList();";
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForCollection(variableToCreate, value, objectDefinitionStatements, objectInitializationStatements);
                }
                valueString = variableToCreate;
            } else if (Set.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Set");
                imports.add("kodkod.util.collections.IdentityHashSet");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(value);
                if (!this.createdInstances.containsKey(System.identityHashCode(value))) {
                    String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = new kodkod.util.collections.IdentityHashSet();";
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForCollection(variableToCreate, value, objectDefinitionStatements, objectInitializationStatements);
                }
                valueString = variableToCreate;
            } else if (Map.class.isAssignableFrom(clazz)) {
                imports.add("java.util.Map");
                imports.add("java.util.IdentityHashMap");
                //DPD VAR NAME fix;
                //String variableToCreate = variableName + "_" + index;
                String variableToCreate = generateVariableName(fieldValue);
                if (this.createdInstances.containsKey(System.identityHashCode(value))) {
                    //                  String createdVariable = this.createdInstances.get(System.identityHashCode(value));
                    //                  String buildStatement = clazz.getCanonicalName() + " " + variableToCreate + " = (" + clazz.getCanonicalName() + ") " + createdVariable + ";";
                    //                  statements.add(buildStatement);
                } else {
                    String buildStatement = /*clazz.getCanonicalName() +*/ "Map " + variableToCreate + " = new java.util.IdentityHashMap();";
                    this.createdInstances.put(System.identityHashCode(value), variableToCreate);
                    objectDefinitionStatements.add(buildStatement);
                    getStatementsForMap(variableToCreate, value, objectDefinitionStatements, objectInitializationStatements);
                }
                valueString = variableToCreate;
            } else {
                if (!hasDefaultConstructor(value.getClass())) {
                    throw new RuntimeException("DYNJALLOY ERROR!: Type: " + value.getClass().getCanonicalName() + " has no default Constructor.");
                }
                //DPD VAR NAME fix;
                //String createdVariable = variableName + "_" + value.getClass().getSimpleName() + "_" + index + "_" + "value";
                String createdVariable = generateVariableName(fieldValue);
                if (this.createdInstances.containsKey(System.identityHashCode(value))) {
                    //DPD BEGIN
                    //                  String previousCreatedVariable = this.createdInstances.get(System.identityHashCode(value));
                    //                  String buildStatement = value.getClass().getCanonicalName() + " " + createdVariable + " = (" + value.getClass().getCanonicalName() + ")" + previousCreatedVariable + ";";
                    //                  statements.add(buildStatement);
                    //DPD END
                } else {
                    String buildStatement = value.getClass().getCanonicalName() + " " + createdVariable + " = new " + value.getClass().getCanonicalName() + "();";
                    this.createdInstances.put(System.identityHashCode(value), createdVariable);
                    objectDefinitionStatements.add(buildStatement);
                    getFieldsInitializationStatements(value.getClass(), value, objectDefinitionStatements, objectInitializationStatements);
                }
                valueString = createdVariable;
            }
            objectInitializationStatements.add(variableName + ".put(" + keyString + ", " + valueString + ");");
            index++;
        }
    }
    //  int tempVarCount = 0;
    //  private String createVar() {
    //      String s = "tmp_" + tempVarCount++;
    //      return s;
    //  }

    /**
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @requires fieldValue already stored in this.createdInstances
     */
    private void getValueForArray(Class<?> componentType, Object fieldValue, List<String> objectDefinitionStatements,
                                  List<String> objectInitializationStatements) throws IllegalArgumentException, IllegalAccessException {
        int length = Array.getLength(fieldValue);
        String arrayAssignedVariable = this.createdInstances.get(System.identityHashCode(fieldValue));
        log.debug("getValueForArray");
        log.debug(fieldValue.toString());
        log.debug(componentType);
        if (componentType.isPrimitive()) {
            String typeSimpleName = componentType.getSimpleName();
            for (int x = 0; x < length; x++) {
                Object elementAsObject = Array.get(fieldValue, x);
                Class<?> elementClass;
                if (elementAsObject == null) {
                    elementClass = null;
                } else {
                    elementClass = elementAsObject.getClass();
                }
                if (typeSimpleName.equals("boolean")) {
                    if (elementClass != null && Boolean.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + Boolean.toString(Array.getBoolean(fieldValue, x)) + ";";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = false;";
                        objectInitializationStatements.add(statement);
                    }
                } else if (typeSimpleName.endsWith("byte")) {
                    if (elementClass != null && Byte.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + Byte.toString(Array.getByte(fieldValue, x)) + ";";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = 0;";
                        objectInitializationStatements.add(statement);
                    }
                } else if (typeSimpleName.endsWith("char")) {
                    if (elementClass != null && Character.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = '" + Character.toString(Array.getChar(fieldValue, x)) + "';";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = 'a';";
                        objectInitializationStatements.add(statement);
                    }
                } else if (typeSimpleName.endsWith("double")) {
                    if (elementClass != null && Double.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + Double.toString(Array.getDouble(fieldValue, x)) + ";";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = 0.0d;";
                        objectInitializationStatements.add(statement);
                    }
                } else if (typeSimpleName.endsWith("float")) {
                    if (elementClass != null && Float.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + Float.toString(Array.getFloat(fieldValue, x)) + ";";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = 0.0f;";
                        objectInitializationStatements.add(statement);
                    }
                } else if (typeSimpleName.endsWith("int")) {
                    if (elementClass != null && Integer.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + Integer.toString(Array.getInt(fieldValue, x)) + ";";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = 0;";
                        objectInitializationStatements.add(statement);
                    }
                } else if (typeSimpleName.endsWith("long")) {
                    if (elementClass != null && Long.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + Long.toString(Array.getLong(fieldValue, x)) + "L;";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = 0L;";
                        objectInitializationStatements.add(statement);
                    }
                } else if (typeSimpleName.endsWith("short")) {
                    if (elementClass != null && Short.class.isAssignableFrom(elementClass)) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + Short.toString(Array.getShort(fieldValue, x)) + ";";
                        objectInitializationStatements.add(statement);
                    } else {
                        String statement = arrayAssignedVariable + "[" + x + "] = 0;";
                        objectInitializationStatements.add(statement);
                    }
                } else {
                    log.error("ERROR: No definida");
                }
            }
        } else {
            //throw new TacoNotImplementedYetException("Type: " + componentType.toString() + "Not Implemented yet");
            for (int x = 0; x < length; x++) {
                Object instance = Array.get(fieldValue, x);
                if (instance == null) {
                    String statement = arrayAssignedVariable + "[" + x + "] = null;";
                    objectInitializationStatements.add(statement);
                } else {
                    if (this.createdInstances.containsKey(System.identityHashCode(instance))) {
                        String statement = arrayAssignedVariable + "[" + x + "] = " + this.createdInstances.get(System.identityHashCode(instance)) + ";";
                        objectInitializationStatements.add(statement);
                    } else {
                        //String varName = createVar();
                        //String initValue;
                        String typeSimpleName = instance.getClass().getSimpleName();
                        if (typeSimpleName.equals("boolean") || typeSimpleName.equals("Boolean")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + instance.toString() + ";";
                            objectInitializationStatements.add(statement);
                        } else if (typeSimpleName.endsWith("byte") || typeSimpleName.endsWith("Integer")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + instance.toString() + ";";
                            objectInitializationStatements.add(statement);
                        } else if (typeSimpleName.endsWith("char") || typeSimpleName.endsWith("Character")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + "'" + instance + "';";
                            objectInitializationStatements.add(statement);
                        } else if (typeSimpleName.endsWith("double") || typeSimpleName.endsWith("Double")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + instance.toString() + ";";
                            objectInitializationStatements.add(statement);
                        } else if (typeSimpleName.endsWith("float") || typeSimpleName.endsWith("Float")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + instance.toString() + ";";
                            objectInitializationStatements.add(statement);
                        } else if (typeSimpleName.endsWith("int") || typeSimpleName.endsWith("Integer")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + instance.toString() + ";";
                            objectInitializationStatements.add(statement);
                        } else if (typeSimpleName.endsWith("long") || typeSimpleName.endsWith("Long")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + instance.toString() + "L;";
                            objectInitializationStatements.add(statement);
                        } else if (typeSimpleName.endsWith("short") || typeSimpleName.endsWith("Short")) {
                            String statement = arrayAssignedVariable + "[" + x + "] = " + instance.toString() + ";";
                            objectInitializationStatements.add(statement);
                        } else if (instance.getClass().isArray()) {
                            String generatedName = generateVariableName(instance);
                            this.createdInstances.put(System.identityHashCode(instance), generatedName);
                            //String statement = instance.getClass().getCanonicalName() + " " + generatedName + " = new " + instance.getClass().getCanonicalName() + ";";
                            int instanceLength = Array.getLength(instance);
                            String statement = instance.getClass().getCanonicalName() + " " + generatedName + " = new " + componentType.getName() + "[" + instanceLength + "];";
                            objectDefinitionStatements.add(statement);
                            Class<?> aComponentType2 = instance.getClass().getComponentType();
                            getValueForArray(aComponentType2, instance, objectDefinitionStatements, objectInitializationStatements);
                            statement = arrayAssignedVariable + "[" + x + "] = " + generatedName + ";";
                            objectInitializationStatements.add(statement);
                        } else {
                            String generatedName = generateVariableName(instance);
                            this.createdInstances.put(System.identityHashCode(instance), generatedName);
                            String statement = instance.getClass().getCanonicalName() + " " + generatedName + " = new " + instance.getClass().getCanonicalName() + "();";
                            objectDefinitionStatements.add(statement);
                            statement = arrayAssignedVariable + "[" + x + "] = " + generatedName + ";";
                            objectInitializationStatements.add(statement);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param aField
     * @param instance
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private String getValueForPrimitiveTypeField(Field aField, Object instance) throws IllegalArgumentException, IllegalAccessException {
        String typeSimpleName = aField.getType().getSimpleName();
        String value = null;

        boolean fieldIsAccessible = aField.isAccessible();
        if (!fieldIsAccessible)
            aField.setAccessible(true);

        if (typeSimpleName.equals("boolean")) {
            value = Boolean.toString(aField.getBoolean(instance));
        } else if (typeSimpleName.endsWith("byte")) {
            value = Byte.toString(aField.getByte(instance));
        } else if (typeSimpleName.endsWith("char")) {
            value = "'" + Character.toString(aField.getChar(instance)) + "'";
        } else if (typeSimpleName.endsWith("double")) {
            value = Double.toString(aField.getDouble(instance));
            if (value.equals("Infinity"))
                value = "Double.POSITIVE_INFINITY";
            else if (value.equals("-Infinity"))
                value = "Double.NEGATIVE_INFINITY";
            else if (value.equals("NaN"))
                value = "Double.NaN";
            else
                value = value + "d";
        } else if (typeSimpleName.endsWith("float")) {
            value = Float.toString(aField.getFloat(instance));
            if (value.equals("Infinity"))
                value = "Float.POSITIVE_INFINITY";
            else if (value.equals("-Infinity"))
                value = "Float.NEGATIVE_INFINITY";
            else if (value.equals("NaN"))
                value = "Float.NaN";
            else
                value = value + "f";
        } else if (typeSimpleName.endsWith("int")) {
            value = Integer.toString(aField.getInt(instance));
        } else if (typeSimpleName.endsWith("long")) {
            value = Long.toString(aField.getLong(instance)) + "L";
        } else if (typeSimpleName.endsWith("short")) {
            value = Short.toString(aField.getShort(instance));
        } else {
            throw new TacoException("ERROR: " + typeSimpleName + " not supported");
        }
        if (!fieldIsAccessible)
            aField.setAccessible(false);
        return value;
    }

    /**
     * Generate the statements to invoke the method from a JUnit tests
     *
     * @param clazz
     * @param methodToCheck
     * @return
     */
    private List<String> getMethodInvocationStatements(Class<?> clazz, Method methodToCheck, List<String> paramsNames) {
        List<String> statements = new ArrayList<String>();

        //String methodParameters = StringUtils.join(recoveredInformation.getMethodParametersNames(), ", ");
        String methodParameters = StringUtils.join(paramsNames, ", ");

        statements.add("");
        statements.add("// Method Invocation");
        if (!methodToCheck.isAccessible()) {
            statements.add("Method method = getAccessibleMethod(\"" + recoveredInformation.getClassToCheck() + "\", \""
                    + recoveredInformation.getMethodToCheck() + "\", true);");
            statements.add("try {");
            statements.add("    method.invoke(instance, " + ((methodParameters.length() == 0) ? "new Object[]{}" : "new Object[]{" + methodParameters + "}") + ");");
            statements.add("} catch (Exception e) {");
            statements.add("    e.printStackTrace();");
            statements.add("} ");
        } else {
            statements.add("instance." + recoveredInformation.getMethodToCheck() + "(" + methodParameters + ");");
        }

        return statements;
    }

    /**
     * Returns the path of the class that will hold the test for the pair
     * class-method found in the recoveredInformation.
     */
    public String getOutputClassFilename() throws IOException {
        File file = new File(getFilename(getOutputClassName()));
        return file.getAbsolutePath();
    }


    /**
     * Returns the name of the class that will hold the test for the pair
     * class-method found in the recoveredInformation.
     */
    public String getOutputClassName() {
        String className = recoveredInformation.getClassToCheck().substring(recoveredInformation.getClassToCheck().lastIndexOf(".") + 1) + "Test";
        String methodName = recoveredInformation.getMethodToCheck();
        int suffix = recoveredInformation.getFileNameSuffix();
        return className + "_" + methodName + "_" + suffix;
    }

    /**
     * Returns the path to the file where the unit test for the given class will
     * be written.
     */
    private String getFilename(String outputClassName) {
        String mySeparator;
        if (separator.equals("\\")) {
            mySeparator = "\\\\";
        } else {
            mySeparator = separator;
        }

        return outputPath + OUTPUT_DIR +
                PACKAGE_NAME.replaceAll("\\.", mySeparator) + separator +
                outputClassName + OUTPUT_SIMPLIFIED_JAVA_EXTENSION;
    }

    /**
     * Deletes the file where the unit test is written.
     * This method might be used in order to start a fresh run.
     */
    public void deleteFile(String outputClassName) {
        File file = new File(getFilename(outputClassName));
        if (file.exists())
            file.delete();
    }

    /**
     * Write the generated JUnit to the file system
     *
     * @param outputClassName
     * @param methodName
     * @param imports
     * @param statements
     * @param isAccessible
     */
    private void writeToFile(String outputClassName, String methodName, Set<String> imports, List<String> statements, boolean isAccessible) {
        JUnitPrettyPrinter jUnitPrettyPrinter = new JUnitPrettyPrinter();
        jUnitPrettyPrinter.setPackageName(PACKAGE_NAME);
        jUnitPrettyPrinter.setClassName(outputClassName);
        jUnitPrettyPrinter.setMethodName(methodName);
        jUnitPrettyPrinter.setImports(imports);
        jUnitPrettyPrinter.setStatements(statements);

        jUnitPrettyPrinter.writeToFile(getFilename(outputClassName), !isAccessible);
    }

    /**
     * @param clazz
     * @return
     */
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

    /**
     * @param parameterType
     * @return
     */
    private boolean hasDefaultConstructor(Class<?> parameterType) {
        boolean ret_val = false;

        // I the class is defined as inner class then, the default constructor
        // contains the containing class at first parameter.
        int constructorParametersAmong = 0;
        if (parameterType.isMemberClass() && !Modifier.isStatic(parameterType.getModifiers())) {
            constructorParametersAmong = 1;
        }

        for (Constructor<?> aConstructor : parameterType.getDeclaredConstructors()) {
            if (aConstructor.getParameterTypes().length == constructorParametersAmong) {
                ret_val = true;
            }
        }

        return ret_val;
    }

    /**
     * Extract the module name for a given parameter
     *
     * @param parameterType
     * @return
     */
    private String getModuleName(Class<?> parameterType) {
        String moduleName = null;
        if (parameterType.isMemberClass()) {
            moduleName = parameterType.getName().replace("$", ".inner.").replace('.', '_');
        } else {
            moduleName = parameterType.getCanonicalName().replace('.', '_');
        }
        return moduleName;
    }


}
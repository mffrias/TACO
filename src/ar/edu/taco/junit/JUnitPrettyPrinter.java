package ar.edu.taco.junit;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.multijava.util.compiler.TabbedPrintWriter;

import ar.edu.taco.utils.FileUtils;

public class JUnitPrettyPrinter {
	private static final int TAB_SIZE = 4;

	private String packageName;
	private String className;
	private String methodName;

	private Set<String> imports = new HashSet<String>();
	private List<String> statements = new ArrayList<String>();


	public void writeToFile(String filenamePath, boolean generateAccessibility) {

		StringWriter stringWriter = new StringWriter();
		TabbedPrintWriter printWriter = new TabbedPrintWriter(stringWriter);

		int testIndex = 0;

		File file = new File(filenamePath);
		if (!file.exists()) {
			printWriter.print("package " + packageName + ";");

			printWriter.println();
			printWriter.println();

			if (generateAccessibility) {
				printWriter.print("import java.lang.reflect.Method;");
				printWriter.println();
				printWriter.print("import java.lang.reflect.Constructor;");
				printWriter.println();
			}
			for (String anImport : this.imports) {
				printWriter.print("import " + anImport + ";");
				printWriter.println();
			}
			printWriter.print("import java.util.HashMap;");
			printWriter.println();

			printWriter.print("public class " + className + " {");
			printWriter.println();
			printWriter.println();

			printWriter.setPos(TAB_SIZE);


			//add new field "instance" to recover the actual input as an object to use from Stryker for hardcoding the input in the requires
			printWriter.print("public HashMap<String, Object> theData = getInstance();");
			printWriter.println();
			printWriter.println();

			//introduce new method "getInstance" to recover the code that build the actual object
			printWriter.print("public HashMap getInstance() {");
			printWriter.println();
			printWriter.print("try {");
			printWriter.println();

			List<String> ls = new ArrayList<String>();
			for (String statement : this.statements){
				if (statement.startsWith("// Method Invocation"))
					break;
				printWriter.print(statement);
				printWriter.println();
				ls.add(statement);

			}

			printWriter.println();
			printWriter.print("HashMap<String, Object> requiredData = new HashMap<String, Object>();");
			printWriter.println();
			printWriter.print("requiredData.put(\"thiz\", instance);");
			printWriter.println();

			for (String statement : ls){
				if (statement.equals("// Parameter Initialization")){
					break;
				}
				String[] split = statement.split(" ");
				String complexVarName = "";
				if (split.length>1){
					complexVarName = split[1];
				}
				String[] splitOnUnderscore = complexVarName.split("_");
				String theVarName = "";
				if (splitOnUnderscore.length > 0){
					theVarName = splitOnUnderscore[0];
				} 
				if (split.length > 1 && !statement.contains("//") && !statement.startsWith("updateValue") && !theVarName.equals("instance") && !split[1].startsWith("_")){
					printWriter.print("requiredData.put(" + "\"" + theVarName + "\", " + split[1] + ");");
					printWriter.println();
				}
			}

     		printWriter.print("return requiredData;");
			printWriter.println();
			printWriter.print("} catch (Exception ex) {ex.printStackTrace();}");
			printWriter.println();
			printWriter.print("return null;");
			printWriter.println();
			printWriter.print("}");
			printWriter.print("//endGetInstance");
			printWriter.println();
			printWriter.println();


			generateUpdateValueAuxiliarMethod(printWriter);
			if (generateAccessibility) {
				printWriter.println();
				printWriter.println();
				generateSetAccessibleAuxiliarConstructor(printWriter);	
				generateSetAccessibleAuxiliarMethod(printWriter);			
			}

			printWriter.println();

		} else {

			try {
				String fileContents = FileUtils.readFile(filenamePath);

				int lastTestDeclarationIndex = fileContents.lastIndexOf("test" + methodName + "_");
				if (lastTestDeclarationIndex >= 0) {
					String lastTestDeclaration = fileContents.substring(
							lastTestDeclarationIndex, fileContents.indexOf('(', lastTestDeclarationIndex));
					testIndex = Integer.parseInt(lastTestDeclaration.substring(
							lastTestDeclaration.lastIndexOf('_') + 1)) + 1;
				}

				int testClassEnd = fileContents.lastIndexOf('}');
				printWriter.print(fileContents.substring(0, testClassEnd));

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		printWriter.println();

		printWriter.setPos(TAB_SIZE);
		printWriter.print("@Test");
		printWriter.println();
		printWriter.print("public void test" + methodName + "_" + testIndex + "() {");
		printWriter.println();

		printWriter.setPos(2 * TAB_SIZE);
		for (String aStatement : this.statements) {
			printWriter.print(aStatement);
			printWriter.println();
		}
		printWriter.setPos(TAB_SIZE);

		printWriter.println();
		printWriter.print("}");

		printWriter.setPos(0);
		printWriter.println();
		printWriter.print("}");


		try {
			FileUtils.writeToFile(filenamePath, stringWriter.toString());
		} catch (IOException e) {
			throw new RuntimeException("DYNJALLOY ERROR!: Error writing generated unit test. " + e.getMessage());
		}
	}






	/**
	 * 
	 * @param printWriter
	 */
	private void generateSetAccessibleAuxiliarConstructor(TabbedPrintWriter printWriter) {
		printWriter.setPos(1 * TAB_SIZE);

		printWriter.print("/**");
		printWriter.println();
		printWriter.print(" * Auxiliar function that embed awful reflection code");
		printWriter.println();
		printWriter.print(" * ");
		printWriter.println();
		printWriter.print(" * @param className");
		printWriter.println();
		printWriter.print(" * @param methodName");
		printWriter.println();
		printWriter.print(" * @param value");
		printWriter.println();
		printWriter.print(" */");

		printWriter.println();
		printWriter.print("private Constructor<?> getAccessibleConstructor(String className, String methodName, boolean value) {");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);

		printWriter.print("Class<?> clazz;");
		printWriter.println();
		printWriter.print("try {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("clazz = Class.forName(className);");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("} catch (ClassNotFoundException e) {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("throw new RuntimeException(\"DYNJALLOY ERROR! \" + e.getMessage());");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("}");

		printWriter.println();
		printWriter.println();
		printWriter.print("Constructor<?> consToCheck = null;");
		printWriter.println();
		printWriter.print("try {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("// Gets parameters types");
		printWriter.println();
		printWriter.print("Class<?>[] parameterTypes = null;");
		printWriter.println();
		printWriter.print("for (Constructor<?> aMethod: clazz.getConstructors()) {");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("if (aMethod.getName().equals(methodName)) {");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("parameterTypes = aMethod.getParameterTypes();");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("}");

		printWriter.println();
		printWriter.print("consToCheck = clazz.getConstructor(parameterTypes);");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("} catch (SecurityException e) {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("throw new RuntimeException(\"DYNJALLOY ERROR! \" + e.getMessage());");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("} catch (NoSuchMethodException e) {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("throw new RuntimeException(\"DYNJALLOY ERROR! \" + e.getMessage());");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.print("consToCheck.setAccessible(value);");
		printWriter.println();
		printWriter.println();
		printWriter.print("return consToCheck;");

		printWriter.println();
		printWriter.setPos(1 * TAB_SIZE);
		printWriter.print("}");

	}




	/**
	 * 
	 * @param printWriter
	 */
	private void generateSetAccessibleAuxiliarMethod(TabbedPrintWriter printWriter) {
		printWriter.setPos(1 * TAB_SIZE);

		printWriter.print("/**");
		printWriter.println();
		printWriter.print(" * Auxiliar function that embed awful reflection code");
		printWriter.println();
		printWriter.print(" * ");
		printWriter.println();
		printWriter.print(" * @param className");
		printWriter.println();
		printWriter.print(" * @param methodName");
		printWriter.println();
		printWriter.print(" * @param value");
		printWriter.println();
		printWriter.print(" */");

		printWriter.println();
		printWriter.print("private Method getAccessibleMethod(String className, String methodName, boolean value) {");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);

		printWriter.print("Class<?> clazz;");
		printWriter.println();
		printWriter.print("try {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("clazz = Class.forName(className);");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("} catch (ClassNotFoundException e) {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("throw new RuntimeException(\"DYNJALLOY ERROR! \" + e.getMessage());");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("}");

		printWriter.println();
		printWriter.println();
		printWriter.print("Method methodToCheck = null;");
		printWriter.println();
		printWriter.print("try {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("// Gets parameters types");
		printWriter.println();
		printWriter.print("Class<?>[] parameterTypes = null;");
		printWriter.println();
		printWriter.print("for (Method aMethod: clazz.getDeclaredMethods()) {");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("if (aMethod.getName().equals(methodName)) {");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("parameterTypes = aMethod.getParameterTypes();");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("}");

		printWriter.println();
		printWriter.print("methodToCheck = clazz.getDeclaredMethod(methodName, parameterTypes);");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("} catch (SecurityException e) {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("throw new RuntimeException(\"DYNJALLOY ERROR! \" + e.getMessage());");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("} catch (NoSuchMethodException e) {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("throw new RuntimeException(\"DYNJALLOY ERROR! \" + e.getMessage());");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.print("methodToCheck.setAccessible(value);");
		printWriter.println();
		printWriter.println();
		printWriter.print("return methodToCheck;");

		printWriter.println();
		printWriter.setPos(1 * TAB_SIZE);
		printWriter.print("}");

	}

	/**
	 * This code generate an auxiliar function used to embed awful code needed to set values using reflection
	 * 
	 * @param printWriter
	 */
	private void generateUpdateValueAuxiliarMethod(TabbedPrintWriter printWriter) {
		printWriter.setPos(1 * TAB_SIZE);

		printWriter.print("/**");
		printWriter.println();
		printWriter.print(" * Auxiliar function that embed awful reflection code");
		printWriter.println();
		printWriter.print(" * ");
		printWriter.println();
		printWriter.print(" * @param instance");
		printWriter.println();
		printWriter.print(" * @param fieldName");
		printWriter.println();
		printWriter.print(" * @param value");
		printWriter.println();
		printWriter.print(" */");

		printWriter.println();
		printWriter.print("private void updateValue(Object instance, String fieldName, Object value) {");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("for (Field aField : instance.getClass().getDeclaredFields()) {");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("if (aField.getName().equals(fieldName)) {");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("try {");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("boolean isAccessible = true;");
		printWriter.println();
		printWriter.print("if (!aField.isAccessible()){");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("aField.setAccessible(true);");
		printWriter.println();
		printWriter.print("isAccessible = false;");
		printWriter.println();
		printWriter.print("}");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("if (aField.getType().isPrimitive()) {");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("String typeSimpleName = aField.getType().getSimpleName();");
		printWriter.println();
		printWriter.print("if (typeSimpleName.equals(\"boolean\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setBoolean(instance, (Boolean) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else if (typeSimpleName.endsWith(\"byte\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setByte(instance, (Byte) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else if (typeSimpleName.endsWith(\"char\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setChar(instance, (Character) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else if (typeSimpleName.endsWith(\"double\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setDouble(instance, (Double) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else if (typeSimpleName.endsWith(\"float\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setFloat(instance, (Float) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else if (typeSimpleName.endsWith(\"int\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setInt(instance, (Integer) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else if (typeSimpleName.endsWith(\"long\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setLong(instance, (Long) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else if (typeSimpleName.endsWith(\"short\")) {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("aField.setShort(instance, (Short) value);");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("} else {");
		printWriter.println();
		printWriter.setPos(7 * TAB_SIZE);
		printWriter.print("System.out.println(\"ERROR: No difinida\");");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("} else {");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("aField.set(instance, value);");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("};");
		printWriter.println();
		printWriter.print("if (!isAccessible)");
		printWriter.println();
		printWriter.setPos(6 * TAB_SIZE);
		printWriter.print("aField.setAccessible(false);");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("} catch (IllegalArgumentException e) {");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("e.printStackTrace();");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("} catch (IllegalAccessException e) {");
		printWriter.println();
		printWriter.setPos(5 * TAB_SIZE);
		printWriter.print("e.printStackTrace();");
		printWriter.println();
		printWriter.setPos(4 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.setPos(3 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.setPos(2 * TAB_SIZE);
		printWriter.print("}");
		printWriter.println();
		printWriter.setPos(1 * TAB_SIZE);
		printWriter.print("}");
	};

	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @param imports the imports to set
	 */
	public void setImports(Set<String> imports) {
		this.imports = imports;
	}

	/**
	 * 
	 * @param statements
	 */
	public void setStatements(List<String> statements) {
		this.statements = statements;
	}
}

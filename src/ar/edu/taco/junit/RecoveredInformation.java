package ar.edu.taco.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * What the counterexample-to-test path knows about one counterexample. {@code SnapshotStage}
 * fills it and {@code JUnitStage} or {@code JUnitStageFinalState} reads it, in
 * {@code TacoMain.run} and in Stryker's {@code DarwinistController.generateJunitFile}:
 * <ul>
 * <li>the class and the method under analysis: {@code classToCheck} as the driver gives it
 *     ({@code pkg.Class}), and {@code methodToCheck} qualified and without its parameter types
 *     ({@code pkg_Class_method}; see docs/METHOD-NAMING.md);</li>
 * <li>the names that {@link InformationRecoveryManager} recovers from the JML AST: fields and
 *     static fields by class, and the method's parameters; it also marks the information valid
 *     ({@code SnapshotStage} marks it invalid again when there is no counterexample);</li>
 * <li>the snapshot, the Java objects that {@code SnapshotBuilder} builds from the Alloy
 *     instance, keyed by variable and state: {@code thiz_0} and {@code <param>_0} (the input),
 *     and, for the final state, {@code thiz_1} and {@code return_1}; and the values of static
 *     fields, which no test builder reads;</li>
 * <li>{@code fileNameSuffix}, the {@code n} of the test class
 *     {@code <Class>_<method>_<n>}.</li>
 * </ul>
 * Plain mutable data, with no static state.
 */
public class RecoveredInformation {
	private boolean validInformation = false;
	
	private String methodToCheck;
	private String classToCheck;
	private Map<String, List<String>> fieldsName = new HashMap<String, List<String>>();
	private Map<String, List<StaticFieldInformation>> staticFieldsName = new HashMap<String, List<StaticFieldInformation>>();
	
	private Map<String, Object> snapshot = new HashMap<String, Object>();
	private Map<String, Map<String, Object>> staticFieldsValues = new HashMap<String, Map<String, Object>>();	
	public Map<String, Map<String, Object>> getStaticFieldsValues() {
		return staticFieldsValues;
	}
	
	private int fileNameSuffix = 0;

	public void setStaticFieldsValues(
			Map<String, Map<String, Object>> staticFieldsValues) {
		this.staticFieldsValues = staticFieldsValues;
	}

	/**
	 * @return the snapshot
	 */
	public Map<String, Object> getSnapshot() {
		return snapshot;
	}

	/**
	 * @param snapshot the snapshot to set
	 */
	public void setSnapshot(Map<String, Object> snapshot) {
		this.snapshot = snapshot;
	}	
	
	private List<String> methodParametersNames;
	
	/**
	 * 
	 * @author ggasser
	 *
	 */
	public static class StaticFieldInformation {
		private String fieldName;
		private String className;
		
		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}
		
		/**
		 * @param fieldName the fieldName to set
		 */
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		
		/**
		 * @return the className
		 */
		
		public String getClassName() {
			return className;
		}
		
		/**
		 * @param className the className to set
		 */
		public void setClassName(String className) {
			this.className = className;
		}
	}
	

	/**
	 * @return the validInformation
	 */
	public boolean isValidInformation() {
		return validInformation;
	}
	
	/**
	 * @param validInformation the validInformation to set
	 */
	public void setValidInformation(boolean validInformation) {
		this.validInformation = validInformation;
	}
	
	/**
	 * @return the fieldsName
	 */
	public List<String> getFieldsNameForClass(String className) {
		if (fieldsName.containsKey(className)) {
			return fieldsName.get(className);
		}
		return new ArrayList<String>();
	}
	
	/**
	 * @param fieldsName the fieldsName to set
	 */
	public void addFieldsName(Map<String, List<String>> fieldsName) {
		this.fieldsName.putAll(fieldsName);
	}
	
	/**
	 * @return the staticFieldsName
	 */
	public List<StaticFieldInformation> getStaticFieldsNameForClass(String className) {
		if (staticFieldsName.containsKey(className)) {
			return staticFieldsName.get(className);
		}
		return new ArrayList<RecoveredInformation.StaticFieldInformation>();
	}

	/**
	 * @param staticFieldsName the staticFieldsName to set
	 */
	public void addStaticFieldsName(Map<String, List<StaticFieldInformation>> staticFieldsName) {
		this.staticFieldsName.putAll(staticFieldsName);
	}

	/**
	 * @return the methodParametersNames
	 */
	public List<String> getMethodParametersNames() {
		return methodParametersNames;
	}
	
	/**
	 * @param methodParametersNames the methodParametersNames to set
	 */
	public void setMethodParametersNames(List<String> methodParametersNames) {
		this.methodParametersNames = methodParametersNames;
	}
	
	/**
	 * @return the methodToCheck
	 */
	public String getMethodToCheck() {
		
		return methodToCheck;
	}
	/**
	 * @param methodToCheck the methodToCheck to set
	 */
	public void setMethodToCheck(String methodToCheck) {
		this.methodToCheck = methodToCheck;
	}
	
	/**
	 * @return the classToCheck
	 */
	public String getClassToCheck() {
		return classToCheck;
	}
	
	/**
	 * @param classToCheck the classToCheck to set
	 */
	public void setClassToCheck(String classToCheck) {
		this.classToCheck = classToCheck;
	}
	
	
	/**
	 * @return the staticFieldsName
	 */
	public List<StaticFieldInformation> getStaticFields() {
		List<RecoveredInformation.StaticFieldInformation> staticFields = new ArrayList<RecoveredInformation.StaticFieldInformation>();
		for (Entry<String, List<StaticFieldInformation>> entry : staticFieldsName.entrySet()) {
			staticFields.addAll(entry.getValue());
		}
		return staticFields;
	}		
	
	
	public int getFileNameSuffix (){
		return fileNameSuffix;
	}
	
	
	/**
	 * Sets the number that ends the generated test's class name. {@code TacoMain.run} and
	 * {@code DarwinistController.generateJunitFile} pass {@code StrykerStage.fileSuffix}, the
	 * static counter that the test builders increment after each test.
	 *
	 * @param i the suffix
	 */
	public void setFileNameSuffix (int i){
		this.fileNameSuffix = i;
	}

}

package ar.edu.taco.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	
	public void setFileNameSuffix (int i){
		this.fileNameSuffix = i;
	}

}

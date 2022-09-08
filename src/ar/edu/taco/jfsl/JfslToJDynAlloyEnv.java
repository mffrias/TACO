package ar.edu.taco.jfsl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlNode;
import org.jmlspecs.checker.JmlTypeDeclaration;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JDynAlloyASTNode;
import ar.edu.jdynalloy.ast.JProgramDeclaration;

public class JfslToJDynAlloyEnv {

	private static class MethodData {
		public MethodData(String methodStringRepresentation,
				Vector<String> parameterIds) {
			this.methodStringRepresentation = methodStringRepresentation;
			this.parameterIds = parameterIds;
		}

		public final String methodStringRepresentation;

		public final Vector<String> parameterIds;
	}

	private JmlMethodDeclaration currentMethodDecl;
	private final Map<String, List<MethodData>> simpleJml_methods = new HashMap<String, List<MethodData>>();
	private boolean post = false;
	private final Set<String> JmlTypenames = new HashSet<String>();
	private final Map<String, String> renaming = new HashMap<String, String>();
	private final Map<JmlNode, JDynAlloyASTNode> simpleJml_to_JDynAlloy_map = new HashMap<JmlNode, JDynAlloyASTNode>();

	public void addMethod(String signatureId,
			String methodStringRepresentation, Vector<String> parameterIds) {
		MethodData methodData = new MethodData(methodStringRepresentation,
				parameterIds);

		List<MethodData> methodsOf = simpleJml_methods.get(signatureId);
		methodsOf.add(methodData);

	}

	public JfslToJDynAlloyEnv() {
		super();
		addJmlTypename("RuntimeException");
		putRename("RuntimeException", "java_lang_RuntimeException");
		
		addJmlTypename("Throwable");
		putRename("Throwable","java_lang_Throwable");
		
		addJmlTypename("Exception");
		putRename("Exception","java_lang_Exception");
		
		addJmlTypename("NullPointerException");
		putRename("NullPointerException", "java_lang_NullPointerException");
		
		addJmlTypename("IndexOutOfBoundsException");
		putRename("IndexOutOfBoundsException", "java_lang_IndexOutOfBoundsException");
		
		addJmlTypename("IllegalArgumentException");
		putRename("IllegalArgumentException", "java_lang_IllegalArgumentException");
				
		addJmlTypename("ClassCastException");
		putRename("ClassCastException", "java_lang_ClassCastException");

		addJmlTypename("Integer");
		putRename("Integer", "java_lang_Integer");
		
		addJmlTypename("Boolean");
		putRename("Boolean", "java_lang_Boolean");
	}
	
	public void addJmlTypename(String signatureId) {
		JmlTypenames.add(signatureId);
	}

	public void addSimpleJmlTypename(String signatureId) {
		simpleJml_methods.put(signatureId, new LinkedList<MethodData>());
	}

	public boolean containsJmlTypename(String signatureId) {
		return JmlTypenames.contains(signatureId);
	}

	public void enterPost() {
		post = true;

	}

	public String getParameterName(int i) {

		String methodStringRepresentation = this.currentMethodDecl
				.stringRepresentation();

		for (String signatureId : this.simpleJml_methods.keySet()) {
			List<MethodData> methodsOf = this.simpleJml_methods
					.get(signatureId);
			for (MethodData methodData : methodsOf) {
				if (methodData.methodStringRepresentation
						.equals(methodStringRepresentation)) {
					return methodData.parameterIds.get(i);
				}
			}
		}
		throw new IndexOutOfBoundsException("no index " + i + " in method "
				+ methodStringRepresentation);
	}

	public void leavePost() {
		post = false;
	}

	public boolean post() {
		return post;
	}

	public void setCurrentMethodDecl(JmlMethodDeclaration node) {
		this.currentMethodDecl = node;

	}

	public void putSimpleJmlToJDynAlloy(JmlNode simpleJmlNode,
			JDynAlloyASTNode jdynalloyNode) {
		this.simpleJml_to_JDynAlloy_map.put(simpleJmlNode, jdynalloyNode);
	}

	public void putRename(String oldName, String newName) {
		renaming.put(oldName, newName);
	}

	public boolean has_new_name(String old_name) {
		return renaming.containsKey(old_name);
	}

	public String get_new_name(String old_name) {
		return renaming.get(old_name);
	}

	public JDynAlloyModule get_module(JmlTypeDeclaration jmlTypeDeclaration) {
		return (JDynAlloyModule) this.simpleJml_to_JDynAlloy_map
				.get(jmlTypeDeclaration);
	}

	public JProgramDeclaration get_program_declaration(
			JmlMethodDeclaration jmlMethodDeclaration) {
		return (JProgramDeclaration) this.simpleJml_to_JDynAlloy_map
				.get(jmlMethodDeclaration);
	}

	public boolean containsSimpleJmlNodeMap(JmlNode simpleJmlNode) {
		return this.simpleJml_to_JDynAlloy_map.containsKey(simpleJmlNode);
	}

}

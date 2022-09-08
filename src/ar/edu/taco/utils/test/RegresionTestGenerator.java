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
package ar.edu.taco.utils.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class RegresionTestGenerator {

	//CONFIGURABLE VALUES
	private final String targetClassName = "slicing.koa.AuditLog";	
	private static final String[] METHODS_WITH_COUNTEREXAMPLE = { "addKiesKring", "getCurrentTimestamp" };
	//END CONFIGURABLE VALUES

	private static final String EOL = "\r\n";

	private final Set<String> methodsNameWithCounterexampleSet;

	private Map<String, Integer> methodsCount;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RegresionTestGenerator() {
		this.methodsNameWithCounterexampleSet = new HashSet();
		Collections.addAll(this.methodsNameWithCounterexampleSet, METHODS_WITH_COUNTEREXAMPLE);
	}

	public static void main(String[] args) throws ClassNotFoundException {
		RegresionTestGenerator regresionTestGenerator = new RegresionTestGenerator();
		regresionTestGenerator.doGenerate();
	}

	private void doGenerate() throws ClassNotFoundException {
		Class<?> targetClazz = retrieveClazz();
		collectMethods(targetClazz);
		String generatedTestSource = generateTestSource();
		System.out.println(generatedTestSource);
	}

	private String generateTestSource() {
		StringBuffer buffer = new StringBuffer();
//		generateHeader(buffer);
		generateBody(buffer);
//		generateFooter(buffer);

		return buffer.toString();
	}

	@SuppressWarnings("unused")
	private void generateHeader(StringBuffer buffer) {
		buffer.append("package ar.edu.taco.regresion.slicing.koa;" + EOL);
		buffer.append("" + EOL);
		buffer.append("import ar.edu.taco.regresion.GenericTestBase;" + EOL);
		buffer.append("import ar.uba.dc.rfm.dynalloy.visualization.VizException;" + EOL);
		buffer.append("" + EOL);
		buffer.append("public class " + simpleClassName(targetClassName) + "Test extends GenericTestBase {" + EOL);
		buffer.append("" + EOL);
		buffer.append("    @Override" + EOL);
		buffer.append("    protected String getClassToCheck() {" + EOL);
		buffer.append("         return \"" + targetClassName + "\";" + EOL);
		buffer.append("    }" + EOL);
		buffer.append("    @Override" + EOL);
		buffer.append("    protected void setUp() {" + EOL);
		buffer.append("         	super.setUp();" + EOL);
		buffer.append("         	setConfigKeyRelevantClasses(\"" + targetClassName + "\");" + EOL);
		buffer.append("    }		" + EOL);

	}

	private void generateBody(StringBuffer buffer) {
		int count = 0;
		for (Entry<String, Integer> entry : this.methodsCount.entrySet()) {
			count++;
			String methodName = entry.getKey();
			buffer.append("# Method: " + methodName + "\n");
			buffer.append("commands.append('java -Xms512m -Xmx1024m -jar dynJml2Alloy.jar -cf config/genericTest.properties -c slicing.koa.AuditLog -m " + methodName + "_0 -b 8 -w 4 -u 3 ' + \n");
			buffer.append("	               '-d slicing.koa.AuditLog, slicing.koa.KiesKring, slicing.koa.KiesLijst, slicing.koa.District, slicing.koa.Candidate, slicing.koa.CandidateList, slicing.koa.CandidateListMetadata ' +\n");
			buffer.append("	               '-rd slicing.koa.AuditLog, slicing.koa.KiesKring, slicing.koa.KiesLijst, slicing.koa.District, slicing.koa.Candidate, slicing.koa.CandidateList, slicing.koa.CandidateListMetadata')");
			buffer.append("\n");
		}
		buffer.append("Total: " + count);
                
                
		
//		for (Entry<String, Integer> entry : this.methodsCount.entrySet()) {
//			for (int i = 0; i < entry.getValue(); i++) {
//				boolean hasCounterExample;
//				if (this.methodsNameWithCounterexampleSet.contains(entry.getKey())) {
//					hasCounterExample = true;
//				} else {
//					hasCounterExample = false;
//				}
//				String methodName = entry.getKey() + "_" + i;
//				generateMethodSupport(buffer, methodName, hasCounterExample);
//			}
//		}
	}
	
	@SuppressWarnings("unused")
	private void generateMethodSupport(StringBuffer buffer, String methodName, boolean hasCounterExample) {
		buffer.append(EOL);
		buffer.append("    public void testRunAndCheck_" + methodName + "() throws VizException {" + EOL);
		buffer.append("        runAndCheck(GENERIC_PROPERTIES,\"" + methodName + "\", " + hasCounterExample + ");" + EOL);
		buffer.append("    }" + EOL);

	}

	@SuppressWarnings("unused")
	private void generateFooter(StringBuffer buffer) {
		buffer.append("}" + EOL);
	}

	private Class<?> retrieveClazz() throws ClassNotFoundException {
		Class<?> clazz = Class.forName(targetClassName);
		return clazz;
	}

	private void collectMethods(Class<?> targetClazz) {
		this.methodsCount = new HashMap<String, Integer>();
		for (Method method : targetClazz.getMethods()) {
			// avoid inherited methods
			if (method.getDeclaringClass().equals(targetClazz)) {
				increaseMethodCount(method.getName());
			}
		}

		for (@SuppressWarnings({ "unused", "rawtypes" }) Constructor constructor : targetClazz.getConstructors()) {
			increaseMethodCount("Constructor");
		}
	}

	private void increaseMethodCount(String mehodName) {
		String name = mehodName;
		if (this.methodsCount.containsKey(name)) {
			int previousValue = this.methodsCount.get(name);
			this.methodsCount.put(name, previousValue + 1);
		} else {
			this.methodsCount.put(name, 1);
		}
	}

	private String simpleClassName(String s) {
		int p = s.lastIndexOf(".");
		if (p < 0) {
			return s;
		}

		return s.substring(p + 1);

	}
}

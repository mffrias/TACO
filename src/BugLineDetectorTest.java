package icse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.junit.Before;

import edu.mit.csail.sdg.alloy4.Pair;
import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class BugLineDetectorTest extends CollectionTestBase {

	private static String[] relevantClasses;
	private static int[] relevantClassesAmounts;
	private static String classToCheck;
	private static String classToCheckPath;
	private static String methodToCheck;
	private static String bugLineMarkerPackage;

	@Override
	protected void setUp() {
		Properties properties = new Properties();
		try {
		  properties.load(new FileInputStream(System.getProperty("user.dir") + "/mystique.properties"));
		} catch (IOException e) {
		  e.printStackTrace();
		  return;
		}
		relevantClasses = properties.getProperty("relevantClasses").split(",");
		String[] relevantClassesAmountsStrings = properties.getProperty("relevantClassesAmounts").split(",");
		relevantClassesAmounts = new int[relevantClassesAmountsStrings.length];
		for(int i = 0; i < relevantClassesAmountsStrings.length; i++) {
			String amount = relevantClassesAmountsStrings[i];
			relevantClassesAmounts[i] = Integer.valueOf(amount);
		}
		classToCheck = properties.getProperty("classToCheck");
		classToCheckPath = properties.getProperty("classToCheckPath");
		methodToCheck = properties.getProperty("methodToCheck");
		bugLineMarkerPackage = properties.getProperty("bugLineMarkerPackage");

		super.setUp();
	}
	
	@Override
	protected String getClassToCheck() {
		return classToCheck;
	}

	public void test_contains() throws VizException {		
		StringBuffer relevantClassesStr = new StringBuffer();
		for (int i = 0; i < relevantClasses.length; i++) {
			relevantClassesStr.append(relevantClasses[i]);
			if (i != relevantClasses.length - 1) 
				relevantClassesStr.append(",");
		}
		setConfigKeyRelevantClasses(relevantClassesStr.toString());
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(false);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyObjectScope(6);
		setConfigKeyInferScope(false);

		relevantClassesStr = new StringBuffer();
		for (int i = 0; i < relevantClasses.length; i++) {
			relevantClassesStr.append(relevantClasses[i]).append(":").append(relevantClassesAmounts[i]);
			if (i != relevantClasses.length - 1) 
				relevantClassesStr.append(",");
		}
		setConfigKeyTypeScopes(relevantClassesStr.toString());
		// setConfigKeyTypeScopes("examples.singlylist.SinglyLinkedList:1,examples.singlylist.SinglyLinkedListNode:7");

		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);

		Properties newOverProp = getProperties();
		newOverProp.put("generateCheck", "true");
		newOverProp.put("generateRun", "false");
		newOverProp.put("include_simulation_program_declaration", "true");

		BugLineDetector main = new BugLineDetector(GENERIC_PROPERTIES,
				newOverProp, classToCheck, methodToCheck);
		
		System.out.println("Entrando al run...");

		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4); //santi para stryker 12/04/2015
		setConfigKeyGenerateUnitTestCase(true);
//		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		
		try {
			FileWriter writer = new FileWriter(new File("bugline.fajita.config"));
			writer.write("RELEVANT_CLASSES=");
			for (int i = 0; i < relevantClasses.length; i++) {
				writer.write(relevantClasses[i]);
				if (i != relevantClasses.length - 1) 
					writer.write(",");
			}
			writer.write("\n\nCLASS_TO_CHECK=" + classToCheck);
			writer.write("\n\nMETHOD_TO_CHECK=" + methodToCheck);
			writer.write("\n\nLOOP_UNROLL=4");
			writer.write("\n\nINT_BITWIDTH=4");
			writer.write("\n\nTIMEOUT_SECS=3600");

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		main.run(classToCheckPath, bugLineMarkerPackage);
		
		// main.run(System.getProperty("user.dir") +
		// "/tests/examples/singlylist/SinglyLinkedList.java");
		System.out.println("Salido del run.");

	}
}

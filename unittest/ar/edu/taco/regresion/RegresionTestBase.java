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
package ar.edu.taco.regresion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import mujava.api.Configuration;
import mujava.op.PRVO;
import mujava.op.basic.COR;

import org.apache.log4j.xml.DOMConfigurator;

import ar.edu.taco.TacoAnalysisResult;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoMain;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyJNILibraryPath;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class RegresionTestBase extends TestCase {

	public static boolean initializated = false;
	Properties overridingProperties = null;

	/**
	 * User can't set config key after call "checkAssertion" or "runAssertion"
	 */
	private boolean analizerIsCalled;

	protected void setUp() {
		if (!initializated) {
			initializated = true;

			File file = new File("config/log4j.xml");
			if (file.exists()) {
				DOMConfigurator.configure("config/log4j.xml");
			} else {
				System.err.println("File config/log4j.xml not found");
			}

			AlloyJNILibraryPath alloyJNILibraryPath = new AlloyJNILibraryPath();
			alloyJNILibraryPath.setupJNILibraryPath();
		}

		overridingProperties = new Properties();
		analizerIsCalled = false;
		setConfigKeyGenerateUnitTestCase(true);

	}

	// BEGIN ************************************ PUBLIC API ***************************************

	protected void runAndCheck(String configFile, String methodToCheck, boolean hasCounterExample) throws VizException {
		AlloyAnalysisResult runAnalysisResult = runAssertionSupport(configFile, methodToCheck);
		if (runAnalysisResult!=null) {
		  assertTrue("The method doesn't have instance ", runAnalysisResult.isSAT());
		}
		check(configFile, methodToCheck, hasCounterExample);
	}

	protected void check(String configFile, String methodToCheck, boolean hasCounterExample) throws VizException {
	    
        List<Pattern> bannedMethods = Arrays.asList(new Pattern[]{
        		Pattern.compile("[^#]*\\#extractMin"),
        		Pattern.compile("[^#]*\\#getClass"),
				Pattern.compile("[^#]*\\#toString"),
				Pattern.compile("[^#]*\\#toLowerCase"),
				Pattern.compile("[^#]*\\#intern"),
				Pattern.compile("[^#]*\\#toCharArray"),
				Pattern.compile("[^#]*\\#getBytes"),
				Pattern.compile("[^#]*\\#toUpperCase"),
				Pattern.compile("[^#]*\\#trim"),
				Pattern.compile("[^#]*\\#toLowerCase"),
				Pattern.compile("[^#]*\\#clone"),
				Pattern.compile("[^#]*\\#hash32"),
				Pattern.compile("[^#]*\\#serialPersistentFields"),
				Pattern.compile("[^#]*\\#serialVersionUID"),
				Pattern.compile("[^#]*\\#hash"),
				Pattern.compile("[^#]*\\#HASHING_SEED"),
				Pattern.compile("[^#]*\\#length"),
				Pattern.compile("[^#]*\\#isEmpty"),
        		Pattern.compile("[^#]*\\#serialPersistentFields"),
				Pattern.compile("[^#]*\\#CASE_INSENSITIVE_ORDER"),
        		Pattern.compile("[^#]*\\#hashCode")
        });
        
        Configuration.add(PRVO.PROHIBITED_METHODS, bannedMethods);
        Configuration.add(COR.ALLOW_BIT_AND, false);
        Configuration.add(COR.ALLOW_BIT_OR, false);
        Configuration.add(COR.ALLOW_LOGICAL_AND, false);
        Configuration.add(COR.ALLOW_LOGICAL_OR, false);
        Configuration.add(COR.ALLOW_XOR, false);
        Configuration.add(PRVO.ENABLE_SUPER, Boolean.FALSE); //Boolean.FALSE para desactivar el uso de super
        Configuration.add(PRVO.ENABLE_THIS, Boolean.TRUE);     //Boolean.FALSE para desactivar el uso de this
        Configuration.add(PRVO.ENABLE_LITERAL_EMPTY_STRING, Boolean.FALSE);
        Configuration.add(PRVO.ENABLE_ONE_BY_TWO_MUTANTS, Boolean.FALSE);
        Configuration.add(PRVO.ENABLE_PRIMITIVE_WRAPPING, Boolean.FALSE);
        Configuration.add(PRVO.ENABLE_PRIMITIVE_TO_OBJECT_ASSIGNMENTS, Boolean.FALSE);


	    
		AlloyAnalysisResult checkAnalysisResult = checkAssertionSupport(configFile, methodToCheck);
		if (checkAnalysisResult != null)
			assertEquals("The method should" + (hasCounterExample ? "" : "n't") + " have counterexample.", hasCounterExample, checkAnalysisResult.isSAT());
		else
			assertEquals("The source method does not compile.", Boolean.TRUE, Boolean.FALSE);
	}

	protected void notInstance(String configFile, String methodToCheck) throws VizException {
		AlloyAnalysisResult runAnalysisResult = runAssertionSupport(configFile, methodToCheck);
		assertTrue("The method shouldn't have instance ", runAnalysisResult.isUNSAT());
	}

	protected void simulate(String configFile, String methodToCheck) throws VizException {
		AlloyAnalysisResult runAnalysisResult = simulationAssertionSupport(configFile, methodToCheck);
		assertTrue("The method doesn't have simulation ", runAnalysisResult.isSAT());
	}

	// *** Config Key

	/**
	 * Si el parametro esta presente se le agrega "but ${bandwidth} int" al
	 * final del parametro assertionArguments Ademas se utiliza para manejar
	 * valores de enteros grandes
	 * 
	 * @param bitwidth
	 *            Selected bitwidth (i.e. if bitwidth==2 => INT={-1,0,1,2})
	 */
	protected void setConfigKeyIntBithwidth(int bitwidth) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("int.bitwidth", bitwidth + "");
	}

	protected void setConfigKeyUseMaxSequenceLength(boolean use_max_alloy_sequence_length) {
		this.overridingProperties.put("useMaxSequenceLength", use_max_alloy_sequence_length);
	}

	/**
	 * Similar al IntBitwidth pero para string
	 * 
	 * @param bitwidth
	 */
	protected void setConfigKeyStringBithwidth(int bitwidth) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("string.bitwidth", bitwidth + "");
	}

	/**
	 * Class to analize
	 * 
	 * @param classToCheck
	 */
	protected void setConfigKeyClassToCheck(String classToCheck) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("classToCheck", classToCheck);
	}

	/**
	 * Additional classes to analize
	 * 
	 */
	protected void setConfigKeyRelevantClasses(String relevantClasses) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("relevantClasses", relevantClasses);
	}

	protected void setConfigSkolemize(boolean skolemize) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("skolemizeInstanceInvariant", skolemize);
		this.overridingProperties.put("skolemizeInstanceAbstraction", skolemize);
	}

	protected void setConfigKeyObjectScope(int value) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("objectScope", value);
	}
	
	

	protected void setConfigKeyIncludeSimulationProgramDeclaration(boolean value) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("include_simulation_program_declaration", value);
	}

	protected void setConfigKeyModularReasoning(boolean value) {
		this.overridingProperties.put("modular_reasoning", value);
	}

	/**
	 * Additional classes to parse
	 * 
	 */
	protected void setConfigKeyClasses(String classes) {
		checkAnalizerIsCalled();
		this.overridingProperties.put("classes", classes);
	}

	protected void setConfigKeyRelevancyAnalysis(boolean value) {
		this.overridingProperties.put("relevancyAnalisys", value);
	}

	protected void setConfigKeyCheckNullDereference(boolean value) {
		this.overridingProperties.put("checkNullDereference", value);
	}


	protected void setConfigKeyCheckArithmeticException(boolean value){
		this.overridingProperties.put("checkArithmeticException", value);
	}


	protected void setConfigKeyLoopUnroll(int value) {
		this.overridingProperties.put("dynalloy.toAlloy.loopUnroll", value);
	}
	
	protected void setConfigKeyRemoveExitWhileGuard(boolean value) {
		this.overridingProperties.put("removeExitWhileGuard", value);
	}

	// ************************************ END API ****************************
	private AlloyAnalysisResult checkAssertionSupport(String configFile, String methodToCheck) throws VizException {
		this.overridingProperties.put("generateCheck", "true");
		this.overridingProperties.put("generateRun", "false");
		this.overridingProperties.put("include_simulation_program_declaration", "false");

		return checkAssertionSupport(configFile, methodToCheck, overridingProperties);
	}

	private AlloyAnalysisResult runAssertionSupport(String configFile, String methodToCheck) throws VizException {
		this.overridingProperties.put("generateCheck", "false");
		this.overridingProperties.put("generateRun", "true");
		this.overridingProperties.put("include_simulation_program_declaration", "false");
		return analyzerSupport(configFile, methodToCheck, overridingProperties);
	}

	private AlloyAnalysisResult simulationAssertionSupport(String configFile, String methodToCheck) throws VizException {
		this.overridingProperties.put("generateCheck", "false");
		this.overridingProperties.put("generateRun", "false");
		this.overridingProperties.put("include_simulation_program_declaration", "true");
		return analyzerSupport(configFile, methodToCheck, overridingProperties);
	}

	private void checkAnalizerIsCalled() {
		if (this.analizerIsCalled) {
			throw new TacoException("User can't set config key after call 'checkAssertion' or 'runAssertion'");
		}
	}

	private void putConfigurationKey(String configKey, String value) {
		this.overridingProperties.put(configKey, value);
	}

	private AlloyAnalysisResult checkAssertionSupport(String configFile, String methodToCheck, Properties overridingProperties) throws VizException {
		return analyzerSupport(configFile, methodToCheck, overridingProperties);
	}

	private AlloyAnalysisResult analyzerSupport(String configFile, String methodToCheck, Properties overridingProperties) throws VizException {
		analizerIsCalled = true;
		overridingProperties.put("methodToCheck", methodToCheck);
		TacoMain main = new TacoMain(null);
		TacoAnalysisResult analysis_result = main.run(configFile, overridingProperties);
		AlloyAnalysisResult analysisResult;
		if (analysis_result != null)
			analysisResult = analysis_result.get_alloy_analysis_result();
		else
			analysisResult = null;
			
		return analysisResult;
	}

	protected void setConfigKeyUseJavaArithmetic(boolean value) {
		this.overridingProperties.put("useJavaArithmetic", value);
	}

	protected void setConfigKeyUseJavaSBP(boolean value) {
		this.overridingProperties.put("useJavaSBP", value);
	}

	protected void setConfigKeyUseTightUpperBounds(boolean value) {
		this.overridingProperties.put("useTightUpperBounds", value);
	}

	protected void setConfigKeyRemoveQuantifiers(boolean value) {
		this.overridingProperties.put("dynalloy.toAlloy.removeQuantifiers", value);
	}

	protected void setConfigKeyDisableIntegerLiteralReduction(boolean value) {
		this.overridingProperties.put("disableIntegerLiteralReduction", value);
	}

	protected void setConfigKeySkolemizeInstanceInvariant(boolean value) {
		this.overridingProperties.put("skolemizeInstanceInvariant", value);
	}

	protected void setConfigKeySkolemizeInstanceAbstraction(boolean value) {
		this.overridingProperties.put("skolemizeInstanceAbstraction", value);
	}

	protected void setConfigKeyGenerateUnitTestCase(boolean value) {
		this.overridingProperties.put("generateUnitTestCase", value);
	}

	protected void setCondigKeyBuildJavaTrace(boolean value) {
		this.overridingProperties.put("buildJavaTrace", value);
	}
	
	protected void setConfigKeyTypeScopes(String type_scopes) {
		this.overridingProperties.put("type_scopes", type_scopes);
	}

	protected void setConfigKeyNumericTypeQuantificationRange(int lowerBound, int upperBound) {
		this.overridingProperties.put("numericRangeLower", lowerBound);
		this.overridingProperties.put("numericRangeUpper", upperBound);
	}
	
	protected void setConfigKeyAbstractSignatureObject(boolean b) {
		this.overridingProperties.put("abstractSignatureObject", b);
	}

	protected void setConfigKeyInferScope(boolean b) {
		this.overridingProperties.put("inferScope", b);
	}

	
	protected void setConfigKeyNoVerify(boolean b) {
		this.overridingProperties.put("noVerify", b);
	}
	
	protected void setConfigKeyNestedLoopUnroll(boolean b) {
		this.overridingProperties.put("nestedLoopUnroll", b);
	}

	/**
	 * If true, will attempt to correct the bug if a counterexample is
	 * found for the method being analyzed (using Stryker). 
	 */
	protected void setConfigKeyAttemptToCorrectBug(boolean value) {
		this.overridingProperties.put("attemptToCorrectBug", value);
	}

	protected void setConfigKeyMaxStrykerMethodsPerFile(int value) {
		this.overridingProperties.put("maxStrykerMethodForFile", value);
	}

	//PARALLEL TIMEOUT ------------------------------------------------------------------------------------
	protected void setConfigKeyParallelTOStep(int minTimeout, int maxTimeout, int stepTimeout, int numProcessorThreads){
		this.overridingProperties.put("parallelTOStep", stepTimeout + "");
		this.overridingProperties.put("parallelMinTO", minTimeout + "");
		this.overridingProperties.put("parallelMaxTO", maxTimeout + "");
		this.overridingProperties.put("parallelNumThreads", numProcessorThreads + "");

	}

	/**
	 * Methods added in order to make the translation work.
	 * 
	 */
	protected void setConfigKeyJMLObjectSequenceToAlloySequence(boolean value) {
		this.overridingProperties.put("JMLObjectSequenceToAlloySequence", value);
	}
	
	protected void setConfigKeyJMLObjectSetToAlloySet(boolean value) {
		this.overridingProperties.put("JMLObjectSetToAlloySet", value);
	}
}

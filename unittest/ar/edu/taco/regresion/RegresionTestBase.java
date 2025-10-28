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
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Pattern;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.stryker.api.impl.MuJavaController;
import ar.edu.taco.utils.FileUtils;
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

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

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
        if (runAnalysisResult != null) {
            assertTrue("The method doesn't have instance ", runAnalysisResult.isSAT());
        }
        check(configFile, methodToCheck, hasCounterExample);
    }

    protected void checkAndRunSpecIfFaulty(String configFile, String methodToCheck) throws VizException {
		this.overridingProperties.put("generateCheck", "true");
		this.overridingProperties.put("generateRun", "false");
		this.overridingProperties.put("include_simulation_program_declaration", "false");
		this.overridingProperties.put("hardcode_input_and_run_spec", "true");
        AlloyAnalysisResult checkAnalysisResult = analyzerSupportForRunningSpec(configFile, methodToCheck, this.overridingProperties);
    }


	public static String obtainClassNameFromFileName (String fileName){
		int lastBackslash = fileName.lastIndexOf("/");
		int lastDot = fileName.lastIndexOf(".");

		if (lastBackslash == TacoMain.NOT_PRESENT) {
			lastBackslash = 0;
		} else {
			lastBackslash += 1;
		}
		if (lastDot == TacoMain.NOT_PRESENT) {
			lastDot = fileName.length();
		}

		return fileName.substring(lastBackslash, lastDot);
	}


	public static String editTestFileToCompile (String junitFile, String sourceClassName, String
			classPackage, String methodName){
		String tmpDir = junitFile.substring(0, junitFile.lastIndexOf(TacoMain.FILE_SEP));
		tmpDir = tmpDir.replaceAll("generated", "output");
		File destFile = new File(tmpDir, obtainClassNameFromFileName(junitFile) + /*"_temp" +*/ ".java");
		String packageSentence = "package " + classPackage + ";\n";
		int posFirstOpeningParenthesis = methodName.lastIndexOf("(");
		methodName = methodName.substring(0, posFirstOpeningParenthesis);
		try {
			destFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(destFile);
			boolean packageAlreadyWritten = false;
			Scanner scan = new Scanner(new File(junitFile));
			scan.useDelimiter("\n");
			boolean nextToTest = false;
			String str = null;
			boolean reachedSecondConstructorFromAnalyzedClass = false;
			boolean translatingGetInstance = true;
			while (scan.hasNext()) {
				str = scan.next();
				if (nextToTest) {
					str = str.replace("()", "(String fileClasspath, String className, String methodName) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException, MalformedURLException");
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					nextToTest = false;
					//				} else if (str.contains("public class")){
					//					int posOpeningBrace = str.indexOf("{");
					//					str = str.substring(0, posOpeningBrace-1);
					//					str = str + "_temp {";
					//					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.contains("package") && !packageAlreadyWritten) {
					fos.write(packageSentence.getBytes(Charset.forName("UTF-8")));
					str = "           import java.util.Arrays;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           import java.net.URL;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           import java.net.URLClassLoader;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           import java.net.MalformedURLException;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           import java.io.File;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           import java.lang.reflect.InvocationTargetException;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					packageAlreadyWritten = true;
				} else if (str.contains("import") && !packageAlreadyWritten) {
					fos.write(packageSentence.getBytes(Charset.forName("UTF-8")));
					fos.write((scan.next() + "\n").getBytes(Charset.forName("UTF-8")));
					packageAlreadyWritten = true;
				} else if ((str.contains("new " + sourceClassName + "(") && !reachedSecondConstructorFromAnalyzedClass
						&& !translatingGetInstance) || (str.contains(sourceClassName + " instance = null;") && !translatingGetInstance)) {
					//		          str = "        try {";
					//		          fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           String[] classpaths = fileClasspath.split(System.getProperty(\"path.separator\"));";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           URL[] urls = new URL[classpaths.length];";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           for (int i = 0 ; i < classpaths.length ; ++i) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "              urls[i] = new File(classpaths[i]).toURI().toURL();";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           }";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           ClassLoader cl2 = new URLClassLoader(urls);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					//		          str = "           ClassLoaderTools.addFile(fileClasspath);";
					//		          fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           Class<?> clazz = cl2.loadClass(className);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           Constructor<?>[] c = clazz.getDeclaredConstructors();";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           Object instance = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	Class<?>[] parameterTypes = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	Object[] paramValues = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	Constructor<?> co = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           if (c.length > 0) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	co = c[0];";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	co.setAccessible(true);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	parameterTypes = co.getParameterTypes();";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	paramValues = new Object[co.getParameterTypes().length];";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "             for (int paramIndexer = 0; paramIndexer<parameterTypes.length; paramIndexer++){";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           		if (parameterTypes[paramIndexer].isPrimitive()){";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           			String typeSimpleName = parameterTypes[paramIndexer].getSimpleName();";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           			if (typeSimpleName.equals(\"boolean\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                         paramValues[paramIndexer] = false;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else if (typeSimpleName.endsWith(\"byte\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                       	paramValues[paramIndexer] = 0;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else if (typeSimpleName.endsWith(\"char\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                       	paramValues[paramIndexer] = 0;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else if (typeSimpleName.endsWith(\"double\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                       	paramValues[paramIndexer] = 0.0d;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else if (typeSimpleName.endsWith(\"float\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                       	paramValues[paramIndexer] = 0.0f;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else if (typeSimpleName.endsWith(\"int\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                       	paramValues[paramIndexer] = 0;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else if (typeSimpleName.endsWith(\"long\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                       	paramValues[paramIndexer] = 0L;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else if (typeSimpleName.endsWith(\"short\")) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                       	paramValues[paramIndexer] = 0;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     } else {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                         System.out.println(\"ERROR: Undefined primitive type.\");";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "                     }";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           		} else {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           			paramValues[paramIndexer] = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           		}";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	}";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	try {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "             	String dataCall = co.getName() + Arrays.toString(paramValues);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "             	System.out.println(dataCall);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	    instance = co.newInstance(paramValues);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	} catch (InstantiationException e) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           		e.printStackTrace();";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	}";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           } else {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	System.out.println(\"The class under analysis has no constructors, and at least one should exist.\");";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           }";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
//                    	str = "           Object instance = clazz.newInstance();";
//                    	fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					reachedSecondConstructorFromAnalyzedClass = true;
				} else if (str.contains("//endGetInstance")) {
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					translatingGetInstance = false;
					reachedSecondConstructorFromAnalyzedClass = false;
				} else if (str.contains("Class<?> clazz;")) {
				} else if (str.contains("new " + sourceClassName + "(") && !translatingGetInstance) {
					String backup = str;
					String objectName = backup.split("[ ]+")[2];
					str = "             Object " + objectName + " = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	try {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "             	String dataCall = co.getName() + Arrays.toString(paramValues);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "             	System.out.println(dataCall);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	   " + objectName + " = co.newInstance(paramValues);";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	} catch (InstantiationException e) {";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           		e.printStackTrace();";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           	}";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.contains("} catch (ClassNotFoundException e) {")) {
					str = str.replace("ClassNotFoundException", "Exception");
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.matches(".*(?i)[\\.a-z0-9\\_]*" + sourceClassName + "(?=[^a-z0-9\\_\\.]).*")) {
					str = str.replaceAll("(?i)[\\.a-z0-9\\_]*" + sourceClassName + "(?=[^a-z0-9\\_\\.])", /*classPackage+"."+*/sourceClassName);
					str = str.replace("\"" + methodName + "\"", "methodName");
					str = str.replace("\"" + sourceClassName + "\"", "clazz");
					//					str = str.replace("(", "(fileClasspath, ");
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.contains("e.printStackTrace();")) {
					//					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					fos.write(("           throw(new java.lang.RuntimeException(e));" + "\n").getBytes(Charset.forName("UTF-8")));
					//					fos.write(("throw e;" + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.contains("private Method getAccessibleMethod")) {
					str = str.replace("(String className, ", "(Class<?> clazz, ");
					//					str = str.replace(") {", ") throws MalformedURLException {");
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.contains("private Constructor<?> getAccessibleConstructor")) {
					str = str.replace("(String className, ", "(Class<?> clazz, ");
					//                  str = str.replace(") {", ") throws MalformedURLException {");
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.contains("method.invoke(instance,")) {
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           instance = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					str = "           method = null;";
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));

				} else if (str.contains("methodToCheck = clazz.getDeclaredMethod(methodName, parameterTypes);")) {
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else if (str.contains("clazz = Class.forName(className);")) {
					//					str = "           ClassLoader cl = ClassLoader.getSystemClassLoader();";
					//					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					//					str = "           final ClassLoader cl2 = new URLClassLoader(new URL[]{new File(fileClasspath).toURI().toURL()}, cl);";
					//					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					//					str = "           clazz = cl2.loadClass(className);";
					//					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
					//					str = "           System.out.println(\"actual class inside method: \"+clazz.getName());";
					//					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				} else {
					if (str.contains("@Test")) {
						nextToTest = true;
					}
					//					if (!scan.hasNext()){
					//						String s = "        } catch (ClassNotFoundException e){";
					//						fos.write((s + "\n").getBytes(Charset.forName("UTF-8")));
					//						s = "        } catch (InstantiationException e){}";
					//						fos.write((s + "\n").getBytes(Charset.forName("UTF-8")));
					//					}
					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
				}
			}
			fos.close();
			scan.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return destFile.toString();
	}

    private AlloyAnalysisResult analyzerSupportForRunningSpec(String configFile, String methodToCheck, Properties overridingProperties) {
        analizerIsCalled = true;
        overridingProperties.put("methodToCheck", methodToCheck);
        TacoMain main1 = new TacoMain(null);
        TacoAnalysisResult analysis_result = main1.run(configFile, overridingProperties);
        if (analysis_result != null) {
            String junitFile = main1.outputJunitFile;
            methodToCheck = overridingProperties.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);
            String sourceRootDir = TacoConfigurator.getInstance().getString(
                    TacoConfigurator.JMLPARSER_SOURCE_PATH_STR);

            try {
                String currentJunit = null;

                String tempFilename = junitFile.substring(0, junitFile.lastIndexOf(TacoMain.FILE_SEP) + 1) /*+ FILE_SEP*/;
                String packageToWrite = "ar.edu.output.junit";
                String fileClasspath = tempFilename.substring(0, tempFilename.lastIndexOf(new String("ar.edu.generated.junit").replaceAll("\\.", TacoMain.FILE_SEP)));
                fileClasspath = fileClasspath.replaceFirst("generated", "output");
                //					String currentClasspath = System.getProperty("java.class.path")+PATH_SEP+fileClasspath/*+PATH_SEP+System.getProperty("user.dir")+FILE_SEP+"generated"*/;
                String classToCheck = TacoConfigurator.getInstance().getString(TacoConfigurator.CLASS_TO_CHECK_FIELD);

                currentJunit = editTestFileToCompile(junitFile, classToCheck, packageToWrite, methodToCheck);

                File[] file1 = new File[]{new File(currentJunit)};
                JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
                StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
                Iterable<? extends JavaFileObject> compilationUnit1 =
                        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file1));
                javaCompiler.getTask(null, fileManager, null, null, null, compilationUnit1).call();
                fileManager.close();
                javaCompiler = null;
                file1 = null;
                fileManager = null;

                ///*mfrias*/		int compilationResult =	javaCompiler.run(null, null, null /*new NullOutputStream()*/, new String[]{"-classpath", currentClasspath, currentJunit});
                ///**/                javaCompiler = null;
                //					if(compilationResult == 0) {
                ClassLoader cl = ClassLoader.getSystemClassLoader();
                @SuppressWarnings("resource")
                ClassLoader cl2 = new URLClassLoader(new URL[]{new File(fileClasspath).toURI().toURL()}, cl);
                //						ClassLoaderTools.addFile(fileClasspath);
                Class<?> clazz = cl2.loadClass(packageToWrite + "." + obtainClassNameFromFileName(junitFile));
                //						Method[] meth = clazz.getMethods();
                //						log.info("preparing to add a class containing a test input to the pool... "+packageToWrite+"."+MuJavaController.obtainClassNameFromFileName(junitFile));
                //						Result result = null;
                //						final Object oToRun = clazz.newInstance();
                Object o1;
                try {
                    o1 = clazz.getConstructor((Class<?>[]) null).newInstance();
                    Field fi = clazz.getDeclaredField("theData");
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> o2 = (HashMap<String, Object>) fi.get(o1);

					TacoMain main2 = new TacoMain(o2);
					TacoMain.isCheckAndRunSpecAssertionSupport = true;
					TacoAnalysisResult analysis_result_2 = main2.run(configFile, overridingProperties);
					return analysis_result_2.get_alloy_analysis_result();

                } catch (IllegalArgumentException e) {
                    //							e.printStackTrace();
                } catch (Exception e) {
                    //							e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
			throw new TacoException("The TACO analysis of the provided code did not report any bugs.");
		}








//
//
//
//		analizerIsCalled = true;
//		overridingProperties.put("methodToCheck", methodToCheck);
//		TacoMain main =  getTacoMainWithFixedInput(checkAnalysisResult);
//		TacoAnalysisResult analysis_result = main.run(configFile, overridingProperties);
//		AlloyAnalysisResult analysisResult;
//		if (analysis_result != null)
//			analysisResult = analysis_result.get_alloy_analysis_result();
//		else
//			analysisResult = null;

        return null;// analysisResult;
    }


    private TacoMain getTacoMainWithFixedInput(AlloyAnalysisResult alloy_analysis_result) throws IllegalArgumentException {
        Class<?>[] inputs = StrykerStage.junitInputs;
        int index = 0;
        if (alloy_analysis_result != null) {
            String location = System.getProperty("user.dir") + System.getProperty("file.separator") +
                    "generated" + System.getProperty("file.separator");
            while (index < StrykerStage.indexToLastJUnitInput && inputs[index] != null &&
                    !((location + (inputs[index].getName())
                            .replace(".", System.getProperty("file.separator")))
                            .replace("output", "generated") + ".java").equals("junitFilename")) { // to be fixed
                index++;
            }
            if (index >= inputs.length || inputs[index] == null)
                throw new IllegalArgumentException("File name does not correspond to any stored input! Broken invariant!");
        } else {
            index = 0;
        }
        Class<?> claz = inputs[index];
        Object o1;
        try {
            o1 = claz.getConstructor((Class<?>[]) null).newInstance();
            Field fi = claz.getDeclaredField("theData");
            @SuppressWarnings("unchecked")
            HashMap<String, Object> o2 = (HashMap<String, Object>) fi.get(o1);
			return new TacoMain(o2);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            //TODO manage exceptions
            return null;
        }
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
        assertTrue("The method shouldtn't have instance ", runAnalysisResult.isUNSAT());
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
     * @param bitwidth Selected bitwidth (i.e. if bitwidth==2 => INT={-1,0,1,2})
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


    protected void setConfigKeyCheckArithmeticException(boolean value) {
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


    /**
     * Methods added in order to make the translation work.
     */
    protected void setConfigKeyJMLObjectSequenceToAlloySequence(boolean value) {
        this.overridingProperties.put("JMLObjectSequenceToAlloySequence", value);
    }

    protected void setConfigKeyJMLObjectSetToAlloySet(boolean value) {
        this.overridingProperties.put("JMLObjectSetToAlloySet", value);
    }


}
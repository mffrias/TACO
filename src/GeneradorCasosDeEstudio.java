import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import ar.edu.taco.utils.FileUtils;


public class GeneradorCasosDeEstudio {

    public static String outputPath = "/Users/zeminlu/Desktop/";
    public static String srcDir = "/Users/zeminlu/ITBA/Ph.D./comitaco/tests/";
    public static String testsDir = "/Users/zeminlu/ITBA/Ph.D./comitaco/unittest";
    public static String classpath = "/Users/zeminlu/Downloads/MutationsGenerator/lib/*:/Users/zeminlu/Downloads/MutationsGenerator/mutationsGenerator.jar:" + srcDir;

    public static void main(String[] args) {
        int fromBug = 1;
        int toBug = 6;
        int fromSet = 1;
        int toSet = 5;

//        String sllMethods[] = {"contains", "insertBack", "getNode"};
//        generateExperiments(fromBug, toBug, fromSet, toSet, "SinglyLinkedList", "roops.core.objects", "icse.singlylinkedlist", "SinglyLinkedListNode", sllMethods, 1, 3);

//        String btMethods[] = {"contains", "insert", "remove"};
//        generateExperiments(fromBug, toBug, fromSet, toSet, "BinTree", "pldi.bintree", "icse.bintree", "BinTreeNode", btMethods, 1, 3);
//
//        String bhMethods[] = {"extractMin", "findMinimum", "insert"};
//        generateExperiments(fromBug, toBug, fromSet, toSet, "BinomialHeap", "pldi.binomialheap", "icse.binomialheap", "BinomialHeapNode", bhMethods, 1, 7);

        String ncllMethods[] = {"addFirst", "contains", "remove"};
        generateExperiments(fromBug, toBug, fromSet, toSet, "NodeCachingLinkedList", "pldi.nodecachinglinkedlist", "icse.nodecachinglinkedlist", "LinkedListNode", ncllMethods, 1, 4);

    }

    @SuppressWarnings("resource")
    public static void generateExperiments(int fromBug, int toBug, int fromSet, int toSet, String className, 
            String classPackage, String newClassPackage, String nodeName, String[] methods, int classScope, int nodeScope) {
        String packageAsPath = "/" + classPackage.replace(".", "/") + "/";
        String newClassPackageAsPath = "/" + newClassPackage.replace(".", "/") + "/";

        try {
            Runtime r = Runtime.getRuntime();
            Process deleteProcess = r.exec("rm -rf " + outputPath + className);
            deleteProcess.waitFor();
            deleteProcess = r.exec("rm -rf " + outputPath + "ICSE/tests" + newClassPackageAsPath);
            deleteProcess.waitFor();
            deleteProcess = r.exec("rm -rf " + outputPath + "ICSE/unittest" + newClassPackageAsPath);
            deleteProcess.waitFor();


            for (int j = fromSet; j <= toSet; ++j) {
                for (String method: methods) {
                    for (int i = fromBug; i <= toBug; ++i) {
                        System.out.print("Generating case set" + j + "/" + className + "/" + method + "/" + i + "...");
                        String cmdarray[] = new String [] {
                                "java","-classpath", classpath,"main.Main", 
                                srcDir, classPackage + "." + className, method,
                                "" + i, "1", outputPath + className + "/set" + j + "/" + method + "/" + i + "Bug/"};
                        Process p = r.exec(cmdarray);
                        p.waitFor();
                        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = "";

                        String output = "";
                        while ((line = b.readLine()) != null) {
                            output += line + "\n";
                        }
                        String extensionlessOutputPath = outputPath + className + "/set" + j + "/" + method + "/" + i 
                                + "Bug" + packageAsPath + className + "/" + method + "/MULTI/0" + packageAsPath + className;
                        String parsedOutput = isValidMutant(output, i);
                        if (parsedOutput == null) {
                            System.out.println("INVALID, RETRYING...");
                            File toDelete = new File(extensionlessOutputPath + ".java");
                            toDelete.delete();
                            --i;
                            continue;
                        }

                        String tempFilename = extensionlessOutputPath + ".java";
                        String tempNodeFilename = System.getProperty("user.dir") + "/tests"+ newClassPackageAsPath + nodeName + ".java";
                        
                        String[] jml4cArgs = {
                                "-Xlint:all",
                                "-nowarn",
                                "-maxProblems", "9999999",
                                "-1.6",
                                tempFilename, tempNodeFilename
                        };


                        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int exitValue = compiler.run(null, null, baos, tempFilename, tempNodeFilename);
                        compiler = null;

                        if (exitValue != 0) {
                            String errors = new String(baos.toByteArray());
                            baos.flush();
                            //                    System.out.println("Los errores fueron:");
                            //                    System.out.println(errors);
                            String errorLines[] = errors.split("\n");

                            System.out.println("INVALID, RETRYING...");
                            File toDelete = new File(extensionlessOutputPath + ".java");
                            toDelete.delete();
                            --i;
                            continue;
                        }

                        System.out.println("VALID!!");

                        FileUtils.writeToFile(extensionlessOutputPath + ".txt", parsedOutput);
                        String filenameSufix = getFilenameSufix(parsedOutput);
                        String dirPath = outputPath + "ICSE/tests" + newClassPackageAsPath + "set" + j + "/";
                        File dir = new File(dirPath);
                        dir.mkdirs();
                        String capMethod = method.substring(0, 1).toUpperCase() + method.substring(1, method.length());
                        String newClassName = className + capMethod + i + "Bug" + filenameSufix;
                        String newFileName = dirPath + newClassName;
                        String newContent = FileUtils.readFile(extensionlessOutputPath + ".java");
                        newContent = newContent.replace(className + "(", className + capMethod + i + "Bug" + filenameSufix + "(");
                        newContent = newContent.replace(className + " ", className + capMethod + i + "Bug" + filenameSufix + " ");
                        newContent = newContent.replace("//mutGenLimit 0", "//mutGenLimit 2");
                        newContent = newContent.replace("//mutGenLimit 1", "//mutGenLimit 0");
                        newContent = newContent.replace("//mutGenLimit 2", "//mutGenLimit 1");
                        newContent = newContent.replace(classPackage + "." + className + ";", newClassPackage + ".set" + j + "." + className + ";");
                        newContent = newContent.replace(classPackage + "." + className + " ", newClassPackage + ".set" + j + "." + className + " ");
                        newContent = newContent.replace(classPackage + "." + nodeName, newClassPackage + "." + nodeName);
                        newContent = newContent.replace(classPackage + ";", newClassPackage + ".set" + j + ";");

                        FileUtils.writeToFile(newFileName + ".java", newContent);
                        r.exec("cp " + extensionlessOutputPath + ".txt " + newFileName + ".txt");

                        String baseFileContent = FileUtils.readFile(testsDir + newClassPackageAsPath + "Stryker" + className + "GenericTest.java");

                        dirPath = outputPath + "ICSE/unittest" + newClassPackageAsPath + "set" + j;
                        dir = new File(dirPath);
                        dir.mkdirs();

                        newContent = new String(baseFileContent);
                        newContent = newContent.replace(nodeName + ":", nodeName + ":" + nodeScope);
                        newContent = newContent.replace(className + "Generic:", newClassName + ":" + classScope);
                        newContent = newContent.replace(className + "Generic", newClassName);
                        newContent = newContent.replace("genericMethod", method);
                        newContent = newContent.replace(newClassPackage + "." + newClassName + ";", newClassPackage + ".set" + j + "." + newClassName + ";");
                        newContent = newContent.replace(classPackage + "." + newClassName + ":", newClassPackage + ".set" + j + "." + newClassName + ":");
                        newContent = newContent.replace(classPackage + "." + nodeName + ":", newClassPackage + "." + nodeName + ":");
                        newContent = newContent.replace(classPackage + "." + nodeName, newClassPackage + "." + nodeName);
                        newContent = newContent.replace(classPackage + "." + newClassName, newClassPackage + ".set" + j + "." + newClassName);
                        newContent = newContent.replace(newClassPackage + ";", newClassPackage + ".set" + j + ";");

                        String newTestFileName = dirPath + "/" + "Stryker" + newClassName + "Test.java";
                        FileUtils.writeToFile(newTestFileName, newContent);
                        
                        FileUtils.writeToFile(dirPath + "/" + newClassName + "BugLineDetector.properties", 
                                generateMystiqueProperties(newClassPackage + ".set" + j + "." + newClassName, classScope, 
                                        newClassPackage + "." + nodeName, nodeScope, method));
                        String newPropertiesFileName = "unittest" + newClassPackageAsPath + "set" + j + "/" + newClassName + "BugLineDetector.properties";
                        FileUtils.writeToFile(dirPath + "/" + newClassName + "BugLineDetectorTest.java", 
                                FileUtils.readFile(System.getProperty("user.dir") + "/src/BugLineDetectorTest.java")
                                .replace("mystique.properties", newPropertiesFileName).replace("package icse;", "package " + newClassPackage + ".set" + j + ";" )
                                .replace("BugLineDetectorTest", newClassName + "BugLineDetectorTest"));
                        
                        String cmdStrykerCommand = newClassPackage + ".set" + j + ".Stryker" + newClassName + "Test\n";

                        FileUtils.appendToFile(dirPath + "/SetCommands-" + i + "Bug.txt", cmdStrykerCommand);

                        FileUtils.appendToFile(outputPath + "ICSE/unittest" + newClassPackageAsPath + "/Commands-" + i + "Bug.txt", cmdStrykerCommand);
                    }
                }
            }
            deleteProcess = r.exec("rm -rf " + outputPath + className);
            deleteProcess.waitFor();
        }
        catch(Exception e1) {
            System.out.println(e1.getMessage());
        }
    }
    
    public static String generateMystiqueProperties(String testClassQN, int testClassScope, String nodeClassQN, int nodeClassScope, String method) {
        String mystique = "";
        
        mystique += "relevantClasses=" + testClassQN + "," + nodeClassQN +",icse.BugLineMarker\n";
        mystique += "relevantClassesAmounts=" + testClassScope + "," + nodeClassScope + ",1\n";
        mystique += "classToCheck=" + testClassQN + "\n";
        mystique += "classToCheckPath=" + testClassQN.replace(".", "/") + ".java\n";
        mystique += "methodToCheck=" + method + "\n";
        mystique += "bugLineMarkerPackage=icse\n";
        
        return mystique;
    }

    public static String isValidMutant(String mutantDesc, int linesAmount) {
        String lines[] = mutantDesc.split("\n");
        Set<Integer> affectedLines = new TreeSet<>();
        String parsedOutput = "";
        for (int i = 0; i < lines.length; ++i) {
            if (lines[i].contains("affected line")) {
                int indexOfAffectedLineNumber = lines[i].indexOf("affected line: ") + "affected line: ".length();
                int affectedLine = Integer.parseInt(lines[i].substring(indexOfAffectedLineNumber, lines[i].indexOf(" ", indexOfAffectedLineNumber)));
                if (!affectedLines.add(affectedLine)) {
                    return null;
                }
                parsedOutput += lines[i] + "\n";
            }
        }
        if (affectedLines.size() != linesAmount) {
            return null;
        }
        return parsedOutput;
    }

    public static String getFilenameSufix(String content) {
        String lines[] = content.split("\n");
        Map<Integer, String> affectedLines = new TreeMap<>();
        for (int i = 0; i < lines.length; ++i) {
            if (lines[i].contains("affected line")) {
                int indexOfAffectedLineNumber = lines[i].indexOf("affected line: ") + "affected line: ".length();
                int affectedLine = Integer.parseInt(lines[i].substring(indexOfAffectedLineNumber, lines[i].indexOf(" ", indexOfAffectedLineNumber)));
                if (affectedLines.containsKey(affectedLine)) {
                    return null;
                }
                affectedLines.put(affectedLine, (lines[i].contains("PRVOL") ? "I" : "D"));
            }
        }
        String filenameSufix = "";

        for(Entry<Integer, String> entry: affectedLines.entrySet()) {
            filenameSufix += entry.getKey() + entry.getValue() + "x";
        }

        return filenameSufix.substring(0, filenameSufix.length() - 1);
    }
}

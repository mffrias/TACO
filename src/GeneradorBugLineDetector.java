import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import ar.edu.taco.utils.FileUtils;


public class GeneradorBugLineDetector {

    public static String outputPath = "/Users/zeminlu/Desktop/";
    public static String srcDir = "/Users/zeminlu/ITBA/Ph.D./comitaco/tests/";
    public static String testsDir = "/Users/zeminlu/ITBA/Ph.D./comitaco/unittest";
    public static String classpath = "/Users/zeminlu/Downloads/MutationsGenerator/lib/*:/Users/zeminlu/Downloads/MutationsGenerator/mutationsGenerator.jar:" + srcDir;

    public static void main(String[] args) throws IOException {
        String bugLineDetectorTest = System.getProperty("user.dir")+"/src/BugLineDetectorTest.java";
        String testsFolder = System.getProperty("user.dir")+"/tests/icse";
        String unittestFolder = System.getProperty("user.dir") + "/unittest/icse";
        
        FilenameFilter dirFilter = new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir.getAbsolutePath() + "/" + name).isDirectory();
            }
        };
        
        FilenameFilter javaFilter = new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(".java") && !name.contains("_temp");
            }
        };
        
        File tests = new File(testsFolder);
        File unittest = new File(unittestFolder);
        
        File[] testStructs = tests.listFiles(dirFilter);
        for (int i = 0; i < testStructs.length; i++) {
            File curStruct = testStructs[i];
            File[] curStructSets = curStruct.listFiles(dirFilter);
            for (int j = 0; j < curStructSets.length; j++) {
                File curSet = curStructSets[j];
                File[] experiments = curSet.listFiles(javaFilter);
                for (int k = 0; k < experiments.length; k++) {
                    File curExp = experiments[k];
                    String testContent = FileUtils.readFile(curExp.getAbsolutePath());
                    String[] testContentLines = testContent.split("\n");
                    String packageLine = null;
                    for (int l = 0; l < testContentLines.length; l++) {
                        if (testContentLines[l].contains("package")) {
                            packageLine = testContentLines[l];
                            break;
                        }
                    }
                    String bugLineDetectorTestPackage = packageLine.substring(packageLine.indexOf("package") + 8, packageLine.indexOf(';'));
                    FileUtils.writeToFile(curExp.getAbsolutePath(), FileUtils.readFile(curExp.getAbsolutePath())
                            .replaceAll("import", "import ar.edu.taco.linedetector.BugLineDetector;\nimport"));
                }
            }
        }
        File[] unittestStructs = unittest.listFiles(dirFilter);
        for (int i = 0; i < unittestStructs.length; i++) {
            File curStruct = unittestStructs[i];
            File[] curStructSets = curStruct.listFiles(dirFilter);
            for (int j = 0; j < curStructSets.length; j++) {
                File curSet = curStructSets[j];
                File[] experiments = curSet.listFiles(javaFilter);
                for (int k = 0; k < experiments.length; k++) {
                    File curExp = experiments[k];
                    String curExpString = FileUtils.readFile(curExp.getAbsolutePath());
                    String[] curExpLines = curExpString.split("\n");
                    String curFQN = null;
                    String curNodeFQN = null;
                    String method = null;
                    int nodeScope = 0;
                    for (int l = 0; l < curExpLines.length; l++) {
                        if (curExpLines[l].contains("setConfigKeyTypeScopes")) {
                            String classes = curExpLines[l].substring(curExpLines[l].indexOf("\"") + 1, curExpLines[l].lastIndexOf("\""));
                            curFQN = classes.substring(0, classes.indexOf(":"));
                            curNodeFQN = classes.substring(classes.indexOf(',') + 1, classes.lastIndexOf(":"));
                            nodeScope = Integer.parseInt(classes.substring(classes.lastIndexOf(":") + 1));
                        } else if (curExpLines[l].contains("check(GENERIC_PROPERTIES")) {
                            method = curExpLines[l].substring(curExpLines[l].indexOf("\"") + 1, curExpLines[l].lastIndexOf("_"));
                        }
                    }
                    String mystiquePropertiesContent = generateMystiqueProperties(curFQN, 1, curNodeFQN, nodeScope, method);
                    String newClassName = curFQN.substring(curFQN.lastIndexOf('.') + 1);
                    FileUtils.writeToFile(curSet.getAbsolutePath() + "/" + newClassName + "BugLineDetectorTest.properties", mystiquePropertiesContent);
                    String newPropertiesFileName = "unittest/" + curFQN.replace('.', '/') + "BugLineDetectorTest.properties";
                    FileUtils.writeToFile(curSet.getAbsolutePath() + "/" + newClassName + "BugLineDetectorTest.java", FileUtils.readFile(bugLineDetectorTest)
                            .replace("mystique.properties", newPropertiesFileName)
                            .replace("package icse;", "package " + curFQN.substring(0, curFQN.lastIndexOf('.')) + ";" )
                            .replace("BugLineDetectorTest", newClassName + "BugLineDetectorTest"));
                    
                }
            }
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

}

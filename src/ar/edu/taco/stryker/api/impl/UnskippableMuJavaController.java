package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import mujava.OpenJavaException;
import mujava.api.MutationOperator;
import mujava.api.MutantsInformationHolder;
import mujava.api.Mutation;
import mujava.app.MutationRequest;
import mujava.app.Mutator;
import openjava.ptree.ParseTreeException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.stryker.api.impl.input.MuJavaFeedback;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInputWrapper;
import ar.edu.taco.utils.FileUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class UnskippableMuJavaController extends AbstractBaseController<MuJavaInput> {

    //	private static AtomicInteger compilationFailCount = new AtomicInteger(0);

    private static final String FILE_SEP = System.getProperty("file.separator");

    // If it is set to false then it will be assumed that if two hashes are
    // equal then that means that the two files are equal. Which of course
    // it is not necessarily true.

    private static UnskippableMuJavaController instance;

    public static List<MuJavaInput> inputsForMuJavaController = Lists.newArrayList();

    private static Logger log = Logger.getLogger(UnskippableMuJavaController.class);

    private static int baseI = 0;

    private static final int batchSize = 1000;

    private String classToMutate;

    private List<MuJavaInput> fathers = Lists.newArrayList();

    public synchronized static UnskippableMuJavaController getInstance() {
        if (instance == null) {
            instance = new UnskippableMuJavaController();
        }
        return instance;
    }

    private UnskippableMuJavaController() {

    }

    @Override
    protected Runnable getRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    MuJavaInput input = queue.take();

                    while (!willShutdown.get()) {
                        if (input == null) {
                            //tardo mucho asique asumo que ya tiene todos,los encolo
                            for (MuJavaInput mjcInput : inputsForMuJavaController) {
                                    MuJavaInput curInput = new MuJavaInput(mjcInput.getFilename(),
                                            mjcInput.getMethod(),
                                            mjcInput.getMutantsToApply(),
                                            null,
                                            mjcInput.getConfigurationFile(),
                                            mjcInput.getOverridingProperties(),
                                            mjcInput.getOriginalFilename(),
                                            mjcInput.getSyncObject(),
                                            mjcInput.getFullyQualifiedFileName(),
                                            mjcInput.getMethodUnderAnalysis());
                                        MuJavaController.getInstance().enqueueTask(curInput);
//                                Encolo esto a OJMLController sin busqueda de feedback, solo para validar que no son solucion ya.
                                if (mjcInput.getMuJavaFeedback() != null) {
                                    MuJavaInput father = fathers.get(mjcInput.getMuJavaFeedback().getFatherIndex());
                                    OpenJMLInput output = mjcInput.getUnskippableOJMLInput();
                                    MuJavaFeedback newFeedback = new MuJavaFeedback(
                                            StrykerJavaFileInstrumenter.parseMethodStartLine(
                                                    output.getFilename(), output.getMethod()),
                                                    mjcInput.getMuJavaFeedback().getLineMutationIndexes(), father.getMuJavaFeedback().getLineMutatorsList(), 
                                                    mjcInput.getMuJavaFeedback().getLastMutatedLines(), father.getMuJavaFeedback().getMutableLines(), father.getMuJavaFeedback().getCurMutableLines());
                                    newFeedback.setMut(father.getMuJavaFeedback().getMut());
                                    newFeedback.setMutantsInformationHolder(father.getMuJavaFeedback().getMutantsInformationHolder());
                                    newFeedback.setFatherIndex(mjcInput.getMuJavaFeedback().getFatherIndex());
                                    newFeedback.setStop(true);
                                    newFeedback.setUnskippableOJML4CFilename(mjcInput.getUnskippableOJML4CFilename());
                                    newFeedback.setUnskippableOJML4CPackage(mjcInput.getUnskippableOJML4CPackage());
                                    output.setFeedback(newFeedback);
                                    log.debug("Adding task to the OpenJMLController");
                                    OpenJMLController.getInstance().enqueueTask(output);
                                    StrykerStage.mutationsQueuedToOJMLC++;
                                }
                            }
                            input = queue.take();
                        }

                        //						                        if (input.getMuJavaFeedback() == null) {
                        if (input.getMuJavaFeedback() == null || input.getMuJavaFeedback().isFatherable()) {
                            fatherize(input, input.getMuJavaFeedback() == null);
                        } else if (!input.getMuJavaFeedback().isFatherable()) {
                            StrykerStage.prunedFathers++;
                        }

                        input = queue.poll(2000, TimeUnit.MILLISECONDS);
                    }
                } catch (InterruptedException e1) {
                    //e1.printStackTrace();
                }
            }
        };
    }

    public void fatherize(MuJavaInput input, boolean first) {
        File firstDir = null;
        File firstFile = null;
        if (first) {
            //            StrykerJavaFileInstrumenter.cleanMutGenLimit0(input);
            try {
                firstDir = MuJavaController.createWorkingDirectory();
                File old = new File(input.getFilename());
                firstFile = new File(firstDir.getAbsolutePath() + input.getFilename().substring(input.getFilename().lastIndexOf(FILE_SEP)));
                firstFile.createNewFile();
                Files.copy(old, firstFile);
            } catch (IOException e) {
                // Handle Exceptions
            }
        } else {
//                        StrykerJavaFileInstrumenter.decrementUnmutatedLimits(input);
        }

        HashSet<MutationOperator> mutOpsForBase = Sets.newHashSet();
        mutOpsForBase.add(MutationOperator.PRVOL_SMART);
        mutOpsForBase.add(MutationOperator.PRVOR_REFINED);
        mutOpsForBase.add(MutationOperator.PRVOU_REFINED);
        mutOpsForBase.add(MutationOperator.AODS);
        mutOpsForBase.add(MutationOperator.AODU);
        mutOpsForBase.add(MutationOperator.AOIS);
        mutOpsForBase.add(MutationOperator.AOIU);
        mutOpsForBase.add(MutationOperator.AORB);
        mutOpsForBase.add(MutationOperator.AORS);
        mutOpsForBase.add(MutationOperator.AORU);
        mutOpsForBase.add(MutationOperator.ASRS);
        mutOpsForBase.add(MutationOperator.COD);
        mutOpsForBase.add(MutationOperator.COI);
        mutOpsForBase.add(MutationOperator.COR);
        mutOpsForBase.add(MutationOperator.LOD);
        // mutOpsForBase.add(MutationOperator.LOI);
        mutOpsForBase.add(MutationOperator.LOR);
        mutOpsForBase.add(MutationOperator.ROR);
        mutOpsForBase.add(MutationOperator.SOR); 

        //        File baseSiblingFile;
        //        try {
        //            baseSiblingFile = File.createTempFile("base", null);
        //            System.out.println(baseSiblingFile.getAbsolutePath());
        //            FileUtils.writeToFile(muJavaInput.getFilename(), FileUtils.readFile(muJavaInput.getFilename()));
        File baseTempFile = null;
        String baseTempFilename = "";
        try {
            baseTempFile = File.createTempFile("base", null);
            baseTempFilename = baseTempFile.getParent() + "/" + baseI++ + "/" + input.getFilename().substring(input.getFilename().lastIndexOf("/") + 1);
            FileUtils.writeToFile(baseTempFilename, FileUtils.readFile(input.getFilename()));
            baseTempFile.delete();
        } catch (IOException e1) {
            // TODO: Define what to do!
            log.error("UNSKIPPABLE: EXCEPTION WHILE DUPLICATING BASE CASE");
        }

        if (first) {
            MuJavaInput baseOutput = new MuJavaInput(baseTempFilename, 
                    input.getMethod(), mutOpsForBase, null, input.getConfigurationFile(), 
                    input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                    input.getFullyQualifiedFileName(), input.getMethodUnderAnalysis());
            baseOutput.setMuJavaFeedback(null);
            log.debug("Adding task to the list");
            inputsForMuJavaController.add(baseOutput);
        }
        
        MuJavaInput inputAsFather = new MuJavaInput(first ? firstFile.getAbsolutePath() : input.getFilename(), 
                input.getMethod(), 
                input.getMutantsToApply(), input.getQtyOfGenerations(), input.getConfigurationFile(), 
                input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                input.getFullyQualifiedFileName(), input.getMethodUnderAnalysis());
        try {
            File fileToMutate;
            String methodToCheck;
            HashSet<MutationOperator> mutOps;
            MuJavaInput muJavaInput;

            fileToMutate = new File(input.getFilename());
            if (!fileToMutate.exists()) {
                throw new IllegalStateException("The file " + input.getFilename() + " doesn't exist. Can't continue.");
                //              return Lists.newArrayList();
            }
            methodToCheck = input.getMethod();
            mutOps = Sets.newHashSet();
            mutOps.add(MutationOperator.PRVOL_SMART); //solo de izquierda
            classToMutate = MuJavaController.obtainClassNameFromFileName(input.getFilename());
            muJavaInput = inputAsFather;

            File tmpDir = MuJavaController.createWorkingDirectory();

            log.debug("Generating mutants...");

            String[] methods1 = new String[] {methodToCheck};
            MutationOperator[] mutops1 = new MutationOperator[mutOps.size()];
            mutOps.toArray(mutops1);
            MutationRequest req1 = new MutationRequest(classToMutate, methods1, mutops1, 
                    fileToMutate.getParent() + FILE_SEP, tmpDir.getAbsolutePath() + FILE_SEP);
            Mutator mut = new Mutator(req1);

            long nanoPrev = System.currentTimeMillis();
            Map<String, MutantsInformationHolder> mutantsInformationHoldersMap = mut.obtainMutants();
            StrykerStage.muJavaMillis += System.currentTimeMillis() - nanoPrev;
            MutantsInformationHolder mutantsInformationHolder = null;
            for (Entry<String, MutantsInformationHolder> mutant : mutantsInformationHoldersMap.entrySet()) {
                if (mutant.getKey().equalsIgnoreCase(input.getMethod())) {
                    mutantsInformationHolder = mutant.getValue();
                }
            }
            List<Mutation> mutantIdentifiers = mutantsInformationHolder.getMutantsIdentifiers();
            //Me quedo solo con los mutant identifiers que afectan solo 1 linea en el metodo en cuestion.
            mutantIdentifiers = new LinkedList<Mutation>(Collections2.filter(mutantIdentifiers, new Predicate<Mutation>() {
                public boolean apply(Mutation arg0) {
                    return arg0.isOneLineInMethodOp() && !MuJavaController.isSkippeableLeftMutation(arg0);
                };
            }));

            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData = MuJavaController.getMutatorsList(mutantIdentifiers);
            Mutation[][] mutatorsList = mutatorsData.getLeft();
            if (mutatorsList.length == 0) {
                return; //No tiene más mutaciones posibles, es una hoja del arbol de mutaciones.
            }

            List<Integer> mutableLines, curMutableLines;
            List<Integer> invertedMutableLinesListForFirst = mutatorsData.getRight().getLeft();
            LinkedList<Integer> straightMutableLinesListForFirst = Lists.newLinkedList();
            for (Integer integer : invertedMutableLinesListForFirst) {
                straightMutableLinesListForFirst.addFirst(integer);
            }
            curMutableLines = Lists.newArrayList(straightMutableLinesListForFirst);
            if (first) {
                mutableLines = curMutableLines;
            } else {
                mutableLines = input.getMuJavaFeedback().getMutableLines();
            }

            Integer[] lineMutationIndexes = new Integer[mutatorsList.length];
            for (int i = 0; i < lineMutationIndexes.length; ++i) {
                lineMutationIndexes[i] = 0;
            }//inicializar todo en 0 si no lo hace

            //            if (first) {
            //                lineMutationIndexes[0] = 9;
            //                lineMutationIndexes[1] = 3;
            //                lineMutationIndexes[2] = 0;
            //                lineMutationIndexes[3] = 0;
            //            }


            MuJavaFeedback newFeedback = new MuJavaFeedback(
                    StrykerJavaFileInstrumenter.parseMethodStartLine(
                            muJavaInput.getFilename(), methodToCheck), 
                            lineMutationIndexes, mutatorsList, null, mutableLines, curMutableLines);
            newFeedback.setSkipUntilMutID(null);
            //            if (input.getMuJavaFeedback() != null) {
            //                newFeedback.setMutantsInformationHolder(input.getMuJavaFeedback().getMutantsInformationHolder());
            //                newFeedback.setMut(input.getMuJavaFeedback().getMut());
            //                //                newFeedback.setLastMutatedLines(input.getMuJavaFeedback().getLastMutatedLines());
            //            }

            newFeedback.setMut(mut);
            newFeedback.setMutantsInformationHolder(mutantsInformationHolder);
            muJavaInput.setMuJavaFeedback(newFeedback);
            muJavaInput.setMutatorsData(mutatorsData);


            fathers.add(muJavaInput);//se agrega el nuevo padre a la lista de padres

            boolean notFirst = false;
            for (Integer integer : lineMutationIndexes) {
                if (!integer.equals(0)) {
                    notFirst = true;
                }
            }
            
            OpenJMLInputWrapper wrapper = MuJavaController.getInstance().buildNextBatchSiblingsFile(muJavaInput, fathers.size() - 1, batchSize, notFirst ? MuJavaController.getInstance().getPreviousIndexes(lineMutationIndexes, mutatorsList) : lineMutationIndexes, false, false);

            MuJavaInput baseSibling = new MuJavaInput(muJavaInput.getFilename(), muJavaInput.getMethod(), 
                    muJavaInput.getMutantsToApply(), muJavaInput.getQtyOfGenerations(), muJavaInput.getConfigurationFile(), 
                    muJavaInput.getOverridingProperties(), muJavaInput.getOriginalFilename(), muJavaInput.getSyncObject(),
                    muJavaInput.getFullyQualifiedFileName(), muJavaInput.getMethodUnderAnalysis());

            MuJavaFeedback baseSiblingFeedback = new MuJavaFeedback(StrykerJavaFileInstrumenter.parseMethodStartLine(muJavaInput.getFilename(), methodToCheck),
                    lineMutationIndexes, muJavaInput.getMuJavaFeedback().getLineMutatorsList(), new ArrayList<Integer>(), 
                    muJavaInput.getMuJavaFeedback().getMutableLines(), muJavaInput.getMuJavaFeedback().getCurMutableLines());

            baseSiblingFeedback.setMut(mut);
            baseSiblingFeedback.setMutantsInformationHolder(mutantsInformationHolder);
            baseSiblingFeedback.setFatherIndex(fathers.size() - 1);
            baseSiblingFeedback.setMutateRight(true);
            baseSibling.setMuJavaFeedback(baseSiblingFeedback);

            if (wrapper == null) {
                log.warn("UNSKIPPABLE: Found father with no children, skipping.");
                return;
            }

            muJavaInput.setIndexesToMethod(wrapper.getIndexesToMethod());
            muJavaInput.setUncompilableChildrenMethodNames(wrapper.getUncompilableMethods());
            muJavaInput.setChildrenFilename(wrapper.getFilename());
            muJavaInput.setJml4cFilename(wrapper.getJml4cFilename());
            muJavaInput.setJml4cPackage(wrapper.getJml4cPackage());
            muJavaInput.setPresentIndexes(Sets.newHashSet(wrapper.getIndexesToMethod().keySet()));
            muJavaInput.setDuplicateMethodIndexes(wrapper.getDuplicateMethodIndexes());

            while ((baseSibling = queueNextRelevantSibling(baseSibling)) != null);

        } catch (ClassNotFoundException | OpenJavaException e) {
            e.printStackTrace();
            // Handle Exceptions
        } catch (ParseTreeException e) {
            e.printStackTrace();
            // Handle Exceptions
        }
    }


    public static final String PATH_SEP = System.getProperty("path.separator");

    public static final String MUTANTS_DEST_PACKAGE = "ar.edu.itba.stryker.mutants";

//    private static final String CLASSPATH = System.getProperty("java.class.path");
//
//    @SuppressWarnings("resource")
//    public OpenJMLInputWrapper buildSiblingsFile(MuJavaInput father, int fatherIndex) {
//        OpenJMLInputWrapper wrapper = null;
//        try {
//            Mutation[][] mutatorsList = father.getMuJavaFeedback().getLineMutatorsList();
//
//            Integer[] lineMutationIndexes = father.getMuJavaFeedback().getLineMutationIndexes();
//
//            File fileToMutate;
//            String methodToCheck;
//
//            fileToMutate = new File(father.getFilename());
//            if (!fileToMutate.exists()) {
//                throw new IllegalStateException("The file " + father.getFilename() + " doesn't exist. Can't continue.");
//            }
//            methodToCheck = father.getMethod();
//            classToMutate = MuJavaController.obtainClassNameFromFileName(father.getFilename());
//
//            //Encolo el hijo
//            Map<String, OpenJMLInput> indexesToInput = Maps.newTreeMap();
//            log.warn("UNSKIPPABLE: Generating children of father index " + fatherIndex + "...");
//
//            log.debug("Generating mutants...");
//
//            Mutator mut = father.getMuJavaFeedback().getMut();
//
//            long nanoPrev = System.currentTimeMillis();
//            MutantsInformationHolder mutantsInformationHolder = father.getMuJavaFeedback().getMutantsInformationHolder();
//
//            CompilationUnit backup = mutantsInformationHolder.getCompUnit();
//
//            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData = father.getMutatorsData();
//            mutatorsList = mutatorsData.getLeft();
//            if (mutatorsList.length == 0) {
//                //TODO revisar esto
//                return null;
//            }
//            while (true) {
//                ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingMutationsLists = 
//                        MuJavaController.calculateNextRelevantSonMutationsLists(lineMutationIndexes.clone(), mutatorsList, 0, 
//                                mutatorsData.getRight().getRight(), true, false, father.getMuJavaFeedback().getNonCompilableIndexes());
//
//                if (nextRelevantSiblingMutationsLists == null) {
//                    log.warn("UNSKIPPABLE: No more children from father index " + fatherIndex);
//                    break;
//                } else if (nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length) {
//                    log.error("UNSKIPPABLE: Error nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length");
//                } else if (nextRelevantSiblingMutationsLists.getLeft().size() == 0) {
//                    log.error("UNSKIPPABLE: Error nextRelevantSiblingMutationsLists.getLeft().size() == 0");
//                }
//
//                lineMutationIndexes = nextRelevantSiblingMutationsLists.getRight();
//
//                if (!Mutator.checkCompatibility(nextRelevantSiblingMutationsLists.getLeft())) {
//                    log.error("UNSKIPPABLE: Generated a mutant identifiers list where at least 2 of them affect the same line");
//                    throw new IllegalArgumentException();
//                }
//
//                List<Integer> mutatedLines = Lists.newArrayList();
//
//                for (Mutation identifier : nextRelevantSiblingMutationsLists.getLeft()) {
//                    Integer affectedLine = identifier.getAffectedLine();
//                    mutatedLines.add(affectedLine);
//                }
//
//                List <Mutation> curIdentifiers = mutantsInformationHolder.getMutantsIdentifiers();
//                curIdentifiers.clear();
//                curIdentifiers.addAll(nextRelevantSiblingMutationsLists.getLeft());
//                mutantsInformationHolder.setCompilationUnit((CompilationUnit) backup.makeRecursiveCopy_keepOriginalID());
//                List<MutantInfo> mil = mut.writeMutants(father.getMethod(), mutantsInformationHolder, true);
//                MutantInfo mutantInfo = mil.get(0);
//                mut.resetMutantFolders();
//
//                OpenJMLInput jmlInput =  MuJavaController.getInstance().mutateWithoutCompiling(mutantInfo, fileToMutate, father, 
//                        fatherIndex, lineMutationIndexes, 
//                        mutantsInformationHolder, mut, mutatedLines);
//                if (jmlInput != null) {
//                    String indexes = "[ ";
//                    for (Integer lineMutationIndex : lineMutationIndexes) {
//                        indexes += lineMutationIndex + " ";
//                    }
//                    indexes += "]";
//                    indexesToInput.put(indexes, jmlInput);
//                }
//            }
//
//            mutantsInformationHolder.setCompilationUnit(backup);
//
//            if (MuJavaController.getInstance().jmlInputs.isEmpty()) {
//                //                System.out.println("Vacio el jmlInputs");
//                return null;
//            }
//
//            log.warn("UNSKIPPABLE: Whole class generated. Total children: " + MuJavaController.getInstance().jmlInputs.size());
//            //            System.out.println("UNSKIPPABLE - Y en indexesToInput hay: " + indexesToInput.size());
//
//            wrapper = MuJavaController.getInstance().createJMLInputWrapper(MuJavaController.getInstance().jmlInputs, classToMutate);
//
//            String filename = wrapper.getFilename();
//            String tempFilename = filename.substring(0, filename.lastIndexOf(FILE_SEP)+1) + 
//                    MUTANTS_DEST_PACKAGE.replaceAll("\\.", FILE_SEP) + FILE_SEP;
//            String packageToWrite = filename.substring(filename.indexOf(FILE_SEP+"a")+1, 
//                    filename.lastIndexOf(FILE_SEP)+1).replaceAll(FILE_SEP, ".")+MUTANTS_DEST_PACKAGE;
//            tempFilename = MuJavaController.adaptSiblingsFileToJML4C(filename, tempFilename, packageToWrite);
//
//            if (tempFilename == null) {
//                log.error("UNSKIPPABLE: Didn't adapt for JML4C!");
//            }
//
//            wrapper.setJml4cFilename(tempFilename);
//            wrapper.setJml4cPackage(packageToWrite);
//            //////////////////////////////////////////////////////////////////////////////////
//            String fileClasspath = tempFilename.substring(
//                    0, tempFilename.lastIndexOf(packageToWrite.replaceAll("\\.", FILE_SEP)));
//
//            String outputPath = wrapper.getFilename().substring(0, wrapper.getFilename().lastIndexOf(FILE_SEP) + 1);
//
//            String[] systemClassPathsToFilter = System.getProperty("java.class.path").split(PATH_SEP);
//
//            String filteredSystemClasspath = "";
//
//            for (int k = 0 ; k < systemClassPathsToFilter.length ; ++k) {
//                if (systemClassPathsToFilter[k].contains("org.eclipse.jdt.core") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.text") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.equinox.common") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.equinox.preferences") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.osgi") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.core.contenttype") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.core.jobs") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.core.resources") ||
//                        systemClassPathsToFilter[k].contains("org.eclipse.core.runtime")) {
//                    continue;
//                }
//                filteredSystemClasspath += systemClassPathsToFilter[k] + PATH_SEP;
//            }
//
//            String currentClasspath = System.getProperty("user.dir")+FILE_SEP+"lib/stryker/jml4c.jar"+
//                    PATH_SEP+fileClasspath+
//                    PATH_SEP+filteredSystemClasspath+
//                    PATH_SEP+System.getProperty("user.dir")+FILE_SEP+"generated";
//
//            String[] jml4cArgs = {
//                    //                    "-help",
//                    "-Xlint:all",
//                    "-nowarn",
//                    "-maxProblems", "9999999",
//                    "-cp", currentClasspath,
//                    //"-sourcepath", fileClasspath,
//                    //"-rac",
//                    //"-d", outputPath,
//                    //"-noInternalSpecs",
//                    //"-P",
//                    "-1.7", //Agregado para que funcione con otro classloader debido a que conflictua con JDT para instrumentacion del codigo
//                    tempFilename
//            };
//
//            log.debug("STRYKER: CLASSPATH = "+ currentClasspath);
//            log.debug("STRYKER: SOURCEPATH = "+ CLASSPATH);
//            log.debug("STRYKER: TEMPFILENAME = "+ tempFilename);
//            log.debug("STRYKER: FILENAME = "+ wrapper.getFilename());
//            log.debug("STRYKER: File Classpath = "+ fileClasspath);
//            log.debug("STRYKER: OUTPUT PATH = "+ outputPath);
//
//            ClassLoader cl2;
//            cl2 = new URLClassLoader(new URL[]{new File(
//                    System.getProperty("user.dir")+FILE_SEP+"lib/stryker/jml4c.jar").toURI().toURL()}, null);
//            Class<?> clazz = cl2.loadClass("org.jmlspecs.jml4.rac.Main");
//            Class<?> clazz2 = cl2.loadClass("org.eclipse.jdt.core.compiler.CompilationProgress");
//
//            log.warn("UNSKIPPABLE: Compiling current batch...");
//
//            Set<String> uncompilableMethods = Sets.newHashSet();
//            Set<String> uncompilableMethodIndexes = Sets.newHashSet();
//
//            while (true) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                nanoPrev = System.currentTimeMillis();
//                Object compiler = clazz.getConstructor(PrintWriter.class, PrintWriter.class, boolean.class, Map.class, clazz2)
//                        .newInstance(new PrintWriter(System.out), new PrintWriter(baos), 
//                                false/*systemExit*/, null/*options*/, null/*progress*/);
//                Method compile = clazz.getMethod("compile", String[].class, java.io.PrintWriter.class, java.io.PrintWriter.class, clazz2);
//                compile.setAccessible(true);
//                Object args[] = new Object[] {jml4cArgs, new PrintWriter(System.out), new PrintWriter(baos), null};
//                boolean exitValue = (boolean) compile.invoke(compiler, args);
//                StrykerStage.compilationMillis += System.currentTimeMillis() - nanoPrev;
//                compiler = null;
//
//                if (exitValue) {
//                    log.warn("UNSKIPPABLE: Compiled and the amount of non-compilable mutants was: " + uncompilableMethods.size());
//                    if (uncompilableMethods.size() > 0) {
//                        //                        System.out.println("Y son:");
//                        //                        for (String uncompilableMethod : uncompilableMethods) {
//                        //                            System.out.println(uncompilableMethod);
//                        //                        }
//                        StrykerStage.nonCompilableMutations += uncompilableMethods.size();
//                    }
//                    wrapper.setUncompilableMethods(uncompilableMethodIndexes);
//                    wrapper.setUncompilableMethods(uncompilableMethods);
//                    wrapper.setIndexesToMethod(indexesToInput);
//                    break;
//                } else {
//                    Map<String, Pair<Integer, Integer>> methodsLineNumbers = 
//                            StrykerJavaFileInstrumenter.parseMethodsLineNumbers(tempFilename, methodToCheck);
//
//                    log.warn("UNSKIPPABLE: Didn't Compile, identifying non-compilable mutants to remove.");
//                    log.debug("UNSKIPPABLE - La clase a mutar es: " + classToMutate);
//                    //buscar en el stderr las líneas que no compilan
//                    String errors = new String(baos.toByteArray());
//                    baos.flush();
//                    //                    System.out.println("Los errores fueron:");
//                    //                    System.out.println(errors);
//                    String errorLines[] = errors.split("\n");
//
//                    //Buscar en el mapa de lineas qué métodos son y agregarlos a la lista
//
//                    Set<String> curUncompilableMethods = Sets.newTreeSet();
//
//                    for (String string : errorLines) {
//                        if (string.contains(classToMutate) && string.contains("at line") && string.contains("ERROR")) {
//                            String errorLine = string.substring(string.indexOf("(at line"));
//                            int errorLineNumber = Integer.valueOf(errorLine.substring(9, errorLine.length() - 1)); //salteo el "(at line " y el  ")" del final
//                            for (Entry<String, Pair<Integer, Integer>> entry : methodsLineNumbers.entrySet()) {
//                                Pair<Integer, Integer> lineNumbers = entry.getValue();
//                                if (errorLineNumber >= lineNumbers.getLeft() && errorLineNumber <= lineNumbers.getRight()) {
//                                    curUncompilableMethods.add(entry.getKey());
//                                }
//                            }
//                        }
//                    }
//                    List<OpenJMLInput> toRemoveJMLInputs = Lists.newArrayList();
//                    List<String> toRemoveIndexes = Lists.newArrayList();
//
//                    for (Entry<String, OpenJMLInput> entry : indexesToInput.entrySet()) {
//                        if (curUncompilableMethods.contains(entry.getValue().getRacMethod())) {
//                            toRemoveIndexes.add(entry.getKey());
//                            toRemoveJMLInputs.add(entry.getValue());
//                        }
//                    }
//
//                    MuJavaController.getInstance().jmlInputs.removeAll(toRemoveJMLInputs);
//
//                    for (String index : toRemoveIndexes) {
//                        indexesToInput.remove(index);
//                        uncompilableMethodIndexes.add(index);
//                    }
//
//                    if (MuJavaController.getInstance().jmlInputs.isEmpty()) {
//                        log.warn("MJC: Found Batch full of non-compilable methods");
//                        log.warn("MJC: Compilation errors: \n" + errors);
//                        if (uncompilableMethods.size() > 0) {
//                            StrykerStage.nonCompilableMutations += uncompilableMethods.size();
//                        }
//                        return null;
//                    }
//                    //Eliminar metodos no compilables
//
//                    wrapper = MuJavaController.getInstance().createJMLInputWrapper(MuJavaController.getInstance().jmlInputs, classToMutate);
//
//                    filename = wrapper.getFilename();
//                    String prevFilename = tempFilename;
//                    tempFilename = filename.substring(0, filename.lastIndexOf(FILE_SEP)+1) + 
//                            MUTANTS_DEST_PACKAGE.replaceAll("\\.", FILE_SEP) + FILE_SEP;
//                    packageToWrite = filename.substring(filename.indexOf(FILE_SEP+"a")+1, 
//                            filename.lastIndexOf(FILE_SEP)+1).replaceAll(FILE_SEP, ".")+MUTANTS_DEST_PACKAGE;
//                    tempFilename = MuJavaController.adaptSiblingsFileToJML4C(filename, tempFilename, packageToWrite);
//
//                    if (tempFilename == null) {
//                        log.error("UNSKIPPABLE: Didn't adapt for JML4C");
//                    }
//
//                    wrapper.setJml4cFilename(tempFilename);
//                    wrapper.setJml4cPackage(packageToWrite);
//
//                    //////////////////////////////////////////////////////////////////////////////////
//                    String prevFileClasspath = fileClasspath;
//                    fileClasspath = tempFilename.substring(
//                            0, tempFilename.lastIndexOf(packageToWrite.replaceAll("\\.", FILE_SEP)));
//
//                    outputPath = wrapper.getFilename().substring(0, wrapper.getFilename().lastIndexOf(FILE_SEP) + 1);
//
//                    systemClassPathsToFilter = System.getProperty("java.class.path").split(PATH_SEP);
//
//                    filteredSystemClasspath = "";
//
//                    for (int k = 0 ; k < systemClassPathsToFilter.length ; ++k) {
//                        if (systemClassPathsToFilter[k].contains("org.eclipse.jdt.core") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.text") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.equinox.common") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.equinox.preferences") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.osgi") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.core.contenttype") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.core.jobs") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.core.resources") ||
//                                systemClassPathsToFilter[k].contains("org.eclipse.core.runtime")) {
//                            continue;
//                        }
//                        filteredSystemClasspath += systemClassPathsToFilter[k] + PATH_SEP;
//                    }
//
//                    currentClasspath = System.getProperty("user.dir")+FILE_SEP+"lib/stryker/jml4c.jar"+
//                            PATH_SEP+fileClasspath+
//                            PATH_SEP+filteredSystemClasspath+
//                            PATH_SEP+System.getProperty("user.dir")+FILE_SEP+"generated";
//
//                    for (int i = 0; i < jml4cArgs.length; ++i) {
//                        jml4cArgs[i] = jml4cArgs[i].replace(prevFilename, tempFilename);
//                        jml4cArgs[i] = jml4cArgs[i].replace(prevFileClasspath, fileClasspath);
//                    }
//
//                    cl2 = new URLClassLoader(new URL[]{new File(
//                            System.getProperty("user.dir")+FILE_SEP+"lib/stryker/jml4c.jar").toURI().toURL()}, null);
//                    clazz = cl2.loadClass("org.jmlspecs.jml4.rac.Main");
//                    clazz2 = cl2.loadClass("org.eclipse.jdt.core.compiler.CompilationProgress");
//
//                    log.warn("UNSKIPPABLE: Compiling filtered batch...");
//
//                    uncompilableMethods.addAll(curUncompilableMethods);
//                }
//            }
//            //            log.info("Creating output for OpenJMLController");
//            //            OpenJMLController.getInstance().enqueueTask(wrapper);
//            //            log.debug("Adding task to the OpenJMLController");
//
//            MuJavaController.getInstance().jmlInputs.clear();
//        } catch (MalformedURLException e) {
//            // TODO: Define what to do!
//        } catch (InstantiationException e) {
//            // TODO: Define what to do!
//        } catch (IllegalAccessException e) {
//            // TODO: Define what to do!
//        } catch (IllegalArgumentException e) {
//            // TODO: Define what to do!
//        } catch (InvocationTargetException e) {
//            // TODO: Define what to do!
//        } catch (NoSuchMethodException e) {
//            // TODO: Define what to do!
//        } catch (SecurityException e) {
//            // TODO: Define what to do!
//        } catch (ClassNotFoundException e) {
//            // Handle Exceptions
//        } catch (OpenJavaException e) {
//            // Handle Exceptions
//        } catch (ParseTreeException e) {
//            // Handle Exceptions
//        } catch (IOException e) {
//            // TODO: Define what to do!
//        }
//        return wrapper;
//    }

    public MuJavaInput queueNextRelevantSibling(MuJavaInput input) {
        MuJavaInput father = fathers.get(input.getMuJavaFeedback().getFatherIndex());
        Mutation[][] mutatorsList = father.getMuJavaFeedback().getLineMutatorsList();

        Integer[] lineMutationIndexes = input.getMuJavaFeedback().getLineMutationIndexes();

        classToMutate = MuJavaController.obtainClassNameFromFileName(father.getFilename());

        //Encolo el hijo
        MuJavaInput curInput = null;
        while (true) {
            log.debug("Generating mutants...");

            Pair<Mutation[][], Pair<List<Integer>, List<Pair<Integer, Integer>>>> mutatorsData = father.getMutatorsData();
            mutatorsList = mutatorsData.getLeft();
            if (mutatorsList.length == 0) {
                return null; //No tiene mas mutaciones posibles, es una hoja del arbol de mutaciones.
            }

            int feedbackIndex = 0;

            ImmutablePair<List<Mutation>, Integer[]> nextRelevantSiblingMutationsLists = 
                    MuJavaController.calculateNextRelevantSonMutationsLists(lineMutationIndexes.clone(), mutatorsList, 
                            feedbackIndex, mutatorsData.getRight().getRight(), input.getMuJavaFeedback().isMutateRight(), 
                            input.getMuJavaFeedback().isUNSAT(), father.getMuJavaFeedback().getNonCompilableIndexes());

            if (nextRelevantSiblingMutationsLists == null) {
                log.warn("UNSKIPPABLE: No more children for father index " + input.getMuJavaFeedback().getFatherIndex());
                return null;
            } else if (nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length) {
                log.error("UNSKIPPABLE: Error nextRelevantSiblingMutationsLists.getRight().length > mutatorsList.length");
            } else if (nextRelevantSiblingMutationsLists.getLeft().size() == 0) {
                log.error("UNSKIPPABLE: Error nextRelevantSiblingMutationsLists.getLeft().size() == 0");
            }

            lineMutationIndexes = nextRelevantSiblingMutationsLists.getRight();
            String indexes = "[ ";
            for (Integer index : lineMutationIndexes) {
                indexes += index + " ";
            }
            indexes += "]";

            log.warn("UNSKIPPABLE: About to generate case Father " + input.getMuJavaFeedback().getFatherIndex() + " - " + indexes);
            String identifiers = "[ ";
            for (Mutation identifier : nextRelevantSiblingMutationsLists.getLeft()) {
                identifiers += " " + identifier.toString();
            }
            identifiers += " ]";
            log.warn("UNSKIPPABLE: And it's mutant identifiers are: " + identifiers);

            Map<String, OpenJMLInput> indexesToMethod = father.getIndexesToMethod();
            Set<String> presentIndexes = father.getPresentIndexes();
            if (father.getDuplicateMethodIndexes().contains(indexes)) {
                continue;
            }
            if (!presentIndexes.contains(indexes) && !father.getUncompilableChildrenMethodNames().contains(indexes)) {
                //actualizar batch

                OpenJMLInputWrapper wrapper = MuJavaController.getInstance().buildNextBatchSiblingsFile(father, input.getMuJavaFeedback().getFatherIndex(), batchSize, MuJavaController.getInstance().getPreviousIndexes(lineMutationIndexes, mutatorsList), false, false);

                if (wrapper == null) {
                    log.warn("MJC: A father with no batches left");
                    if (!father.getOriginalFilename().equals(father.getFilename())) {
//                        new File(father.getFilename()).delete(); //El padre ya no sirve mas
                    }

                    return null;
                }

                father.setIndexesToMethod(wrapper.getIndexesToMethod());
                father.setUncompilableChildrenMethodNames(wrapper.getUncompilableMethods());
                father.setChildrenFilename(wrapper.getFilename());
                father.setJml4cFilename(wrapper.getJml4cFilename());
                father.setJml4cPackage(wrapper.getJml4cPackage());
                father.setPresentIndexes(Sets.newHashSet(wrapper.getIndexesToMethod().keySet()));
                father.setDuplicateMethodIndexes(wrapper.getDuplicateMethodIndexes());

                nextRelevantSiblingMutationsLists = wrapper.getNextRelevantSiblingsMutationsLists();
                lineMutationIndexes = nextRelevantSiblingMutationsLists.getRight();
                indexes = "[ ";
                for (Integer index : lineMutationIndexes) {
                    indexes += index + " ";
                }
                indexes += "]";
            }
            
            if (father.getUncompilableChildrenMethodNames().contains(indexes)) {
                log.warn("UNSKIPPABLE: Omitted mutation for non compiling");
                OpenJMLInput jmlInput = indexesToMethod.get(indexes);
                if (jmlInput == null) {
                    continue;
                }
                //TODO revisar estos argumentos
                MuJavaInput mujavainput = new MuJavaInput(jmlInput.getFilename(), input.getMethod(), 
                        input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), 
                        input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                        input.getFullyQualifiedFileName(), input.getMethodUnderAnalysis());
                MuJavaFeedback newFeedback = new MuJavaFeedback(
                        StrykerJavaFileInstrumenter.parseMethodStartLine(mujavainput.getFilename(), mujavainput.getMethod()),
                        lineMutationIndexes, father.getMuJavaFeedback().getLineMutatorsList(), 
                        jmlInput.getFeedback().getLastMutatedLines(), father.getMuJavaFeedback().getMutableLines(),
                        father.getMuJavaFeedback().getCurMutableLines());

                newFeedback.setMut(father.getMuJavaFeedback().getMut());
                newFeedback.setMutantsInformationHolder(father.getMuJavaFeedback().getMutantsInformationHolder());
                newFeedback.setFatherIndex(input.getMuJavaFeedback().getFatherIndex());

                newFeedback.setFatherable(false);
                newFeedback.setGetSibling(true);
                newFeedback.setSkipUntilMutID(null);
                mujavainput.setMuJavaFeedback(newFeedback);

                UnskippableMuJavaController.getInstance().enqueueTask(mujavainput);
                continue;
            }

            List<Integer> mutatedLines = Lists.newArrayList();

            for (Mutation identifier : nextRelevantSiblingMutationsLists.getLeft()) {
                Integer affectedLine = identifier.getAffectedLine();
                mutatedLines.add(affectedLine);
            }

            OpenJMLInput output = indexesToMethod.get(indexes);
            output.setFullyQualifiedClassName(input.getFullyQualifiedFileName());;
            output.setMethodUnderAnalysis(input.getMethodUnderAnalysis());

            if (output != null) {
                HashSet<MutationOperator> mutOpsForBase = Sets.newHashSet();
                mutOpsForBase.add(MutationOperator.PRVOL_SMART);
                mutOpsForBase.add(MutationOperator.PRVOR_REFINED);
                mutOpsForBase.add(MutationOperator.PRVOU_REFINED);
                mutOpsForBase.add(MutationOperator.AODS);
                mutOpsForBase.add(MutationOperator.AODU);
                mutOpsForBase.add(MutationOperator.AOIS);
                mutOpsForBase.add(MutationOperator.AOIU);
                mutOpsForBase.add(MutationOperator.AORB);
                mutOpsForBase.add(MutationOperator.AORS);
                mutOpsForBase.add(MutationOperator.AORU);
                mutOpsForBase.add(MutationOperator.ASRS);
                mutOpsForBase.add(MutationOperator.COD);
                mutOpsForBase.add(MutationOperator.COI);
                mutOpsForBase.add(MutationOperator.COR);
                mutOpsForBase.add(MutationOperator.LOD);
                // mutOpsForBase.add(MutationOperator.LOI);
                mutOpsForBase.add(MutationOperator.LOR);
                mutOpsForBase.add(MutationOperator.ROR);
                mutOpsForBase.add(MutationOperator.SOR);

                MuJavaFeedback newFeedback = new MuJavaFeedback(
                        StrykerJavaFileInstrumenter.parseMethodStartLine(output.getFilename(), output.getMethod()),
                        lineMutationIndexes, father.getMuJavaFeedback().getLineMutatorsList(), mutatedLines, 
                        father.getMuJavaFeedback().getMutableLines(), father.getMuJavaFeedback().getCurMutableLines());
                newFeedback.setMut(father.getMuJavaFeedback().getMut());
                newFeedback.setMutantsInformationHolder(father.getMuJavaFeedback().getMutantsInformationHolder());
                newFeedback.setFatherIndex(input.getMuJavaFeedback().getFatherIndex());
                newFeedback.setFatherable(true);
                newFeedback.setGetSibling(true);
                output.setFeedback(newFeedback);
                log.debug("Adding task to the OpenJMLController");
                curInput = new MuJavaInput(output.getFilename(),
                        output.getMethod(),
                        mutOpsForBase,
                        null,
                        output.getConfigurationFile(),
                        output.getOverridingProperties(),
                        output.getOriginalFilename(),
                        output.getSyncObject(),
                        output.getFullyQualifiedClassName(),
                        output.getMethodUnderAnalysis());
                curInput.setMuJavaFeedback(newFeedback);
                curInput.setUnskippableOJMLInput(output);
                curInput.setUnskippableOJML4CFilename(father.getJml4cFilename());
                curInput.setUnskippableOJML4CPackage(father.getJml4cPackage());

                inputsForMuJavaController.add(curInput);
                newFeedback = new MuJavaFeedback(
                        StrykerJavaFileInstrumenter.parseMethodStartLine(output.getFilename(), output.getMethod()),
                        lineMutationIndexes, father.getMuJavaFeedback().getLineMutatorsList(), mutatedLines, 
                        father.getMuJavaFeedback().getMutableLines(), father.getMuJavaFeedback().getCurMutableLines());
                newFeedback.setMut(father.getMuJavaFeedback().getMut());
                newFeedback.setMutantsInformationHolder(father.getMuJavaFeedback().getMutantsInformationHolder());
                newFeedback.setFatherIndex(input.getMuJavaFeedback().getFatherIndex());
                newFeedback.setFatherable(false);
                newFeedback.setGetSibling(true);

                curInput = new MuJavaInput(output.getFilename(),
                        output.getMethod(),
                        mutOpsForBase,
                        null,
                        output.getConfigurationFile(),
                        output.getOverridingProperties(),
                        output.getOriginalFilename(),
                        output.getSyncObject(),
                        output.getFullyQualifiedClassName(),
                        output.getMethodUnderAnalysis());
                curInput.setMuJavaFeedback(newFeedback);
                UnskippableMuJavaController.getInstance().enqueueTask(curInput);
                break;
            }
        }
        return curInput;
    }

    public List<MuJavaInput> getFathers() {
        return fathers;
    }

    @Override
    protected int getQtyOfThreads() {
        return 1;
    }
}

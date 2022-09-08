package ar.edu.taco.stryker.api.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.junit.Test;

import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.stryker.api.impl.input.MuJavaFeedback;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInput;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class OpenJMLController extends AbstractBaseController<OpenJMLInput> {

    public static final String FILE_SEP = System.getProperty("file.separator");

    public static final String PATH_SEP = System.getProperty("path.separator");

    public static final String MUTANTS_DEST_PACKAGE = "ar.edu.itba.stryker.mutants";

    private static Logger log = Logger.getLogger(OpenJMLController.class);

    private static int curJunitIndex = 0;

    //    private static final String CLASSPATH = System.getProperty("java.class.path");

    private static OpenJMLController instance;

    private static int consumedMutants = 0;

    public synchronized static OpenJMLController getInstance() {
        if (instance == null) {
            instance = new OpenJMLController();
        }
        return instance;
    }

    //	private JUnitCore junit;
    private ExecutorService executor = Executors.newSingleThreadExecutor(); //Executors.newFixedThreadPool(10);

    private Thread runningThread = null;

    private OpenJMLController() {
        //		try {
        //			ClassLoaderTools.addFile(System.getProperty("user.dir")+FILE_SEP+"generated"+FILE_SEP);
        //		} catch (IOException e) {
        //			e.printStackTrace();
        //		}
    }

    @Override
    protected Runnable getRunnable() {
        return new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try {
                    while (!willShutdown.get()) {
                        try {
                            //int j = 0;
                            log.debug("Taking new input from queue");
                            OpenJMLInput input = queue.take();
                            log.debug("Took from queue");
                            log.debug("Queue size: "+queue.size());
                            MuJavaInput father;
                            String tempFilename = null;
                            String packageToWrite = null;
                            
                            if (input.getFeedback().isStop()) {
                                father = UnskippableMuJavaController.getInstance().getFathers().get(input.getFeedback().getFatherIndex());
                                tempFilename = input.getFeedback().getUnskippableOJML4CFilename();
                                packageToWrite = input.getFeedback().getUnskippableOJML4CPackage();
                            } else {
                                father = MuJavaController.getInstance().getFathers().get(input.getFeedback().getFatherIndex());
                                tempFilename = father.getJml4cFilename();
                                packageToWrite = father.getJml4cPackage();
                            }
                            if(tempFilename == null) {
                                log.debug("filename was null");
                                shutdown();
                                break;
                            }
                            log.debug("Took: "+input);

                            final String fileClasspath = tempFilename.substring(
                                    0, tempFilename.lastIndexOf(packageToWrite.replaceAll("\\.", FILE_SEP)));
                            Boolean result = true;
                            final String qualifiedName = editFileToPassToNextStage(tempFilename);

                            String newFileClasspath = fileClasspath + PATH_SEP + System.getProperty("user.dir")+FILE_SEP+"lib/stryker/jml4c.jar";

                            //                            log.debug("compiled file with exit code = "+exitValue);
                            try {
                                log.info("preparing to run a test... "+packageToWrite+"."+MuJavaController.obtainClassNameFromFileName(tempFilename));

                                Class<?>[] junitInputs = StrykerStage.junitInputs;

                                Set<String> candidateMethods = Sets.newHashSet();
                                Map<String, String> failedMethods = Maps.newHashMap();
                                Set<String> nullPointerMethods = Sets.newHashSet();
                                Set<String> timeoutMethods = Sets.newHashSet();
                                Boolean threadTimeout = false;
                                String methodName = input.getRacMethod();

                                int maxNumberAttemptedInputs = Math.min(StrykerStage.indexToLastJUnitInput, 9);
                                log.debug("maxNumberAttemptedInputs: "+maxNumberAttemptedInputs);
                                boolean failed = false;

                                for (int attempted = 0; attempted < maxNumberAttemptedInputs && !failed; attempted++){
                                    Class<?> junitInputClass = junitInputs[curJunitIndex];
                                    Method[] methods = junitInputClass.getMethods();
                                    Method methodToRun = null;
                                    for(Method m : methods) {
                                        if(m.isAnnotationPresent(Test.class)) {
                                            methodToRun = m;
                                            break;
                                        }
                                    }
                                    final Method methodToRunInCallable = methodToRun; 
                                    methodToRunInCallable.setAccessible(true);
                                    final Object oToRun =  junitInputClass.newInstance();
                                    final Object[] inputToInvoke = new Object[]{newFileClasspath, qualifiedName, methodName};
                                    Callable<Boolean> task = new Callable<Boolean>() {
                                        public Boolean call() throws InvocationTargetException {
                                            Boolean result = false;
                                            try {
                                                runningThread = Thread.currentThread();
                                                long timeprev = System.currentTimeMillis();
                                                methodToRunInCallable.invoke(oToRun, inputToInvoke);
                                                long timepost = System.currentTimeMillis();
                                                result = true;
                                                log.debug("time taken: "+(timepost - timeprev));
                                            } catch (IllegalAccessException e) {
                                                log.debug("Entered IllegalAccessException");
                                                //                                                e.printStackTrace();
                                            } catch (IllegalArgumentException e) {
                                                log.debug("Entered IllegalArgumentException");
                                                //                                                e.printStackTrace();
                                            } catch (InvocationTargetException e) {
                                                //                                                e.printStackTrace();
                                                log.debug("Entered InvocationTargetException");
                                                log.debug("QUIT BECAUSE OF JML RAC");
                                                String retValue = null;
                                                StringWriter sw = null;
                                                PrintWriter pw = null;
                                                try {
                                                    sw = new StringWriter();
                                                    pw = new PrintWriter(sw);
                                                    e.printStackTrace(pw);
                                                    retValue = sw.toString();
                                                    //                                                        System.out.println(retValue);
                                                    //                                                        System.out.println("------------------------------------------------------------------------------------------------");
                                                } finally {
                                                    try {
                                                        if(pw != null)  pw.close();
                                                        if(sw != null)  sw.close();
                                                    } catch (IOException ignore) {}
                                                }
                                                if (retValue.contains("org.jmlspecs.jml4.rac.runtime.JML") && retValue.contains("Error")) {
                                                    //                                                    System.out.println(retValue);
                                                    //                                                    System.out.println("Fallo RAC!!");
                                                    result = false;
                                                    //                                                } else if (retValue.contains("JMLExitExceptionalPostconditionError")) { 
                                                    //                                                    result = null;
                                                    //                                                } else if (retValue.contains("JMLInvariantError")) {
                                                    //                                                    result = false;
                                                } else if (retValue.contains("NullPointerException")) {
                                                    //                                                    System.out.println("NULL POINTER EXCEPTION EN RAC!!!!!!!!!!!!");
                                                    result = null;
                                                } else if (retValue.contains("ThreadDeath")) {
                                                    //                                                    System.out.println("THREAD DEATH EN RAC!!!!!!!!!!!!!!!!");
                                                    result = null;
                                                    result = null;
                                                } else {
                                                    log.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                                                            "\nFAILED METHODDDD FOR NO REASON!!!!!!!!!!!!!!!!!!!!" +
                                                            "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                                    e.printStackTrace();
                                                    result = null;
                                                }
                                            } catch (Throwable e) {
                                                log.debug("Entered throwable");
                                                //                                                System.out.println("THROWABLEEE!!!!!!!!!!!!!!!!!!!!!!");
                                                e.printStackTrace();
                                                return false;
                                            }
                                            return result;
                                        }
                                    };
                                    threadTimeout = false;
                                    Long nanoPrev = System.currentTimeMillis();
                                    Future<Boolean> future = executor.submit(task);
                                    try {
                                        result = future.get(3000, TimeUnit.MILLISECONDS);
                                    } catch (TimeoutException ex) {
                                        result = true;
                                        threadTimeout = true;
                                        runningThread.stop();
                                        executor.shutdownNow();
                                        executor = Executors.newSingleThreadExecutor();
                                        // handle the timeout
                                    } catch (InterruptedException e) {
                                        log.debug("Interrupted");
                                        // handle the interrupts
                                    } catch (ExecutionException e) {
                                        // handle other exceptions
                                        log.debug("Excecution Exception");
                                    } catch (Throwable e) {
                                        log.debug("Exception");
                                        // handle other exceptions
                                    } finally {
                                        future.cancel(true); // may or may not desire this	
                                    }
                                    StrykerStage.racMillis += (System.currentTimeMillis() - nanoPrev);
                                    log.info("test ran");
                                    if (result == null) {
                                        log.warn("TEST FAILED BECAUSE OF AN EXCEPTION IN MUTATED METHOD: :( for file: " + 
                                                tempFilename + ", method: "+methodName + ", input: " + curJunitIndex);
                                        failed = true;
                                        nullPointerMethods.add(methodName);
                                        String junitfile = StrykerStage.junitFiles[curJunitIndex];
                                        failedMethods.put(methodName, junitfile);
                                    } else if (!result) {
                                        log.warn("TEST FAILED: :( for file: " + tempFilename + ", method: "+methodName + ", input: " + curJunitIndex);
                                        String junitfile = StrykerStage.junitFiles[curJunitIndex];
                                        failedMethods.put(methodName, junitfile);
                                        failed = true;
                                    } else {
                                        if (threadTimeout) {
                                            timeoutMethods.add(methodName);
                                            log.warn("Timeout Mutation " + input.getFilename() + " for method: " + timeoutMethods.iterator().next());
                                        }
                                        if (attempted + 1 == maxNumberAttemptedInputs) {
                                            log.warn("TEST PASSED: :) for file: " + tempFilename + ", method: "+methodName + ", input: " + curJunitIndex);
                                            DarwinistInput output = null;
                                            output = new DarwinistInput(input.getFilename(), 
                                                    input.getOriginalFilename(), input.getConfigurationFile(), 
                                                    input.getMethod(), input.getOverridingProperties(), qualifiedName, 
                                                    inputToInvoke, false, null, null, null, null, null, 
                                                    input.getFeedback(), input.getMutantsToApply(), input.getSyncObject());
                                            output.setRacMethod(input.getRacMethod());
                                            DarwinistController.getInstance().enqueueTask(output);
                                            StrykerStage.candidatesQueuedToDarwinist++;
                                            candidateMethods.add(methodName);
                                            log.debug("Enqueded task to Darwinist Controller");
                                        } else {
                                            log.debug("TEST CANDIDATE TO PASS :), for file: " + tempFilename 
                                                    + ", method: "+methodName + ", input: " + curJunitIndex);
                                            log.debug("The class to be used in OpenJMLController is: "
                                                    +junitInputClass.getName());
                                        }
                                        curJunitIndex++;
                                        if (curJunitIndex == junitInputs.length || junitInputs[curJunitIndex] == null){
                                            curJunitIndex = 0;
                                        }
                                    }
                                }	

                                consumedMutants++;

                                log.warn("Mutants consumed by RAC: "+ consumedMutants);

                                if (!nullPointerMethods.isEmpty()) {
                                    log.warn("Null Pointer Mutation: " + input.getFilename() + " for method: " + nullPointerMethods.iterator().next());
                                } else if (!candidateMethods.isEmpty()) {
                                    log.warn("Candidate Mutation: " + input.getFilename() + " for method: " + candidateMethods.iterator().next());
                                } else {
                                    log.warn("Postcondition Failed Mutation: " + input.getFilename() + " for method: " + failedMethods.keySet().iterator().next());
                                }

                                if (!input.getFeedback().isStop()) {

                                    if (failed) {
                                        if (!input.getFeedback().isStop()) {
                                            if (MuJavaController.feedbackOn) {
                                                final Properties props = new Properties();
                                                Properties oldProps = input.getOverridingProperties();
                                                for(Entry<Object,Object> o : oldProps.entrySet()){
                                                    if(o.getKey().equals("attemptToCorrectBug")) {
                                                        props.put(o.getKey(), "false");
                                                    } else if (o.getKey().equals("generateUnitTestCase")) {
                                                        props.put(o.getKey(), "false");
                                                    } else if (o.getKey().equals("generateCheck")) {
                                                        props.put(o.getKey(), "true");
                                                    } else if (o.getKey().equals("generateRun")) {
                                                        props.put(o.getKey(), "false");
                                                    } else if (o.getKey().equals("methodToCheck")) {
                                                        props.put(o.getKey(), input.getMethod() + "_0");
                                                    } else {
                                                        props.put(o.getKey(), o.getValue());
                                                    }
                                                }
                                                //TODO Ver si el primer argumento no tiene que ser filename
                                                DarwinistInput darwinistInput = new DarwinistInput(
                                                        input.getFilename(), 
                                                        input.getOriginalFilename(), 
                                                        input.getConfigurationFile(), 
                                                        input.getMethod(), 
                                                        props, 
                                                        null, 
                                                        null,
                                                        true, 
                                                        input.getMethod(),
                                                        failedMethods.get(methodName),
                                                        null,
                                                        null,
                                                        null,
                                                        input.getFeedback(),
                                                        input.getMutantsToApply(),
                                                        input.getSyncObject()
                                                        );
                                                darwinistInput.setRacMethod(input.getRacMethod());
                                                DarwinistController.getInstance().enqueueTask(darwinistInput);
                                                StrykerStage.mutationsQueuedToDarwinistForSeq++;
                                            } else {
                                                MuJavaInput mujavainput = new MuJavaInput(
                                                        input.getFilename(), input.getMethod(), 
                                                        input.getMutantsToApply(), new AtomicInteger(0), 
                                                        input.getConfigurationFile(), input.getOverridingProperties(), 
                                                        input.getOriginalFilename(), input.getSyncObject(),
                                                        input.getFullyQualifiedClassName(), input.getMethodUnderAnalysis());
                                                MuJavaFeedback feedback = input.getFeedback();
                                                feedback.setFatherable(true);
                                                feedback.setGetSibling(true);
                                                feedback.setMutateRight(true);
                                                mujavainput.setMuJavaFeedback(feedback);
                                                MuJavaController.getInstance().enqueueTask(mujavainput);
                                            }
                                        }
                                        StrykerStage.postconditionFailedMutations++;
                                    }
                                }

                                if (!timeoutMethods.isEmpty()) {
                                    //                                    StrykerStage.mutationsQueuedToDarwinistForSeq -= timeoutMethods.size();
                                    //                                    StrykerStage.postconditionFailedMutations -= timeoutMethods.size();
                                    StrykerStage.timeoutMutations += timeoutMethods.size();

                                    //                                        for (String string : timeoutMethods) {
                                    //                                            StrykerStage.timeoutMutations++;
                                    //                                            input = map.get(string);
                                    //                                            MuJavaInput mujavainput = new MuJavaInput(wrapper.getOldFilename(), string, input.getJunitInputs(), input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject());
                                    //                                            MuJavaFeedback feedback = input.getFeedback();
                                    //                                            feedback.setMutateUntilLine(0);
                                    //                                            mujavainput.setMuJavaFeedback(feedback);
                                    //                                            mujavainput.getMuJavaFeedback().setFatherable(true);
                                    //                                            MuJavaController.getInstance().enqueueTask(mujavainput);
                                    //                                        }
                                }

                                if (!nullPointerMethods.isEmpty()) {
                                    //                                                StrykerStage.mutationsQueuedToDarwinistForSeq -= nullPointerMethods.size();
                                    StrykerStage.postconditionFailedMutations -= nullPointerMethods.size();
                                    StrykerStage.nullPointerExceptionMutations += nullPointerMethods.size();
                                    //                                        for (String string : nullPointerMethods) {
                                    //                                            StrykerStage.nullPointerExceptionMutations++;
                                    //                                            input = map.get(string);
                                    //                                            MuJavaInput mujavainput = new MuJavaInput(wrapper.getOldFilename(), string, input.getJunitInputs(), input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject());
                                    //                                            MuJavaFeedback feedback = input.getFeedback();
                                    //                                            feedback.setMutateUntilLine(0);
                                    //                                            mujavainput.setMuJavaFeedback(feedback);
                                    //                                            mujavainput.getMuJavaFeedback().setFatherable(true);
                                    //                                            MuJavaController.getInstance().enqueueTask(mujavainput);
                                    //                                        }
                                }
                            } catch (IllegalArgumentException e) {
                                //                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (InterruptedException e) {
                            break;
                            //e.printStackTrace();
                        }
                    }
                    log.warn("Shutting down Darwinist Controller");
                    DarwinistInput output = new DarwinistInput(
                            null, null, null, null, null, null, null, false, null, null, null, null, null, null, null, null);
                    DarwinistController.getInstance().enqueueTask(output);
                    //DarwinistController.getInstance().shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public String editFileToPassToNextStage(String filename) {
                String classPackage = filename.substring(filename.indexOf(FILE_SEP+"a")+1, filename.lastIndexOf(FILE_SEP)).replaceAll(FILE_SEP, ".");

                return classPackage+"."+MuJavaController.obtainClassNameFromFileName(filename);
            }

        };
    }

    public static void addUrl(URL u) {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { u });
            sysloader = null;
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                throw new IOException(
                        "Error, could not add URL to system classloader");
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }

    @Override
    protected int getQtyOfThreads() {
        return 1;
    }

}

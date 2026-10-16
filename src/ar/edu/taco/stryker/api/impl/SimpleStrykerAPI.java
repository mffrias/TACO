package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import ar.edu.taco.stryker.api.StrykerAPI;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import mujava.api.MutationOperator;

/**
 * Default {@link StrykerAPI}: starts the four singleton controllers and hands them the bug.
 *
 * <p>The search is a pipeline of controllers, each with one worker thread:
 * <ol>
 * <li>{@link UnskippableMuJavaController} receives the initial input on its local queue,
 *     generates the first mutants and seeds {@link ar.edu.taco.engine.StrykerStage#weedQueue}
 *     (queues 1 and 2) once its local queue has been empty for 2 seconds;</li>
 * <li>{@link MuJavaController} (queue 1) generates and compiles further mutants and
 *     computes feedback;</li>
 * <li>{@link OpenJMLController} (queue 2) filters mutants by running the stored
 *     counterexamples with runtime assertion checking;</li>
 * <li>{@link DarwinistController} (queue 3) verifies the survivors with TACO and records
 *     fixes.</li>
 * </ol>
 * The run ends when {@link DarwinistController} is shut down: when a fix is found (with
 * {@link MuJavaController#finishOnFirstFix}) or when an acknowledgement on the weed queue
 * reports that no work is left (see {@link WeedQueue}).
 *
 * <p>Because the controllers are singletons whose state is not reset, only one Stryker run
 * per JVM is supported.
 */
public class SimpleStrykerAPI implements StrykerAPI {

    private DarwinistController darwinistController;
    private MuJavaController mujavaController;
    private UnskippableMuJavaController unskippableMujavaController;
    private OpenJMLController openjmlController;

    /**
     * Obtains the four controller singletons and starts them, in this order:
     * {@link UnskippableMuJavaController}, {@link MuJavaController},
     * {@link OpenJMLController}, {@link DarwinistController}.
     */
    @Override
    public void start() {
        mujavaController = MuJavaController.getInstance();
        openjmlController = OpenJMLController.getInstance();
        darwinistController = DarwinistController.getInstance();
        unskippableMujavaController = UnskippableMuJavaController.getInstance();
        unskippableMujavaController.start();
        mujavaController.start();
        openjmlController.start();
        darwinistController.start();	
    }

    /**
     * Starts the search and waits for it to end. Requires {@link #start()} to have been
     * called.
     *
     * <p>Forwards {@code maxMethodsInFile} to {@link MuJavaController#setMaxMethodsInFile(int)}
     * and enqueues a single {@link MuJavaInput} to {@link UnskippableMuJavaController} (the
     * mandatory first stage). That input uses {@code classToMutate}'s absolute path both as
     * the file to mutate and as the original file, carries {@code mutOps},
     * {@code generationsWanted}, {@code configFile} and {@code overridingProperties}, a new
     * synchronization object, and the {@code classToCheck} and {@code methodToCheck} values of
     * {@code overridingProperties}. {@code classNameToMutate} is ignored.
     *
     * <p>It then polls {@link DarwinistController}'s shutdown flag every 500 ms, with no
     * timeout; interrupts are printed and ignored.
     *
     * @return {@link DarwinistController#getResolvedBugs()}: the paths of the mutant files
     *         verified as fixes, or an empty list. This is the controller's own list, not a
     *         copy.
     */
    @Override
    public List<String> fixBug(File classToMutate, String classNameToMutate, String methodToMutate, 
            HashSet<MutationOperator> mutOps, AtomicInteger generationsWanted, String configFile, 
            Properties overridingProperties, int maxMethodsInFile) {
        mujavaController.setMaxMethodsInFile(maxMethodsInFile);
        unskippableMujavaController.enqueueTask(new MuJavaInput(classToMutate.getAbsolutePath(), methodToMutate, 
                mutOps, generationsWanted, configFile, overridingProperties, classToMutate.getAbsolutePath(),
                new Object(), overridingProperties.getProperty("classToCheck"), overridingProperties.getProperty("methodToCheck")));
        while(!darwinistController.willShutdown.get()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Interrupted Exception en el SimpleStrykerAPI");
                e.printStackTrace();
            }
        }
        return darwinistController.getResolvedBugs();
    }

}

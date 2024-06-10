package ar.edu.taco.utils;

import ar.edu.taco.TacoAnalysisResult;
import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.jml.parser.JmlParser;
//import ar.edu.taco.jml.JmlParser.TypeCheckerMain;
import org.apache.log4j.Logger;
//import org.multijava.mjc.Main.ExpectedGF;
//import org.multijava.mjc.Main.ExpectedIndifferent;
//import org.multijava.mjc.Main.ExpectedResult;
//import org.multijava.mjc.Main.ExpectedType;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ar.edu.taco.simplejml.SimpleJmlToJDynAlloyContext;

public class TranslateThread extends Thread {


	public JmlParser threadParserInstance = new JmlParser();
	//
	//	public TypeCheckerMain threadTypeCheckerMainInstance = threadParserInstance.new TypeCheckerMain();
	//
	//	public Debug threadDebug = new Debug();
	//	
	//	public ExpectedIndifferent threadExpectedIndifferent = new org.multijava.mjc.Main().new ExpectedIndifferent(0);
	//	
	//	public ExpectedResult threadExpectedResult = new org.multijava.mjc.Main().new ExpectedResult();
	//	
	//	public ExpectedType threadExpectedType = new org.multijava.mjc.Main().new ExpectedType();
	// 
	//	public ExpectedGF threadExpectedGF = new org.multijava.mjc.Main().new ExpectedGF();




	JCompilationUnitTypeWrapper JUnit;
	SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext;
	Object inputToFix;
	JmlToSimpleJmlContext jmlToSimpleJmlContext;
	Properties overridingProperties;
	Logger log;
	TacoAnalysisResult translatedAnalysisResult;
	String classToCheck;
	String methodToCheck;
	String sourceRootDir;
	String configFile;
	String FILE_SEP;

	Semaphore semJmlParser;
	Semaphore semJava2JDyn;
	Semaphore semJDyn2Dyn;
	Semaphore semJUnitConstruction;

	ConcurrentLinkedQueue<Message> theSharedQueue;







	public TranslateThread(ConcurrentLinkedQueue<Message> theSharedQueue, Semaphore semJmlParser, Semaphore semJava2JDyn, Semaphore semJDyn2Dyn,
			Semaphore semJUnitConstruction, JCompilationUnitTypeWrapper simpleUnit, JmlToSimpleJmlContext jmlSimpleContext,
			Properties tacoProperties, Logger tacoLog, TacoAnalysisResult tacoResult,
			Object tacoInputFix, String tacoClassToCheck,
			String tacoMethodToCheck, String tacoSourceRootDir, String tacoConfigFile,
			String tacoFILE_SEP, int timeout){

		this.JUnit = simpleUnit;
		this.jmlToSimpleJmlContext = jmlSimpleContext;
		this.overridingProperties = tacoProperties;
		this.log = tacoLog;
		this.translatedAnalysisResult = tacoResult;
		this.inputToFix = tacoInputFix;
		this.classToCheck = tacoClassToCheck;
		this.methodToCheck = tacoMethodToCheck;
		this.sourceRootDir = tacoSourceRootDir;
		this.configFile = tacoConfigFile;
		this.FILE_SEP = tacoFILE_SEP;
		this.semJmlParser = semJmlParser;
		this.semJava2JDyn = semJava2JDyn;
		this.semJDyn2Dyn = semJDyn2Dyn;
		this.theSharedQueue = theSharedQueue;
		this.semJUnitConstruction = semJUnitConstruction;
	}

	@Override
	public void run() {


		Message m = new Message();
		m.setTheWorkingThread(this);

		//create callable
		TranslateCallable translationCallable = 
				new TranslateCallable(semJmlParser, semJava2JDyn, semJDyn2Dyn, JUnit, jmlToSimpleJmlContext,overridingProperties,log,translatedAnalysisResult,inputToFix,classToCheck,methodToCheck,sourceRootDir,configFile,FILE_SEP);

		ExecutorService callableService = Executors.newFixedThreadPool(1);

		long currentTime = System.currentTimeMillis();
		Future<TacoAnalysisResult> theFuture = callableService.submit(translationCallable);

		TacoAnalysisResult tacoAnalysisResult = null;

		try{
			int timeout = Integer.MAX_VALUE;
			if (!JUnit.getDeterminized()) {
				timeout = JUnit.getTimeout();
			} 
			this.JUnit.setTimeout(timeout);


			tacoAnalysisResult = theFuture.get(Integer.MAX_VALUE, TimeUnit.SECONDS);

		} catch (CancellationException e) {
			System.out.println("FAILS WITH CANCELLATION EXCEPTION");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("FAILS WITH INTERRUPTED EXCEPTION");

			e.printStackTrace();
		} catch (TimeoutException e) {
			System.out.println("FAILS WITH TIMEOUT EXCEPTION");
			//			m.setTO(true);
			e.printStackTrace();
		} catch (ExecutionException e) {
			System.out.println("FAILS WITH EXECUTED EXCEPTION");
			e.getCause().printStackTrace();
		} finally {
			if (this.getCompilationUnitWrapper().getTimeOuted()) {
				m.setTO(true);
			} else {
				System.out.println("IT WAS NOT TIMEOUT");
				m.setTheResult(this.getCompilationUnitWrapper().getOutput());
			}
		}

		System.out.println("Message in Translate " + 
				m.theResult + " " + 
				m.TO + " " + 
				m.theWorkingThread.getCompilationUnitWrapper().getDeterminized() +  " " + 
				m.theWorkingThread.getCompilationUnitWrapper().getTimeout());

		
		theSharedQueue.offer(m);
		callableService.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!callableService.awaitTermination(2, TimeUnit.SECONDS)) {
				callableService.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!callableService.awaitTermination(2, TimeUnit.SECONDS)) {}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			callableService.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
			System.out.println("THIS SHOULD NOT PRINT");
		}
	}


	public JCompilationUnitTypeWrapper getCompilationUnitWrapper() {
		// TODO Auto-generated method stub
		return this.JUnit;
	}

	public void setCompilationUnitWrapper(JCompilationUnitTypeWrapper determinizedWrapped) {
		// TODO Auto-generated method stub
		this.JUnit = determinizedWrapped;
	}
}
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
package ar.edu.taco.jml.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlOptions;
import org.jmlspecs.checker.JmlTypeDeclaration;
import org.jmlspecs.checker.JmlTypeLoader;
import org.jmlspecs.checker.Main;
import org.multijava.mjc.CClass;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.util.compiler.CompilationAbortedError;
import org.multijava.util.compiler.CompilationAbortedException;

import ar.edu.taco.Java7ClassLibrary;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;

/**
 * TACO's JML parser: parses and type checks Java + JML sources with JML2 (on MultiJava,
 * {@code lib/jml-release.jar}) and gives out their compilation units, the input of
 * {@code engine.JmlStage}. {@code TacoMain.run} calls {@link #initialize} once for each class it
 * translates (the class to check and those of {@code classes} or {@code relevantClasses}), each
 * time with that class alone, read from {@code jmlParser.sourcePathStr}, and collects
 * {@link #getCompilationUnits()} after each call; {@code JmlStage} does the same with the
 * simplified files it writes under {@code output/}. See docs/ARCHITECTURE.md (section 1).
 *
 * <p>JML2 cannot read the class library of Java 8 or later. When a Java 7 {@code rt.jar} is
 * there ({@code Java7ClassLibrary}: {@code -Dstryker.classlib} or {@code lib/java7/rt.jar}),
 * {@link #initialize} makes it MultiJava's boot class path; without it, type checking stops at
 * {@code java.lang.CharSequence} and the abort is only logged. See docs/BUILDING.md ("The Java 7
 * class library") and docs/FIXES-FOR-MASTER.md (S16).
 *
 * <p>Static state: TACO uses one instance, {@link #getInstance()}, for every parse in the JVM.
 * It keeps the class list and source path of its last successful {@link #initialize}, so it
 * gives out only the last file's units (docs/FIXES-FOR-MASTER.md, S12); callers keep the units
 * they need. The units live in JML2's static type loader
 * ({@code JmlTypeLoader.getJmlSingleton()}), which every parse starts afresh, and the boot class
 * path is a static field of MultiJava's {@code ClassPath} that is never reset. Not thread-safe:
 * Stryker's two threads that run TACO are synchronized on {@code DarwinistController}.
 */
public class JmlParser {
    private Logger log = Logger.getLogger(JmlParser.class);
    private List<String> parse;

    /**
     * JML2's command-line front end with a compilation step that does not stop TACO: it processes
     * JML2's task queue (parsing and type checking) and logs a
     * {@code CompilationAbortedException} or {@code CompilationAbortedError} instead of passing it
     * on. {@link JmlParser#initialize} runs one per call.
     */
    public class TypeCheckerMain extends Main {

        /**
         * Processes JML2's task queue, logs an aborted compilation and how long it took.
         *
         * @param arg0 unused
         * @return always {@code true}, also when the compilation was aborted; errors reported by
         *         the type checker are seen in its output (see {@link JmlParser#initialize})
         */
        @Override
        protected boolean runCompilation(long arg0) {
            long time = System.currentTimeMillis();

            try {
                processTaskQueue();
            } catch (CompilationAbortedException e) {
                log.error(e);
            } catch (CompilationAbortedError e) {
                log.error(e);
            }

            log.info("Parser JML finished in " + (int) (System.currentTimeMillis() - time) / 1000 + " seconds");

            return true;
        }

    }

    /**
     * JML file extensions
     */
    private static final String[] EXTENSIONS = new String[]{".java", ".spec", ".jml", ".refines-java", ".refines-spec", ".refines-jml"};

    private static final String PATH_SEP = System.getProperty("path.separator");

    private static final String FILE_SEP = System.getProperty("file.separator");

    private final static JmlParser instance = new JmlParser();

    private boolean initialized = false;
    private List<String> file_sources;
    private final HashMap<String, JCompilationUnitType> compilation_unit_of = new HashMap<String, JCompilationUnitType>();

    /**
     * Returns the parser TACO uses for every parse in the JVM (see the class comment).
     *
     * @return the singleton
     */
    public static JmlParser getInstance() {
        return instance;
    }

    /**
     * Creates a parser that is not the singleton. TACO uses {@link #getInstance()} only
     * ({@code JmlStage} calls it through a new instance). Any instance shares JML2's static state.
     */
    public JmlParser() {
        //initialized = false;
    }

    /**
     * Parses and type checks the source files of the given classes with JML2, in a new JML2
     * session, and remembers them for {@link #getCompilationUnits()}.
     *
     * <p>Each class is looked up under the roots of {@code sourcePathStr} ({@link #getFile});
     * the classes of {@code TacoConfigurator.get_aux_classes_set()} ({@code java.util.Set}) are
     * skipped.
     * JML2 gets: as source path, those roots; as class path, {@code java.class.path} without the
     * running JDK's entries ({@code Java7ClassLibrary.withoutJdkEntries}), then
     * {@code appClassPath}, then a fixed Windows path to {@code jml-release.jar} (from the
     * original authors' machine); as boot class path, the Java 7 {@code rt.jar} if there is one
     * (see the class comment); and Java 1.4 source syntax with generics, purity and
     * {@code assignable} checks, no MultiJava extensions, no universes, quiet.
     *
     * <p>Side effects: sets MultiJava's static boot class path (when the Java 7 library is
     * there); starts a new JML2 session, which drops the units of earlier parses from JML2's type
     * loader; clears the units recorded by {@link #getTypeDeclaration}. Only on success does it
     * store the roots and the class list (until then, the last successful call's stay). On
     * failure it prints the type checker's output to {@code System.out}; on success the output
     * is discarded.
     *
     * @param sourcePathStr the source roots, separated by {@code path.separator}; each must exist
     * @param appClassPath one more class path entry for JML2 (TACO passes {@code <user.dir>/bin})
     * @param parse the classes to parse, by qualified name; for a nested class
     *        ({@code Outer$Inner}) the file of {@code Outer} is used
     * @return {@code false} if the type checker's output contains the text {@code error};
     *         {@code true} otherwise, also when the compilation was aborted (only logged, see
     *         {@link TypeCheckerMain})
     * @throws RuntimeException if a source root does not exist, or no file is found for a class
     */
    public boolean initialize(String sourcePathStr, String appClassPath, List<String> parse) {

        compilation_unit_of.clear();

        List<String> sources = new ArrayList<String>();
        for (String s : sourcePathStr.split(PATH_SEP)) {
            File f = new File(s);
            if (!f.exists()) {
                throw new RuntimeException("Source path does not exist: " + s);
            } else
                sources.add(f.getAbsolutePath());
        }
        //		sources.add(System.getProperty("user.dir") + FILE_SEP + "specs");

        // The JML2 parser cannot use the Java 8+ class library: on JDK 8 type checking stops at
        // java.lang.CharSequence. Read a Java 7 class library instead, if there is one.
        if (Java7ClassLibrary.isAvailable()) {
            org.multijava.util.classfile.ClassPath.setBootClassPath(Java7ClassLibrary.path());
        }

        JmlOptions options = new JmlOptions("jml");

        // Paths
        String classPath = Java7ClassLibrary.withoutJdkEntries(System.getProperty("java.class.path")) + PATH_SEP + appClassPath + PATH_SEP
                + "D:/work/facu/Tesis/Workspace/dynjalloy/lib/jml-release.jar" + PATH_SEP;

        options.set_classpath(classPath);
        StringBuilder sourcePath = new StringBuilder();
        for (String s : sources)
            sourcePath.append(s).append(PATH_SEP);
        options.set_sourcepath(sourcePath.toString());

        // Allow generic source code (experimental)
        options.set_generic(true);

        // Parse assertions and other Java 1.4 syntax
        options.set_source("1.4");

        // Deny multi-java code
        options.set_multijava(false);

        // Type-checking configuration
        options.set_purity(true);
        options.set_assignable(true);
        options.set_Assignable(true);
        options.set_universesx("no");

        // Verbose
        options.set_verbose(false);
        options.set_Quiet(!options.verbose());
        options.set_quiet(!options.verbose());

        // Experimental options
        options.set_keepGoing(false);

        // Feed in file names
        List<String> fileNames = new ArrayList<String>();
        for (String s : parse) {
            if (!(TacoConfigurator.get_aux_classes_set().contains(s))) {
                String file = getFile(s, sources);
                if (file == null)
                    throw new RuntimeException("Specification file not found for " + s);
                else
                    fileNames.add(file);
            }
        }
        TypeCheckerMain main = new TypeCheckerMain();
        OutputStream os = new ByteArrayOutputStream();

        main.run(fileNames.toArray(new String[]{}), options, os);

        if (os.toString().contains("error")) {
            System.out.println(os.toString());
            return false;
        }

        file_sources = sources;
//		System.out.println("Initialized value 2: " + initialized);
        initialized = true;
//		System.out.println("Initialized value 3: " + initialized);
        // DOB
        this.parse = parse;

        return true;
    }

    /**
     * Returns the source file of a class: looks, root by root in order, for
     * {@code <root>/<package path>/<Class><ext>} with the extensions {@code .java},
     * {@code .spec}, {@code .jml}, {@code .refines-java}, {@code .refines-spec} and
     * {@code .refines-jml}, in that order. A nested class ({@code Outer$Inner}) is looked up as
     * {@code Outer}.
     *
     * @param className the class, by qualified name
     * @param sources the source roots
     * @return the path of the first file that exists, or {@code null} if there is none
     */
    protected String getFile(String className, List<String> sources) {
        // check if we are in a thread
        // if true, remove prefix to get pure class name
        String prefixName = "output";
//		String classPrefix = className.substring(0,prefixName.length());//System.out.println("Prefix name: " + prefixName);
//		if (prefixName.equals(classPrefix)){
//			className = className.substring(prefixName.length()+1);
//		}

        // System.out.println("Class name: " + className);

        String cu = ((className.contains("$")) ? className.substring(0, className.indexOf("$")) : className).replace(".", FILE_SEP);

        // Look for the file
        for (String source : sources) {
            for (String ext : EXTENSIONS) {
                StringBuilder sb = new StringBuilder();
                sb.append(source).append(FILE_SEP).append(cu).append(ext);
                String filename = sb.toString();
                //				System.out.println("looking for: "+filename);
                if (new File(filename).exists())
                    return filename;
            }
        }

        return null;
    }

    /**
     * Returns the compilation unit recorded for a class by {@link #getTypeDeclaration} since the
     * last {@link #initialize}. No caller in TACO, jDynAlloy or dynalloy4; and since only
     * {@link #collectsASTs()}, which has no caller either, calls {@link #getTypeDeclaration}, it
     * returns {@code null} as the code stands.
     *
     * @param classname the class, by qualified name
     * @return the recorded unit, or {@code null}
     */
    public JCompilationUnitType getCompilationUnitType(String classname) {
        return compilation_unit_of.get(classname);
    }

    /**
     * Returns the JML type declaration of a class, from JML2's type loader, and records the
     * compilation unit of the class's file for {@link #getCompilationUnitType}. Only
     * {@link #collectsASTs()} calls it.
     *
     * <p>Assumes a successful {@link #initialize} (checked by an {@code assert} only) whose JML2
     * session parsed the class's file. Unlike {@link #getCompilationUnits()}, it does not skip
     * {@code TacoConfigurator.get_aux_classes_set()}; for a class with no file under the source
     * roots it throws a {@code NullPointerException} ({@code new File(null)}).
     *
     * @param className the class, by qualified name
     * @return its type declaration
     * @throws TacoException if JML2 has no compilation unit for the class's file
     * @throws RuntimeException if JML2 has no type declaration for the class
     */
    public JmlTypeDeclaration getTypeDeclaration(String className) {
        assert (initialized == true);

        JmlTypeLoader jmlSingleton = JmlTypeLoader.getJmlSingleton();

        String clazzName = className.replace(".", "/");

        String filename = getFile(className, file_sources);
        File file = new File(filename);
        JCompilationUnitType compilationUnit = jmlSingleton.getCUnitAST(file);
        if (compilationUnit == null) {
            throw new TacoException("could not find compilation unit for " + filename);
        }

        //		boolean b1 = jmlSingleton.isTypeLoaded(clazzName);

        //		CClass clazz2 = jmlSingleton.loadType(clazzName);

        CClass clazz = jmlSingleton.lookupType(clazzName);

        JmlTypeDeclaration result = jmlSingleton.typeDeclarationOf(clazz);
        if (result == null)
            throw new RuntimeException("The source for the class " + className
                    + " was either not found or not parsed; try adding the class as an additional dependency to be parsed.");

        compilation_unit_of.put(className, compilationUnit);

        return result;

    }

    /**
     * Returns the type declarations of the classes of the last successful {@link #initialize}
     * ({@link #getTypeDeclaration} for each, which records their units), as an unmodifiable
     * list in the order of the class list. No caller in TACO; TACO uses
     * {@link #getCompilationUnits()}. Unlike that method, it does not skip
     * {@code TacoConfigurator.get_aux_classes_set()}.
     *
     * @return the type declarations
     */
    @Deprecated
    public List<JmlTypeDeclaration> collectsASTs() {
        List<JmlTypeDeclaration> asts = new ArrayList<JmlTypeDeclaration>();
        for (String fileName : this.parse) {
            asts.add(this.getTypeDeclaration(fileName));
        }
        asts = Collections.unmodifiableList(asts);
        return asts;
    }

    /**
     * Returns the compilation units of the classes given to the last successful
     * {@link #initialize}, without those of {@code TacoConfigurator.get_aux_classes_set()}, each
     * unit once, in the order of the class list. They come from JML2's type loader, which holds
     * the files of the current JML2 session only.
     *
     * <p>Call it right after an {@link #initialize} that returned {@code true}. After one that
     * returned {@code false} or threw, it still uses the class list and source roots of the last
     * successful call, whose files the new session may not have; before any successful call it
     * throws a {@code NullPointerException}. The {@code initialized} flag is checked by an
     * {@code assert} only.
     *
     * @return the units, in a new list
     * @throws TacoException if JML2 has no compilation unit for one of the files
     */
    public List<JCompilationUnitType> getCompilationUnits() {
        assert (initialized == true);
        JmlTypeLoader jmlSingleton = JmlTypeLoader.getJmlSingleton();

        List<JCompilationUnitType> compilation_units = new LinkedList<JCompilationUnitType>();

        for (String class_name : this.parse) {
            // check if we are in a thread
            // if true, remove prefix to get pure class name
            String prefixName = "output_" + Thread.currentThread().getName();
//            String classPrefix = class_name.substring(0, prefixName.length());
//            if (prefixName.equals(classPrefix)) {
//                class_name = class_name.substring(prefixName.length() + 1);
//            }

            //	System.out.println("Get compilation unit for class: " + class_name + " on thread: " + Thread.currentThread().getName());

            if (!TacoConfigurator.get_aux_classes_set().contains(class_name)) {
                String filename = getFile(class_name, file_sources);
                File file = new File(filename);
                JCompilationUnitType compilationUnit = jmlSingleton.getCUnitAST(file);
                if (compilationUnit == null) {
                    throw new TacoException("could not find compilation unit for " + filename);
                }
                if (!compilation_units.contains(compilationUnit)) {
                    compilation_units.add(compilationUnit);
                }
            }
        }

        return compilation_units;
    }


}

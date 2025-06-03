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

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;

public class JmlParser {
    private Logger log = Logger.getLogger(JmlParser.class);
    private List<String> parse;

    public class TypeCheckerMain extends Main {

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

    public static JmlParser getInstance() {
        return instance;
    }

    public JmlParser() {
        //initialized = false;
    }

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

        JmlOptions options = new JmlOptions("jml");

        // Paths
        String classPath = System.getProperty("java.class.path") + PATH_SEP + appClassPath + PATH_SEP
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

    public JCompilationUnitType getCompilationUnitType(String classname) {
        return compilation_unit_of.get(classname);
    }

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

    @Deprecated
    public List<JmlTypeDeclaration> collectsASTs() {
        List<JmlTypeDeclaration> asts = new ArrayList<JmlTypeDeclaration>();
        for (String fileName : this.parse) {
            asts.add(this.getTypeDeclaration(fileName));
        }
        asts = Collections.unmodifiableList(asts);
        return asts;
    }

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

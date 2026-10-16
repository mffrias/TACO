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
package ar.edu.taco;

import java.io.File;

/**
 * The Java 7 class library that the JML tools read.
 *
 * <p>The JML parser (JML2/MultiJava, {@code lib/jml-release.jar}) and the runtime assertion
 * checker JML4C ({@code lib/stryker/jml4c.jar}) resolve JDK types by reading the JDK's class
 * files, and they cannot use the class library of Java 8 or later (they do read the subjects'
 * classes compiled for Java 8). On Java 8, 21 and 25 they, and the JDT parser Stryker uses for
 * variabilization, are given a Java 7 {@code rt.jar} instead: the one named by the
 * {@value #PROPERTY} system property, or {@value #DEFAULT_LOCATION} under the working directory,
 * where {@code scripts/fetch-java7-classlib.sh} puts it.
 *
 * <p>Plain TACO runs without it: the JML parser then stops type checking at
 * {@code java.lang.CharSequence}, and TACO logs the error and goes on. Stryker cannot: without it
 * JML4C compiles no mutant and the run never ends, so {@link #checkForStryker()} fails first.
 */
public final class Java7ClassLibrary {

    /** System property naming a Java 7 {@code rt.jar}. */
    public static final String PROPERTY = "stryker.classlib";

    /** Where the {@code rt.jar} is looked for when {@value #PROPERTY} is not set, relative to the working directory. */
    public static final String DEFAULT_LOCATION = "lib/java7/rt.jar";

    private Java7ClassLibrary() {
    }

    /**
     * The Java 7 {@code rt.jar} to use: the {@value #PROPERTY} system property if set, or
     * {@value #DEFAULT_LOCATION} under {@code user.dir}. The file may not exist; see
     * {@link #isAvailable()}.
     */
    public static String path() {
        String configured = System.getProperty(PROPERTY);
        if (configured != null && !configured.isEmpty()) {
            return configured;
        }
        return System.getProperty("user.dir") + File.separator + DEFAULT_LOCATION;
    }

    /** Whether {@link #path()} exists. */
    public static boolean isAvailable() {
        return new File(path()).isFile();
    }

    /**
     * {@code classPath} without the entries that belong to the running JDK (files under its
     * installation directory). Some launchers, IntelliJ among them, put the JDK's own jars, its
     * {@code rt.jar} included, on the class path of the programs they run; the JML tools, which
     * are handed {@code java.class.path}, would then read the running JDK's classes there despite
     * {@link #path()}.
     *
     * @param classPath a class path, entries separated by {@link File#pathSeparator}
     * @return the class path without the running JDK's entries
     */
    public static String withoutJdkEntries(String classPath) {
        if (classPath == null) {
            return null;
        }
        File javaHome = new File(System.getProperty("java.home")).getAbsoluteFile();
        // On Java 8, java.home is the JDK's jre/ directory, and lib/tools.jar & co. are next to it.
        File jdk = "jre".equals(javaHome.getName()) ? javaHome.getParentFile() : javaHome;
        String jdkPrefix = canonicalPath(jdk) + File.separator;
        StringBuilder kept = new StringBuilder();
        for (String entry : classPath.split(File.pathSeparator, -1)) {
            if (!entry.isEmpty() && canonicalPath(new File(entry)).startsWith(jdkPrefix)) {
                continue;
            }
            if (kept.length() > 0) {
                kept.append(File.pathSeparator);
            }
            kept.append(entry);
        }
        return kept.toString();
    }

    private static String canonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (java.io.IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * Fails unless Stryker can run: on Java 8, 21 or 25, with a Java 7 class library at
     * {@link #path()}. This TACO does not run on Java 7, and Stryker has not been tried on other
     * versions.
     *
     * @throws IllegalStateException with instructions, if that is not the case
     */
    public static void checkForStryker() {
        String version = System.getProperty("java.specification.version");
        if (!"1.8".equals(version) && !"21".equals(version) && !"25".equals(version)) {
            throw new IllegalStateException("Stryker runs on Java 8, 21 or 25, but this is Java " + version
                    + " (" + System.getProperty("java.home") + ").");
        }
        if (!isAvailable()) {
            throw new IllegalStateException("Stryker needs a Java 7 class library for the JML tools, and "
                    + path() + " does not exist. Run scripts/fetch-java7-classlib.sh, or set -D" + PROPERTY
                    + " to a Java 7 rt.jar.");
        }
    }
}

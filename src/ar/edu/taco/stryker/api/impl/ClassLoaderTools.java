package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Reflection helpers that add a file or URL to a {@link URLClassLoader} at run time by calling its
 * protected {@code addURL} method. The variants without a class loader argument target the system class
 * loader, which is a {@code URLClassLoader} only up to Java 8 (Stryker runs on Java 8).
 * <p>
 * Currently unused (no callers in this repository; the remaining references are commented out).
 * OpenJMLController has a similar static {@code addUrl} helper.
 */
public class ClassLoaderTools {

	/** Parameter types of {@code URLClassLoader.addURL(URL)}. */
	private static final Class<?>[] parameters = new Class<?>[] { URL.class };

	/**
	 * Adds the file or directory at path {@code s} to the system class loader.
	 *
	 * @param s path of a jar file or class directory
	 * @throws IOException if the URL could not be added
	 */
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}// end method

	/**
	 * Adds {@code f} to the system class loader.
	 *
	 * @param f a jar file or class directory
	 * @throws IOException if the URL could not be added
	 */
	public static void addFile(File f) throws IOException {
		addURL(f.toURI().toURL());
	}// end method

	/**
	 * Adds {@code u} to the system class loader, which must be a {@link URLClassLoader}.
	 *
	 * @param u the URL to add
	 * @throws IOException if the reflective call fails
	 */
	public static void addURL(URL u) throws IOException {

		URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}// end try catch

	}// end method
	
	/**
	 * Adds {@code u} to the given class loader. The error message still mentions the system class
	 * loader.
	 *
	 * @param u the URL to add
	 * @param sysloader the class loader to extend
	 * @throws IOException if the reflective call fails
	 */
	public static void addURL(URL u, URLClassLoader sysloader) throws IOException {

		Class<?> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}// end try catch

	}// end method

}// end class
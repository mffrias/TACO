package ar.edu.taco.stryker.api.impl;

/**
 * Java source snippets that Stryker writes into generated files.
 */
public class StringsToWriteInFile {

	
	/**
	 * Java source of a helper method,
	 * {@code protected static ReachSet reach(Object o, Class<?> clazz, String str)}.
	 * {@code MuJavaController.adaptSiblingsFileToJML4C} inserts it right after the opening brace of the
	 * class in every JML4C batch file. It also rewrites each JML {@code \reach(o, T, f + g)} into
	 * {@code reach(o, T.class, "f + g")}, so that runtime assertion checking can evaluate the
	 * expression.
	 * <p>
	 * The helper traverses the heap breadth-first from {@code o}, following only the fields named in
	 * {@code str} (separated by {@code +}, with spaces ignored, and looked up with
	 * {@code getDeclaredField} on each object's class). It returns a {@link ReachSet} containing
	 * {@code o} plus every reached object that is an instance of {@code clazz}, or an empty set if
	 * {@code o} is {@code null}. Reflection errors are printed and ignored.
	 */
	public final static String reachMethod = "" +
//		"    @java.lang.SuppressWarnings(\"unchecked\")" + "\n" +
		"    protected static ar.edu.taco.stryker.api.impl.ReachSet reach(/*@nullable@*/ Object o, Class<?> clazz, String str) {" + "\n" +
		"        String fieldsToMoveThroughAsStrings[] = str.replaceAll(\" \", \"\").split(\"\\\\+\");" + "\n" +
		"        ar.edu.taco.stryker.api.impl.ReachSet objectSet = new ar.edu.taco.stryker.api.impl.ReachSet();" + "\n" +
		"        java.util.IdentityHashMap<Object,Object> visitedObjects = new java.util.IdentityHashMap<Object,Object>();" + "\n" +
		"        java.util.Queue<Object> queue = new java.util.LinkedList<Object>();" + "\n" +
		"        " + "\n" +
		"		 if(o == null) {" + "\n" +
		"        	return objectSet;" + "\n" +
		"		 }" +"\n"+
		"        visitedObjects.put(o, o);" + "\n" +
		"        queue.add(o);" + "\n" +
		"		 objectSet.add(o);" + "\n" +
		"        while(!queue.isEmpty()) {" + "\n" +
		"            Object currentObject = queue.poll();" + "\n" +
		"            " + "\n" +
		"            java.util.List<java.lang.reflect.Field> fieldsToMoveThrough = new java.util.ArrayList<java.lang.reflect.Field>();" + "\n" +
		"            for(int i = 0; i < fieldsToMoveThroughAsStrings.length; i++) {" + "\n" +
		"                    String fieldAsString = fieldsToMoveThroughAsStrings[i];" + "\n" +
		"                try {" + "\n" +
		"                    java.lang.reflect.Field field = currentObject.getClass().getDeclaredField(fieldAsString);" + "\n" +
		"                    fieldsToMoveThrough.add(field);" + "\n" +
		"                } catch (NoSuchFieldException e) {" + "\n" +
		"                " + "e.printStackTrace();" + "\n" +
		"                } catch (SecurityException e) {" + "\n" +
		"                     " + "e.printStackTrace();" + "\n" +
		"                }" + "\n" +
		"            }" + "\n" +
		"            " + "\n" +
		"            for(java.lang.reflect.Field field : fieldsToMoveThrough) {" + "\n" +
		"                field.setAccessible(true);" + "\n" +
		"                try {" + "\n" +
		"                    Object accessedObject = field.get(currentObject);" + "\n" +
		"                    if(accessedObject != null && visitedObjects.containsKey(accessedObject) == false) {" + "\n" +
		"                        visitedObjects.put(accessedObject, accessedObject);" + "\n" +
		"                        queue.add(accessedObject);" + "\n" +
		"                        if(clazz.isInstance(accessedObject)) {" + "\n" +
		"                            objectSet.add(accessedObject);" + "\n" +
		"                        }" + "\n" +
		"                    }" + "\n" +
		"                } catch (IllegalArgumentException e) {" + "\n" +
		"                " + "e.printStackTrace();" + "\n" +
		"                } catch (IllegalAccessException e) {" + "\n" +
		"                    " + "e.printStackTrace();" + "\n" +
		"                }" + "\n" +
		"            }" + "\n" +
		"        }" + "\n" +
		"        " + "\n" +
		"        return objectSet;" + "\n" +
	    "    }" + "\n";
}

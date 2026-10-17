package ar.edu.taco.stryker;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads and builds the {@code methodToCheck} strings that TACO accepts.
 *
 * <p>A driver names the method under analysis by its class, name and
 * parameter types: {@code <package>_<Class>_<method>(<type>, <type>)}, e.g.
 * {@code icse_singlylinkedlist_set1_SinglyLinkedList_contains(java_lang_Object)}
 * or {@code ximena_MutRecursive_isEven(int, ximena_MutRecursive)}. A type is
 * written as the JML parser prints it with its dots as underscores (see
 * {@link #typeName}): primitive types as they are, {@code java_lang_Object},
 * {@code int[]}, and a nested class as {@code pkg_Outer_Inner}. TACO ignores
 * the spaces and turns any remaining dots into underscores
 * ({@code TacoConfigurator.getMethodToCheck}), and when it looks for the
 * method it turns the underscores of the types back into dots, so a type whose
 * name contains an underscore cannot be named this way.
 *
 * <p>Stryker needs the plain method name for mutation and for the generated
 * counterexample tests, and has to build new names when variabilization adds
 * parameters to the method. All of that goes through this class, so a change
 * in how TACO names methods only has to be made here.
 */
public final class MethodToCheck {

    private MethodToCheck() {
    }

    /**
     * The name without the parameter types, e.g.
     * {@code icse_singlylinkedlist_set1_SinglyLinkedList_contains} for
     * {@code icse_singlylinkedlist_set1_SinglyLinkedList_contains(java_lang_Object)}.
     */
    public static String qualifiedName(String methodToCheck) {
        int openingParenthesis = methodToCheck.indexOf('(');
        if (openingParenthesis < 0) {
            throw new IllegalArgumentException("methodToCheck must list the parameter types, e.g. "
                    + "\"pkg_Class_contains(java_lang_Object)\", but it is \"" + methodToCheck + "\"");
        }
        return methodToCheck.substring(0, openingParenthesis).trim();
    }

    /**
     * The plain name of the method, e.g. {@code contains} for
     * {@code icse_singlylinkedlist_set1_SinglyLinkedList_contains(java_lang_Object)}
     * and the class {@code icse.singlylinkedlist.set1.SinglyLinkedList}. The
     * class prefix is removed as a whole, so class and method names may
     * contain underscores.
     *
     * @param methodToCheck the {@code methodToCheck} of the driver
     * @param classToCheck the fully qualified name of the class under analysis
     * @throws IllegalArgumentException if {@code methodToCheck} is not named
     *         after {@code classToCheck}
     */
    public static String methodName(String methodToCheck, String classToCheck) {
        String qualifiedName = qualifiedName(methodToCheck);
        String prefix = classToCheck.replace('.', '_') + "_";
        if (!qualifiedName.startsWith(prefix) || qualifiedName.length() == prefix.length()) {
            throw new IllegalArgumentException("methodToCheck must be named " + prefix
                    + "<method>(<types>), e.g. \"" + prefix + "contains(java_lang_Object)\", but it is \""
                    + methodToCheck + "\"");
        }
        return qualifiedName.substring(prefix.length());
    }

    /**
     * The parameter types, in declaration order and without spaces, e.g.
     * {@code [int[], java_lang_Object]} for
     * {@code pkg_Class_copy(int[], java_lang_Object)}.
     */
    public static List<String> parameterTypes(String methodToCheck) {
        int openingParenthesis = methodToCheck.indexOf('(');
        int closingParenthesis = methodToCheck.lastIndexOf(')');
        if (openingParenthesis < 0 || closingParenthesis < openingParenthesis) {
            throw new IllegalArgumentException("methodToCheck must list the parameter types, e.g. "
                    + "\"pkg_Class_contains(java_lang_Object)\", but it is \"" + methodToCheck + "\"");
        }
        List<String> types = new ArrayList<String>();
        String parameters = methodToCheck.substring(openingParenthesis + 1, closingParenthesis);
        for (String type : parameters.split(",")) {
            type = type.replaceAll("\\s", "");
            if (!type.isEmpty()) {
                types.add(type);
            }
        }
        return types;
    }

    /**
     * How a type is written in a {@code methodToCheck}: its name with dots
     * replaced by underscores, e.g. {@code java_lang_Object} for
     * {@code java.lang.Object} and {@code pkg_Outer_Inner} for the nested class
     * {@code pkg.Outer.Inner}; {@code int} and {@code int[]} stay as they are.
     *
     * @param javaTypeName a type name as in Java source, with dots
     */
    public static String typeName(String javaTypeName) {
        return javaTypeName.replace('.', '_');
    }

    /**
     * Builds a {@code methodToCheck} from a name without types (see
     * {@link #qualifiedName}) and parameter types written as in
     * {@link #typeName}, e.g. {@code pkg_Class_contains(java_lang_Object, int)}.
     */
    public static String of(String qualifiedName, List<String> parameterTypes) {
        StringBuilder name = new StringBuilder(qualifiedName).append('(');
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (i > 0) {
                name.append(", ");
            }
            name.append(parameterTypes.get(i));
        }
        return name.append(')').toString();
    }
}

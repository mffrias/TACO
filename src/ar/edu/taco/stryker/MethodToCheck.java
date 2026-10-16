package ar.edu.taco.stryker;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads and builds the {@code methodToCheck} strings that TACO accepts.
 *
 * <p>A driver names the method under analysis by its name and parameter
 * types, e.g. {@code contains(java.lang.Object)} or
 * {@code copyArray(int[], int, int, int[])}. TACO ignores spaces and turns
 * dots into underscores ({@code TacoConfigurator.getMethodToCheck}), so a
 * built name only has to list the types in declaration order, with their
 * fully qualified names.
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
     * The plain name of the method, e.g. {@code contains} for
     * {@code contains(java.lang.Object)}.
     */
    public static String methodName(String methodToCheck) {
        int openingParenthesis = methodToCheck.indexOf('(');
        if (openingParenthesis < 0) {
            throw new IllegalArgumentException("methodToCheck must list the parameter types, e.g. "
                    + "\"contains(java.lang.Object)\", but it is \"" + methodToCheck + "\"");
        }
        return methodToCheck.substring(0, openingParenthesis).trim();
    }

    /**
     * The parameter types, in declaration order and without spaces, e.g.
     * {@code [int[], int]} for {@code copyArray(int[], int)}.
     */
    public static List<String> parameterTypes(String methodToCheck) {
        int openingParenthesis = methodToCheck.indexOf('(');
        int closingParenthesis = methodToCheck.lastIndexOf(')');
        if (openingParenthesis < 0 || closingParenthesis < openingParenthesis) {
            throw new IllegalArgumentException("methodToCheck must list the parameter types, e.g. "
                    + "\"contains(java.lang.Object)\", but it is \"" + methodToCheck + "\"");
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
     * Builds the name of {@code methodName} with the given parameter types,
     * e.g. {@code contains(java.lang.Object,int)}.
     */
    public static String of(String methodName, List<String> parameterTypes) {
        StringBuilder name = new StringBuilder(methodName).append('(');
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (i > 0) {
                name.append(',');
            }
            name.append(parameterTypes.get(i));
        }
        return name.append(')').toString();
    }
}

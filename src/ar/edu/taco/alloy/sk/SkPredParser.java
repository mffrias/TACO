package ar.edu.taco.alloy.sk;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads the skolem variables of the Java arithmetic operations from the Alloy text, for
 * {@link SkPredFactBuilder}: the fields of {@code one sig QF} whose names have the format of
 * {@link SkPredArgument}. It reads the text line by line, from the line after the one that
 * contains {@code one sig QF} to the next line that contains a closing brace, and takes the
 * text before the first colon of each line as a field name; so it assumes that the printer
 * writes one field per line. {@link #parse(String)} replaces the arguments read before.
 */
class SkPredParser {

	private Set<SkPredArgument> sk_pred_arguments = new HashSet<SkPredArgument>();

	public void parse(String alloyStr) {

		sk_pred_arguments.clear();

		Scanner scanner;
		StringReader fileInputStream;
		fileInputStream = new StringReader(alloyStr);

		boolean visiting_QF = false;
		scanner = new Scanner(fileInputStream);
		while (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			if (nextLine.contains("one sig QF")) {
				// Entering QF
				visiting_QF = true;
			} else if (visiting_QF && nextLine.contains("}")) {
				// Exiting QF
				visiting_QF = false;
			} else if (visiting_QF) {
				// Visiting QF
				String[] name_type = nextLine.split(":");
				String name = name_type[0].trim();
				if (SkPredArgument.is_skolemized_predicate_argument_str(name)) {
					SkPredArgument skolemizedPredicateArgument = SkPredArgument.parse_skolemized_predicate_argument_str(name);
					sk_pred_arguments.add(skolemizedPredicateArgument);
				}
			}
		}
		scanner.close();
	}

	public Set<SkPredArgument> get_sk_pred_arguments() {
		return this.sk_pred_arguments;
	}

}

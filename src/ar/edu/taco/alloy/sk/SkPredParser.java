package ar.edu.taco.alloy.sk;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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

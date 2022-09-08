package ar.edu.taco.infer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ar.edu.taco.infer.Graph.LabeledNode;

public class GraphPathCounter {

	private Graph class_graph;

	private Set<String> cyclic_nodes;

	public Map<String, IntegerOrInfinity> count_all_paths(Graph class_graph) {
		this.class_graph = class_graph;

		Stack<String> path = new Stack<String>();
		String root_node_id = "$Root$";
		path.push(root_node_id);

		cyclic_nodes = find_cyclic_nodes();

		Map<String, IntegerOrInfinity> number_of_paths = new HashMap<String, IntegerOrInfinity>();
		for (String node_id : this.class_graph.nodeSet()) {
			IntegerOrInfinity path_count = count_paths(root_node_id, path, node_id);
			number_of_paths.put(node_id, path_count);
		}

		return number_of_paths;
	}

	private Set<String> find_cyclic_nodes() {

		Set<String> found_nodes = new HashSet<String>();

		Stack<String> path = new Stack<String>();
		String root_node_id = "$Root$";
		path.push(root_node_id);

		for (String node_id : class_graph.nodeSet()) {
			found_nodes.addAll(find_cyclic_nodes(root_node_id, path, node_id));
		}

		return found_nodes;
	}

	private Set<String> find_cyclic_nodes(String node_id, Stack<String> path, String destination_node_id) {
		Set<String> found_nodes = new HashSet<String>();
		if (node_id.equals(destination_node_id)) {
			return found_nodes;
		} else {
			for (LabeledNode labeled_node : class_graph.getLabelledEgdes(node_id)) {
				String new_node_id = labeled_node.node_id;
				if (path.contains(new_node_id)) {
					int start_index = path.indexOf(new_node_id);
					for (int i = start_index; i < path.size(); i++) {
						found_nodes.add(path.get(i));
					}
				} else {
					path.push(new_node_id);
					found_nodes.addAll(find_cyclic_nodes(new_node_id, path, destination_node_id));
					path.pop();
				}
			}
		}
		return found_nodes;
	}

	private IntegerOrInfinity count_paths(String node_id, Stack<String> path, String destination_node_id) {
		if (node_id.equals(destination_node_id)) {

			if (isCyclicPath(path))
				return IntegerOrInfinity.INFINITY;
			else
				return new IntegerOrInfinity(1);
		} else {
			IntegerOrInfinity path_count = new IntegerOrInfinity(0);
			for (LabeledNode labeled_node : class_graph.getLabelledEgdes(node_id)) {

				if (path_count.equals(IntegerOrInfinity.INFINITY)) {
					break;
				}

				String new_node_id = labeled_node.node_id;
				if (!path.contains(new_node_id)) {
					path.push(new_node_id);
					IntegerOrInfinity result = count_paths(new_node_id, path, destination_node_id);
					if (result.equals(IntegerOrInfinity.INFINITY)) {
						path_count = IntegerOrInfinity.INFINITY;
					} else {
						path_count = new IntegerOrInfinity(path_count.int_value + result.int_value);
					}

					path.pop();
				}
			}
			return path_count;
		}
	}

	private boolean isCyclicPath(Stack<String> path) {

		for (String node_id : path) {
			if (cyclic_nodes.contains(node_id))
				return true;
		}

		return false;
	}

}

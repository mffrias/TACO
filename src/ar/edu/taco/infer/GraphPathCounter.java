package ar.edu.taco.infer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ar.edu.taco.infer.Graph.LabeledNode;

/**
 * Counts, for each node of a class graph ({@link ClassGraphBuilder#buildClassGraph}), the simple
 * paths to it from {@code "$Root$"}: the input scope of {@link ScopeInference}. A path through
 * a node that lies on a cycle counts as {@link IntegerOrInfinity#INFINITY}. The graph and the
 * cyclic nodes are kept in fields during {@link #count_all_paths}, so an instance must not be
 * shared between threads. Every simple path is enumerated, once per node, so the cost can grow
 * exponentially with the size of the graph.
 */
public class GraphPathCounter {

	private Graph class_graph;

	private Set<String> cyclic_nodes;

	/**
	 * Returns the number of simple paths from {@code "$Root$"} to each node: 0 for a node that
	 * cannot be reached, 1 for {@code "$Root$"} itself, and {@link IntegerOrInfinity#INFINITY} if
	 * one of the paths goes through a cyclic node. A node is cyclic if a depth-first search from
	 * {@code "$Root$"} towards some node, which stops when it reaches that node, finds it on a
	 * cycle. So a cycle is missed when every node outside it, {@code "$Root$"} apart, lies on
	 * every path from {@code "$Root$"} to it (e.g. a graph with only {@code $Root$ -> C} and
	 * {@code C -> C}): its nodes get finite counts.
	 *
	 * @param class_graph the graph; it must contain {@code "$Root$"} if it has any other node
	 *        (otherwise a {@code NullPointerException})
	 * @return the count of every node of the graph
	 */
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

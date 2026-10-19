package ar.edu.taco.infer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A directed graph over Alloy signature ids, with optionally labeled edges, as built by
 * {@link ClassGraphBuilder}: the class graph (edges labeled with a field or parameter name) and
 * the extension tree (unlabeled edges). In both, the node {@code "$Root$"} is where the paths
 * start: the method's parameters and the static fields, or the top of the signature hierarchy.
 * Edges are not deduplicated ({@link LabeledNode} keeps {@code Object}'s {@code equals}), so
 * adding the same edge twice gives two edges, and two paths in {@link GraphPathCounter}.
 */
class Graph {

	/**
	 * The target end of an edge: the target node and the edge's label ({@code null} for none).
	 */
	public static class LabeledNode {
		public LabeledNode(String label_id, String node_id) {
			this.label_id = label_id;
			this.node_id = node_id;
		}

		String label_id;
		String node_id;
	}

	private Set<String> nodes = new HashSet<String>();
	private Map<String, Set<LabeledNode>> edges = new HashMap<String, Set<LabeledNode>>();

	/**
	 * Adds an edge, and its two nodes if they are new. The edge is added even if an equal one is
	 * already there.
	 *
	 * @param node_src the source node
	 * @param node_target the target node
	 * @param label_id the label, e.g. a field name, or {@code null}
	 */
	public void addEge(String node_src, String node_target, String label_id) {
		addNode(node_src);
		addNode(node_target);

		Set<LabeledNode> target_set = edges.get(node_src);
		target_set.add(new LabeledNode(label_id, node_target));
	}

	private void addNode(String node_id) {
		nodes.add(node_id);
		if (!edges.containsKey(node_id)) {
			edges.put(node_id, new HashSet<LabeledNode>());
		}
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("[");

		boolean start = true;
		for (String node_src : this.edges.keySet()) {
			Set<LabeledNode> out_edges = this.edges.get(node_src);

			for (LabeledNode labeled_node : out_edges) {
				if (!start) {
					buff.append(",");
				} else {
					start = false;
				}
				buff.append(node_src);
				if (labeled_node.label_id != null) {
					buff.append("--");
					buff.append(labeled_node.label_id);
				}
				buff.append("-->");
				buff.append(labeled_node.node_id);
			}
		}

		buff.append("]");
		return buff.toString();
	}

	public Set<String> nodeSet() {
		return this.nodes;
	}

	/**
	 * Returns the outgoing edges of a node: the live set, empty for a node without them.
	 *
	 * @param node_id the node
	 * @return the edges, or {@code null} if the node is not in the graph
	 */
	public Set<LabeledNode> getLabelledEgdes(String node_id) {
		return this.edges.get(node_id);
	}
}

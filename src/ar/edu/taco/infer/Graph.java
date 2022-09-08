package ar.edu.taco.infer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Graph {

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

	public Set<LabeledNode> getLabelledEgdes(String node_id) {
		return this.edges.get(node_id);
	}
}

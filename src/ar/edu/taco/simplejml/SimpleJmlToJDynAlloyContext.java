package ar.edu.taco.simplejml;


import java.util.HashSet;
import java.util.IdentityHashMap;
import org.jmlspecs.checker.JmlNode;
import ar.edu.jdynalloy.ast.JDynAlloyASTNode;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;

public class SimpleJmlToJDynAlloyContext {

	private IdentityHashMap<JmlNode, JDynAlloyASTNode> simpleJml_to_JDynAlloy_map = new IdentityHashMap<JmlNode, JDynAlloyASTNode>();
	private IdentityHashMap<JDynAlloyASTNode, JmlNode> jdynalloy_to_simpleJml_map = new IdentityHashMap<JDynAlloyASTNode, JmlNode>();
//	private List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInContracts = new ArrayList<AlloyFormula>();
//	private HashMap<ExprVariable, JType> varsEncodingValueOfArithmeticOperationsInContracts = new HashMap<ExprVariable, JType>();
	
	
	public void record_simpleJml_to_JDynAlloy_mapping(JmlNode simpleJmlNode,
			JDynAlloyASTNode jdynalloyNode) {
		this.simpleJml_to_JDynAlloy_map.put(simpleJmlNode, jdynalloyNode);
		this.jdynalloy_to_simpleJml_map.put(jdynalloyNode, simpleJmlNode);
	}

//	public void recordPredsEncodingValueOfArithmeticOperationsInContracts(ArrayList<AlloyFormula> factCandidates){
//		this.predsEncodingValueOfArithmeticOperationsInContracts.addAll(factCandidates);
//	}
	
	public JDynAlloyASTNode get_jdynalloy_node(JmlNode simpleJml_node) {
		return this.simpleJml_to_JDynAlloy_map.get(simpleJml_node);
	}

	public boolean contains_simpleJml_node_map(JmlNode simpleJml_node) {
		return this.simpleJml_to_JDynAlloy_map.containsKey(simpleJml_node);
	}

	public boolean contains_jdynalloy_node_map(JDynAlloyASTNode jdynalloy_node) {
		return this.jdynalloy_to_simpleJml_map.containsKey(jdynalloy_node);
	}

	public JmlNode get_simpleJml_node(JDynAlloyASTNode jdynalloy_node) {
		return this.jdynalloy_to_simpleJml_map.get(jdynalloy_node);
	}

}

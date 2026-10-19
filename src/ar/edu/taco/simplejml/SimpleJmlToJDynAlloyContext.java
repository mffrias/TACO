package ar.edu.taco.simplejml;


import java.util.HashSet;
import java.util.IdentityHashMap;
import org.jmlspecs.checker.JmlNode;
import ar.edu.jdynalloy.ast.JDynAlloyASTNode;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;

/**
 * Two-way map between nodes of the simplified Java + JML AST and the JDynAlloy nodes they were
 * translated into. {@link JDynAlloyASTVisitor} fills it during {@code engine.SimpleJmlStage}:
 * each class or interface declaration with its {@code JDynAlloyModule}, each method or
 * constructor declaration with its {@code JProgramDeclaration}. {@code jfsl.JfslStage} reads it
 * to find the declaration behind a module or program. Nodes are compared by identity
 * ({@code IdentityHashMap}). There is one instance per {@link JavaToJDynAlloyManager}, that is,
 * per TACO run; it holds no static state.
 */
public class SimpleJmlToJDynAlloyContext {

	private IdentityHashMap<JmlNode, JDynAlloyASTNode> simpleJml_to_JDynAlloy_map = new IdentityHashMap<JmlNode, JDynAlloyASTNode>();
	private IdentityHashMap<JDynAlloyASTNode, JmlNode> jdynalloy_to_simpleJml_map = new IdentityHashMap<JDynAlloyASTNode, JmlNode>();
//	private List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInContracts = new ArrayList<AlloyFormula>();
//	private HashMap<ExprVariable, JType> varsEncodingValueOfArithmeticOperationsInContracts = new HashMap<ExprVariable, JType>();
	
	
	/**
	 * Records that a simplified JML node was translated into a JDynAlloy node, in both
	 * directions. Each direction keeps the last node recorded for a key.
	 *
	 * @param simpleJmlNode the declaration in the simplified AST
	 * @param jdynalloyNode the module or program it was translated into
	 */
	public void record_simpleJml_to_JDynAlloy_mapping(JmlNode simpleJmlNode,
			JDynAlloyASTNode jdynalloyNode) {
		this.simpleJml_to_JDynAlloy_map.put(simpleJmlNode, jdynalloyNode);
		this.jdynalloy_to_simpleJml_map.put(jdynalloyNode, simpleJmlNode);
	}

//	public void recordPredsEncodingValueOfArithmeticOperationsInContracts(ArrayList<AlloyFormula> factCandidates){
//		this.predsEncodingValueOfArithmeticOperationsInContracts.addAll(factCandidates);
//	}
	
	/**
	 * Returns the JDynAlloy node recorded for a simplified JML node.
	 *
	 * @param simpleJml_node the declaration in the simplified AST
	 * @return the module or program, or {@code null} if none was recorded
	 */
	public JDynAlloyASTNode get_jdynalloy_node(JmlNode simpleJml_node) {
		return this.simpleJml_to_JDynAlloy_map.get(simpleJml_node);
	}

	/**
	 * Returns whether a JDynAlloy node was recorded for a simplified JML node.
	 *
	 * @param simpleJml_node the declaration in the simplified AST
	 * @return {@code true} if {@link #record_simpleJml_to_JDynAlloy_mapping} was called with it
	 */
	public boolean contains_simpleJml_node_map(JmlNode simpleJml_node) {
		return this.simpleJml_to_JDynAlloy_map.containsKey(simpleJml_node);
	}

	/**
	 * Returns whether a simplified JML node was recorded for a JDynAlloy node.
	 *
	 * @param jdynalloy_node a module or program
	 * @return {@code true} if {@link #record_simpleJml_to_JDynAlloy_mapping} was called with it
	 */
	public boolean contains_jdynalloy_node_map(JDynAlloyASTNode jdynalloy_node) {
		return this.jdynalloy_to_simpleJml_map.containsKey(jdynalloy_node);
	}

	/**
	 * Returns the simplified JML node a JDynAlloy node was translated from.
	 *
	 * @param jdynalloy_node a module or program
	 * @return the declaration, or {@code null} if none was recorded
	 */
	public JmlNode get_simpleJml_node(JDynAlloyASTNode jdynalloy_node) {
		return this.jdynalloy_to_simpleJml_map.get(jdynalloy_node);
	}

}

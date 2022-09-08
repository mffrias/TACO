package edu.mit.csail.sdg.annotations.spec;

import org.antlr.runtime.tree.Tree;


public abstract class PublicVisitor<N,M> extends Visitor<N, M> {

	public N publicVisit(M env, Tree tree) {
		return this.visit(env, tree);
	}
}

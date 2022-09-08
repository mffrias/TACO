package ar.edu.taco.stryker.api.impl;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class StrykerDuplicateVariablesVanisherASTVisitor extends ASTVisitor {

    private final ASTRewrite rewrite;
    private Set<String> variables;
    public StrykerDuplicateVariablesVanisherASTVisitor(final AST ast) {
        super();
        this.rewrite = ASTRewrite.create(ast);
    }
    
    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }
    
    @Override
    public boolean visit(VariableDeclarationStatement node) {
        @SuppressWarnings("unchecked")
        List<VariableDeclarationFragment> fragments = node.fragments();
        int size = fragments.size();
        for (VariableDeclarationFragment fragment : fragments) {
            if (variables.contains(fragment.getName().toString())) {
                rewrite.remove(fragment, null);
                size -= 1;
            } else {
                variables.add(fragment.getName().toString());
            }
        }
        if (size == 0) {
            rewrite.remove(node, null);
        }

        return false;
    }
    
    public ASTRewrite getRewrite() {
        return rewrite;
    }
}

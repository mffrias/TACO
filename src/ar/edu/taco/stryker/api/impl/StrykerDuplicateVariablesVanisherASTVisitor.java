package ar.edu.taco.stryker.api.impl;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

/**
 * JDT visitor that removes repeated local variable declarations. Any declaration fragment whose name has
 * already been seen (in the set given to {@link #setVariables(Set)}) is removed together with its
 * initializer, and a declaration statement left without fragments is removed entirely. The edits are
 * collected in {@link #getRewrite()}.
 * <p>
 * It appears intended for sequential traces, where a declaration inside a loop body is recorded once
 * per iteration. Only {@link StrykerJavaFileInstrumenter#replaceMethodBodies} uses it, and that method
 * is currently unused (no callers in this repository). This class is therefore effectively unused too.
 */
public class StrykerDuplicateVariablesVanisherASTVisitor extends ASTVisitor {

    private final ASTRewrite rewrite;
    private Set<String> variables;
    /**
     * @param ast AST of the compilation unit to rewrite
     */
    public StrykerDuplicateVariablesVanisherASTVisitor(final AST ast) {
        super();
        this.rewrite = ASTRewrite.create(ast);
    }
    
    /**
     * Sets the names already declared. The visitor adds every new name it sees to this set.
     *
     * @param variables mutable set of variable names
     */
    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }
    
    /**
     * Removes the fragments of {@code node} whose names were already seen, records the other names, and
     * removes the whole statement if no fragment is left.
     *
     * @return {@code false}, so that nested nodes are not visited
     */
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
    
    /**
     * @return the rewrite collecting the removals; the caller applies it to the parsed document
     */
    public ASTRewrite getRewrite() {
        return rewrite;
    }
}

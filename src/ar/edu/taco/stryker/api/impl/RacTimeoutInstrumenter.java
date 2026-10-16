package ar.edu.taco.stryker.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ar.edu.taco.utils.FileUtils;

/**
 * Inserts {@link RacTimeout#check()} calls into a mutant batch before JML4C compiles it, so that
 * a RAC run that times out can be ended by interrupting its thread (see {@link RacTimeout}).
 *
 * <p>A call goes at the start of every method body (not constructors, whose first statement may
 * have to be {@code super(...)} or {@code this(...)}) and of every loop body ({@code while},
 * {@code do}, {@code for}, enhanced {@code for}); a loop body that is not a block is wrapped in
 * one. The calls are inserted on existing lines, so line numbers do not change:
 * {@link MuJavaController} maps JML4C's error lines back to mutants by line number.
 */
public final class RacTimeoutInstrumenter {

    private static final Logger log = Logger.getLogger(RacTimeoutInstrumenter.class);

    static final String CHECK = "ar.edu.taco.stryker.api.impl.RacTimeout.check();";

    private RacTimeoutInstrumenter() {
    }

    /**
     * Instruments {@code filename} in place. If the file cannot be read or parsed, it is left as it
     * is: its runs can then only be ended by the fallback of {@link RacTimeout#endTimedOutRun}.
     *
     * @param filename a Java source file
     */
    public static void instrument(String filename) {
        String source;
        try {
            source = FileUtils.readFile(filename);
        } catch (IOException e) {
            log.warn("Not instrumenting " + filename + " for RAC timeouts: " + e);
            return;
        }
        String instrumented = instrumentSource(source);
        if (instrumented == null) {
            log.warn("Not instrumenting " + filename + " for RAC timeouts: it does not parse");
            return;
        }
        try {
            FileUtils.writeToFile(filename, instrumented);
        } catch (IOException e) {
            log.warn("Could not write the instrumented " + filename + ": " + e);
        }
    }

    /**
     * Returns {@code source} with the checks inserted, or {@code null} if it has syntax errors.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static String instrumentSource(String source) {
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
        parser.setCompilerOptions(options);
        parser.setSource(source.toCharArray());
        CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        for (IProblem problem : unit.getProblems()) {
            if (problem.isError()) {
                return null;
            }
        }

        final CompilationUnit root = unit;
        final List<Insertion> insertions = new ArrayList<Insertion>();
        unit.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                if (!node.isConstructor() && node.getBody() != null) {
                    insertions.add(new Insertion(node.getBody().getStartPosition() + 1, " " + CHECK));
                }
                return true;
            }

            @Override
            public boolean visit(WhileStatement node) {
                loopBody(node.getBody());
                return true;
            }

            @Override
            public boolean visit(DoStatement node) {
                loopBody(node.getBody());
                return true;
            }

            @Override
            public boolean visit(ForStatement node) {
                loopBody(node.getBody());
                return true;
            }

            @Override
            public boolean visit(EnhancedForStatement node) {
                loopBody(node.getBody());
                return true;
            }

            private void loopBody(Statement body) {
                if (body instanceof Block) {
                    insertions.add(new Insertion(body.getStartPosition() + 1, " " + CHECK));
                } else {
                    // Before the body's leading comments, so that a JML annotation such as
                    // "//@ loop_invariant" still comes right before the loop it belongs to.
                    insertions.add(new Insertion(root.getExtendedStartPosition(body), "{ " + CHECK + " "));
                    insertions.add(new Insertion(body.getStartPosition() + body.getLength(), " }"));
                }
            }
        });

        // Apply from the end, so that earlier offsets stay valid. Insertions at the same offset
        // are all " }" (closing a body that ends where an enclosing one ends), so their relative
        // order does not matter.
        Collections.sort(insertions, new Comparator<Insertion>() {
            @Override
            public int compare(Insertion a, Insertion b) {
                return b.offset - a.offset;
            }
        });
        StringBuilder result = new StringBuilder(source);
        for (Insertion insertion : insertions) {
            result.insert(insertion.offset, insertion.text);
        }
        return result.toString();
    }

    private static final class Insertion {
        final int offset;
        final String text;

        Insertion(int offset, String text) {
            this.offset = offset;
            this.text = text;
        }
    }
}

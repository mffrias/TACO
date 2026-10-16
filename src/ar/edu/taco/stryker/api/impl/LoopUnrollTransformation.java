package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import recoder.CrossReferenceServiceConfiguration;
import recoder.ParserException;
import recoder.convenience.TreeWalker;
import recoder.java.CompilationUnit;
import recoder.java.PrettyPrinter;
import recoder.java.SourceVisitor;
import recoder.java.Statement;
import recoder.java.StatementBlock;
import recoder.java.declaration.MethodDeclaration;
import recoder.java.declaration.VariableDeclaration;
import recoder.java.statement.If;
import recoder.java.statement.Then;
import recoder.java.statement.While;
import recoder.kit.Transformation;
import recoder.list.generic.ASTArrayList;
import recoder.list.generic.ASTList;
import ar.edu.taco.utils.FileUtils;

/**
 * Source-level loop unrolling with RECODER. Used only by
 * {@link DarwinistController#computateFeedback} to build the loop-free ("sequential") version of a
 * mutant that variabilization works on ({@code M_U} in the thesis).
 *
 * <p>Every {@code while (g) body} in a method whose name contains the given method name is replaced
 * by a block of {@code unroll} copies of {@code if (g) body}; the loop's own comments are attached
 * to the {@code if}. {@code unroll} is TACO's loop-unroll bound. Nothing asserts or assumes that
 * the guard is false after the last copy, so executions that need more iterations just skip the
 * remaining ones. Nested loops are handled by walking the tree again until a pass makes no
 * replacement. Only {@code while} loops are unrolled: {@code for}, enhanced {@code for} and
 * {@code do} loops are left as they are, so a statement inside them is later variabilized with
 * one fresh variable for all iterations.
 *
 * <p>The result is printed with {@link FajitaPrettyPrinter} (4-space indentation). The class keeps
 * mutable static state ({@link #runAgain}) and is therefore not thread-safe; its only caller is
 * synchronized. Neither the constructor nor the {@link Transformation} superclass is used.
 */
public class LoopUnrollTransformation extends Transformation {

    /** Set by the visitor after each replacement so that {@link #transform} walks the tree again. */
    private static boolean runAgain = true;

    /**
     * Reads {@code sourcepath}, unrolls the {@code while} loops of the methods whose name contains
     * {@code methodName} and writes the pretty-printed unit to {@code destpath}, which may be the
     * same file (an existing file is deleted first and parent directories are created). A parse
     * error is only printed, after which {@link #transform} receives a {@code null} unit.
     *
     * @param unroll number of copies of each loop body
     * @param methodName substring of the names of the methods to transform
     * @param sourcepath file to read
     * @param destpath file to write
     * @throws IOException if reading or writing fails
     */
    public static void javaUnroll(int unroll, String methodName, String sourcepath, String destpath) throws IOException {
        String contents = FileUtils.readFile(sourcepath);

        CrossReferenceServiceConfiguration recoder = new CrossReferenceServiceConfiguration();

        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = recoder.getProgramFactory().parseCompilationUnit(contents);
        } catch (ParserException e) {
            e.printStackTrace();
        }

        runAgain = true;
        transform(unroll, methodName, compilationUnit);

        LoopUnrollPrettyPrinter.print(destpath, compilationUnit);

    }

    /**
     * Walks {@code compilationUnit} with a {@link LoopUnrollTransformationVisitor} until a pass
     * makes no replacement, then leaves {@link #runAgain} set to {@code true} for the next call.
     */
    public static void transform(int unroll, String methodName, CompilationUnit compilationUnit) {
        while(runAgain) {
            runAgain = false;
            TreeWalker treeWalker = new TreeWalker(compilationUnit);
            SourceVisitor transformVisitor = new LoopUnrollTransformationVisitor(unroll, methodName);
            while (treeWalker.next()) {
                treeWalker.getProgramElement().accept(transformVisitor);
            }
        }
        runAgain = true;

    }

    public LoopUnrollTransformation() {
        super(new CrossReferenceServiceConfiguration());
    }

    /**
     * Replaces each {@code while} loop it visits by {@code unroll} copies of the corresponding
     * {@code if}. {@code shouldExamine} is updated whenever the tree walk reaches a method
     * declaration, so only loops in methods whose name contains {@code unrollMethodName} are
     * transformed.
     */
    public static class LoopUnrollTransformationVisitor extends SourceVisitor {

        private int unroll = 0;

        private boolean shouldExamine = false;
        
        private String unrollMethodName;

        public LoopUnrollTransformationVisitor(int unroll, String unrollMethodName) {
            this.unroll = unroll;
            this.unrollMethodName = unrollMethodName;
        }

        @Override
        protected void visitVariableDeclaration(VariableDeclaration x) {
            if(!shouldExamine) {
                return;
            }
            super.visitVariableDeclaration(x);
        }

        @Override
        public void visitMethodDeclaration(MethodDeclaration x) {
            super.visitMethodDeclaration(x);
            String methodName = x.getIdentifier().getText();
            shouldExamine = methodName.contains(unrollMethodName);
        }

        @Override
        public void visitWhile(While x) {
            if(!shouldExamine) {
                return;
            }
            If iff = new If(x.getGuard(), new Then(x.getBody()));
            iff.setComments(x.getComments());

            ASTList<Statement> replacement = new ASTArrayList<>();
//            StatementBlock parent = (StatementBlock) x.getASTParent();
//            ASTList<Statement> block = ((StatementBlock) x.getASTParent()).getBody();

            int index = 0;

            for (int i = 0; i < unroll; i++) {
                replacement.add(index++, iff.deepClone());
            }
            doReplace(x, new StatementBlock(replacement));
            LoopUnrollTransformation.runAgain = true;
        }

    }

    /**
     * Entry point used by {@link LoopUnrollTransformation#javaUnroll}: its static {@code print}
     * method prints with a {@link FajitaPrettyPrinter}. This class itself is never instantiated.
     */
    public static class LoopUnrollPrettyPrinter extends PrettyPrinter {

        protected LoopUnrollPrettyPrinter(FileWriter writer, Properties properties) {
            super(writer, properties);
        }

        public static void print(String outputFile, CompilationUnit compilationUnit) throws IOException {
            File file = new File(outputFile);
            if (file.exists())
                file.delete();
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            Properties properties = new Properties();
            properties.put(INDENTATION_AMOUNT, "4");
            FajitaPrettyPrinter printer = new FajitaPrettyPrinter(writer, properties);
            printer.visitCompilationUnit(compilationUnit);
            writer.close();
        }

        @Override
        public boolean getBooleanProperty(String key) {
            return false;
        }

    }
    
    /**
     * RECODER pretty printer that answers {@code false} for every boolean formatting property.
     * Its own static {@code print} method is not called anywhere in {@code src/} or
     * {@code unittest/}.
     */
    public static class FajitaPrettyPrinter extends PrettyPrinter {

        public FajitaPrettyPrinter(FileWriter writer, Properties properties) {
            super(writer, properties);
        }

        public static void print(String outputFile, CompilationUnit compilationUnit) throws IOException {
            File file = new File(outputFile);
            if (file.exists()) file.delete();
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            Properties properties = new Properties();
            properties.put(INDENTATION_AMOUNT, "4");
            FajitaPrettyPrinter printer = new FajitaPrettyPrinter(writer, properties);
            printer.visitCompilationUnit(compilationUnit);
            writer.close();
        }

        @Override
        public boolean getBooleanProperty(String key) {
            return false;
        }

    }
}


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

public class LoopUnrollTransformation extends Transformation {

    private static boolean runAgain = true;

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


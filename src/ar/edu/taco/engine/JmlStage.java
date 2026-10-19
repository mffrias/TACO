/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.engine;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jmlspecs.checker.JmlTypeDeclaration;
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JTypeDeclarationType;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.jml.ASTSimplifierManager;
import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.jml.parser.JmlParser;
import ar.edu.taco.utils.FileUtils;

/**
 * @author ggasser
 *
 */
public class JmlStage implements ITacoStage {

	static final private String OUTPUT_SIMPLIFIED_JAVA_EXTENSION = ".java";
//	static final private String FILE_SEPARATOR = File.separator;

	@SuppressWarnings("unused")
	private List<JmlTypeDeclaration> asts;

	private List<JCompilationUnitType> compilation_units;

	private JmlToSimpleJmlContext jmlToSimpleJmlContext;
	private List<JCompilationUnitType> simplified_compilation_units;

	/**
	 * Static: whether TACO computes the specified output for a fixed input (the second run of
	 * {@code checkAndRunSpecIfFaulty}, and every later run in the JVM, see below).
	 * {@link #execute()} copies it into
	 * {@code ASTSimplifierManager.isCheckAndAfterRunSpec}, which then adds
	 * {@code methodBodySimplifier}: every method body of every class becomes
	 * {@code return <default value>}. {@code TacoMain.run} sets it before each run from
	 * {@code TacoMain.isCheckAndRunSpecAssertionSupport}, which is never reset. See
	 * docs/SPEC-OUTPUT.md ("checkAndRunSpecIfFaulty").
	 */
	public static boolean  isCheckAndAfterRunSpec = false;

	/**
	 * Creates the JML simplification stage, the first after parsing in
	 * {@code TacoMain.run}.
	 *
	 * @param compilation_units the Java + JML compilation units parsed by
	 *        {@code JmlParser} from {@code jmlParser.sourcePathStr}: the class to check and
	 *        the classes of {@code classes} or {@code relevantClasses}
	 */
	public JmlStage(List<JCompilationUnitType> compilation_units) {
		this.asts = null;
		this.compilation_units = compilation_units;
		this.jmlToSimpleJmlContext = null;
	}

	/**
	 * Simplifies the compilation units and parses the result again. With a new
	 * {@code ASTSimplifierManager}, each unit goes through its simplifiers, in the order its
	 * constructor lists them (from {@code ForRemoverVisitor} to
	 * {@code ReturnStatementWrapperVisitor}, plus {@code methodBodySimplifier} when
	 * {@link #isCheckAndAfterRunSpec}). Each simplified unit
	 * is written, pretty-printed, to {@code <output_dir>/<package path>/<Class>.java} (e.g.
	 * {@code output/icse/singlylinkedlist/set1/SinglyLinkedListContains1Bug25D.java}), and
	 * these files are parsed again, one at a time, with the {@code JmlParser} singleton
	 * (whether each parse succeeds is not checked). The re-parsed units are
	 * {@link #get_simplified_compilation_units()}.
	 *
	 * <p>Side effects: sets the static {@code ASTSimplifierManager.isCheckAndAfterRunSpec};
	 * writes under {@code output_dir}; reinitializes the {@code JmlParser} singleton, which
	 * afterwards holds only the last simplified file's units.
	 *
	 * @throws TacoException if the canonical path of {@code output_dir} cannot be computed
	 * @throws RuntimeException if a simplified file cannot be written
	 */
	@Override
	public void execute() {
		ASTSimplifierManager.isCheckAndAfterRunSpec = isCheckAndAfterRunSpec;

		ASTSimplifierManager aAstSimplifierManager = new ASTSimplifierManager();

		List<JCompilationUnitType> newAsts = simplify_compilation_units(aAstSimplifierManager);

		List<String> files = write_simplified_compilation_units(newAsts);

		parse_simplified_compilation_units(files);

//		this.simplified_compilation_units = newAsts;

		jmlToSimpleJmlContext = aAstSimplifierManager.getJmlToSimpleJmlContext();
	}

	private void parse_simplified_compilation_units(List<String> files) {
		String canonical_outdir_path;
		try {
			String path = TacoConfigurator.getInstance().getOutputDir() ;
			File output_dir = new File(path);
			output_dir.mkdir();
			canonical_outdir_path = output_dir.getCanonicalPath();
		} catch (IOException e) {
			throw new TacoException("canonical path couldn't be computed " + e.getMessage());
		}

		simplified_compilation_units = new LinkedList<>();
		for (String file : files) {
			LinkedList<String> theEncapsulatedFileName = new LinkedList<>();
			theEncapsulatedFileName.add(file);
			JmlParser theParserInstance = new JmlParser().getInstance();
			theParserInstance.initialize(canonical_outdir_path, System.getProperty("user.dir") + System.getProperty("file.separator") + "bin" /* Unused */,
					theEncapsulatedFileName);
			simplified_compilation_units.addAll(theParserInstance.getCompilationUnits());
		}
//		JmlParser theParserInstance = ((TacoThread)(Thread.currentThread())).threadParserInstance.getInstance();
//		theParserInstance.initialize(canonical_outdir_path, System.getProperty("user.dir") + System.getProperty("file.separator") + "bin" /* Unused */,
//				files);

		//simplified_compilation_units = theParserInstance.getCompilationUnits();

	}

	private List<String> write_simplified_compilation_units(List<JCompilationUnitType> newAsts) {
		List<String> files = new LinkedList<String>();
		String canonical_path = makeCanonicalPath();

		for (JCompilationUnitType compilation_unit : newAsts) {
			assert compilation_unit.typeDeclarations().length==1;
			JTypeDeclarationType typeDeclaration = compilation_unit.typeDeclarations()[0];
			String filename = canonical_path + java.io.File.separator + typeDeclaration.getCClass().getJavaName().replaceAll("\\.", "/");
			files.add(typeDeclaration.getCClass().getJavaName());
			try {
				FileUtils.writeToFile(filename + OUTPUT_SIMPLIFIED_JAVA_EXTENSION, JavaAndJmlPrettyPrint2.print(compilation_unit));
			} catch (IOException e) {
				throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
			}
		}
		return files;
	}

	private List<JCompilationUnitType> simplify_compilation_units(ASTSimplifierManager aAstSimplifierManager) {

		if (JmlStage.isCheckAndAfterRunSpec)
			ASTSimplifierManager.isCheckAndAfterRunSpec = true;

		List<JCompilationUnitType> newAsts = new LinkedList<JCompilationUnitType>();

		for (JCompilationUnitType compilation_unit : this.compilation_units) {

			List<JCompilationUnitType> new_compilation_units = aAstSimplifierManager.simplify(compilation_unit);
			newAsts.addAll(new_compilation_units);
		}
		return newAsts;
	}

	private String makeCanonicalPath() {
		String output_dir = TacoConfigurator.getInstance().getOutputDir();
		File out_dir_dir = new File(output_dir);

		if (!out_dir_dir.exists()) {
			out_dir_dir.mkdirs();
		}

		String canonical_path;
		try {
			canonical_path = out_dir_dir.getCanonicalPath();
		} catch (IOException e1) {
			throw new TacoException("path couldn't be found: " + out_dir_dir);
		}
		return canonical_path;
	}

	/**
	 * @return the asts
	 */
	public List<JCompilationUnitType> get_simplified_compilation_units() {
		return simplified_compilation_units;
	}

	/**
	 * Returns what the simplification recorded for the next translation steps (the
	 * {@code JmlToSimpleJmlContext} filled by the simplifiers, e.g. by
	 * {@code FNBlockVisitor}); {@code TacoMain} passes it to the JFSL stage.
	 *
	 * @return the context of the last {@link #execute()}, or {@code null} before it
	 */
	public JmlToSimpleJmlContext getJmlToSimpleJmlContext() {
		return jmlToSimpleJmlContext;
	}

}

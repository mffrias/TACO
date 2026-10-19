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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.binding.fieldcollector.FieldCollectorVisitor;
import ar.edu.jdynalloy.binding.symboltable.FieldDescriptor;
import ar.edu.jdynalloy.binding.symboltable.SymbolTable;
import ar.edu.jdynalloy.parser.JDynAlloyParserManager;
import ar.edu.jdynalloy.parser.JDynAlloyParsingException;
import ar.edu.jdynalloy.parser.JDynAlloyProgramParseContext;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

/**
 * Parses JDynAlloy source files ({@code .djals}) into modules, to be added to those that
 * TACO translated from Java. It reads the files of the key {@code jdynalloy.parser.inputFiles}
 * and the class-path resources of {@code jdynalloy.parser.inputResources} plus those that
 * {@link PrecompiledModules#execute()} registered just before
 * ({@code TacoConfigurator.getJDynAlloyParserInputResources()}): the built-in modules kept as
 * {@code .djals} under {@code src/ar/edu/taco/engine/precompiledmodules/} ({@code java.util}
 * collections, integer and object arrays, ...). {@code TacoMain.run} runs it when
 * {@code jdynalloy.parser.enabled} is on (the default, and on in
 * {@code config/genericTest.properties}); with it off, those built-in modules are missing
 * from the analysis. Holds no static state.
 */
public class JDynAlloyParsingStage implements ITacoStage {
	private List<JDynAlloyModule> parsedModules;
	private List<JDynAlloyModule> givenModules;
	private boolean javaArithmetic;
	
	/**
	 * Returns the value given to {@link #setJavaArithmetic(boolean)}; the stage does not use
	 * it.
	 *
	 * @return the stored flag, {@code false} by default
	 */
	public boolean getJavaArithmetic(){
		return javaArithmetic;
	}
	
	/**
	 * Stores a Java-arithmetic flag that nothing reads: {@link #execute()} takes
	 * {@code useJavaArithmetic} from the configuration, and no caller sets it.
	 *
	 * @param b the flag
	 */
	public void setJavaArithmetic(boolean b){
		this.javaArithmetic = b;
	}
	

	/**
	 * Returns the modules parsed by the second pass of {@link #execute()}.
	 *
	 * @return the parsed modules; empty before {@link #execute()}
	 */
	public List<JDynAlloyModule> getParsedModules() {
		return parsedModules;
	}

	/**
	 * Creates the stage.
	 *
	 * @param givenModules the modules built so far (translated from Java, built-in and
	 *        static-fields modules); their fields are known to the parser of the second pass.
	 *        They are not changed, and not part of {@link #getParsedModules()}
	 */
	public JDynAlloyParsingStage(List<JDynAlloyModule> givenModules) {
		this.givenModules = givenModules;
		this.parsedModules = new ArrayList<JDynAlloyModule>();
	}

	/**
	 * Parses the configured files and resources twice. The first pass parses them without a
	 * context, only to collect, with the given modules, every field
	 * ({@code FieldCollectorVisitor}, with {@code useJavaArithmetic} from the configuration).
	 * The second pass parses them again with a context that knows those fields, and its
	 * modules are the result. For files (not resources), the first module of each file gets
	 * empty lists of arithmetic-constraint variables and predicates.
	 *
	 * @throws TacoException if a file is not found; a {@code JDynAlloyParsingException}
	 *         (unchecked) from the parser propagates
	 */
	@Override
	public void execute() {

		try {
			// first pass
			List<JDynAlloyModule> firstPassModules = new ArrayList<JDynAlloyModule>();
			makeParserPass(firstPassModules, null);
			firstPassModules.addAll(this.givenModules);

			// retrieve all fields
			SymbolTable symbolTable = new SymbolTable();
			boolean isJavaArithmetic = TacoConfigurator.getInstance().getUseJavaArithmetic();
			symbolTable.setJavaArithmetic(isJavaArithmetic);
			FieldCollectorVisitor fieldCollectorVisitor = new FieldCollectorVisitor(symbolTable, TacoConfigurator.getInstance().getUseJavaArithmetic());
			for (JDynAlloyModule aModule : firstPassModules) {
				aModule.accept(fieldCollectorVisitor);
				
			}

			Set<AlloyVariable> fields = new HashSet<AlloyVariable>();
			for (FieldDescriptor fieldDescriptor : symbolTable.getFieldSet()) {
				String aFieldName = fieldDescriptor.getFieldName();
				AlloyVariable variable = AlloyVariable.buildAlloyVariable(aFieldName);
				fields.add(variable);
			}

			// create new context
			JDynAlloyProgramParseContext ctx = new JDynAlloyProgramParseContext(new HashSet<AlloyVariable>(), fields, true);

			// second pass modules
			makeParserPass(this.parsedModules, ctx);

		} catch (FileNotFoundException e) {
			throw new TacoException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private void makeParserPass(List<JDynAlloyModule> resultModules, JDynAlloyProgramParseContext ctx) throws JDynAlloyParsingException, FileNotFoundException {
		List<String> sourceFiles = (List<String>)TacoConfigurator.getInstance().getList(TacoConfigurator.JDYNALLOY_PARSER_INPUT_FILES, new ArrayList<String>());
		for (String jDynalloySourceFile : sourceFiles) {
			List<JDynAlloyModule> parsedModules = JDynAlloyParserManager.parseModulesFile(jDynalloySourceFile, ctx);
			parsedModules.get(0).setPredsEncodingValueOfArithmeticOperationsInObjectInvariants(new LinkedList<AlloyFormula>());
			parsedModules.get(0).setVarsEncodingValueOfArithmeticOperationsInObjectInvariants(new AlloyTyping());
			resultModules.addAll(parsedModules);
		}

		Set<String> resourceFiles = TacoConfigurator.getInstance().getJDynAlloyParserInputResources();
		for (String jDynalloySourceFile : resourceFiles) {
			List<JDynAlloyModule> parsedModules = JDynAlloyParserManager.parseModulesResource(jDynalloySourceFile, ctx);
			resultModules.addAll(parsedModules);
		}
	}
}

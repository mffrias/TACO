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
package ar.edu.taco.jml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlSourceMethod;
import org.multijava.mjc.JCompilationUnitType;

import ar.edu.taco.jml.block.BlockSimplifier;
import ar.edu.taco.jml.cast.CastSClassVisitor;
import ar.edu.taco.jml.defaultconstructor.DefaultConstructorSimplifier;
import ar.edu.taco.jml.expression.ESBlockVisitor;
import ar.edu.taco.jml.fieldnames.FNBlockVisitor;
import ar.edu.taco.jml.initialization.FieldInitializerSimplifier;
import ar.edu.taco.jml.invoke.ActualParameterNormalizerVisitor;
import ar.edu.taco.jml.literal.LiteralBlockVisitor;
import ar.edu.taco.jml.loop.DoWhileBlockVisitor;
import ar.edu.taco.jml.loop.LSBlockVisitor;
import ar.edu.taco.jml.loop.WhileBlockVisitor;
import ar.edu.taco.jml.loop.WhileRemoverSimplifier;
import ar.edu.taco.jml.static_calls.QualifyStaticCallsVisitor;
import ar.edu.taco.jml.varnames.VNBlockVisitor;
import ar.edu.taco.simplejml.AssumeSimplifierVisitor;
import ar.edu.taco.simplejml.GhostFieldsSimplifier;
import ar.edu.taco.simplejml.ShortcutRemoverVisitor;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class ASTSimplifierManager {
	private static Logger log = Logger.getLogger(ASTSimplifierManager.class);
	private List<JmlAstClonerStatementVisitor> simplifiers;

	private final JmlToSimpleJmlContext jmlToSimpleJmlContext;

	public ASTSimplifierManager() {
		this.jmlToSimpleJmlContext = new JmlToSimpleJmlContext();
		// order of simplifiers
		this.simplifiers = new ArrayList<JmlAstClonerStatementVisitor>();
	
	 	simplifiers.add(new WhileRemoverSimplifier());
		simplifiers.add(new BlockSimplifier());
		simplifiers.add(new ShortcutRemoverVisitor());
		simplifiers.add(new GhostFieldsSimplifier());
		simplifiers.add(new DefaultConstructorSimplifier());
		simplifiers.add(new FieldInitializerSimplifier());
		simplifiers.add(new LSBlockVisitor());
		simplifiers.add(new WhileBlockVisitor());
		simplifiers.add(new DoWhileBlockVisitor());
		simplifiers.add(new SpecMethodCallRemoverVisitor());
		simplifiers.add(new VNBlockVisitor());
		simplifiers.add(new FNBlockVisitor(this.jmlToSimpleJmlContext));
		simplifiers.add(new CastSClassVisitor());
		simplifiers.add(new LiteralBlockVisitor());
		simplifiers.add(new ESBlockVisitor());
		simplifiers.add(new QualifyStaticCallsVisitor());
		simplifiers.add(new ActualParameterNormalizerVisitor());
		simplifiers.add(new AssumeSimplifierVisitor());
		simplifiers.add(new ReturnStatementWrapperVisitor());
	}

	public JmlToSimpleJmlContext getJmlToSimpleJmlContext() {
		return this.jmlToSimpleJmlContext;
	}

	public List<JCompilationUnitType> simplify(JCompilationUnitType input_compilation_unit) {
		log.debug("Simplifying Compilation Unit: " + input_compilation_unit.fileNameIdent());

		JCompilationUnitType compilation_unit = input_compilation_unit;
		for (JmlAstClonerStatementVisitor simplifier : simplifiers) {
			compilation_unit.accept(simplifier);
			compilation_unit = (JCompilationUnitType) simplifier.getStack().pop();
		}


		log.debug("Simplifier Ends for compilation unit:" + input_compilation_unit.fileNameIdent());

		return Collections.<JCompilationUnitType> singletonList(compilation_unit);
	}

}

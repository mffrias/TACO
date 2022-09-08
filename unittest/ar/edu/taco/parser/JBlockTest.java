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
package ar.edu.taco.parser;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JCreateObject;
import ar.edu.jdynalloy.ast.JProgramCall;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JWhile;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;

public class JBlockTest extends JDynAlloyParserTestBase {

	public void testJBlock() throws RecognitionException, TokenStreamException {
		String text = "{ " + "\n" + "skip; " + "\n" + "assume TruePred[];" + "\n" + "}";
		JBlock block = this.initializeParser(text).jBlock(this.getCtx());
		assertEquals(2, block.getBlock().size());
		assertTrue(block.getBlock().get(0) instanceof JSkip);
		assertTrue(block.getBlock().get(1) instanceof JAssume);
	}

	public void testJBlockWithProblems() throws RecognitionException, TokenStreamException {
		String text = "{ " + "\n" 
					  + "call Constructor[throw,throw];" + "\n" 
					   + "createObject<java_lang_IndexOutOfBoundsException>[throw];" + "\n"
					    + "}";
		JBlock block = this.initializeParser(text).jBlock(this.getCtx());
		assertEquals(2, block.getBlock().size());
		assertTrue(block.getBlock().get(0) instanceof JProgramCall);
		assertTrue(block.getBlock().get(1) instanceof JCreateObject);
	}

	public void testJBlockWithProblemsTwo() throws RecognitionException, TokenStreamException {
		String text = "{ " + "\n" 
					  + "value:=1;" + "\n" 
					   + "while TruePred[] { skip; } ;" + "\n"
					    + "}";
		JBlock block = this.initializeParser(text).jBlock(this.getCtx());
		assertEquals(2, block.getBlock().size());
		assertTrue(block.getBlock().get(0) instanceof JAssignment);
		assertTrue(block.getBlock().get(1) instanceof JWhile);
	}

	   

}

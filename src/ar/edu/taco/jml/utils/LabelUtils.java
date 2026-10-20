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
package ar.edu.taco.jml.utils;

/**
 * Names for the branch labels of the JDynAlloy {@code if} statements that TACO's translation
 * builds ({@code JIfThenElse}'s {@code branchId}, which jDynAlloy turns into the
 * {@code lblpos}/{@code lblneg} test-predicate labels of DynAlloy). Its callers are
 * {@code simplejml.BlockStatementsVisitor} (a boolean assigned a formula) and
 * {@code simplejml.helpers.BlockStatementSolver} (the guard around each statement).
 *
 * <p>Static state: the package-private counters {@code ifLabelCount} (1) and
 * {@code whileLabelCount} (10000), which nothing changes, so every label is {@code if_1}
 * (and {@code while_10000}). Unclear: whether the labels were meant to be distinct, as the names
 * {@code next...Label} and the counters suggest; with one label for every branch they cannot
 * tell branches apart.
 */
public class LabelUtils {

	//Labels
	static int ifLabelCount = 1;  
	static int whileLabelCount = 10000;
	
	/**
	 * Returns {@code if_<ifLabelCount>}, always {@code if_1} (see the class comment).
	 *
	 * @return the label
	 */
	public static String nextIfLabel() {
		return "if_" + ifLabelCount;
	}
	
	/**
	 * Returns {@code while_<whileLabelCount>}, always {@code while_10000}. No caller in TACO.
	 *
	 * @return the label
	 */
	public static String nextWhileLabel() {
		return "while_" + whileLabelCount;
	}
	
}

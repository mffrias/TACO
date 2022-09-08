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
package ar.edu.taco.simplejml.methodinfo;

import org.multijava.mjc.CType;

/**
 * @author elgaby
 *
 */
public class ConcreteMethodInformation implements MethodInformation {
	private boolean isConstructor;
	private boolean isStatic;
	private boolean isModelMethod;
	private CType returnType;
	private String qualifiedReceiverType;
	
	
	/**
	 * @return the isConstructor
	 */
	public boolean isConstructor() {
		return isConstructor;
	}


	/**
	 * @param isConstructor the isConstructor to set
	 */
	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}


	/**
	 * @return the isStatic
	 */
	public boolean isStatic() {
		return isStatic;
	}


	/**
	 * @param isStatic the isStatic to set
	 */
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	
	/**
	 * @return the isModelMethod
	 */
	public boolean isModelMethod() {
		return isModelMethod;
	}


	/**
	 * @param isModelMethod the isModelMethod to set
	 */
	public void setModelMethod(boolean isModelMethod) {
		this.isModelMethod = isModelMethod;
	}


	/**
	 * @return the returnType
	 */
	public CType getReturnType() {
		return returnType;
	}


	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(CType returnType) {
		this.returnType = returnType;
	}


	/**
	 * @return the qualifiedReceiverType
	 */
	public String getQualifiedReceiverType() {
		return qualifiedReceiverType;
	}


	/**
	 * @param qualifiedReturnType the qualifiedReceiverType to set
	 */
	public void setQualifiedReceiverType(String qualifiedReceiverType) {
		this.qualifiedReceiverType = qualifiedReceiverType;
	}


	/* (non-Javadoc)
	 * @see ar.edu.taco.pipeline.javatodynjalloy.methodinfo.MethodInformation#hasReturnType()
	 */
	@Override
	public boolean hasReturnType() {
		return (this.returnType != null && !this.returnType.toVerboseString().equalsIgnoreCase("void"));
	}
}

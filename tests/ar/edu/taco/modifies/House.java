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
package ar.edu.taco.modifies;

public class House {
	
	private /*@ spec_public @*/ int doors; //@ in objectState;
	private /*@ spec_public @*/ int windows; //@ in objectState;
	private /*@ spec_public @*/ Address address; //@ in objectState;
	private /*@ spec_public @*/ String phone;
	
	public House() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the doors
	 */
	public /*@ pure @*/ int getDoors() {
		return doors;
	}

	/*@
	  @ requires aDoors > 0;
	 @*/
	public void setDoors(int aDoors) {
		this.doors = aDoors;
	}
	
	/**
	 * @return the windows
	 */
	public int getWindows() {
		return windows;
	}
	/**
	 * @param windows the windows to set
	 */
	public void setWindows(int aWindows) {
		this.windows = aWindows;
	}
	
	/*@
	  @ requires this.doors > 0;
	  @ requires house.doors > 0;
	  @ ensures \result == house.getDoors() + this.doors;
	  @*/
	public int sumDoors(House house) {
		return house.getDoors() + this.doors;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

}

/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.util;


public class SimpleCircularList {
	Object[] list;
	int currentIndex;
	int size;

	public SimpleCircularList(int size) {
		this.size = size;
		this.list = new Object[size];
	}

	public void add(Object o) {
		list[currentIndex] = o;
		currentIndex++;
	}

	private int pollIndex = -1;

	public Object poll() {
		int temp = pollIndex + 1;
		if(temp > (size - 1)){
			pollIndex = 0;
		} else{
			pollIndex++;
		}
		return list[pollIndex];
	}

	public Object getObjectAt(int index) throws ArrayIndexOutOfBoundsException {
		return list[index];
	}
}

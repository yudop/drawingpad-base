package com.appineering.android.drawingpad.ArtCore.LayerCore;

import android.graphics.PorterDuffXfermode;

public class Layer {
	PorterDuffXfermode mXfer;
	int mIndex;
	String mName;
	public Layer(int index){
		mIndex = index;
	}
	public void moveUp() {
		mIndex--;
	}
	public void moveDown() {
		mIndex++;
	}

}

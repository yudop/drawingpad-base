package com.appineering.android.drawingpad.GuiCore;

import android.graphics.Canvas;

public class DrawingThread extends Thread {
	private boolean mRun = false;
	private DrawingView mDrawingView;
	private Canvas mCanvas;
	
	public DrawingThread(DrawingView view, Canvas canvas){
		mDrawingView = view;
		mCanvas = canvas;
	}
	@Override
	public void run() {
		while (mRun) {
			mDrawingView.onDraw(mCanvas);
		}
	}
}
package com.appineering.android.drawingpad.ArtCore.ToolCore.Shape;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.appineering.android.drawingpad.ArtCore.ToolCore.DrawingTool;

public class RectangleTool extends DrawingTool {
	private Rect mRect;
	private float mX1, mY1, mX2, mY2;
	private int  mA1, mB1, mA2, mB2;

	public RectangleTool(MotionEvent event) {
		super(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mRect = new Rect();
			mX1 = event.getX();
			mY1 = event.getY();
			mA1 = (int) mX1;
			mB1 = (int) mY1;
		}else if (event.getAction() == MotionEvent.ACTION_UP) {
			mX2 = event.getX();
			mY2 = event.getY();
			mA2 = (int) mX2;
			mB2 = (int) mY2;

			mRect.set(mA1, mB1, mA2, mB2);
		}
	}
}

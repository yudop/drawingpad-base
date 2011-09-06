package com.appineering.android.drawingpad.ArtCore.ToolCore.Brush;

import com.appineering.android.drawingpad.ArtCore.ToolCore.DrawingTool;

import android.graphics.Path;
import android.view.MotionEvent;

public class PaintbrushTool extends DrawingTool {
	private float mX, mY, dx, dy;
	private int TOUCH_TOLERANCE;
	private Path mPath;
	
	public PaintbrushTool(MotionEvent event) {
		super(event);
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mPath = new Path();
			mPath.moveTo(event.getX(), event.getY());
			mX = event.getX();
			mY = event.getY();
		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
			dx = Math.abs(event.getX() - mX);
			dy = Math.abs(event.getY() - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (event.getX() + mX)/2, (event.getY() + mY)/2);
				mX = event.getX();
				mY = event.getY();
			}
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			mPath.quadTo(mX, mY, (event.getX() + mX)/2, (event.getY() + mY)/2);
			mX = event.getX();
			mY = event.getY();
			mPath.setLastPoint(event.getX(), event.getY());
		}

	}
}

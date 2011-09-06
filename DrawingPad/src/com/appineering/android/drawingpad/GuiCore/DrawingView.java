package com.appineering.android.drawingpad.GuiCore;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

	public DrawingView(Context context) {
		super(context);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		
			return true;
		}
	

	@Override
	public void onDraw(Canvas canvas) {

	}


}

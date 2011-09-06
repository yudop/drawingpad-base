package com.appineering.android.drawingpad;

import com.appineering.android.drawingpad.GuiCore.DrawingView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class DrawingPadActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DrawingView mDrawingView = new DrawingView(this);
        setContentView(mDrawingView);
    }
}
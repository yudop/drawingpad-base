package com.appineering.android.drawingpad;

import java.io.File;
import java.io.IOException;

import com.appineering.android.drawingpad.GuiCore.DrawingView;
import com.madrobot.graphics.BitmapUtils;
import com.madrobot.io.file.SDCardUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.Toast;

public class DrawingPadActivity extends Activity {
	private Bitmap mBitmap;
	private File mFilePath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         if (SDCardUtils.isMounted() && SDCardUtils.canWrite()) {
        	 mFilePath = new File(Environment.getExternalStorageDirectory().toString() + "/DrawingPad");         }
        mBitmap = BitmapUtils.createBitmapFromAsset(this, "untitled");
        
        try {
			BitmapUtils.writeBitmapToFile(mBitmap, mFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "There was an error creating the drawing file", 5);
		}
        DrawingView mDrawingView = new DrawingView(this);
        setContentView(mDrawingView);
    }
}
package com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Filters;

import com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Core.AndroidImage;


/**
 * @author robert.hinds
 * 
 * class to invert the color of an image
 * 
 * Based on Marvin plugin http://marvinproject.sourceforge.net/en/plugins/invert.html
 * Originally authored by Gabriel Ambr?sio Archanjo
 *
 */
public class InvertFilter implements IAndroidFilter{

	@Override
	public AndroidImage process(AndroidImage imageIn) {
		int r, g, b;
		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				r = (255-(int)imageIn.getRComponent(x, y));
				g = (255-(int)imageIn.getGComponent(x, y));
				b = (255-(int)imageIn.getBComponent(x, y));

				imageIn.setPixelColor(x,y,r,g,b);
			}
		}
		
		return imageIn;
	}

}

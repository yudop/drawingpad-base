package com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Filters;

import com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Core.AndroidImage;



/**
 * @author robert.hinds
 *
 * class to apply an effect to make the image look
 * like it is taken of a TV/monitor (applies lines that 
 * look like the refresh rate)
 * 
 * Based on Marvin plugin http://marvinproject.sourceforge.net/en/plugins/television.html
 * Originally authored by Gabriel Ambr?sio Archanjo
 *
 */
public class MonitorFilter implements IAndroidFilter {

	@Override
	public AndroidImage process(AndroidImage imageIn) {
		int r,g,b;
		for (int x = 0; x < imageIn.getWidth(); x++) {			
			for (int y = 0; y < imageIn.getHeight(); y+=3) {
				
				r=0;
				g=0;
				b=0;
					
				for(int w=0; w<3; w++){
					if(y+w < imageIn.getHeight() ){
						r += (imageIn.getRComponent(x, y+w))/2;
						g += (imageIn.getGComponent(x, y+w))/2;
						b += (imageIn.getBComponent(x, y+w))/2;						
					}
				}
				r = getValidInterval(r);
				g = getValidInterval(g);
				b = getValidInterval(b);
						
				for(int w=0; w<3; w++){
					if(y+w < imageIn.getHeight()){
						if(w == 0){
							imageIn.setPixelColor(x,y+w,r,0,0);
						}
						else if(w ==1){
							imageIn.setPixelColor(x,y+w,0,g,0);
						}
						else if(w==2){
							imageIn.setPixelColor(x,y+w,0,0,b);
						}
					}
				}				
			}
		}
		
		return imageIn;
	}
	
	
	/**
	 * method to calculate an appropriate interval for flicker lines
	 * 
	 * @param a_value
	 * @return
	 */
	public int getValidInterval(int a_value){
		if(a_value < 0) return 0;
		if(a_value > 255) return 255;
		return a_value;
	}

}

package com.sccp.library.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtil {

	public static final int COMPLEX_UNIT_PX = 0x001;
	public static final int COMPLEX_UNIT_DIP = 0x002;
	public static final int COMPLEX_UNIT_SP = 0x003;
	public static final int COMPLEX_UNIT_PT = 0x004;
	public static final int COMPLEX_UNIT_IN = 0x005;
	public static final int COMPLEX_UNIT_MM = 0x006;
	
	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}
	
	public static int dipToPx(Context context, float dip) {
		System.out.println(getDensity(context));
		return (int)(dip * getDensity(context) + 0.5f);
	}
	
	public static int pxToDip(Context context, int px) {
		return (int)(px / getDensity(context) + 0.5f);
	}
	
	public static DisplayMetrics getDisplayMetrics(Activity activity) {
		
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return dm;
	}
    
    public static float applyDimension(int unit, float value, DisplayMetrics metrics)  {
    	
    	switch (unit) {
    	
			case COMPLEX_UNIT_PX: 
				return value;
			
			case COMPLEX_UNIT_DIP: 
				return value * metrics.density; 
			
			case COMPLEX_UNIT_SP: 
				return value * metrics.scaledDensity;
			
			case COMPLEX_UNIT_PT:
				return value * metrics.xdpi * (1.0f/72);
			
			case COMPLEX_UNIT_IN:
				return value * metrics.xdpi;
			
			case COMPLEX_UNIT_MM:
				return value * metrics.xdpi * (1.0f/25.4f);
		}
    	
		return 0; 
	}
}

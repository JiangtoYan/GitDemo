package com.sccp.library.util;

import android.os.Environment;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

/**
 * 通用工具类
 */
public class ViewUtil {
	
	private static long lastClickTime = 0;
	
	/***
	 * 判断两次点击时间间隔是否小于500毫秒
	 * 
	 * @return boolean
	 */
    public static boolean isFastDoubleClick() {
    	
        long time = System.currentTimeMillis();
        
        if(time - lastClickTime < 500) {
            return true;
        }
        
        lastClickTime = time;
        return false;
    }
    
    /**
     * 获取应用运行的最大内存
     *
     * @return 最大内存
     */
    public static long getMaxMemory() {

        return Runtime.getRuntime().maxMemory() / 1024;
    }

    /**
     * Animates ImageView with "fade-in" effect
     * @param imageView ImageView which display image in
     * @param durationMillis The length of the animation in milliseconds
     */
    public static void animate(View imageView, int durationMillis) {	
    	animate(imageView, durationMillis, 0, 1);
	}
    
    /**
     * Animates ImageView with "fade-in" effect
     * @param imageView ImageView which display image in
     * @param durationMillis The length of the animation in milliseconds
     * @param fromAlpha Starting alpha value for the animation, where 1.0 means fully opaque and 0.0 means fully transparent.
     * @param toAlpha Ending alpha value for the animation.
     */
    public static void animate(View imageView, int durationMillis, float fromAlpha, float toAlpha) {
		
		if (imageView != null) {
			
			AlphaAnimation fadeImage = new AlphaAnimation(fromAlpha, toAlpha);
			fadeImage.setDuration(durationMillis);
			fadeImage.setInterpolator(new DecelerateInterpolator());
			imageView.startAnimation(fadeImage);
		}
	}
}

package com.sccp.library.util;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtil {

    private static SharedPreferenceUtil s_SharedPreferenceUtil;
//    private static String PREFERENCE_NAME = "com.sccp.library";
 
    private SharedPreferences msp;
     
    /**
     * 获取唯一的instance
     *
     * @return
     */
    public static synchronized SharedPreferenceUtil getInstance(Context context) {
    	

        if (s_SharedPreferenceUtil == null) {
        	s_SharedPreferenceUtil = new SharedPreferenceUtil(context);
        }
    	
        return s_SharedPreferenceUtil;
    }

    public SharedPreferences getSharedPreference() {
        return msp;
    }
    
    public SharedPreferenceUtil(Context context) {
        msp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE | Context.MODE_APPEND);
    }
    
    public SharedPreferenceUtil(Context context, String preferenceName) {
        msp = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE | Context.MODE_APPEND);
    }
 
    public synchronized void putString(String key, String value) {
         
        Editor editor = msp.edit();
     	editor.putString(key, value);
        editor.commit();
    }
    
    public synchronized void putInt(String key, int value) {
        
    	Editor editor = msp.edit();
    	editor.putInt(key, value);
    	editor.commit();
    }
    
	public synchronized void putLong(String key, long value) {
        
		Editor editor = msp.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public synchronized void putBoolean(String key, boolean value) {
        
		Editor editor = msp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
    
    public long getLong(String key, long defaultValue) {
		return msp.getLong(key, defaultValue);
	}
    
    public int getInt(String key, int defaultValue) {
		return msp.getInt(key, defaultValue);
	}
    
    public String getString(String key, String defaultValue) {
		return msp.getString(key, defaultValue);
	}

    public boolean getBoolean(String key, boolean defaultValue) {
		return msp.getBoolean(key, defaultValue);
	}
    
    public synchronized void remove(String key) {
        
        Editor editor = msp.edit();
        editor.remove(key);
        editor.commit();
    }
    
    public synchronized void removeAll() {
        
        Editor editor = msp.edit();
        editor.clear();
        editor.commit();
    }
    
    /**
     * put string preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    
	public static boolean putString(Context context, String key, String value) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE | Context.MODE_APPEND);
		SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
	}
	
	/**
     * get string preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     *         name that is not a string
     * @see #getString(Context, String, String)
     */
	
	public static String getString(Context context, String key) {
		return getString(context, key, null);
	}
	
	/**
     * get string preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a string
     */
	
	public static String getString(Context context, String key, String defaultValue) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);
	}
	
	/**
     * put int preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
	
	public static boolean putInt(Context context, String key, int value) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE | Context.MODE_APPEND);
		SharedPreferences.Editor editor = settings.edit();
	   	editor.putInt(key, value);
		return editor.commit();
	}
	
	/**
     * get int preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a int
     * @see #getInt(Context, String, int)
     */
	
	public static int getInt(Context context, String key) {
		return getInt(context, key, -1);
	}
	
	/**
     * get int preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a int
     */
	
	public static int getInt(Context context, String key, int defaultValue) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return settings.getInt(key, defaultValue);
	}
	
	/**
     * put long preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
	
	public static boolean putLong(Context context, String key, long value) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE | Context.MODE_APPEND);
		SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
	}
	
	/**
     * get long preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a long
     * @see #getLong(Context, String, long)
     */
	
	public static long getLong(Context context, String key) {
		return getLong(context, key, -1);
	}
	
	/**
     * get long preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a long
     */
	
	public static long getLong(Context context, String key, long defaultValue) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return settings.getLong(key, defaultValue);
	}
	
	/**
     * put float preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
	
	public static boolean putFloat(Context context, String key, float value) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE | Context.MODE_APPEND);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}
	
	/**
     * get float preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a float
     * @see #getFloat(Context, String, float)
     */
	
	public static float getFloat(Context context, String key) {
		return getFloat(context, key, -1);
	}
	
	/**
     * get float preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a float
     */
	
	public static float getFloat(Context context, String key, float defaultValue) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return settings.getFloat(key, defaultValue);
	}
	
	/**
	     * put boolean preferences
	     * 
	     * @param context
	     * @param key The name of the preference to modify
	     * @param value The new value for the preference
	     * @return True if the new values were successfully written to persistent storage.
	     */
	public static boolean putBoolean(Context context, String key, boolean value) {
		
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE | Context.MODE_APPEND);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	
	/**
     * get boolean preferences, default is false
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     *         name that is not a boolean
     * @see #getBoolean(Context, String, boolean)
     */
	
	public static boolean getBoolean(Context context, String key) {
		return getBoolean(context, key, false);
	}
	
	/**
     * get boolean preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a boolean
     */
	public static boolean getBoolean(Context context, String key, boolean defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		return settings.getBoolean(key, defaultValue);
	}

    
    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
    }
    
    /** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
}

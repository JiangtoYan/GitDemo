package com.sccp.library.cache;

import com.sccp.library.util.SharedPreferenceUtil;

import android.content.Context;

public class SharedPreferenceCache {

    private static SharedPreferenceCache SPCacheInstance = null;
    
    /**
     * Default config, can not clear when user logout
     */
    private SharedPreferenceUtil defaultSharedPreference = null;
 
    /**
     * App config, can not clear when user logout
     */
    private SharedPreferenceUtil appConfigSharedPreference = null;

	/**
     * App cache data, storage common data, like channel article list, can clear when user logout
     */
    private SharedPreferenceUtil appCacheSharedPreference = null;
    
    /**
     * User's setting data, do not suggest clear when user logout
     */
    private SharedPreferenceUtil userDataSharedPreference = null;
    
    /**
     * User's common cache data, such as user publish article list and so on, can clear when user logout
     */
    private SharedPreferenceUtil userCacheSharedPreference = null;
    
    /**
     * 获取唯一的instance
     *
     * @return
     */
    public static synchronized SharedPreferenceCache getInstance(Context context) {
    	
        if (SPCacheInstance == null) {
        	SPCacheInstance = new SharedPreferenceCache(context);
        }
    	
        return SPCacheInstance;
    }

    public SharedPreferenceCache getSharedPreference() {
        return SPCacheInstance;
    }
    
    public SharedPreferenceCache(Context context) {

    	defaultSharedPreference 	= new SharedPreferenceUtil(context);
    	appConfigSharedPreference 	= new SharedPreferenceUtil(context, "app_config");
    	appCacheSharedPreference 	= new SharedPreferenceUtil(context, "app_cahce");
    	
    	userDataSharedPreference = new SharedPreferenceUtil(context, "user_data");
    	userCacheSharedPreference 	= new SharedPreferenceUtil(context, "user_cache");
    }
    
    public SharedPreferenceUtil setUserData(Context context, String uid) {

    	userDataSharedPreference = new SharedPreferenceUtil(context, "user_data_" + uid);
    	return userDataSharedPreference;
    }
    
    public SharedPreferenceUtil setUserCache(Context context, String uid) {

    	userCacheSharedPreference = new SharedPreferenceUtil(context, "user_cache_" + uid);
    	return userCacheSharedPreference;
    }
    
    public SharedPreferenceUtil getDefaultSharedPreference() {
		return defaultSharedPreference;
	}

    public SharedPreferenceUtil getAppConfigSharedPreference() {
		return appConfigSharedPreference;
	}

	public SharedPreferenceUtil getAppCacheSharedPreference() {
		return appCacheSharedPreference;
	}

	public SharedPreferenceUtil getUserDataSharedPreference() {
		return userDataSharedPreference;
	}

	public SharedPreferenceUtil getUserCacheSharedPreference() {
		return userCacheSharedPreference;
	}
	
	public void clearAppConfigSharedPreference() {
		appConfigSharedPreference.removeAll();
	}
	
	public void clearAppCacheSharedPreference() {
		appCacheSharedPreference.removeAll();
	}
	
	public void clearUserDataSharedPreference() {
		userDataSharedPreference.removeAll();
	}
	
	public void clearUserCacheSharedPreference() {
		userCacheSharedPreference.removeAll();
	}
}

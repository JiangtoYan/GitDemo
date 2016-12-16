package com.sccp.library.util;

import android.content.Context;
import net.sf.json.JSONObject;

public class ConfigUtil {

	private final static String CONFIG_NAME = "app_config";
	private SharedPreferenceUtil sharedPreference;
	private static ConfigUtil configUtils;
	
	public static String apiHost = "http://120.25.237.16:8011/";
	public static String imageHost = "http://120.25.237.16:8011/";
	public static String cloudImageHost = "http://120.25.237.16:8011/";
	public static String cloudVideoHost = "http://120.25.237.16:8011/";
	public static String AnalyticsChannel = "default";
	
	public static synchronized ConfigUtil getInstance(Context context) {
		
		if(configUtils == null) {
			configUtils = new ConfigUtil(context);
		}
		
		return configUtils;
	}
	
	public ConfigUtil(Context context) {
		sharedPreference = SharedPreferenceUtil.getInstance(context);
	}
	
	public String getConfigFromSharedPreference() {
		
		String config = sharedPreference.getString(CONFIG_NAME, "");
		return config;
	}
	
	public JSONObject getConfig(String configName) {
		
		String config = getConfigFromSharedPreference();
		
		if(StringUtil.isEmpty(config)) {
			return null;
		}
		
		JSONObject configJson = JSONObject.fromObject(config);
		
		if(configJson == null) {
			return null;
		}
		
		if(configJson.containsKey(configName)) {
			return configJson.getJSONObject(configName);
		}
		
		return null;
	}
	
	public boolean setConfig(String configName, String configValue) {
		
		String config = getConfigFromSharedPreference();
		
		if(StringUtil.isEmpty(config)) {
			return false;
		}
		
		JSONObject configJson = JSONObject.fromObject(config);
		
		if(configJson == null) {
			return false;
		}
		
		configJson.put(configName, configValue);
		
		String appConfig = configJson.toString();
		sharedPreference.putString(CONFIG_NAME, appConfig);
		
		return true;
	}
	
	public static String getApiHost() {
		return apiHost;
	}

}
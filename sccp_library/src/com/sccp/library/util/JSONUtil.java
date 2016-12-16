package com.sccp.library.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtil {

	private static final String LOG_TAG = new Object() {  
        public String getClassName() {  
            String clazzName = this.getClass().getName();  
            return clazzName.substring(0, clazzName.lastIndexOf('$'));  
        }  
    }.getClassName() + ":";
	
	public static void put(JSONObject jsonObject, String key, Object value) {
		
		if(jsonObject == null) {
			Log.d(LOG_TAG + "put", "jsonObject = null");
			return;
		}
		
		try {
			jsonObject.put(key, value);
		} catch (JSONException e) {
			Log.e(LOG_TAG + "put", "Exception happend!");
			e.printStackTrace();
		}
	}
	
	public static void add(JSONArray jsonArray, int key, Object value) {
		
		if(jsonArray == null) {
			Log.d(LOG_TAG + "add", "jsonArray = null");
			return;
		}
		
		try {
			jsonArray.put(key, value);
		} catch (JSONException e) {
			Log.e(LOG_TAG + "put", "Exception happend!");
			e.printStackTrace();
		}
	}
}

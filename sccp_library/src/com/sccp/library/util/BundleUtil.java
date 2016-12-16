package com.sccp.library.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;

public class BundleUtil {

	public static Bundle mapToBundle(Map<String, String> map) {
		Bundle bundle = new Bundle();
		for (String key : map.keySet()) {
			bundle.putString(key, map.get(key));
		}
		return bundle;
	}

	public static Map<String, String> bundleToMap(Bundle extras) {
		
		Map<String, String> map = new HashMap<String, String>();

		Set<String> ks = extras.keySet();
		Iterator<String> iterator = ks.iterator();
		
		while (iterator.hasNext()) {
			String key = iterator.next();
			map.put(key, extras.getString(key));
		}
		return map;
	}
}

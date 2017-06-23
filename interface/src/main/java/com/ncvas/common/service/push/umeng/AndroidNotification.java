package com.ncvas.common.service.push.umeng;

import net.sf.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;

public abstract class AndroidNotification extends UmengNotification {
	// Keys can be set in the payload level
	protected static final HashSet<String> PAYLOAD_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"display_type"}));
	
	// Keys can be set in the body level
	protected static final HashSet<String> BODY_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"ticker", "title", "text", "builder_id", "icon", "largeIcon", "img", "play_vibrate", "play_lights", "play_sound",
			"sound", "after_open", "url", "activity", "custom"}));
	
	// Keys can be set in the policy level
	protected static final HashSet<String> POLICY_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"start_time", "expire_time", "max_send_num", "out_biz_no"
	}));
	
	// Set key/value in the rootJson, for the keys can be set please see ROOT_KEYS, PAYLOAD_KEYS, 
	// BODY_KEYS and POLICY_KEYS.
	@Override
	public boolean setPredefinedKeyValue(String key, Object value) throws Exception {
		if (ROOT_KEYS.contains(key)) {
			// This key should be in the root level
			rootJson.put(key, value);
		} else if (PAYLOAD_KEYS.contains(key)) {
			// This key should be in the payload level
			JSONObject payloadJson = null;
			if (!rootJson.has("payload")) {
				rootJson.put("payload", new JSONObject());
			}
			payloadJson = rootJson.optJSONObject("payload");
			payloadJson.put(key, value);
		} else if (BODY_KEYS.contains(key)) {
			// This key should be in the body level
			JSONObject bodyJson = null;
			JSONObject payloadJson = null;
			// 'body' is under 'payload', so build a payload if it doesn't exist
			if (!rootJson.has("payload")) {
				rootJson.put("payload", new JSONObject());
			}
			payloadJson = rootJson.optJSONObject("payload");
			// Get body JSONObject, generate one if not existed
			if (!payloadJson.has("body")) {
				payloadJson.put("body", new JSONObject());
			}
			bodyJson = payloadJson.optJSONObject("body");
			bodyJson.put(key, value);
		} else if (POLICY_KEYS.contains(key)) {
			// This key should be in the body level
			JSONObject policyJson = null;
			if (!rootJson.has("policy")) {
				rootJson.put("policy", new JSONObject());
			}
			policyJson = rootJson.optJSONObject("policy");
			policyJson.put(key, value);
		} else {
			if (key.equals("payload") 
					|| key.equals("body") 
					|| key.equals("policy") 
					|| key.equals("extra")) {
				throw new Exception("You don't need to set value for " + key + " , just set values for the sub keys in it.");
			} else {
				throw new Exception("Unknown key: " + key);
			}
		}
		return true;
	}
	
	// Set extra key/value for Android notification
	public boolean setExtraField(String key, String value) throws Exception {
		JSONObject payloadJson = null;
		JSONObject extraJson = null;
		if (!rootJson.has("payload")) {
			rootJson.put("payload", new JSONObject());
		}
		payloadJson = rootJson.optJSONObject("payload");
		
		if (!payloadJson.has("extra")) {
			payloadJson.put("extra", new JSONObject());
		}
		extraJson = payloadJson.optJSONObject("extra");
		extraJson.put(key, value);
		return true;
	}
	
}

package com.ncvas.common.service.push.umeng.android;

import com.ncvas.common.service.push.umeng.AndroidNotification;

public class AndroidBroadcast extends AndroidNotification {
	
	public AndroidBroadcast() throws Exception {
		this.setPredefinedKeyValue("type", "broadcast");	
	}
	
}

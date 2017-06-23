package com.ncvas.common.service.push.umeng.android;

import com.ncvas.common.service.push.umeng.AndroidNotification;

public class AndroidGroupcast extends AndroidNotification {
	
	public AndroidGroupcast() throws Exception {
		this.setPredefinedKeyValue("type", "groupcast");	
	}
	
}

package com.ncvas.common.service.push.umeng.android;

import com.ncvas.common.service.push.umeng.AndroidNotification;

public class AndroidUnicast extends AndroidNotification {
	
	public AndroidUnicast() throws Exception {
		this.setPredefinedKeyValue("type", "unicast");	
	}
}
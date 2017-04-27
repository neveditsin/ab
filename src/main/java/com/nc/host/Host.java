package com.nc.host;

import java.util.Map;

public interface Host {
	String getId();
	String getAddress();
	String getUrl();
	Integer getSshPort();
	String getSshUsername();
	String getSshPawwsord();
	String getSshKeypath();
	Map<String, Object> getParams();
	Map<String, String> getTagMap();
	boolean isKeyAuthUsed();
}

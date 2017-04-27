package com.nc.host;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.cronutils.utils.StringUtils;

public class GenericHost implements Host{
	
	private final String id;
	private final String address;
	private final String url;
	private final Integer sshPort;
	private final String sshUsername;
	private final String sshPassword;
	private final String sshKeypath;
	private final Map<String, Object> paramMap;
	private final Map<String, String> tagMap;
	
	private static final Map<String, String> fieldNameMap;
	static {
		fieldNameMap = new HashMap<>();
		fieldNameMap.put("id", "ID");
		fieldNameMap.put("address", "ADDRESS");
		fieldNameMap.put("url", "URL");
		fieldNameMap.put("sshPort", "SSH PORT");
		fieldNameMap.put("sshUsername", "SSH USERNAME");
	}
	

	private GenericHost(String id, String address, String url, Integer sshPort,
			String sshUsername, String sshPassword, String sshKeypath, Map<String, String> tagMap) {
		super();
		this.id = id;
		this.address = address;
		this.url = url;
		this.sshPort = sshPort;
		this.sshUsername = sshUsername;
		this.sshPassword = sshPassword;
		this.sshKeypath = sshKeypath;
		this.tagMap = tagMap;
		
		paramMap = new TreeMap<>();
		for (Field field : this.getClass().getDeclaredFields()) {
		    String name = field.getName();
		    Object value = null;
			try {
				value = field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			if (fieldNameMap.containsKey(name)) {
				paramMap.put(fieldNameMap.get(name), value);
			}
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericHost other = (GenericHost) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static class GenericHostBuilder{
		private String id;
		private String address;
		private String url;
		private Integer sshPort;
		private String sshUsername;
		private String sshPassword;
		private String sshKeypath;
		private Map<String, String> tagMap;
		
		public GenericHostBuilder(String id){
			this.id = id;
		}		
		
		public GenericHostBuilder setAddress(String address) {
			this.address = address;
			return this;			
		}
		
		public GenericHostBuilder setUrl(String url) {
			this.url = url;
			return this;
		}
		public GenericHostBuilder setSshPort(Integer sshPort) {
			this.sshPort = sshPort;
			return this;
		}
		public GenericHostBuilder setSshUsername(String sshUsername) {
			this.sshUsername = sshUsername;
			return this;
		}
		public GenericHostBuilder setSshPassword(String sshPassword) {
			this.sshPassword = sshPassword;
			return this;
		}
		public GenericHostBuilder setTagMap(Map<String, String> tagMap) {
			this.tagMap = tagMap;
			return this;
		}
		
		public GenericHostBuilder setSshKeypath(String sshKeypath) {
			this.sshKeypath = sshKeypath;
			return this;
		}
		
		public GenericHost getGenericHost() {
			return new GenericHost(id, address, url, sshPort, sshUsername,
					sshPassword, sshKeypath, tagMap != null ? tagMap
							: Collections.emptyMap());
		}	
	}	
	
	@Override
	public String getId() {
		return id;
	}


	@Override
	public String getAddress() {
		if (address == null) {
			throw new UnsupportedOperationException("this host does not have an address");
		}
		return address;
	}

	@Override
	public String getUrl() {
		if (url == null) {
			throw new UnsupportedOperationException("this host does not have url");
		}
		return url;
	}

	@Override
	public Integer getSshPort() {
		if (sshPort == null) {
			throw new UnsupportedOperationException("this host does not have sshPort");
		}
		return sshPort;
	}

	@Override
	public String getSshUsername() {
		if (sshUsername == null) {
			throw new UnsupportedOperationException("this host does not have sshUsername");
		}
		return sshUsername;
	}

	@Override
	public String getSshPawwsord() {
		if (sshPassword == null) {
			throw new UnsupportedOperationException("this host does not have sshPassword");
		}
		return sshPassword;
	}

	@Override
	public String getSshKeypath() {
		if (sshKeypath == null) {
			throw new UnsupportedOperationException("this host does not have sshKeypath");
		}
		return sshKeypath;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (String field : paramMap.keySet()) {
			sb.append(String.format("%s:%s, ", field, paramMap.get(field)));
		}
		sb.delete(sb.lastIndexOf(","), sb.length());
		return sb.toString();
	}

	@Override
	public Map<String, Object> getParams() {
		return paramMap;
	}

	@Override
	public Map<String, String> getTagMap() {
		return tagMap;
	}

	@Override
	public boolean isKeyAuthUsed() {
		return !StringUtils.isEmpty(this.sshKeypath);
	}


}

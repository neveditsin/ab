package com.nc.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ConfigurationException;

import com.nc.host.Host;

public class Utils {
	public static String preprocessString(String str, Host h) throws ConfigurationException{
		return preprocessString(str, h.getTagMap(), h.getId());
	}
	
	private static String preprocessString(String str,
			Map<String, String> tagMap, String hostId)
			throws ConfigurationException {
		
		String orig = str;
		Pattern r = Pattern.compile("(\\$htag\\[ *([a-zA-Z0-9_]*) *\\])");
		Matcher m = r.matcher(str);
		
		while (m.find()) {
			String tagId = m.group(2);
			if (tagMap.containsKey(tagId)) {
				str = str.replaceAll(Pattern.quote(m.group(1)),
						tagMap.get(tagId));
			} else {
				throw new ConfigurationException(
						"Preprocessing error. String: '" + orig
								+ "'. Tag ID: '" + tagId
								+ "' is not defined for the host '" + hostId
								+ "'");

			}
		}
		
		return str;
	}
	
	//TODO make a unit test
	
//	public static void main(String[] args) throws ConfigurationException{
//		
//		Map<String, String> m = new HashMap<>();
//		m.put("abc", "valu");
//		m.put("abdc", "valu1");
//		System.out.println(preprocessString("hi $htag[abc] $htag[abdc] $htag[abc] $htag[abdc] $htag[abdc]", m, "qq"));
//	}
}

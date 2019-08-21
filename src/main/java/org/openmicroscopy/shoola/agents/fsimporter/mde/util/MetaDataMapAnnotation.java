package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.util.HashMap;

public class MetaDataMapAnnotation 
{
	private HashMap<String,String> map;
	
	public void setMap(HashMap<String,String> newMap)
	{
		map=newMap;
	}
	
	public HashMap<String,String> getMap()
	{
		return map;
	}
	
	/** string.substring(string.lastIndexOf('-') + 1)*/
	public static <T> T last(T[] array) {
		return array[array.length - 1];
	}


	public static String getLastSubstring(String key, String string) 
	{
		String result="";
		try{
			result=last(key.split(":"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}

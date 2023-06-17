/**
 * @author Justin Espedal
 */
package com.polydes.datastruct;

import com.stencyl.Engine;

class DataStructures
{
	private static var init = false;
	private static var reloadSet = false;

	public static function get(s:String):Dynamic
	{
		if(!init)
			readData();

		return nameMap.get(s);
	}

	public static function getByID(i:Int):Dynamic
	{
		if(!init)
			readData();

		return idMap.get(i);
	}

	private static function readData()
	{
		DataStructureReader.readData();
		init = true;
		if(!reloadSet)
		{
			Engine.addReloadListener(reload);
			reloadSet = true;
		}
	}

	public static var idMap:Map<Int, Dynamic> = new Map<Int, Dynamic>();
	public static var nameMap:Map<String, Dynamic> = new Map<String, Dynamic>();
	public static var types:Map<String, String> = new Map<String, String>();

	public static function reload()
	{
		init = false;
		idMap = new Map<Int, Dynamic>();
		nameMap = new Map<String, Dynamic>();
		types = new Map<String, String>();
		StringData.readers = new Map<String, String->Dynamic>();
	}
}

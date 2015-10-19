package com.polydes.datastruct;

abstract Control(String)
{
	inline public function new(s:String)
	{
		this = s;
	}

	public static function fromString(s:String):Control
	{
		return s;
	}
}
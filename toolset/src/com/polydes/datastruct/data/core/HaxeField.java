package com.polydes.datastruct.data.core;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.ext.registry.RORealizer;

public class HaxeField implements RORealizer<HaxeDataType>
{
	public String name;
	public HaxeDataType type;
	public ExtrasMap editorData;
	public String defaultValue;
	
	public HaxeField(String name, HaxeDataType type, ExtrasMap editorData)
	{
		this.name = name;
		this.type = type;
		defaultValue = StringUtils.EMPTY;
		
		this.editorData = editorData == null ?
			new ExtrasMap(DataContext.NO_CONTEXT) :
			editorData;
	}

	@Override
	public String toString()
	{
		return "HaxeField [name=" + name + ", type=" + type + ", editorData="
				+ editorData + ", defaultValue=" + defaultValue + "]";
	}

	@Override
	public void realizeRO(HaxeDataType type)
	{
		this.type = type;
	}
}

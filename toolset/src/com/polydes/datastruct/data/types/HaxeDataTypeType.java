package com.polydes.datastruct.data.types;

import com.polydes.datastruct.DataStructuresExtension;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;

public class HaxeDataTypeType extends DataType<HaxeDataType>
{
	public HaxeDataTypeType()
	{
		super(HaxeDataType.class, "haxe-datatype");
	}

	@Override
	public HaxeDataType decode(String s, DataContext ctx)
	{
		return DataStructuresExtension.get().getHaxeTypes().getItem(s);
	}

	@Override
	public String toDisplayString(HaxeDataType data)
	{
		return data.getHaxeType();
	}

	@Override
	public String encode(HaxeDataType data, DataContext ctx)
	{
		return data.getHaxeType();
	}
	
	@Override
	public HaxeDataType copy(HaxeDataType t)
	{
		return t;
	}
}
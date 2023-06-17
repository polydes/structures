package com.polydes.datastruct.data.types;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.core.Dynamic;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;

public class DynamicType extends DataType<Dynamic>
{
	public DynamicType()
	{
		super(Dynamic.class, "dynamic");
	}
	
	@Override
	public Dynamic decode(String s, DataContext ctx)
	{
		int i = s.lastIndexOf(":");
		if(i == -1)
			return new Dynamic(s, HaxeTypes._String);
		
		String value = s.substring(0, i);
		String type = s.substring(i + 1);
		HaxeDataType htype = DataStructuresExtension.get().getHaxeTypes().getItem(type);
		return new Dynamic(HaxeTypeConverter.decode(htype.dataType, value, ctx), htype);
	}

	@Override
	public String encode(Dynamic e, DataContext ctx)
	{
		return HaxeTypeConverter.encode(e.type.dataType, e.value, ctx) + ":" + e.type.getHaxeType();
	}

	@Override
	public String toDisplayString(Dynamic data)
	{
		return data.type.dataType.checkToDisplayString(data.value);
	}
	
	@Override
	public Dynamic copy(Dynamic t)
	{
		return new Dynamic(t.type.dataType.checkCopy(t.value), t.type);
	}
}
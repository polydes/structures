package com.polydes.datastruct.data.types.haxe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeObjectType;

import stencyl.core.util.Lang;

public class HaxeObjectHaxeType extends HaxeDataType
{
	HaxeObjectType type;
	
	public HaxeObjectHaxeType(HaxeObjectType type)
	{
		super(type, type.getId(), "OBJECT");
		this.type = type;
	}
	
	@Override
	public List<String> generateHaxeReader()
	{
		if(type.getDef().haxereaderExpression != null)
		{
			return List.of(
					String.format("StringData.registerReader(\"%s\", function(s) return %s);", getHaxeType(), type.getDef().haxereaderExpression)
			);
		}
		else
		{
			String typeList = Arrays.stream(type.getDef().fields)
					.map((field) -> "\"" + field.type.getHaxeType() + "\"")
					.collect(Collectors.joining(",", "[", "]"));

			return List.of(String.format("StringData.registerHaxeObjectReader(\"%s\", %s, %s);", getHaxeType(), getHaxeType(), typeList));
		}
	}
}

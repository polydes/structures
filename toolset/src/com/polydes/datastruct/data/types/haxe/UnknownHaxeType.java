package com.polydes.datastruct.data.types.haxe;

import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.PropertyKey;
import stencyl.core.datatypes.UnknownDataType;

public class UnknownHaxeType extends HaxeDataType
{
	public UnknownHaxeType(String name)
	{
		super(new UnknownDataType(name), name, "OBJECT");
	}
	
	public static final PropertyKey<ExtrasMap> EXTRAS_MAP = new PropertyKey<>("extrasMap");
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		return new UnknownProperties(extras);
	}

	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		return ((UnknownProperties) props).getMap();
	}
	
	public static class UnknownProperties extends DataTypeProperties
	{
		public UnknownProperties(ExtrasMap extras)
		{
			put(EXTRAS_MAP, extras);
		}
		
		public ExtrasMap getMap()
		{
			return get(EXTRAS_MAP);
		}
	}
}

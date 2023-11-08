package com.polydes.datastruct.data.types;

import stencyl.core.datatypes.Types;

public class DSTypes
{
	public static DynamicType _Dynamic = new DynamicType();
	public static ExtrasResourceType _ExtrasResource = new ExtrasResourceType();
	public static HaxeDataTypeType _HaxeDataType = new HaxeDataTypeType();
	
	public static void register()
	{
		Types.get().loadReference(_Dynamic);
		Types.get().loadReference(_ExtrasResource);
	}
	
	public static void unregister()
	{
		Types.get().unloadReference(_Dynamic);
		Types.get().unloadReference(_ExtrasResource);
	}
}

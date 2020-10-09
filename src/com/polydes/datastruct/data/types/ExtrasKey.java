package com.polydes.datastruct.data.types;

import com.polydes.common.data.types.PropertyKey;

public class ExtrasKey<T>
{
	public final PropertyKey<T> propertyKey;
	public final String id;

	public ExtrasKey(PropertyKey<T> propertyKey, String id)
	{
		this.propertyKey = propertyKey;
		this.id = id;
	}
}

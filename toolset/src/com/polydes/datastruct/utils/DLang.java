package com.polydes.datastruct.utils;

import stencyl.core.api.data.DataList;
import stencyl.core.api.datatypes.DataType;

public class DLang
{
	public static final DataList datalist(DataType<?> type, Object... a)
	{
		DataList list = new DataList(type.getRef());
		for(Object o : a)
			list.add(o);
		return list;
	}
}

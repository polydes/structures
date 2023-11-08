package com.polydes.datastruct.data.types;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.DataStructuresExtension;

import stencyl.core.api.data.DataList;
import stencyl.core.api.data.DataSet;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.datatypes.ArrayType;
import stencyl.core.datatypes.Types;

/**
 * Kind of a hack class to make it so we can still have Haxe types
 * in our input/output instead of corresponding Java types.
 */
public class HaxeTypeConverter
{
	private static HaxeTypes getHT()
	{
		return DataStructuresExtension.get().getHaxeTypes();
	}
	
	@SuppressWarnings("rawtypes")
	private static HashMap<String, Coder> coders;
	static
	{
		coders = new HashMap<>();
		coders.put(Types._Array.getId(), new ArrayCoder());
		coders.put(Types._Set.getId(), new SetCoder());
	}
	
	public static Object decode(DataType<?> type, String s, DataContext ctx)
	{
		if(coders.containsKey(type.getId()))
			return coders.get(type.getId()).decode(s, ctx);
		else
			return type.decode(s, ctx);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static String encode(DataType type, Object o, DataContext ctx)
	{
		if(o == null)
			return "";
		
		if(type.javaType.isAssignableFrom(o.getClass()))
		{
			if(coders.containsKey(type.getId()))
				return coders.get(type.getId()).encode(o, ctx);
			else
				return type.encode(o, ctx);
		}
		
		System.out.println("Failed to encode " + o);
		
		return "";
	}
	
	static interface Coder<T>
	{
		T decode(String s, DataContext ctx);
		String encode(T t, DataContext ctx);
	}
	
	static class ArrayCoder implements Coder<DataList>
	{
		@Override
		public DataList decode(String s, DataContext ctx)
		{
			if(s.isEmpty())
				return null;
			
			int i = s.lastIndexOf(":");
			String typename = s.substring(i + 1);
			DataType<?> genType = getHT().getItem(typename).dataType;
			DataList list = new DataList(genType.getRef());
			
			for(String s2 : ArrayType.getEmbeddedArrayStrings(s))
				list.add(HaxeTypeConverter.decode(genType, s2, ctx));
			
			return list;
		}
		
		@Override
		public String encode(DataList array, DataContext ctx)
		{
			if(array == null)
				return "";
			
			String s = "[";
			
			for(int i = 0; i < array.size(); ++i)
				s += HaxeTypeConverter.encode(array.genType.getObject(), array.get(i), ctx) + (i < array.size() - 1 ? "," : "");
			s += "]:" + getHT().getHaxeFromDT(array.genType.getId()).getHaxeType();
			
			return s;
		}
	}
	
	static class SetCoder implements Coder<DataSet>
	{
		@Override
		public DataSet decode(String s, DataContext ctx)
		{
			int typeMark = s.lastIndexOf(":");
			if(typeMark == -1)
				return new DataSet(DSTypes._Dynamic);
			
			DataType<?> dtype = getHT().getItem(s.substring(typeMark + 1)).dataType;
			if(dtype == null)
				return new DataSet(DSTypes._Dynamic);
			
			DataSet toReturn = new DataSet(dtype);
			
			for(String s2 : StringUtils.split(s.substring(1, typeMark - 1), ","))
				toReturn.add(HaxeTypeConverter.decode(dtype, s2, ctx));
			
			return toReturn;
		}

		@Override
		public String encode(DataSet t, DataContext ctx)
		{
			Object[] a = t.toArray(new Object[0]);
			String s = "[";
			DataType<?> type = t.genType;
			
			for(int i = 0; i < a.length; ++i)
			{
				s += HaxeTypeConverter.encode(type, a[i], ctx);
				
				if(i < a.length - 1)
					s += ",";
			}
			
			s += "]:" + getHT().getHaxeFromDT(type.getId()).getHaxeType();
			
			return s;
		}
	}
}

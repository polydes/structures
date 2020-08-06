package com.polydes.datastruct.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Reflect
{
	private static final Logger log = Logger.getLogger(Reflect.class);
	
	public static HashMap<String, Field> getDeclaredFieldMap(Object o)
	{
		HashMap<String, Field> toReturn = new HashMap<String, Field>();
		Class<?> cls = (o instanceof Class) ? ((Class<?>) o) : o.getClass();

		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields)
			toReturn.put(f.getName(), f);

		return toReturn;
	}

	public static Object newInstance(Class<?> cls)
	{
		Constructor<?> ctor = null;
		try
		{
			ctor = cls.getDeclaredConstructor();
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			log.error(e.getMessage(), e);
		}

		try
		{
			return ctor.newInstance();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			log.error(e.getMessage(), e);
		}

		return null;
	}

	public static void setField(Field f, Object o, Object value)
	{
		try
		{
			if(!f.isAccessible())
			{
				f.setAccessible(true);
				f.set(o, value);
				f.setAccessible(false);
			}
			else
				f.set(o, value);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			log.error(e.getMessage(), e);
		}
	}
	
	public static Object getFieldValue(Field f, Object o)
	{
		try
		{
			if(!f.isAccessible())
			{
				f.setAccessible(true);
				Object toReturn = f.get(o);
				f.setAccessible(false);
				return toReturn;
			}
			else
				return f.get(o);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			log.error(e.getMessage(), e);
		}
		
		return null;
	}

	public static Class<?>[] getGenericTypes(Field f)
	{
		ParameterizedType type = (ParameterizedType) f.getGenericType();
		Type[] types = type.getActualTypeArguments();
		Class<?>[] toReturn = new Class<?>[types.length];
		for (int i = 0; i < types.length; ++i)
		{
			toReturn[i] = (Class<?>) types[i];
		}
		return toReturn;
	}

	public static Field[] getFields(Object o, String[] fieldNames)
	{
		try
		{
			Class<?> c = o.getClass();
			Field[] fields = new Field[fieldNames.length];
			for (int i = 0; i < fields.length; ++i)
				fields[i] = c.getDeclaredField(fieldNames[i]);

			return fields;
		}
		catch (SecurityException | NoSuchFieldException e)
		{
			log.error(e.getMessage(), e);
		}

		return null;
	}

	public static Object[] getValues(Object o, String[] fieldNames)
	{
		HashMap<String, Method> nameMethodMap = new HashMap<String, Method>();

		BeanInfo info = null;
		try
		{
			info = Introspector.getBeanInfo(o.getClass(), Object.class);
		}
		catch (IntrospectionException e)
		{
			log.error(e.getMessage(), e);
		}
		PropertyDescriptor[] props = info.getPropertyDescriptors();
		for (PropertyDescriptor pd : props)
		{
			nameMethodMap.put(pd.getName(), pd.getReadMethod());
		}

		Object[] vals = new Object[fieldNames.length];
		for (int i = 0; i < fieldNames.length; ++i)
			vals[i] = invoke(nameMethodMap.get(fieldNames[i]), o);

		return vals;
	}

	public static Method getMethod(Object o, String name, Class<?>... args)
	{
		if (o instanceof Class)
		{
			try
			{
				return ((Class<?>) o).getMethod(name, args);
			}
			catch (SecurityException | NoSuchMethodException e)
			{
				log.error(e.getMessage(), e);
			}
		}

		BeanInfo info = null;
		try
		{
			info = Introspector.getBeanInfo(o.getClass(), Object.class);
		}
		catch (IntrospectionException e)
		{
			log.error(e.getMessage(), e);
		}
		MethodDescriptor[] methods = info.getMethodDescriptors();
		for (MethodDescriptor md : methods)
		{
			if (md.getMethod().getName().equals(name))
				return md.getMethod();
		}
		return null;
	}

	public static Object invoke(Method toInvoke, Object invokeOn, Object... args)
	{
		try
		{
			return toInvoke.invoke(invokeOn, args);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
}

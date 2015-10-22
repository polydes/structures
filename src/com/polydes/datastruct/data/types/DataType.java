package com.polydes.datastruct.data.types;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

import stencyl.sw.editors.snippet.designer.Definition;

public abstract class DataType<T> implements Comparable<DataType<?>>
{
	public static String DEFAULT_VALUE = "default";
	public static String EDITOR = "editor";
	
	public String stencylType;
	public String haxeType;
	public Class<T> javaType;
	
	public String haxeClassname;
	public String haxePackage;
	
	private final int hash;
	
	public DataType(Class<T> javaType, String haxeType, String stencylType)
	{
		this.javaType = javaType;
		this.haxeType = haxeType;
		this.stencylType = stencylType;
		
		int lastDot = haxeType.lastIndexOf('.');
		if(lastDot == -1)
		{
			haxePackage = "";
			haxeClassname = haxeType;
		}
		else
		{
			haxePackage = haxeType.substring(0, lastDot);
			haxeClassname = haxeType.substring(lastDot + 1);
		}
		
		hash = haxeType.hashCode();
	}
	
	public abstract ExtrasMap saveExtras(ExtraProperties extras);
	public abstract ExtraProperties loadExtras(ExtrasMap extras);
	/*{
		if(extraParameters != null)
			for(StructureField f : extraParameters.getFields())
			{
				if(field.optionalArgs.containsKey(f.name))
				{
					DataType<?> converter = Types.fromXML(f.type);
					field.optionalArgs.put(f.name, converter.decode("" + field.optionalArgs.get(f.name)));
				}
				else if(f.optionalArgs.containsKey("default"))
				{
					DataType<?> converter = Types.fromXML(f.type);
					field.optionalArgs.put(f.name, converter.decode("" + f.optionalArgs.get("default")));
				}
			}
	}*/
	
	protected static JComponent[] comps(JComponent... c)
	{
		return c;
	}
	
	//return null for classes that already exist
	public List<String> generateHaxeClass()
	{
		return null;
	}
	
	public List<String> generateHaxeReader()
	{
		return null;
	}
//	public abstract JComponent[] getEditor(DataUpdater<T> updater, ExtraProperties extras, PropertiesSheetStyle style);
//	public JComponent[] getEditor(DataUpdater<T> updater, ExtrasMap extras, PropertiesSheetStyle style)
//	{
//		return getEditor(updater, loadExtras(extras), style);
//	}
	/**
	 * Create editor, set value, add listener.<br />
	 * Dispose when you're done.
	 */
	public abstract DataEditor<T> createEditor(ExtraProperties extras, PropertiesSheetStyle style);
	public DataEditor<T> createEditor(ExtrasMap extras, PropertiesSheetStyle style)
	{
		return createEditor(loadExtras(extras), style);
	}
	
	/**
	 * From the passed in StructureFieldPanel, the following are accessible:	<br />
	 * - panel  :  StructureFieldPanel											<br />
	 * - extraProperties  :  Card												<br />
	 * 																			<br />
	 * - field  :  StructureField												<br />
	 * - preview  :  PropertiesSheet											<br />
	 * - previewKey  :  DataItem												<br />
	 */
	public /*abstract*/ void applyToFieldPanel(StructureFieldPanel panel)
	{
		System.out.println("APPLYING OTHER " + haxeType);
	};
	
	public abstract T decode(String s);
	public abstract String toDisplayString(T data);
	
	protected static <S> S or(S item, S defaultValue)
	{
		return item == null ? defaultValue: item;
	}
	
	@SuppressWarnings("unchecked")
	public String checkToDisplayString(Object o)
	{
		if(o == null)
			return "null";
		
		if(javaType.isAssignableFrom(o.getClass()))
			return toDisplayString((T) o);
		
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public String checkEncode(Object o)
	{
		if(o == null)
			return "";
		
		if(javaType.isAssignableFrom(o.getClass()))
			return encode((T) o);
		
		System.out.println("Failed to encode " + o);
		
		return "";
	}
	
	public abstract String encode(T t);

	@Override
	public int compareTo(DataType<?> dt)
	{
		return haxeType.compareTo(dt.haxeType);
	}
	
	@SuppressWarnings("unchecked")
	public T checkCopy(Object o)
	{
		if(o == null)
			return null;
		
		if(javaType.isAssignableFrom(o.getClass()))
			return copy((T) o);
		
		System.out.println("Failed to copy " + o);
		
		return null;
	}
	
	public abstract T copy(T t);
	
	@Override
	public String toString()
	{
		return haxeType;
	}

	public ArrayList<Definition> getBlocks()
	{
		return null;
	}
	
	public static class InvalidEditor<T> extends DataEditor<T>
	{
		PropertiesSheetStyle style;
		String msg;
		
		public InvalidEditor(String msg, PropertiesSheetStyle style)
		{
			this.msg = msg;
			this.style = style;
		}
		
		@Override public void set(T t) {}
		@Override public T getValue() { return null; }
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(style.createSoftLabel(msg));
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof DataType) && haxeType.equals(((DataType<?>) obj).haxeType);
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}
}
package com.polydes.datastruct.data.types;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.datatypes.Types;
import stencyl.core.datatypes.UnknownDataType;
import stencyl.core.ext.registry.RegistryObject;
import stencyl.core.lib.code.design.Definition;

public abstract class HaxeDataType implements RegistryObject
{
	private static final Logger log = Logger.getLogger(HaxeDataType.class);
	
	public String stencylType;
	private String haxeType; //registry key
	
	public DataType<?> dataType;
	
	public HaxeDataType(DataType<?> dataType, String haxeType, String stencylType)
	{
		if(dataType instanceof UnknownDataType || dataType == null)
			throw new IllegalArgumentException("HaxeDataType cannot be instantiated with an unknown or null core data type.");
		this.dataType = dataType;
		this.haxeType = haxeType;
		this.stencylType = stencylType;
	}
	
	public String getSimpleClassname()
	{
		if(haxeType.contains("."))
			return StringUtils.substringAfterLast(haxeType, ".");
		else
			return haxeType;
	}
	
	public String getPackage()
	{
		if(haxeType.indexOf('.') == -1)
			return StringUtils.EMPTY;
		else
			return StringUtils.substringBeforeLast(haxeType, ".");
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
	
	public ExtrasMap saveExtras(DataTypeProperties extras, DataContext ctx)
	{
		return new ExtrasMap(ctx);
	}
	
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		return new DataTypeProperties();
	}
	
	/**
	 * From the passed in StructureFieldPanel, the following are accessible:	<br />
	 * - panel  :  StructureFieldPanel											<br />
	 * - extraProperties  :  Card												<br />
	 * 																			<br />
	 * - field  :  StructureField												<br />
	 * - preview  :  PropertiesSheet											<br />
	 * - previewKey  :  DefaultLeaf												<br />
	 */
	public /*abstract*/ void applyToFieldPanel(StructureFieldPanel panel)
	{
		log.debug("APPLYING OTHER " + haxeType);
	};
	
	public ArrayList<Definition> getBlocks()
	{
		return null;
	}
	
	public String getHaxeType()
	{
		return haxeType;
	}
	
	public void changeHaxeType(String newType)
	{
		DataStructuresExtension.get().getHaxeTypes().renameItem(this, newType);
	}
	
	@Override
	public String toString()
	{
		return getSimpleClassname();
	}
	
	@Override
	public String getKey()
	{
		return haxeType;
	}
	
	@Override
	public void setKey(String newKey)
	{
		Types.get().unloadReference(dataType);
		this.haxeType = newKey;
		Types.get().loadReference(dataType);
	}

	public ImageIcon getIcon(Object value)
	{
		return null;
	}
	
	public boolean isIconProvider()
	{
		return false;
	}
}
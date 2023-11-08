package com.polydes.datastruct.data.types.haxe;

import javax.swing.*;

import com.polydes.datastruct.data.core.ExtrasResource;
import com.polydes.datastruct.data.types.DSTypes;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.ExtrasResourceType;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.app.sys.FileRenderer;
import stencyl.core.api.datatypes.properties.DataTypeProperties;

import static com.polydes.datastruct.data.types.ExtrasResourceType.RESOURCE_TYPE;

public class ExtrasImageHaxeType extends HaxeDataType
{
	public ExtrasImageHaxeType()
	{
		super(DSTypes._ExtrasResource, "com.polydes.datastruct.ExtrasImage", "IMAGE");
	}
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		DataTypeProperties props = new DataTypeProperties();
		props.put(RESOURCE_TYPE, ExtrasResourceType.ResourceType.IMAGE);
		return props;
	}
	
	@Override
	public ImageIcon getIcon(Object value)
	{
		if(value instanceof ExtrasResource r)
		{
            if(r.file != null && r.file.exists())
				return FileRenderer.generateThumb(r.file);
		}
		return null;
	}
	
	@Override
	public boolean isIconProvider()
	{
		return true;
	}
}
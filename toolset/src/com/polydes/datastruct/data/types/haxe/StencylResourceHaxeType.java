package com.polydes.datastruct.data.types.haxe;

import java.awt.*;

import javax.swing.*;

import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.app.io.IconLoader;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.ExtrasKey;
import stencyl.core.datatypes.StencylResourceType;
import stencyl.core.datatypes.Types;
import stencyl.core.lib.resource.Resource;

import static stencyl.core.datatypes.StencylResourceType.RENDER_PREVIEW;

public class StencylResourceHaxeType<T extends Resource> extends HaxeDataType
{
	public StencylResourceType<T> srt;
	
	public StencylResourceHaxeType(StencylResourceType<T> srt, String haxeType, String stencylType)
	{
		super(srt, haxeType, stencylType);
		this.srt = srt;
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Boolean> KEY_RENDER_PREVIEW = new ExtrasKey<>(RENDER_PREVIEW, "renderPreview");
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		DataTypeProperties props = new DataTypeProperties();
		if(extras.containsKey(KEY_RENDER_PREVIEW))
			props.put(RENDER_PREVIEW, Boolean.TRUE);
		return props;
	}
	
	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		ExtrasMap extras = new ExtrasMap(ctx);
		if(props.get(RENDER_PREVIEW) == Boolean.TRUE)
			extras.put(KEY_RENDER_PREVIEW, Types._Bool, Boolean.TRUE);
		return extras;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		panel.getEditorSheet().build()
			.field(RENDER_PREVIEW.id)._editor(Types._Bool).add()
			.finish();
	}
	
	@Override
	public ImageIcon getIcon(Object value)
	{
		if(value instanceof Resource r)
		{
			Image img = r.getThumbnail();
			if(img != null) return new ImageIcon(img);
			return IconLoader.loadIcon("res/global/warning.png");
		}
		return null;
	}
	
	@Override
	public boolean isIconProvider()
	{
		return true;
	}
}
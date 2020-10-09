package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.data.types.builtin.StencylResourceType.RENDER_PREVIEW;

import javax.swing.ImageIcon;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.StencylResourceType;
import com.polydes.common.sw.Resources;
import com.polydes.datastruct.data.types.ExtrasKey;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Resource;

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
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		if(extras.containsKey(KEY_RENDER_PREVIEW))
			props.put(RENDER_PREVIEW, Boolean.TRUE);
		return props;
	}
	
	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap extras = new ExtrasMap();
		if(props.get(RENDER_PREVIEW) == Boolean.TRUE)
			extras.put(KEY_RENDER_PREVIEW, Types._Bool, Boolean.TRUE);
		return extras;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		panel.getEditorSheet().build()
			.field(RENDER_PREVIEW.id)._boolean().add()
			.finish();
	}
	
	@Override
	public ImageIcon getIcon(Object value)
	{
		if(value instanceof AbstractResource)
			return new ImageIcon(Resources.getImage((AbstractResource) value));
		return null;
	}
	
	@Override
	public boolean isIconProvider()
	{
		return true;
	}
}
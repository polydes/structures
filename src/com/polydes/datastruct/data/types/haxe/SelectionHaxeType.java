package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.data.types.builtin.extra.SelectionType.EDITOR;
import static com.polydes.common.data.types.builtin.extra.SelectionType.OPTIONS;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.extra.SelectionType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasKey;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class SelectionHaxeType extends HaxeDataType
{
	public SelectionHaxeType()
	{
		super(Types._Selection, "com.polydes.datastruct.Selection", "TEXT");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Editor>   KEY_EDITOR  = new ExtrasKey<>(EDITOR, "editor");
	private static final ExtrasKey<DataList> KEY_OPTIONS = new ExtrasKey<>(OPTIONS, "options");
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(EDITOR, extras.getEnum(KEY_EDITOR, Editor.Dropdown));
		if(extras.containsKey(KEY_OPTIONS))
			props.put(OPTIONS, extras.getTyped(KEY_OPTIONS, Types._Array, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.putEnum(KEY_EDITOR, props.get(EDITOR));
		if(props.containsKey(OPTIONS))
			emap.putTyped(KEY_OPTIONS, Types._Array, props.get(OPTIONS));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._enum(Editor.class).add()
			
			.field(OPTIONS.id)._array().simpleEditor().add()
			
			.finish();
	}
}

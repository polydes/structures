package com.polydes.datastruct.data.types.haxe;

import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.app.comp.datatypes.array.SimpleArrayEditor;
import stencyl.app.comp.datatypes.selection.DropdownSelectionEditor;
import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.core.api.data.DataList;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.ExtrasKey;
import stencyl.core.datatypes.Types;

import static stencyl.core.datatypes.SelectionType.*;

public class SelectionHaxeType extends HaxeDataType
{
	public SelectionHaxeType()
	{
		super(Types._Selection, "com.polydes.datastruct.Selection", "TEXT");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<DataList> KEY_OPTIONS = new ExtrasKey<>(OPTIONS, "options");
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		DataTypeProperties props = new DataTypeProperties();
		props.put(EDITOR, extras.get(KEY_EDITOR, Types._String, DropdownSelectionEditor.id));
		if(extras.containsKey(KEY_OPTIONS))
			props.put(OPTIONS, extras.getTyped(KEY_OPTIONS, Types._Array, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		ExtrasMap emap = new ExtrasMap(ctx);
		emap.put(KEY_EDITOR, Types._String, props.get(EDITOR));
		if(props.containsKey(OPTIONS))
			emap.putTyped(KEY_OPTIONS, Types._Array, props.get(OPTIONS));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		DataList editorList = DataList.fromStrings(new String[] {DropdownSelectionEditor.id});
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._editor(DropdownSelectionEditor.BUILDER).source(editorList).add()
			
			.field(OPTIONS.id)._editor(SimpleArrayEditor.BUILDER).add()
			
			.finish();
	}
}

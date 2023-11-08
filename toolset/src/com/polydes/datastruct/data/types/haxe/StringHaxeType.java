package com.polydes.datastruct.data.types.haxe;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.app.comp.datatypes.selection.DropdownSelectionEditor;
import stencyl.app.comp.datatypes.string.ExpandingStringEditor;
import stencyl.app.comp.datatypes.string.SingleLineStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.core.api.data.DataList;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.ExtrasKey;
import stencyl.core.datatypes.Types;

import static stencyl.core.datatypes.StringType.*;

public class StringHaxeType extends HaxeDataType
{
	public StringHaxeType()
	{
		super(Types._String, "String", "TEXT");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<String> KEY_REGEX  = new ExtrasKey<>(REGEX, "regex");
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		DataTypeProperties props = new DataTypeProperties();
		props.put(EDITOR, extras.get(KEY_EDITOR, Types._String, SingleLineStringEditor.id));
		props.put(REGEX, extras.get(KEY_REGEX, Types._String, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		ExtrasMap emap = new ExtrasMap(ctx);
		emap.put(KEY_EDITOR, Types._String, props.get(EDITOR));
		if(props.containsKey(REGEX))
			emap.put(KEY_REGEX, Types._String, props.get(REGEX));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		DataList editorList = DataList.fromStrings(new String[] {SingleLineStringEditor.id, ExpandingStringEditor.id});
		
		DataTypeProperties props = panel.getExtras();
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._editor(DropdownSelectionEditor.BUILDER).source(editorList).add()
			
			.field(REGEX.id)._editor(Types._String).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(REGEX.id, event -> {
			if(StringUtils.isEmpty(props.get(REGEX)))
				props.put(REGEX, null);
		});
	}
}

package com.polydes.datastruct.data.types.haxe;

import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.app.comp.datatypes.intprim.PlainIntegerEditor;
import stencyl.app.comp.datatypes.intprim.SliderIntegerEditor;
import stencyl.app.comp.datatypes.intprim.SpinnerIntegerEditor;
import stencyl.app.comp.datatypes.selection.DropdownSelectionEditor;
import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.core.api.data.DataList;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.ExtrasKey;
import stencyl.core.datatypes.Types;

import static stencyl.core.datatypes.IntType.*;

public class IntHaxeType extends HaxeDataType
{
	public IntHaxeType()
	{
		super(Types._Int, "Int", "NUMBER");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Integer> KEY_MIN    = new ExtrasKey<>(MIN, "min");
	private static final ExtrasKey<Integer> KEY_MAX    = new ExtrasKey<>(MAX, "max");
	private static final ExtrasKey<Integer> KEY_STEP   = new ExtrasKey<>(STEP, "step");
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		DataTypeProperties props = new DataTypeProperties();
		props.put(EDITOR, extras.get(KEY_EDITOR, Types._String, PlainIntegerEditor.id));
		props.put(MIN, extras.get(KEY_MIN, Types._Int, null));
		props.put(MAX, extras.get(KEY_MAX, Types._Int, null));
		props.put(STEP, extras.get(KEY_STEP, Types._Int, 1));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		ExtrasMap emap = new ExtrasMap(ctx);
		emap.put(KEY_EDITOR, Types._String, props.get(EDITOR));
		if(props.containsKey(MIN))
			emap.put(KEY_MIN, Types._Int, props.get(MIN));
		if(props.containsKey(MAX))
			emap.put(KEY_MAX, Types._Int, props.get(MAX));
		emap.put(KEY_STEP, Types._Int, props.get(STEP));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		final DataTypeProperties props = panel.getExtras();

		DataList editorList = DataList.fromStrings(new String[] {PlainIntegerEditor.id, SliderIntegerEditor.id, SpinnerIntegerEditor.id});
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._editor(DropdownSelectionEditor.BUILDER).source(editorList).add()
			
			.field(MIN.id).optional()._editor(Types._Int).add()
			
			.field(MAX.id).optional()._editor(Types._Int).add()
			
			.field(STEP.id)._editor(Types._Int).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(EDITOR.id, event -> {
			panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR).equals(SpinnerIntegerEditor.id));
		});
		
		panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR).equals(SpinnerIntegerEditor.id));
	}
}

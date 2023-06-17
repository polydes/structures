package com.polydes.datastruct.data.types.haxe;

import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.app.comp.datatypes.floatprim.PlainFloatEditor;
import stencyl.app.comp.datatypes.floatprim.SliderFloatEditor;
import stencyl.app.comp.datatypes.floatprim.SpinnerFloatEditor;
import stencyl.app.comp.datatypes.selection.DropdownSelectionEditor;
import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.core.api.data.DataList;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.ExtrasKey;
import stencyl.core.datatypes.Types;

import static stencyl.core.datatypes.FloatType.*;

public class FloatHaxeType extends HaxeDataType
{
	public FloatHaxeType()
	{
		super(Types._Float, "Float", "NUMBER");
	}

	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Float>   KEY_MIN            = new ExtrasKey<>(MIN, "min");
	private static final ExtrasKey<Float>   KEY_MAX            = new ExtrasKey<>(MAX, "max");
	private static final ExtrasKey<Float>   KEY_STEP           = new ExtrasKey<>(STEP, "step");
	private static final ExtrasKey<Integer> KEY_DECIMAL_PLACES = new ExtrasKey<>(DECIMAL_PLACES, "decimalPlaces");

	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		DataTypeProperties props = new DataTypeProperties();
		props.put(EDITOR, extras.get(KEY_EDITOR, Types._String, PlainFloatEditor.id));
		props.put(MIN, extras.get(KEY_MIN, Types._Float, null));
		props.put(MAX, extras.get(KEY_MAX, Types._Float, null));
		props.put(DECIMAL_PLACES, extras.get(KEY_DECIMAL_PLACES, Types._Int, null));
		props.put(STEP, extras.get(KEY_STEP, Types._Float, 0.01f));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		ExtrasMap emap = new ExtrasMap(ctx);
		emap.put(KEY_EDITOR, Types._String, props.get(EDITOR));
		if(props.containsKey(MIN))
			emap.put(KEY_MIN, Types._Float, props.get(MIN));
		if(props.containsKey(MAX))
			emap.put(KEY_MAX, Types._Float, props.get(MAX));
		if(props.containsKey(DECIMAL_PLACES))
			emap.put(KEY_DECIMAL_PLACES, Types._Int, props.get(DECIMAL_PLACES));
		emap.put(KEY_STEP, Types._Float, props.get(STEP));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		final DataTypeProperties props = panel.getExtras();

		DataList editorList = DataList.fromStrings(new String[] {PlainFloatEditor.id, SliderFloatEditor.id, SpinnerFloatEditor.id});
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._editor(DropdownSelectionEditor.BUILDER).source(editorList).add()
			
			.field(MIN.id).optional()._editor(Types._Float).add()
			
			.field(MAX.id).optional()._editor(Types._Float).add()
			
			.field(DECIMAL_PLACES.id).optional()._editor(Types._Int).add()
			
			.field(STEP.id)._editor(Types._Float).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(EDITOR.id, event -> {
			panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR).equals(SpinnerFloatEditor.id));
			panel.setRowVisibility(sheet, DECIMAL_PLACES.id, props.get(EDITOR).equals(SliderFloatEditor.id));
		});
		
		panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR).equals(SpinnerFloatEditor.id));
		panel.setRowVisibility(sheet, DECIMAL_PLACES.id, props.get(EDITOR).equals(SliderFloatEditor.id));
	}
}

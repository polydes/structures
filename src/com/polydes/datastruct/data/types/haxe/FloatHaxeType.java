package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.data.types.builtin.basic.FloatType.DECIMAL_PLACES;
import static com.polydes.common.data.types.builtin.basic.FloatType.EDITOR;
import static com.polydes.common.data.types.builtin.basic.FloatType.MAX;
import static com.polydes.common.data.types.builtin.basic.FloatType.MIN;
import static com.polydes.common.data.types.builtin.basic.FloatType.STEP;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.FloatType;
import com.polydes.common.data.types.builtin.basic.FloatType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasKey;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class FloatHaxeType extends HaxeDataType
{
	public FloatHaxeType()
	{
		super(Types._Float, "Float", "NUMBER");
	}

	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Editor>  KEY_EDITOR         = new ExtrasKey<>(EDITOR, "editor");
	private static final ExtrasKey<Float>   KEY_MIN            = new ExtrasKey<>(MIN, "min");
	private static final ExtrasKey<Float>   KEY_MAX            = new ExtrasKey<>(MAX, "max");
	private static final ExtrasKey<Float>   KEY_STEP           = new ExtrasKey<>(STEP, "step");
	private static final ExtrasKey<Integer> KEY_DECIMAL_PLACES = new ExtrasKey<>(DECIMAL_PLACES, "decimalPlaces");

	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(EDITOR, extras.getEnum(KEY_EDITOR, Editor.Plain));
		props.put(MIN, extras.get(KEY_MIN, Types._Float, null));
		props.put(MAX, extras.get(KEY_MAX, Types._Float, null));
		props.put(DECIMAL_PLACES, extras.get(KEY_DECIMAL_PLACES, Types._Int, null));
		props.put(STEP, extras.get(KEY_STEP, Types._Float, 0.01f));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.putEnum(KEY_EDITOR, props.get(EDITOR));
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
		final EditorProperties props = panel.getExtras();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._enum(FloatType.Editor.class).add()
			
			.field(MIN.id).optional()._float().add()
			
			.field(MAX.id).optional()._float().add()
			
			.field(DECIMAL_PLACES.id).optional()._int().add()
			
			.field(STEP.id)._float().add()
			
			.finish();
		
		sheet.addPropertyChangeListener(EDITOR.id, event -> {
			panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR) == Editor.Spinner);
			panel.setRowVisibility(sheet, DECIMAL_PLACES.id, props.get(EDITOR) == Editor.Slider);
		});
		
		panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR) == Editor.Spinner);
		panel.setRowVisibility(sheet, DECIMAL_PLACES.id, props.get(EDITOR) == Editor.Slider);
	}
}

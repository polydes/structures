package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.data.types.builtin.basic.IntType.EDITOR;
import static com.polydes.common.data.types.builtin.basic.IntType.MAX;
import static com.polydes.common.data.types.builtin.basic.IntType.MIN;
import static com.polydes.common.data.types.builtin.basic.IntType.STEP;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.IntType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasKey;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class IntHaxeType extends HaxeDataType
{
	public IntHaxeType()
	{
		super(Types._Int, "Int", "NUMBER");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Editor>  KEY_EDITOR = new ExtrasKey<>(EDITOR, "editor");
	private static final ExtrasKey<Integer> KEY_MIN    = new ExtrasKey<>(MIN, "min");
	private static final ExtrasKey<Integer> KEY_MAX    = new ExtrasKey<>(MAX, "max");
	private static final ExtrasKey<Integer> KEY_STEP   = new ExtrasKey<>(STEP, "step");
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(EDITOR, extras.getEnum(KEY_EDITOR, Editor.Plain));
		props.put(MIN, extras.get(KEY_MIN, Types._Int, null));
		props.put(MAX, extras.get(KEY_MAX, Types._Int, null));
		props.put(STEP, extras.get(KEY_STEP, Types._Int, 1));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.putEnum(KEY_EDITOR, props.get(EDITOR));
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
		final EditorProperties props = panel.getExtras();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._enum(Editor.class).add()
			
			.field(MIN.id).optional()._int().add()
			
			.field(MAX.id).optional()._int().add()
			
			.field(STEP.id)._int().add()
			
			.finish();
		
		sheet.addPropertyChangeListener(EDITOR.id, event -> {
			panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR) == Editor.Spinner);
		});
		
		panel.setRowVisibility(sheet, STEP.id, props.get(EDITOR) == Editor.Spinner);
	}
}

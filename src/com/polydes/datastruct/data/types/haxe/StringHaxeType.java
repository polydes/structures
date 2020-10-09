package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.data.types.builtin.basic.StringType.EDITOR;
import static com.polydes.common.data.types.builtin.basic.StringType.REGEX;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.StringType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.types.ExtrasKey;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class StringHaxeType extends HaxeDataType
{
	public StringHaxeType()
	{
		super(Types._String, "String", "TEXT");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Editor> KEY_EDITOR = new ExtrasKey<>(EDITOR, "editor");
	private static final ExtrasKey<String> KEY_REGEX  = new ExtrasKey<>(REGEX, "regex");
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(EDITOR, extras.getEnum(KEY_EDITOR, Editor.SingleLine));
		props.put(REGEX, extras.get(KEY_REGEX, Types._String, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.putEnum(KEY_EDITOR, props.get(EDITOR));
		if(props.containsKey(REGEX))
			emap.put(KEY_REGEX, Types._String, props.get(REGEX));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		EditorProperties props = panel.getExtras();
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._enum(Editor.class).add()
			
			.field(REGEX.id)._string().add()
			
			.finish();
		
		sheet.addPropertyChangeListener(REGEX.id, event -> {
			if(StringUtils.isEmpty(props.get(REGEX)))
				props.put(REGEX, null);
		});
	}
}

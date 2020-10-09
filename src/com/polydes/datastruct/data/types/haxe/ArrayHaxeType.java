package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.data.types.builtin.basic.ArrayType.EDITOR;
import static com.polydes.common.data.types.builtin.basic.ArrayType.GEN_TYPE;
import static com.polydes.common.data.types.builtin.basic.ArrayType.GEN_TYPE_PROPS;
import static com.polydes.common.util.Lang.or;

import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.PropertyKey;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.basic.ArrayType.Editor;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.types.ExtrasKey;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeDataTypeType;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.data.types.haxe.UnknownHaxeType.UnknownProperties;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public class ArrayHaxeType extends HaxeDataType
{
	public ArrayHaxeType()
	{
		super(Types._Array, "Array", "LIST");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Editor>           KEY_EDITOR         = new ExtrasKey<>(EDITOR, "editor");
	private static final ExtrasKey<DataType<?>>      KEY_GEN_TYPE       = new ExtrasKey<>(GEN_TYPE, "genType");
	private static final ExtrasKey<EditorProperties> KEY_GEN_TYPE_PROPS = new ExtrasKey<>(GEN_TYPE_PROPS, "genTypeProps");
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		final EditorProperties props = new EditorProperties();
		props.put(EDITOR, extras.getEnum(KEY_EDITOR, Editor.Standard));
		if(extras.containsKey(KEY_GEN_TYPE_PROPS))
			props.put(GEN_TYPE_PROPS, new UnknownProperties(extras.getMap(KEY_GEN_TYPE_PROPS)));
		
		extras.requestDataType(KEY_GEN_TYPE, HaxeTypes._String, (type) -> {
			props.put(GEN_TYPE, type.dataType);
			EditorProperties genExtra = props.get(GEN_TYPE_PROPS);
			if(genExtra instanceof UnknownProperties)
				props.put(GEN_TYPE_PROPS, type.loadExtras(((UnknownProperties) genExtra).getMap()));
		});
		
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		DataType<?> genType = props.get(GEN_TYPE);
		HaxeDataType genTypeHaxe = DataStructuresExtension.get().getHaxeTypes().getHaxeFromDT(genType.getId());
		
		ExtrasMap emap = new ExtrasMap();
		emap.putEnum(KEY_EDITOR, props.get(EDITOR));
		emap.putDataType(KEY_GEN_TYPE, genType);
		emap.putMap(KEY_GEN_TYPE_PROPS, genTypeHaxe.saveExtras(props.get(GEN_TYPE_PROPS)));
		return emap;
	}
	
	public static final PropertyKey<HaxeDataType> GEN_TYPE_PROXY = new PropertyKey<>("_" + GEN_TYPE.id);
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		final PropertiesSheet preview = panel.getPreview();
		final DefaultLeaf previewKey = panel.getPreviewKey();
		
		HaxeDataTypeType hdt = new HaxeDataTypeType();
		EditorProperties props = panel.getExtras();
		
		String typeID = or(props.get(GEN_TYPE), Types._String).getId();
		props.put(GEN_TYPE_PROXY, DataStructuresExtension.get().getHaxeTypes().getHaxeFromDT(typeID));
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._enum(Editor.class).add()
			
			.field(GEN_TYPE_PROXY.id).label("Data Type")._editor(hdt).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(GEN_TYPE_PROXY.id, event -> {
			HaxeDataType newType = (HaxeDataType) event.getNewValue();
			props.put(GEN_TYPE, newType.dataType);
			preview.refreshLeaf(previewKey);
		});
	}
}

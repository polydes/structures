package com.polydes.datastruct.data.types.haxe;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.types.DSTypes;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.data.types.haxe.UnknownHaxeType.UnknownProperties;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.app.comp.datatypes.array.SimpleArrayEditor;
import stencyl.app.comp.datatypes.array.StandardArrayEditor;
import stencyl.app.comp.datatypes.selection.DropdownSelectionEditor;
import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.core.api.data.DataList;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.ExtrasKey;
import stencyl.core.api.datatypes.properties.PropertyKey;
import stencyl.core.datatypes.Types;
import stencyl.core.util.Lang;

import static stencyl.core.datatypes.ArrayType.*;

public class ArrayHaxeType extends HaxeDataType
{
	public ArrayHaxeType()
	{
		super(Types._Array, "Array", "LIST");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<String> KEY_EDITOR         = new ExtrasKey<>(EDITOR, "editor");
	private static final ExtrasKey<DataType<?>>      KEY_GEN_TYPE       = new ExtrasKey<>(GEN_TYPE, "genType");
	private static final ExtrasKey<DataTypeProperties> KEY_GEN_TYPE_PROPS = new ExtrasKey<>(GEN_TYPE_PROPS, "genTypeProps");
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		final DataTypeProperties props = new DataTypeProperties();
		props.put(EDITOR, extras.get(KEY_EDITOR, Types._String, StandardArrayEditor.id));
		if(extras.containsKey(KEY_GEN_TYPE_PROPS))
			props.put(GEN_TYPE_PROPS, new UnknownProperties(extras.getMap(KEY_GEN_TYPE_PROPS)));
		
		extras.requestDataType(KEY_GEN_TYPE, HaxeTypes._String, (type) -> {
			props.put(GEN_TYPE, type.dataType);
			DataTypeProperties genExtra = props.get(GEN_TYPE_PROPS);
			if(genExtra instanceof UnknownProperties)
				props.put(GEN_TYPE_PROPS, type.loadExtras(((UnknownProperties) genExtra).getMap()));
		});
		
		return props;
	}

	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		DataType<?> genType = props.get(GEN_TYPE);
		HaxeDataType genTypeHaxe = DataStructuresExtension.get().getHaxeTypes().getHaxeFromDT(genType.getId());
		
		ExtrasMap emap = new ExtrasMap(ctx);
		emap.put(KEY_EDITOR, Types._String, props.get(EDITOR));
		emap.putDataType(KEY_GEN_TYPE, genType);
		emap.putMap(KEY_GEN_TYPE_PROPS, genTypeHaxe.saveExtras(props.get(GEN_TYPE_PROPS), ctx));
		return emap;
	}
	
	public static final PropertyKey<HaxeDataType> GEN_TYPE_PROXY = new PropertyKey<>("_" + GEN_TYPE.id);
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		DataTypeProperties props = panel.getExtras();
		
		String typeID = Lang.or(props.get(GEN_TYPE), Types._String).getId();
		props.put(GEN_TYPE_PROXY, DataStructuresExtension.get().getHaxeTypes().getHaxeFromDT(typeID));
		
		DataList editorList = DataList.fromStrings(new String[] {SimpleArrayEditor.id, StandardArrayEditor.id});
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		sheet.build()
		
			.field(EDITOR.id)._editor(DropdownSelectionEditor.BUILDER).source(editorList).add()
			
			.field(GEN_TYPE_PROXY.id).label("Data Type")._editor(DSTypes._HaxeDataType).add()
			
			.finish();
		
		sheet.addPropertyChangeListener(GEN_TYPE_PROXY.id, event -> {
			HaxeDataType newType = (HaxeDataType) event.getNewValue();
			
			panel.getPreview().model.clearProperty(panel.getField());
			panel.getField().setDefaultValue(null);
			
			sheet.writeField(props, GEN_TYPE.id, newType.dataType);
		});
	}
}

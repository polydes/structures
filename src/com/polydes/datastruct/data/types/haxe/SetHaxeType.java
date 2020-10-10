package com.polydes.datastruct.data.types.haxe;

import static com.polydes.common.data.types.builtin.extra.SetType.EDITOR;
import static com.polydes.common.data.types.builtin.extra.SetType.GEN_TYPE;
import static com.polydes.common.data.types.builtin.extra.SetType.SOURCE;
import static com.polydes.common.data.types.builtin.extra.SetType.SOURCE_FILTER;

import java.util.function.Predicate;

import com.polydes.common.data.core.DataList;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.PropertyKey;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.extra.SetType;
import com.polydes.common.data.types.builtin.extra.SetType.Editor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.types.ExtrasKey;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

public class SetHaxeType extends HaxeDataType
{
	public SetHaxeType()
	{
		super(Types._Set, "com.polydes.datastruct.Set", "OBJECT");
	}
	
	public  static final PropertyKey<SourceType>                 SOURCE_TYPE            = new PropertyKey<>("sourceType");
	public  static final PropertyKey<String>                     SOURCE_ID              = new PropertyKey<>("sourceId");
	private static final PropertyKey<DataList>                   SOURCE_PROXY_LIST      = new PropertyKey<>("_" + SOURCE.id);
	private static final PropertyKey<StencylResourceHaxeType<?>> SOURCE_PROXY_RESOURCE  = new PropertyKey<>("_" + SOURCE.id);
	private static final PropertyKey<StructureHaxeType>          SOURCE_PROXY_STRUCTURE = new PropertyKey<>("_" + SOURCE.id);
	private static final PropertyKey<Predicate<?>>               FILTER_PROXY           = new PropertyKey<>("_" + SOURCE_FILTER.id);
	
	public enum SourceType
	{
		Resource,
		Structure,
		Custom
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<Editor>        KEY_EDITOR        = new ExtrasKey<>(EDITOR, "editor");
	private static final ExtrasKey<SourceType>    KEY_SOURCE_TYPE   = new ExtrasKey<>(SOURCE_TYPE, "sourceType");
	private static final ExtrasKey<String>        KEY_SOURCE_ID     = new ExtrasKey<>(SOURCE_ID, "sourceId");
	private static final ExtrasKey<DataList>      KEY_SOURCE        = new ExtrasKey<>(SOURCE_PROXY_LIST, "source");
//	private static final ExtrasKey<Predicate<?>>  KEY_SOURCE_FILTER = new ExtrasKey<>(SOURCE_FILTER, "sourceFilter");
//	private static final ExtrasKey<DataType<?>>   KEY_GEN_TYPE      = new ExtrasKey<>(GEN_TYPE, "genType");
	
	@Override
	public EditorProperties loadExtras(ExtrasMap extras)
	{
		EditorProperties props = new EditorProperties();
		props.put(EDITOR, extras.getEnum(KEY_EDITOR, Editor.Checklist));
		props.put(SOURCE_TYPE, extras.getEnum(KEY_SOURCE_TYPE, SourceType.Custom));
		String sourceId = extras.get(KEY_SOURCE_ID, Types._String, null);
		switch(props.get(SOURCE_TYPE))
		{
			case Custom:
				props.put(SOURCE, extras.getTyped(KEY_SOURCE, Types._Array, null));
				props.put(GEN_TYPE, Types._String);
				break;
			case Resource:
				DataStructuresExtension.get().getHaxeTypes().requestValue(sourceId, htype -> {
					props.put(SOURCE, ((StencylResourceHaxeType<?>) htype).srt.getList());
					props.put(GEN_TYPE, htype.dataType);
				});
				break;
			case Structure:
				DataStructuresExtension.get().getHaxeTypes().requestValue(sourceId, htype -> {
					props.put(SOURCE, Structures.getList(((StructureHaxeType) htype).type.def));
					props.put(GEN_TYPE, htype.dataType);
				});
				break;
		}
		//TODO transform a string into an appropriate predicate
//		props.put(SetType.SOURCE_FILTER, extras.get("sourceFilter", Types._String, null));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(EditorProperties props)
	{
		ExtrasMap emap = new ExtrasMap();
		emap.putEnum(KEY_EDITOR, props.get(EDITOR));
		
		SourceType sourceType = props.get(SOURCE_TYPE);
		emap.putEnum(KEY_SOURCE_TYPE, sourceType);
		if(sourceType == SourceType.Custom)
			emap.putTyped(KEY_SOURCE, Types._Array, (DataList) props.get(SOURCE));
		else
			emap.put(KEY_SOURCE_ID, Types._String, props.get(SOURCE_ID)); 
		//TODO transform a predicate into a string
//		if(props.containsKey(SetType.SOURCE_FILTER))
//			emap.put("sourceFilter", props.get(SetType.SOURCE_FILTER));
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		EditorProperties props = panel.getExtras();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		
		props.remove(SOURCE_PROXY_LIST);
		props.remove(FILTER_PROXY);
		
		sheet.build()
			.field(SOURCE_TYPE.id)._enum(SourceType.class).add()
			.field(SOURCE_PROXY_LIST.id)._array().simpleEditor().genType(Types._String).add()
			.field(FILTER_PROXY.id).optional()._string().add()
			.finish();
		
		sheet.addPropertyChangeListener(SOURCE_TYPE.id, event -> {
			updateSourceType(panel, sheet, props);
		});
		
		sheet.addPropertyChangeListener(SOURCE_PROXY_LIST.id, event -> {
			updateSource(panel, sheet, props);
		});
		
		updateSourceType(panel, sheet, props);
	}

	private void updateSourceType(StructureFieldPanel panel, PropertiesSheetSupport sheet, EditorProperties props)
	{
		SourceType type = props.get(SOURCE_TYPE);
		
		HaxeTypes types = DataStructuresExtension.get().getHaxeTypes();
		
		props.remove(SOURCE_PROXY_LIST);
		
		switch(type)
		{
			case Custom:
				sheet.change().field(SOURCE_PROXY_LIST.id)
					._array().simpleEditor().genType(Types._String).change().finish();
				break;
			case Resource:
				sheet.change().field(SOURCE_PROXY_RESOURCE.id)
					._collection(types.values()).filter(htype -> (htype instanceof StencylResourceHaxeType)).change().finish();
				break;
			case Structure:
				sheet.change().field(SOURCE_PROXY_STRUCTURE.id)
					._collection(types.values()).filter(htype -> (htype instanceof StructureHaxeType)).change().finish();
				break;
		}
		
		panel.setRowVisibility(sheet, FILTER_PROXY.id, type != SourceType.Custom);
	}
	
	private void updateSource(StructureFieldPanel panel, PropertiesSheetSupport sheet, EditorProperties props)
	{
		SourceType type = props.get(SOURCE_TYPE);
		
		if(props.get(SOURCE_PROXY_LIST) == null)
			return;
		
		//remove GEN_TYPE first so it forces the PropertyChangeEvent to be fired.
		props.remove(GEN_TYPE);
		
		switch(type)
		{
			case Custom:
				props.put(SOURCE, props.get(SOURCE_PROXY_LIST));
				sheet.writeField(props, GEN_TYPE.id, Types._String);
				break;
			case Resource:
				StencylResourceHaxeType<?> srht = props.get(SOURCE_PROXY_RESOURCE);
				props.put(SOURCE, srht.srt.getList());
				sheet.writeField(props, GEN_TYPE.id, srht.dataType);
				break;
			case Structure:
				StructureHaxeType sht = props.get(SOURCE_PROXY_STRUCTURE);
				props.put(SOURCE, Structures.getList(sht.type.def));
				sheet.writeField(props, GEN_TYPE.id, sht.dataType);
				break;
		}
	}
}

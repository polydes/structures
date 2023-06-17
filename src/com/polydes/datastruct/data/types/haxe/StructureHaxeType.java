package com.polydes.datastruct.data.types.haxe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.StructureType;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.ui.StructureIconProvider;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;

import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.ExtrasKey;
import stencyl.core.api.datatypes.properties.PropertyKey;
import stencyl.core.api.fs.Locations;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.datatypes.Types;
import stencyl.core.util.Lang;

import static com.polydes.datastruct.data.types.StructureType.*;

public class StructureHaxeType extends HaxeDataType
{
	StructureType type;
	
	public StructureHaxeType(StructureType structureType)
	{
		super(structureType, structureType.def.getFullClassname(), "OBJECT");
		this.type = structureType;
	}

	private static final PropertyKey<String> FILTER_PROXY = new PropertyKey<>("_" + SOURCE_FILTER.id);
	
	@Override
	public List<String> generateHaxeClass()
	{
		File file = new File(Locations.getGameExtensionLocation("com.polydes.datastruct"), "templates/Structure.hx");
		String s = Text.readString(file);
		
		String fieldmap = "";
		String variables = "";
		String typeinfo = "";
		typeinfo += "\t\tm.set(\"name\", \"String\");\n";
		for(StructureField f : type.def.getFields())
		{
			fieldmap += String.format("\t\tm.set(\"%s\", \"%s\");\n", f.getLabel(), f.getVarname());
			variables += String.format("\tpublic var %s:%s;\n", f.getVarname(), getType(f));
			typeinfo += String.format("\t\tm.set(\"%s\", \"%s\");\n", f.getVarname(), f.getType().getHaxeType());
		}
		
		s = StringUtils.replace(s, "[PACKAGE]", getPackage());
		s = StringUtils.replace(s, "[CLASSNAME]", getSimpleClassname());
		s = StringUtils.replace(s, "[FIELDMAP]", fieldmap, 1);
		s = StringUtils.replace(s, "[TYPEINFO]", typeinfo, 1);
		s = StringUtils.replace(s, "[VARIABLES]", variables, 1);
		s = StringUtils.replace(s, "[CUSTOM]", type.def.customCode + runCustomCodeInjector(type.def), 1);
		
		ArrayList<String> lines = new ArrayList<String>();
		for(String s2 : s.split("\n"))
			lines.add(s2);
		
		return lines;
	}
	
	private String getType(StructureField f)
	{
		//TODO: Formalize type parameters again instead of handling it explicitly
		String type = f.getType().getHaxeType();
		if(type.equals("Array"))
			type = "Array<Dynamic>";
		else if(type.equals("Set"))
			type = "Set<Dynamic>";
		if(f.isOptional())
			return "Null<" + type + ">";
		else
			return type;
	}
	
	private String runCustomCodeInjector(StructureDefinition def)
	{
		StringBuilder builder = new StringBuilder();
		runCustomCodeInjector(def.guiRoot, builder);
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	private <S extends SDE> void runCustomCodeInjector(DefaultLeaf d, StringBuilder builder)
	{
		S sde = (S) d.getUserData();
		SDEType<S> sdetype = (SDEType<S>) SDETypes.fromClass(sde.getClass());
		sdetype.genCode(sde, builder);
		
		if(d instanceof DefaultBranch)
			for(DefaultLeaf item : ((DefaultBranch) d).getItems())
				runCustomCodeInjector(item, builder);
	}
	
	@Override
	public List<String> generateHaxeReader()
	{
		return Lang.arraylist("StringData.registerStructureReader(\"" + getHaxeType() + "\");");
	}
	
	//SERIALIZATION KEYS -- do not change these.
	private static final ExtrasKey<String> KEY_SOURCE_FILTER  = new ExtrasKey<>(FILTER_PROXY, "sourceFilter");
	private static final ExtrasKey<Boolean> KEY_ALLOW_SUBTYPES = new ExtrasKey<>(ALLOW_SUBTYPES, "allowSubtypes");
	private static final ExtrasKey<Boolean> KEY_RENDER_PREVIEW = new ExtrasKey<>(RENDER_PREVIEW, "renderPreview");
	
	@Override
	public DataTypeProperties loadExtras(ExtrasMap extras)
	{
		DataTypeProperties props = new DataTypeProperties();
		String filterText = extras.get(KEY_SOURCE_FILTER, Types._String, null);
		if(filterText != null)
			props.put(SOURCE_FILTER, new StructureCondition(null, filterText));
		if(extras.containsKey(KEY_RENDER_PREVIEW))
			props.put(RENDER_PREVIEW, Boolean.TRUE);
		props.put(ALLOW_SUBTYPES, extras.get(KEY_ALLOW_SUBTYPES, Types._Bool, false));
		return props;
	}

	@Override
	public ExtrasMap saveExtras(DataTypeProperties props, DataContext ctx)
	{
		ExtrasMap emap = new ExtrasMap(ctx);
		if(props.containsKey(SOURCE_FILTER))
			emap.put(KEY_SOURCE_FILTER, Types._String, props.get(SOURCE_FILTER).getText());
		if(props.get(ALLOW_SUBTYPES) == Boolean.TRUE)
			emap.put(KEY_ALLOW_SUBTYPES, Types._Bool, Boolean.TRUE);
		if(props.get(RENDER_PREVIEW) == Boolean.TRUE)
			emap.put(KEY_RENDER_PREVIEW, Types._Bool, Boolean.TRUE);
		return emap;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		DataTypeProperties props = panel.getExtras();
		
		PropertiesSheetSupport sheet = panel.getEditorSheet();
		sheet.build()
			.field(FILTER_PROXY.id).optional()._editor(Types._String).add()
			.field(RENDER_PREVIEW.id)._editor(Types._Bool).add()
			.finish();
		
		sheet.addPropertyChangeListener(FILTER_PROXY.id, event -> {
			StructureCondition condition = props.get(SOURCE_FILTER);
			String conditionText = props.get(FILTER_PROXY);
			
			if(condition == null && !conditionText.isEmpty())
				condition = new StructureCondition(null, conditionText);
			else if(condition != null && conditionText.isEmpty())
				condition = null;
			else if(condition != null && !conditionText.isEmpty())
				condition.setText(conditionText);
			
			//remove SOURCE_FILTER first so it forces the PropertyChangeEvent to be fired.
			props.remove(SOURCE_FILTER);
			sheet.writeField(props, SOURCE_FILTER.id, condition);
		});
	}
	
	@Override
	public ImageIcon getIcon(Object value)
	{
		if(value instanceof Structure)
			return StructureIconProvider.getStructureIcon((Structure) value);
		return null;
	}
	
	@Override
	public boolean isIconProvider()
	{
		return true;
	}
}

package com.polydes.datastruct.data.types;

import org.apache.log4j.Logger;

import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureCondition;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.PropertyKey;

public class StructureType extends DataType<Structure>
{
	private static final Logger log = Logger.getLogger(StructureType.class);
	
	public StructureDefinition def;
	
	public StructureType(StructureDefinition def)
	{
		super(Structure.class, def.getFullClassname());
		this.def = def;
	}
	
	public static final PropertyKey<StructureCondition> SOURCE_FILTER  = new PropertyKey<>("sourceFilter");
	public static final PropertyKey<Boolean>            ALLOW_SUBTYPES = new PropertyKey<>("allowSubtypes");
	public static final PropertyKey<Boolean>            RENDER_PREVIEW = new PropertyKey<>("renderPreview");
	
	@Override
	public Structure decode(String s, DataContext ctx)
	{
		try
		{
			int id = Integer.parseInt(s);
			Structure model = Structures.getStructure(id);
			
			if(model == null)
			{
				log.warn("Couldn't load structure with id " + s + ". It no longer exists.");
				return null;
			}
			if(!model.getTemplate().is(def) && !model.getTemplate().isUnknown() && !def.isUnknown())
			{
				log.warn("Couldn't load structure with id " + s + " as type " + def.getName());
				return null;
			}
			
			return model;
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}

	@Override
	public String encode(Structure model, DataContext ctx)
	{
		if(model == null)
			return "";
		
		return "" + model.getID();
	}

	@Override
	public String toDisplayString(Structure data)
	{
		return String.valueOf(data);
	}
	
	@Override
	public Structure copy(Structure t)
	{
		return t;
	}
}
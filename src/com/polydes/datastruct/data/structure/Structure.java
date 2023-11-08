package com.polydes.datastruct.data.structure;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.HaxeTypeConverter;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultLeaf;

public class Structure
{
	private static final Logger log = Logger.getLogger(Structure.class);
	
	private static HashMap<StructureDefinition, ArrayList<Structure>> allStructures = new HashMap<>();
	
	public final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private StructureDefinition template;
	private HashMap<StructureField, Object> fieldData;
	private HashMap<StructureField, Boolean> enabledFields;
	private int id;
	
	public DefaultLeaf dref;
	
	public Structure(int id, String name, String templateName)
	{
		StructureDefinitions defs = DataStructuresExtension.get().getStructureDefinitions();
		StructureDefinition def = defs.requestValue(templateName, value -> realizeTemplate(value));
		load(id, name, def);
	}
	
	public Structure(int id, String name, StructureDefinition template)
	{
		load(id, name, template);
	}
	
	private void load(int id, String name, StructureDefinition template)
	{
		this.id = id;
		this.template = template;
		fieldData = new HashMap<>();
		enabledFields = new HashMap<>();
		
		allStructures.get(template).add(this);
		
		dref = new DefaultLeaf(name, this);
	}
	
	public void loadDefaults()
	{
		String name = dref.getName();
		
		for(StructureField f : template.getFields())
		{
			Object setTo = null;
			
			if(f.getEditorProperties() != null)
			{
				if(f.getDefaultValue() instanceof Object defValue)
					setTo = f.getType().dataType.checkCopy(defValue);
			}
			else if(!f.isOptional())
			{
				setTo = f.getType().dataType.decode("", template.getCtx());
			}

			enabledFields.put(f, setTo != null);
			if(setTo != null)
			{
				fieldData.put(f, setTo);
				pcs.firePropertyChange(f.getVarname(), null, setTo);
				log.debug(name + "::" + f.getVarname() + "=" + setTo + " (init)");
			}
		}
	}
	
	public int getID()
	{
		return id;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
	
	public void setPropertyFromString(StructureField field, String value)
	{
		DataContext ctx = getTemplate().getCtx();
		Object newValue = HaxeTypeConverter.decode(field.getType().dataType, value, ctx);
		Object oldValue = fieldData.get(field);
		fieldData.put(field, newValue);
		pcs.firePropertyChange(field.getVarname(), oldValue, newValue);
		dref.setDirty(true);
		
		log.debug(dref.getName() + "::" + field.getVarname() + "=" + oldValue + " -> " + newValue + " (by string)");
	}
	
	public void setProperty(StructureField field, Object value)
	{
		Object oldValue = fieldData.get(field);
		fieldData.put(field, value);
		pcs.firePropertyChange(field.getVarname(), oldValue, value);
		dref.setDirty(true);
		
		log.debug(dref.getName() + "::" + field.getVarname() + "=" + oldValue + " -> " + value + " (by object)");
	}
	
	public void clearProperty(StructureField field)
	{
		Object oldValue = fieldData.get(field);
		fieldData.put(field, null);
		enabledFields.put(field, !field.isOptional());
		pcs.firePropertyChange(field.getVarname(), oldValue, null);
		dref.setDirty(true);
	}
	
	public boolean isPropertyEnabled(StructureField field)
	{
		return enabledFields.get(field) == Boolean.TRUE;
	}
	
	public void setPropertyEnabled(StructureField field, boolean value)
	{
		enabledFields.put(field, value);
	}
	
	public Object getPropByName(String name)
	{
		StructureField f = template.getField(name);
		if(f == null)
			return null;
		
		return getProperty(f);
	}
	
	public Object getProperty(StructureField field)
	{
		return fieldData.get(field);
	}
	
	public StructureField getField(String name)
	{
		return template.getField(name);
	}
	
	public Collection<StructureField> getFields()
	{
		return template.getFields();
	}

	public StructureDefinition getTemplate()
	{
		return template;
	}
	
	public boolean checkCondition(StructureCondition condition)
	{
		if(condition == null)
			return true;
		
		return condition.check(this);
	}
	
	public String getDefname()
	{
		return template.getName();
	}
	
	public Structure copy()
	{
		Structure newStructure = new Structure(id, dref.getName(), template);
		
		for(StructureField field : getFields())
		{
			newStructure.setProperty(field, field.getType().dataType.checkCopy(getProperty(field)));
			newStructure.enabledFields.put(field, enabledFields.get(field));
		}
		
		return newStructure;
	}
	
	public void assignTo(Structure structure)
	{
		for(StructureField field : getFields())
		{
			setProperty(field, field.getType().dataType.checkCopy(structure.getProperty(field)));
			enabledFields.put(field, structure.enabledFields.get(field));
		}
	}
	
	public static void removeType(StructureDefinition def)
	{
		for(Structure s : allStructures.remove(def))
			s.dispose();
	}
	
	public void dispose()
	{
		fieldData.clear();
		enabledFields.clear();
		if(Structures.structures.containsKey(template))
		{
			Structures.structures.get(template).remove(this);
		}
		Structures.structuresByID.remove(getID());
	}
	
	public static ArrayList<Structure> getAllOfType(StructureDefinition def)
	{
		return allStructures.get(def);
	}

	public static void addType(StructureDefinition def)
	{
		allStructures.put(def, new ArrayList<>());
	}
	
	@Override
	public String toString()
	{
		return dref.getName();
	}
	
	private Map<String, String> unknownData;
	
	class NoNullStringMap extends HashMap<String,String>
	{
		@Override
		public String put(String key, String value)
		{
			if(value == null)
				return super.remove(key);
			else
				return super.put(key, value);
		}
	}
	
	public Map<String, String> getUnknownData()
	{
		return unknownData;
	}
	
	public void setUnknownProperty(String key, String value)
	{
		if(unknownData == null)
			unknownData = new NoNullStringMap();
		unknownData.put(key, value);
	}
	
	public void realizeTemplate(StructureDefinition def)
	{
		if(fieldData == null) //not yet loaded
			return;
		
		log.info("Realizing unknown structure " + dref.getName() + " as " + def.getFullClassname());
		
		StructureDefinition oldTemplate = template;
		template = def;
		
		if(oldTemplate != template)
		{
			for(StructureField f : template.getFields())
			{
				Object value = HaxeTypeConverter.decode(f.getType().dataType, "", template.getCtx());
				fieldData.put(f, value);
				enabledFields.put(f, !f.isOptional());
				pcs.firePropertyChange(f.getVarname(), null, value);
				log.debug(dref.getName() + "::" + f.getVarname() + "=" + " -> " + value + " (init by string)");
			}
			
			allStructures.get(oldTemplate).remove(this);
			allStructures.get(template).add(this);
		}
		
		if(unknownData != null)
		{
			dref.markAsLoading(true);
			for(Iterator<Entry<String,String>> it = unknownData.entrySet().iterator(); it.hasNext(); )
			{
				Entry<String,String> entry = it.next();
				StructureField field = template.getField(entry.getKey());
				if(field == null)
					continue;
				
				setPropertyFromString(field, entry.getValue());
				setPropertyEnabled(field, true);
				it.remove();
			}
			if(unknownData.isEmpty())
				unknownData = null;
			dref.markAsLoading(false);
		}
	}
}
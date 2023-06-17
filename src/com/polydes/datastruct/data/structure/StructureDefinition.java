package com.polydes.datastruct.data.structure;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.folder.FolderPolicy;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.structure.elements.StructureTabset;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.ext.registry.RegistryObject;
import stencyl.core.lib.IProject;

public class StructureDefinition implements RegistryObject
{
	public static FolderPolicy STRUCTURE_DEFINITION_POLICY = new StructureDefinitionEditingPolicy();
	
	private BufferedImage iconImg;
	
	private IProject project;
	private DataContext ctx;
	private String name;
	private String classname; // registry key
	
	public String iconSource;
	public String customCode = "";
	private final LinkedHashMap<String, StructureField> fields;
	public DefaultLeaf dref;
	public Folder guiRoot; //this is passed in from elsewhere.
	
	
	public StructureDefinition parent = null;
	
	public StructureDefinition(IProject project, String name, String classname)
	{
		this.project = project;
		ctx = DataContext.fromMap(Map.of("Project", project));
		this.name = name;
		this.classname = classname;
		fields = new LinkedHashMap<String, StructureField>();
		customCode = "";
		
		Structure.addType(this);
		
		dref = new DefaultLeaf(name, this);
		
		guiRoot = new Folder("root", new StructureTable(this));
		guiRoot.setPolicy(STRUCTURE_DEFINITION_POLICY);
		guiRoot.addListener(DefaultLeaf.DIRTY, event -> {
			dref.setDirty(guiRoot.isDirty());
		});
		dref.addListener(DefaultLeaf.DIRTY, event -> {
			guiRoot.setDirty(dref.isDirty());
		});
	}
	
	public void dispose()
	{
		Structure.removeType(this);
		dref = null;
		guiRoot = null;
		project = null;
	}
	
	public void setImage(BufferedImage image)
	{
		this.iconImg = image;
	}
	
	public IProject getProject()
	{
		return project;
	}
	
	public DataContext getCtx()
	{
		return ctx;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Display name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * com.package.Class
	 */
	public String getFullClassname()
	{
		return classname;
	}
	
	/**
	 * (com.package.)Class
	 */
	public String getSimpleClassname()
	{
		return StringUtils.substringAfterLast(classname, ".");
	}
	
	/**
	 * com.package(.Class)
	 */
	public String getPackage()
	{
		if(classname.indexOf('.') == -1)
			return StringUtils.EMPTY;
		else
			return StringUtils.substringBeforeLast(classname, ".");
	}

	public void changeClassname(String newClassname)
	{
		DataStructuresExtension.get().getStructureDefinitions().renameItem(this, newClassname);
		Structures.root.setDirty(true);
	}
	
	public BufferedImage getIconImg()
	{
		return iconImg;
	}
	
	public StructureField getField(String name)
	{
		return fields.get(name);
	}
	
	public Collection<StructureField> getFields()
	{
		return fields.values();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public void addField(StructureField f)
	{
		fields.put(f.getVarname(), f);
	}
	
	public void removeField(StructureField f)
	{
		fields.remove(f.getVarname());
	}

	public void setFieldName(StructureField f, String name)
	{
		fields.remove(f.getVarname());
		fields.put(name, f);
	}
	
	public void setDirty(boolean value)
	{
		guiRoot.setDirty(value);
	}
	
	public boolean isDirty()
	{
		return guiRoot.isDirty();
	}

	/*-------------------------------------*\
	 * Inheritence
	\*-------------------------------------*/ 
	
	public boolean is(StructureDefinition def)
	{
		return this == def || (parent != null && parent.is(def));
	}

	//===
	
	static class StructureDefinitionEditingPolicy extends FolderPolicy
	{
		public StructureDefinitionEditingPolicy()
		{
			duplicateItemNamesAllowed = false;
			folderCreationEnabled = false;
			itemCreationEnabled = true;
			itemEditingEnabled = false;
			itemRemovalEnabled = true;
		}
		
		@Override
		public boolean canAcceptItem(Folder folder, DefaultLeaf item)
		{
			boolean tabset = folder.getUserData() instanceof StructureTabset;
			boolean tab = item.getUserData() instanceof StructureTab;
			
			if(tabset != tab)
				return false;
			
			return super.canAcceptItem(folder, item);
		}
	}
	
	/*-------------------------------------*\
	 * Unknown Definitions
	\*-------------------------------------*/ 
	
	private boolean unknown;

	public boolean isUnknown()
	{
		return unknown;
	}
	
	public void realize(String name, String classname)
	{
		this.name = name;
		this.classname = classname;
		dref.markAsLoading(true);
		dref.setName(name);
		dref.markAsLoading(false);
		unknown = false;
	}
	
	public static StructureDefinition newUnknown(IProject project, String name)
	{
		StructureDefinition def = new StructureDefinition(project, name, name);
		def.unknown = true;
		return def;
	}

	public void realizeFieldHaxeType(StructureField field, HaxeDataType t)
	{
		//like StructureField::realizeRO, this requires than when a HaxeDataType
		//has been registered, it already has a valid DataType<?>
		if(Structures.structures.containsKey(this))
			for(Structure struct : Structures.structures.get(this))
				if(struct.getProperty(field) != null)
					struct.setPropertyFromString(field, (String) struct.getProperty(field));
	}
	
	@Override
	public String getKey()
	{
		return classname;
	}
	
	@Override
	public void setKey(String newKey)
	{
		this.classname = newKey;
	}
}
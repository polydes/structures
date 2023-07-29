package com.polydes.datastruct.data.structure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.folder.FolderPolicy;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.StructureType;
import com.polydes.datastruct.data.types.haxe.StructureHaxeType;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.io.read.StructureDefinitionReader;
import com.polydes.datastruct.io.write.StructureDefinitionWriter;
import com.polydes.datastruct.ui.datatypes.StructureEditorDE;

import stencyl.app.api.datatypes.EditorProviders;
import stencyl.app.api.datatypes.EditorProviders.EditorInitializer;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.fs.LocationProvider.DefaultLocations;
import stencyl.core.api.fs.Locations;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.datatypes.Types;
import stencyl.core.ext.registry.ObjectRegistry;
import stencyl.core.ext.res.ResourceLoader;
import stencyl.core.ext.res.Resources;
import stencyl.core.io.FileHelper;
import stencyl.core.lib.IProject;

import static stencyl.core.api.datatypes.DataType.UNSET_EDITOR;

public class StructureDefinitions extends ObjectRegistry<StructureDefinition>
{
	private static final Logger log = Logger.getLogger(StructureDefinitions.class);
	private static Resources res = ResourceLoader.getResources("com.polydes.datastruct");

	private IProject project;
	public Folder root;
	private HashMap<Folder, File> baseFolders;

	public StructureDefinitions(IProject project)
	{
		this.project = project;
		root = new Folder("Structure Definitions");
		baseFolders = new HashMap<Folder, File>();

		FolderPolicy policy = new FolderPolicy()
		{
			@Override
			public boolean canAcceptItem(Folder folder, DefaultLeaf item)
			{
				return false;
			}
		};
		policy.folderCreationEnabled = false;
		policy.itemCreationEnabled = false;
		policy.itemEditingEnabled = false;
		policy.itemRemovalEnabled = false;
		root.setPolicy(policy);
	}

	public void addFolder(File fsfolder, String name)
	{
		Folder newFolder = new Folder(name);
		newFolder.setPolicy(new UniqueRootPolicy());
		baseFolders.put(newFolder, fsfolder);
		for(File f : FileHelper.listFiles(fsfolder))
			load(f, newFolder);
		root.markAsLoading(true);
		root.addItem(newFolder);
		root.markAsLoading(false);
	}

	public void load(File fsfile, Folder dsfolder)
	{
		dsfolder.markAsLoading(true);
		if(fsfile.isDirectory())
		{
			Folder newFolder = new Folder(fsfile.getName());
			for(File f : fsfile.listFiles())
				load(f, newFolder);
		}
		else
		{
			if(fsfile.getName().endsWith(".xml"))
			{
				dsfolder.addItem(loadDefinition(fsfile).dref);
			}
		}
		dsfolder.markAsLoading(false);
	}

	//If the definition already exists as an unknown definition,
	//it will simply have its data set
	public StructureDefinition loadDefinition(File fsfile)
	{
		String fname = fsfile.getName();

		if(!fname.endsWith(".xml"))
			return null;

		String defname = fname.substring(0, fname.length() - 4);
		Element structure;
		try
		{
			structure = FileHelper.readXMLFromFile(fsfile).getDocumentElement();
		}
		catch(IOException ex)
		{
			log.error("Failed to read structure definition from " + fsfile, ex);
			return null;
		}
		
		String classname = structure.getAttribute("classname");

		StructureDefinition def = isUnknown(classname) ?
			getItem(classname) :
			new StructureDefinition(project, defname, classname);
		if(def.isUnknown())
			def.realize(defname, classname);

		StructureDefinitionReader.read(structure, def);

		File parent = fsfile.getParentFile();

		File haxeFile = new File(parent, defname + ".hx");
		if(haxeFile.exists())
			def.customCode = Text.readString(haxeFile);

		try
		{
			def.setImage(ImageIO.read(new File(parent, defname + ".png")));
		}
		catch (IOException e)
		{
			log.warn("Couldn't load icon for Structure Definition " + def.getName());
			def.setImage(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB));
		}

		registerItem(def);

		return def;
	}

	@Override
	public StructureDefinition generatePlaceholder(String key)
	{
		StructureDefinition def = StructureDefinition.newUnknown(project, key);
		def.setImage(res.loadImage("question-32.png"));
		return def;
	}

	private void registerWithLists(StructureDefinition def)
	{
		if(Structures.structures.containsKey(def))
			return;

		Structures.structures.put(def, new ArrayList<Structure>());

		StructureType structureType = new StructureType(def);
		StructureHaxeType newHaxeType = new StructureHaxeType(structureType);

		Types.get().loadReference(structureType);
		DataStructuresExtension.get().getHaxeTypes().registerItem(newHaxeType);

		Map<DataType<?>, Map<String, EditorInitializer<?>>> editors = EditorProviders.editors;
		editors.put(structureType, Map.of(
			UNSET_EDITOR, (EditorInitializer) (props, sheet, style) -> new StructureEditorDE(structureType, props, sheet, style)
		));
	}

	@Override
	protected void preregisterItem(StructureDefinition def)
	{
		registerWithLists(def);
		super.preregisterItem(def);
	}

	@Override
	public void registerItem(StructureDefinition def)
	{
		registerWithLists(def);
		super.registerItem(def);
	}

	@Override
	public void unregisterItem(StructureDefinition def)
	{
		super.unregisterItem(def);
		for(Structure s : Structures.structures.get(def))
			Structures.model.removeItem(s.dref, s.dref.getParent());
		
		HaxeDataType hdt = DataStructuresExtension.get().getHaxeTypes().getItem(def.getKey());
		DataStructuresExtension.get().getHaxeTypes().unregisterItem(def.getKey());
		Types.get().unloadReference(hdt.dataType);
		EditorProviders.editors.remove(hdt.dataType);
		
		Structures.structures.remove(def);
		def.dispose();
	}

	@Override
	public void renameItem(StructureDefinition value, String newName)
	{
		String oldKey = value.getKey();

		super.renameItem(value, newName);

		HaxeDataType hdt = DataStructuresExtension.get().getHaxeTypes().getItem(oldKey);
		Types.get().unloadReference(hdt.dataType);
		DataStructuresExtension.get().getHaxeTypes().renameItem(oldKey, newName);
		Types.get().loadReference(hdt.dataType);
	}

	public void saveChanges() throws IOException
	{
		for(Folder dsfolder : baseFolders.keySet())
		{
			if(dsfolder.isDirty())
			{
				File fsfolder = baseFolders.get(dsfolder);
				File temp = new File(DefaultLocations.getLocation(Locations.STENCYL_TEMP) + File.separator + "data structure defs save");
				temp.mkdirs();

				FileUtils.deleteDirectory(temp);
				temp.mkdirs();

				for(DefaultLeaf d : dsfolder.getItems())
					save(d, temp);

				FileUtils.deleteDirectory(fsfolder);
				fsfolder.mkdirs();
				FileUtils.copyDirectory(temp, fsfolder);
			}
		}
		root.setDirty(false);
	}

	public void save(DefaultLeaf item, File file) throws IOException
	{
		if(item instanceof DefaultBranch)
		{
			File saveDir = new File(file, item.getName());
			if(!saveDir.exists())
				saveDir.mkdirs();

			for(DefaultLeaf d : ((DefaultBranch) item).getItems())
				save(d, saveDir);
		}
		else
		{
			StructureDefinition def = (StructureDefinition) item.getUserData();

			Document doc = FileHelper.newDocument();
			Element e = doc.createElement("structure");
			StructureDefinitionWriter.write(doc, e, def);
			doc.appendChild(e);
			FileHelper.writeXMLToFile(doc, new File(file, def.getName() + ".xml"));
			if(def.getIconImg() != null)
				ImageIO.write(def.getIconImg(), "png", new File(file, def.getName() + ".png"));
			if(!def.customCode.isEmpty())
				FileUtils.writeStringToFile(new File(file, def.getName() + ".hx"), def.customCode);
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();
		baseFolders.clear();
		root = null;
	}

	class UniqueRootPolicy extends FolderPolicy
	{
		public UniqueRootPolicy()
		{
			duplicateItemNamesAllowed = false;
			folderCreationEnabled = false;
			itemCreationEnabled = true;
			itemEditingEnabled = true;
			itemRemovalEnabled = true;
		}

		@Override
		public boolean canAcceptItem(Folder folder, DefaultLeaf item)
		{
			Folder fromFolder = (item instanceof Folder) ?
				(Folder) item :
				(Folder) item.getParent();

			boolean sameRoot = (fromFolder.getPolicy() == this);

			return super.canAcceptItem(folder, item) && sameRoot;
		}
	}

	public void removeFolder(File fsfolder)
	{
		for(Entry<Folder, File> f : baseFolders.entrySet())
		{
			if(f.getValue().equals(fsfolder))
			{
				root.removeItem(f.getKey());
				baseFolders.remove(f.getKey());
			}
		}
	}
}
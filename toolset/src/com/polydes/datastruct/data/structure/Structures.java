package com.polydes.datastruct.data.structure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.HaxeTypeConverter;
import com.polydes.datastruct.io.FolderInfo;
import com.polydes.datastruct.io.Text;

import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;
import stencyl.core.io.FileHelper;

import static stencyl.core.util.PathHelper.abs;
import static stencyl.core.util.PathHelper.filenameOf;

public class Structures
{
//	private static final Logger log = Logger.getLogger(Structures.class);

	private static int nextID = 0;
	private static Structures _instance;
	public static HierarchyModel<DefaultLeaf,DefaultBranch> model;
	public static StructureFolder root;

	private Structures()
	{
		root = new StructureFolder("Structures");
		model = new HierarchyModel<>(root, DefaultLeaf.class, DefaultBranch.class);
	}

	public static Structures get()
	{
		if(_instance == null)
			_instance = new Structures();

		return _instance;
	}

	//Loading is done in two passes.

	//Lightload: The first pass just gets id and type of each object
	//Maps are populated with this data

	//Deepload: The second pass reads in the key-value pairs, which might include refs to other structures.

	private HashMap<String, HashMap<String, String>> fmaps;

	public void load(Path folder)
	{
		fmaps = new HashMap<>();

		FolderInfo info = new FolderInfo(folder);
		ArrayList<String> order = info.getFileOrder();
		for(String fname : order)
		{
			Path file = folder.resolve(fname);
			lightload(file);
		}
		root.markAsLoading(true);
		for(String fname : order)
		{
			Path file = folder.resolve(fname);
			deepload(root, file);
		}
		root.markAsLoading(false);
		order.clear();
		info.clear();

		fmaps.clear();
		fmaps = null;
	}

	public void lightload(Path file)
	{
		if(!Files.exists(file))
			return;

		if(Files.isDirectory(file))
		{
			for(Path f : FileHelper.listFilesSimple(file))
				if(!filenameOf(f).equals(FolderInfo.FOLDER_INFO_FILENAME))
					lightload(f);
		}
		else
		{
			HashMap<String, String> map = Text.readKeyValues(file);
			fmaps.put(abs(file), map);

			String name = filenameOf(file);
			int id = Integer.parseInt(map.get("struct_id"));
			String type = map.remove("struct_type");
			nextID = Math.max(nextID, id + 1);

			Structure model = new Structure(id, name, type);
			structures.get(model.getTemplate()).add(model);
			structuresByID.put(model.getID(), model);
		}
	}

	public void deepload(StructureFolder folder, Path file)
	{
		if(!Files.exists(file))
			return;

		if(Files.isDirectory(file))
		{
			StructureFolder newFolder = new StructureFolder(filenameOf(file));
			newFolder.markAsLoading(true);
			FolderInfo info = new FolderInfo(file);
			ArrayList<String> order = info.getFileOrder();
			if(info.containsKey("childType"))
				newFolder.childType = DataStructuresExtension.get().getStructureDefinitions().getItem(info.get("childType"));
			for(String fname : order)
				deepload(newFolder, file.resolve(fname));
			newFolder.markAsLoading(false);
			folder.addItem(newFolder);
		}
		else
		{
			HashMap<String, String> map = fmaps.get(file.toAbsolutePath().toString());
			Structure model = structuresByID.get(Integer.parseInt(map.remove("struct_id")));

			boolean unknown = model.getTemplate().isUnknown();

			for(Entry<String, String> entry : map.entrySet())
			{
				StructureField f = model.getField(entry.getKey());
				if(f == null || unknown)
				{
					model.setUnknownProperty(entry.getKey(), entry.getValue());
					continue;
				}
				model.setPropertyFromString(f, entry.getValue());
				model.setPropertyEnabled(f, true);
			}

			folder.addItem(model.dref);
		}
	}

	public void saveChanges(Path file) throws IOException
	{
		if(root.isDirty())
		{
			FolderInfo info = new FolderInfo();

			for(DefaultLeaf d : root.getItems())
			{
				save(d, file);
				info.addFilenameToOrder(d.getName());
			}

			info.writeToFolder(file);
			info.clear();
		}
		root.setDirty(false);
	}

	public void save(DefaultLeaf item, Path file) throws IOException
	{
		if(item instanceof DefaultBranch)
		{
			Path saveDir = file.resolve(item.getName());
			Files.createDirectories(saveDir);

			FolderInfo info = new FolderInfo();

			for(DefaultLeaf d : ((DefaultBranch) item).getItems())
			{
				save(d, saveDir);
				info.addFilenameToOrder(d.getName());
			}

			if(((StructureFolder) item).childType != null)
				info.put("childType", ((StructureFolder) item).childType.getName());

			info.writeToFolder(saveDir);
			info.clear();
		}
		else
		{
			Structure s = (Structure) item.getUserData();
			ArrayList<String> toWrite = new ArrayList<>();
			toWrite.add("struct_id=" + s.getID());
			toWrite.add("struct_type=" + s.getTemplate().getFullClassname());

			for(StructureField field : s.getFields())
			{
				if(field.isOptional() && !s.isPropertyEnabled(field))
					continue;
				Object o = s.getProperty(field);
				if(o == null)
					continue;
				toWrite.add(field.getVarname() + "=" + HaxeTypeConverter.encode(field.getType().dataType, o, s.getTemplate().getCtx()));
			}
			if(s.getUnknownData() != null)
				for(Entry<String, String> entry : s.getUnknownData().entrySet())
					toWrite.add(entry.getKey() + "=" + entry.getValue());

			Files.write(file.resolve(item.getName()), toWrite);
		}
	}

	public static HashMap<StructureDefinition, ArrayList<Structure>> structures = new HashMap<>();
	public static HashMap<Integer, Structure> structuresByID = new HashMap<>();

	public static Collection<Structure> getList(StructureDefinition type)
	{
		return structures.get(type);
	}

	public static Structure getStructure(int i)
	{
		return structuresByID.get(i);
	}

	public static int newID()
	{
		return nextID++;
	}

	public static void dispose()
	{
		for(StructureDefinition key : structures.keySet())
			for(Structure s : structures.get(key))
				s.dispose();
		structures.clear();
		structuresByID.clear();
		_instance = null;
		root = null;
		nextID = 0;
	}
}

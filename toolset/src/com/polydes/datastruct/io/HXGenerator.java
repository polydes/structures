package com.polydes.datastruct.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.core.api.fs.Locations;
import stencyl.core.io.FileHelper;

import static stencyl.core.util.PathHelper.filenameOf;

public class HXGenerator
{
	public static List<String> generateFileList(Path dir)
	{
		List<String> toWrite = new ArrayList<>();
		
		for(Path f : FileHelper.listFilesSimple(dir))
			addFile("", f, toWrite);
		
		return toWrite;
	}
	
	public static void addFile(String prepend, Path file, List<String> toWrite)
	{
		String filename = filenameOf(file);
		if(Files.isDirectory(file))
		{
			prepend += filename + "/";
			for(Path f : FileHelper.listFilesSimple(file))
				addFile(prepend, f, toWrite);
		}
		else
		{
			if(!filename.equals("folderinfo.txt"))
				toWrite.add(prepend + filename);
		}
	}
	
	public static List<String> generateReader()
	{
		Path engineRoot = DataStructuresExtension.get().getInfo().getEngineRootPath();
		Path f = engineRoot.resolve("templates/DataStructureReader.hx");
		String s = Text.readString(f);
		
		StringBuilder imports = new StringBuilder();
		StringBuilder classmap = new StringBuilder();
		StringBuilder datatypes = new StringBuilder();
		for(StructureDefinition def : DataStructuresExtension.get().getStructureDefinitions().values())
		{
			imports.append(String.format("import %s;\n", def.getFullClassname()));
			classmap.append(String.format("\t\tclassmap.set(\"%s\", Type.resolveClass(\"%s\"));\n", def.getFullClassname(), def.getFullClassname()));
			datatypes.append(String.format("\t\tDataStructures.types.set(\"%s\", \"\");\n", def.getFullClassname()));
		}
		
		s = StringUtils.replace(s, "[IMPORTS]", imports.toString(), 1);
		s = StringUtils.replace(s, "[CLASSMAP]", classmap.toString(), 1);
		s = StringUtils.replace(s, "[DATATYPES]", datatypes.toString(), 1);
		
		StringBuilder readers = new StringBuilder();
		
		for(HaxeDataType type : DataStructuresExtension.get().getHaxeTypes().values())
		{
			List<String> lines = type.generateHaxeReader();
			if(lines != null)
				for(String line : lines)
					readers.append(line).append("\n");
		}
		
		s = StringUtils.replace(s, "[READERS]", readers.toString(), 1);

        return new ArrayList<>(Arrays.asList(s.split("\n")));
	}
}

package com.polydes.datastruct.ext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeObjectType;
import com.polydes.datastruct.data.types.haxe.HaxeObjectHaxeType;
import com.polydes.datastruct.io.read.HaxeObjectDefinitionReader;

import stencyl.app.api.datatypes.EditorProviders;
import stencyl.app.api.datatypes.EditorProviders.EditorInitializer;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.datatypes.Types;
import stencyl.core.io.FileHelper;

import static stencyl.core.api.datatypes.DataType.UNSET_EDITOR;
import static stencyl.core.util.PathHelper.extensionOf;

public class HaxeDataTypeExtension
{
	/*-------------------------------------*\
	 * Building from xml
	\*-------------------------------------*/ 
	
	public static ArrayList<HaxeDataType> readTypesFolder(Path f, DataContext ctx)
	{
		ArrayList<HaxeDataType> types = new ArrayList<>();
		
		for(Path file : FileHelper.listFiles(f, true, false))
			if(extensionOf(file).equals("xml"))
				types.add(readType(file, ctx));
		
		return types;
	}
	
	public static HaxeDataType readType(Path path, DataContext ctx)
	{
		HaxeObjectType newDT = new HaxeObjectType(HaxeObjectDefinitionReader.read(path, ctx));
		//TODO [2023-06-18] these aren't unregistered anywhere
		Types.get().loadReference(newDT);
		EditorProviders.editors.put(newDT, Map.of(
			UNSET_EDITOR, (EditorInitializer) (props, sheet) -> newDT.new HaxeObjectEditor(props, sheet)
		));
		return new HaxeObjectHaxeType(newDT);
	}
}

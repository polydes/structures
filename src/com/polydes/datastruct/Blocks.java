package com.polydes.datastruct;

import java.util.ArrayList;

import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.core.lib.code.design.Definition;
import stencyl.sw.app.ext.SWExtensionInstance;
import stencyl.sw.core.lib.snippet.designer.Definitions.DefinitionMap;

public class Blocks
{
	public static void addDesignModeBlocks(HaxeDataType type)
	{
		DefinitionMap dseBlocks = ((SWExtensionInstance) DataStructuresExtension.get().owner()).getBlocks();
		
		ArrayList<Definition> blocks = type.getBlocks();
		if(blocks != null)
		{
			for(Definition def : blocks)
			{
				dseBlocks.put(def.tag, def);
			}
		}
	}

	public static void removeDesignModeBlocks(HaxeDataType type)
	{
		DefinitionMap dseBlocks = ((SWExtensionInstance) DataStructuresExtension.get().owner()).getBlocks();
		
		ArrayList<Definition> blocks = type.getBlocks();
		if(blocks != null)
		{
			for(Definition def : blocks)
			{
				dseBlocks.remove(def.tag);
			}
		}
	}
}

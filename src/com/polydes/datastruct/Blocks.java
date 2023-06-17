package com.polydes.datastruct;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.core.lib.code.attribute.AttributeType;
import stencyl.core.lib.code.design.Definition;
import stencyl.core.lib.code.design.Definition.Category;
import stencyl.core.lib.code.design.block.BlockType;
import stencyl.core.lib.code.gen.CodeBuilder;
import stencyl.core.lib.code.gen.codemap.BasicCodeMap;
import stencyl.sw.app.editors.snippet.designer.block.Block;
import stencyl.sw.app.editors.snippet.designer.block.BlockTheme;
import stencyl.sw.app.editors.snippet.designer.dropdown.CodeConverter;
import stencyl.sw.app.editors.snippet.designer.dropdown.DropdownData;
import stencyl.sw.app.editors.snippet.designer.dropdown.DropdownData.DropdownConverter;
import stencyl.sw.app.ext.SWExtensionInstance;
import stencyl.sw.core.lib.attribute.HaxeAttributeTypes;
import stencyl.sw.core.lib.snippet.designer.Definitions.DefinitionMap;
import stencyl.sw.core.lib.snippet.designer.Definitions.OrderedDefinitionMap;

public class Blocks
{
	private static final Logger log = Logger.getLogger(Blocks.class);
	
	public static DefinitionMap tagCache = new OrderedDefinitionMap();
	
	public static void addDesignModeBlocks()
	{
		DefinitionMap dseBlocks = ((SWExtensionInstance) DataStructuresExtension.get().owner()).getBlocks();
		
//		set [propname] for [object] to [value]		object.prop = value;
		
		String spec = "set %1 for %0 to %2";
		
		Definition blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-set-prop1",
			new AttributeType[] { HaxeAttributeTypes.OBJECT, HaxeAttributeTypes.CODE, HaxeAttributeTypes.OBJECT },
			new BasicCodeMap("~.~ = ~;"),
			null,
			spec,
			BlockType.ACTION,
			HaxeAttributeTypes.VOID,
			null
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");

		dseBlocks.put(blockDef.tag, blockDef);
		tagCache.put(blockDef.tag, blockDef);

//		get [propname] for [object]					object.prop
		
		spec = "get %1 from %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-get-prop1",
			new AttributeType[] { HaxeAttributeTypes.OBJECT, HaxeAttributeTypes.CODE },
			new BasicCodeMap("~.~"),
			null,
			spec,
			BlockType.NORMAL,
			HaxeAttributeTypes.OBJECT,
			null
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");

		dseBlocks.put(blockDef.tag, blockDef);
		tagCache.put(blockDef.tag, blockDef);
		
//		set [propname] for [objectname] to [value]	DataStructures.get(objectname).propname = value;
		
		spec = "set %1 for %0 to %2";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-set-prop2",
			new AttributeType[] { HaxeAttributeTypes.TEXT, HaxeAttributeTypes.CODE, HaxeAttributeTypes.OBJECT },
			new BasicCodeMap("DataStructures.get(~).~ = ~;"),
			null,
			spec,
			BlockType.ACTION,
			HaxeAttributeTypes.VOID,
			null
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");

		dseBlocks.put(blockDef.tag, blockDef);
		tagCache.put(blockDef.tag, blockDef);
		
//		get [propname] for [objectname]				DataStructures.get(objectname).propname
		
		spec = "get %1 from %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-get-prop2",
			new AttributeType[] { HaxeAttributeTypes.TEXT, HaxeAttributeTypes.CODE },
			new BasicCodeMap("DataStructures.get(~).~"),
			null,
			spec,
			BlockType.NORMAL,
			HaxeAttributeTypes.OBJECT,
			null
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");

		dseBlocks.put(blockDef.tag, blockDef);
		tagCache.put(blockDef.tag, blockDef);
		
//		transformer setter: set insets for window to [(top, bottom, left, right)]
//		transformer getter: top of [get insets for window]
		
		spec = "get data with name: %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-get-data",
			new AttributeType[] { HaxeAttributeTypes.TEXT },
			new BasicCodeMap("DataStructures.get(~)"),
			null,
			spec,
			BlockType.NORMAL,
			HaxeAttributeTypes.OBJECT,
			null
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");

		dseBlocks.put(blockDef.tag, blockDef);
		tagCache.put(blockDef.tag, blockDef);
	}
	
	public static void addDesignModeBlocks(HaxeDataType type)
	{
		DefinitionMap dseBlocks = ((SWExtensionInstance) DataStructuresExtension.get().owner()).getBlocks();
		
		ArrayList<Definition> blocks = type.getBlocks();
		if(blocks != null)
		{
			for(Definition def : blocks)
			{
				dseBlocks.put(def.tag, def);
				tagCache.put(def.tag, def);
			}
		}
	}
	
	public static DropdownData createGenericDropdown(final String[] phrases, final String[] codeTexts)
	{
		return new DropdownData
		(
			new DropdownConverter()
			{
				@Override
				public int getIDForItem(Object o)
				{
					return -1;
				}
				
				@Override
				public Object[] getItems()
				{
					return phrases;
				}
			},
			new CodeConverter()
			{
				@Override
				public void toCode(CodeBuilder builder, int dropdownID, Block b, int index, Object o)
				{
					try
					{
						builder.append(codeTexts[index]);
					}
	
					catch(ArrayIndexOutOfBoundsException e)
					{
						log.error(e.getMessage(), e);
						builder.error();
					}
				}
			}
		);
	}
	
	public static void dispose()
	{
		DefinitionMap dseBlocks = ((SWExtensionInstance) DataStructuresExtension.get().owner()).getBlocks();
		for(String tag : tagCache.keySet())
			dseBlocks.remove(tag);
		tagCache.clear();
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
				tagCache.remove(def.tag);
			}
		}
	}
}

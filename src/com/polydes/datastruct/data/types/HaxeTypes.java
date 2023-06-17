package com.polydes.datastruct.data.types;

import java.util.HashMap;

import com.polydes.datastruct.Blocks;
import com.polydes.datastruct.data.types.haxe.ArrayHaxeType;
import com.polydes.datastruct.data.types.haxe.BoolHaxeType;
import com.polydes.datastruct.data.types.haxe.ColorHaxeType;
import com.polydes.datastruct.data.types.haxe.DynamicHaxeType;
import com.polydes.datastruct.data.types.haxe.ExtrasImageHaxeType;
import com.polydes.datastruct.data.types.haxe.FloatHaxeType;
import com.polydes.datastruct.data.types.haxe.IntHaxeType;
import com.polydes.datastruct.data.types.haxe.SelectionHaxeType;
import com.polydes.datastruct.data.types.haxe.SetHaxeType;
import com.polydes.datastruct.data.types.haxe.StencylResourceHaxeType;
import com.polydes.datastruct.data.types.haxe.StringHaxeType;

import stencyl.core.ext.registry.ObjectRegistry;
import stencyl.sw.core.lib.resource.SWResourceTypes;

public class HaxeTypes extends ObjectRegistry<HaxeDataType>
{
	public static ArrayHaxeType _Array = new ArrayHaxeType();
	public static BoolHaxeType _Bool = new BoolHaxeType();
	public static DynamicHaxeType _Dynamic = new DynamicHaxeType();
	public static FloatHaxeType _Float = new FloatHaxeType();
	public static IntHaxeType _Int = new IntHaxeType();
	public static StringHaxeType _String = new StringHaxeType();

	public static ColorHaxeType _Color = new ColorHaxeType();
	public static ExtrasImageHaxeType _ExtrasImage = new ExtrasImageHaxeType();
	public static SelectionHaxeType _Selection = new SelectionHaxeType();
	public static SetHaxeType _Set = new SetHaxeType();

	public HaxeTypes()
	{
		addBasicTypes();
	}

	public void addBasicTypes()
	{
		//Basic
		registerItem(_Array);
		registerItem(_Bool);
		registerItem(_Dynamic);
		registerItem(_Float);
		registerItem(_Int);
		registerItem(_String);

		//Extra
		registerItem(_Color);
		registerItem(_ExtrasImage);
		registerItem(_Selection);
		registerItem(_Set);

		//Stencyl types
		registerItem(new StencylResourceHaxeType<>(SWResourceTypes.actortype.getDataType(), "com.stencyl.models.actor.ActorType", "ACTORTYPE"));
		registerItem(new StencylResourceHaxeType<>(SWResourceTypes.background.getDataType(), "com.stencyl.models.Background", "OBJECT"));
		registerItem(new StencylResourceHaxeType<>(SWResourceTypes.font.getDataType(), "com.stencyl.models.Font", "FONT"));
//		registerItem(new StencylResourceHaxeType<>(SWResourceTypes.scene.getDataType(), "com.stencyl.models.Scene", "SCENE"));
		registerItem(new StencylResourceHaxeType<>(SWResourceTypes.sound.getDataType(), "com.stencyl.models.Sound", "SOUND"));
		registerItem(new StencylResourceHaxeType<>(SWResourceTypes.tileset.getDataType(), "com.stencyl.models.scene.Tileset", "OBJECT"));
		registerItem(new StencylResourceHaxeType<>(SWResourceTypes.control.getDataType(), "com.polydes.datastruct.Control", "CONTROL"));
	}

	@Override
	public void registerItem(HaxeDataType type)
	{
		super.registerItem(type);
		Blocks.addDesignModeBlocks(type);
		haxeFromDt.put(type.dataType.getId(), type);
	}

	@Override
	public void unregisterItem(HaxeDataType type)
	{
		super.unregisterItem(type);
		Blocks.removeDesignModeBlocks(type);
		haxeFromDt.remove(type.dataType.getId());
	}

	@Override
	protected void preregisterItem(HaxeDataType object)
	{
		// override so nothing happens.
	}

	@Override
	public HaxeDataType generatePlaceholder(String key)
	{
		return null;
	}

    /*================================================*\
	 | DataType Glue
	\*================================================*/

	private HashMap<String, HaxeDataType> haxeFromDt = new HashMap<>();

	public HaxeDataType getHaxeFromDT(String dataType)
	{
		return haxeFromDt.get(dataType);
	}
}

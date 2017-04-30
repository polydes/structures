package com.polydes.datastruct.updates;

import java.io.File;

import com.polydes.datastruct.DataStructuresExtension;

import stencyl.core.lib.Game;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;
import stencyl.sw.util.Worker;

public class V3_GameExtensionUpdate implements Worker
{
	@Override
	public void doWork()
	{
		DataStructuresExtension dse = DataStructuresExtension.get();
		
		File oldExtrasFolder = new File(Locations.getGameLocation(Game.getGame()) + "extras/[ext] data structures");
		File oldExtrasDefsFolder = new File(oldExtrasFolder, "defs");
		File oldExtrasDataFolder = new File(oldExtrasFolder, "data");
		
		File newExtrasDataFolder = new File(dse.getExtrasFolder(), "data");
		File newExtrasDefsFolder = new File(dse.getDataFolder(), "defs");
		
		FileHelper.copyDirectory(oldExtrasDataFolder, newExtrasDataFolder);
		FileHelper.copyDirectory(oldExtrasDefsFolder, newExtrasDefsFolder);
		FileHelper.delete(oldExtrasFolder);
	}
}
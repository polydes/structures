package com.polydes.datastruct.updates;

import java.io.File;

import com.polydes.datastruct.DataStructuresExtension;

import stencyl.core.io.FileHelper;
import stencyl.core.lib.IProject;
import stencyl.core.util.Worker;

public class V3_GameExtensionUpdate implements Worker
{
	private final IProject project;

	public V3_GameExtensionUpdate(IProject project)
	{
		this.project = project;
	}

	@Override
	public void doWork()
	{
		DataStructuresExtension dse = DataStructuresExtension.get();
		
		File oldExtrasFolder = project.getFile("extras", "[ext] data structures");
		File oldExtrasDefsFolder = new File(oldExtrasFolder, "defs");
		File oldExtrasDataFolder = new File(oldExtrasFolder, "data");
		
		File newExtrasDataFolder = new File(dse.getExtrasFolder(), "data");
		File newExtrasDefsFolder = new File(dse.getDataFolder(), "defs");
		
		FileHelper.copyDirectory(oldExtrasDataFolder, newExtrasDataFolder);
		FileHelper.copyDirectory(oldExtrasDefsFolder, newExtrasDefsFolder);
		FileHelper.delete(oldExtrasFolder);
	}
}
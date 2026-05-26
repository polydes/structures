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
		File oldExtrasFolder = project.getFile("extras", "[ext] data structures");
		File oldExtrasDefsFolder = new File(oldExtrasFolder, "defs");
		File oldExtrasDataFolder = new File(oldExtrasFolder, "data");

		File dataFolder = new File(project.getFiles().getExtensionGameDataLocation("com.polydes.datastruct"));
		File extrasFolder = new File(project.getFiles().getExtensionExtrasDataLocation("com.polydes.datastruct"));
		File newExtrasDataFolder = new File(extrasFolder, "data");
		File newExtrasDefsFolder = new File(dataFolder, "defs");

		if(oldExtrasDataFolder.exists())
			FileHelper.copyDirectory(oldExtrasDataFolder, newExtrasDataFolder);
		if(oldExtrasDefsFolder.exists())
			FileHelper.copyDirectory(oldExtrasDefsFolder, newExtrasDefsFolder);
		FileHelper.delete(oldExtrasFolder);
	}
}
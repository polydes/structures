package com.polydes.datastruct.updates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.file.PathUtils;
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
		String EXTENSION_ID = "com.polydes.datastruct";

		Path oldExtrasFolder = project.getPath("extras", "[ext] data structures");
		Path oldExtrasDefsFolder = oldExtrasFolder.resolve("defs");
		Path oldExtrasDataFolder = oldExtrasFolder.resolve("data");

		Path newExtrasDataFolder = project.getFiles().getExtensionExtrasDataLocation(EXTENSION_ID).resolve("data");
		Path newExtrasDefsFolder = project.getFiles().getExtensionGameDataLocation(EXTENSION_ID).resolve("defs");

        try {
			if(Files.exists(oldExtrasDataFolder))
				PathUtils.copyDirectory(oldExtrasDataFolder, newExtrasDataFolder);
			if(Files.exists(oldExtrasDefsFolder))
				PathUtils.copyDirectory(oldExtrasDefsFolder, newExtrasDefsFolder);
			FileHelper.delete(oldExtrasFolder);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
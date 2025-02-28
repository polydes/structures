package com.polydes.datastruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.*;

import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.types.DSTypes;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.io.HXGenerator;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.ui.datatypes.DSEditorProviders;
import com.polydes.datastruct.updates.V3_GameExtensionUpdate;
import com.polydes.datastruct.updates.V4_FullTypeNamesUpdate;

import stencyl.app.comp.darktree.DarkTree;
import stencyl.app.comp.dg.MessageDialog;
import stencyl.app.ext.PageAddon;
import stencyl.app.ext.PageAddon.EngineExtensionPageAddon;
import stencyl.core.api.fs.Locations;
import stencyl.core.ext.GameExtension;
import stencyl.core.ext.engine.ExtensionInstanceManager.FormatUpdateSubmitter;
import stencyl.sw.app.center.GameLibrary;

import static stencyl.core.api.fs.LocationProvider.DefaultLocations.getPath;

public class DataStructuresExtension extends GameExtension
{
	private static final Logger log = Logger.getLogger(DataStructuresExtension.class);

	private static DataStructuresExtension instance;

	public SDETypes sdeTypes;
	public HaxeTypes haxeTypes;
	public StructureDefinitions structureDefinitions;

	public static boolean forceUpdateData = false;
	private boolean initialized = false;

	public static DataStructuresExtension get()
	{
		return instance;
	}

	public SDETypes getSdeTypes()
	{
		return sdeTypes;
	}

	public HaxeTypes getHaxeTypes()
	{
		return haxeTypes;
	}

	public StructureDefinitions getStructureDefinitions()
	{
		return structureDefinitions;
	}

	/*
	 * Happens when StencylWorks launches. 
	 * 
	 * Avoid doing anything time-intensive in here, or it will
	 * slow down launch.
	 */
	@Override
	public void onLoad()
	{
		LogManager.getLogger("com.polydes.datastruct").setLevel(Level.DEBUG);

		instance = this;

		PageAddon structuresSidebarPage = new EngineExtensionPageAddon(owner())
		{
			@Override
			public JPanel getPage()
			{
				return MainPage.get();
			}
		};

		owner().getAddons().setAddon(GameLibrary.DASHBOARD_SIDEBAR_PAGE_ADDONS, structuresSidebarPage);

		initialized = false;

		Prefs.DEFPAGE_X = readIntProp("defpage.x", -1);
		Prefs.DEFPAGE_Y = readIntProp("defpage.y", -1);
		Prefs.DEFPAGE_WIDTH = readIntProp("defpage.width", 640);
		Prefs.DEFPAGE_HEIGHT = readIntProp("defpage.height", 480);
		Prefs.DEFPAGE_SIDEWIDTH = readIntProp("defpage.sidewidth", DarkTree.DEF_WIDTH);
		Prefs.DEFPAGE_SIDEDL = readIntProp("defpage.sidedl", 150);

		try
		{
			DSTypes.register();
			DSEditorProviders.registerBuiltinEditors();

			sdeTypes = new SDETypes();
			haxeTypes = new HaxeTypes();
			structureDefinitions = new StructureDefinitions(getProject());

			Files.createDirectories(getDataFolder().resolve("defs"));
			DataStructuresExtension.get().getStructureDefinitions().addFolder(getDataFolder().resolve("defs"), "My Structures");

			Files.createDirectories(getExtrasFolder().resolve("data"));
//			Images.get().load(getExtrasFolder());
			Structures.get().load(getExtrasFolder().resolve("data"));

			initialized = true;
		}
		catch(Exception ex)
		{
			MessageDialog.showGenericDialog
				(
					"Couldn't initialize Data Structures Extension",
					"Error: " + ex.getMessage()
				);

			log.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void onUnload()
	{
		properties.put("defpage.x", Prefs.DEFPAGE_X);
		properties.put("defpage.y", Prefs.DEFPAGE_Y);
		properties.put("defpage.width", Prefs.DEFPAGE_WIDTH);
		properties.put("defpage.height", Prefs.DEFPAGE_HEIGHT);
		properties.put("defpage.sidewidth", Prefs.DEFPAGE_SIDEWIDTH);
		properties.put("defpage.sidedl", Prefs.DEFPAGE_SIDEDL);

		MainPage.disposePages();
		structureDefinitions.dispose();
		StructureCondition.dispose();
		haxeTypes.dispose();
//		Images.dispose();
		Structures.dispose();
		sdeTypes.dispose();
		DSEditorProviders.unregisterBuiltinEditors();
		DSTypes.unregister();

		sdeTypes = null;
		haxeTypes = null;
		structureDefinitions = null;

		initialized = false;
	}

	public boolean isInitialized()
	{
		return initialized;
	}

	private static Path sourceDir;

	@Override
	protected void onSave()
	{
		if(initialized)
		{
			try
			{
				DataStructuresExtension.get().getStructureDefinitions().saveChanges();
			}
			catch (IOException e1)
			{
				log.error(e1.getMessage(), e1);
			}

			if(forceUpdateData)
			{
				forceUpdateData = false;
				Structures.root.setDirty(true);
			}

			Path saveTo = getExtrasFolder().resolve("data");
			if(Structures.root.isDirty())
			{
				Path temp = getPath(Locations.STENCYL_TEMP, "data structures save");
				try{Files.createDirectories(temp);}catch(IOException e){log.error(e.getMessage(), e);}
				boolean failedToSave = false;

				try{PathUtils.deleteDirectory(temp);}catch(IOException e){log.error(e.getMessage(), e);}
				try{Files.createDirectories(temp);}catch(IOException e){log.error(e.getMessage(), e);}
				try{Structures.get().saveChanges(temp);}catch(IOException e){failedToSave = true; log.error(e.getMessage(), e);}
				if(!failedToSave)
				{
					try{PathUtils.deleteDirectory(saveTo);}catch(IOException e){log.error(e.getMessage(), e);}
					try{Files.createDirectories(saveTo);}catch(IOException e){log.error(e.getMessage(), e);}
					try{PathUtils.copyDirectory(temp, saveTo);}catch(IOException e){log.error(e.getMessage(), e);}
				}
			}
		}
	}

	@Override
	public void onGameBuild()
	{
		if(initialized)
		{
			Path hxProjectDir = Locations.getHXProjectDir(getProject());
			sourceDir = hxProjectDir.resolve("Source");

			Path dataList = hxProjectDir.resolve("Assets/data/MyDataStructures.txt");
			Text.writeLines(dataList, HXGenerator.generateFileList(getExtrasFolder().resolve("data")));

			write("com.polydes.datastruct.DataStructureReader", HXGenerator.generateReader());
			for(HaxeDataType type : haxeTypes.values())
			{
				List<String> lines = type.generateHaxeClass();
				if(lines != null)
					write(type.getHaxeType(), lines);
			}
		}
	}

	private void write(String path, List<String> lines)
	{
		path = StringUtils.replace(path, ".", "/") + ".hx";
		Path out = sourceDir.resolve(path);
		try
		{
			Files.createDirectories(out.getParent());
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
		Text.writeLines(out, lines);
	}

	@Override
	protected void onInstalled()
	{
		try
		{
			Files.createDirectories(getExtrasFolder().resolve("data"));
			Files.createDirectories(getDataFolder().resolve("defs"));
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected int detectOldInstall()
	{
		return Files.exists(getProject().getPath("extras", "[ext] data structures")) ? 2 : -1;
	}

	@Override
	protected void onUninstalled()
	{

	}

	@Override
	public void updateFromVersion(int fromVersion, FormatUpdateSubmitter updateQueue)
	{
		if(fromVersion < 3)
			updateQueue.add(new V3_GameExtensionUpdate(getProject()));
		if(fromVersion < 4)
			updateQueue.after(V3_GameExtensionUpdate.class).add(new V4_FullTypeNamesUpdate(getProject()));
		forceUpdateData = true;
	}
}

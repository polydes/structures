package com.polydes.datastruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.swing.*;

import org.apache.commons.io.FileUtils;
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
import com.polydes.datastruct.updates.TypenameUpdater;
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

import static stencyl.core.api.fs.LocationProvider.DefaultLocations.getFile;

public class DataStructuresExtension extends GameExtension
{
	private static final Logger log = Logger.getLogger(DataStructuresExtension.class);

	private static DataStructuresExtension instance;

	public SDETypes sdeTypes;
	public HaxeTypes haxeTypes;
	public StructureDefinitions structureDefinitions;

	public static boolean forceUpdateData = false;
	private boolean initialized = false;

	private TypenameUpdater typenameUpdater = new TypenameUpdater();

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

	public TypenameUpdater getTypenameUpdater()
	{
		return typenameUpdater;
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

			new File(getDataFolder(), "defs").mkdirs();
			DataStructuresExtension.get().getStructureDefinitions().addFolder(new File(getDataFolder(), "defs"), "My Structures");

			new File(getExtrasFolder(), "data").mkdirs();
//			Images.get().load(new File(Locations.getGameLocation(getGame()), "extras"));
			Structures.get().load(new File(getExtrasFolder(), "data"));

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

	private static String sourceDir;

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

			File saveTo = new File(getExtrasFolder(), "data");
			if(Structures.root.isDirty())
			{
				File temp = getFile(Locations.STENCYL_TEMP, "data structures save");
				temp.mkdirs();
				boolean failedToSave = false;

				try{FileUtils.deleteDirectory(temp);}catch(IOException e){log.error(e.getMessage(), e);}
				temp.mkdirs();
				try{Structures.get().saveChanges(temp);}catch(IOException e){failedToSave = true; log.error(e.getMessage(), e);}
				if(!failedToSave)
				{
					try{FileUtils.deleteDirectory(saveTo);}catch(IOException e){log.error(e.getMessage(), e);}
					saveTo.mkdirs();
					try{FileUtils.copyDirectory(temp, saveTo);}catch(IOException e){log.error(e.getMessage(), e);}
				}
			}
		}
	}

	@Override
	public void onGameBuild()
	{
		if(initialized)
		{
			String hxProjectDir = StringUtils.stripEnd(Locations.getHXProjectDir(getProject()), "/\\");
			sourceDir = hxProjectDir + File.separator + "Source";

			File dataList = Path.of(hxProjectDir, "Assets", "data", "MyDataStructures.txt").toFile();
			Text.writeLines(dataList, HXGenerator.generateFileList(new File(getExtrasFolder(), "data")));

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
		path = StringUtils.replace(path, ".", File.separator) + ".hx";
		File out = new File(sourceDir, path);
		if(!out.getParentFile().exists())
			out.getParentFile().mkdirs();
		Text.writeLines(out, lines);
	}

	@Override
	protected void onInstalled()
	{
		new File(getExtrasFolder(), "data").mkdirs();
		new File(getDataFolder(), "defs").mkdirs();
	}

	@Override
	protected int detectOldInstall()
	{
		return getProject().getFile("extras", "[ext] data structures").exists() ? 2 : -1;
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
			updateQueue.after(V3_GameExtensionUpdate.class).add(new V4_FullTypeNamesUpdate());
		forceUpdateData = true;
	}
}
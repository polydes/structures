package com.polydes.datastruct.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import stencyl.core.io.FileHelper;

import static stencyl.core.util.PathHelper.filenameOf;

public class FolderInfo extends HashMap<String, String>
{
	public static String FOLDER_INFO_FILENAME = "folderinfo.txt";
	public static String FILE_ORDER_KEY = "fileOrder";
	
	//=== For Reading
	
	private Path folder;
	
	public FolderInfo(Path folder)
	{
		if(!Files.isDirectory(folder))
			return;
		
		this.folder = folder;
		
		Path info = folder.resolve(FOLDER_INFO_FILENAME);
		if(!Files.exists(info))
			return;
		
		putAll(Text.readKeyValues(info));
	}
	
	public ArrayList<String> getFileOrder()
	{
		ArrayList<String> fileOrder = new ArrayList<>();
		
		if(containsKey(FILE_ORDER_KEY))
			fileOrder = new ArrayList<>(Arrays.asList(StringUtils.split(get(FILE_ORDER_KEY), ",")));
		
		for(Path f : FileHelper.listFilesSimple(folder))
		{
			String fname = filenameOf(f);
			if(!fileOrder.contains(fname) && !fname.equals(FOLDER_INFO_FILENAME))
				fileOrder.add(fname);
		}

		return fileOrder;
	}
	
	//=== For Writing
	
	private ArrayList<String> fileOrder;
	
	public FolderInfo()
	{
		fileOrder = new ArrayList<>();
	}
	
	public void addFilenameToOrder(String filename)
	{
		if(fileOrder == null)
			fileOrder = new ArrayList<>();
		
		fileOrder.add(filename);
	}
	
	public void writeToFolder(Path folder)
	{
		ArrayList<String> lines = new ArrayList<>();
		
		for(String key : keySet())
			lines.add(key + "=" + get(key));
		
		if(fileOrder != null)
			lines.add(FILE_ORDER_KEY + "=" + StringUtils.join(fileOrder.toArray(new String[0]), ","));
		
		Text.writeLines(folder.resolve(FOLDER_INFO_FILENAME), lines);
	}
}

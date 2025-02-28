package com.polydes.datastruct.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class Text
{
	private static final Logger log = Logger.getLogger(Text.class);
	
	public static List<String> readLines(Path file)
	{
		try
		{
			return Files.readAllLines(file);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}
	
	public static String readString(Path file)
	{
		try
		{
			return Files.readString(file);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			return "";
		}
	}
	
	public static HashMap<String, String> readKeyValues(Path file)
	{
		HashMap<String, String> map = new HashMap<>();
		
		for(String s : readLines(file))
		{
			if(!s.contains("="))
				continue;
			
			String[] parts = s.split("=");
			map.put(parts[0], parts.length > 1 ? parts[1] : "");
		}
		
		return map;
	}
	
	public static void writeKeyValues(Path file, Map<String, String> map)
	{
		List<String> lines = new ArrayList<>();
		
		for(Entry<String, String> entry : map.entrySet())
			lines.add(entry.getKey() + "=" + entry.getValue());
		
		writeLines(file, lines);
	}
	
	public static void writeLines(Path file, Collection<String> lines)
	{
		try
		{
			Files.write(file, lines);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
		}
	}
}

package com.polydes.datastruct.updates;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.io.Text;

import stencyl.sw.util.FileHelper;

public class TypenameUpdater
{
	private static final Logger log = Logger.getLogger(TypenameUpdater.class);
	
	HashMap<String, String> typeBackMap = new HashMap<>();
	
	public void addTypes(HashMap<String, String> types)
	{
		typeBackMap.putAll(types);
	}
	
	public void addType(String old, String newType)
	{
		typeBackMap.put(old, newType);
	}
	
	public void convert()
	{
		DataStructuresExtension dse = DataStructuresExtension.get();
		File data = new File(dse.getExtrasFolder(), "data");
		File defs = new File(dse.getDataFolder(), "defs");
		
		for(File xmlFile : FileHelper.listFiles(defs, "xml"))
		{
			try
			{
				Document doc = FileHelper.readXMLFromFile(xmlFile);
				applyToDocument(doc);
				FileHelper.writeXMLToFile(doc, xmlFile);
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		for(File dataFile : FileHelper.listFiles(data))
		{
			if(dataFile.getName().endsWith(".txt"))
				continue;
			
			applyToData(dataFile);
		}
	}
	
	public void applyToDocument(Document doc)
	{
		NodeList nl = doc.getElementsByTagName("field");
		for(int i = 0; i < nl.getLength(); ++i)
		{
			Element e = (Element) nl.item(i);
			String type = e.getAttribute("type");
			e.setAttribute("type", Lang.or(typeBackMap.get(type), type));
		}
	}
	
	public void applyToData(File dataFile)
	{
		HashMap<String, String> props = Text.readKeyValues(dataFile);
		String type = props.get("struct_type");
		String newtype = typeBackMap.get(type);
		if(newtype != null)
		{
			props.put("struct_type", newtype);
			log.debug(dataFile.getName() + " converted: " + type + " -> " + newtype);
			Text.writeKeyValues(dataFile, props);
		}
		else
		{
			log.debug(dataFile.getName() + " is " + type);
		}
	}
}

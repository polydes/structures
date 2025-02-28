package com.polydes.datastruct.updates;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.io.Text;

import stencyl.core.io.FileHelper;
import stencyl.core.util.Lang;

import static stencyl.core.util.PathHelper.extensionOf;
import static stencyl.core.util.PathHelper.filenameOf;

public class TypenameUpdater
{
	private static final Logger log = Logger.getLogger(TypenameUpdater.class);

	private static TypenameUpdater instance;

	public static TypenameUpdater get()
	{
		if(instance == null)
			instance = new TypenameUpdater();
		return instance;
	}

	HashMap<String, String> typeBackMap = new HashMap<>();
	
	public void addTypes(Map<String, String> types)
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
		Path data = dse.getExtrasFolder().resolve("data");
		Path defs = dse.getDataFolder().resolve("defs");
		
		for(Path xmlFile : FileHelper.listFiles(defs, true, false, "xml"))
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
		for(Path dataFile : FileHelper.listFiles(data, true, false))
		{
			if(extensionOf(dataFile).equals("txt"))
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
	
	public void applyToData(Path dataFile)
	{
		HashMap<String, String> props = Text.readKeyValues(dataFile);
		String type = props.get("struct_type");
		String newtype = typeBackMap.get(type);
		if(newtype != null)
		{
			props.put("struct_type", newtype);
			log.debug(filenameOf(dataFile) + " converted: " + type + " -> " + newtype);
			Text.writeKeyValues(dataFile, props);
		}
		else
		{
			log.debug(filenameOf(dataFile) + " is " + type);
		}
	}
}

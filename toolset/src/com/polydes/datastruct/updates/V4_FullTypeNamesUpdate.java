package com.polydes.datastruct.updates;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stencyl.core.api.VerificationHelper;
import stencyl.core.io.FileHelper;
import stencyl.core.io.XML;
import stencyl.core.io.XmlHelper;
import stencyl.core.lib.IProject;
import stencyl.core.util.Worker;

import static stencyl.core.util.PathHelper.extensionOf;
import static stencyl.core.util.PathHelper.filenameOf;

public class V4_FullTypeNamesUpdate implements Worker
{
	private static final Logger log = Logger.getLogger(V4_FullTypeNamesUpdate.class);

	private final IProject project;

	public V4_FullTypeNamesUpdate(IProject project)
	{
		this.project = project;
	}

	@Override
	public void doWork()
	{
		String EXTENSION_ID = "com.polydes.datastruct";

		Path data = project.getFiles().getExtensionExtrasDataLocation(EXTENSION_ID).resolve("data");
		Path defs = project.getFiles().getExtensionGameDataLocation(EXTENSION_ID).resolve("defs");
		
		TypenameUpdater tu = TypenameUpdater.get();
		tu.addTypes(Map.ofEntries(
			Map.entry("Boolean", "Bool"),
			Map.entry("Color", "com.polydes.datastruct.Color"),
			Map.entry("Control", "com.polydes.datastruct.Control"),
			Map.entry("Image", "com.polydes.datastruct.ExtrasImage"),
			Map.entry("Selection", "com.polydes.datastruct.Selection"),
			Map.entry("Set", "com.polydes.datastruct.Set"),
			Map.entry("ActorType", "com.stencyl.models.actor.ActorType"),
			Map.entry("Background", "com.stencyl.models.Background"),
			Map.entry("Font", "com.stencyl.models.Font"),
			Map.entry("Sound", "com.stencyl.models.Sound"),
			Map.entry("Tileset", "com.stencyl.models.scene.Tileset")
		));
		for(Path xmlFile : FileHelper.listFiles(defs, true, false, "xml"))
		{
			String name = filenameOf(xmlFile);
			name = name.substring(0, name.length() - ".xml".length());
			try
			{
				Document doc = FileHelper.readXMLFromFile(xmlFile);
				String classname = doc.getDocumentElement().getAttribute("classname");
				tu.addType(name, classname);
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		for(Path xmlFile : FileHelper.listFiles(defs, true, false, "xml"))
		{
			try
			{
				Document doc = FileHelper.readXMLFromFile(xmlFile);
				
				tu.applyToDocument(doc);
				
				NodeList nl = doc.getElementsByTagName("if");
				for(int i = 0; i < nl.getLength(); ++i)
				{
					Element e = (Element) nl.item(i);
					
					if(e.hasAttribute("condition"))
						continue;
					
					Element conditionElement = XmlHelper.child(e, 0);
					String conditionString = subFromXML(conditionElement);
					
					e.removeChild(conditionElement);
					e.setAttribute("condition", conditionString);
				}
				
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
			
			tu.applyToData(dataFile);
		}
	}
	
	private static String subFromXML(Element e)
	{
		if(e.getTagName().equals("is"))
		{
			return XML.read(e, "field") + " == " + codeRepresentation(XML.read(e, "value"));
		}
		else if(e.getTagName().equals("not"))
		{
			if(XmlHelper.child(e, 0).getTagName().equals("is"))
			{
				Element sub = XmlHelper.child(e, 0);
				return XML.read(sub, "field") + " != " + codeRepresentation(XML.read(sub, "value"));
			}
			else
				return "!(" + subFromXML(XmlHelper.child(e, 0)) + ")";
		}
		else if(e.getTagName().equals("and"))
		{
			return subFromXML(XmlHelper.child(e, 0)) + " && " + subFromXML(XmlHelper.child(e, 1));
		}
		else if(e.getTagName().equals("or"))
		{
			return subFromXML(XmlHelper.child(e, 0)) + " || " + subFromXML(XmlHelper.child(e, 1));
		}
		else
			return "";
	}
	
	private static String codeRepresentation(String value)
	{
		if(VerificationHelper.isInteger(value) || VerificationHelper.isFloat(value) || value.equals("true") || value.equals("false"))
			return value;
		else
			return "\"" + value + "\"";
	}
}

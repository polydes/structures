package com.polydes.datastruct.updates;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.polydes.datastruct.DataStructuresExtension;

import stencyl.core.api.VerificationHelper;
import stencyl.core.io.FileHelper;
import stencyl.core.io.XML;
import stencyl.core.io.XmlHelper;
import stencyl.core.lib.IProject;
import stencyl.core.util.Lang;
import stencyl.core.util.Worker;

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
		File dataFolder = new File(project.getFiles().getExtensionGameDataLocation("com.polydes.datastruct"));
		File extrasFolder = new File(project.getFiles().getExtensionExtrasDataLocation("com.polydes.datastruct"));

		File data = new File(extrasFolder, "data");
		File defs = new File(dataFolder, "defs");
		
		TypenameUpdater tu = TypenameUpdater.get();
		tu.addTypes(Lang.hashmap(
			"Boolean", "Bool",
			"Color", "com.polydes.datastruct.Color",
			"Control", "com.polydes.datastruct.Control",
			"Image", "com.polydes.datastruct.ExtrasImage",
			"Selection", "com.polydes.datastruct.Selection",
			"Set", "com.polydes.datastruct.Set",
			"ActorType", "com.stencyl.models.actor.ActorType",
			"Background", "com.stencyl.models.Background",
			"Font", "com.stencyl.models.Font",
			"Sound", "com.stencyl.models.Sound",
			"Tileset", "com.stencyl.models.scene.Tileset"
		));
		for(File xmlFile : FileHelper.listFiles(defs, "xml"))
		{
			String name = xmlFile.getName();
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
		for(File xmlFile : FileHelper.listFiles(defs, "xml"))
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
		for(File dataFile : FileHelper.listFiles(data))
		{
			if(dataFile.getName().endsWith(".txt"))
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
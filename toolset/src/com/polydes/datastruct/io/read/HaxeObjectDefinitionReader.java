package com.polydes.datastruct.io.read;

import java.io.IOException;

import org.w3c.dom.Element;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.core.HaxeField;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;
import com.polydes.datastruct.data.types.ExtrasMap;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.io.FileHelper;
import stencyl.core.io.XML;
import stencyl.core.io.XmlHelper;
import stencyl.core.util.Lang;

public class HaxeObjectDefinitionReader
{
	public static HaxeObjectDefinition read(String path, DataContext ctx)
	{
		Element root;
		try
		{
			root = FileHelper.readXMLFromFile(path).getDocumentElement();
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
		String haxeClass = root.getAttribute("class");
		Element fields = XmlHelper.child(root, "fields");
		
		HaxeField[] hfs = Lang.mapCA(XmlHelper.children(fields), HaxeField.class, (field) ->
		{
			String name = field.getAttribute("name");
			String type = field.getAttribute("type");
			ExtrasMap editorData = null;
			
			if(field.hasChildNodes())
			{
				Element editor = XmlHelper.child(field, "editor");
				if(editor != null)
				{
					editorData = new ExtrasMap(ctx);
					editorData.backingPutAll(XML.readMap(editor));
				}
			}
			
			HaxeField hf = new HaxeField(name, null, editorData);
			DataStructuresExtension.get().getHaxeTypes().requestValue(type, hf);
			
			if(field.hasAttribute("default"))
				hf.defaultValue = field.getAttribute("default");
			
			return hf;
		});
		
		HaxeObjectDefinition def = new HaxeObjectDefinition(haxeClass, hfs);
		
		def.showLabels = XML.readBoolean(fields, "showlabels", true);
		
		Element reader = XmlHelper.child(root, "haxereader");
		if(reader != null)
			def.haxereaderExpression = reader.getAttribute("expr");
		
		return def;
	}
}

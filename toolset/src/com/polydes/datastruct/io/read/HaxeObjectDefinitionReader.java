package com.polydes.datastruct.io.read;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.core.HaxeField;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;
import com.polydes.datastruct.data.types.ExtrasMap;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.io.FileHelper;
import stencyl.core.io.XML;
import stencyl.core.io.XmlHelper;

public class HaxeObjectDefinitionReader
{
	public static HaxeObjectDefinition read(Path path, DataContext ctx)
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
		Element fieldsElement = XmlHelper.child(root, "fields");
		List<Element> fields = XmlHelper.children(fieldsElement);
		List<HaxeField> hfs = new ArrayList<>(fields.size());

		for(Element field : fields)
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

			hfs.add(hf);
		}
		
		HaxeObjectDefinition def = new HaxeObjectDefinition(haxeClass, hfs.toArray(new HaxeField[0]));
		
		def.showLabels = XML.readBoolean(fieldsElement, "showlabels", true);
		
		Element reader = XmlHelper.child(root, "haxereader");
		if(reader != null)
			def.haxereaderExpression = reader.getAttribute("expr");
		
		return def;
	}
}

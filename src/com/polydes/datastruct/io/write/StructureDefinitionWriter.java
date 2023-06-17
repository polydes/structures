package com.polydes.datastruct.io.write;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.elements.StructureUnknown;

import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;

public class StructureDefinitionWriter
{
	private static HashMap<String, String> namespaces = new HashMap<>();
	
	private static String getNS(String ext)
	{
		if(!namespaces.containsKey(ext))
		{
			//generate a short namespace key.
			String largeKey = StringUtils.substringAfterLast(ext, ".");
			String key = largeKey.substring(0, 1);
			while(namespaces.containsValue(key) && key.length() < largeKey.length())
				key = largeKey.substring(0, key.length() + 1);
			
			if(namespaces.containsKey(key))
			key = StringUtils.replace(ext, ".", "");
			
			namespaces.put(ext, key);
		}
		
		return namespaces.get(ext);
	}
	
	public static void write(Document doc, Element root, StructureDefinition def)
	{
		DataContext ctx = def.getCtx();
		
		root.setAttribute("classname", def.getFullClassname());
		if(def.parent != null)
			root.setAttribute("extends", def.parent.getFullClassname());
		if(def.iconSource != null)
			root.setAttribute("iconSource", def.iconSource);
		for(DefaultLeaf n : def.guiRoot.getItems())
			writeNode(doc, root, n, ctx);
	}
	
	@SuppressWarnings("unchecked")
	public static <S extends SDE> void writeNode(Document doc, Element parent, DefaultLeaf gui, DataContext ctx)
	{
		S obj = (S) gui.getUserData();
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(obj.getClass());
		
		String namespace = (obj instanceof StructureUnknown) ?
			((StructureUnknown) obj).namespace : type.owner;
		
		Element e = (namespace != null && !namespace.isEmpty()) ?
				doc.createElementNS(namespace, getNS(namespace) + ":" + type.tag) :
				doc.createElement(type.tag);
		type.write(obj, e, ctx);
		
		if(gui instanceof DefaultBranch)
			for(DefaultLeaf n : ((DefaultBranch) gui).getItems())
				writeNode(doc, e, n, ctx);
		
		parent.appendChild(e);
	}
}

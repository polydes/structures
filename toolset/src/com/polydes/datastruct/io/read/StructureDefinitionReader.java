package com.polydes.datastruct.io.read;

import org.w3c.dom.Element;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.StructureDefinition;

import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.io.XmlHelper;

public class StructureDefinitionReader
{
//	private static final Logger log = Logger.getLogger(StructureDefinitionReader.class);
	
	public static void read(Element root, StructureDefinition model)
	{
		if(root.hasAttribute("extends"))
			DataStructuresExtension.get().getStructureDefinitions().requestValue(root.getAttribute("extends"), def -> model.parent = def);
		if(root.hasAttribute("iconSource"))
			model.iconSource = root.getAttribute("iconSource");
		readFields(root, model, model.guiRoot);
	}
	
	private static void readFields(Element parent, StructureDefinition model, DefaultBranch gui)
	{
		if(parent != null)
		{
			for(Element e : XmlHelper.children(parent))
			{
				String ns = e.getNamespaceURI();
				if(ns == null)
					ns = SDETypes.BASE_OWNER;
				DataStructuresExtension.get().getSdeTypes().requestValue(ns, e.getLocalName(), type -> {
					
					gui.markAsLoading(true);
					SDE newItem = type.read(model, e);
					
					if(type.isBranchNode)
					{
						Folder item = new Folder(newItem.getDisplayLabel(), newItem);
						readFields(e, model, item);
						gui.addItem(item);
					}
					else
					{
						gui.addItem(new DefaultLeaf(newItem.getDisplayLabel(), newItem));
					}
					gui.markAsLoading(false);
					
				});
			}
		}
	}
}

package com.polydes.datastruct.data.structure.elements;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

import stencyl.app.ext.res.AppResourceLoader;
import stencyl.app.ext.res.AppResources;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.io.XML;

public class StructureUnknown extends SDE
{
	private static AppResources res = AppResourceLoader.getResources("com.polydes.datastruct");
	
	public String namespace;
	public String tag;
	public HashMap<String, String> atts;
	
	public StructureUnknown(String namespace, String tag, HashMap<String, String> atts)
	{
		this.namespace = namespace;
		this.tag = tag;
		this.atts = atts;
	}
	
	@Override
	public String getDisplayLabel()
	{
		return namespace + ":" + tag;
	}

	public static class UnknownType extends SDEType<StructureUnknown>
	{
		public UnknownType(String tag)
		{
			sdeClass = StructureUnknown.class;
			this.tag = tag;
			isBranchNode = true;
			icon = res.loadIcon("question-white.png");
			childTypes = new ArrayList<>();
		}
		
		@Override
		public StructureUnknown read(StructureDefinition model, Element e)
		{
			return new StructureUnknown(e.getPrefix(), e.getLocalName(), XML.readMap(e));
		}

		@Override
		public void write(StructureUnknown object, Element e, DataContext ctx)
		{
			XML.writeMap(e, object.atts);
		}
		
		@Override
		public StructureUnknown create(StructureDefinition def, StructureDefinitionEditor defEditor, String nodeName)
		{
			return null;
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureUnknown value, int i)
		{
			if(parent.getUserData() instanceof StructureUnknown)
				return null;
			
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			group.add(sheet.style.createLabel(value.getDisplayLabel()), sheet.style.createDescriptionRow("This element couldn't be loaded."));
			group.add(sheet.style.rowgap);
			
			parentCard.addGroup(i, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureUnknown value)
		{
			
		}

		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureUnknown value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureUnknown value)
		{
			
		}
	}
}

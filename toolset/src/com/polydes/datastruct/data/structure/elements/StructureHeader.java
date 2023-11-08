package com.polydes.datastruct.data.structure.elements;

import org.w3c.dom.Element;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

import stencyl.app.comp.RoundedLabel;
import stencyl.app.ext.res.AppResourceLoader;
import stencyl.app.ext.res.AppResources;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.io.XML;

public class StructureHeader extends SDE
{
	private static AppResources res = AppResourceLoader.getResources("com.polydes.datastruct");
	
	private String label;
	
	public StructureHeader(String label)
	{
		this.label = label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	@Override
	public String getDisplayLabel()
	{
		return label;
	}

	public static class HeaderType extends SDEType<StructureHeader>
	{
		public HeaderType()
		{
			sdeClass = StructureHeader.class;
			tag = "header";
			isBranchNode = false;
			icon = res.loadThumbnail("header.png", 16);
			childTypes = null;
		}
		
		@Override
		public StructureHeader read(StructureDefinition model, Element e)
		{
			return new StructureHeader(XML.read(e, "label"));
		}

		@Override
		public void write(StructureHeader object, Element e, DataContext ctx)
		{
			XML.write(e, "label", object.getLabel());
		}

		@Override
		public StructureHeader create(StructureDefinition def, StructureDefinitionEditor defEditor, String nodeName)
		{
			return new StructureHeader(nodeName);
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureHeader value, int i)
		{
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			group.add(sheet.style.rowgap);
			group.add(null, sheet.style.createRoundedLabel("<html><b>" + value.getLabel() + "</b></html>"));
			group.add(sheet.style.rowgap);
			
			parentCard.addGroup(i, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureHeader value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureHeader value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			card.layoutContainer();
		}
		
		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureHeader value)
		{
			((RoundedLabel) ((RowGroup) gui).rows[1].components[1]).setText(value.getLabel());
		}
	}
}

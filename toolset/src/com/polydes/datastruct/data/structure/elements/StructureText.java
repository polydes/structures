package com.polydes.datastruct.data.structure.elements;

import javax.swing.*;

import org.w3c.dom.Element;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;

import stencyl.app.ext.res.AppResourceLoader;
import stencyl.app.ext.res.AppResources;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.io.XML;

public class StructureText extends SDE
{
	private static AppResources res = AppResourceLoader.getResources("com.polydes.datastruct");
	
	private String label;
	private String text;
	
	public StructureText(String label, String text)
	{
		this.label = label;
		this.text = text;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
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

	public static class TextType extends SDEType<StructureText>
	{
		public TextType()
		{
			sdeClass = StructureText.class;
			tag = "text";
			isBranchNode = false;
			icon = res.loadThumbnail("text.png", 16);
			childTypes = null;
		}
		
		@Override
		public StructureText read(StructureDefinition model, Element e)
		{
			return new StructureText(XML.read(e, "label"), XML.read(e, "text"));
		}

		@Override
		public void write(StructureText object, Element e, DataContext ctx)
		{
			XML.write(e, "label", object.getLabel());
			XML.write(e, "text", object.getText());
		}
		
		@Override
		public StructureText create(StructureDefinition def, StructureDefinitionEditor defEditor, String nodeName)
		{
			return new StructureText(nodeName, "");
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureText value, int i)
		{
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			group.add(sheet.style.createLabel(value.getLabel()), sheet.style.createDescriptionRow(value.getText()));
			group.add(sheet.style.rowgap);
			
			parentCard.addGroup(i, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureText value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureText value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureText value)
		{
			Row r = ((RowGroup) gui).rows[0];
			((JLabel) r.components[0]).setText(value.getLabel());
			((JLabel) r.components[1]).setText(value.getText());
		}
	}
}

package com.polydes.datastruct.data.structure.elements;

import org.w3c.dom.Element;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.Deck;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

import stencyl.app.ext.res.AppResourceLoader;
import stencyl.app.ext.res.AppResources;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.io.XML;
import stencyl.core.util.Lang;

public class StructureTab extends SDE
{
	private static AppResources res = AppResourceLoader.getResources("com.polydes.datastruct");
	
	private String label;
	
	public StructureTab(String label)
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

	public static class TabType extends SDEType<StructureTab>
	{
		public TabType()
		{
			sdeClass = StructureTab.class;
			tag = "tab";
			isBranchNode = true;
			icon = res.loadThumbnail("tab.png", 16);
			childTypes = Lang.arraylist(
				StructureCondition.class,
				StructureField.class,
				StructureHeader.class,
				StructureTabset.class,
				StructureText.class
			);
		}
		
		@Override
		public StructureTab read(StructureDefinition model, Element e)
		{
			return new StructureTab(XML.read(e, "label"));
		}

		@Override
		public void write(StructureTab object, Element e, DataContext ctx)
		{
			XML.write(e, "label", object.getLabel());
		}

		@Override
		public StructureTab create(StructureDefinition def, StructureDefinitionEditor defEditor, String nodeName)
		{
			return new StructureTab(nodeName);
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureTab value, int i)
		{
			Deck deckParent = getFirstDeckParent(sheet, parent);
			
			Card card = new Card(value.getLabel(), true);
			deckParent.addCard(card, i);
			
			return card;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureTab value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureTab value)
		{
			Card card = (Card) gui;
			if(card.deck != null)
				card.deck.removeCard(card);
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureTab value)
		{
			((Card) gui).button.setText(value.getLabel());
		}
		
		private Deck getFirstDeckParent(PropertiesSheet sheet, DefaultLeaf n)
		{
			while(!(n.getUserData() instanceof StructureTabset))
				n = n.getParent();
			
			return (Deck) ((RowGroup) sheet.guiMap.get(n)).rows[3].components[0].getComponent(0);
		}
	}
}

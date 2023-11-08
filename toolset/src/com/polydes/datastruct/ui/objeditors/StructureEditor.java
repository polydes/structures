package com.polydes.datastruct.ui.objeditors;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.ui.StructureIconProvider;
import com.polydes.datastruct.ui.table.PropertiesSheet;

import stencyl.app.api.nodes.NodeUIProperties;
import stencyl.app.comp.TitledPanel;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.app.comp.util.IconUtil;
import stencyl.app.comp.util.Layout;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;
import stencyl.core.api.pnodes.Leaf;

public class StructureEditor extends TitledPanel implements PropertyChangeListener
{
	public PropertiesSheet properties;
	public Structure structure;
	
	public StructureEditor(Structure structure)
	{
		this(structure, null);
	}
	
	public StructureEditor(Structure structure, HierarchyModel<DefaultLeaf, DefaultBranch> model)
	{
		super(structure.dref.getName(), StructureIconProvider.getStructureIcon(structure));
		
		this.structure = structure;
		
		properties = new PropertiesSheet(structure, model, PropertiesSheetStyle.DARK);
		structure.dref.addListener(this);
		
		add(Layout.aligned(properties, SwingConstants.LEFT, SwingConstants.TOP), BorderLayout.CENTER);
		
		revalidate();
	}
	
	public void highlightElement(DefaultLeaf n)
	{
		properties.highlightElement(n);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		
		if(properties != null)
			properties.dispose();
		properties = null;
		structure.dref.removeListener(this);
		structure = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		switch(evt.getPropertyName())
		{
			case Leaf.NAME:
				label.setText((String) evt.getNewValue());
				break;
			case NodeUIProperties.ICON:
				label.setIcon(IconUtil.getIcon((ImageIcon) evt.getNewValue(), TitledPanel.ICON_SIZE));
				break;
			default:
				//do nothing
				break;
		}
	}
}

package com.polydes.datastruct.ui;

import javax.swing.*;

import com.polydes.datastruct.data.structure.StructureDefinition;

import stencyl.app.api.nodes.NodeIconProvider;
import stencyl.core.api.pnodes.DefaultLeaf;

public class StructureDefinitionIconProvider implements NodeIconProvider<DefaultLeaf>
{
    @Override
    public ImageIcon getIcon(DefaultLeaf leaf)
    {
        if(leaf.getUserData() instanceof StructureDefinition sdef)
        {
            return new ImageIcon(sdef.getIconImg());
        }
        
        return null;
    }
}

package com.polydes.datastruct.ui;

import javax.swing.*;

import com.polydes.datastruct.data.structure.Structure;

import stencyl.app.api.nodes.NodeIconProvider;
import stencyl.core.api.pnodes.DefaultLeaf;

public class StructureIconProvider implements NodeIconProvider<DefaultLeaf>
{
    @Override
    public ImageIcon getIcon(DefaultLeaf leaf)
    {
        if(leaf.getUserData() instanceof Structure s)
        {
            return getStructureIcon(s);
        }

        return null;
    }
    
    public static ImageIcon getStructureIcon(Structure s)
    {
        if(s == null)
            return null;
        if(s.getTemplate().iconSource instanceof String iconSource)
            return s.getTemplate().getField(iconSource).getIcon(s.getPropByName(iconSource));
        else
            return new ImageIcon(s.getTemplate().getIconImg());
    }
}

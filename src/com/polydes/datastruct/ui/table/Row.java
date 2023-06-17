package com.polydes.datastruct.ui.table;

import javax.swing.*;

public class Row
{
	public JComponent[] components = null;
	public int height;
	
	public Row(int height, JComponent... comps)
	{
		this.height = height;
		this.components = comps;
	}
}
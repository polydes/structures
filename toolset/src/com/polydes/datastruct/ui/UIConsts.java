package com.polydes.datastruct.ui;

import java.awt.*;

import javax.swing.*;

import stencyl.app.ext.res.AppResourceLoader;
import stencyl.app.ext.res.AppResources;

public class UIConsts
{
	private static final AppResources res = AppResourceLoader.getResources("com.polydes.datastruct");
	
	public static final Color SIDEBAR_COLOR = new Color(62, 62, 62);
	public static final Color TEXT_EDITOR_COLOR = new Color(43, 43, 43);
	public static final Color TEXT_COLOR_BASE = Color.WHITE;
	public static final Color TREE_SELECTION_COLOR = new Color(102, 102, 102);
	
	public static final Font TREE_BRANCH_FONT = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 11.0f);
	public static final Font TREE_LEAF_FONT = UIManager.getFont("Label.font").deriveFont(11.0f);
	
	public static final int TREE_ITEM_HEIGHT = 20;
	
	public static final Font displayNameFont = new Font("Arial", Font.BOLD, 20);

	public static final ImageIcon folderIcon = res.loadIcon("page/folder-small.png");
}

package com.polydes.datastruct;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.page.StructureDefinitionPage;
import com.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import com.polydes.datastruct.ui.page.StructurePage;

import stencyl.app.comp.MiniSplitPane;
import stencyl.app.comp.darktree.DarkTree;

public class MainPage extends JPanel
{
	private static MainPage _instance;
	private JPanel pageView;
	private MiniSplitPane splitPane;
	
	
	private JButton defButton;
	private final ActionListener openDefinitionsWindowAction = e -> {
		StructureDefinitionsWindow.get();
		SwingUtilities.invokeLater(() -> StructureDefinitionsWindow.get().setVisible(true));
	};
	
	private MainPage()
	{
		super(new BorderLayout());
		
		//viewProviders.put()
		
		add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(createSidebar());
		splitPane.setRightComponent(pageView = new JPanel(new BorderLayout()));
		splitPane.setDividerLocation(DarkTree.DEF_WIDTH);
		
		pageView.add(StructurePage.get().getTreePage(), BorderLayout.CENTER);
	}
	
	private JPanel createSidebar()
	{
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		
		JPanel bWrapper = new JPanel();
		bWrapper.setBackground(UIConsts.SIDEBAR_COLOR);
		
		defButton = new JButton("Open Structure Editor");
		defButton.addActionListener(openDefinitionsWindowAction);
		defButton.setBackground(null);
		
		bWrapper.add(defButton);
		bWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		sidebar.add(bWrapper);
		sidebar.add(StructurePage.get().getSidebar());
		
		return sidebar;
	}
	
	public static MainPage get()
	{
		if(_instance == null)
			_instance = new MainPage();
		
		return _instance;
	}
	
	public void dispose()
	{
		pageView.removeAll();
		splitPane.removeAll();
		defButton.removeActionListener(openDefinitionsWindowAction);
		removeAll();
	}
	
	public static void disposePages()
	{
		StructurePage.disposeInstance();
		StructureDefinitionPage.dispose();
		StructureDefinitionsWindow.disposeWindow();
		if(_instance != null)
			_instance.dispose();
		_instance = null;
	}

	public void gameSaved()
	{
		revalidate();
		repaint();
	}
}
package com.polydes.datastruct.ui;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import stencyl.app.comp.ButtonBarFactory;
import stencyl.app.comp.GroupButton;
import stencyl.app.comp.dg.StencylDialog;
import stencyl.app.lnf.Theme;
import stencyl.core.loc.LanguagePack;
import stencyl.core.util.OS;
import stencyl.sw.app.main.SW;

public class MiniDialog extends StencylDialog
{
	private static final LanguagePack lang = LanguagePack.get();
	
	public boolean canceled;
	
	public MiniDialog(JComponent content, String title, int width, int height)
	{
		super(SW.get(), title, width, height, false, false);

		if(!OS.isMacOSXForStyling())
		{
			setBackground(Theme.APP_COLOR);
		}
		
		add(content, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public Object getResult()
	{
		return null;
	}

	@Override
	public void cancel()
	{
		canceled = true;
		setVisible(false);
	}

	@Override
	public JComponent createContentPanel()
	{
		return null;
	}

	@Override
	public JPanel createButtonPanel()
	{
		JButton okButton = new GroupButton(0);
		JButton cancelButton = new GroupButton(0);

		okButton.setAction
		(
			new AbstractAction(lang.get("globals.ok"))
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					canceled = false;
					setVisible(false);
				}
			}
		);

		cancelButton.setAction
		(
			new AbstractAction(lang.get("globals.cancel")) 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					cancel();
				}
			}
		);

		return ButtonBarFactory.createButtonBar
		(
			this,
			new JButton[] {okButton, cancelButton},
			0,
			false
		);
	}
}
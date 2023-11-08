package com.polydes.datastruct.ui.page;

import javax.swing.*;

import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.utils.LightweightWindow;

import stencyl.app.lnf.Theme;

public class PropertiesWindow extends LightweightWindow
{
	public PropertiesWindow(JDialog owner)
	{
		super(owner);
		setBackground(Theme.LIGHT_BUTTON_BAR_START);
		setContents(createContents());
	}
	
	/*-------------------------------------*\
	 * Construct UI
	\*-------------------------------------*/
	
	private StructureObjectPanel toEdit;
	
	public JPanel createContents()
	{
		if(toEdit != null)
			return toEdit.getView();
		else
			return new JPanel();
	}
	
	public void setObject(StructureObjectPanel toEdit)
	{
		if(this.toEdit != null)
			this.toEdit.dispose();
		
		this.toEdit = toEdit;
		setContents(createContents());
		
		validate();
		repaint();
	}
	
	@Override
	public void submit()
	{
		if(toEdit != null)
			toEdit.dispose();
		
		toEdit = null;
		
		super.submit();
	}
	
	@Override
	public void cancel()
	{
//		int result =
//			UI.showYesCancelPrompt(
//				"Discard Changes",
//				"Are you sure you'd like to discard changes?"
//			);
//		
//		if(UI.choseYes(result))
//		{
//			if(toEdit != null)
//			{
//				toEdit.revertChanges();
//				toEdit.disposeEditor();
//			}
//			
//			toEdit = null;
//			
//			super.cancel();
//		}
		
		if(toEdit != null)
			toEdit.dispose();
		toEdit = null;
		super.cancel();
	}

	@Override
	protected boolean verify()
	{
		boolean result = true;
		
		okButton.setEnabled(result);
		
		return result;
	}
}
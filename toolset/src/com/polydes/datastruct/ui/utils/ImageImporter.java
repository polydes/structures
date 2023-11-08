package com.polydes.datastruct.ui.utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import com.polydes.datastruct.utils.PngFilter;

import stencyl.app.comp.ImagePreview;
import stencyl.app.comp.filechooser.ImageFileView;
import stencyl.core.ext.FileHandler;
import stencyl.core.util.OS;
import stencyl.sw.app.main.SW;

public class ImageImporter implements ActionListener
{
	FileHandler handler;
	
	public ImageImporter(FileHandler handler)
	{
		this.handler = handler;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		File file = null;
		
		if(OS.isMacOSX()) 
		{
			FileDialog fc = new FileDialog(SW.get(), "Import Image");
			fc.setPreferredSize(new Dimension(800, 600));
			fc.setVisible(true);
			fc.setFilenameFilter(new PngFilter());
			
			if(fc.getFile() != null)
			{
				file = new File(fc.getDirectory(), fc.getFile());
			}
		}
		
		else
		{
			JFileChooser fc = new JFileChooser();
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new PngFilter());
			fc.setFileView(new ImageFileView());
			fc.setAccessory(new ImagePreview(fc));
			
			int returnVal = fc.showDialog(SW.get(), "Import Image");
		    
			if(returnVal == JFileChooser.APPROVE_OPTION)
		    {
		        file = fc.getSelectedFile();
		    }
		}

		if(file != null) 
		{
			handler.handleFile(file);
		}
	}
}

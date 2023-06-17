package com.polydes.datastruct.ui.table;

import java.awt.*;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;

public class Highlighter extends JComponent
{
	private Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	public JComponent[] target;
	public Rectangle r;
	
	public void setTargets(JComponent... comps)
	{
		target = comps;
		ready = false;
	}
	
	public void addTargets(JComponent... comps)
	{
		target = ArrayUtils.addAll(target, comps);
		ready = false;
	}
	
	public Rectangle getRect()
	{
		r = null;
		for(JComponent c : target)
			if(c != null)
				if(r == null) r = SwingUtilities.convertRectangle(c, SwingUtilities.getLocalBounds(c), this);
				else r.add(SwingUtilities.convertRectangle(c, SwingUtilities.getLocalBounds(c), this));
		
		return r;
	}
	
	public boolean ready = false;
	
	@Override
	public void paint(Graphics g)
	{
		if(target == null)
			return;
		
		if(!ready)
			getRect();
		
		if(r == null)
			return;
		
		ready = true;
		
		g.setColor(Color.RED);
		((Graphics2D) g).setStroke(stroke);
		if(r.height == 0)
			g.drawRect(r.x - 6, r.y - 6, r.width + 12, 0);
		else
			g.drawRect(r.x - 6, r.y - 6, r.width + 12, r.height + 12);
	}
}
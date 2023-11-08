package com.polydes.datastruct.ui.objeditors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.datastruct.ui.object.ObjectEditor;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.datastruct.ui.table.Table;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.EditorSheet;
import stencyl.app.comp.DisabledPanel;
import stencyl.app.comp.datatypes.array.SimpleArrayEditor;
import stencyl.app.comp.datatypes.string.ExpandingStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.app.comp.propsheet.PropertiesSheetSupport.FieldInfo;
import stencyl.app.comp.propsheet.PropertiesSheetWrapper;
import stencyl.app.comp.util.Layout;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.util.ColorUtil;

import static stencyl.core.util.Lang.asArray;

public class StructureObjectPanel extends Table implements EditorSheet, ObjectEditor
{
	protected PropertiesSheetSupport sheet;
	
	protected PropertiesSheet preview;
	protected DefaultLeaf previewKey;
	
	private HashMap<String, PropertiesSheetSupport> extensions = new HashMap<>();
	private HashMap<String, DisposableSheetWrapper> wrappers = new HashMap<>();
	
	public StructureObjectPanel(PropertiesSheetStyle style, Object model)
	{
		super(style);
		sheet = createSheetExtension(model, "base");
		
		setBorder(BorderFactory.createEmptyBorder(style.rowgap, style.rowgap, 0, style.rowgap));
	}
	
	private void addGenericRowAtInternal(int row, String label, JComponent... comps)
	{
		RowGroup group = new RowGroup(null);
		group.rows = new Row[0];
		group.add(style.createLabel(label), Layout.horizontalBox(comps));
		group.add(style.rowgap);
		addGroup(row, group);
	}
	
	public int addGenericRow(String label, JComponent... comps)
	{
		RowGroup group = new RowGroup(null);
		group.rows = new Row[0];
		group.add(style.createLabel(label), Layout.horizontalBox(comps));
		group.add(style.rowgap);
		addGroup(rows.length, group);
		return rows.length - 1;
	}
	
	public JCheckBox createEnabler(final DataEditor<?> editor, final DisabledPanel panel, final boolean initialValue)
	{
		final JCheckBox enabler = new JCheckBox();
		enabler.setSelected(initialValue);
		enabler.setBackground(null);
		
		enabler.addActionListener(new ActionListener()
		{
			private boolean enabled = initialValue;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(enabled != enabler.isSelected())
				{
					panel.setEnabled(enabler.isSelected());
					if(!enabler.isSelected())
						editor.setValue(null);
					previewKey.setDirty(true);
					enabled = enabler.isSelected();
					
					revalidate();
				}
			}
		});
		
		panel.setEnabled(enabler.isSelected());
		
		return enabler;
	}
	
	public void setRowVisibility(PropertiesSheetSupport sheet, String id, boolean visible)
	{
		int rowID = ((DisposableSheetWrapper) sheet.getWrapper()).rowIndex.get(id);
		rows[rowID].setConditionallyVisible(visible);
		
		layoutContainer();
		revalidate();
		setSize(getPreferredSize());
	}
	
	private ComponentListener resizeListener = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			setSize(getPreferredSize());
		}
	};
	
	public PropertiesSheetSupport createSheetExtension(Object model, String id)
	{
		DisposableSheetWrapper wrapper = new DisposableSheetWrapper();
		PropertiesSheetSupport support = new PropertiesSheetSupport(wrapper, this, style, model);
		
		extensions.put(id, support);
		wrappers.put(id, wrapper);
		
		return support;
	}
	
	public void clearSheetExtension(String id)
	{
		extensions.remove(id).dispose();
		wrappers.remove(id);
	}
	
	private void removeRow(int rowID)
	{
		removeGroup(rowID);
		for(DisposableSheetWrapper wrapper : wrappers.values())
			wrapper.decrementGreaterThan(rowID, 1);
	}
	
	public void setPreviewSheet(PropertiesSheet sheet, DefaultLeaf key)
	{
		preview = sheet;
		previewKey = key;
	}

	@Override
	public void saveChanges()
	{
		
	}

	@Override
	public void revertChanges()
	{
		for(PropertiesSheetSupport support : extensions.values())
			support.revertChanges();
	}

	@Override
	public void dispose()
	{
		sheet = null;
		preview = null;
		previewKey = null;
		
		removeAll();
		
		for(String key : asArray(extensions.keySet(), String.class))
			clearSheetExtension(key);
		
		extensions = null;
		wrappers = null;
	}

	@Override
	public Object getSheetProperty(String property)
	{
		if(preview == null) return null;
		return preview.getSheetProperty(property);
	}

	// PropertiesSheetWrapper
	
	public class DisposableSheetWrapper implements PropertiesSheetWrapper
	{
		private boolean editorsInitialized = false;
		private HashMap<String, Integer> rowIndex = new HashMap<>();
		
		public void decrementGreaterThan(int pivot, int amount)
		{
			for(String rowKey : rowIndex.keySet())
			{
				int rowIsAt = rowIndex.get(rowKey);
				if(rowIsAt > pivot)
					rowIndex.put(rowKey, rowIsAt - amount);
			}
		}
		
		private JComponent[] buildRow(FieldInfo field, DataEditor<?> editor)
		{
			JComponent[] comps = editor.getComponents();
			
			/*
			 * TODO: For further customization, these can be placed in a map
			 * of resizing editors. Hardcoding for now.
			 */
			if(editor instanceof SimpleArrayEditor || editor instanceof ExpandingStringEditor)
			{
				final JComponent[] resizingComps = comps;
				for(JComponent comp : resizingComps)
					comp.addComponentListener(resizeListener);
				editor.addDisposeListener(() -> {
					for(JComponent comp : resizingComps)
						comp.removeComponentListener(resizeListener);
				});
			}
			
			String hint = field.getHint();
			if(hint != null && !hint.isEmpty())
				comps = ArrayUtils.add(comps, style.createEditorHint(hint));
			
			if(field.isOptional())
			{
				Color bg = ColorUtil.deriveTransparent(style.pageBg, 130);
				DisabledPanel dpanel = new DisabledPanel(Layout.horizontalBox(comps), bg);
				dpanel.setBackground(style.pageBg);
				//TODO: editor.getValue() will NOT be an accurate value at this time.
				JCheckBox enabler = createEnabler(editor, dpanel, editor.getValue() != null);
				
				return new JComponent[] {enabler, dpanel};
			}
			else
				return comps;
		}
		
		@Override
		public void addField(FieldInfo newField, DataEditor<?> editor)
		{
			JComponent[] comps = buildRow(newField, editor);
			editor.addListener(() -> {if(editorsInitialized && previewKey != null) previewKey.setDirty(true);});
			
			int row = addGenericRow(newField.getLabel(), comps);
			rowIndex.put(newField.getVarname(), row);
		}
		
		@Override
		public void changeField(String varname, FieldInfo field, DataEditor<?> editor)
		{
			JComponent[] comps = buildRow(field, editor);
			editor.addListener(() -> {if(editorsInitialized && previewKey != null) previewKey.setDirty(true);});
			
			int row = rowIndex.get(varname);
			removeGroup(row);
			addGenericRowAtInternal(row, field.getLabel(), comps);
			
			layoutContainer();
			revalidate();
			setSize(getPreferredSize());
		}
		
		@Override
		public void addHeader(String title)
		{
			int row = addGenericRow("", style.createRoundedLabel(title));
			rowIndex.put("H: " + title, row);
		}
		
		@Override
		public void finish()
		{
			//TODO: finish() is only called after adding fields, not after changing fields.
			//may need to fix some dirtiness logic here.
			editorsInitialized = true;
		}

		@Override
		public void dispose()
		{
			for(String key : rowIndex.keySet())
				removeRow(rowIndex.get(key));
			rowIndex = null;
		}
	}
}
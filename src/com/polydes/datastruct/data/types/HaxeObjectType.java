package com.polydes.datastruct.data.types;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.datastruct.data.core.HaxeObject;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.EditorProviders;
import stencyl.app.api.datatypes.EditorSheet;
import stencyl.app.api.datatypes.UpdateListener;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.app.comp.util.Layout;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.DataTypeProperties;

public class HaxeObjectType extends DataType<HaxeObject>
{
	private HaxeObjectDefinition def;
	
	public HaxeObjectType(HaxeObjectDefinition def)
	{
		super(HaxeObject.class, def.haxeClass);
		this.def = def;
	}
	
	public HaxeObjectDefinition getDef()
	{
		return def;
	}

	@Override
	public HaxeObject decode(String s, DataContext ctx)
	{
		String[] parts = s.length() <= 2 ?
				ArrayUtils.EMPTY_STRING_ARRAY :
				s.substring(1, s.length() - 1).split(",");
		Object[] values = new Object[def.fields.length];
		for(int i = 0; i < def.fields.length; ++i)
			values[i] = def.fields[i].type.dataType.decode(parts.length > i ? parts[i] : def.fields[i].defaultValue, ctx);
		
		return new HaxeObject(def, values);
	}

	@Override
	public String encode(HaxeObject o, DataContext ctx)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		for(int i = 0; i < o.values.length; ++i)
		{
			sb.append(def.fields[i].type.dataType.checkEncode(o.values[i], ctx));
			if(i + 1 < o.values.length)
				sb.append(",");
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	@Override
	public String toDisplayString(HaxeObject data)
	{
		return encode(data, DataContext.NO_CONTEXT);
	}
	
	@Override
	public HaxeObject copy(HaxeObject t)
	{
		return new HaxeObject(t);
	}
	
	public class HaxeObjectEditor extends DataEditor<HaxeObject>
	{
		final JLabel[] labels;
		final DataEditor<?>[] editors;
		final JComponent[] comps;
		
		public HaxeObjectEditor(DataTypeProperties props, EditorSheet sheet, PropertiesSheetStyle style)
		{
			editors = new DataEditor[def.fields.length];
			labels = def.showLabels ?
				new JLabel[def.fields.length] : null;
			
			UpdateListener updater = () -> updated();
			
			for(int i = 0; i < def.fields.length; ++i)
			{
				HaxeDataType htype = def.fields[i].type;
				editors[i] = EditorProviders.createEditor(htype.dataType, htype.loadExtras(def.fields[i].editorData), sheet, style);
				editors[i].addListener(updater);
				if(labels != null)
				{
					labels[i] = style.createLabel(def.fields[i].name);
					labels[i].setHorizontalAlignment(SwingConstants.CENTER);
				}
			}
			
			List<JComponent> jcomps = new ArrayList<JComponent>();
			for(int i = 0; i < def.fields.length; ++i)
			{
				JComponent c = new JPanel(new BorderLayout());
				c.setBackground(null);
				if(labels != null)
					c.add(labels[i], BorderLayout.NORTH);
				c.add(Layout.horizontalBox(editors[i].getComponents()), BorderLayout.CENTER);
				jcomps.add(c);
			}
			
			comps = jcomps.toArray(new JComponent[jcomps.size()]);
		}
		
		@Override
		public void set(HaxeObject o)
		{
			if(o == null)
				o = new HaxeObject(def, new Object[def.fields.length]);
			for(int i = 0; i < def.fields.length; ++i)
				editors[i].setValueUnchecked(o.values[i]);
		}
		
		@Override
		public HaxeObject getValue()
		{
			Object[] values = new Object[editors.length];
			for(int i = 0; i < editors.length; ++i)
				values[i] = editors[i].getValue();
			return new HaxeObject(def, values);
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps;
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			for(DataEditor<?> editor : editors)
				editor.dispose();
		}
	}
}

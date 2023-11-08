package com.polydes.datastruct.data.structure.elements;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypeConverter;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.EditorProviders;
import stencyl.app.api.datatypes.UpdateListener;
import stencyl.app.comp.DisabledPanel;
import stencyl.app.comp.datatypes.array.StandardArrayEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.app.comp.util.Layout;
import stencyl.app.ext.res.AppResourceLoader;
import stencyl.app.ext.res.AppResources;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.datatypes.ArrayType;
import stencyl.core.ext.registry.RORealizer;
import stencyl.core.io.XML;
import stencyl.core.io.XmlHelper;
import stencyl.core.util.ColorUtil;

public class StructureField extends SDE implements RORealizer<HaxeDataType>
{
	private static AppResources res = AppResourceLoader.getResources("com.polydes.datastruct");
	
	private StructureDefinition owner;
	
	private String varname;
	private HaxeDataType type;
	private String label;
	private String hint;
	private boolean optional;
	private Object defaultValue;
	private DataTypeProperties props;
	
	private ExtrasMap emap;
	
	public StructureField(StructureDefinition owner, String varname, String type, String label, String hint, boolean optional, String defaultValue, ExtrasMap extras)
	{
		this.owner = owner;
		this.varname = varname;
		this.label = label;
		this.hint = hint;
		this.optional = optional;
		this.defaultValue = defaultValue;
		
		emap = extras;
		DataStructuresExtension.get().getHaxeTypes().requestValue(type, this);
	}
	
	@Override
	public void realizeRO(HaxeDataType type)
	{
		this.type = type;
		this.props = type.loadExtras(emap);
		if(defaultValue instanceof String)
		{
			DataContext ctx = owner.getCtx();
			this.defaultValue = HaxeTypeConverter.decode(type.dataType, (String) defaultValue, ctx);
		}
		emap = null;
		if(waitingForTypeInfo != null)
			for(Runnable r : waitingForTypeInfo)
				r.run();
		waitingForTypeInfo = null;
	}
	
	private Runnable[] waitingForTypeInfo = null;
	
	public void waitForTypeInfo(Runnable callback)
	{
		if(type != null)
			callback.run();
		else
		{
			if(waitingForTypeInfo == null)
				waitingForTypeInfo = new Runnable[] {callback};
			else
				waitingForTypeInfo = ArrayUtils.add(waitingForTypeInfo, callback);
		}
	}
	
	public StructureDefinition getOwner()
	{
		return owner;
	}
	
	public void loadExtras(ExtrasMap extras)
	{
		this.props = type.loadExtras(extras);
	}
	
	public DataTypeProperties getEditorProperties()
	{
		return props;
	}
	
	public void setEditorProperties(DataTypeProperties props)
	{
		this.props = props;
	}
	
	public String getHint()
	{
		return hint;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public String getVarname()
	{
		return varname;
	}
	
	public HaxeDataType getType()
	{
		return type;
	}
	
	public boolean isOptional()
	{
		return optional;
	}
	
	public Object getDefaultValue()
	{
		return defaultValue;
	}
	
	public void setHint(String hint)
	{
		this.hint = hint;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}
	
	public void setDefaultValue(Object defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	
	public void setType(HaxeDataType type)
	{
		this.type = type;
	}
	
	@Override
	public String toString()
	{
		return varname + ":" + type;
	}

	public ImageIcon getIcon(Object value)
	{
		return type.getIcon(value);
	}

	public static String formatVarname(String s)
	{
		s = StringUtils.removePattern(s, "[^a-zA-Z0-9_]");
		
		if(s.isEmpty())
			return s;
		
		if(Character.isDigit(s.charAt(0)))
			s = "_" + s;
		if(Character.isUpperCase(s.charAt(0)))
			s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
		
		return s;
	}

	@Override
	public String getDisplayLabel()
	{
		return label;
	}
	
	public static class FieldType extends SDEType<StructureField>
	{
		public FieldType()
		{
			sdeClass = StructureField.class;
			tag = "field";
			isBranchNode = false;
			icon = res.loadThumbnail("field.png", 16);
			childTypes = null;
		}
		
		@Override
		public StructureField read(StructureDefinition model, Element e)
		{
			HashMap<String, String> map = XML.readMap(e);
			
			String name = take(map, "name");
			String type = take(map, "type");
			String label = take(map, "label");
			String hint = take(map, "hint");
			boolean optional = take(map, "optional").equals("true");
			String defaultValue = take(map, "default");
			ExtrasMap emap = new ExtrasMap(model.getCtx());
			emap.backingPutAll(map);
			if(e.hasChildNodes())
				XmlHelper.children(e).forEach((child) -> emap.backingPut(child.getTagName(), readExtrasFromElement(child, model.getCtx())));
			
			StructureField toAdd = new StructureField(model, name, type, label, hint, optional, defaultValue, emap);
			model.addField(toAdd);
			toAdd.waitForTypeInfo(() -> model.realizeFieldHaxeType(toAdd, toAdd.type));
			
			return toAdd;
		}
		
		public static ExtrasMap readExtrasFromElement(Element e, DataContext ctx)
		{
			ExtrasMap emap = new ExtrasMap(ctx);
			emap.backingPutAll(XML.readMap(e));
			if(e.hasChildNodes())
				XmlHelper.children(e).forEach((child) -> emap.backingPut(child.getTagName(), readExtrasFromElement(child, ctx)));
			return emap;
		}
		
		@Override
		public void write(StructureField f, Element e, DataContext ctx)
		{
			e.setAttribute("name", f.getVarname());
			e.setAttribute("type", f.getType().getHaxeType());
			XML.write(e, "label", f.getLabel());
			if(!f.getHint().isEmpty())
				XML.write(e, "hint", f.getHint());
			if(f.isOptional())
				e.setAttribute("optional", "true");
			if(f.getDefaultValue() != null)
				e.setAttribute("default", HaxeTypeConverter.encode(f.getType().dataType, f.getDefaultValue(), ctx));
			
			HaxeDataType dtype = f.getType();
			ExtrasMap emap = dtype.saveExtras(f.getEditorProperties(), ctx);
			if(emap != null)
				writeExtrasToElement(e.getOwnerDocument(), e, emap);
		}
		
		public static void writeExtrasToElement(Document doc, Element e, ExtrasMap emap)
		{
			for(Entry<String,Object> entry : emap.backingEntrySet())
			{
				if(entry.getValue() instanceof ExtrasMap)
				{
					Element child = doc.createElement(entry.getKey());
					writeExtrasToElement(doc, child, (ExtrasMap) entry.getValue());
					e.appendChild(child);
				}
				else if(entry.getValue() != null)
					e.setAttribute(entry.getKey(), (String) entry.getValue());
			}
		}
		
		private String take(HashMap<String, String> map, String name)
		{
			if(map.containsKey(name))
				return map.remove(name);
			else
				return "";
		}

		@Override
		public StructureField create(StructureDefinition def, StructureDefinitionEditor defEditor, String nodeName)
		{
			StructureField newField =
					new StructureField(def, StructureField.formatVarname(nodeName), HaxeTypes._String.getHaxeType(), nodeName, "", false, null, new ExtrasMap(def.getCtx()));
			defEditor.addField(newField, defEditor.getPreview());
			return newField;
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, StructureField value, int i)
		{
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			psLoad(sheet, group, node, value);
			
			parentCard.addGroup(i, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureField value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			psLoad(sheet, group, node, value);
			
			card.addGroup(groupIndex, group);
			card.layoutContainer();
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureField value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			sheet.fieldEditorMap.remove(value).dispose();
			
			card.layoutContainer();
		}
		
		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, StructureField value)
		{
			RowGroup group = (RowGroup) gui;
			
			((JLabel) group.rows[0].components[0]).setText(value.getLabel());
			if(!value.getHint().isEmpty())
				sheet.style.setDescription((JLabel) group.rows[2].components[1], value.getHint());
		}
		
		/*================================================*\
		 | Helpers
		\*================================================*/
		
		public void psLoad(PropertiesSheet sheet, RowGroup group, DefaultLeaf node, StructureField f)
		{
			String name = f.getLabel().isEmpty() ? f.getVarname() : f.getLabel();
			
			group.rows = new Row[0];
			group.add(sheet.style.createLabel(name), createEditor(sheet, f));
			if(!f.getHint().isEmpty())
			{
				group.add(sheet.style.hintgap);
				group.add(null, sheet.style.createDescriptionRow(f.getHint()));
			}
			group.add(sheet.style.rowgap);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public JComponent createEditor(PropertiesSheet sheet, final StructureField f)
		{
			JComponent editPanel = null;
			
			DataType type = f.getType().dataType;
			
			if(sheet.fieldEditorMap.containsKey(f))
				sheet.fieldEditorMap.get(f).dispose();
			
			final DataEditor deditor = EditorProviders.createEditor(type, f.getEditorProperties(), sheet, sheet.style);
			
			if(deditor instanceof StandardArrayEditor)
			{
				DataType<?> genType = f.getEditorProperties().get(ArrayType.GEN_TYPE);
				HaxeDataType htype = DataStructuresExtension.get().getHaxeTypes().getHaxeFromDT(genType.getId());
				if(htype.isIconProvider())
					((StandardArrayEditor) deditor).getEditorComponent().setIconProvider(o -> htype.getIcon(o));
			}
			
			deditor.setValue(sheet.model.getProperty(f));
			deditor.addListener(() -> {
				sheet.model.setProperty(f, deditor.getValue());
				sheet.refreshVisibleComponents();
			});
			
			sheet.fieldEditorMap.put(f, deditor);
			
			editPanel = Layout.horizontalBox(sheet.style.fieldDimension, deditor.getComponents());
			
			if(f.isOptional())
			{
				Color bg = ColorUtil.deriveTransparent(sheet.style.pageBg, 210);
				DisabledPanel dpanel = new DisabledPanel(editPanel, bg);
				dpanel.setBackground(sheet.style.pageBg);
				return constrict(sheet.style, createEnabler(sheet.model, dpanel, f), dpanel);
			}
			else
				return editPanel;
		}
		
		private JCheckBox createEnabler(final Structure model, final DisabledPanel dpanel, final StructureField f)
		{
			final JCheckBox enabler = new JCheckBox();
			enabler.setSelected(model.isPropertyEnabled(f));
			enabler.setBackground(null);
			
			enabler.addActionListener(e -> {
				if(model.isPropertyEnabled(f) != enabler.isSelected())
				{
					dpanel.setEnabled(enabler.isSelected());
					model.setPropertyEnabled(f, enabler.isSelected());
					if(!enabler.isSelected())
						model.clearProperty(f);
				}
			});
			
			dpanel.setEnabled(model.isPropertyEnabled(f));
			
			return enabler;
		}
		
		private JPanel constrict(PropertiesSheetStyle style, JComponent... comps)
		{
			return Layout.horizontalBox(style.fieldDimension, comps);
		}
	}
}
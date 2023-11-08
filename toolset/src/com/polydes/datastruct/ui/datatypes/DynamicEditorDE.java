package com.polydes.datastruct.ui.datatypes;

import java.awt.*;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.datastruct.data.core.Dynamic;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypes;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.DataEditorBuilder;
import stencyl.app.api.datatypes.EditorProviders;
import stencyl.app.api.datatypes.EditorSheet;
import stencyl.app.api.datatypes.UpdateListener;
import stencyl.app.comp.dg.DialogPanel;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.app.comp.util.Layout;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.datatypes.properties.PropertyKey;

@SuppressWarnings("rawtypes")
public class DynamicEditorDE extends DataEditor<Dynamic>
{
    private static final DataTypeProperties noProps = new DataTypeProperties()
    {
        @Override
        public <T, U extends T> T put(PropertyKey<T> key, U value)
        {
            throw new RuntimeException();
        };
    };

    private final HaxeDataTypeEditorDE typeChooser;
    private final JPanel valueEditorWrapper;
    private final JComponent[] comps;

    private DataEditor valueEditor;
    private PropertiesSheetStyle style;

    private Dynamic data;
    private EditorSheet sheet;

    public DynamicEditorDE(DataTypeProperties props, EditorSheet sheet, PropertiesSheetStyle style)
    {
        this.sheet = sheet;
        this.style = style;

        typeChooser = new HaxeDataTypeEditorDE(null, sheet, style);
        valueEditorWrapper = new JPanel();
        valueEditorWrapper.setBackground(null);

        typeChooser.addListener(() -> {
            setType(typeChooser.getValue());
            updated();
        });

        JComponent[] typeChooserComps = typeChooser.getComponents();
        comps = ArrayUtils.add(typeChooserComps, valueEditorWrapper);
    }

//		public void excludeTypes(final HashSet<DataType<?>> types)
//		{
//			typeChooser.setFilter(new Predicate<DataType<?>>()
//			{
//				@Override
//				public boolean test(DataType<?> t)
//				{
//					return !types.contains(t);
//				}
//			});
//		}

    @Override
    public Dynamic getValue()
    {
        return data;
    }

    @Override
    public void set(Dynamic t)
    {
        if(t == null)
            t = new Dynamic("", HaxeTypes._String);
        data = t;
        typeChooser.setValue(t.type);
    }

    @SuppressWarnings("unchecked")
    private void setType(HaxeDataType newType)
    {
        if(valueEditor == null || !newType.equals(data.type))
        {
            data.type = newType;
            if(!newType.dataType.javaType.isInstance(data.value))
                data.value = newType.dataType.decode("", DataContext.NO_CONTEXT);

            valueEditorWrapper.removeAll();

            if(valueEditor != null)
                valueEditor.dispose();

            JComponent editor = null;

            valueEditor = EditorProviders.createEditor(newType.dataType, noProps, sheet, PropertiesSheetStyle.DARK);
            valueEditor.setValue(data.value);
            valueEditor.addListener(() -> {
                data.value = valueEditor.getValue();
                DynamicEditorDE.this.updated();
            });

            editor = Layout.horizontalBox(style.fieldDimension, valueEditor.getComponents());

            valueEditorWrapper.add(editor, BorderLayout.CENTER);
            valueEditorWrapper.revalidate();
        }
    }

    @Override
    public JComponent[] getComponents()
    {
        return comps;
    }

    public DialogPanel createMiniPage()
    {
        DialogPanel page = new DialogPanel(style.pageBg.darker());
        page.addGenericRow("Type", Layout.horizontalBox(typeChooser.getComponents()));
        page.addGenericRow("Value", valueEditorWrapper);
        page.finishBlock();

        return page;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        typeChooser.dispose();
        valueEditorWrapper.removeAll();
        if(valueEditor != null)
            valueEditor.dispose();

        data = null;
        valueEditor = null;
        style = null;
    }

    public static class Builder extends DataEditorBuilder
    {
    }
}
package com.polydes.datastruct.ui.datatypes;

import java.util.Comparator;

import javax.swing.*;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.types.HaxeDataType;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.DataEditorBuilder;
import stencyl.app.api.datatypes.EditorSheet;
import stencyl.app.api.datatypes.PropertyBuilder;
import stencyl.app.comp.UpdatingCombo;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.api.datatypes.properties.DataTypeProperties;

public class HaxeDataTypeEditorDE extends DataEditor<HaxeDataType>
{
    final UpdatingCombo<HaxeDataType> typeChooser;

    public HaxeDataTypeEditorDE(DataTypeProperties props, EditorSheet sheet, PropertiesSheetStyle style)
    {
        typeChooser = new UpdatingCombo<>(DataStructuresExtension.get().getHaxeTypes().values(), null);
        typeChooser.setComparator(Comparator.comparing(HaxeDataType::getHaxeType));
        typeChooser.addActionListener(event -> updated());
    }

    @Override
    public void set(HaxeDataType t)
    {
        typeChooser.setSelectedItem(t);
    }

    @Override
    public HaxeDataType getValue()
    {
        return typeChooser.getSelected();
    }

    @Override
    public JComponent[] getComponents()
    {
        return new JComponent[] {typeChooser};
    }

    @Override
    public void dispose()
    {
        super.dispose();
        typeChooser.dispose();
    }

    public static class Builder extends DataEditorBuilder implements PropertyBuilder
    {
    }
}
package com.polydes.datastruct.ui.datatypes;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.*;

import com.polydes.datastruct.data.core.ExtrasResource;
import com.polydes.datastruct.data.types.ExtrasResourceType;
import com.polydes.datastruct.data.types.ExtrasResourceType.ResourceType;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.DataEditorBuilder;
import stencyl.app.api.datatypes.EditorSheet;
import stencyl.app.comp.datatypes.file.FileEditor;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.datatypes.Types;
import stencyl.core.util.Lang;

import static stencyl.core.lib.ProjectLocations.EXTRA_DATA;
import static stencyl.core.util.PathHelper.abs;

public class ExtrasResourceEditorDE extends DataEditor<ExtrasResource>
{
    final FileEditor fileEditor;

    public ExtrasResourceEditorDE(DataTypeProperties props, EditorSheet sheet)
    {
        ResourceType type = Lang.or(props.get(ExtrasResourceType.RESOURCE_TYPE), ResourceType.ANY);

        fileEditor =
            (FileEditor) DataEditorBuilder.create(FileEditor.Builder.class, Types._File, new DataTypeProperties())
                .rootDirectory(abs(sheet.getProject().getPath(EXTRA_DATA))).filter(type.getFilter()).rendered()
                .build(sheet, sheet.getSheetStyle());

        fileEditor.addListener(() -> updated());
    }

    @Override
    public ExtrasResource getValue()
    {
        Path f = fileEditor.getValue();
        if(f == null)
            return null;
        if(!Files.exists(f))
            return null;
        ExtrasResource r = new ExtrasResource();
        r.file = f;
        return r;
    }

    @Override
    public void set(ExtrasResource t)
    {
        if(t == null)
            fileEditor.setValue(null);
        else
            fileEditor.setValue(t.file);
    }

    @Override
    public JComponent[] getComponents()
    {
        return fileEditor.getComponents();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        fileEditor.dispose();
    }

    public static class Builder extends DataEditorBuilder
    {
        public Builder type(ResourceType type)
        {
            putProp(ExtrasResourceType.RESOURCE_TYPE, type);
            return this;
        }
    }
}

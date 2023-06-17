package com.polydes.datastruct.ui.datatypes;

import java.util.Map;

import com.polydes.datastruct.data.types.DSTypes;

import stencyl.app.api.datatypes.EditorProviders;
import stencyl.app.api.datatypes.EditorProviders.EditorInitializer;
import stencyl.core.api.datatypes.DataType;

import static stencyl.core.api.datatypes.DataType.UNSET_EDITOR;

public class DSEditorProviders
{
    public static void registerBuiltinEditors()
    {
        Map<DataType<?>, Map<String, EditorInitializer<?>>> editors = EditorProviders.editors;

        editors.put(DSTypes._Dynamic, Map.of(
            UNSET_EDITOR, (EditorInitializer) DynamicEditorDE::new
        ));
        editors.put(DSTypes._ExtrasResource, Map.of(
            UNSET_EDITOR, (EditorInitializer) ExtrasResourceEditorDE::new
        ));
        editors.put(DSTypes._HaxeDataType, Map.of(
            UNSET_EDITOR, (EditorInitializer) HaxeDataTypeEditorDE::new
        ));
    }
    
    public static void unregisterBuiltinEditors()
    {
        Map<DataType<?>, Map<String, EditorInitializer<?>>> editors = EditorProviders.editors;

        editors.remove(DSTypes._Dynamic);
        editors.remove(DSTypes._ExtrasResource);
        editors.remove(DSTypes._HaxeDataType);
    }
}

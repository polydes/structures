package com.polydes.datastruct.ui.objeditors;

import java.util.HashMap;
import java.util.Map;

import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.structure.elements.StructureHeader;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.structure.elements.StructureTabset;
import com.polydes.datastruct.data.structure.elements.StructureText;
import com.polydes.datastruct.data.structure.elements.StructureUnknown;

import stencyl.app.comp.propsheet.PropertiesSheetStyle;

public class SdePanelProviders
{
    public interface SdePanelProvider<T extends SDE>
    {
        StructureObjectPanel getPanel(T sde, StructureDefinitionEditor structDefEditor);
    }
    
    private static final Map<Class<? extends SDE>, SdePanelProvider<?>> providers = new HashMap<>();

    public static <T extends SDE> void register(Class<T> cls, SdePanelProvider<T> provider)
    {
        providers.put(cls, provider);
    }

    public static <T extends SDE> StructureObjectPanel getPanel(T sde, StructureDefinitionEditor structDefEditor)
    {
        SdePanelProvider<T> provider = (SdePanelProvider) providers.get(sde.getClass());
        if(provider == null) return null;
        return provider.getPanel(sde, structDefEditor);
    }
    
    static
    {
        //SDEs
       register(StructureCondition.class, (sc, defEditor) -> new StructureConditionPanel(sc, PropertiesSheetStyle.LIGHT));
       register(StructureField.class, (sf, defEditor) -> new StructureFieldPanel(sf, defEditor, PropertiesSheetStyle.LIGHT));
       register(StructureHeader.class, (sh, defEditor) -> new StructureHeaderPanel(sh, PropertiesSheetStyle.LIGHT));
       register(StructureTab.class, (st, defEditor) -> new StructureTabPanel(st, PropertiesSheetStyle.LIGHT));
       register(StructureTabset.class, null);
       register(StructureText.class, (st, defEditor) -> new StructureTextPanel(st, PropertiesSheetStyle.LIGHT));
       register(StructureUnknown.class, null);
    }
}

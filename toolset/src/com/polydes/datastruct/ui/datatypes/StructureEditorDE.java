package com.polydes.datastruct.ui.datatypes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Predicate;

import javax.swing.*;

import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.types.StructureType;
import com.polydes.datastruct.ui.StructureIconProvider;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.DataEditorBuilder;
import stencyl.app.api.datatypes.EditorSheet;
import stencyl.app.api.nodes.NodeUIProperties;
import stencyl.app.comp.RenderedPanel;
import stencyl.app.comp.UpdatingCombo;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.api.datatypes.properties.DataTypeProperties;

import static com.polydes.datastruct.ui.table.PropertiesSheet.STRUCTURE_PROPERTY;

public class StructureEditorDE extends DataEditor<Structure> implements PropertyChangeListener
{
    private final StructureDefinition def;
    
    private final RenderedPanel panel;
    private final UpdatingCombo<Structure> editor;
    private Structure oldStructure;

    public StructureEditorDE(StructureType type, DataTypeProperties props, EditorSheet sheet, PropertiesSheetStyle style)
    {
        this.def = type.def;
        Structure currentStructure = (Structure) sheet.getSheetProperty(STRUCTURE_PROPERTY);
        StructureCondition condition = props.get(StructureType.SOURCE_FILTER);
        Predicate<Structure> predicate = condition == null ? null : new StructurePredicate(condition, currentStructure);

        boolean allowSubtypes = props.get(StructureType.ALLOW_SUBTYPES) == Boolean.TRUE;

        if(allowSubtypes)
        {
            Predicate<Structure> onlySubtypes = (s) -> s.getTemplate().is(def);
            predicate = predicate == null ? onlySubtypes : onlySubtypes.and(predicate);
        }

        editor = new UpdatingCombo<>(allowSubtypes ? Structures.structuresByID.values() : Structures.getList(def), predicate);
        editor.setIconProvider(StructureIconProvider::getStructureIcon);
        editor.addActionListener(event -> valueUpdated(true));
        oldStructure = null;

        panel = props.get(StructureType.RENDER_PREVIEW) == Boolean.TRUE ?
            new RenderedPanel(90, 60, 0) : null;
    }

    public StructureEditorDE(StructureType type)
    {
        this.def = type.def;
        editor = new UpdatingCombo<>(Structures.getList(def), null);
        editor.addActionListener(event -> updated());
        panel = null;
    }

    private void valueUpdated(boolean callUpdated)
    {
        Structure t = editor.getSelected();
        if(t == oldStructure)
            return;
        if(panel != null)
        {
            if(oldStructure != null)
                uninstallIconListener(oldStructure);
            if(t != null)
                installIconListener(t);
            else
                panel.setLabel(null);
        }
        oldStructure = t;
        if(callUpdated)
            updated();
    }

    private void installIconListener(Structure t)
    {
        t.dref.addListener(NodeUIProperties.ICON, this);
        setImageIcon(StructureIconProvider.getStructureIcon(t));
    }

    private void uninstallIconListener(Structure t)
    {
        t.dref.removeListener(NodeUIProperties.ICON, this);
        setImageIcon(StructureIconProvider.getStructureIcon(t));
    }

    private void setImageIcon(ImageIcon icon)
    {
        if(icon == null)
            panel.setLabel(null);
        else
            panel.setLabel(icon.getImage());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        setImageIcon((ImageIcon) evt.getNewValue());
    }

    @Override
    public void set(Structure t)
    {
        editor.setSelectedItem(t);
        valueUpdated(false);
    }

    @Override
    public Structure getValue()
    {
        return editor.getSelected();
    }

    @Override
    public JComponent[] getComponents()
    {
        if(panel != null)
            return new JComponent[] {panel, editor};
        else
            return new JComponent[] {editor};
    }

    @Override
    public void dispose()
    {
        super.dispose();
        if(panel != null && oldStructure != null)
            uninstallIconListener(oldStructure);
        editor.dispose();
    }

    public class Builder extends DataEditorBuilder
    {
        public Builder filter(StructureCondition filter)
        {
            props.put(StructureType.SOURCE_FILTER, filter);
            return this;
        }

        public Builder allowSubtypes()
        {
            props.put(StructureType.ALLOW_SUBTYPES, true);
            return this;
        }

        public Builder rendered()
        {
            props.put(StructureType.RENDER_PREVIEW, Boolean.TRUE);
            return this;
        }
    }

    class StructurePredicate implements Predicate<Structure>
    {
        private StructureCondition condition;
        private Structure s;

        public StructurePredicate(StructureCondition condition, Structure s)
        {
            this.condition = condition;
            this.s = s;
        }

        @Override
        public boolean test(Structure s2)
        {
            return condition.check(s, s2);
        }
    }
}

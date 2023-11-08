package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.DSTypes;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.table.PropertiesSheet;

import stencyl.app.comp.datatypes.string.ExpandingStringEditor;
import stencyl.app.comp.datatypes.string.SingleLineStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.app.comp.propsheet.PropertiesSheetSupport;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.datatypes.Types;

public class StructureFieldPanel extends StructureObjectPanel
{
	StructureField field;

	boolean oldDirty;

	PropertiesSheetSupport editorSheet;

	public StructureFieldPanel(final StructureField field, StructureDefinitionEditor defEditor, PropertiesSheetStyle style)
	{
		super(style, field);

		this.field = field;

		String nameHint =
			"Variable Name Format:<br/>" +
				"A letter or underscore, followed by any<br/>" +
				"number of letters, numbers, or underscores.";

		sheet.build()

			.field("label")._editor(Types._String).add()

			.field("type")._editor(DSTypes._HaxeDataType).add()

			.field("varname").label("Name").hint(nameHint)._editor(SingleLineStringEditor.BUILDER).regex("([a-zA-Z_][a-z0-9A-Z_]*)?").add()

			.field("hint")._editor(ExpandingStringEditor.BUILDER).add()

			.field("optional")._editor(Types._Bool).add().onUpdate(() -> preview.refreshLeaf(previewKey))

			.field("defaultValue").label("Default")._editor(field.getType().dataType).loadProps(field.getEditorProperties()).add()

			.finish();

		sheet.addPropertyChangeListener("label", event -> {
			previewKey.setName(field.getLabel());

			String oldLabel = (String) event.getOldValue();
			if(StructureField.formatVarname(oldLabel).equals(field.getVarname()))
				sheet.updateField("varname", StructureField.formatVarname(field.getLabel()));

			preview.lightRefreshLeaf(previewKey);
		});

		sheet.addPropertyChangeListener("type", event -> {
			HaxeDataType type = field.getType();

			field.setDefaultValue(type.dataType.decode("", field.getOwner().getCtx()));
			sheet.change().field("defaultValue")._editor(type.dataType).change().finish();

			field.setType(type);
			defEditor.setFieldTypeForPreview(field, type);
			refreshFieldEditors();
			preview.refreshLeaf(previewKey);

			layoutContainer();
			revalidate();
			setSize(getPreferredSize());
		});

		sheet.addPropertyChangeListener("hint", event -> {
			String oldV = (String) event.getOldValue();
			String newV = (String) event.getNewValue();
			if(oldV.isEmpty() || newV.isEmpty())
				preview.refreshLeaf(previewKey);
			else
				preview.lightRefreshLeaf(previewKey);
		});

		refreshFieldEditors();
	}

	// === Methods for DataType extra property appliers.

	@Override
	public void setPreviewSheet(PropertiesSheet sheet, DefaultLeaf key)
	{
		super.setPreviewSheet(sheet, key);
		refreshFieldEditors();
	}

	private void refreshFieldEditors()
	{
		if(editorSheet != null)
			clearSheetExtension("editor");
		editorSheet = createSheetExtension(field.getEditorProperties(), "editor");
		editorSheet.addPropertyChangeListener(event -> refreshGeneratedEditor());
		field.getType().applyToFieldPanel(StructureFieldPanel.this);
	}

	protected void refreshGeneratedEditor()
	{
		sheet.change()
			.field("defaultValue")._editor(field.getType().dataType).loadProps(field.getEditorProperties()).change()
			.finish();
		if(preview != null)
			preview.refreshLeaf(previewKey);
	}

	public StructureField getField()
	{
		return field;
	}

	public DataTypeProperties getExtras()
	{
		return field.getEditorProperties();
	}

	public PropertiesSheet getPreview()
	{
		return preview;
	}

	public DefaultLeaf getPreviewKey()
	{
		return previewKey;
	}

	public PropertiesSheetSupport getSheet()
	{
		return sheet;
	}

	public PropertiesSheetSupport getEditorSheet()
	{
		return editorSheet;
	}

	// ===

	public void revert()
	{
		super.revertChanges();
	}
}

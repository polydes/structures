package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.elements.StructureCondition;

import stencyl.app.comp.datatypes.string.SingleLineStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;

public class StructureConditionPanel extends StructureObjectPanel
{
	public StructureConditionPanel(final StructureCondition condition, PropertiesSheetStyle style)
	{
		super(style, condition);

		sheet.build()

			.field("text").label("Condition")
			._editor(SingleLineStringEditor.BUILDER)
			.add().onUpdate(() -> {
				previewKey.setName(condition.getText());
				preview.lightRefreshLeaf(previewKey);
				preview.refreshVisibleComponents();
			})

			.finish();
	}
}
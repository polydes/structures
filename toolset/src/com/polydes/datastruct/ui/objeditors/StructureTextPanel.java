package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.elements.StructureText;

import stencyl.app.comp.datatypes.string.ExpandingStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.datatypes.Types;

public class StructureTextPanel extends StructureObjectPanel
{
	public StructureTextPanel(final StructureText text, PropertiesSheetStyle style)
	{
		super(style, text);

		sheet.build()

			.field("label")._editor(Types._String).add().onUpdate(() -> {
				previewKey.setName(text.getLabel());
				preview.lightRefreshLeaf(previewKey);
			})

			.field("text")._editor(ExpandingStringEditor.BUILDER).add().onUpdate(() -> {
				preview.lightRefreshLeaf(previewKey);
			})

			.finish();
	}
}

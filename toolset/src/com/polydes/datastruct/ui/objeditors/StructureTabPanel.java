package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.elements.StructureTab;

import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.datatypes.Types;

public class StructureTabPanel extends StructureObjectPanel
{
	public StructureTabPanel(final StructureTab tab, PropertiesSheetStyle style)
	{
		super(style, tab);

		sheet.build()

			.field("label")._editor(Types._String).add().onUpdate(() -> {
				previewKey.setName(tab.getLabel());
				preview.lightRefreshLeaf(previewKey);
			})

			.finish();
	}
}

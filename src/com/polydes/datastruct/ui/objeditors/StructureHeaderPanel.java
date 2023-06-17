package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.elements.StructureHeader;

import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.datatypes.Types;

public class StructureHeaderPanel extends StructureObjectPanel
{
	public StructureHeaderPanel(final StructureHeader header, PropertiesSheetStyle style)
	{
		super(style, header);

		sheet.build()

			.field("label")._editor(Types._String).add().onUpdate(() -> {
				previewKey.setName(header.getLabel());
				preview.lightRefreshLeaf(previewKey);
			})

			.finish();
	}
}

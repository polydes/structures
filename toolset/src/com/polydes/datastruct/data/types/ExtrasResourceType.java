package com.polydes.datastruct.data.types;

import com.polydes.datastruct.data.core.ExtrasResource;

import stencyl.app.comp.Prompt;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.PropertyKey;
import stencyl.core.io.FileFilter;

import java.nio.file.Path;

import static stencyl.core.lib.ProjectLocations.EXTRA_DATA;

public class ExtrasResourceType extends DataType<ExtrasResource>
{
	public ExtrasResourceType()
	{
		super(ExtrasResource.class, "extra");
	}

	public static final PropertyKey<ResourceType> RESOURCE_TYPE = new PropertyKey<>("resourceType");

	@Override
	public ExtrasResource decode(String s, DataContext ctx)
	{
		ExtrasResource r = new ExtrasResource();
		r.file = ctx.getProject().getPath(EXTRA_DATA, s);
		return r;
	}

	@Override
	public String encode(ExtrasResource i, DataContext ctx)
	{
		if(i == null)
			return "";
		
		Path root = ctx.getProject().getPath(EXTRA_DATA);
		Path full = i.file;
		
		return root.relativize(full).toString();
	}

	@Override
	public ExtrasResource copy(ExtrasResource t)
	{
		return t;
	}

	public enum ResourceType
	{
		ANY(null),
		IMAGE(Prompt.ExtensionFilter.PNG_FILTER);

		final FileFilter filter;

		ResourceType(FileFilter filter)
		{
			this.filter = filter;
		}

		public FileFilter getFilter()
		{
			return filter;
		}
	}
}
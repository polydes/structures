package com.polydes.datastruct.data.types;

import com.polydes.datastruct.data.core.ExtrasResource;

import stencyl.app.comp.Prompt;
import stencyl.core.api.datatypes.DataContext;
import stencyl.core.api.datatypes.DataType;
import stencyl.core.api.datatypes.properties.PropertyKey;
import stencyl.core.io.FileFilter;

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
		r.file = ctx.getProject().getFile("extras", s);
		return r;
	}

	@Override
	public String encode(ExtrasResource i, DataContext ctx)
	{
		if(i == null)
			return "";
		
		String root = ctx.getProject().getLocation("extras");
		String full = i.file.getAbsolutePath();
		
		return full.substring(root.length() + 1);
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

		FileFilter filter;

		private ResourceType(FileFilter filter)
		{
			this.filter = filter;
		}

		public FileFilter getFilter()
		{
			return filter;
		}
	}
}
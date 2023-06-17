package com.polydes.datastruct.ui.page;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureFolder;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.list.ListUtils;
import com.polydes.datastruct.ui.objeditors.StructureEditor;

import stencyl.app.api.nodes.DefaultNodeCreator;
import stencyl.app.api.nodes.HierarchyModelInterface;
import stencyl.app.api.nodes.NodeIconProvider;
import stencyl.app.api.nodes.NodeViewProvider;
import stencyl.app.comp.Prompt;
import stencyl.app.comp.filelist.BranchPage;
import stencyl.app.comp.filelist.TreePage;
import stencyl.core.api.Choice;
import stencyl.core.api.pnodes.Branch;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;
import stencyl.sw.app.main.SW;

public class StructurePage
{
	private static StructurePage _instance;

	private HierarchyModelInterface<DefaultLeaf,DefaultBranch> modelInterface;
	private HierarchyModel<DefaultLeaf,DefaultBranch> model;
	private TreePage<DefaultLeaf, DefaultBranch> treePage;
	private JComponent sidebar;
	private Boolean listEditEnabled;

	private final Map<Folder, BranchPage<?,?>> folderPages = new IdentityHashMap<>();
	private final Map<Structure, StructureEditor> editors = new IdentityHashMap<>();
	
	public StructurePage(HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		this.model = model;
		modelInterface = new HierarchyModelInterface<>(model);
		modelInterface.setNodeCreator(new StructureNodeCreater());
		
		StructureNodeUiProvider uiProvider = new StructureNodeUiProvider();
		
		treePage = new TreePage<>(modelInterface);
		treePage.setNodeIconProvider(uiProvider);
		treePage.setNodeViewProvider(uiProvider);
		
		setListEditEnabled(true);
		model.setUniqueLeafNames(true);
		
		treePage.getTree().enablePropertiesButton();
		
		sidebar = ListUtils.addHeader(treePage.getTree(), "Data");
	}

	public TreePage<DefaultLeaf, DefaultBranch> getTreePage()
	{
		return treePage;
	}

	private class StructureNodeCreater extends DefaultNodeCreator<DefaultLeaf,DefaultBranch>
	{
		//For our purposes here, the object these folders point to is a type limiter.

		private CreatableNodeInfo cneForDef(StructureDefinition def)
		{
			return new CreatableNodeInfo(def.getName(), def, new ImageIcon(def.getIconImg()));
		}
		
		@Override
		public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch branchNode)
		{
			StructureFolder parent = (StructureFolder) modelInterface.getCreationParentFolder(treePage.getTree().getSelectionState());

			ArrayList<CreatableNodeInfo> items = new ArrayList<>();
			if(parent.childType != null)
				items.add(cneForDef(parent.childType));
			else
				for(StructureDefinition def : DataStructuresExtension.get().getStructureDefinitions().values())
					items.add(cneForDef(def));
			return items;
		}

		@Override
		public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName, DefaultBranch newNodeFolder, int insertPosition)
		{
			if(selected.name.equals("Folder"))
				return new StructureFolder(nodeName);

			int id = Structures.newID();
			StructureDefinition type = (StructureDefinition) selected.data;
			Structure toReturn = new Structure(id, nodeName, type);
			toReturn.loadDefaults();
			Structures.structures.get(type).add(toReturn);
			Structures.structuresByID.put(id, toReturn);
			toReturn.dref.setDirty(true);
			return toReturn.dref;
		}

		@Override
		public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
		{
			return null;
		}

		@Override
		public boolean attemptRemove(List<DefaultLeaf> toRemove)
		{
			int numStructuresToRemove = 0;
			for(DefaultLeaf item : toRemove)
				if(!(item instanceof Branch))
					++numStructuresToRemove;

			String plural = (numStructuresToRemove > 1 ? "s" : "");

			Choice result =
				Prompt.showYesCancelPrompt(
					"Remove Selected Structure" + plural,
					"Are you sure you want to remove " + numStructuresToRemove +  " structure" + plural + "?",
					"Remove", "Cancel"
				);

			return result == Choice.YES;
		}

		@Override
		public void nodeRemoved(DefaultLeaf toRemove)
		{
			if(toRemove.getUserData() instanceof Structure s)
			{
				Structures.structures.get(s.getTemplate()).remove(s);
				Structures.structuresByID.remove(s.getID());
				if(editors.remove(s) instanceof StructureEditor editor)
					editor.dispose();
				s.dispose();
			}
		}

		@Override
		public void editNode(DefaultLeaf toEdit)
		{
			if(!(toEdit instanceof StructureFolder))
				return;

			EditFolderDialog dg = new EditFolderDialog(SW.get());
			dg.setFolder((StructureFolder) toEdit);
			dg.dispose();
		}
	}
	
	private class StructureNodeUiProvider implements
		NodeIconProvider<DefaultLeaf>,
		NodeViewProvider<DefaultLeaf,DefaultBranch>
	{
		@Override
		public ImageIcon getIcon(DefaultLeaf object)
		{
			if(object instanceof Folder)
			{
				return UIConsts.folderIcon;
			}
			if(object.getUserData() instanceof Structure s)
			{
				if(s.getTemplate().iconSource instanceof String iconSource)
					return s.getTemplate().getField(iconSource).getIcon(s.getPropByName(iconSource));
				else
					return new ImageIcon(s.getTemplate().getIconImg());
			}

			return null;
		}

		@Override
		public JPanel getView(DefaultLeaf object)
		{
			if(object.getUserData() instanceof Structure structure)
				return editors.computeIfAbsent(structure, StructureEditor::new);
			return null;
		}

		@Override
		public void disposeView(DefaultLeaf object)
		{
			if(object.getUserData() instanceof Structure structure)
				if(editors.remove(structure) instanceof StructureEditor structureEditor)
					structureEditor.dispose();
		}
	}
	
	public void updateEditorsForDefinition(StructureDefinition sdef)
	{
		var editorsIter = editors.values().iterator();
		while(editorsIter.hasNext())
		{
			StructureEditor editor = editorsIter.next();
			if(editor.structure.getTemplate() == sdef)
			{
				editor.dispose();
				editorsIter.remove();
			}
		}
	}
	
	public JComponent getSidebar()
	{
		return sidebar;
	}
	
	public static StructurePage get()
	{
		if (_instance == null)
			_instance = new StructurePage(Structures.model);
		
		return _instance;
	}
	
	public void setListEditEnabled(boolean value)
	{
		if(listEditEnabled == null || listEditEnabled != value)
		{
			listEditEnabled = value;
			if(listEditEnabled)
			{
				treePage.getTree().setListEditEnabled(true);
			}
			else
			{
				treePage.getTree().setListEditEnabled(false);
			}
		}
	}
	
	public static void disposeInstance()
	{
		if(_instance != null)
		{
			_instance.dispose();
			_instance = null;
		}
	}
	
	public void dispose()
	{
		for(BranchPage<?,?> branchPage : new ArrayList<>(folderPages.values()))
			branchPage.dispose();
		for(StructureEditor structureEditor : new ArrayList<>(editors.values()))
			structureEditor.dispose();
		folderPages.clear();
		editors.clear();
		
		treePage.dispose();
		sidebar.removeAll();
		sidebar = null;
	}
}

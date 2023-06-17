package com.polydes.datastruct.ui.objeditors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;

import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureTable;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.structure.elements.StructureHeader;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.structure.elements.StructureTabset;
import com.polydes.datastruct.data.structure.elements.StructureText;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.list.ListUtils;
import com.polydes.datastruct.ui.page.StructurePage;

import stencyl.app.api.nodes.DefaultNodeCreator;
import stencyl.app.api.nodes.HierarchyModelInterface;
import stencyl.app.api.nodes.select.NodeSelection;
import stencyl.app.api.nodes.select.SelectionType;
import stencyl.app.comp.darktree.DarkTree;
import stencyl.core.api.datatypes.properties.DataTypeProperties;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;
import stencyl.core.api.pnodes.NodeUtils;

public class StructureDefinitionEditor extends JPanel
{
	public StructureDefinition def;
	public HierarchyModelInterface<DefaultLeaf, DefaultBranch> modelInterface;
	public HierarchyModel<DefaultLeaf, DefaultBranch> model;
	public DarkTree<DefaultLeaf,DefaultBranch> tree;
	public JComponent treeView;
	public Structure preview;
	private StructureEditor previewEditor;

	private boolean savedDefinitionDirtyState;
	
	private int getPosAvoidingTabsetParent(DefaultBranch newNodeFolder)
	{
		int insertPosition;
		
		NodeSelection<DefaultLeaf,DefaultBranch> selection = tree.getSelectionState();
		
		if(selection.getType() == SelectionType.FOLDERS && !(newNodeFolder.getUserData() instanceof StructureTabset))
			insertPosition = newNodeFolder.getItems().size();
		else
			insertPosition = NodeUtils.getIndex(selection.lastNode()) + 1;
		return insertPosition;
	}
	
	private final Action createFieldAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = modelInterface.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			modelInterface.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureField.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createHeaderAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = modelInterface.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			modelInterface.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureHeader.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createTextAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = modelInterface.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			modelInterface.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureText.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createTabAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = modelInterface.getCreationParentFolder(tree.getSelectionState());
			if(newNodeFolder.getUserData() instanceof StructureTabset)
				modelInterface.createNewItem(SDETypes.asCNInfo.get(StructureTab.class), tree.getSelectionState());
			else if(newNodeFolder.getUserData() instanceof StructureTab && !(newNodeFolder.getUserData() instanceof StructureTable))
			{
				DefaultBranch tabset = newNodeFolder.getParent();
				int insertPosition = tabset.indexOfItem(newNodeFolder) + 1;
				modelInterface.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureTab.class), tabset, insertPosition);
			}
			else
				modelInterface.createNewItem(SDETypes.asCNInfo.get(StructureTabset.class), tree.getSelectionState());
		}
	};
	
	private final Action createConditionAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = modelInterface.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			modelInterface.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureCondition.class), newNodeFolder, insertPosition);
		}
	};
	
	private static ImageIcon getSdeIcon(DefaultLeaf leaf)
	{
		SDEType<?> type = SDETypes.fromClass((Class) leaf.getUserData().getClass());
		if(type == null) return null;
		return type.icon;
	}
	
	public StructureDefinitionEditor(final StructureDefinition def)
	{
		super(new BorderLayout());
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		
		this.def = def;
		savedDefinitionDirtyState = def.dref.isDirty();
		
		model = new HierarchyModel<>(def.guiRoot, DefaultLeaf.class, DefaultBranch.class);
		model.setUniqueLeafNames(false);
		
		modelInterface = new HierarchyModelInterface<>(model);
		
		tree = new DarkTree<>(modelInterface);
		tree.setIconProvider(StructureDefinitionEditor::getSdeIcon);
		tree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setListEditEnabled(true);
		modelInterface.setNodeCreator(new DefaultNodeCreator<>()
		{
			@Override
			public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch creationBranch)
			{
				if(tree.getSelectionState().isEmpty())
					return null;
				
				DefaultBranch parent = modelInterface.getCreationParentFolder(tree.getSelectionState());
				SDE o = (SDE) parent.getUserData();
				SDEType<?> type = SDETypes.fromClass(o.getClass());
				
				ArrayList<CreatableNodeInfo> toReturn = new ArrayList<>();
				type.childTypes.forEach((listedType) -> toReturn.add(SDETypes.asCNInfo.get(listedType)));
				return toReturn;
			}
			
			@Override
			public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName, DefaultBranch newNodeFolder, int insertPosition)
			{
				@SuppressWarnings("unchecked")
				Class<? extends SDE> cls = (Class<? extends SDE>) selected.data;
				
				SDEType<?> type = SDETypes.fromClass(cls);
				
				DefaultLeaf leaf;
				if(type.isBranchNode)
					leaf = new Folder(nodeName, type.create(def, StructureDefinitionEditor.this, nodeName));
				else
					leaf = new DefaultLeaf(nodeName, type.create(def, StructureDefinitionEditor.this, nodeName));
				leaf.setDirty(true);
				return leaf;
			}
			
			NodeAction<DefaultLeaf> setAsIcon = new NodeAction<DefaultLeaf>("Set as Icon", null, leaf -> {
				StructureField field = (StructureField) leaf.getUserData();
				def.iconSource = field.getVarname();
			});
			
			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				ArrayList<NodeAction<DefaultLeaf>> actions = new ArrayList<>();
				if(targets.length == 1)
				{
					Object data = targets[0].getUserData();
					if(data instanceof StructureField)
					{
						StructureField field = (StructureField) data;
						if(field.getType().isIconProvider())
							actions.add(setAsIcon);
					}
				}
				return actions;
			}
			
			@Override
			public boolean attemptRemove(List<DefaultLeaf> toRemove)
			{
				return true;
			}
			
			@Override
			public void nodeRemoved(DefaultLeaf toRemove)
			{
				if(toRemove instanceof DefaultBranch)
				{
					for(DefaultLeaf item : ((DefaultBranch) toRemove).getItems())
						nodeRemoved(item);
				}
				else 
				{
					if(toRemove.getUserData() instanceof StructureField)
						removeField((StructureField) toRemove.getUserData(), preview);
				}
			}
		});
		
		tree.setNamingEditingAllowed(false);
		
		//tree.expandLevel(0);
		
		treeView = ListUtils.addHeader(tree, "View");
		
		installActions(this);
		installActions(tree);

		preview = new Structure(-1, def.getName(), def);
		preview.loadDefaults();
		previewEditor = new StructureEditor(preview, model);
		add(previewEditor);
	}
	
	private void installActions(JComponent c)
	{
		installAction(c, "F", "createField", createFieldAction);
		installAction(c, "H", "createHeader", createHeaderAction);
		installAction(c, "T", "createTab", createTabAction);
		installAction(c, "K", "createCondition", createConditionAction);
		installAction(c, "D", "createText", createTextAction);
	}
	
	private void uninstallActions(JComponent c)
	{
		uninstallAction(c, "F", "createField");
		uninstallAction(c, "H", "createHeader");
		uninstallAction(c, "T", "createTab");
		uninstallAction(c, "K", "createCondition");
		uninstallAction(c, "D", "createText");
	}
	
	private void installAction(JComponent c, String key, String name, Action action)
	{
		c.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl " + key), name);
		c.getActionMap().put(name, action);
	}
	
	private void uninstallAction(JComponent c, String key, String name)
	{
		c.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl " + key), null);
		c.getActionMap().put(name, null);
	}
	
	public Structure getPreview()
	{
		return preview;
	}

	public StructureEditor getPreviewEditor()
	{
		return previewEditor;
	}
	
	public void disposePreview()
	{
		if(previewEditor != null)
			previewEditor.dispose();
		if(preview != null)
			preview.dispose();
		preview = null;
	}

	//=== For runtime updating

	private ArrayList<StructureField> addedFields;
	private ArrayList<StructureField> removedFields;
	private HashMap<StructureField, TypeUpdate> typeUpdates;
	private HashMap<StructureField, NameUpdate> nameUpdates;

	private record NameUpdate(String originalName, String newName) {}
	
	private record TypeUpdate(HaxeDataType originalType, DataTypeProperties originalOptArgs, HaxeDataType newType, DataTypeProperties newOptArgs)
	{
	}

	public void addField(StructureField f, Structure s)
	{
		if(addedFields == null)
			addedFields = new ArrayList<StructureField>();
		addedFields.add(f);

		def.addField(f);
		s.clearProperty(f);
	}

	public void removeField(StructureField f, Structure s)
	{
		if(removedFields == null)
			removedFields = new ArrayList<StructureField>();
		removedFields.add(f);

		s.clearProperty(f);
		def.removeField(f);
	}

	public void setFieldTypeForPreview(StructureField f, HaxeDataType type)
	{
		if(typeUpdates == null)
			typeUpdates = new HashMap<StructureField, TypeUpdate>();

		if(!typeUpdates.containsKey(f))
			typeUpdates.put(f,
				new TypeUpdate(
					f.getType(),
					f.getEditorProperties(),
					null,
					null
				)
			);

		TypeUpdate update = typeUpdates.get(f);
		typeUpdates.put(f,
			new TypeUpdate(
				update.originalType,
				update.originalOptArgs,
				type,
				type.loadExtras(new ExtrasMap(def.getCtx()))
			)
		);

		preview.clearProperty(f);
		f.setEditorProperties(update.newOptArgs);
	}

	public void update()
	{
		updateTypes();
		refreshFields(true);
		refreshEditors();
	}

	public void updateTypes()
	{
		if(typeUpdates != null)
		{
			for(StructureField field : typeUpdates.keySet())
			{
				TypeUpdate typeUpdate = typeUpdates.get(field);
				if(typeUpdate.originalType == typeUpdate.newType)
					continue;
				setFieldType(field, typeUpdate.newType);
			}
			typeUpdates.clear();
			typeUpdates = null;
		}
	}

	public void revertTypes()
	{
		if(typeUpdates != null)
		{
			for(StructureField field : typeUpdates.keySet())
			{
				field.setEditorProperties(typeUpdates.get(field).originalOptArgs);
				field.setType(typeUpdates.get(field).originalType);
				setFieldType(field, typeUpdates.get(field).originalType);
			}
			typeUpdates.clear();
			typeUpdates = null;
		}
	}

	public void setFieldType(StructureField f, HaxeDataType type)
	{
		for(Structure s : Structure.getAllOfType(def))
			s.clearProperty(f);
		if(f.getType() != type)
		{
			f.setType(type);
			setFieldType(f, type);
			f.setEditorProperties(type.loadExtras(new ExtrasMap(def.getCtx())));
		}
	}

	public void refreshFields(boolean commit)
	{
		if(commit)
		{
			//if(removeField != null) ... was in here before. Why?
			//This does nothing now.
		}
		else //revert
		{
			if(addedFields != null)
			{
				for(StructureField f : addedFields)
					def.removeField(f);
				addedFields.clear();
				addedFields = null;
			}
			if(removedFields != null)
			{
				for(StructureField f : removedFields)
					def.addField(f);
				removedFields.clear();
				removedFields = null;
			}
		}
	}

	private void revertNames()
	{
		if(nameUpdates != null)
		{
			for(StructureField f : nameUpdates.keySet())
				setFieldName(f, nameUpdates.get(f).originalName);
			nameUpdates.clear();
			nameUpdates = null;
		}
	}

	public void refreshEditors()
	{
		StructurePage.get().updateEditorsForDefinition(def);
	}

	public void setFieldName(StructureField f, String name)
	{
		def.setFieldName(f, name);
	}
	
	public void dispose()
	{
		removeAll();
		
		uninstallActions(this);
		uninstallActions(tree);
		
		disposePreview();
		model.dispose();
		model = null;
		def = null;
		tree.dispose();
		tree = null;
		treeView = null;
	}

	public void revertChanges()
	{
		revertTypes();
		revertNames();
		refreshFields(false);

		if(!savedDefinitionDirtyState)
			def.dref.setDirty(false);
	}
}

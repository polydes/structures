package com.polydes.datastruct.ui.page;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;

import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.Prefs;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.ui.StructureDefinitionIconProvider;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.list.ListUtils;
import com.polydes.datastruct.ui.objeditors.SdePanelProviders;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.utils.SnappingDialog;

import stencyl.app.api.nodes.DefaultNodeCreator;
import stencyl.app.api.nodes.HierarchyModelInterface;
import stencyl.app.api.nodes.NodeCreator.CreatableNodeInfo;
import stencyl.app.api.nodes.select.NodeSelection;
import stencyl.app.api.nodes.select.NodeSelectionEvent;
import stencyl.app.api.nodes.select.NodeSelectionListener;
import stencyl.app.api.nodes.select.SelectionType;
import stencyl.app.comp.MiniSplitPane;
import stencyl.app.comp.Prompt;
import stencyl.app.comp.StatusBar;
import stencyl.app.comp.UI;
import stencyl.app.comp.darktree.DarkTree;
import stencyl.core.api.Choice;
import stencyl.core.api.pnodes.DefaultBranch;
import stencyl.core.api.pnodes.DefaultLeaf;
import stencyl.core.api.pnodes.HierarchyModel;

public class StructureDefinitionPage extends JPanel
{
	private static StructureDefinitionPage _instance;

	public MiniSplitPane splitPane;
	private JPanel emptySidebarBottom;

	private HierarchyModel<DefaultLeaf, DefaultBranch> definitionsfm;
	private DarkTree<DefaultLeaf,DefaultBranch> definitionTree;

	private JComponent definitionTreeView;

	protected JScrollPane scroller;
	protected JPanel page;

	private StructureDefinitionEditor editor;
	private Map<StructureDefinition, StructureDefinitionEditor> editors = new IdentityHashMap<>();
	private Map<SDE, StructureObjectPanel> sdeEditors = new IdentityHashMap<>();

	private NodeSelectionListener<DefaultLeaf,DefaultBranch> definitionStateListener = new NodeSelectionListener<>()
	{
		@Override
		public void selectionChanged(NodeSelectionEvent<DefaultLeaf, DefaultBranch> e)
		{
			NodeSelection<DefaultLeaf, DefaultBranch> selection = definitionTree.getSelectionState();

			page.removeAll();

			int dl = splitPane.getDividerLocation();

			if(selection.getType() == SelectionType.FOLDERS)
			{
				revalidate();
				repaint();
				splitPane.setBottomComponent(emptySidebarBottom);
				splitPane.setDividerLocation(dl);
				return;
			}
			DefaultLeaf di = selection.firstNode();
			StructureDefinition toEdit = (StructureDefinition) di.getUserData();
			StructureDefinitionEditor oldEditor = editor;
			editor = editors.computeIfAbsent(toEdit, StructureDefinitionEditor::new);
			editor.setAlignmentX(LEFT_ALIGNMENT);

			page.add(editor, BorderLayout.CENTER);

			if(oldEditor != null)
				oldEditor.tree.getSelectionState().removeSelectionListener(editorStateListener);
			editor.tree.getSelectionState().addSelectionListener(editorStateListener);

			splitPane.setTopComponent(definitionTreeView);
			splitPane.setBottomComponent(editor.treeView);
			splitPane.setDividerLocation(dl);

			editor.tree.getTree().setSelectionPath(editor.tree.getRootPath());
			editorStateListener.selectionChanged(null);

			revalidate();
			repaint();
		}
	};

	private NodeSelectionListener<DefaultLeaf,DefaultBranch> editorStateListener = new NodeSelectionListener<>()
	{
		@Override
		public void selectionChanged(NodeSelectionEvent<DefaultLeaf, DefaultBranch> e)
		{
			NodeSelection<DefaultLeaf, DefaultBranch> selection = editor.tree.getSelectionState();

			PropertiesWindow propsWindow = StructureDefinitionsWindow.get().getPropsWindow();

			DefaultLeaf di = selection.get(0);
			Object selected = (di == null) ? null : di.getUserData();
			if(selected instanceof SDE sde)
			{
				if(!sdeEditors.containsKey(sde))
				{
					StructureObjectPanel panel = SdePanelProviders.getPanel(sde, editor);
					if(panel != null)
					{
						sdeEditors.put(sde, panel);
					}
				}
				StructureObjectPanel sdeEditor = sdeEditors.get(sde);

				if(sdeEditor != null)
				{
					sdeEditor.setPreviewSheet(editor.getPreviewEditor().properties, di);

					propsWindow.setObject(sdeEditor);
					if(!propsWindow.isVisible())
					{
						propsWindow.setVisible(true);
						propsWindow.addComponentListener(propsWindowAdapter);
					}
				}
				else if(propsWindow.isVisible())
				{
					propsWindow.removeComponentListener(propsWindowAdapter);
					propsWindow.setObject(null);
					propsWindow.setVisible(false);
				}

				if(editor.def != null)
				{
					editor.getPreviewEditor().highlightElement(di);
				}
			}
			else if(propsWindow.isVisible())
			{
				propsWindow.removeComponentListener(propsWindowAdapter);
				propsWindow.setObject(null);
				propsWindow.setVisible(false);
			}

			revalidate();
			repaint();
		}
	};

	private ComponentAdapter propsWindowAdapter = new ComponentAdapter()
	{
		@Override
		public void componentHidden(ComponentEvent e)
		{
			StructureDefinitionsWindow.get().getPropsWindow().removeComponentListener(this);
			if(editor != null)
				editor.tree.getSelectionState().clear();
		}
	};

	public static StructureDefinitionPage get()
	{
		if (_instance == null)
			_instance = new StructureDefinitionPage();

		return _instance;
	}

	public StructureDefinitionPage()
	{
		super(new BorderLayout());

		Folder root = DataStructuresExtension.get().getStructureDefinitions().root;
		definitionsfm = new HierarchyModel<>(root, DefaultLeaf.class, DefaultBranch.class);
		HierarchyModelInterface<DefaultLeaf,DefaultBranch> definitionsFmUi = new HierarchyModelInterface<>(definitionsfm);
		definitionTree = new DarkTree<>(definitionsFmUi);
		definitionTree.setIconProvider(new StructureDefinitionIconProvider());
		definitionTree.setNamingEditingAllowed(false);
		definitionTree.expand((Folder) DataStructuresExtension.get().getStructureDefinitions().root.getItemByName("My Structures"));

		page = new JPanel(new BorderLayout());
		page.setBackground(UIConsts.TEXT_EDITOR_COLOR);
		scroller = UI.createScrollPane(page);
		scroller.setBackground(null);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		splitPane = new MiniSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		definitionTree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		add(scroller, BorderLayout.CENTER);

		definitionTree.setListEditEnabled(true);
		definitionTree.enablePropertiesButton();
		definitionsfm.setUniqueLeafNames(true);
		definitionTree.getSelectionState().addSelectionListener(definitionStateListener);

		definitionsFmUi.setNodeCreator(new DefaultNodeCreator<DefaultLeaf,DefaultBranch>()
		{
			@Override
			public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch creationBranch)
			{
				return createNodeList;
			}

			@Override
			public DefaultLeaf createNode(CreatableNodeInfo item, String nodeName, DefaultBranch newNodeFolder, int insertPosition)
			{
				DefaultLeaf newLeaf;
				if(item.name.equals("Folder"))
					newLeaf = new Folder(nodeName);
				else
				{
					CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog(DataStructuresExtension.get().getProject());
					dg.setNodeName(nodeName);
					StructureDefinition toCreate = dg.newDef;
					dg.dispose();

					if(toCreate != null)
					{
						DataStructuresExtension.get().getStructureDefinitions().registerItem(toCreate);
						newLeaf = toCreate.dref;
					}
					else
					{
						newLeaf = null;
					}
				}
				
				if(newLeaf != null)
					newLeaf.setDirty(true);
				return newLeaf;
			}

			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				return null;
			}

			@Override
			public void editNode(DefaultLeaf toEdit)
			{
				if(toEdit instanceof DefaultBranch)
					return;

				if(!(toEdit.getUserData() instanceof StructureDefinition))
					return;

				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog(DataStructuresExtension.get().getProject());
				dg.setDefinition((StructureDefinition) toEdit.getUserData());
				dg.dispose();
				definitionTree.repaint();
			}

			@Override
			public boolean attemptRemove(List<DefaultLeaf> toRemove)
			{
				if(toRemove.size() > 0 && toRemove.get(0).getUserData() instanceof StructureDefinition)
				{
					StructureDefinition def = (StructureDefinition) toRemove.get(0).getUserData();
					Choice result =
						Prompt.showYesCancelPrompt(
							"Remove Structure Definition",
							"Are you sure you want to remove this structure definition? (Will delete " + Structures.structures.get(def).size() + " structures)",
							"Remove", "Cancel"
						);

					return result == Choice.YES;
				}
				return false;
			}

			@Override
			public void nodeRemoved(DefaultLeaf toRemove)
			{
				if(toRemove.getUserData() instanceof StructureDefinition def)
				{
					StructureDefinitionEditor editor = editors.remove(def);
					if(editor != null)
						editor.dispose();
					DataStructuresExtension.get().getStructureDefinitions().unregisterItem(def);
				}
			}
		});

		emptySidebarBottom = new JPanel(new BorderLayout());
		emptySidebarBottom.setBackground(UIConsts.SIDEBAR_COLOR);

		int initDivLoc = Prefs.DEFPAGE_SIDEWIDTH;

		splitPane.setTopComponent(definitionTreeView = ListUtils.addHeader(definitionTree, "Object"));
		splitPane.setBottomComponent(emptySidebarBottom);
		splitPane.setDividerLocation(initDivLoc);

		definitionTree.forceRerender();

		PropertiesWindow propsWindow = StructureDefinitionsWindow.get().getPropsWindow();
		propsWindow.snapToComponent(scroller.getViewport(), SnappingDialog.TOP_RIGHT);
		propsWindow.setVisible(false);
	}

	public void selectNone()
	{
		if(editor != null)
			editor.tree.getSelectionState().clear();
		definitionTree.getSelectionState().clear();
	}

	public void selectDefinition(StructureDefinition def)
	{
		if(editor != null)
			editor.tree.getSelectionState().clear();
		definitionTree.getSelectionState().set(def.dref);
	}

	private static final ArrayList<CreatableNodeInfo> createNodeList = new ArrayList<CreatableNodeInfo>();

	static
	{
		createNodeList.add(new CreatableNodeInfo("Structure", null, null));
	}

	public JComponent getSidebar()
	{
		return splitPane;
	}

	public static void dispose()
	{
		if(_instance != null)
		{
			_instance.definitionsfm.dispose();
			_instance.definitionTree.dispose();
		}
		_instance = null;
	}

	public void updateStructureDefinitionEditors()
	{
		for(StructureDefinitionEditor editor : editors.values())
			editor.update();
	}

	public void disposeStructureDefinitionEditors()
	{
		for(StructureDefinitionEditor editor : editors.values())
			editor.dispose();
		editors.clear();
	}
}

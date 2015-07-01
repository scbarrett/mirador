/* --------------------------------------------------------------------------+
   ModelTree.java - Provides a data model suitable for viewing as a tree.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Data are extracted from an Ecore model reconstructed from the modifications
   made to a replica of some common ancestor model.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import ca.dsrg.mirador.model.EcoreExtra.NameComparator;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 * Data model for viewing in the trees of the Element Matching dialog. User
 * objects at each node provide model element details.
 *
 * @since   v0.20 - Mar 15, 2010
 * @author  Stephen Barrett
 */
public class ModelTree {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Builds a data model suitable for viewing as a tree, from the <i>left</i> or
   * <i>right</i> log of changes made to a common ancestor design model.
   *
   * @param  model  Ecore model to extract data from.
   * @param  tree  GUI component to display extracted data.
   */
  public ModelTree(MiradorModel model, JTree tree) {
    model_ = model;
    tree_ = tree;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the Ecore model the tree's data are extracted from.
   *
   * @return  Ecore model the tree is based on.
   * @category  getter
   */
  public MiradorModel getMiradorModel() {
    return model_;
  }


  /**
   * Gives the tree GUI component.
   *
   * @return  Tree GUI component.
   * @category  getter
   */
  public JTree getTree() {
    return tree_;
  }


  /**
   * Gives the tree's data model.
   *
   * @return  Data model of the tree.
   * @category  getter
   */
  public DefaultTreeModel getTreeModel() {
    return tree_model_;
  }


  /**
   * Gives the path from the tree root to the specified node.
   *
   * @param  node  Tree node to obtain path to.
   * @return  Path to the specified tree node.
   */
  static public TreePath getTreePath(TreeNode node) {
    List<TreeNode> list = new ArrayList<TreeNode>();

    // Add all nodes to list
    while (node != null) {
      list.add(node);
      node = node.getParent();
    }
    Collections.reverse(list);

    // Convert array of nodes to TreePath
    return new TreePath(list.toArray());
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /*
   * @param  container  Object to be examined for children.
   * @param  parent  Tree node to which child nodes are to be added.
   */
  private void addAttributes(EcoreExtra container, ModelTreeNode parent) {
    // Arrange contained elements by copying into a collection sorted by name.
    SortedSet<EcoreExtra> extras =
        new TreeSet<EcoreExtra>(new NameComparator());

    for (EObject contained : container.getElement().eContents()) {
      if (!(contained instanceof ENamedElement))
        continue;

      EcoreExtra extra = model_.getExtra((ENamedElement) contained);
      if (extra != null && extra.getEcoreType() == EcoreType.ATTRIBUTE)
        extras.add(extra);
    }


    // Create node with design element's details, and add as child of caller.
    for (EcoreExtra extra : extras) {
      ModelTreeNode child = new ModelTreeNode(extra, true);
      parent.add(child);
      // Provide each child with the path to its node in the tree.
      extra.setTreePath(getTreePath(child));
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  container  Object to be examined for children.
   * @param  parent  Tree node to which child nodes are to be added.
   */
  private void addClasses(EcoreExtra container, ModelTreeNode parent) {
    // Arrange contained elements by copying into a collection sorted by name.
    SortedSet<EcoreExtra> extras =
        new TreeSet<EcoreExtra>(new NameComparator());

    for (EObject contained : container.getElement().eContents()) {
      if (!(contained instanceof ENamedElement))
        continue;

      EcoreExtra extra = model_.getExtra((ENamedElement) contained);
      if (extra != null && extra.getEcoreType() == EcoreType.CLASS)
        extras.add(extra);
    }


    // Create node with design element's details, and add as child of caller.
    for (EcoreExtra extra : extras) {
      ModelTreeNode child = new ModelTreeNode(extra, true);
      parent.add(child);
      // Provide each child with the path to its node in the tree.
      extra.setTreePath(getTreePath(child));

      // Recurse on possible child element types in desired order of appearance.
      addAttributes(extra, child);
      addOperations(extra, child);
      addReferences(extra, child);
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  container  Object to be examined for children.
   * @param  parent  Tree node to which child nodes are to be added.
   */
  private void addDataTypes(EcoreExtra container, ModelTreeNode parent) {
    // Arrange contained elements by copying into a collection sorted by name.
    SortedSet<EcoreExtra> extras =
        new TreeSet<EcoreExtra>(new NameComparator());

    for (EObject contained : container.getElement().eContents()) {
      if (!(contained instanceof ENamedElement))
        continue;

      EcoreExtra extra = model_.getExtra((ENamedElement) contained);
      if (extra != null && extra.getEcoreType() == EcoreType.DATATYPE)
        extras.add(extra);
    }


    // Create node with design element's details, and add as child of caller.
    for (EcoreExtra extra : extras) {
      ModelTreeNode child = new ModelTreeNode(extra, true);
      parent.add(child);
      // Provide each child with the path to its node in the tree.
      extra.setTreePath(getTreePath(child));
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  container  Object to be examined for children.
   * @param  parent  Tree node to which child nodes are to be added.
   */
  private void addOperations(EcoreExtra container, ModelTreeNode parent) {
    // Arrange contained elements by copying into a collection sorted by name.
    SortedSet<EcoreExtra> extras =
        new TreeSet<EcoreExtra>(new NameComparator());

    for (EObject contained : container.getElement().eContents()) {
      if (!(contained instanceof ENamedElement))
        continue;

      EcoreExtra extra = model_.getExtra((ENamedElement) contained);
      if (extra != null && extra.getEcoreType() == EcoreType.OPERATION)
        extras.add(extra);
    }


    // Create node with design element's details, and add as child of caller.
    for (EcoreExtra extra : extras) {
      ModelTreeNode child = new ModelTreeNode(extra, true);
      parent.add(child);
      // Provide each child with the path to its node in the tree.
      extra.setTreePath(getTreePath(child));

      // Recurse on possible child element types in desired order of appearance.
      addParameters(extra, child);
    }
  }


  /**
   * Recursively adds package design model elements to the tree model as child
   * nodes of the caller. Digs progressively downward into the design model to
   * add elements from the lower levels.
   *
   * @param  container  Object to be examined for children.
   * @param  parent  Tree node to which child nodes are to be added.
   */
  private void addPackages(EcoreExtra container, ModelTreeNode parent) {
    // Arrange contained elements by copying into a collection sorted by name.
    SortedSet<EcoreExtra> extras =
        new TreeSet<EcoreExtra>(new NameComparator());

    for (EObject contained : container.getElement().eContents()) {
      if (!(contained instanceof ENamedElement))
        continue;

      EcoreExtra extra = model_.getExtra((ENamedElement) contained);
      if (extra != null && extra.getEcoreType() == EcoreType.PACKAGE)
        extras.add(extra);
    }


    // Create node with design element's details, and add as child of caller.
    for (EcoreExtra extra : extras) {
      ModelTreeNode child = new ModelTreeNode(extra, true);
      parent.add(child);
      // Provide each child with the path to its node in the tree.
      extra.setTreePath(getTreePath(child));

      // Recurse on possible child element types in desired order of appearance.
      addPackages(extra, child);
      addClasses(extra, child);
      addDataTypes(extra, child);
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  container  Object to be examined for children.
   * @param  parent  Tree node to which child nodes are to be added.
   */
  private void addParameters(EcoreExtra container, ModelTreeNode parent) {
    // Arrange contained elements by copying into a collection sorted by name.
    SortedSet<EcoreExtra> extras =
        new TreeSet<EcoreExtra>(new NameComparator());

    for (EObject contained : container.getElement().eContents()) {
      if (!(contained instanceof ENamedElement))
        continue;

      EcoreExtra extra = model_.getExtra((ENamedElement) contained);
      if (extra != null && extra.getEcoreType() == EcoreType.PARAMETER)
        extras.add(extra);
    }


    // Create node with design element's details, and add as child of caller.
    for (EcoreExtra extra : extras) {
      ModelTreeNode child = new ModelTreeNode(extra, true);
      parent.add(child);
      // Provide each child with the path to its node in the tree.
      extra.setTreePath(getTreePath(child));
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  container  Object to be examined for children.
   * @param  parent  Tree node to which child nodes are to be added.
   */
  private void addReferences(EcoreExtra container, ModelTreeNode parent) {
    // Arrange contained elements by copying into a collection sorted by name.
    SortedSet<EcoreExtra> extras =
        new TreeSet<EcoreExtra>(new NameComparator());

    for (EObject contained : container.getElement().eContents()) {
      if (!(contained instanceof ENamedElement))
        continue;

      EcoreExtra extra = model_.getExtra((ENamedElement) contained);
      if (extra != null && extra.getEcoreType() == EcoreType.REFERENCE)
        extras.add(extra);
    }


    // Create node with design element's details, and add as child of caller.
    for (EcoreExtra extra : extras) {
      ModelTreeNode child = new ModelTreeNode(extra, true);
      parent.add(child);
      // Provide each child with the path to its node in the tree.
      extra.setTreePath(getTreePath(child));
    }
  }


  public void expandRows() { // TODO:3 Put in expand/collapse buttons.
    // Expand all nodes. Size changes on each iteration.
    for (int i = 0; i < tree_.getRowCount(); ++i)
      tree_.expandRow(i);
  }


  /**
   * Initiates the recursive building of the tree model from CoObRA change logs.
   * Each node of the tree contains a MElement user object which contains the
   * details of the model element being represented.
   */
  public void populate() {
    if (model_.getProject() == null || model_.getRoot() == null)
      return;

    // Make root node of tree view from model's topmost package.
    EPackage proj = model_.getProject();
    EcoreExtra proj_extra = model_.getExtra(proj);
    ModelTreeNode proj_node = new ModelTreeNode(proj_extra, true);
    TreePath proj_path = getTreePath(proj_node);
    proj_extra.setTreePath(proj_path);
    // Populate tree's working data from elements under the root node.
    tree_model_ = new DefaultTreeModel(proj_node, true);

    // Recurse on *all* element types in order to catch orphans.
    addPackages(proj_extra, proj_node);  // Recurse on model node's packages.


    for (Iterator<EObject> it = model_.getXmiModel().getContents().iterator();
        it.hasNext();) {
      ENamedElement elem = (ENamedElement) it.next();

      if (elem == proj)
        continue;

      EcoreExtra elem_extra = model_.getExtra(elem);
      ModelTreeNode elem_node = new ModelTreeNode(elem_extra, true);
      elem_extra.setTreePath(proj_path.pathByAddingChild(elem_node));
      proj_node.add(elem_node);

      addPackages(elem_extra, elem_node);
      addClasses(elem_extra, elem_node);
      addAttributes(elem_extra, elem_node);
      addOperations(elem_extra, elem_node);
      addParameters(elem_extra, elem_node);
      addReferences(elem_extra, elem_node);
      addDataTypes(elem_extra, elem_node);
    }
  }


  // Instance data ----------------------------------------------------------
  /** Ecore model the tree is based on. */
  private MiradorModel model_;

  /** GUI component for displaying data extracted from the Ecore model. */
  private JTree tree_;

  /** Data model of the tree for holding data extracted from the Ecore model. */
  private DefaultTreeModel tree_model_;
  // End instance data ------------------------------------------------------
}

/* --------------------------------------------------------------------------+
   ModelTreeCellRenderer.java - Customizes the node rendering of the Mirador model
     element trees.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Customization is for the purposes of providing node icons and tool tips.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import ca.dsrg.mirador.model.EcoreExtra;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;


/**
 * Customizes the node rendering of the Mirador model element trees for the
 * purposes of providing special node icons and tool tips.
 *
 * @since   v0.10 - Feb 15, 2010
 * @author  Stephen Barrett
 */
public class ModelTreeCellRenderer extends DefaultTreeCellRenderer {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public ModelTreeCellRenderer(boolean show_matches) {
    show_matches_ = show_matches;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Sets the value and appearance of the current node in the specified model
   * element tree.
   *
   * Returns the Component that the renderer uses to draw the value.
   *
   * @param  tree  The tree the node to render belongs to.
   * @param  tree_node  The tree node to render.
   * @param  is_selected  True if node is to be drawn as selected.
   * @param  is_expanded  True if node is to be drawn as expanded.
   * @param  is_leaf  True if node is to be drawn as a leaf.
   * @param  row  Tree row node appears in.
   * @param  has_focus  True if node currently has focus.
   * @return  Component for displaying the node as drawn by the renderer.
   * @see  ModelTreeNode
   */
  @Override public Component getTreeCellRendererComponent(JTree tree,
      Object tree_node, boolean is_selected, boolean is_expanded,
      boolean is_leaf, int row, boolean has_focus) {
    ModelTreeNode node = (ModelTreeNode) tree_node;

    // Set the view's icons as given by the node object.
    setOpenIcon(node.getIconOpened());
    setClosedIcon(node.getIconClosed());
    setLeafIcon(node.getIconLeaf());


    // Construct the path to the node as a string.
    StringBuilder path = new StringBuilder();

    // Examine each node along the path for its name.
    for (TreeNode path_node : node.getPath())
      path.append(path_node.toString() + '.');

    // Clean up path string.
    if (path.charAt(0) == '.')
      path.deleteCharAt(0);

    if (path.lastIndexOf(".") == path.length() - 1)
      path.deleteCharAt(path.length() - 1);


    // Set a tool tip with the node object's type, path, and ID.
    String tool_tip = ((node.getClassName() != null)
        ? node.getClassName() + ": "
        : "") + path.toString() + " (ID=" + node.getId() + ")";
    setToolTipText(tool_tip);


    // This must be called before disabling matches.
    Component cell = super.getTreeCellRendererComponent(tree, node, is_selected,
        is_expanded, is_leaf, row, has_focus);

    if (show_matches_)
      disableMatches(node);

    return cell;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  node  Purpose of the argument.
   */
  public void disableMatches(ModelTreeNode node) {
    EcoreExtra element = node.getExtra();

    if (element != null && element.getMatch() != null)
      setEnabled(false);
  }


  // Instance data ----------------------------------------------------------
  private boolean show_matches_;
  // End instance data ------------------------------------------------------
}

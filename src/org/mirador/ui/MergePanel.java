/* --------------------------------------------------------------------------+
   MergePanel.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.Mirador;
import ca.dsrg.mirador.difference.EcoreTyper;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.merge.ChangeOp;
import ca.dsrg.mirador.merge.ChangeOpPlane;
import ca.dsrg.mirador.merge.MergeWorks;
import ca.dsrg.mirador.model.MiradorModel;
import ca.dsrg.mirador.model.ModelRepository;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.5 - Jan 30, 2010
 * @author  Stephen Barrett
 */
public class MergePanel extends WizardPanel {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   *                                                                      DOCDO: Provide constructor overview.
   *
   * @param  wizard  Purpose of the argument.
   */
  public MergePanel(MergeWizard wizard) {
    super(wizard, PANEL_INFO, HELP_FILE);
    initialize();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  private void dumpApplies() {
    for (int i = 0; i <= apply_tbl_.getSelectedRow(); ++i) {
      ChangeOp op_lf = ((ChangeOp) apply_tbl_.getValueAt(i, ApplyOpTable.LEFT));
      String op_string_lf = (op_lf != null) ? op_lf.toString() : "";

      ChangeOp op_rt = ((ChangeOp) apply_tbl_.getValueAt(i, ApplyOpTable.RIGHT));
      String op_string_rt = (op_rt != null) ? op_rt.toString() : "";

      String sym = (String) apply_tbl_.getValueAt(i, ApplyOpTable.STATUS);
      if (sym.equals(">"))
        sym = "    >";
      else if (sym.equals(">>"))
        sym = "   >>";
      else if (sym.equals("<<>>"))
        sym = "<< >>";
      if (sym.equals("X"))
        sym = "  X";
      if (sym.equals("!"))
        sym = "  !";

      Debug.dbg.printf("%50s %-5s %-50s\n", op_string_lf, sym, op_string_rt);
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  private void cleanModel() {
    MiradorModel model = model_repo_.getModelMerged();

    for (Iterator<ENamedElement> it = model.elementIterator(); it.hasNext();) {
      ENamedElement elem = it.next();
      EcoreType typ = EcoreTyper.typeEObject(elem);
      EClassifier etyp;
      String id;
      ENamedElement rep;

      switch (typ) {
        case ATTRIBUTE:
          etyp = ((EAttribute) elem).getEType();
          id = model.getId(etyp);
          rep = null;

          if (id == null)
            id = model_repo_.getDiffModelLeft().getId(etyp);

          if (id == null)
            id = model_repo_.getDiffModelRight().getId(etyp);

          if (id != null)
            rep = model.getElement(id);

          if (etyp != rep)
            ((EAttribute) elem).setEType((EClassifier) rep);
        break;

        case CLASS:
        break;

        case DATATYPE:
        break;

        case OPERATION:
          etyp = ((EOperation) elem).getEType();
          id = model.getId(etyp);
          rep = null;

          if (id == null)
            id = model_repo_.getDiffModelLeft().getId(etyp);

          if (id == null)
            id = model_repo_.getDiffModelRight().getId(etyp);

          if (id != null)
            rep = model.getElement(id);

          if (etyp != rep)
            ((EOperation) elem).setEType((EClassifier) rep);
        break;

        case PACKAGE:
        break;

        case PARAMETER:
          etyp = ((EParameter) elem).getEType();
          id = model.getId(etyp);
          rep = null;

          if (id == null)
            id = model_repo_.getDiffModelLeft().getId(etyp);

          if (id == null)
            id = model_repo_.getDiffModelRight().getId(etyp);

          if (id != null)
            rep = model.getElement(id);

          if (etyp != rep)
            ((EParameter) elem).setEType((EClassifier) rep);
        break;

        case REFERENCE:
          etyp = ((EReference) elem).getEType();
          id = model.getId(etyp);
          rep = null;

          if (id == null)
            id = model_repo_.getDiffModelLeft().getId(etyp);

          if (id == null)
            id = model_repo_.getDiffModelRight().getId(etyp);

          if (id != null)
            rep = model.getElement(id);

          if (etyp != rep)
            ((EReference) elem).setEType((EClassifier) rep);
        break;
      }
    }
  }


  private void scrollTable() {
    int row_no = (apply_tbl_.getSelectedRow() < apply_tbl_.getRowCount() - 1)
        ? apply_tbl_.getSelectedRow() + 1 : apply_tbl_.getSelectedRow();

    if (row_no < apply_tbl_.getRowCount()) {
      apply_tbl_.setRowSelectionInterval(row_no, row_no);
      Rectangle rect = apply_tbl_.getCellRect(row_no, 1, true);
      apply_tbl_.scrollRectToVisible(rect);
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   * Populate and render the trees. Fire table fill.
   *
   */
  private void syncViews() {
    // Detach renderers to avoid trying to render nothing.
    view_tree_.setCellRenderer(null);

    // Populated each tree's data model.
    model_tree_.populate();

    // Assign populated data models to their respective tree views.
    view_tree_.setModel(model_tree_.getTreeModel());

    // Assign cell renderer *after* the models are in place and populated.
    ModelTreeCellRenderer renderer = new ModelTreeCellRenderer(false);
    view_tree_.setCellRenderer(renderer);

    model_tree_.expandRows();


    apply_op_tbl_.fillTable();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleApplyPanel() {
    // Instantiate the components.
    JComponent table_sep = DefaultComponentFactory.getInstance()
        .createSeparator("Apply Desired Changes and Resolve Conflicts",
        SwingConstants.CENTER);

    apply_tbl_ = new JTable();
    apply_scl_ = new JScrollPane(apply_tbl_);
    JPanel control_pnl = assembleControlPanel();


    // Set visual and behavioral aspects of the components.
    apply_tbl_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    apply_tbl_.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    apply_tbl_.setRowSelectionAllowed(true);
    apply_tbl_.setToolTipText("Select a change...");

    ToolTipManager.sharedInstance().registerComponent(apply_tbl_);


    // Set panel layout and constraints.
    String col_spec = "pref:grow, $ugap, pref";
    String row_spec = "pref, $rgap, fill:MIN(86dlu;pref):grow";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(table_sep,   cc.rcw(1, 1, 3));
    builder.add(apply_scl_,  cc.rc (3, 1));
    builder.add(control_pnl, cc.rc (3, 3));

    return builder.getPanel();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleControlPanel() {
    // Instantiate the components.
    accept_btn_ = new JButton("Accept");
    reject_btn_ = new JButton("Reject");
    left_btn_ = new JButton("Left");
    right_btn_ = new JButton("Right");
    apply_btn_ = new JButton("Apply");
    undo_btn_ = new JButton("Undo");


    // Set visual and behavioral aspects of the components.
    accept_btn_.setMnemonic(KeyEvent.VK_C);
    accept_btn_.setToolTipText("Apply...");

    reject_btn_.setMnemonic(KeyEvent.VK_J);
    reject_btn_.setToolTipText("Apply...");

    left_btn_.setMnemonic(KeyEvent.VK_L);
    left_btn_.setToolTipText("Apply...");

    right_btn_.setMnemonic(KeyEvent.VK_R);
    right_btn_.setToolTipText("Apply...");

    apply_btn_.setMnemonic(KeyEvent.VK_A);
    apply_btn_.setToolTipText("Apply...");

    undo_btn_.setMnemonic(KeyEvent.VK_U);
    undo_btn_.setToolTipText("Apply...");


    // Set panel layout and constraints.
    String col_spec = "pref, $rgap, pref";
    String row_spec = "$ugap, pref, 5dlu, pref, 5dlu, pref, $ugap";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(accept_btn_, cc.rc(2, 1));
    builder.add(reject_btn_, cc.rc(2, 3));
    builder.add(left_btn_,   cc.rc(4, 1));
    builder.add(right_btn_,  cc.rc(4, 3));
    builder.add(apply_btn_,  cc.rc(6, 1));
    builder.add(undo_btn_,   cc.rc(6, 3));

    return builder.getPanel();
  }


  /**
   * Instantiate the GUI's components, and populate its containers.
   */
  private void assembleGui() {
    // Instantiate the components.
    JComponent tree_sep = DefaultComponentFactory.getInstance()
        .createSeparator("Build Merged Model from Common Ancestor",
        SwingConstants.CENTER);

    view_tree_ = new JTree();
    view_scl_ = new JScrollPane(view_tree_);

    JPanel apply_pnl = assembleApplyPanel();
    view_spl_ = new JSplitPane(JSplitPane.VERTICAL_SPLIT, view_scl_, apply_pnl);


    // Set visual and behavioral aspects of the components.
    DefaultTreeSelectionModel mode = new DefaultTreeSelectionModel();
    mode.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    view_tree_.setSelectionModel(mode);
    view_tree_.setToolTipText("Select an element to...");

    ToolTipManager.sharedInstance().registerComponent(view_tree_);

//??    view_spl_.setPreferredSize(view_spl_.getMaximumSize());
    view_spl_.setOneTouchExpandable(true);
    view_spl_.setResizeWeight(0.7);


    // Set panel layout and constraints.
    String col_spec = "pref:grow";
    String row_spec = "pref, $rgap, fill:pref:grow";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout, this);
    builder.setBorder(Borders.TABBED_DIALOG_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(tree_sep,  cc.rc(1, 1));
    builder.add(view_spl_, cc.rc(3, 1));
  }


  /**
   * Attach observers to panel components.
   */
  private void attachHandlers() { // TODO:3 Add handlers to sync table row selection with tree node.
    accept_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onAcceptChangeClick(ev);
      }
    });


    reject_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onRejectChangeClick(ev);
      }
    });


    left_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onLeftChangeClick(ev);
      }
    });


    right_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onRightChangeClick(ev);
      }
    });


    apply_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onApplyChangeClick(ev);
      }
    });


    undo_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onUndoChangeClick(ev);
      }
    });
  }


  private void buildViews() {
    // Create and initialize tree view.
    model_tree_ = new ModelTree(model_repo_.getModelMerged(), view_tree_);

    // Create and initialize table view.
    apply_op_tbl_ = new ApplyOpTable(model_repo_, apply_tbl_);
  }


  /**
   * Constructs the panel and readys it for display.
   */
  private void initialize() {
    // Create and place the visual components on the panel.
    assembleGui();


    // Set state of components *prior* to establishing event links.


    // Link observers to panel components for event handling.
    attachHandlers();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onAcceptChangeClick(ActionEvent ev) {
    apply_op_tbl_.acceptChange();
    scrollTable();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onApplyChangeClick(ActionEvent ev) {
    int cnt = 0;

    for (int i = 0; i <= apply_tbl_.getSelectedRow(); ++i) {
      String sym = (String) apply_tbl_.getValueAt(i, ApplyOpTable.STATUS);
      int dir = ApplyOpTable.STATUS;

      if (sym.equals("X") || sym.equals("!"))  // FIXME:2 Use change object, not GUI!
        continue;  // May lead to inconsistencies.
      else if (sym.equals("<<") || sym.equals(">>") || sym.equals("<<>>"))
        continue;
      else if (sym.equals("<") || sym.equals("<>>"))
        dir = ApplyOpTable.LEFT;
      else if (sym.equals(">") || sym.equals("<<>"))
        dir = ApplyOpTable.RIGHT;


      if (apply_op_tbl_.applyChange(i, dir)) {
        if (sym.equals("<"))
          sym = "<<";
        else if (sym.equals(">"))
          sym = ">>";
        else if (sym.equals("<>"))
          sym = "<<>>";
      }
      else
        sym = "!";

      apply_tbl_.setValueAt(sym, i, ApplyOpTable.STATUS);

      if (sym != "!")
        ++cnt;
    }


    if (cnt > 0) { // FIXME:1 Simplify refresh of tree.
      // Detach renderers to avoid trying to render nothing.
      view_tree_.setCellRenderer(null);

      // Populated each tree's data model.
      model_tree_.populate();

      // Assign populated data models to their respective tree views.
      view_tree_.setModel(model_tree_.getTreeModel());

      // Assign cell renderer *after* the models are in place and populated.
      ModelTreeCellRenderer renderer = new ModelTreeCellRenderer(false);
      view_tree_.setCellRenderer(renderer);

      model_tree_.expandRows();
      scrollTable();
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onCancelClick(ActionEvent ev) {
    // TODO:3 Save states and preferences.
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onClose(WindowEvent ev) {
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onLeftChangeClick(ActionEvent ev) {
    apply_op_tbl_.leftChange();
    scrollTable();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onOkayClick(ActionEvent ev) {
    int last_row = apply_tbl_.getRowCount() - 1;
    apply_tbl_.setRowSelectionInterval(last_row, last_row);
    apply_btn_.doClick();

    cleanModel();

    if(Debug.dbg.isDebug()) {
      Debug.dbg.println("\n\n\n\t    --- APPLIED CHANGE OPS ---");
      dumpApplies();

      Debug.dbg.println("\n\n\n\t    --- MERGED MODEL ---");
      ModelRepository.dumpEcoreModel(model_repo_.getModelMerged());
    }

    if(Debug.dbg.isDebug() || Mirador.isAutoMode()) {
      String name = model_repo_.getFileLeft().getName();
      name = name.substring(0, name.lastIndexOf('.'));
      model_repo_.getModelMerged()
          .saveXmiModel("usr/" + name + "(mirador-merge).ecore");
    }

    if(Mirador.isAutoMode())
      model_repo_.outputAutoReports(merge_works_.getMasterSide(), apply_tbl_);
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onRejectChangeClick(ActionEvent ev) {
    apply_op_tbl_.rejectChange();
    scrollTable();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onRightChangeClick(ActionEvent ev) {
    apply_op_tbl_.rightChange();
    scrollTable();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onTabEnter(ChangeEvent ev) {
    // Complete construction of GUI.
    model_repo_ = wizard_.getModelRepository();  // Get latest repository.
    merge_works_ = wizard_.getMergeWorks();

    updateNavigation();  // Synchronize panel buttons with current GUI state.

    /*merge_plane_ =??*/ new ChangeOpPlane(model_repo_, wizard_.getMergeWorks());

    buildViews();  // Construct tree and table views.

    // Finalize state of GUI and event handlers.
    syncViews();
    wizard_.getStatusBar().setText(PANEL_INFO);  // Add panel status.
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onUndoChangeClick(ActionEvent ev) {
    undo_btn_.setText("As if");
    apply_op_tbl_.undoChange();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private void updateNavigation() {
    TabActionBar action_bar = wizard_.getActionBar();
    action_bar.enableBackwardButton();
    action_bar.disableForwardButton();  // TODO:2 Enable elsewhere, on condition.
    action_bar.enableOkayButton();  // TODO:2 Enable elsewhere, on condition.
  }


  // Instance data ----------------------------------------------------------
  private ModelRepository model_repo_;
  private MergeWorks merge_works_;
//??  private ChangeOpPlane merge_plane_;  // Why is this never used??

  private JTree view_tree_;
  private ModelTree model_tree_;

  private JTable apply_tbl_;
  private ApplyOpTable apply_op_tbl_;

  private JSplitPane view_spl_;
  private JScrollPane view_scl_;
  private JScrollPane apply_scl_;

  private JButton accept_btn_;
  private JButton reject_btn_;
  private JButton left_btn_;
  private JButton right_btn_;
  private JButton apply_btn_;
  private JButton undo_btn_;
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  static private final String HELP_FILE = "model-merge.html";
  static private final String PANEL_INFO =
      "Use this pane to effect the model merge.";
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  // End nested types -------------------------------------------------------
}

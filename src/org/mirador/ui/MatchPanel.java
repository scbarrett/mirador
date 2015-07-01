/* --------------------------------------------------------------------------+
   MatchPanel.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.InvocationParser;
import ca.dsrg.mirador.Mirador;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.match.EclEvaluator;
import ca.dsrg.mirador.match.ElementMatcher;
import ca.dsrg.mirador.match.MatchStrategy;
import ca.dsrg.mirador.match.MeasureMatrix;
import ca.dsrg.mirador.match.SimilarityEvaluator;
import ca.dsrg.mirador.merge.MergeWorks;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.ModelRepository;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.5 - Jan 30, 2010
 * @author  Stephen Barrett
 */
public class MatchPanel extends WizardPanel {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   *                                                                      DOCDO: Provide constructor overview.
   *
   * @param  wizard  Purpose of the argument.
   */
  public MatchPanel(MergeWizard wizard) {
    super(wizard, PANEL_INFO, HELP_FILE);
    initialize();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives left detail table GUI component.
   *
   * @return  Left detail table component.
   * @category  getter
   */
  public JTable getTableLeft() {
    return view_lf_tbl_;
  }


  /**
   * Gives right detail table GUI component.
   *
   * @return  Right detail table component.
   * @category  getter
   */
  public JTable getTableRight() {
    return view_rt_tbl_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  public void addStrategy(MatchStrategy to_add) {
    if (to_add != null && strategies_.size() < MAX_STRATEGIES)
      strategies_.add(to_add);
  }


  public void removeStrategy(MatchStrategy to_remove) {
    if (to_remove != null)
      strategies_.remove(to_remove);
  }


  public MatchStrategy getStrategy(int index) {
    return strategies_.get(index);
  }


  public MatchStrategy getStrategy(String label) {
    MatchStrategy rc = null;

    for (MatchStrategy strategy : strategies_) {
      if (label.equals(strategy.getLabel())) {
        rc = strategy;
        break;
      }
    }

    return rc;
  }


  public ListIterator<MatchStrategy> strategyIterator() {
    return strategies_.listIterator();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  private void matchThreshold() { // TODO:3 Add a spinner for lower cutoff (i.e., eliminate from table view).
    if (merge_works_.getMasterSide() == MergeSide.LEFT) {
      ElementMatcher.matchThreshold(
          model_repo_.getDiffModelLeft().getRankMatrix(),
          ((Number) limit_spn_.getValue()).floatValue());
    }
    else if (merge_works_.getMasterSide() == MergeSide.RIGHT) {
      ElementMatcher.matchThreshold(
          model_repo_.getDiffModelRight().getRankMatrix(),
          ((Number) limit_spn_.getValue()).floatValue());
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private void measureSimilarlities() {
    measure_lf_mtx_ = new MeasureMatrix(strategies_);
    model_repo_.getDiffModelLeft().setMatchMatrix(measure_lf_mtx_);

    ElementMatcher.measureSimilarities(model_repo_.getDiffModelLeft(),
       model_repo_.getDiffModelRight());

    measure_rt_mtx_ = new MeasureMatrix(strategies_);
    model_repo_.getDiffModelRight().setMatchMatrix(measure_rt_mtx_);

    ElementMatcher.measureSimilarities(model_repo_.getDiffModelRight(),
       model_repo_.getDiffModelLeft());
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private void rankSimilarlities() {
    ranking_lf_mtx_ = new MeasureMatrix(strategies_);
    model_repo_.getDiffModelLeft().setRankMatrix(ranking_lf_mtx_);

    ElementMatcher.rankSimilarlities(model_repo_.getDiffModelLeft(),
        ranking_lf_mtx_.getMatchStrategyIndex());

    Debug.dbg.println("\n\n\n\t    --- LEFT to RIGHT SIMILARITIES ---");
    ranking_lf_mtx_.dump();


    ranking_rt_mtx_ = new MeasureMatrix(strategies_);
    model_repo_.getDiffModelRight().setRankMatrix(ranking_rt_mtx_);

    ElementMatcher.rankSimilarlities(model_repo_.getDiffModelRight(),
        ranking_rt_mtx_.getMatchStrategyIndex());

    Debug.dbg.println("\n\n\n\t    --- RIGHT to LEFT SIMILARITIES ---");
    ranking_rt_mtx_.dump();
  }


  /**                                                                     DOCDO: Provide method overview.
   * Populate and render the trees. Fire table fill.
   *
   */
  private void syncViews() {
    // Detach renderers to avoid trying to render nothing.
    view_lf_tree_.setCellRenderer(null);
    view_rt_tree_.setCellRenderer(null);

    // Populated each tree's data model.
    model_lf_tree_.populate();
    model_rt_tree_.populate();

    // Assign populated data models to their respective tree views.
    view_lf_tree_.setModel(model_lf_tree_.getTreeModel());
    view_rt_tree_.setModel(model_rt_tree_.getTreeModel());

    // Assign cell renderer *after* the models are in place and populated.
    ModelTreeCellRenderer renderer = new ModelTreeCellRenderer(true);
    view_lf_tree_.setCellRenderer(renderer);
    view_rt_tree_.setCellRenderer(renderer);

    model_lf_tree_.expandRows();
    model_rt_tree_.expandRows();


    // Trigger filling of detail tables from current tree nodes.
    if (view_lf_tree_.getSelectionPath() != null)
      view_lf_tree_.setSelectionPath(view_lf_tree_.getSelectionPath());
    else
      view_lf_tree_.setSelectionRow(0);

    if (view_rt_tree_.getSelectionPath() != null)
      view_rt_tree_.setSelectionPath(view_rt_tree_.getSelectionPath());
    else
      view_rt_tree_.setSelectionRow(0);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleControlMasterPanel() {
    // Instantiate the components.
    JPanel panel = new JPanel(new GridLayout(0, 1));
    ButtonGroup group = new ButtonGroup();
    master_lf_rbt_ = new JRadioButton("Left");
    master_rt_rbt_ = new JRadioButton("Right");


    // Set visual and behavioral aspects of the components.
    panel.setBorder(BorderFactory.createTitledBorder("Master side"));

    master_lf_rbt_.setMnemonic(KeyEvent.VK_L);
    master_lf_rbt_.
        setToolTipText("Merge from point of view of the \"left\" model.");
    master_rt_rbt_.setMnemonic(KeyEvent.VK_R);
    master_rt_rbt_.
        setToolTipText("Merge from point of view of the \"right\" model.");

    group.add(master_lf_rbt_);
    group.add(master_rt_rbt_);


    // Add components to the panel.
    panel.add(master_lf_rbt_);
    panel.add(master_rt_rbt_);

    return panel;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleControlPanel() {
    // Instantiate the components.
    JPanel master_pnl = assembleControlMasterPanel();
    match_btn_ = new JButton("Match");
    unmatch_btn_ = new JButton("Unmatch");


    // Set visual and behavioral aspects of the components.
    match_btn_.setMnemonic(KeyEvent.VK_M);
    match_btn_.setToolTipText("Pair the selected tree elements.");
    unmatch_btn_.setMnemonic(KeyEvent.VK_U);
    unmatch_btn_.setToolTipText("Unpair the selected tree elements.");


    // Set panel layout and constraints.
    String col_spec = "$rgap, pref, $rgap";
    String row_spec = "pref, $ugap, pref, 4dlu, pref";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.DLU2_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(master_pnl,   cc.rcw(1, 1, 3));
    builder.add(match_btn_,   cc.rc (3, 2));
    builder.add(unmatch_btn_, cc.rc (5, 2));

    return builder.getPanel();
  }


  /**
   * Instantiate the GUI's components, and populate its containers.
   */
  private void assembleGui() {
    // Assemble the constituent panels.
    weight_pnl_ = assembleWeightPanel();
    JPanel option_pnl = assembleOptionPanel();
    JPanel view_pnl = assembleViewPanel();


    // Set panel layout and constraints.
    String col_spec = "pref:grow";
    String row_spec = "pref, $ugap, pref, $ugap, fill:pref:grow";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout, this);
    builder.setBorder(Borders.TABBED_DIALOG_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(weight_pnl_, cc.rc (1, 1));
    builder.add(option_pnl,  cc.rc (3, 1));
    builder.add(view_pnl,    cc.rc (5, 1));
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleOptionPanel() {
    // Instantiate the components.
    JComponent separator = DefaultComponentFactory.getInstance()
        .createSeparator("Set Model Matching Options", SwingConstants.CENTER);
    strategy_pnl_ = assembleOptionStrategyPanel();

    sync_cbx_ = new JCheckBox();
    InvocationParser parser = Mirador.getParser();
    float weight = new Float((parser.optionValue("match_threshold") != null)
        ? parser.optionValue("match_threshold") : "0.0");
    limit_spn_ = new JSpinner(new SpinnerNumberModel(0, 0, 1, weight));
    limit_btn_ = new JButton("Show");
    limit_spn_.setEditor(new JSpinner.NumberEditor(limit_spn_, "0.00"));


    // Set visual and behavioral aspects of the components.
    sync_cbx_.setMnemonic(KeyEvent.VK_Y);
    sync_cbx_.setToolTipText(
        "Check so selected element's match in opposite tree is also selected.");
    limit_spn_.setToolTipText(
        "Set similarity limit at which elements will be automatically matched.");
    limit_btn_.setToolTipText(
        "Highlight elements that will matched for the specified threshold.");


    // Set panel layout and constraints.
    String col_spec = "pref, $rgap, pref, fill:$ugap:grow, pref, $rgap,"
        + "pref, $ugap, pref";
    String row_spec = "pref, $rgap, pref, $rgap, pref";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(separator,                                cc.rcw(1, 1, 9));
    builder.add(strategy_pnl_,                            cc.rcw(3, 1, 9));
    builder.add(sync_cbx_,                                cc.rc (5, 1));
    builder.addLabel("S&ynchronize tree node selections", cc.rc (5, 3));
    builder.add(limit_spn_,                               cc.rc (5, 5));
    builder.addLabel("Matching similarity threshold",     cc.rc (5, 7)) ;
    builder.add(limit_btn_,                               cc.rc (5, 9)) ;

    return builder.getPanel();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleOptionStrategyPanel() {
    boolean is_first = (strategy_pnl_ == null);
    int sz;

    // Instantiate the components.
    strategy_grp_ = new ButtonGroup();
    if (is_first) {
      strategy_pnl_ = new JPanel(new GridLayout(1, 0));
      sz = MAX_STRATEGIES;
    }
    else {
      strategy_pnl_.removeAll();
      sz = strategies_.size();
    }


    // Set visual and behavioral aspects of the components.
    strategy_pnl_.setBorder(
        BorderFactory.createTitledBorder("Automatic matching strategy"));


    // Add components to the panel.
    for (int i = 0; i < sz; ++i) {
      JRadioButton radio = (is_first)
          ? new JRadioButton("a Strategy") : strategies_.get(i).getRadio();
      strategy_grp_.add(radio);
      strategy_pnl_.add(radio);
    }

    return strategy_pnl_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleViewPanel() {
    // Instantiate the components.
    JComponent separator = DefaultComponentFactory.getInstance()
        .createSeparator("Alter Model Element Matches", SwingConstants.CENTER);

    JPanel control_pnl = assembleControlPanel();

    view_lf_tree_ = new JTree();
    view_lf_scl_ = new JScrollPane(view_lf_tree_);
    view_lf_tbl_ = new JTable();
    table_lf_scl_ = new JScrollPane(view_lf_tbl_);
    view_lf_spl_ = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        view_lf_scl_, table_lf_scl_);

    view_rt_tree_ = new JTree();
    view_rt_scl_ = new JScrollPane(view_rt_tree_);
    view_rt_tbl_ = new JTable();
    table_rt_scl_ = new JScrollPane(view_rt_tbl_);
    view_rt_spl_ = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        view_rt_scl_, table_rt_scl_);


    // Set visual and behavioral aspects of the components.
    DefaultTreeSelectionModel mode = new DefaultTreeSelectionModel();
    mode.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    view_lf_tree_.setSelectionModel(mode);
    view_lf_tree_.setToolTipText("Select an element to highlight its match"
    	+ "in the right tree, and show its details the table below.");

    mode = new DefaultTreeSelectionModel();
    mode.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    view_rt_tree_.setSelectionModel(mode);
    view_rt_tree_.setToolTipText("Select an element to highlight its match"
        + "in the left tree, and show its details the table below.");

    ToolTipManager.sharedInstance().registerComponent(view_lf_tree_);
    ToolTipManager.sharedInstance().registerComponent(view_rt_tree_);

    view_lf_tbl_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    view_lf_tbl_.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    view_lf_tbl_.setRowSelectionAllowed(true);
    view_lf_tbl_.setToolTipText("Select a candidate to highlight its"
        + " potential match in the right tree.");

    view_rt_tbl_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    view_rt_tbl_.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    view_rt_tbl_.setRowSelectionAllowed(true);
    view_rt_tbl_.setToolTipText("Select a candidate to highlight its"
        + " potential match in the left tree.");

    view_lf_spl_.setPreferredSize(view_lf_spl_.getMaximumSize());
    view_lf_spl_.setOneTouchExpandable(true);
    view_lf_spl_.setResizeWeight(0.7);

    view_rt_spl_.setPreferredSize(view_rt_spl_.getMaximumSize());
    view_rt_spl_.setOneTouchExpandable(true);
    view_rt_spl_.setResizeWeight(0.7);


    // Set panel layout and constraints.
    String col_spec = "center:160dlu:grow, pref, center:160dlu:grow";
    String row_spec = "pref, $rgap, pref, $rgap, fill:MIN(180dlu;pref):grow";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(separator,                   cc.rcw(1, 1, 3));
    builder.addLabel("Left model elements",  cc.rc (3, 1));
    builder.add(view_lf_spl_,                cc.rc (5, 1));
    builder.add(control_pnl,                 cc.rc (5, 2));
    builder.addLabel("Right model elements", cc.rc (3, 3));
    builder.add(view_rt_spl_,                cc.rc (5, 3));

    return builder.getPanel();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleWeightPanel() {
    // Instantiate the components.
    JComponent separator = DefaultComponentFactory.getInstance()
        .createSeparator("Set Relative Weights for Matching Strategies",
        SwingConstants.CENTER);

    update_btn_ = new JButton("Update");


    // Set visual and behavioral aspects of the components.
    update_btn_.setMnemonic(KeyEvent.VK_U);
    update_btn_.setToolTipText("Force similarity calculation using specified"
        + " weights, and redisplay new values in detail tables.");


    // Set panel layout and constraints.
    String col_spec =
          "pref:grow, $ugap, pref:grow, $ugap, pref:grow, $ugap,"
        + "pref:grow, $ugap, pref:grow, $ugap, pref:grow, $ugap,"
        + "pref:grow, $ugap, pref:grow, $ugap, pref";
    String row_spec = "pref, $rgap, pref, $rgap, pref";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder;

    int sz;
    int col;

    if (weight_pnl_ == null) {
      builder = new PanelBuilder(layout);
      sz = MAX_STRATEGIES;
      col = 1;
    }
    else {
      weight_pnl_.removeAll();
      builder = new PanelBuilder(layout, weight_pnl_);
      sz = strategies_.size();
      col = 17 - (sz - 1) * 2;
    }
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(separator, cc.rcw(1, 1, 17));
    for (int i = 1; i < sz; ++i) {
      if (weight_pnl_ == null) {
        builder.addLabel("a Strategy", cc.rcw(3, col, 2));
        builder.add(new JSpinner(), cc.rc (5, col));
      }
      else {
        builder.addLabel(strategies_.get(i).getLabel(), cc.rcw(3, col, 2));
        builder.add(strategies_.get(i).getSpinner(), cc.rc (5, col));
      }
      col += 2;
    }
    builder.add(update_btn_, cc.rc (5, col));

    return builder.getPanel();
  }


  /**
   * Attach observers to panel components.
   */
  private void attachHandlers() {
    match_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onElementMatchClick(ev);
      }
    });


    unmatch_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onElementUnmatchClick(ev);
      }
    });


    limit_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onLimitShowClick(ev);
      }
    });


    // Left master side radio button event handler.
    master_lf_rbt_.addItemListener(new ItemListener() {
      @Override public void itemStateChanged(ItemEvent ev) {
        onMasterSideChange(ev);
      }
    });


    // Right master side radio button event handler.
    master_rt_rbt_.addItemListener(new ItemListener() {
      @Override public void itemStateChanged(ItemEvent ev) {
        onMasterSideChange(ev);
      }
    });


    TreeMouseListener listener = new TreeMouseListener();
    view_lf_tree_.addMouseListener(listener);
    view_rt_tree_.addMouseListener(listener);

    table_lf_scl_.addAncestorListener(
        new DividerListener(view_lf_spl_, view_rt_spl_));
    table_rt_scl_.addAncestorListener(
        new DividerListener(view_rt_spl_, view_lf_spl_));
  }


  private void attachViewHandlers() {
    if (tree_lf_lsr_ != null)
      view_lf_tree_.removeTreeSelectionListener(tree_lf_lsr_);
    tree_lf_lsr_ = new TreeNodeListener(view_rt_tree_, measure_lf_tbl_);
    view_lf_tree_.addTreeSelectionListener(tree_lf_lsr_);

    if (tree_rt_lsr_ != null)
      view_rt_tree_.removeTreeSelectionListener(tree_rt_lsr_);
    tree_rt_lsr_ = new TreeNodeListener(view_lf_tree_, measure_rt_tbl_);
    view_rt_tree_.addTreeSelectionListener(tree_rt_lsr_);


    if (table_lf_lsr_ != null)
      view_lf_tbl_.getSelectionModel().removeListSelectionListener(table_lf_lsr_);
    view_lf_tbl_.getSelectionModel().addListSelectionListener(
        new TableRowListener(measure_lf_tbl_, model_rt_tree_));

    if (table_rt_lsr_ != null)
      view_rt_tbl_.getSelectionModel().removeListSelectionListener(table_rt_lsr_);
    view_rt_tbl_.getSelectionModel().addListSelectionListener(
        new TableRowListener(measure_rt_tbl_, model_lf_tree_));
  }


  private void buildViews() {
    // Create and initialize tree views.
    model_lf_tree_ =
        new ModelTree(model_repo_.getDiffModelLeft(), view_lf_tree_);
    model_rt_tree_ =
        new ModelTree(model_repo_.getDiffModelRight(), view_rt_tree_);

    // Create and initialize table views.
    measure_lf_tbl_ = new MeasureTable(model_repo_.getDiffModelLeft(),
        strategies_, view_lf_tbl_);
    measure_rt_tbl_ = new MeasureTable(model_repo_.getDiffModelRight(),
        strategies_, view_rt_tbl_);
  }


  /**
   * Constructs the panel and readys it for display.
   */
  private void initialize() {
    // Create and place the visual components on the panel.
    assembleGui();


    // Set state of components *prior* to establishing event links.
    if (Mirador.getParser().optionValue("master_side") == "right")
      master_rt_rbt_.setSelected(true);
    else
      master_lf_rbt_.setSelected(true);

    limit_btn_.setEnabled(true);
    limit_spn_.setValue(0.6f);
    sync_cbx_.setSelected(true);


    // Link observers to panel components for event handling.
    attachHandlers();
  }


  private void loadEvaluators() {
    strategies_.clear();
    addStrategy(new MatchStrategy(null, 1.0f, "Overall", null,
        "Automatically match elements based on overall similarity."));

    for (Iterator<SimilarityEvaluator> it = merge_works_.evaluatorIterator();
        it.hasNext();) {
      SimilarityEvaluator evaluator = it.next();
      addStrategy(new MatchStrategy(evaluator, evaluator.getInitalWeight(),
          evaluator.getLabel(), evaluator.getSpinToolTip(),
          evaluator.getRadioToolTip()));
    }

    setupEvaluators();
  }


  private void loadMatches() {  // TODO:2 Prematched elements.
  }


  private void setupEvaluators() {
    MatchStrategy ecl_strategy = getStrategy("by ECL");

    if (ecl_strategy != null) {
      ((EclEvaluator) ecl_strategy.getEvaluator()).compare(
          model_repo_.getDiffModelLeft().getXmiModel(),
          model_repo_.getDiffModelRight().getXmiModel(), null);
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
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
  void onElementMatchClick(ActionEvent ev) {
    TreePath path_lf = view_lf_tree_.getSelectionPath();
    ModelTreeNode node_lf = (ModelTreeNode) path_lf.getLastPathComponent();
    EcoreExtra element_lf = node_lf.getExtra();

    TreePath path_rt = view_rt_tree_.getSelectionPath();
    ModelTreeNode node_rt = (ModelTreeNode) path_rt.getLastPathComponent();
    EcoreExtra element_rt = node_rt.getExtra();

    // Match only like elements to like.
    if (element_lf.getEcoreType() == element_rt.getEcoreType()) {
      element_lf.setMatch(element_rt);
      element_lf.setKeepMatch();
      view_lf_tree_.repaint();
      view_rt_tree_.repaint();
      match_btn_.setEnabled(false);
      unmatch_btn_.setEnabled(true);
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onElementUnmatchClick(ActionEvent ev) {
    TreePath path_lf = view_lf_tree_.getSelectionPath();
    ModelTreeNode node_lf = (ModelTreeNode) path_lf.getLastPathComponent();
    EcoreExtra element_lf = node_lf.getExtra();

    TreePath path_rt = view_rt_tree_.getSelectionPath();
    ModelTreeNode node_rt = (ModelTreeNode) path_rt.getLastPathComponent();
    EcoreExtra element_rt = node_rt.getExtra();

    // Unmatch only already matched elements.
    if (element_lf.getMatch() != null
        && element_lf.getMatch().equals(element_rt)) {
      element_lf.setMatch(null);
      element_lf.resetKeepMatch();
      view_lf_tree_.repaint();
      view_rt_tree_.repaint();
      match_btn_.setEnabled(true);
      unmatch_btn_.setEnabled(false);
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onLimitShowClick(ActionEvent ev) {
    // Pair elements that meet or exceed new similarity threshold.
    matchThreshold();

    // Populate trees and tables based on latest measures of similarity.
    syncViews();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onMasterSideChange(ItemEvent ev) {
    if (ev.getStateChange() == ItemEvent.SELECTED) {
      if (ev.getItem() == master_lf_rbt_)
        merge_works_.setMasterSideLeft();
      else if (ev.getItem() == master_rt_rbt_)
        merge_works_.setMasterSideRight();


      // Pair elements w.r.t. new side that meet or exceed similarity threshold.
      matchThreshold();

      // Populate trees and tables based on latest measures of similarity.
      syncViews();
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onOkayClick(ActionEvent ev) {
    wizard_.getActionBar().getForwardButton().doClick();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onStrategyChange(ActionEvent ev) {
    // Set index to new matching strategy.
    for (int i = 0; i < strategies_.size(); ++i) {
      if (strategies_.get(i).getLabel().equals(ev.getActionCommand())) {
        measure_lf_mtx_.setMatchStrategyIndex(i);
        measure_rt_mtx_.setMatchStrategyIndex(i);
        break;
      }
    }

    // Order measures of selected matching criteria from high to low.
    rankSimilarlities();

    // Pair elements w.r.t. new strategy that meet or exceed similarity threshold.
    matchThreshold();

    // Populate trees and tables based on latest measures of similarity.
    syncViews();
  }


  /**                                                                     DOCDO: Provide method overview.
   * Synchronize panel view with new inputs from model panel.
   *
   * @param  ev  The triggering event.
   */
  void onTabEnter(ChangeEvent ev) {
    // Complete construction of GUI.
    model_repo_ = wizard_.getModelRepository();  // Get latest model repository.
    merge_works_ = wizard_.getMergeWorks();
    loadEvaluators();  // Load similarity evaluators specified in model panel.

    assembleWeightPanel(); // Weight panel with new evaluators and...
    update_btn_.addActionListener(new ActionListener() { // ... event handlers.
      @Override public void actionPerformed(ActionEvent ev) {
        onWeightUpdateClick(ev);
      }
    });

    assembleOptionStrategyPanel();  // Strategy panel with new evaluators and...
    Enumeration<AbstractButton> strategies = strategy_grp_.getElements();
    while (strategies.hasMoreElements()) { // ... event handlers.
      JRadioButton strategy = (JRadioButton) strategies.nextElement();

      strategy.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          onStrategyChange(ev);
        }
      });
    }

    buildViews();  // Construct tree and table views.

    updateNavigation();  // Synchronize panel buttons with current GUI state.


    // Initialize models, and measure and match elements based on new settings.
    loadMatches();  // Load similarity evaluators specified in model panel.

//??    ElementMatcher.matchCommon(model_repo_.getModelLeft().getElements(),
//        model_repo_.getModelRight().getElements());  // FIXME:2 Common matches should be sticky.

    measureSimilarlities();  // Measure all possible element pairings.


    // Finalize state of GUI and event handlers.
    wizard_.getStatusBar().setText(PANEL_INFO);  // Add panel status.


    attachViewHandlers();  // Add tree and table handlers.


    //Set master side without invoking any events.
    merge_works_.setMasterSideLeft();

    // Pick "Overall" scoring to rank and score elements, and fire syncViews().
    if (strategy_grp_.getElements().hasMoreElements())
      ((JRadioButton) strategy_grp_.getElements().nextElement()).doClick();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onTabExit(ChangeEvent ev) {
    // TODO:3 Output match report.

    matchThreshold();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onWeightUpdateClick(ActionEvent ev) {
    // Set strategy weights as specified by spinner boxes.
    for (int i = 1; i < strategies_.size(); ++i) {
      strategies_.get(i).setWeight(((Number)
          strategies_.get(i).getSpinner().getValue()).floatValue());
    }

    // Obtain measures for all possible element pairings using new weights.
    measureSimilarlities();  // TODO:2 Just multiple against nominal measures.

    // Order measures of selected matching criteria from high to low.
    rankSimilarlities();

    // Pair elements that meet or exceed similarity threshold.
    matchThreshold();

    // Populate trees and tables based on latest measures of similarity.
    syncViews();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private void updateNavigation() {
    TabActionBar action_bar = wizard_.getActionBar();
    action_bar.enableBackwardButton();
  }


  // Instance data ----------------------------------------------------------
  private JPanel weight_pnl_;
  private JPanel strategy_pnl_;
  private ButtonGroup strategy_grp_;

  private JRadioButton master_lf_rbt_;
  private JRadioButton master_rt_rbt_;

  private JButton match_btn_;
  private JButton unmatch_btn_;
  private JButton update_btn_;

  private JButton limit_btn_;
  private JSpinner limit_spn_;
  private JCheckBox sync_cbx_;

  private JTree view_lf_tree_;
  private JTree view_rt_tree_;

  private ModelTree model_lf_tree_;
  private ModelTree model_rt_tree_;

  private TreeNodeListener tree_lf_lsr_;
  private TreeNodeListener tree_rt_lsr_;

  private JSplitPane view_lf_spl_;
  private JSplitPane view_rt_spl_;

  private JScrollPane view_lf_scl_;
  private JScrollPane view_rt_scl_;

  private JTable view_lf_tbl_;
  private JTable view_rt_tbl_;

  private JScrollPane table_lf_scl_;
  private JScrollPane table_rt_scl_;

  private MeasureTable measure_lf_tbl_;
  private MeasureTable measure_rt_tbl_;

  private TableRowListener table_lf_lsr_;
  private TableRowListener table_rt_lsr_;

  private ModelRepository model_repo_;
  private MergeWorks merge_works_;

  private MeasureMatrix measure_lf_mtx_;
  private MeasureMatrix measure_rt_mtx_;
  private MeasureMatrix ranking_lf_mtx_;
  private MeasureMatrix ranking_rt_mtx_;

  private List<MatchStrategy> strategies_ = new ArrayList<MatchStrategy>();
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  static private final String HELP_FILE = "element-match.html";
  static private final String PANEL_INFO =
      "Use this pane to establish correspondence between mode elements.";
  static public final int MAX_STRATEGIES = 9;  // Overall plus eight more.
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.19 - Mar 10, 2010
   * @author  Stephen Barrett
   */
  private class DividerListener extends JScrollPane implements AncestorListener {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    /**                                                                   DOCDO: Provide constructor overview.
     *
     * @param  master  Purpose of the argument.
     * @param  slave  Purpose of the argument.
     */
    public DividerListener(JSplitPane master, JSplitPane slave) {
      master_ = master;
      slave_ = slave;
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void ancestorAdded(AncestorEvent ev) {}


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void ancestorMoved(AncestorEvent ev) {
      slave_.setDividerLocation(master_.getDividerLocation());
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void ancestorRemoved(AncestorEvent ev) {}


    // Instance data --------------------------------------------------------
    private JSplitPane master_;
    private JSplitPane slave_;
    // End instance data ----------------------------------------------------
  }


  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.19 - Mar 10, 2010
   * @author  Stephen Barrett
   */
  static private class TableRowListener implements ListSelectionListener {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    public TableRowListener(MeasureTable source, ModelTree target) {
      source_ = source;
      target_ = target;
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void valueChanged(ListSelectionEvent ev) {
      if (ev.getValueIsAdjusting())
        return;

      int index = source_.getTable().getSelectedRow();
      if (index >= 0) {
        EcoreExtra source =
            (EcoreExtra) source_.getTable().getValueAt(index, 0);

        if (source != null) {
          EcoreExtra target = target_.getMiradorModel().getExtra(source.getId());
          target_.getTree().addSelectionPath(target.getTreePath());
        }
      }
    }


    // Instance data --------------------------------------------------------
    private MeasureTable source_;
    private ModelTree target_;
    // End instance data ----------------------------------------------------
  }


  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.19 - Mar 10, 2010
   * @author  Stephen Barrett
   */
  static private class TreeMouseListener extends MouseAdapter {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void mouseEntered(MouseEvent ev) {
      ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void mouseExited(MouseEvent ev) {
      ToolTipManager.sharedInstance().setDismissDelay(DISMISS_MS);
    }


    // Class data -----------------------------------------------------------
    static private int DISMISS_MS =
        ToolTipManager.sharedInstance().getDismissDelay();
    // End class data -------------------------------------------------------
  }


  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.13 - Feb 26, 2010
   * @author  Stephen Barrett
   */
  private class TreeNodeListener implements TreeSelectionListener {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    public TreeNodeListener(JTree slave, MeasureTable detail) {
      slave_ = slave;
      detail_ = detail;
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void valueChanged(TreeSelectionEvent ev) {
      ModelTreeNode selected_node =
          ((ModelTreeNode) ev.getPath().getLastPathComponent());

      EcoreExtra selected = selected_node.getExtra();


      // Update selected element's table details.
      detail_.fillTable(selected);

      // Selected element's matched candidate in the other tree.
      EcoreExtra matched = selected.getMatch();
      if (sync_cbx_.isSelected() && matched != null) {
        if (matched.getTreePath() != null)
          slave_.addSelectionPath(matched.getTreePath());  // Fires event again.
      }


      // Update panel buttons.
      ModelTreeNode synched_node =
          ((ModelTreeNode) slave_.getLastSelectedPathComponent());

      if (synched_node != null) {
        EcoreExtra synched = synched_node.getExtra();

        if (synched == matched) {
          match_btn_.setEnabled(false);
          unmatch_btn_.setEnabled(true);
        }
        else if (selected.getEcoreType() == synched.getEcoreType()) {
          match_btn_.setEnabled(true);
          unmatch_btn_.setEnabled(false);
        }
        else {
          match_btn_.setEnabled(false);
          unmatch_btn_.setEnabled(false);
        }
      }
    }


    // Instance data --------------------------------------------------------
    private JTree slave_;
    private MeasureTable detail_;
    // End instance data ----------------------------------------------------
  }
  // End nested types -------------------------------------------------------
}

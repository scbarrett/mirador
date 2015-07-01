/* --------------------------------------------------------------------------+
   MergeWizard.java - High-level description, and place in the system.
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
import ca.dsrg.mirador.Constants;
import ca.dsrg.mirador.Mirador;
import ca.dsrg.mirador.MiradorFujabaPlugin;
import ca.dsrg.mirador.merge.MergeWorks;
import ca.dsrg.mirador.model.ModelRepository;
import ca.dsrg.mirador.ui.TabActionBar.Callable;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.2 - Jan 20, 2010
 * @author  Stephen Barrett
 */
public class MergeWizard extends JDialog {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  plugin_path  Purpose of the argument.
   */
  private MergeWizard(JFrame owner, String plugin_path) {
    super(owner, TITLE + Constants.VERSION, false);
    Constants.Initializer.plugin_path_ = plugin_path;
    merge_works_ = new MergeWorks();
    initialize();
  }


  /**
   * Returns either an existing singleton, or a new instance of the class.
   *
   * @return  The solitary class instance.
   */
  static public MergeWizard get() {
    if (instance_ == null)
      instance_ = new MergeWizard(new JFrame(), "file:");

    return instance_;
  }


  /**
   * Returns either an existing singleton, or a new instance of the class.
   *
   * @param  owner Purpose of the argument.
   * @param  plugin_path  Purpose of the argument.
   * @return  The solitary class instance.
   */
  static public MergeWizard get(JFrame owner, String plugin_path) {
    if (instance_ == null)
      instance_ = new MergeWizard(owner, plugin_path);

    return instance_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public JTabbedPane getPane() {
    return tab_pane_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public TabActionBar getActionBar() {
    return action_bar_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public ModelRepository getModelRepository() {
    return model_repo_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  repository  Set-to value for the field.
   * @category  setter
   */
  public void setModelRepository(ModelRepository repository) {
    model_repo_ = repository;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public JLabel getStatusBar() {
    return status_lbl_;
  }


  /**
   * Returns the wizard's associated model merging plug-in.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public MiradorFujabaPlugin getFugabaPlugin() {
    return fujaba_plugin_;
  }


  /**
   * Establishes a reciprocal link between wizard and model merging plug-in.
   *
   * @param  plugin  Set-to value for the field.
   * @return  true = value set, false = value not set
   */
  public boolean setFujabaPlugin(MiradorFujabaPlugin plugin) {
    boolean rc = false;

    // Remove old linkage, and establish new; in both directions.
    if (fujaba_plugin_ != plugin) { // Exit if link is in place.
      if (fujaba_plugin_ != null)
        fujaba_plugin_.setWizard(null);

      fujaba_plugin_ = plugin;

      if (plugin != null)
        plugin.setWizard(this);

      rc = true;
    }

    return rc;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public MergeWorks getMergeWorks() {
    return merge_works_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  works  Set-to value for the field.
   * @category  setter
   */
  public void setMergeWorks(MergeWorks works) {
    merge_works_ = works;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  true = success, false = failure
   */
  public boolean isStandAlone() {
    return (fujaba_plugin_ == null);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**                                                                     DOCDO: Provide method overview.
   *
   */
  public void close() {
    dispose();

    if (isStandAlone())
      System.exit(0);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Instantiate the GUI's components, and populate its containers.
   */
  private void assembleGui() {
    // Create subcontainers.
    tab_pane_ = new JTabbedPane();

    // Instantiate GUI components...
    action_bar_ = new TabActionBar(new BarCallback());
    status_lbl_ = new JLabel();
    Font font = status_lbl_.getFont();
    status_lbl_.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 1));

    model_pg_ = new ModelPanel(this);
    match_pg_ = new MatchPanel(this);
    merge_pg_ = new MergePanel(this);

    // ...populate subcontainers with components, and...
    tab_pane_.addTab("Input Models", model_pg_);
    tab_pane_.addTab("Match Elements", match_pg_);
    tab_pane_.addTab("Merge Models", merge_pg_);

    // ...fill main container with subcontainers.
    add(status_lbl_, "North");
    add(tab_pane_, "Center");
    add(action_bar_.getPanel(), "South");
  }


  /**                                                                     DOCDO: Provide method overview.
   * Add component event observers and handlers.
   * (Attach observers, specify handlers as closures.)
   */
  private void attachHandlers() {
    tab_pane_.addChangeListener(new ChangeListener() {
      @Override public void stateChanged(ChangeEvent ev) {
        onTabChange(ev);
      }
    });


    addWindowListener(new WindowAdapter() {
      @Override public void windowClosing(WindowEvent ev) {
        onClose(ev);
      }
    });
  }


  /**
   * Constructs the dialog and readys it for display.
   */
  private void initialize() {
    // Create and place the visual components on the panel.
    assembleGui();


    // Set state of components *prior* to establishing event links.


    // Link observers to panel components for event handling.
    attachHandlers();


    // Set default appearance of the dialog.
    pack();  // Adjust contents to be at, or above preferred sizes.  // TODO:3 Specify preferred, max, and min sizes of components.

    // Simulate panel entry.
    onTabChange(new ChangeEvent(model_pg_));

    if (Mirador.getParser().isOptionPassed("auto_mode")) {
      action_bar_.getOkayButton().doClick();
      onClose(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onClose(WindowEvent ev) {
    switch (previous_idx_) {
      case MERGE_PAGE:
        merge_pg_.onClose(ev);

      case MATCH_PAGE:
        match_pg_.onClose(ev);

      case MODEL_PAGE:
        model_pg_.onClose(ev);

      close();
    }

  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onTabChange(ChangeEvent ev) {
    int current_idx = tab_pane_.getSelectedIndex();

    switch (previous_idx_) {
      case NO_PAGE:  // Coming from outside of wizard, into model input page.
        // Simulate panel entry. Member model_pnl_ is null at this point.
        ((ModelPanel) ev.getSource()).onTabEnter(ev);
        current_idx = MODEL_PAGE;
      break;

      case MODEL_PAGE:
        switch (current_idx) {
          case MATCH_PAGE:
            model_pg_.onTabExit(ev);
            match_pg_.onTabEnter(ev);
          break;

          case MERGE_PAGE:
            model_pg_.onTabExit(ev);
            merge_pg_.onTabEnter(ev);
          break;

          default:
            throw new IndexOutOfBoundsException("Bad switch case");
        }
      break;

      case MATCH_PAGE:
        switch (current_idx) {
          case MODEL_PAGE:
            model_pg_.onTabEnter(ev);
          break;

          case MERGE_PAGE:
            match_pg_.onTabExit(ev);
            merge_pg_.onTabEnter(ev);
          break;

          default:
            throw new IndexOutOfBoundsException("Bad switch case");
        }
      break;

      case MERGE_PAGE:
        switch (current_idx) {
          case MODEL_PAGE:
            model_pg_.onTabEnter(ev);
          break;

          case MATCH_PAGE:
            match_pg_.onTabEnter(ev);
          break;

          default:
            throw new IndexOutOfBoundsException("Bad switch case");
        }
      break;

      default:
        throw new IndexOutOfBoundsException("Bad switch case");
    }

    previous_idx_ = current_idx;
  }


  // Instance data ----------------------------------------------------------
  private MiradorFujabaPlugin fujaba_plugin_;

  private TabActionBar action_bar_;
  private JLabel status_lbl_;
  private JTabbedPane tab_pane_;

  private ModelPanel model_pg_;
  private MatchPanel match_pg_;
  private MergePanel merge_pg_;
  private int previous_idx_ = NO_PAGE;

  private ModelRepository model_repo_;
  private MergeWorks merge_works_;
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  static public final String TITLE = "Mirador Model Merge Tool v";

  static public final int NO_PAGE = -1;
  static public final int MODEL_PAGE = 0;
  static public final int MATCH_PAGE = 1;
  static public final int MERGE_PAGE = 2;

  static private MergeWizard instance_;
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  private class BarCallback implements Callable {
    /**
     * Handles backward button press event.
     *
     * @param  ev  The triggering event.
     */
    @Override public void onBackwardClick(ActionEvent ev) {
      // Move to proceeding dialog panel if not already on first panel.
      int idx = tab_pane_.getSelectedIndex();
      if (idx > 0)
        tab_pane_.setSelectedIndex(idx - 1);
    }


    /**
     * Handles cancel button press event.
     *
     * @param  ev  The triggering event.
     */
    @Override public void onCancelClick(ActionEvent ev) {
      switch (previous_idx_) {
        case MERGE_PAGE:
          merge_pg_.onCancelClick(ev);

        case MATCH_PAGE:
          match_pg_.onCancelClick(ev);

        case MODEL_PAGE:
          model_pg_.onCancelClick(ev);

        onClose(new WindowEvent(instance_, WindowEvent.WINDOW_CLOSING));
      }
    }


    /**
     * Handles forward button press event.
     *
     * @param  ev  The triggering event.
     */
    @Override public void onForwardClick(ActionEvent ev) {
      // Move to succeeding dialog panel if not already on last panel.
      int idx = tab_pane_.getSelectedIndex();
      int cnt = tab_pane_.getTabCount();
      if (idx < cnt - 1)
        tab_pane_.setSelectedIndex(idx + 1);
    }


    /**
     * Handles help button press event.
     *
     * @param  ev  The triggering event.
     */
    @Override public void onHelpClick(ActionEvent ev) {
    // Construct help panel for current panel of wizard.
// TODO:3 Generalize from Fujaba specific help system.
//      WizardPanel current_pnl = (WizardPanel) owner_.getSelectedComponent();
//      HtmlDialog help_dlg = new HtmlDialog(new JFrame(),
//          panelHelpUrl(current_pnl.getHelpName()), current_pnl.getInfo(), true);
//
//      // Set appearance of help panel.
//      help_dlg.getHtmlPanel().setNavigationBarVisible(true);
//      help_dlg.getHtmlPanel().setNavigationBarButtonEnablity(true, true, false);
//      help_dlg.setVisible(true);
    }


    /**
     * Handles okay button press event.
     *
     * @param  ev  The triggering event.
     */
    @Override public void onOkayClick(ActionEvent ev) {
      switch (previous_idx_) {
        case MODEL_PAGE:
          model_pg_.onOkayClick(ev);

        case MATCH_PAGE:
          match_pg_.onOkayClick(ev);

        case MERGE_PAGE:
          merge_pg_.onOkayClick(ev);
      }
    }
  }
  // End nested types -------------------------------------------------------
}

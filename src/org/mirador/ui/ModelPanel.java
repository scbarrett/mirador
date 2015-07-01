/* --------------------------------------------------------------------------+
   ModelPanel.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.InvocationParser;
import ca.dsrg.mirador.Mirador;
import ca.dsrg.mirador.change.emf.EMFModelRepository;
import ca.dsrg.mirador.change.fujaba.FujabaModelRepository;
import ca.dsrg.mirador.match.DependencyEvaluator;
import ca.dsrg.mirador.match.EclEvaluator;
import ca.dsrg.mirador.match.IdEvaluator;
import ca.dsrg.mirador.match.NameEvaluator;
import ca.dsrg.mirador.match.SimilarityEvaluator;
import ca.dsrg.mirador.match.StructureEvaluator;
import ca.dsrg.mirador.merge.MergeWorks;
import ca.dsrg.mirador.model.ModelRepository;
import ca.dsrg.mirador.model.ModelRepository.ModelType;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.4 - Jan 29, 2010
 * @author  Stephen Barrett
 */
public class ModelPanel extends WizardPanel {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  wizard  Purpose of the argument.
   */
  public ModelPanel(MergeWizard wizard) {
    super(wizard, PANEL_INFO, HELP_FILE);
    initialize();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
//  /**                                                                     DOCDO: Provide method overview.
//   *
//   * @return  The field's current value.
//   * @category  getter
//   */
//  public JTextField getBaseFileName() {
//    return model_file_bs_txt_;
//  }


//  /**                                                                     DOCDO: Provide method overview.
//   *
//   * @return  The field's current value.
//   * @category  getter
//   */
//  public JTextField getLeftFileName() {
//    return model_file_lf_txt_;
//  }


//  /**                                                                     DOCDO: Provide method overview.
//   *
//   * @return  The field's current value.
//   * @category  getter
//   */
//  public JTextField getRightFileName() {
//    return model_file_rt_txt_;
//  }


//  /**                                                                     DOCDO: Provide method overview.
//   *
//   * @return  The field's current value.
//   * @category  getter
//   */
//  public JTextField getMatchFileName() {
//    return match_file_txt_;
//  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleDecisionPanel() {
    // Instantiate the components.
    JComponent match_sep = DefaultComponentFactory.getInstance().
        createSeparator("Input Element Matching File",
        SwingConstants.CENTER);

    previous_cbx_ = new JCheckBox("Load previous element matching file");
    previous_file_txt_ = new JTextField();
    previous_dir_btn_ = new JButton(Constants.FILE_OPENED_IMG);


    JComponent table_sep = DefaultComponentFactory.getInstance().
        createSeparator("Input Decision Table Definition Files",
        SwingConstants.CENTER);

    match_cbx_ = new JCheckBox("Load element matching table definition");
    match_file_txt_ = new JTextField();
    match_dir_btn_ = new JButton(Constants.FILE_OPENED_IMG);

    before_cbx_ = new JCheckBox("Load before predicate table definition");
    before_file_txt_ = new JTextField();
    before_dir_btn_ = new JButton(Constants.FILE_OPENED_IMG);

    resolve_cbx_ = new JCheckBox("Load conflict resolution table definition");
    resolve_file_txt_ = new JTextField();
    resolve_dir_btn_ = new JButton(Constants.FILE_OPENED_IMG);


    // Set visual and behavioral aspects of the components.
    previous_cbx_.setMnemonic(KeyEvent.VK_P);
    previous_cbx_.setToolTipText(
        "Check to load previously saved model element matches. (no affect)");
//??    previous_file_txt_.getDocument().putProperty("name", "previous_file");
    previous_file_txt_.setToolTipText(
        "File path to previous element matching file.");
    previous_dir_btn_.setToolTipText("Browse for previous element matching file.");

    match_cbx_.setMnemonic(KeyEvent.VK_T);
    match_cbx_.setToolTipText(
        "Check to load element match table definition.");
//??    match_file_txt_.getDocument().putProperty("name", "match_file");
    match_file_txt_.setToolTipText(
        "File path to element match table definition.");
    match_dir_btn_.setToolTipText("Browse for element match table definition.");

    before_cbx_.setMnemonic(KeyEvent.VK_F);
    before_cbx_.setToolTipText(
        "Check to load conflict detection table definition.");
//??    before_file_txt_.getDocument().putProperty("name", "before_file");
    before_file_txt_.setToolTipText(
        "File path to conflict detection table definition.");
    before_dir_btn_.setToolTipText("Browse for conflict detection table definition.");

    resolve_cbx_.setMnemonic(KeyEvent.VK_S);
    resolve_cbx_.setToolTipText(
        "Check to load conflict resolution table definition.");
//??    resolve_file_txt_.getDocument().putProperty("name", "resolve_file");
    resolve_file_txt_.setToolTipText(
        "File path to conflict resolution table definition.");
    resolve_dir_btn_.setToolTipText("Browse for conflict resolution table definition.");


    // Set panel layout and constraints.
    String col_spec = "pref, $rgap, MAX(90dlu;pref), MIN(70dlu;pref):grow, "
        + "$rgap, pref, pref:grow";
    String row_spec =
          "0dlu , pref, "
        + "$ugap, MIN(13dlu;pref), "
        + "8dlu , pref, "
        +" $ugap, MIN(13dlu;pref), $rgap, MIN(13dlu;pref), "
        + "$rgap, MIN(13dlu;pref)";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(match_sep,          cc.rcw(2, 1, 7));
    builder.add(previous_cbx_,      cc.rc (4, 1));
    builder.add(previous_file_txt_, cc.rcw(4, 3, 2));
    builder.add(previous_dir_btn_,  cc.rc (4, 6));

    builder.add(table_sep,          cc.rcw(6, 1, 7));
    builder.add(match_cbx_,         cc.rc (8, 1));
    builder.add(match_file_txt_,    cc.rcw(8, 3, 2));
    builder.add(match_dir_btn_,     cc.rc (8, 6));
    builder.add(before_cbx_,        cc.rc (10, 1));
    builder.add(before_file_txt_,   cc.rcw(10, 3, 2));
    builder.add(before_dir_btn_,    cc.rc (10, 6));
    builder.add(resolve_cbx_,       cc.rc (12, 1));
    builder.add(resolve_file_txt_,  cc.rcw(12, 3, 2));
    builder.add(resolve_dir_btn_,   cc.rc (12, 6));

    return builder.getPanel();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleModelPanel() {
    // Instantiate the components.
    JComponent file_sep = DefaultComponentFactory.getInstance()
        .createSeparator("Input Model Versions", SwingConstants.CENTER);

    model_file_bs_txt_ = new JTextField();
    model_file_lf_txt_ = new JTextField();
    model_file_rt_txt_ = new JTextField();

    model_dir_bs_btn_ = new JButton(Constants.FILE_OPENED_IMG);
    model_dir_lf_btn_ = new JButton(Constants.FILE_OPENED_IMG);
    model_dir_rt_btn_ = new JButton(Constants.FILE_OPENED_IMG);


    // Set visual and behavioral aspects of the components.
    //model_file_bs_txt_.getDocument().putProperty("name", "common_file");
    model_file_bs_txt_.setToolTipText("File path to common model.");
    model_dir_bs_btn_.setToolTipText("Browse for common model.");

    //model_file_lf_txt_.getDocument().putProperty("name", "left_file");
    model_file_lf_txt_.setToolTipText("File path to left model.");
    model_dir_lf_btn_.setToolTipText("Browse for left model.");

    //model_file_rt_txt_.getDocument().putProperty("name", "right_file");
    model_file_rt_txt_.setToolTipText("File path to right model.");
    model_dir_rt_btn_.setToolTipText("Browse for right model.");


    // Set panel layout and constraints.
    String col_spec = "40dlu, right:pref, $rgap, MAX(155dlu;pref), "
    	+ "MIN(70dlu;pref):grow, $rgap, pref, pref:grow";
    String row_spec =
        "4dlu , pref, "
      + "$ugap, MIN(13dlu;pref), $rgap, MIN(13dlu;pref), $rgap, MIN(13dlu;pref)";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(file_sep,            cc.rcw(2, 1, 8));
    builder.addLabel("&Base model",  cc.rc (4, 2));
    builder.add(model_file_bs_txt_,  cc.rcw(4, 4, 2));
    builder.add(model_dir_bs_btn_,   cc.rc (4, 7));
    builder.addLabel("&Left model",  cc.rc (6, 2));
    builder.add(model_file_lf_txt_,  cc.rcw(6, 4, 2));
    builder.add(model_dir_lf_btn_,   cc.rc (6, 7));
    builder.addLabel("&Right model", cc.rc (8, 2));
    builder.add(model_file_rt_txt_,  cc.rcw(8, 4, 2));
    builder.add(model_dir_rt_btn_,   cc.rc (8, 7));

    return builder.getPanel();
  }


  /**
   * Instantiate the GUI's components, and populate its containers.
   */
  private void assembleGui() {
    // Instantiate the components.
    JPanel model_pnl = assembleModelPanel();
    JPanel match_pnl = assembleMatchPanel();
    JPanel decision_pnl = assembleDecisionPanel();


    // Set panel layout and constraints.
    String col_spec = "pref:grow";
    String row_spec = "pref, 9dlu, pref, 9dlu, pref";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout, this);
    builder.setBorder(Borders.TABBED_DIALOG_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(model_pnl,    cc.rc (1, 1));
    builder.add(match_pnl,    cc.rc (3, 1));
    builder.add(decision_pnl, cc.rc (5, 1));
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private JPanel assembleMatchPanel() {
    // Instantiate the components.
    JComponent strat_sep = DefaultComponentFactory.getInstance().
        createSeparator("Select Model Element Matching Strategies",
        SwingConstants.CENTER);

    strategy1_cbx_ =
        new JCheckBox("By ID matching strategy");
    strategy2_cbx_ =
        new JCheckBox("By Name matching strategy");
    strategy3_cbx_ =
        new JCheckBox("By Structure matching strategy");
    strategy4_cbx_ =
        new JCheckBox("By Dependency matching strategy");
    strategy5_cbx_ =
        new JCheckBox("By ECL script matching strategy");
    strategy6_cbx_ =
        new JCheckBox("User supplied matching strategy #1        Class name");
    strategy7_cbx_ =
        new JCheckBox("User supplied matching strategy #2        Class name");

    user_class1_txt_ = new JTextField();
    user_class2_txt_ = new JTextField();


    // Set visual and behavioral aspects of the components.
    strategy1_cbx_.setMnemonic(KeyEvent.VK_1);
    strategy1_cbx_.setToolTipText(
        "Check to measure similarity with the by ID matching strategy.");
    strategy2_cbx_.setMnemonic(KeyEvent.VK_2);
    strategy2_cbx_.setToolTipText(
        "Check to measure similarity with the by Name matching strategy.");
    strategy3_cbx_.setMnemonic(KeyEvent.VK_3);
    strategy3_cbx_.setToolTipText(
        "Check to measure similarity with the by Structure matching strategy.");
    strategy4_cbx_.setMnemonic(KeyEvent.VK_4);
    strategy4_cbx_.setToolTipText(
        "Check to measure similarity with the by Dependency matching strategy.");
    strategy5_cbx_.setMnemonic(KeyEvent.VK_5);
    strategy5_cbx_.setToolTipText(
        "Check to measure similarity with the by ECL matching strategy.");
    strategy6_cbx_.setMnemonic(KeyEvent.VK_6);
    strategy6_cbx_.setToolTipText(
        "Check to measure similarity with a user defined matching strategy.");
    strategy7_cbx_.setMnemonic(KeyEvent.VK_7);
    strategy7_cbx_.setToolTipText(
        "Check to measure similarity with a user defined matching strategy.");


    // Set panel layout and constraints.
    String col_spec = "pref, $rgap, MAX(70dlu;pref), MIN(70dlu;pref):grow, "
        + "$rgap, pref, pref:grow";
    String row_spec =
          "0dlu , pref, "
        + "$ugap, pref, $rgap, pref, $rgap, pref, $rgap, pref, $rgap, pref, "
        + "$rgap, MIN(13dlu;pref), $rgap, MIN(13dlu;pref)";
    FormLayout layout = new FormLayout(col_spec, row_spec);


    // Initialize builder of the panel with the layout and a border.
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setBorder(Borders.EMPTY_BORDER);


    // Add components to the panel.
    CellConstraints cc = new CellConstraints();
    builder.add(strat_sep,          cc.rcw(2, 1, 7));
    builder.add(strategy1_cbx_,     cc.rc (4, 1));
    builder.add(strategy2_cbx_,     cc.rc (6, 1));
    builder.add(strategy3_cbx_,     cc.rc (8, 1));
    builder.add(strategy4_cbx_,     cc.rc (10, 1));
    builder.add(strategy5_cbx_,     cc.rc (12, 1));
    builder.add(strategy6_cbx_,     cc.rc (14, 1));
    builder.add(user_class1_txt_,   cc.rc (14, 3));
    builder.add(strategy7_cbx_,     cc.rc (16, 1));
    builder.add(user_class2_txt_,   cc.rc (16, 3));

    return builder.getPanel();
  }


  /**
   * Attach event observers and handlers to panel components.
   */
  private void attachHandlers() {
    // Common model file directory button event handler.
    model_dir_bs_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onModelFileClick(ev, model_file_bs_txt_);
      }
    });


    // Left model file directory button event handler.
    model_dir_lf_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onModelFileClick(ev, model_file_lf_txt_);
      }
    });


    // Right model file directory button event handler.
    model_dir_rt_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onModelFileClick(ev, model_file_rt_txt_);
      }
    });


    // Element match file check box event handler.
    previous_cbx_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onPreviousFileCheck(ev);
      }
    });


    // Element match file directory button event handler.
    previous_dir_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onPreviousFileClick(ev, previous_file_txt_);
      }
    });


    // Before predicate definition file check box event handler.
    match_cbx_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onMatchFileCheck(ev);
      }
    });


    // Before predicate definition file directory button event handler.
    match_dir_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onDefinitionFileClick(ev, match_file_txt_);
      }
    });


    // Before predicate definition file check box event handler.
    before_cbx_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onBeforeFileCheck(ev);
      }
    });


    // Before predicate definition file directory button event handler.
    before_dir_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onDefinitionFileClick(ev, before_file_txt_);
      }
    });


    // Conflict predicate definition file check box event handler.
    resolve_cbx_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onResolveFileCheck(ev);
      }
    });


    // Conflict predicate definition file directory button event handler.
    resolve_dir_btn_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onDefinitionFileClick(ev, resolve_file_txt_);
      }
    });


    // First user strategy class check box event handler.
    strategy6_cbx_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onUser1FileCheck(ev);
      }
    });


    // Second user strategy class check box event handler.
    strategy7_cbx_.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent ev) {
        onUser2FileCheck(ev);
      }
    });


    // Switch to left model file name event handler.
    model_file_bs_txt_.getDocument().addDocumentListener(
        new FileFieldListener(model_file_bs_txt_, model_bs_fwp_));

    // Switch to left model file name event handler.
    model_file_lf_txt_.getDocument().addDocumentListener(
        new FileFieldListener(model_file_lf_txt_, model_lf_fwp_));

    // Switch to right model file name event handler.
    model_file_rt_txt_.getDocument().addDocumentListener(
        new FileFieldListener(model_file_rt_txt_, model_rt_fwp_));

    // Switch to previous file name event handler.
    previous_file_txt_.getDocument().addDocumentListener(
        new FileFieldListener(previous_file_txt_, previous_fwp_));

    // Switch to before predicate file name event handler.
    match_file_txt_.getDocument().addDocumentListener(
        new FileFieldListener(match_file_txt_, match_fwp_));

    // Switch to before predicate file name event handler.
    before_file_txt_.getDocument().addDocumentListener(
        new FileFieldListener(before_file_txt_, before_fwp_));

    // Switch to conflict resolution file name event handler.
    resolve_file_txt_.getDocument().addDocumentListener(
        new FileFieldListener(resolve_file_txt_, resolve_fwp_));
  }


  /**
   * Constructs the panel and readys it for display.
   */
  private void initialize() {
    // Create and place the visual components on the panel.
    assembleGui();


    // Set state of components *prior* to establishing event links.
    previous_cbx_.setSelected(false);
    previous_file_txt_.setEnabled(false);

    before_cbx_.setSelected(false);
    before_file_txt_.setEnabled(false);

    resolve_cbx_.setSelected(false);
    resolve_file_txt_.setEnabled(false);

    strategy1_cbx_.setSelected(true);
    strategy1_cbx_.setEnabled(false);
    strategy2_cbx_.setSelected(true);
    strategy3_cbx_.setSelected(true);
    strategy4_cbx_.setSelected(true);
    strategy5_cbx_.setSelected(true);
    strategy6_cbx_.setSelected(true);
    strategy7_cbx_.setSelected(true);


    // Link observers to panel components for event handling.
    attachHandlers();
  }


  private void initializeEvaluators() {
    MergeWorks works = wizard_.getMergeWorks();
    InvocationParser parser = Mirador.getParser();
    float weight;

    if (strategy1_cbx_.isSelected()) {
      weight = new Float((parser.optionValue("by_id") != null)
          ? parser.optionValue("by_id") : "0.0");
      works.addEvaluator(new IdEvaluator(weight));
    }

    if (strategy2_cbx_.isSelected()) {
      weight = new Float((parser.optionValue("by_name") != null)
          ? parser.optionValue("by_name") : "0.0");
      works.addEvaluator(new NameEvaluator(weight));
    }

    if (strategy3_cbx_.isSelected()) {
      weight = new Float((parser.optionValue("by_structure") != null)
          ? parser.optionValue("by_structure") : "0.0");
      works.addEvaluator(new StructureEvaluator(weight));
    }

    if (strategy4_cbx_.isSelected()) {
      weight = new Float((parser.optionValue("by_dependency") != null)
          ? parser.optionValue("by_dependency") : "0.0");
      works.addEvaluator(new DependencyEvaluator(weight));
    }

    if (strategy5_cbx_.isSelected()) {
      weight = new Float((parser.optionValue("by_ecl") != null)
          ? parser.optionValue("by_ecl") : "0.0");
      works.addEvaluator(new EclEvaluator(weight));
    }

    if (strategy6_cbx_.isSelected()) {
//      weight = new Float((parser.optionValue("by_user1") != null)
//          ? parser.optionValue("by_user1") : "0.0");
      String eval_name = user_class1_txt_.getText();

      try {
        SimilarityEvaluator eval_obj = (SimilarityEvaluator)
            Class.forName(eval_name).newInstance();

        works.addEvaluator(eval_obj);
      }
      catch (Exception ex) {
        System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
        ex.printStackTrace();
      }
    }

    if (strategy7_cbx_.isSelected()) {
//      weight = new Float((parser.optionValue("by_user2") != null)
//          ? parser.optionValue("by_user2") : "0.0");
      String eval_name = user_class2_txt_.getText();

      try {
        SimilarityEvaluator eval_obj = (SimilarityEvaluator)
            Class.forName(eval_name).newInstance();

        works.addEvaluator(eval_obj);
      }
      catch (Exception ex) {
        System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
        ex.printStackTrace();
      }
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onBeforeFileCheck(ActionEvent ev) {
    before_file_txt_.setEnabled(before_cbx_.isSelected());
    before_dir_btn_.setEnabled(before_cbx_.isSelected());
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
  void onDefinitionFileClick(ActionEvent ev, JTextField file_name_txt) {
    JFileChooser chooser = new JFileChooser(".");
    chooser.setFileFilter(
        new FileNameExtensionFilter("Decision table definition files", "ddf"));

    String file_name = file_name_txt.getText();
    File file = new File(file_name);
    chooser.setSelectedFile(file);

    boolean rc;
    do {
      rc = true;
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        file = chooser.getSelectedFile();

        if (file.exists())
          file_name_txt.setText(file.getPath());
        else {
          JOptionPane.showMessageDialog(this, "Select an existing model.",
              "Input Error", JOptionPane.ERROR_MESSAGE);
          rc = false;
        }
      }
    } while (rc == false);
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onMatchFileCheck(ActionEvent ev) {
    match_file_txt_.setEnabled(match_cbx_.isSelected());
    match_dir_btn_.setEnabled(match_cbx_.isSelected());
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onModelFileClick(ActionEvent ev, JTextField file_name_txt) {
    JFileChooser chooser = new JFileChooser(".");
    chooser.setFileFilter(  // FIXME:2 Read in all model types!
        new FileNameExtensionFilter("EMF model", "ecore"));
    chooser.setFileFilter(
        new FileNameExtensionFilter("Fujaba model", "ctr"));
    chooser.setAcceptAllFileFilterUsed(true);


    String file_name = file_name_txt.getText();
    File file = new File(file_name);
    chooser.setSelectedFile(file);


    boolean rc;
    do {
      rc = true;
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        file = chooser.getSelectedFile();

        if (file.exists())
          file_name_txt.setText(file.getPath());
        else {
          JOptionPane.showMessageDialog(this, "Select an existing model.",
              "Input Error", JOptionPane.ERROR_MESSAGE);
          rc = false;
        }
      }
    } while (rc == false);
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
  void onPreviousFileCheck(ActionEvent ev) {
    previous_file_txt_.setEnabled(previous_cbx_.isSelected());
    previous_dir_btn_.setEnabled(previous_cbx_.isSelected());
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onPreviousFileClick(ActionEvent ev, JTextField file_name_txt) {
    JFileChooser chooser = new JFileChooser(".");
    chooser.setFileFilter(
        new FileNameExtensionFilter("Mirador element match files", "elm"));

    String file_name = file_name_txt.getText();
    File file = new File(file_name);
    chooser.setSelectedFile(file);

    boolean rc;
    do {
      rc = true;
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        file = chooser.getSelectedFile();

        if (file.exists())
          file_name_txt.setText(file.getPath());
        else {
          JOptionPane.showMessageDialog(this, "Select an existing file.",
              "Input Error", JOptionPane.ERROR_MESSAGE);
          rc = false;
        }
      }
    } while (rc == false);
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onResolveFileCheck(ActionEvent ev) {
    resolve_file_txt_.setEnabled(resolve_cbx_.isSelected());
    resolve_dir_btn_.setEnabled(resolve_cbx_.isSelected());
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onTabEnter(ChangeEvent ev) {
    InvocationParser parser = Mirador.getParser();

    model_file_bs_txt_.setText(parser.argumentValue("base_model"));
    model_file_lf_txt_.setText(parser.argumentValue("left_model"));
    model_file_rt_txt_.setText(parser.argumentValue("right_model"));


    previous_cbx_.setSelected(parser.isOptionPassed("previous_file"));
    onPreviousFileCheck(null);  // Simulate change of match input file.
    previous_file_txt_.setText(parser.optionValue("previous_file"));

    match_cbx_.setSelected(parser.isOptionPassed("match_table"));
    onMatchFileCheck(null);  // Simulate change of match input file.
    match_file_txt_.setText(parser.optionValue("match_table"));

    before_cbx_.setSelected(parser.isOptionPassed("conflict_table"));
    onBeforeFileCheck(null);  // Simulate change of match input file.
    before_file_txt_.setText(parser.optionValue("conflict_table"));

    resolve_cbx_.setSelected(parser.isOptionPassed("resolve_table"));
    onResolveFileCheck(null);  // Simulate change of match input file.
    resolve_file_txt_.setText(parser.optionValue("resolve_table"));

    strategy1_cbx_.setSelected(true);
    strategy2_cbx_.setSelected(parser.isOptionPassed("by_name"));
    strategy3_cbx_.setSelected(parser.isOptionPassed("by_structure"));
    strategy4_cbx_.setSelected(parser.isOptionPassed("by_dependency"));
    strategy5_cbx_.setSelected(parser.isOptionPassed("by_ecl"));
    strategy6_cbx_.setSelected(parser.isOptionPassed("by_user1"));
    onUser1FileCheck(null);  // Simulate change of first user strategy class.
    strategy7_cbx_.setSelected(parser.isOptionPassed("by_user2"));
    onUser2FileCheck(null);  // Simulate change of second user strategy class.

    user_class1_txt_.setText(parser.optionValue("by_user1"));
    user_class2_txt_.setText(parser.optionValue("by_user2"));

    updateNavigation();  // Synchronize panel buttons with current GUI state.


    // Finalize state of GUI and event handlers.
    wizard_.getStatusBar().setText(PANEL_INFO);  // Add panel status.
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onTabExit(ChangeEvent ev) {
    ModelRepository model_repo = wizard_.getModelRepository();

    // Test for a new merging session, or a change in model files.
    if (model_repo == null
        || model_repo.getFileBase() != model_bs_fwp_.file
        || model_repo.getFileLeft() != model_lf_fwp_.file
        || model_repo.getFileRight() != model_rt_fwp_.file) {

      // Parse valid model files for match panel use.
      switch(model_lf_fwp_.type) {
        case EMF:
          EMFModelRepository e_repo =
              new EMFModelRepository(model_bs_fwp_.file, model_lf_fwp_.file,
                  model_rt_fwp_.file);
          wizard_.setModelRepository(e_repo);
          model_repo = e_repo;
        break;

        case FUJABA:
          FujabaModelRepository f_repo =
              new FujabaModelRepository(model_bs_fwp_.file, model_lf_fwp_.file,
                  model_rt_fwp_.file);
          wizard_.setModelRepository(f_repo);
          model_repo = f_repo;
        break;
      }
    }

    // Extract differences of state between base model and modified replicas.
    model_repo.buildDifferenceModels();

    // Pass on various data input files to the matching panel.
    MergeWorks works = wizard_.getMergeWorks();
    works.setElementMatchFile(previous_fwp_.file);
    works.setBeforeFile(before_fwp_.file);
    works.setResolveTableFile(resolve_fwp_.file);

    // Capture matching strategy selections to be employed by matching panel.
    initializeEvaluators();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onUser1FileCheck(ActionEvent ev) {
    user_class1_txt_.setEnabled(strategy6_cbx_.isSelected());
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ev  The triggering event.
   */
  void onUser2FileCheck(ActionEvent ev) {
    user_class2_txt_.setEnabled(strategy7_cbx_.isSelected());
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private void updateNavigation() {
    TabActionBar action_bar = wizard_.getActionBar();
    action_bar.disableBackwardButton();
    action_bar.enableOkayButton();

    if (model_bs_fwp_.file != null && model_lf_fwp_.file != null
        && model_rt_fwp_.file != null
        && model_bs_fwp_.type == model_lf_fwp_.type
        && model_lf_fwp_.type == model_rt_fwp_.type)
      action_bar.enableForwardButton();
    else
      action_bar.disableForwardButton();
  }


  // Instance data ----------------------------------------------------------
  private JTextField model_file_bs_txt_;
  private JTextField model_file_lf_txt_;
  private JTextField model_file_rt_txt_;
  private JTextField previous_file_txt_;
  private JTextField match_file_txt_;
  private JTextField before_file_txt_;
  private JTextField resolve_file_txt_;

  private JButton model_dir_bs_btn_;
  private JButton model_dir_lf_btn_;
  private JButton model_dir_rt_btn_;
  private JButton previous_dir_btn_;
  private JButton match_dir_btn_;
  private JButton before_dir_btn_;
  private JButton resolve_dir_btn_;

  private JCheckBox previous_cbx_;
  private JCheckBox match_cbx_;
  private JCheckBox before_cbx_;
  private JCheckBox resolve_cbx_;
  private JCheckBox strategy1_cbx_;
  private JCheckBox strategy2_cbx_;
  private JCheckBox strategy3_cbx_;
  private JCheckBox strategy4_cbx_;
  private JCheckBox strategy5_cbx_;
  private JCheckBox strategy6_cbx_;
  private JCheckBox strategy7_cbx_;

  private JTextField user_class1_txt_;
  private JTextField user_class2_txt_;

  /** Model input files, wrapped to allow modification by panel events. */
  private FileWrapper model_bs_fwp_ = new FileWrapper();
  private FileWrapper model_lf_fwp_ = new FileWrapper();
  private FileWrapper model_rt_fwp_ = new FileWrapper();
  private FileWrapper previous_fwp_ = new FileWrapper();
  private FileWrapper match_fwp_ = new FileWrapper();
  private FileWrapper before_fwp_ = new FileWrapper();
  private FileWrapper resolve_fwp_ = new FileWrapper();
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  static private final String HELP_FILE = "model-input.html";
  static private final String PANEL_INFO =
      "Use this pane to input models, and set merge options.";
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**                                                                     DOCDO: Provide class overview.
   *
   * @since v0.4 - Jan 29, 2010
   * @author Stephen Barrett
   */
  class FileFieldListener implements DocumentListener {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    /**                                                                   DOCDO: Provide constructor overview.
     *
     * @param  file_name_txt  Purpose of the argument.
     */
    public FileFieldListener(JTextField file_name_txt, FileWrapper file_wpr) {
      file_name_txt_ = file_name_txt;
      file_wpr_ = file_wpr;
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void changedUpdate(DocumentEvent ev) {
      setExistingFileName(ev);
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void insertUpdate(DocumentEvent ev) {
      setExistingFileName(ev);
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  ev  The triggering event.
     */
    @Override public void removeUpdate(DocumentEvent ev) {
      setExistingFileName(ev);
    }


    /**
     * Tests for the existence of a file corresponding to the entered file name.
     *
     * @param  ev  The triggering event.
     */
    private void setExistingFileName(DocumentEvent ev) {
      if (!file_name_txt_.isEnabled())  // Guard clause.
        return;

      File tmp_file = new File(file_name_txt_.getText());

      if (tmp_file.exists()) {
        file_wpr_.file = tmp_file;
        // Take extension from File object to eliminate path for consideration.
        String file_ext = getFileExtension(tmp_file.getName());

        if (file_ext.equals("ctr"))
          file_wpr_.type = ModelType.FUJABA;
        else if (file_ext.equals("ecore"))
          file_wpr_.type = ModelType.EMF;
        else if (file_ext.equals("elm") || file_ext.equals("ddf"))
          ;  // Not a model. Do nothing.
        else
          file_wpr_.type = ModelType.UNKNOWN;
      }
      else {
        file_wpr_.file = null;
        file_wpr_.type = ModelType.UNKNOWN;
      }

      updateNavigation();
    }


    private String getFileExtension(String file_name) {
      int pos = file_name.lastIndexOf('.');
      String file_ext = (pos > 0) ? file_name.substring(pos + 1) : "";
      return file_ext.toLowerCase();
    }


    // Instance data --------------------------------------------------------
    private JTextField file_name_txt_;
    private FileWrapper file_wpr_;
    // End instance data ----------------------------------------------------
  }


  /**
   * Wrapper for the model input files to allow panel event modification.
   *
   * Events may now alter the File object's referenced file in the manner of
   * pointers, without creating a new File object.
   *
   * @since   v0.21 - Mar 26, 2010
   * @author  Stephen Barrett
   */
  class FileWrapper {
    // Instance data --------------------------------------------------------
    public File file;
    public ModelType type = ModelType.UNKNOWN;
    // End instance data ----------------------------------------------------
  }
  // End nested types -------------------------------------------------------
}

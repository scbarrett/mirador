/* --------------------------------------------------------------------------+
   WizardPanel.java - Abstract basis for panels of the merge wizard dialog.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import javax.swing.JPanel;


/**
 * Abstract merge wizard panel with default behavior regarding panel information
 * and help.
 *
 * @since   v0.2 - Jan 20, 2010
 * @author  Stephen Barrett
 */
abstract public class WizardPanel extends JPanel {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public WizardPanel(MergeWizard wizard, String panel_info, String help_name) {
    wizard_ = wizard;
    panel_info_ = panel_info;
    help_name_ = help_name;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives name of panel's help file.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public String getHelpName() {
    return help_name_;
  }


  /**
   * Gives panel's information string
   *
   * @return  The field's current value.
   * @category  getter
   */
  public String getInfo() {
    return panel_info_;
  }


  // Instance data ----------------------------------------------------------
  /** The Mirador model merging wizard this panel belongs to. */
  protected MergeWizard wizard_;

  /** Name of panel's help file. */
  protected String help_name_;

  /** Panel's information string. */
  protected String panel_info_;
  // End instance data ------------------------------------------------------
}

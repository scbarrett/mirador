/* --------------------------------------------------------------------------+
   AboutBox.java - Help about box for the Mirador Model Merging plug-in.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import ca.dsrg.mirador.Constants;
import de.uni_paderborn.fujaba.app.ScrollPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * A modal help about box for the Mirador Model Merging tool when running as a
 * Fujaba plug-in. A self-scrolling pane displays the application credits.
 *
 * @since   v0.1 - Jan 19, 2010
 * @author  Stephen Barrett
 */
public class AboutBox extends JDialog {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Builds and displays the about box.
   *
   * @param  owner  Owning frame which controls the dialog's lifetime.
   */
  public AboutBox(JFrame owner) {
    super(owner, MergeWizard.TITLE, true);
    initialize();
    setVisible(true);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Instantiate the GUI's components, and populate its containers.
   */
  private void assembleGui() {
    // Instantiate the components.
    JLabel about_lbl = new JLabel(Constants.SPLASH_IMG);
    ScrollPanel about_scl = new ScrollPanel(Constants.SPLASH_IMG.getIconWidth(),
        90, 20, Constants.WHO_DOC);

    // Place components.
    add(about_lbl, "Center");
    add(about_scl, "South");
  }


  /**
   * Constructs the panel and readys it for display.
   */
  private void initialize() {
    // Create and place the visual components on the panel.
    assembleGui();

    // Set default appearance of the dialog.
    pack();  // Contents at, or above preferred sizes.
    setResizable(false);
    setLocationRelativeTo(this.getOwner());  // Center in Fujaba's frame.
  }
}

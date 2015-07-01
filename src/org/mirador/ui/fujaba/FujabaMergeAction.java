/* --------------------------------------------------------------------------+
   FujabaMergeAction.java - Listener that responds to Fujaba Merge Model event.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Class is linked to the event through action entries in stable.xml.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui.fujaba;
import ca.dsrg.mirador.ui.MergeWizard;
import de.uni_paderborn.fujaba.app.FrameMain;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


/**
 * Action listener to handle Fujaba events regarding Mirador model merging.<p>
 *
 * Class is linked to events through action entries in the stable.xml plug-in
 * project file.
 *
 * @since   v0.1 - Jan 19, 2010
 * @author  Stephen Barrett
 */
public class FujabaMergeAction extends AbstractAction {
  /**
   * Launches the Mirador model merger in response to a Fujaba user action.
   *
   * @param  ev  Details of the initiating event.
   */
  @Override public void actionPerformed(ActionEvent ev) {
    // Create, center, and show the Mirador wizard.
    MergeWizard wizard = MergeWizard.get();
    wizard.setLocationRelativeTo(FrameMain.get());
    wizard.setVisible(true);
    wizard.toFront();
  }
}

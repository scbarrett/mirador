/* --------------------------------------------------------------------------+
   AboutBoxAction.java - Listener that responds to Fujaba About Mirador event.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Class is linked to the event through action entries in stable.xml.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui.fujaba;
import ca.dsrg.mirador.ui.AboutBox;
import de.uni_paderborn.fujaba.app.FrameMain;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;


/**
 * Action listener to handle Fujaba events regarding the Mirador about box.<p>
 *
 * Class is linked to events through action entries in the stable.xml plug-in
 * project file.
 *
 * @since   v0.1 - Jan 19, 2010
 * @author  Stephen Barrett
 */
public class AboutBoxAction extends AbstractAction {
  /**
   * Shows the Mirador about box in response to a Fujaba user action.
   *
   * @param  ev  Details of the initiating event.
   */
  @Override public void actionPerformed(ActionEvent ev) {
    // Display the Mirador wizard about box in the Fujaba application.
    new AboutBox(((JFrame) FrameMain.get().getFrame()));
  }
}

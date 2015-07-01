/* --------------------------------------------------------------------------+
   MiradorFujabaPlugin.java - Class to wrap the Mirador model merging tool
     in order to run it as a Fujaba plug-in.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Though intended as a plug-in, Mirador may also be run in stand-alone mode.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;
import ca.dsrg.mirador.ui.MergeWizard;
import de.uni_paderborn.fujaba.app.FrameMain;
import de.upb.lib.plugins.AbstractPlugin;


/**
 * Wrapper for the model merging wizard in order to use as a Fujaba plug-in.
 * Constructed by Fujaba on its start-up.
 *
 * @since   v0.1 - Jan 19, 2010
 * @author  Stephen Barrett
 * @see  MergeWizard
 */
public class MiradorFujabaPlugin extends AbstractPlugin {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Establishes a reciprocal link between plug-in and model merging wizard.
   *
   * @param  wizard  Mirador model merging wizard to link with.
   * @return  true = links established, false = links not established
   */
  public boolean setWizard(MergeWizard wizard) {
    boolean rc = false;

    // Remove old linkage, and establish new; in both directions.
    if (wizard_ != wizard) { // Exit if link is in place.
      if (wizard_ != null)
        wizard_.setFujabaPlugin(null);

      wizard_ = wizard;

      if (wizard != null)
        wizard.setFujabaPlugin(this);

      rc = true;
    }

    return rc;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Associates the plug-in with a merge wizard. A reciprocal setter in the
   * wizard creates a reverse link. Called by Fujaba after construction.
   *
   * @return  true = success, false = failure
   */
  @Override public boolean initialize() {
    boolean rc;

    try {
      // Create a merge wizard (through "get()"), then link with this plug-in.
      setWizard(MergeWizard.get(FrameMain.get(), getInstallationPath() + '/'));
      //registerPluginPreferenceStore(MiradorPreferences.get());  // TODO:3 Add preferences to plug-in.

      rc = true;
    }
    catch (Exception ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      rc = false;
    }

    return rc;
  }


  // Class data -------------------------------------------------------------
  /** The Mirador model merging wizard. */
  static private MergeWizard wizard_;
  // End class data ---------------------------------------------------------
}

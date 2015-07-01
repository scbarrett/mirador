/* --------------------------------------------------------------------------+
   TestFujabaPluginLaunch.java - Unit tests for the launching of Mirador as a
     Fujaba plugin.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Upon start-up, Fujaba conducts some handshaking with its plug-ins, which is
   the target of this test class.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ca.dsrg.mirador.MiradorFujabaPlugin;
import ca.dsrg.mirador.ui.MergeWizard;
import org.junit.Before;
import org.junit.Test;
import javax.swing.JFileChooser;


/**
 * Tests the linkage established at Fujaba start-up between Fujaba and the
 * Mirador plug-in.<p>
 *
 * The only difference between plug-in and stand-alone modes is the path to the
 * plug-in directory: in the former case it is given as an absolute path, while
 * in the latter it is given as relative.
 *
 * @since   v0.12 - Feb 25, 2010
 * @author  Stephen Barrett
 * @see  MergeWizard
 */
public class TestFujabaPluginLaunch {
  /**
   * Obtains the path to the plug-in prior to each test case, in order to
   * simulate its being passed in by Fujaba to class <b>MergeWizard</b>.
   */
  @Before public void setUp() {
    path_ = "file:"
        + (new JFileChooser(".")).getCurrentDirectory().toString();
  }

  /**
   * Asserts creation of the wizard, and linkage with its plug-in wrapper.
   */
  @Test public final void testPluginLaunch() {
    MiradorFujabaPlugin plugin = new MiradorFujabaPlugin();

    plugin.setInstallationPath(path_);
    assertTrue("\nWrapping a new wizard as a plug-in", plugin.initialize());

    MergeWizard wizard = (MergeWizard) FieldAccessor.getField(plugin, "wizard_");
    assertNotNull("\nInspecting wizard creation", wizard);
    assertTrue("\nInspecting wizard linkage", wizard.getFugabaPlugin() == plugin);
  }


  // Instance data ----------------------------------------------------------
  /** Simulated invocation parameter to the plug-in as passed in by Fujaba. */
  private String path_;
  // End instance data ------------------------------------------------------
}

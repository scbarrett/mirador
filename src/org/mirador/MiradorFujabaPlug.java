/* --------------------------------------------------------------------------+
   MiradorFujabaPlug.java - Launches Fujaba from which the Mirador model
     merging tool may be run as a plug-in.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   This is *not* a general purpose launcher.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;
import ca.dsrg.mirador.ui.MergeWizard;


/**
 * Development convenience class for launching Fujaba in a Java process.<p>
 *
 * <i>Not</i> general purpose &#8211; the jar file to execute, and the relative
 * path to it are both hard coded here.
 *
 * @since   v0.3 - Jan 22, 2010
 * @author  Stephen Barrett
 * @see  MergeWizard
 */
public class MiradorFujabaPlug {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Program entry point. Command line arguments are ignored by Fujaba.
   *
   * @param  args  Command line arguments as strings. Ignored.
   */
  static public void main(String[] args) throws Exception {
    System.out.println("Running the Mirador Model Merger inside of Fujaba.");

    // Start Fujaba with assertions enabled.
    Process p = Runtime.getRuntime().exec("java -ea -mx512m -jar lib/fujaba.jar");
    p.waitFor();

    System.out.println("Fujaba completed execution.");
  }
}

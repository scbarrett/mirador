/* --------------------------------------------------------------------------+
   Mirador.java - Launches the Mirador model merging tool as a stand-alone app.

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
import java.text.ParseException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 * Development convenience class for launching Mirador in its own thread as a
 * stand-alone application.
 *
 * <i>Not</i> a general purpose. However, could easily be converted into the
 * stand-alone launcher.
 *
 * @since   v0.1 - Jan 19, 2010
 * @author  Stephen Barrett
 * @see  MergeWizard
 */
public class Mirador {
  /**
   * Program entry point. Parses the command line and invokes the wizard with
   * the passed arguments.
   *
   * @param  args  Command line arguments as strings: <tt>[-d]</tt>
   * @see  MergeWizard
   */
  static public void main(String[] args) {
    // Parse the invocation command's options and arguments.
    try {
      parser_ = new InvocationParser(args);
      System.out.println("Running the Mirador Model Merger stand alone.");
    }
    catch (ParseException ex) {
      System.exit(Constants.EXIT_FAILURE);
    }
    catch (Exception ex) {
      String msg;

      if (ex.getMessage() != null)
        msg = "\n\tUnanticipated error: " + ex.getMessage();
      else
        msg = "\n\tException thrown by: " + ex.getClass();

      System.err.println(msg);
      System.err.println("\n\t!!! Invocation failed. !!!\n");
      System.exit(Constants.EXIT_FAILURE);
    }


    // Set all flags and variables called for by options.
    actualizeOptions();


    // Queue runnable object for asynchronous dispatch.
    SwingUtilities.invokeLater(new Runnable() {
      /**
       * Closure made here in a non-UI thread is queued for later starting as a
       * Swing UI thread that then invokes the Mirador wizard GUI.
       */
      @Override public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
          System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
          ex.printStackTrace();
        }

        // Create with "get()", then position and show the Mirador wizard.
        MergeWizard.get().setVisible(!is_auto_);  // TODO:3 Place according as preference - MergeWizard.get().setLocationRelativeTo(null);
      }
    });
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  static public InvocationParser getParser() {
    return parser_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  static public boolean isAutoMode() {
    return is_auto_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  static private void actualizeOptions() {
    // Check for debug mode option.
    if (parser_.isOptionPassed("debug_mode"))
      Debug.dbg.setDebug();
    else
      Debug.dbg.resetDebug();

    // Check for automatic operation mode option.
    is_auto_ = (parser_.isOptionPassed("auto_mode"));
  }


  // Class data -------------------------------------------------------------
  static private InvocationParser parser_;
  static private boolean is_auto_;
  // End class data ---------------------------------------------------------
}

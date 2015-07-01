/* --------------------------------------------------------------------------+
   TestConstants.java - Unit tests for the start-up initialization of Mirador's
     application-wide constants.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Only plug-in mode is tested, since it is not possible to undo initialization
   of the constants once the Constants.Initializer() constructor has been run.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import static org.junit.Assert.assertEquals;
import ca.dsrg.mirador.Constants;
import ca.dsrg.mirador.ui.MergeWizard;
import org.junit.Before;
import org.junit.Test;
import javax.swing.JFileChooser;


/**
 * Tests the initialization of Mirador's application-wide constants as done
 * when running as a Fujaba plug-in.<p>
 *
 * In plug-in mode, Fujaba passes the wizard's location to class
 * <b>MergeWizard</b> which then assigns the parameter to the static variable
 * <i>plugin_path</i> in class <b>Constants.Initializer</b>.<p>
 *
 * @since   v0.12 - Feb 25, 2010
 * @author  Stephen Barrett
 * @see  MergeWizard
 */
public class TestConstants {
  /**
   * Obtains the path to the plug-in prior to each test case, in order to
   * simulate its being passed in by Fujaba to class <b>MergeWizard</b>.<p>
   *
   * The first access of a member of class <b>Constants</b> forces the running
   * of <i>Constants.Initializer()</i> which uses this value to dynamically
   * initialize all the <tt>static final</tt> members of the class.
   */
  @Before public void setUp() {
    path_ = "file:"
        + (new JFileChooser(".")).getCurrentDirectory().toString() + '/';
    Constants.Initializer.plugin_path_ = path_;
  }

  /**
   * Asserts the initialized values of the URL constants.
   */
  @Test public final void testUrls() {
    assertEquals("\nInitializing BASE_URL",
        path_,
        Constants.BASE_URL.toString());
    assertEquals("\nInitializing HELP_URL",
        path_ + "doc/html/",
        Constants.HELP_URL.toString());
    assertEquals("\nInitializing IMAGE_URL",
        path_ + "img/",
        Constants.IMAGE_URL.toString());
    assertEquals("\nInitializing SCRIPT_URL",
        path_ + "doc/script/",
        Constants.SCRIPT_URL.toString());
    assertEquals("\nInitializing WHO_DOC",
        path_ + "doc/script/mirador.who",
        Constants.WHO_DOC.toString());
  }

  /**
   * Asserts the initialized values of the image file constants.
   */
  @Test public final void testImages() {
    assertEquals("\nInitializing ATTRIBUTE_CLOSED_IMG",
        Constants.IMAGE_URL + "attribute_closed.gif",
        Constants.ATTRIBUTE_CLOSED_IMG.toString());
    assertEquals("\nInitializing ATTRIBUTE_OPENED_IMG",
        Constants.IMAGE_URL + "attribute_opened.gif",
        Constants.ATTRIBUTE_OPENED_IMG.toString());
    assertEquals("\nInitializing ATTRIBUTE_LEAF_IMG",
        Constants.IMAGE_URL + "attribute_leaf.gif",
        Constants.ATTRIBUTE_LEAF_IMG.toString());
    assertEquals("\nInitializing BACKWARD_IMG",
        Constants.IMAGE_URL + "backward.png",
        Constants.BACKWARD_IMG.toString());
    assertEquals("\nInitializing CANCEL_IMG",
        Constants.IMAGE_URL + "cancel.png",
        Constants.CANCEL_IMG.toString());
    assertEquals("\nInitializing CLASS_CLOSED_IMG",
        Constants.IMAGE_URL + "class_closed.gif",
        Constants.CLASS_CLOSED_IMG.toString());
    assertEquals("\nInitializing CLASS_OPENED_IMG",
        Constants.IMAGE_URL + "class_opened.gif",
        Constants.CLASS_OPENED_IMG.toString());
    assertEquals("\nInitializing CLASS_LEAF_IMG",
        Constants.IMAGE_URL + "class_leaf.gif",
        Constants.CLASS_LEAF_IMG.toString());
    assertEquals("\nInitializing DATATYPE_CLOSED_IMG",
        Constants.IMAGE_URL + "datatype_closed.gif",
        Constants.DATATYPE_CLOSED_IMG.toString());
    assertEquals("\nInitializing DATATYPE_OPENED_IMG",
        Constants.IMAGE_URL + "datatype_opened.gif",
        Constants.DATATYPE_OPENED_IMG.toString());
    assertEquals("\nInitializing DATATYPE_LEAF_IMG",
        Constants.IMAGE_URL + "datatype_leaf.gif",
        Constants.DATATYPE_LEAF_IMG.toString());
    assertEquals("\nInitializing FILE_OPENED_IMG",
        Constants.IMAGE_URL + "file_opened.png",
        Constants.FILE_OPENED_IMG.toString());
    assertEquals("\nInitializing FORWARD_IMG",
        Constants.IMAGE_URL + "forward.png",
        Constants.FORWARD_IMG.toString());
    assertEquals("\nInitializing GENERALIZATION_CLOSED_IMG",
        Constants.IMAGE_URL + "generalization_closed.gif",
        Constants.GENERALIZATION_CLOSED_IMG.toString());
    assertEquals("\nInitializing GENERALIZATION_OPENED_IMG",
        Constants.IMAGE_URL + "generalization_opened.gif",
        Constants.GENERALIZATION_OPENED_IMG.toString());
    assertEquals("\nInitializing GENERALIZATION_LEAF_IMG",
        Constants.IMAGE_URL + "generalization_leaf.gif",
        Constants.GENERALIZATION_LEAF_IMG.toString());
    assertEquals("\nInitializing HELP_IMG",
        Constants.IMAGE_URL + "help.png",
        Constants.HELP_IMG.toString());
    assertEquals("\nInitializing OKAY_IMG",
        Constants.IMAGE_URL + "okay.png",
        Constants.OKAY_IMG.toString());
    assertEquals("\nInitializing OPERATION_CLOSED_IMG",
        Constants.IMAGE_URL + "operation_closed.gif",
        Constants.OPERATION_CLOSED_IMG.toString());
    assertEquals("\nInitializing OPERATION_OPENED_IMG",
        Constants.IMAGE_URL + "operation_opened.gif",
        Constants.OPERATION_OPENED_IMG.toString());
    assertEquals("\nInitializing OPERATION_LEAF_IMG",
        Constants.IMAGE_URL + "operation_leaf.gif",
        Constants.OPERATION_LEAF_IMG.toString());
    assertEquals("\nInitializing PACKAGE_CLOSED_IMG",
        Constants.IMAGE_URL + "package_closed.gif",
        Constants.PACKAGE_CLOSED_IMG.toString());
    assertEquals("\nInitializing PACKAGE_OPENED_IMG",
        Constants.IMAGE_URL + "package_opened.gif",
        Constants.PACKAGE_OPENED_IMG.toString());
    assertEquals("\nInitializing PACKAGE_LEAF_IMG",
        Constants.IMAGE_URL + "package_leaf.gif",
        Constants.PACKAGE_LEAF_IMG.toString());
    assertEquals("\nInitializing PARAMETER_CLOSED_IMG",
        Constants.IMAGE_URL + "parameter_closed.gif",
        Constants.PARAMETER_CLOSED_IMG.toString());
    assertEquals("\nInitializing PARAMETER_OPENED_IMG",
        Constants.IMAGE_URL + "parameter_opened.gif",
        Constants.PARAMETER_OPENED_IMG.toString());
    assertEquals("\nInitializing PARAMETER_LEAF_IMG",
        Constants.IMAGE_URL + "parameter_leaf.gif",
        Constants.PARAMETER_LEAF_IMG.toString());
    assertEquals("\nInitializing PROJECT_CLOSED_IMG",
        Constants.IMAGE_URL + "project_closed.png",
        Constants.PROJECT_CLOSED_IMG.toString());
    assertEquals("\nInitializing PROJECT_OPENED_IMG",
        Constants.IMAGE_URL + "project_opened.png",
        Constants.PROJECT_OPENED_IMG.toString());
    assertEquals("\nInitializing PROJECT_LEAF_IMG",
        Constants.IMAGE_URL + "project_leaf.png",
        Constants.PROJECT_LEAF_IMG.toString());
    assertEquals("\nInitializing REFERENCE_CLOSED_IMG",
        Constants.IMAGE_URL + "reference_closed.gif",
        Constants.REFERENCE_CLOSED_IMG.toString());
    assertEquals("\nInitializing REFERENCE_OPENED_IMG",
        Constants.IMAGE_URL + "reference_opened.gif",
        Constants.REFERENCE_OPENED_IMG.toString());
    assertEquals("\nInitializing REFERENCE_LEAF_IMG",
        Constants.IMAGE_URL + "reference_leaf.gif",
        Constants.REFERENCE_LEAF_IMG.toString());
    assertEquals("\nInitializing SPLASH_IMG",
        Constants.IMAGE_URL + "splash.gif",
        Constants.SPLASH_IMG.toString());
  }


  // Instance data ----------------------------------------------------------
  /** Simulated invocation parameter to the plug-in as passed in by Fujaba. */
  private String path_;
  // End instance data ------------------------------------------------------
}

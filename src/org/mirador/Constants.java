/* --------------------------------------------------------------------------+
   Constants.java - Application-wide constants.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Most of the values here are dependent on the plug-in path. Due to this
   dependency, initialization must occur at runtime. The assignments to static
   final variables are effected by the constructor of a static inner class.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;


/**
 * An uninstantiateable class that serves only as a container for the constants
 * of the Mirador model merging wizard.<p>
 *
 * Member <i>RTI</i> is constructed when any of the public data members that
 * depend on it are first accessed, thus effecting the runtime initialization of
 * the class' constants through its constructor.
 *
 * @since   v0.6 - Feb 1, 2010
 * @author  Stephen Barrett
 */
public class Constants {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   * Suppress default constructor to prevent instantiation.
   *
   */
  private Constants() {
  }


  // Class data -------------------------------------------------------------
  /** Current version of the wizard plug-in. */
  static public final String VERSION = "0.84";
  /** Package research and development institution. */
  static public final String ORGANIZATION =
      "Concordia University, Montreal";
  /** Package primary contact information. */
  static public final String CONTACT =
      "Stephen Barrett:  ste_barr@encs.concordia.ca";

  /** Application failure exit status. */
  static public final int EXIT_FAILURE = -1;
  /** Application success exit status. */
  static public final int EXIT_SUCCESS = 0;

  /** Object that initializes constants of the class at runtime. */
  static private Initializer RTI = new Initializer();
  /** Base directory for the plug-in files. */
  static public final URL BASE_URL = RTI.base_url_;
  /** Root directory for the documentation files. */
  static public final URL HELP_URL = RTI.help_url_;
  /** Root directory for the image files. */
  static public final URL IMAGE_URL = RTI.image_url_;
  /** Root directory for the script files. */
  static public final URL SCRIPT_URL = RTI.script_url_;
  /** The Mirador personnel script file. */
  static public final URL WHO_DOC = RTI.who_doc_;

  /** Icon for class field tree nodes with unseen children. */
  static public final ImageIcon ATTRIBUTE_CLOSED_IMG = RTI.attribute_closed_img_;
  /** Icon for class field tree nodes with seen children. */
  static public final ImageIcon ATTRIBUTE_OPENED_IMG = RTI.attribute_opened_img_;
  /** Icon for class field tree nodes with no children. */
  static public final ImageIcon ATTRIBUTE_LEAF_IMG = RTI.attribute_leaf_img_;
  /** Previous page button image. */
  static public final ImageIcon BACKWARD_IMG = RTI.backward_img_;
  /** Cancel button image. */
  static public final ImageIcon CANCEL_IMG = RTI.cancel_img_;
  /** Icon for class tree nodes with unseen children. */
  static public final ImageIcon CLASS_CLOSED_IMG = RTI.class_closed_img_;
  /** Icon for class tree nodes with seen children. */
  static public final ImageIcon CLASS_OPENED_IMG = RTI.class_opened_img_;
  /** Icon for class tree nodes with no children. */
  static public final ImageIcon CLASS_LEAF_IMG = RTI.class_leaf_img_;
  /** Icon for data type tree nodes with unseen children. */
  static public final ImageIcon DATATYPE_CLOSED_IMG = RTI.datatype_closed_img_;
  /** Icon for data type tree nodes with seen children. */
  static public final ImageIcon DATATYPE_OPENED_IMG = RTI.datatype_opened_img_;
  /** Icon for data type tree nodes with no children. */
  static public final ImageIcon DATATYPE_LEAF_IMG = RTI.datatype_leaf_img_;
  /** Open file button image. */
  static public final ImageIcon FILE_OPENED_IMG = RTI.file_opened_img_;
  /** Next page button image. */
  static public final ImageIcon FORWARD_IMG = RTI.forward_img_;
  /** Icon for generalization tree nodes with unseen children. */
  static public final ImageIcon GENERALIZATION_CLOSED_IMG = RTI.generalization_closed_img_;
  /** Icon for generalization tree nodes with seen children. */
  static public final ImageIcon GENERALIZATION_OPENED_IMG = RTI.generalization_opened_img_;
  /** Icon for generalization tree nodes with no children. */
  static public final ImageIcon GENERALIZATION_LEAF_IMG = RTI.generalization_leaf_img_;
  /** Help button image. */
  static public final ImageIcon HELP_IMG = RTI.help_img_;
  /** Okay button image. */
  static public final ImageIcon OKAY_IMG = RTI.okay_img_;
  /** Icon for class operation tree nodes with unseen children. */
  static public final ImageIcon OPERATION_CLOSED_IMG = RTI.operation_closed_img_;
  /** Icon for class operation tree nodes with seen children. */
  static public final ImageIcon OPERATION_OPENED_IMG = RTI.operation_opened_img_;
  /** Icon for class operation tree nodes with no children. */
  static public final ImageIcon OPERATION_LEAF_IMG = RTI.operation_leaf_img_;
  /** Icon for package tree nodes with unseen children. */
  static public final ImageIcon PACKAGE_CLOSED_IMG = RTI.package_closed_img_;
  /** Icon for package tree nodes with seen children. */
  static public final ImageIcon PACKAGE_OPENED_IMG = RTI.package_opened_img_;
  /** Icon for package tree nodes with no children. */
  static public final ImageIcon PACKAGE_LEAF_IMG = RTI.package_leaf_img_;
  /** Icon for package tree nodes with unseen children. */
  static public final ImageIcon PARAMETER_CLOSED_IMG = RTI.parameter_closed_img_;
  /** Icon for package tree nodes with seen children. */
  static public final ImageIcon PARAMETER_OPENED_IMG = RTI.parameter_opened_img_;
  /** Icon for package tree nodes with no children. */
  static public final ImageIcon PARAMETER_LEAF_IMG = RTI.parameter_leaf_img_;
  /** Icon for project tree node with unseen children. */
  static public final ImageIcon PROJECT_CLOSED_IMG = RTI.project_closed_img_;
  /** Icon for project tree node with seen children. */
  static public final ImageIcon PROJECT_OPENED_IMG = RTI.project_opened_img_;
  /** Icon for project tree node with no children. */
  static public final ImageIcon PROJECT_LEAF_IMG = RTI.project_leaf_img_;
  /** Icon for reference nodes with unseen children. */
  static public final ImageIcon REFERENCE_CLOSED_IMG = RTI.reference_closed_img_;
  /** Icon for reference nodes with seen children. */
  static public final ImageIcon REFERENCE_OPENED_IMG = RTI.reference_opened_img_;
  /** Icon for reference nodes with no children. */
  static public final ImageIcon REFERENCE_LEAF_IMG = RTI.reference_leaf_img_;
  /** About box splash image. */
  static public final ImageIcon SPLASH_IMG = RTI.splash_img_;
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**
   * Shadows the public constant data members for initialization purposes, with
   * private non-constants that can be assigned values.<p>
   *
   * The values here are created dynamically and then assigned to the constant
   * members. Doing so allows for the members to be declared as <tt>static
   * final</tt>, and yet not be assigned values until runtime.
   *
   * @since   v0.6 - Feb 1, 2010
   * @author  Stephen Barrett
   */
  static public class Initializer {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    /**
     * Initializes the non-constant shadows of the constant members with
     * dynamically obtained values, which can then in turn be assigned to the
     * <tt>static final</tt> constants themselves.
     */
    private Initializer() {
      try {
        // Create useful stem URLs.
        base_url_ = new URL(plugin_path_);
        help_url_ = new URL(base_url_, HELP_DIR);
        image_url_ = new URL(base_url_, IMAGE_DIR);
        script_url_ = new URL(base_url_, SCRIPT_DIR);

        // Fetch scripts.
        who_doc_ = new URL(script_url_, "mirador.who");

        // Fetch images.
        attribute_closed_img_ = new ImageIcon(new URL(image_url_, "attribute_closed.gif"));
        attribute_opened_img_ = new ImageIcon(new URL(image_url_, "attribute_opened.gif"));
        attribute_leaf_img_ = new ImageIcon(new URL(image_url_, "attribute_leaf.gif"));
        backward_img_ = new ImageIcon(new URL(image_url_, "backward.png"));
        cancel_img_ = new ImageIcon(new URL(image_url_, "cancel.png"));
        class_closed_img_ = new ImageIcon(new URL(image_url_, "class_closed.gif"));
        class_opened_img_ = new ImageIcon(new URL(image_url_, "class_opened.gif"));
        class_leaf_img_ = new ImageIcon(new URL(image_url_, "class_leaf.gif"));
        datatype_closed_img_ = new ImageIcon(new URL(image_url_, "datatype_closed.gif"));
        datatype_opened_img_ = new ImageIcon(new URL(image_url_, "datatype_opened.gif"));
        datatype_leaf_img_ = new ImageIcon(new URL(image_url_, "datatype_leaf.gif"));
        file_opened_img_ = new ImageIcon(new URL(image_url_, "file_opened.png"));
        forward_img_ = new ImageIcon(new URL(image_url_, "forward.png"));
        generalization_closed_img_ = new ImageIcon(new URL(image_url_, "generalization_closed.gif"));
        generalization_opened_img_ = new ImageIcon(new URL(image_url_, "generalization_opened.gif"));
        generalization_leaf_img_ = new ImageIcon(new URL(image_url_, "generalization_leaf.gif"));
        help_img_ = new ImageIcon(new URL(image_url_, "help.png"));
        okay_img_ = new ImageIcon(new URL(image_url_, "okay.png"));
        operation_closed_img_ = new ImageIcon(new URL(image_url_, "operation_closed.gif"));
        operation_opened_img_ = new ImageIcon(new URL(image_url_, "operation_opened.gif"));
        operation_leaf_img_ = new ImageIcon(new URL(image_url_, "operation_leaf.gif"));
        package_closed_img_ = new ImageIcon(new URL(image_url_, "package_closed.gif"));
        package_opened_img_ = new ImageIcon(new URL(image_url_, "package_opened.gif"));
        package_leaf_img_ = new ImageIcon(new URL(image_url_, "package_leaf.gif"));
        parameter_closed_img_ = new ImageIcon(new URL(image_url_, "parameter_closed.gif"));
        parameter_opened_img_ = new ImageIcon(new URL(image_url_, "parameter_opened.gif"));
        parameter_leaf_img_ = new ImageIcon(new URL(image_url_, "parameter_leaf.gif"));
        project_closed_img_ = new ImageIcon(new URL(image_url_, "project_closed.png"));
        project_opened_img_ = new ImageIcon(new URL(image_url_, "project_opened.png"));
        project_leaf_img_ = new ImageIcon(new URL(image_url_, "project_leaf.png"));
        reference_closed_img_ = new ImageIcon(new URL(image_url_, "reference_closed.gif"));
        reference_opened_img_ = new ImageIcon(new URL(image_url_, "reference_opened.gif"));
        reference_leaf_img_ = new ImageIcon(new URL(image_url_, "reference_leaf.gif"));
        splash_img_ = new ImageIcon(new URL(image_url_, "splash.gif"));
      }
      catch (MalformedURLException ex) {
        System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
        ex.printStackTrace();
      }
    }


    // Instance data --------------------------------------------------------
    /** Initializer of the base directory for the plug-in files. */
    private URL base_url_;
    /** Initializer of the root directory for the documentation files. */
    private URL help_url_;
    /** Initializer of the root directory for the image files. */
    private URL image_url_;
    /** Initializer of the root directory for the script files. */
    private URL script_url_;
    /** Initializer of the Mirador personnel script file. */
    private URL who_doc_;

    /** Initializer of the icon for class field nodes with unseen children. */
    private ImageIcon attribute_closed_img_;
    /** Initializer of the icon for class field nodes with seen children. */
    private ImageIcon attribute_opened_img_;
    /** Initializer of the icon for class field nodes with no children. */
    private ImageIcon attribute_leaf_img_;
    /** Initializer of the previous page button image. */
    private ImageIcon backward_img_;
    /** Initializer of the cancel button image. */
    private ImageIcon cancel_img_;
    /** Initializer of the icon for class nodes with unseen children. */
    private ImageIcon class_closed_img_;
    /** Initializer of the icon for class nodes with seen children. */
    private ImageIcon class_opened_img_;
    /** Initializer of the icon for class nodes with no children. */
    private ImageIcon class_leaf_img_;
    /** Initializer of the icon for data type nodes with unseen children. */
    private ImageIcon datatype_closed_img_;
    /** Initializer of the icon for data type nodes with seen children. */
    private ImageIcon datatype_opened_img_;
    /** Initializer of the icon for data type nodes with no children. */
    private ImageIcon datatype_leaf_img_;
    /** Initializer of the opened file button image. */
    private ImageIcon file_opened_img_;
    /** Initializer of the next page button image. */
    private ImageIcon forward_img_;
    /** Initializer of the generalization relation with no children. */
    private ImageIcon generalization_closed_img_;
    /** Initializer of the generalization relation with seen children. */
    private ImageIcon generalization_opened_img_;
    /** Initializer of the generalization relation with unseen children. */
    private ImageIcon generalization_leaf_img_;
    /** Initializer of the help button image. */
    private ImageIcon help_img_;
    /** Initializer of the okay button image. */
    private ImageIcon okay_img_;
    /** Initializer of the icon for class operation nodes with unseen children. */
    private ImageIcon operation_closed_img_;
    /** Initializer of the icon for class operation nodes with seen children. */
    private ImageIcon operation_opened_img_;
    /** Initializer of the icon for class operation nodes with no children. */
    private ImageIcon operation_leaf_img_;
    /** Initializer of the icon for package nodes with unseen children. */
    private ImageIcon package_closed_img_;
    /** Initializer of the icon for package nodes with seen children. */
    private ImageIcon package_opened_img_;
    /** Initializer of the icon for package nodes with no children. */
    private ImageIcon package_leaf_img_;
    /** Initializer of the icon for package nodes with unseen children. */
    private ImageIcon parameter_closed_img_;
    /** Initializer of the icon for package nodes with seen children. */
    private ImageIcon parameter_opened_img_;
    /** Initializer of the icon for package nodes with no children. */
    private ImageIcon parameter_leaf_img_;
    /** Initializer of the icon for project node with unseen children. */
    private ImageIcon project_closed_img_;
    /** Initializer of the icon for project node with seen children. */
    private ImageIcon project_opened_img_;
    /** Initializer of the icon for project node with no children. */
    private ImageIcon project_leaf_img_;
    /** Initializer of the icon for reference relation with unseen children. */
    private ImageIcon reference_closed_img_;
    /** Initializer of the icon for reference relation with seen children. */
    private ImageIcon reference_opened_img_;
    /** Initializer of the icon for reference relation with no children. */
    private ImageIcon reference_leaf_img_;
    /** Initializer of the about box splash image. */
    private ImageIcon splash_img_;

    /** Root directory for the documentation files. */
    private final String HELP_DIR = "doc/html/";
    /** Root directory for the image files. */
    private final String IMAGE_DIR = "img/";
    /** Root directory for the script files. */
    private final String SCRIPT_DIR = "doc/script/";
    // End instance data ----------------------------------------------------


    // Class data -----------------------------------------------------------
    /** Path to plug-in, provided by either Fujaba or the stand-alone wizard. */
    static public String plugin_path_;
    // End class data -------------------------------------------------------
  }
  // End nested types -------------------------------------------------------
}

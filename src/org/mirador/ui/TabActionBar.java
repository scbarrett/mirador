/* --------------------------------------------------------------------------+
   TabActionBar.java - Container for main button bar of a tabbed wizard dialog.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Contains a panel, rather than *being* a panel because the ButtonBarFactory
   used for consistent look & feel, precludes inheritance.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import ca.dsrg.mirador.Constants;
import com.jgoodies.forms.factories.ButtonBarFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JPanel;


/**
 * Button bar container intended to be placed at bottom of the wizard dialog.
 * Includes some basic navigation and help functionality.
 *
 * @since   v0.6 - Feb 1, 2010
 * @author  Stephen Barrett
 */
public class TabActionBar {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Creates a button bar for use by the tabbed wizard.
   *
   * @param  callback  Tabbed dialog the button bar belongs to.
   */
  public TabActionBar(Callable callback) {
    initialize(callback);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Accesses the backward button for the purpose of adding listeners.
   *
   * @return  The backward button.
   * @category  getter
   */
  public JButton getBackwardButton() {
    return backward_btn_;
  }


  /**
   * Accesses the cancel button for the purpose of adding listeners.
   *
   * @return  The cancel button.
   * @category  getter
   */
  public JButton getCancelButton() {
    return cancel_btn_;
  }


  /**
   * Accesses the forward button for the purpose of adding listeners.
   *
   * @return  The forward button.
   * @category  getter
   */
  public JButton getForwardButton() {
    return forward_btn_;
  }


  /**
   * Accesses the help button for the purpose of adding listeners.
   *
   * @return  The help button.
   * @category  getter
   */
  public JButton getHelpButton() {
    return help_btn_;
  }


  /**
   * Accesses the okay button for the purpose of adding listeners.
   *
   * @return  The okay button.
   * @category  getter
   */
  public JButton getOkayButton() {
    return okay_btn_;
  }


  /**
   * Gives the button bar panel as built by the JGoodies factory.
   *
   * @return  The actual bar panel with navigation and help buttons.
   * @category  getter
   */
  public JPanel getPanel() {
    return action_pnl_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Disables the backward button.
   */
  public void disableBackwardButton() {
    backward_btn_.setEnabled(false);
  }


  /**
   * Enables the backward button.
   */
  public void enableBackwardButton() {
    backward_btn_.setEnabled(true);
  }


  /**
   * Disables the cancel button.
   */
  public void disableCancelButton() {
    cancel_btn_.setEnabled(false);
  }


  /**
   * Enables the cancel button.
   */
  public void enableCancelButton() {
    cancel_btn_.setEnabled(true);
  }


  /**
   * Disables the forward button.
   */
  public void disableForwardButton() {
    forward_btn_.setEnabled(false);
  }


  /**
   * Enables the forward button.
   */
  public void enableForwardButton() {
    forward_btn_.setEnabled(true);
  }


  /**
   * Disables the okay button.
   */
  public void disableOkayButton() {
    okay_btn_.setEnabled(false);
  }


  /**
   * Enables the okay button.
   */
  public void enableOkayButton() {
    okay_btn_.setEnabled(true);
  }


  /**
   * Constructs the help URL for a given panel of the wizard dialog.
   *
   * @param  file_name  Name of panel's HTML help file.
   * @return  URL for the panel's HTML help.
   */
  @SuppressWarnings("unused")
  static private URL panelHelpUrl(String file_name) {
    URL file_url = null;

    try {
      file_url = new URL(Constants.HELP_URL, file_name);
    }
    catch (MalformedURLException ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      ex.printStackTrace();
    }

    return file_url;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Instantiate the GUI's components, and populate its containers.
   */
  private void assembleGui() {
    // Instantiate GUI components.
    help_btn_ = new JButton("Help", Constants.HELP_IMG);
    backward_btn_ = new JButton(Constants.BACKWARD_IMG);
    forward_btn_ = new JButton(Constants.FORWARD_IMG);
    okay_btn_ = new JButton("Finish", Constants.OKAY_IMG);
    cancel_btn_ = new JButton("Cancel", Constants.CANCEL_IMG);

    // Build wizard toolbar panel from components.
    action_pnl_ = ButtonBarFactory.buildWizardBar(new JButton[]{ help_btn_ },
        backward_btn_, forward_btn_, new JButton[]{ okay_btn_, cancel_btn_ });
  }


  /**
   * Attach observers to panel components.
   */
  private void attachHandlers(final Callable callback) {
    if (callback != null) {
      // Backward button event handler.
      backward_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          callback.onBackwardClick(ev);
        }
      });


      // Cancel button event handler.
      cancel_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          callback.onCancelClick(ev);
        }
      });


      // Forward button event handler.
      forward_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          callback.onForwardClick(ev);
        }
      });


      // Help button event handler.
      help_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          callback.onHelpClick(ev);
        }
      });


      // Okay button event handler.
      okay_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          callback.onOkayClick(ev);
        }
      });
    }
    else {
      // Backward button event handler.
      backward_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          onBackwardClick(ev);
        }
      });


      // Cancel button event handler.
      cancel_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          onCancelClick(ev);
        }
      });


      // Forward button event handler.
      forward_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          onForwardClick(ev);
        }
      });


      // Help button event handler.
      help_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          onHelpClick(ev);
        }
      });


      // Okay button event handler.
      okay_btn_.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent ev) {
          onOkayClick(ev);
        }
      });
    }
  }


  /**
   * Constructs the panel and readys it for display.
   */
  private void initialize(Callable callback) {
    // Create and place the visual components on the panel.
    assembleGui();

    // Link observers to panel components for event handling.
    attachHandlers(callback);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ event handlers
  /**
   * Handles backward button press event.
   *
   * @param  ev  The triggering event.
   */
  private void  onBackwardClick(ActionEvent ev) {
    // Default tab bar action.
  }


  /**
   * Handles cancel button press event.
   *
   * @param  ev  The triggering event.
   */
  private void onCancelClick(ActionEvent ev) {
    // Default tab bar action.
  }


  /**
   * Handles forward button press event.
   *
   * @param  ev  The triggering event.
   */
  private void  onForwardClick(ActionEvent ev) {
    // Default tab bar action.
  }


  /**
   * Handles help button press event.
   *
   * @param  ev  The triggering event.
   */
  private void onHelpClick(ActionEvent ev) {
    // Default tab bar action.
  }


  /**
   * Handles okay button press event.
   *
   * @param  ev  The triggering event.
   */
  private void onOkayClick(ActionEvent ev) {
    // Default tab bar action.
  }


  // Instance data ----------------------------------------------------------
  private JPanel action_pnl_;
  private JButton backward_btn_;
  private JButton cancel_btn_;
  private JButton forward_btn_;
  private JButton help_btn_;
  private JButton okay_btn_;
  // End instance data ------------------------------------------------------


  /**                                                                       DOCDO: Provide class overview.
   *
   * @since   v0.80 - Dec 22, 2010
   * @author  Stephen Barrett
   */
  public interface Callable {
    public void onBackwardClick(ActionEvent ev);


    public void onCancelClick(ActionEvent ev);


    public void onForwardClick(ActionEvent ev);


    public void onHelpClick(ActionEvent ev);


    public void onOkayClick(ActionEvent ev);
  }
}

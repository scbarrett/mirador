/* --------------------------------------------------------------------------+
   MatchStrategy.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.match;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.27 - Jun 20, 2010
 * @author  Stephen Barrett
 */
public class MatchStrategy {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public MatchStrategy(SimilarityEvaluator evaluator, float weight,
      String label, String spin_tool_tip, String radio_tool_tip) {
    evaluator_ = evaluator;

    spinner_ = new JSpinner(new SpinnerNumberModel(
        new Float(0.0), new Float(0.0), new Float(1.0), new Float(0.1)));
    spinner_.setValue(weight);
    spinner_.setEditor(new JSpinner.NumberEditor(spinner_, "0.00"));
    spinner_.setToolTipText(spin_tool_tip);

    radio_ = new JRadioButton(label);
    radio_.setToolTipText(radio_tool_tip);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public SimilarityEvaluator getEvaluator() {
    return evaluator_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  evaluator  Set-to value for the field.
   * @category  setter
   */
  public void setEvaluator(SimilarityEvaluator evaluator) {
    evaluator_ = evaluator;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public String getLabel() {
    return radio_.getText();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  label  Set-to value for the field.
   * @category  setter
   */
  public void setLabel(String label) {
    radio_.setText(label);
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public JRadioButton getRadio() {
    return radio_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public JSpinner getSpinner() {
    return spinner_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public float getWeight() {
    return ((Number) spinner_.getValue()).floatValue();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  weight  Set-to value for the field.
   * @category  setter
   */
  public void setWeight(float weight) {
    spinner_.setValue(weight);
  }


  // Instance data ----------------------------------------------------------
  private SimilarityEvaluator evaluator_;
  private JSpinner spinner_;
  private JRadioButton radio_;
  // End instance data ------------------------------------------------------
}

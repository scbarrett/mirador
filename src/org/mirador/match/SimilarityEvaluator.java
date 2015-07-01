/* --------------------------------------------------------------------------+
   SimilarityEvaluator.java - Abstract base class for matching strategies used
     for measuring model element similarity.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Mirador supports several model comparison modules for schema matching.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.match;
import ca.dsrg.mirador.model.EcoreExtra;
import java.util.Random;


/**
 * Abstract evaluator of model element similarity.
 *
 * @since   v0.16 - Mar 4, 2010
 * @author  Stephen Barrett
 */
abstract public class SimilarityEvaluator {
  public SimilarityEvaluator() {
    this(0.0f);
  }


  public SimilarityEvaluator(float weight) {
    inital_weight_ = weight;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public float getInitalWeight() {
    return inital_weight_;
  }


  public void setInitalWeight(float weight) {
    inital_weight_ = weight;
  }


  public String getLabel() {
    return label_;
  }


  public String getSpinToolTip() {
    return spin_tool_tip_;
  }


  public String getRadioToolTip() {
    return radio_tool_tip_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Evaluates similarity from one model element to another. As measures are not
   * always symmetrical, a direction of evaluation is implied by the arguments.
   * The measure is a normalized representation of similarity.
   *
   * @param  from_element  Model element <i>from</i> which to measure.
   * @param  to_element  Model element <i>to</i> which to measure.
   * @return  Normalized similarity: 0.0 = no similarity, 1.0 = identical
   */
  public float evaluate(EcoreExtra from_element, EcoreExtra to_element) {
    return 0.0f;
  }


  // Instance data ----------------------------------------------------------
  protected final Random random_ = new Random();  // TODO:3 Replace with real algorithm.
  protected float inital_weight_;
  protected String label_ = "by Default";
  protected String spin_tool_tip_ =
      "Set weight of the by Default strategy in similarity calculation.";
  protected String radio_tool_tip_ =
      "Automatically match elements based on default similarity.";
  // End instance data ------------------------------------------------------
}

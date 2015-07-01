/* --------------------------------------------------------------------------+
   IdEvaluator.java - Matching strategy that measures the ID similarity of
     model elements.

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


/**
 * Evaluator of model element ID similarity.
 *
 * @since   v0.18 - Mar 6, 2010
 * @author  Stephen Barrett
 */
public class IdEvaluator extends SimilarityEvaluator {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public IdEvaluator() {
  }


  public IdEvaluator(float weight) {
    super(weight);
    label_ = "by ID";
    spin_tool_tip_ =
      "Set weight of the by ID strategy in similarity calculation.";
    radio_tool_tip_ =
      "Automatically match elements based on ID similarity.";
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Evaluates similarity of IDs from one model element to another. As measures
   * are not always symmetrical, a direction of evaluation is implied by the
   * arguments. The measure is a normalized representation of similarity.
   *
   * @param  from_element  Model element <i>from</i> which to measure.
   * @param  to_element  Model element <i>to</i> which to measure.
   * @return  Normalized similarity: 0.0 = no similarity, 1.0 = identical
   */
  @Override public float evaluate(EcoreExtra from_element,
      EcoreExtra to_element) {
    float similarity;

    // No gray area: either the IDs are identical, or they are not.
    if (from_element.getId().equals(to_element.getId()))
      similarity = 1.0f;  // Same IDs, created in base model.
    else
      similarity = 0.0f;  // Different IDs.

    return similarity;
  }
}

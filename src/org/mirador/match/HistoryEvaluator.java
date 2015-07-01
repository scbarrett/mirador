/* --------------------------------------------------------------------------+
   HistoryEvaluator.java - Matching strategy that measures the historical
     similarity of model elements.

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
 * Evaluator of model element historical similarity.
 *
 * @since   v0.21 - Mar 27, 2010
 * @author  Stephen Barrett
 */
public class HistoryEvaluator extends SimilarityEvaluator {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public HistoryEvaluator() {
  }


  public HistoryEvaluator(float weight) {
    super(weight);
    label_ = "by History";
    spin_tool_tip_ =
      "Set weight of the by History strategy in similarity calculation.";
    radio_tool_tip_ =
      "Automatically match elements based on history similarity.";
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Evaluates historical similarity from one model element to another. As
   * measures are not always symmetrical, a direction of evaluation is implied
   * by the arguments. The measure is a normalized representation of similarity.
   *
   * @param  from_element  Model element <i>from</i> which to measure.
   * @param  to_element  Model element <i>to</i> which to measure.
   * @return  Normalized similarity: 0.0 = no similarity, 1.0 = identical
   */
  @Override public float evaluate(EcoreExtra from_element,
      EcoreExtra to_element) {
    float similarity;
    similarity = random_.nextFloat();  // TODO:3 Replace with real algorithm.
    return similarity;
  }
}

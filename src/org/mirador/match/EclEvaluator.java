/* --------------------------------------------------------------------------+
   EclEvaluator.java - Matching strategy that measures the similarity of model
     elements by way of Epsilon Comparison Language scripts.

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
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.trace.Match;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.IEolExecutableModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import java.io.File;


/**
 * Evaluator of model element similarity using the Epsilon Comparison Language.
 *
 * @since   v0.26 - Jun 10, 2010
 * @author  Stephen Barrett
 */
public class EclEvaluator extends SimilarityEvaluator {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public EclEvaluator() {
  }


  public EclEvaluator(float weight) {
    super(weight);
    label_ = "by ECL";
    spin_tool_tip_ =
      "Set weight of the by Epsilon Comparison Language strategy in similarity calculation.";
    radio_tool_tip_ =
      "Automatically match elements based on ECL script similarity.";
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public MatchTrace getMatchTrace() {
    return ecl_trace_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public MatchTrace compare(XMIResource xmodel_lf, XMIResource xmodel_rt,
      File ecl_file) {
    IEolExecutableModule module = new EclModule();

    if (ecl_file == null)  // TODO:3 Change default file to a URI load.
      ecl_file = new File("usr/mirador-evaluator.ecl");

    try {
      module.parse(ecl_file);
    }
    catch (Exception ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      ex.printStackTrace();
    }

    new ResourceSetImpl().getResources().add(xmodel_lf);
    module.getContext().getModelRepository().addModel(
        new InMemoryEmfModel("Left", xmodel_lf, EcorePackage.eNS_URI));

    new ResourceSetImpl().getResources().add(xmodel_rt);
    module.getContext().getModelRepository().addModel(
         new InMemoryEmfModel("Right", xmodel_rt, EcorePackage.eNS_URI));

    try {
      ecl_trace_ = (MatchTrace) module.execute();
    }
    catch (EolRuntimeException ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      ex.printStackTrace();
    }

    return ecl_trace_;
  }


  /**
   * Evaluates similarity from one model element to another. As
   * measures are not always symmetrical, a direction of evaluation is implied
   * by the arguments. The measure is a normalized representation of similarity.
   *
   * @param  from_element  Model element <i>from</i> which to measure.
   * @param  to_element  Model element <i>to</i> which to measure.
   * @return  Normalized similarity: 0.0 = no similarity, 1.0 = identical
   */
  @Override public float evaluate(EcoreExtra from_element,
      EcoreExtra to_element) {
    float similarity = 0.0f;

    if (ecl_trace_ != null) {
      Match match = ecl_trace_.getReduced()
          .getMatch(from_element.getElement(), to_element.getElement());

      if (match != null) {
        similarity = (Float) match.getInfo().get("mirador");
      }
    }

    return similarity;
  }


  // Instance data ----------------------------------------------------------
  private MatchTrace ecl_trace_;
  // End instance data ------------------------------------------------------
}

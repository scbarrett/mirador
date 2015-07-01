/* --------------------------------------------------------------------------+
   ElementMatcher.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.match.MeasureMatrix.CellComparator;
import ca.dsrg.mirador.match.MeasureMatrix.MeasureCell;
import ca.dsrg.mirador.match.MeasureMatrix.MeasureRow;
import ca.dsrg.mirador.match.MeasureMatrix.RowComparator;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.16 - Mar 4, 2010
 * @author  Stephen Barrett
 */
public class ElementMatcher {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   * Suppress default constructor to prevent instantiation.
   *
   */
  private ElementMatcher() {}


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
//  /**                                                                     DOCDO: Provide method overview.
//   *
//   * @param  from_elements  Master side...
//   * @param  to_elements  Slave side...
//   */
//  static public void matchCommon(
//      Map<String, EcoreExtra> from_elements,
//      Map<String, EcoreExtra> to_elements) {
//    for (Iterator<EcoreExtra> from_it =
//        from_elements.values().iterator(); from_it.hasNext();) {
//      EcoreExtra from_element = from_it.next();
//
//      // Only consider those elements that are part of the base model.
//      if (from_element.getMergeSide() != MergeSide.BASE)
//        continue;
//
//      for (Iterator<EcoreExtra> to_it =
//          to_elements.values().iterator(); to_it.hasNext();) {
//        EcoreExtra to_element = to_it.next();
//
//        // Match elements on ID.
//        if (to_element.getMergeSide() == MergeSide.BASE &&
//            from_element.getId().equals(to_element.getId())) {
//          from_element.setMatch(to_element);
//          break;  // There can only be one common match.
//        }
//      }
//    }
//  }


  /**                                                                     DOCDO: Provide method overview.
   * Works from master side's (i.e., from_element) point of view.
   *
   * @param  rankings  Purpose of the argument.
   * @param  limit  Purpose of the argument.
   */
  static public void matchThreshold(MeasureMatrix rankings, float limit) {
    Debug.dbg.println("\n\n\n\t    --- Model Element Matches ---");

    // Clear previously made element matches.
    for (MeasureRow row : rankings.values()) {
      if (!row.getFromElement().isKeepMatch())  // Skip manually made matches.
        row.getFromElement().setMatch(null);
    }

    // Select best of "from element's" candidate matches.
    for (MeasureRow row : rankings.values()) {
      EcoreExtra from_element = row.getFromElement();

      for (MeasureCell cell : row.values()) {
        EcoreExtra to_element = cell.getToElement();
        int strategy_idx = rankings.getMatchStrategyIndex();

        // Match elements if "to element" is available.
        if (to_element.getMatch() == null
            && cell.getMeasures().get(strategy_idx) >= limit) {
          // TODO:2 Matching by strategy requires ranking by the same strategy!
          from_element.setMatch(to_element);
          break;
        }
      }

      if (from_element.getMatch() != null) {
        Debug.dbg.format("%24s %3s %s\n", from_element.getElement().getName(),
            "<=>", from_element.getMatch().getElement().getName());
      }
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  from_ecore_model  Purpose of the argument.
   * @param  to_ecore_model  Purpose of the argument.
   */
  static public void measureSimilarities(MiradorModel from_ecore_model,
      MiradorModel to_ecore_model) {
    MeasureMatrix measures = from_ecore_model.getMatchMatrix();

    for (Iterator<EcoreExtra> from_it = from_ecore_model.extraIterator();
        from_it.hasNext();) {
      EcoreExtra from_extra = from_it.next();

      for (Iterator<EcoreExtra> to_it = to_ecore_model.extraIterator();
          to_it.hasNext();) {
        EcoreExtra to_extra = to_it.next();

        // Compare only like elements to like.
        if (from_extra.getEcoreType() == to_extra.getEcoreType()) {
          measures.put(from_extra, to_extra);
          MeasureCell cell = measures.get(from_extra.getId(), to_extra.getId());

          float similarity;
          float ttl_score = 0;
          float max_score = 0;

          List<MatchStrategy> strategies = measures.getStrategies();
          for (int i = 1; i < strategies.size(); ++i) {
            similarity =
                strategies.get(i).getEvaluator().evaluate(from_extra, to_extra);

            // Match on ID is special -- elements are from base model. (Except when usurped by developer...)
            if (i == 1 && similarity == 1.0) { // TODO:3 Should not assume #1 strategy is by ID.
              ttl_score = 1.0f;
              max_score = 1.0f;
            }

            if (!(ttl_score == 1.0 && max_score == 1.0)) { // Skip if matched by ID.
              float weight = strategies.get(i).getWeight();
              if (weight > 0) {
                ttl_score += similarity * similarity * weight;
                max_score += weight;
              }
            }

            cell.put(i, similarity);
          }

          cell.getMeasures()  // Set overall score.
              .set(0, (max_score != 0) ? ttl_score / max_score : 0);
        }
      }
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  ecore_model  Purpose of the argument.
   * @param  strategy_idx  Purpose of the argument.
   */
  static public void rankSimilarlities(MiradorModel ecore_model,
      int strategy_idx) {
    MeasureMatrix measures = ecore_model.getMatchMatrix();

    // Sort the measure matrix by overall score, high to low.
    SortedSet<MeasureRow> rows =
        new TreeSet<MeasureRow>(new RowComparator());

    for (MeasureRow row : measures.values()) {
      SortedSet<MeasureCell> cells =
          new TreeSet<MeasureCell>(new CellComparator(strategy_idx));

      for (MeasureCell cell : row.values())
        cells.add(cell);

      MeasureRow new_row = measures.new MeasureRow(row.getFromElement());
      for (MeasureCell cell : cells)
        new_row.put(cell);

      rows.add(new_row);
    }


    MeasureMatrix rankings = ecore_model.getRankMatrix();
    for (MeasureRow row : rows)
      rankings.put(row.getFromElement().getId(), row);
  }
}

/* --------------------------------------------------------------------------+
   MeasureMatrix.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.model.EcoreExtra;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.17 - Mar 5, 2010
 * @author  Stephen Barrett
 */
public class MeasureMatrix
    extends LinkedHashMap<String, MeasureMatrix.MeasureRow> {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  strategies  Purpose of the argument.
   */
  public MeasureMatrix(List<MatchStrategy> strategies) {
    strategies_ = strategies;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  from_id  ID of element "from" which measurements are made -- row index.
   * @param  to_id  ID of element "to" which measurements are made -- column index.
   * @return  What is returned by the method.
   */
  public MeasureCell get(String from_id, String to_id) {
    return (get(from_id) != null) ? get(from_id).get(to_id) : null;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public List<MatchStrategy> getStrategies() {
    return strategies_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  What is returned by the method.
   * @category  getter
   */
  public int getMatchStrategyIndex() {
    return strategy_idx_;
  }


  public void setMatchStrategyIndex(int index) {
    strategy_idx_ = index;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  from_element  Element "from" which measurements are made -- row dimension.
   * @param  to_element  Element "to" which measurements are made -- column dimension.
   * @return  What is returned by the method.
   */
  public MeasureCell put(EcoreExtra from_element, EcoreExtra to_element) {
    MeasureCell cell = new MeasureCell(from_element, to_element);  // FIXME:3 Defensive copy.
    return put(cell);
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  cell  Purpose of the argument.
   * @return  What is returned by the method.
   */
  public MeasureCell put(MeasureCell cell) {
    String from_id = cell.from_element_.getId();
    MeasureRow row;

    if (!containsKey(from_id)) {
      row = new MeasureRow(cell.from_element_);
      put(from_id, row);
    }
    else
      row = get(from_id);

    return row.put(cell);
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  row  Purpose of the argument.
   * @return  What is returned by the method.
   */
  public MeasureRow put(MeasureRow row) {
    String from_id = row.from_element_.getId();
    return put(from_id, row);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   */
  public void dump() {
    if (Debug.dbg.isDebug()) {
      for (MeasureRow row : values()) {
        row.dump();
      }
    }
  }


  // Instance data ----------------------------------------------------------
  private List<MatchStrategy> strategies_ = new ArrayList<MatchStrategy>();

  /** Which strategy is being used for matching threshold. */
  private int strategy_idx_;
  // End instance data ------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.17 - Mar 5, 2010
   * @author  Stephen Barrett
   */
  public class MeasureCell {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    /**                                                                   DOCDO: Provide constructor overview.
     *
     * @param  from_element  Element measurement is take <i>from</i>.
     * @param  to_element  Element measurement is take <i>to</i>.
     */
    public MeasureCell(EcoreExtra from_element, EcoreExtra to_element) {
      from_element_ = from_element;
      to_element_ = to_element;

      for (int i = 0; i < strategies_.size(); ++i)
        measures_.add(0.0f);
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
    /**                                                                   DOCDO: Provide method overview.
     *
     * @return  The field's current value.
     * @category  getter
     */
    public EcoreExtra getFromElement() {
      return from_element_;
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @return  The field's current value.
     * @category  getter
     */
    public EcoreExtra getToElement() {
      return to_element_;
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @return  The field's current value.
     * @category  getter
     */
    public List<Float> getMeasures() {
      return measures_;
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     */
    public void put(int index, float similarity) {
      measures_.add(index, similarity);
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
    /**                                                                   DOCDO: Provide method overview.
     *
     */
    public void dump() {
      if (Debug.dbg.isDebug()) {
        final int MAX_ID = 11;
        final int MAX_NAME = 11;

        String id = '(' + to_element_.getId() + ')';
        String name = (to_element_.getName().length() < MAX_NAME)
            ? to_element_.getName()
            : to_element_.getName().substring(0, MAX_NAME);

        Debug.dbg.format("%"+MAX_NAME+"s %-"+MAX_ID+"."+MAX_ID+"s:   ",
            name, id);

        for (int i = 0; i < strategies_.size(); ++i)
          Debug.dbg.format("%s = %.3f   ", strategies_.get(i).getLabel(),
              measures_.get(i));

        Debug.dbg.println();
      }
    }


    // Instance data --------------------------------------------------------
    private EcoreExtra from_element_;
    private EcoreExtra to_element_;
    private List<Float> measures_ = new ArrayList<Float>();
    // End instance data ----------------------------------------------------
  }


  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.17 - Mar 5, 2010
   * @author  Stephen Barrett
   */
  public class MeasureRow extends LinkedHashMap<String, MeasureCell> {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    /**                                                                   DOCDO: Provide constructor overview.
     *
     * @param  from_element  Row element all measurements are taken <i>from</i>.
     */
    public MeasureRow(EcoreExtra from_element) {
      from_element_ = from_element;
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
    /**                                                                   DOCDO: Provide method overview.
     *
     * @return  The field's current value.
     * @category  getter
     */
    public EcoreExtra getFromElement() {
      return from_element_;
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  to_element  Column element that measurement is take <i>to</i>.
     * @return  What is returned by the method.
     */
    public MeasureCell put(EcoreExtra to_element) {
      String to_id = to_element.getId();
      MeasureCell cell;

      if (!containsKey(to_id))
        cell = new MeasureCell(from_element_, to_element);
      else
        cell = get(to_id);

      return put(cell);
    }


    /**                                                                   DOCDO: Provide method overview.
     *
     * @param  cell  Purpose of the argument.
     * @return  What is returned by the method.
     */
    public MeasureCell put(MeasureCell cell) {
      String to_id = cell.to_element_.getId();
      return put(to_id, cell);
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
    /**                                                                   DOCDO: Provide method overview.
     *
     */
    public void dump() {
      if (Debug.dbg.isDebug()) {
        String id = from_element_.getId();
        String name = from_element_.getName();
        String class_name = from_element_.getClassName();

        Debug.dbg.format("%s (%s) - [%s]\n", name, id, class_name);

        for (MeasureCell cell : values()) {
          Debug.dbg.print("   ");
          cell.dump();
        }
        Debug.dbg.println();
      }
    }


    // Instance data --------------------------------------------------------
    private EcoreExtra from_element_;
    // End instance data ----------------------------------------------------
  }


  /**                                                                       DOCDO: Provide class overview.
   *
   * @since   v0.17 - Mar 5, 2010
   * @author  Stephen Barrett
   */
  static public class CellComparator implements Comparator<MeasureCell> {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    public CellComparator(int strategy_idx) {
      strategy_idx_ = strategy_idx;
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
    public int compare(MeasureCell lhs, MeasureCell rhs) {
      int rc;

      // Swap order of comparison to get high to low sorting.
      rc = rhs.measures_.get(strategy_idx_).
          compareTo(lhs.measures_.get(strategy_idx_));

      // Measure equality is not duplication! Prefer insertion ordering.
      return (rc != 0) ? rc : 1;
    }


    // Instance data --------------------------------------------------------
    private int strategy_idx_ = 0;
    // End instance data ----------------------------------------------------
  }


  /**                                                                       DOCDO: Provide class overview.
   *
   * @since   v0.17 - Mar 5, 2010
   * @author  Stephen Barrett
   */
  static public class RowComparator implements Comparator<MeasureRow> {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
    @Override public int compare(MeasureRow lhs, MeasureRow rhs) {
      Iterator<MeasureCell> lhs_it = lhs.values().iterator();
      Iterator<MeasureCell> rhs_it = rhs.values().iterator();

      if (!lhs_it.hasNext() || !rhs_it.hasNext())
        throw new IllegalArgumentException(
            "MatchRow does not contain any cells.");

      int rc;
      // Swap order of comparison to get high to low sorting.
      rc = rhs_it.next().measures_.get(0)
          .compareTo(lhs_it.next().measures_.get(0));

      // Measure equality is not duplication! Prefer insertion ordering.
      return (rc != 0) ? rc : 1;
    }
  }
  // End nested types -------------------------------------------------------
}

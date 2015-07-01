/* --------------------------------------------------------------------------+
   DecisionCondition.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.decision;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.23 - Apr 13, 2010
 * @author  Stephen Barrett
 */
public class TableCondition {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  test  Purpose of the argument.
   * @param  states  Purpose of the argument.
   */
  public TableCondition(ConditionTest test, Tristate... states) {
    this ("", test, states);
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  name  Purpose of the argument.
   * @param  test  Purpose of the argument.
   * @param  states  Purpose of the argument.
   */
  public TableCondition(String name, ConditionTest test, Tristate... states) {
    name_ = name;
    test_ = test;
    states_ = states;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public String getName() {
    return name_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  public void states(Tristate... states) {
    states_ = states;
  }


  /**
   * Fetches specified change record from those that make up this transaction.
   *
   * @param  to_fetch  Index of record to retrieve from collection.
   * @return  Requested change record if exists, otherwise null.
   * @category  collection
   */
  public Tristate state(int to_fetch) {
    return states_[to_fetch];
  }


  public Tristate[] getStates() {
    return states_;
  }


  public int size() {
    return states_.length;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  @Override public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < states_.length; ++i) {
      if (i != 0)
        buf.append("  ");

      buf.append(states_[i].toString());
    }

    return buf.toString();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public boolean test(Object... objs) {
    return test_.testCondition(objs);
  }


  // Instance data ----------------------------------------------------------
  private String name_;
  private ConditionTest test_;
  private Tristate[] states_;
  // End instance data ------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.24 - Apr 14, 2010
   * @author  Stephen Barrett
   */
  public interface ConditionTest { // TODO:3 Change to return ActionResult in order to get fired rule number.
    public boolean testCondition(Object... objs);
  }
  // End nested types -------------------------------------------------------


  // Class data -------------------------------------------------------------
  // End class data ---------------------------------------------------------
}

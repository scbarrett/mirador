/* --------------------------------------------------------------------------+
   Tristate.java - High-level description of module and place in system.
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
public class Tristate {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   * Suppress default constructor to prevent instantiation.
   *
   */
  private Tristate(int state) {
    state_ = (state == FALSE_INT || state == TRUE_INT) ? state : UNDEF_INT;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public int getState() {
    return state_;
  }


  public boolean toBoolean() {
    if (state_ == UNDEF_INT)
      throw new IllegalStateException("Can't convert. Object's state is unknown.");
    else
      return (state_ == FALSE_INT) ? false : true;
  }


  public boolean toFalse() {
    if (state_ != UNDEF_INT)
      return (state_ == FALSE_INT) ? false : true;
    else
      return false;
  }


  public boolean toTrue() {
    if (state_ != UNDEF_INT)
      return (state_ == FALSE_INT) ? false : true;
    else
      return true;
  }


  static public Tristate forState(boolean state) {
    return (state) ? TRUE : FALSE;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  public boolean isFalse() {
    return (state_ == FALSE_INT);
  }


  public boolean isTrue() {
    return (state_ == TRUE_INT);
  }


  public boolean isUndef() {
    return (state_ == UNDEF_INT);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  /**
   * Returns the object as a string.
   *
   * @return  String representation of object.
   */
  @Override public String toString() {
    String rc = null;

    switch (state_) {
      case TRUE_INT:
        rc = "Y";
      break;

      case FALSE_INT:
        rc = "N";
      break;

      case UNDEF_INT:
        rc = "-";
      break;
    }

    return rc;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public Tristate and(Tristate tri) {
    if (state_ != UNDEF_INT && tri.getState() != UNDEF_INT)
      return (toBoolean() & tri.toBoolean()) ? TRUE : FALSE;
    else
      return UNDEF;
  }


  public Tristate not() {
    if (state_ != UNDEF_INT)
      return (state_ == FALSE_INT) ? TRUE : FALSE;
    else
      return UNDEF;
  }


  public Tristate or(Tristate tri) {
    if (state_ != UNDEF_INT && tri.getState() != UNDEF_INT)
      return (toBoolean() | tri.toBoolean()) ? TRUE : FALSE;
    else
      return UNDEF;
  }


  // Instance data ----------------------------------------------------------
  private int state_;
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  static private final int FALSE_INT = 0;
  static private final int TRUE_INT = 1;
  static private final int UNDEF_INT = 2;

  static public final Tristate FALSE = new Tristate(FALSE_INT);
  static public final Tristate TRUE = new Tristate(TRUE_INT);
  static public final Tristate UNDEF = new Tristate(UNDEF_INT);
  // End class data ---------------------------------------------------------
}

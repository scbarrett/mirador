/* --------------------------------------------------------------------------+
   ChangeOp.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   -------------------------------------------------------------------------*/
package ca.dsrg.mirador.merge;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.71 - Dec 3, 2010
 * @author  Stephen Barrett
 */
abstract public class ChangeOp {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  What is returned by the method.
   * @category  getter
   */
  public MergeSide getMergeSide() {
    return side_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  public boolean isAtomic() {
    return (this instanceof AtomicChangeOp);
  }


  public boolean isBefore(ChangeOp to_test) {
    boolean rc = false;

    if (to_test.isAtomic())
      rc = isBefore((AtomicChangeOp) to_test);
    else if (to_test.isComposite())
      rc = isBefore((CompositeChangeOp) to_test);
    else if (to_test.isContradict())
      rc = isBefore((ContradictChangeOp) to_test);

    return rc;
  }


  public boolean isBefore(AtomicChangeOp to_test) {
    return false;
  }


  public boolean isBefore(CompositeChangeOp to_test) {
    return false;
  }


  public boolean isBefore(ContradictChangeOp to_test) {
    return false;
  }


  public boolean isComposite() {
    return (this instanceof CompositeChangeOp);
  }


  public boolean isConflict() {
    return false;
  }


  public boolean isContradict() {
    return (this instanceof ContradictChangeOp);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public CompositeChangeOp addToComposite(CompositeChangeOp add_to) {
    return add_to;
  }


  public ContradictChangeOp addToPartition(ContradictChangeOp add_to) {
    return add_to;
  }


  // Instance data ----------------------------------------------------------
  protected MergeSide side_ = MergeSide.NONE;
  // End instance data ------------------------------------------------------
}

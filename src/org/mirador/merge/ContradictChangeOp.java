/* --------------------------------------------------------------------------+
   ContradictOpChange.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.MiradorException;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.71 - Dec 3, 2010
 * @author  Stephen Barrett
 */
public class ContradictChangeOp extends ConflictChangeOp {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public ContradictChangeOp(ChangeOp change) {
    if (change.getMergeSide() == MergeSide.LEFT)
      setChangeLeft(change);
    else if (change.getMergeSide() == MergeSide.RIGHT)
      setChangeRight(change);
  }


  public ContradictChangeOp(ChangeOp change1, ChangeOp change2) {
    if (change1.getMergeSide() == MergeSide.LEFT)
      setChangeLeft(change1);
    else if (change1.getMergeSide() == MergeSide.RIGHT)
      setChangeRight(change1);

    if (change2.getMergeSide() == MergeSide.LEFT)
      setChangeLeft(change2);
    else if (change2.getMergeSide() == MergeSide.RIGHT)
      setChangeRight(change2);

    if (change_lf_ == null || change_rt_ == null)
      throw new MiradorException("Conflicting changes not from opposing sides.");
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public CompositeChangeOp getChangeLeft() {
    return change_lf_;
  }


  public void setChangeLeft(ChangeOp to_set) {
    if (to_set != null) {
      if (to_set instanceof AtomicChangeOp)
        change_lf_ = new CompositeChangeOp(to_set);
      else if (to_set instanceof CompositeChangeOp)
        change_lf_ = (CompositeChangeOp) to_set;
    }
  }


  public CompositeChangeOp getChangeRight() {
    return change_rt_;
  }


  public void setChangeRight(ChangeOp to_set) {
    if (to_set != null) {
      if (to_set instanceof AtomicChangeOp)
        change_rt_ = new CompositeChangeOp(to_set);
      else if (to_set instanceof CompositeChangeOp)
        change_rt_ = (CompositeChangeOp) to_set;
    }
  }


  public void clearChangeLeft() {
    change_lf_ = null;
  }


  public void clearChangeRight() {
    change_rt_ = null;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  /**
   * Gives the conflict's resolution status.
   *
   * @return  true = conflict resolved, false = conflict not resolved
   * @category  getter
   */
  public boolean isResolved() {
    return is_resolved_;
  }


  /**
   * Clears the conflict's resolution flag.
   *
   * @return  true = conflict was resolved, false = conflict was not resolved
   * @category  setter
   */
  public boolean resetResolved() {
    boolean old_resolved = is_resolved_;
    is_resolved_ = false;
    return old_resolved;
  }


  /**
   * Sets the conflict's resolution flag.
   *
   * @return  true = conflict was resolved, false = conflict was not resolved
   * @category  setter
   */
  public boolean setResolved() {
    boolean old_resolved = is_resolved_;
    is_resolved_ = true;
    return old_resolved;
  }


  /**
   * Sets the conflict's resolution flag.
   *
   * @param  is_resolved  true = can resolve, false = cannot resolve
   * @return  true = conflict was resolved, false = conflict was not resolved
   * @category  setter
   */
  public boolean setDebug(boolean is_resolved) {
    boolean old_resolved = is_resolved_;
    is_resolved_ = is_resolved;
    return old_resolved;
  }


  @Override public boolean isBefore(AtomicChangeOp to_test) {
    boolean rc = false;

    rc = change_lf_.isBefore(to_test);
    if (!rc)
      rc = change_rt_.isBefore(to_test);

    return rc;
  }


  @Override public boolean isBefore(CompositeChangeOp to_test) {
    boolean rc = false;

    rc = change_lf_.isBefore(to_test);
    if (!rc)
      rc = change_rt_.isBefore(to_test);

    return rc;
  }


  @Override public boolean isBefore(ContradictChangeOp to_test) {
    return (change_lf_.isBefore(to_test) || change_rt_.isBefore(to_test));
  }


  public boolean isComplex() {
    return (change_lf_.changesSize() > 1 || change_rt_.changesSize() > 1);
  }


  @Override public boolean isConflict() {
    return (change_lf_.changesSize() >= 1 && change_rt_.changesSize() >= 1);
  }


  public boolean isSimple() {
    return !isComplex();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  @Override public String toString() {
    return "{ " + change_lf_ + ", " + change_rt_ + " }";
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  @Override public ContradictChangeOp addToPartition(ContradictChangeOp add_to) {
    if (add_to != null) {
      if (getMergeSide() == MergeSide.LEFT)
        add_to.setChangeLeft(addToComposite(add_to.getChangeLeft()));
      else if (getMergeSide() == MergeSide.RIGHT)
        add_to.setChangeRight(addToComposite(add_to.getChangeRight()));
    }
    else
      add_to = new ContradictChangeOp(this);

    return add_to;
  }


  // Instance data ----------------------------------------------------------
  private CompositeChangeOp change_lf_;
  private CompositeChangeOp change_rt_;
  private boolean is_resolved_ = false;
  // End instance data ------------------------------------------------------
}

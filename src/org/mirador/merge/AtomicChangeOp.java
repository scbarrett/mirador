/* --------------------------------------------------------------------------+
   AtomicChangeOp.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.difference.MiradorTyper.MiradorType;
import ca.dsrg.mirador.model.EcoreExtra;
import org.eclipse.emf.ecore.ENamedElement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.71 - Dec 3, 2010
 * @author  Stephen Barrett
 */
abstract public class AtomicChangeOp extends ChangeOp {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  target  Purpose of the argument.
   * @param  side  Purpose of the argument.
   */
  public AtomicChangeOp(EcoreExtra target, MergeSide side) {
    this(target, null, side);
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  target  Purpose of the argument.
   * @param  update  Purpose of the argument.
   * @param  side  Purpose of the argument.
   */
  public AtomicChangeOp(EcoreExtra target, EcoreExtra update, MergeSide side) {
    side_ = side;
    targeted_ = target;
    updated_ = update;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public EcoreType getEcoreType() {
    return targeted_.getEcoreType();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public ENamedElement getMatch() {
    return (targeted_.getMatch() != null)
        ? targeted_.getMatch().getElement() : null;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public EcoreExtra getMatchExtra() {
    return targeted_.getMatch();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public MiradorType getMiradorType() {
    return targeted_.getMiradorType();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public ENamedElement getTargeted() {
    return (targeted_ != null) ? targeted_.getElement() : null;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @category  getter
   */
  public void setTargeted(ENamedElement element) {
    if (targeted_ != null)
      targeted_.setElement(element);
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public EcoreExtra getTargetedExtra() {
    return targeted_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @category  getter
   */
  public void setTargetedExtra(EcoreExtra extra) {
    targeted_ = extra;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public ENamedElement getUpdated() {
    return (updated_ != null) ? updated_.getElement() : null;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public EcoreExtra getUpdatedExtra() {
    return updated_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  op  Purpose of the argument.
   */
  public void addRelation(AtomicChangeOp op, Relation relation) {
    relations_.put(op, relation);
  }


  public Relation getRelation(ChangeOp op) {
    return relations_.get(op);
  }


  /**
   * Replaces the given change operation relation value in this change's map
   * of such relations. Replacement of the map's entry value is done the hard
   * way in order to maintain the map's original entry ordering.
   *
   * @param  op  Key of map entry whose value is to be replaced.
   * @param  relation  Replacement value for <i>op's</i> relation attribute.
   */
  public void replaceRelation(AtomicChangeOp op, Relation relation) {
    Set<Map.Entry<AtomicChangeOp, Relation>> op_set = relations_.entrySet();

    for (Map.Entry<AtomicChangeOp, Relation> entry : op_set) {
      if (entry.getKey() == op) {
        entry.setValue(relation);
        break;
      }
    }
  }


  public Iterator<Relation> relationIterator() {
    return relations_.values().iterator();
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public Map<AtomicChangeOp, Relation> getRelations() {
    return relations_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  @Override public boolean isComposite() {
    return false;
  }


  @Override public boolean isBefore(AtomicChangeOp to_test) {
    Relation relation = relations_.get(to_test);
    return (relation != null && relation == Relation.BEFORE);
  }


  @Override public boolean isBefore(CompositeChangeOp to_test) {
    boolean rc = false;

    for (ListIterator<AtomicChangeOp> it = to_test.changeIterator();
        it.hasNext();) {
      rc = isBefore(it.next());
      if (rc) break;
    }

    return rc;
  }


  @Override public boolean isBefore(ContradictChangeOp to_test) {
    boolean rc = false;

    rc = isBefore(to_test.getChangeLeft());
    if (!rc)
      rc = isBefore(to_test.getChangeRight());

    return rc;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  @Override public String toString() {
    return targeted_.toString() + " (" + targeted_.getId() + ','
        + ((getMergeSide() == MergeSide.LEFT) ? 'L' : 'R') + ')';
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  @Override public CompositeChangeOp addToComposite(CompositeChangeOp add_to) {
    if (add_to != null) {
      if (getMergeSide() != add_to.getMergeSide())
        throw new MiradorException("Added component is from opposite side.");

      add_to.addChange(this);
    }
    else
      add_to = new CompositeChangeOp(this);

    return add_to;
  }


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
  private EcoreExtra targeted_;
  private EcoreExtra updated_;

  private Map<AtomicChangeOp, Relation> relations_ =
      new HashMap<AtomicChangeOp, Relation>();
  // End instance data ------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**
   * Tag values to indicate atomic change operations relationships.
   *
   * @since   v4.5 - Apr 9, 2010
   * @author  Stephen Barrett
   */
  static public enum Relation { BEFORE, CONFLICT, REQUIRE }
  // End nested types -------------------------------------------------------
}

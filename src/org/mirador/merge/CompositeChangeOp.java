/* --------------------------------------------------------------------------+
   CompositeOpChange.java - High-level description of module and place in system.
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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.71 - Dec 3, 2010
 * @author  Stephen Barrett
 */
public class CompositeChangeOp extends ChangeOp {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public CompositeChangeOp() {
  }


  public CompositeChangeOp(ChangeOp change) {
    side_ = change.getMergeSide();
    change.addToComposite(this);
  }


  public CompositeChangeOp(ChangeOp change1, ChangeOp change2) {
    if (change1.getMergeSide() != change2.getMergeSide())
      throw new MiradorException("Components are from opposite sides.");

    side_ = change1.getMergeSide();
    change1.addToComposite(this);
    change2.addToComposite(this);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  /**
   * Fetches specified change operation from those that make up this composite.
   *
   * @param  index  Index of operation to retrieve from collection.
   * @return  Requested change operation if exists, otherwise null.
   * @category  collection
   */
  public AtomicChangeOp getChange(int index) {
    return changes_.get(index);
  }


  public ChangeOp setChange(int index, AtomicChangeOp to_set) {
    return changes_.set(index, to_set);
  }


  /**
   * Removes the given change operation from those that make up this block.
   *
   * @param  index  Index of operation to remove from collection.
   * @category  collection
   */
  public AtomicChangeOp removeChange(int index) {
    return changes_.remove(index);
  }


  public boolean addChange(AtomicChangeOp to_add) {
    boolean is_added = false;

    for (int i = 0; i < changes_.size(); ++i) {
      if (to_add.isBefore(changes_.get(i))) {
        changes_.add(i, to_add);
        is_added = true;
        isCycle(to_add, i + 1);
        break;
      }
    }

    if (!is_added)
      is_added = changes_.add(to_add);

    return is_added;
  }


  public int changesSize() {
    return changes_.size();
  }


  /**
   * Gives access to the similarity evaluators used in this merge session.
   *
   * @return  Iterator over evaluators.
   * @category  collection
   */
  public ListIterator<AtomicChangeOp> changeIterator() {
    return changes_.listIterator();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  @Override public boolean isBefore(AtomicChangeOp to_test) {
    boolean rc = false;

    for (ListIterator<AtomicChangeOp> it = changeIterator(); it.hasNext();) {
      rc = it.next().isBefore(to_test);
      if (rc) break;
    }

    return rc;
  }


  @Override public boolean isBefore(CompositeChangeOp to_test) {
    boolean rc = false;

    for (ChangeOp composite : changes_) {
      if (composite.isBefore(to_test)) {
        rc = true;
        break;
      }
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


  private boolean isCycle(ChangeOp to_test, int index) {
    boolean rc = false;

    for (int i = index; i < changes_.size(); ++i) {
      if (changes_.get(i).isBefore(to_test))
        throw new MiradorException("Composite change has circular reference.");
    }

    return rc;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  @Override public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("[ ");

    for (ChangeOp composite : changes_) {
      buf.append('[' + composite.toString() + "], ");
    }

    int len = buf.length();
    if (!changes_.isEmpty())
      buf.delete(len - 2, len);
    else
      buf.deleteCharAt(len - 1);
    buf.append(" ]");

    return buf.toString();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  @Override public CompositeChangeOp addToComposite(CompositeChangeOp add_to) {
    if (add_to != null)
      if (getMergeSide() != add_to.getMergeSide())
        throw new MiradorException("Added component is from opposite side.");
    else
      add_to = new CompositeChangeOp();

    for (ChangeOp to_add : changes_) {
      to_add.addToComposite(add_to);
    }

    return add_to;
  }


  // Instance data ----------------------------------------------------------
  private List<AtomicChangeOp> changes_ = new ArrayList<AtomicChangeOp>();
  // End instance data ------------------------------------------------------
}

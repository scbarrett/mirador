/* --------------------------------------------------------------------------+
   ChangeTransaction.java - Abstract representation of a transaction of model
     change log records.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Generic container for a model change transaction -- a single high level
 * change effected against one model element -- effectively a named List of
 * ChangeRecord objects.
 *
 * @since   v0.8 - Feb 5, 2010
 * @author  Stephen Barrett
 */
abstract public class ChangeTransaction<T extends ChangeRecord> {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the ID of the transaction.
   *
   * @return  ID of the transaction.
   * @category  getter
   */
  public String getId() {
    return tx_id_;
  }


  /**
   * Gives which of the models being merged the transaction was executed on.
   *
   * @return  Which side of a merge the transaction is from.
   * @category  getter
   */
  public MergeSide getMergeSide() {
    return merge_side_;
  }


  /**
   * Gives the name of the transaction.
   *
   * @return  Name of the transaction.
   * @category  getter
   */
  public String getName() {
    return tx_name_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  /**
   * Adds the given change record to those that make up this transaction.
   *
   * @param  to_add  Record to add to collection.
   * @category  collection
   */
  public void addChange(T to_add) {
    if (to_add != null)
      changes_.add(to_add);
  }


  /**
   * Removes the given change record from those that make up this transaction.
   *
   * @param  to_remove  Record to remove from collection.
   * @category  collection
   */
  public void removeChange(T to_remove) {
    if (to_remove != null)
      changes_.remove(to_remove);
  }


  /**
   * Fetches specified change record from those that make up this transaction.
   *
   * @param  to_fetch  Index of record to retrieve from collection.
   * @return  Requested change record if exists, otherwise null.
   * @category  collection
   */
  public T getChange(int to_fetch) {
    return changes_.get(to_fetch);
  }


  /**
   * Gives access to the change records that make up this transaction.
   *
   * @return  Iterator over change records.
   * @category  collection
   */
  public ListIterator<T> changeIterator() {
    return changes_.listIterator();
  }


  /**
   * Gives access to the change records that make up this transaction.
   *
   * @param  index  Starting index of iterator.
   * @return  Iterator over change records.
   * @category  collection
   */
  public ListIterator<T> changeIterator(int index) {
    return changes_.listIterator(index);
  }


  /**
   * Gives the Change records that make up the transaction.
   *
   * @return  Change records that make up the transaction.
   * @category  collection
   */
  public List<T> getChanges() {
    return changes_;
  }


  /**
   * Gives the no elements in collection flag.
   *
   * @return  true = collection is empty, false = collection is not empty
   * @category  collection
   */
  public boolean isEmpty() {
    return changes_.isEmpty();
  }


  // Instance data ----------------------------------------------------------
  /** Name of the transaction. */
  protected String tx_name_;

  /** ID of the transaction. */
  protected String tx_id_;

  /** Side of merge the transaction is from. */
  protected MergeSide merge_side_;

  /** Change records that make up the transaction. */
  protected List<T> changes_ = new ArrayList<T>();
  // End instance data ------------------------------------------------------
}

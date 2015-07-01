/* --------------------------------------------------------------------------+
   ChangeRepository.java - Abstraction of deposit point for the salient items
     of a merge session as drawn from the common, left, and right models.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Repository items include the common, left, and right change logs, lists of
   their changes, and the operations gathered into transactions.

   While not mandated by the change recorder decoupling design, it is currently
   assumed that the models are of the same type.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Overall repository of model change records, and transactional groupings for
 * the models involved in a merge. The model change logs are identified by
 * their "side" in the merge: COMMON, LEFT, or RIGHT.
 *
 * @since   v0.9 - Feb 11, 2010
 * @author  Stephen Barrett
 *
 * @see  ChangeRecord
 * @see  ChangeTransaction
 */
abstract public class ChangeRepository<T extends ChangeRecord,
    U extends ChangeTransaction<T>> {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  change_log_bs  Purpose of the argument.
   * @param  change_log_lf  Purpose of the argument.
   * @param  change_log_rt  Purpose of the argument.
   */
  public ChangeRepository(File change_log_bs, File change_log_lf,
      File change_log_rt) {
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives changes of the common base model.
   *
   * @return  Base model changes.
   * @category  getter
   */
  public List<T> getChangesBase() {
    return changes_bs_;
  }


  /**
   * Gives changes of the left model.
   *
   * @return  Left model changes.
   * @category  getter
   */
  public List<T> getChangesLeft() {
    return changes_lf_;
  }


  /**
   * Gives changes of the right model.
   *
   * @return  Right model changes.
   * @category  getter
   */
  public List<T> getChangesRight() {
    return changes_rt_;
  }


  /**
   * Gives changes of the common base model in common format.
   *
   * @return  Base model changes in recorder neutral format.
   * @category  getter
   */
//  public List<MiradorRecord> getMiradorChangesBase() {
//    return mchanges_bs_;
//  }


  /**
   * Gives changes of the left model in common format.
   *
   * @return  Left model changes in recorder neutral format.
   * @category  getter
   */
//  public List<MiradorRecord> getMiradorChangesLeft() {
//    return mchanges_lf_;
//  }


  /**
   * Gives changes of the right model in common format.
   *
   * @return  Right model changes in recorder neutral format.
   * @category  getter
   */
//  public List<MiradorRecord> getMiradorChangesRight() {
//    return mchanges_rt_;
//  }


  /**
   * Gives change transactions of the common base model in common format.
   *
   * @return  Base model change transactions in recorder neutral format.
   * @category  getter
   */
//  public List<MiradorTransaction> getMiradorTransactionsBase() {
//    return mtxs_bs_;
//  }


  /**
   * Gives change transactions of the left model in common format.
   *
   * @return  Left model change transactions in recorder neutral format.
   * @category  getter
   */
//  public List<MiradorTransaction> getMiradorTransactionsLeft() {
//    return mtxs_lf_;
//  }


  /**
   * Gives change transactions of the right model in common format.
   *
   * @return  Right model change transactions in recorder neutral format.
   * @category  getter
   */
//  public List<MiradorTransaction> getMiradorTransactionsRight() {
//    return mtxs_rt_;
//  }


  /**
   * Gives change transactions of the common base model.
   *
   * @return  Base model change transactions.
   * @category  getter
   */
  public List<U> getTransactionsBase() {
    return txs_bs_;
  }


  /**
   * Gives change transactions of the left model.
   *
   * @return  Left model change transactions.
   * @category  getter
   */
  public List<U> getTransactionsLeft() {
    return txs_lf_;
  }


  /**
   * Gives change transactions of the right model.
   *
   * @return  Right model change transactions.
   * @category  getter
   */
  public List<U> getTransactionsRight() {
    return txs_rt_;
  }


  // Instance data ----------------------------------------------------------
  /** Model change operations extracted from the change logs. */
  protected List<T> changes_bs_ = new ArrayList<T>();
  protected List<T> changes_lf_ = new ArrayList<T>();
  protected List<T> changes_rt_ = new ArrayList<T>();

  /** Model change operations from logs grouped into transactions. */
  protected List<U> txs_bs_ = new ArrayList<U>();
  protected List<U> txs_lf_ = new ArrayList<U>();
  protected List<U> txs_rt_ = new ArrayList<U>();

  /** Model change operations in Mirador's recorder neutral format. */
//  protected List<MiradorRecord> mchanges_bs_ = new ArrayList<MiradorRecord>();
//  protected List<MiradorRecord> mchanges_lf_ = new ArrayList<MiradorRecord>();
//  protected List<MiradorRecord> mchanges_rt_ = new ArrayList<MiradorRecord>();

  /** Model change transactions in Mirador's recorder neutral format. */
//  protected List<MiradorTransaction> mtxs_bs_ =
//      new ArrayList<MiradorTransaction>();
//  protected List<MiradorTransaction> mtxs_lf_ =
//      new ArrayList<MiradorTransaction>();
//  protected List<MiradorTransaction> mtxs_rt_ =
//      new ArrayList<MiradorTransaction>();
  // End instance data ------------------------------------------------------
}

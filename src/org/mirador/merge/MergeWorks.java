/* --------------------------------------------------------------------------+
   MergeWorks.java - Concrete deposit point for the salient items of a
     merge session as drawn from tool-neutral common, left, and right models.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Besides storing the change logs, operations, and transactions of a merge
   session, the concrete repository acts as a cache for the Ecore models that
   it reconstructs from the logs as part of the change recorder decoupling
   mechanism. The similarity evaluators selected to match model elements, and
   the decision tables that drive the merge are also kept here.

   Though a base change log is part of the repository, by their nature Fujaba
   models always contain this common ancestor model.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.merge;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.decision.DecisionTable;
import ca.dsrg.mirador.match.SimilarityEvaluator;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.45 - Oct 23, 2010
 * @author  Stephen Barrett
 */
public class MergeWorks {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the before predicate decision table.
   *
   * @return  Before predicate decision table.
   * @category  getter
   */
  public DecisionTable getBeforeTable() {
    return before_table_;
  }


  /**
   * Assigns the before predicate decision table.
   *
   * @param  table  Before predicate decision table.
   * @category  setter
   */
  public void setBeforeTable(DecisionTable table) {
    before_table_ = table;
  }


  /**
   * Gives the before predicate's table definition file.
   *
   * @return  Definition file for the before predicate table.
   * @category  getter
   */
  public File getBeforeTableFile() {
    return before_file_;
  }


  /**
   * Assigns the before predicate's table definition file.
   *
   * @param  file  Definition file for the before predicate table.
   * @category  setter
   */
  public void setBeforeFile(File file) {
    before_file_ = file;
  }


  /**
   * Gives the model element matching file
   *
   * @return  Element matching file.
   * @category  getter
   */
  public File getElementMatchFile() {
    return match_file_;
  }


  /**
   * Assigns the model element matching file
   *
   * @param  file  Element matching file.
   * @category  setter
   */
  public void setElementMatchFile(File file) {
    match_file_ = file;
  }


  /**
   * Gives the side of the merge that is in charge.
   *
   * @return  Master side of the merge.
   * @category  getter
   */
  public MergeSide getMasterSide() {
    return master_side_;
  }


  /**
   * Puts the left side of the merge in charge.
   *
   * @category  setter
   */
  public void setMasterSideLeft() {
    master_side_ = MergeSide.LEFT;
    if (resolve_table_ != null)
      resolve_table_.setMasterSide(master_side_);
  }


  /**
   * Puts the right side of the merge in charge.
   *
   * @category  setter
   */
  public void setMasterSideRight() {
    master_side_ = MergeSide.RIGHT;
    if (resolve_table_ != null)
      resolve_table_.setMasterSide(master_side_);
  }


  /**
   * Gives the change conflict resolution decision table.
   *
   * @return  Conflict resolution decision table.
   * @category  getter
   */
  public DecisionTable getResolveTable() {
    return resolve_table_;
  }


  /**
   * Assigns the change conflict resolution decision table.
   *
   * @param  table  Change conflict resolution decision table.
   * @category  setter
   */
  public void setResolveTable(DecisionTable table) {
    resolve_table_ = table;
  }


  /**
   * Gives the change conflict resolution's table definition file.
   *
   * @return  Definition file for the change conflict resolution table.
   * @category  getter
   */
  public File getResolveTableFile() {
    return resolve_file_;
  }


  /**
   * Assigns the change conflict resolution's table definition file.
   *
   * @param  file  Definition file for the change conflict resolution table.
   * @category  setter
   */
  public void setResolveTableFile(File file) {
    resolve_file_ = file;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  /**
   * Add the given similarity evaluator to the repository's collection.
   *
   * @param  to_add  Evaluator to add to collection.
   * @category  collection
   */
  public void addEvaluator(SimilarityEvaluator to_add) {
    evaluators_.put(to_add.getLabel(), to_add);
  }


  /**
   * Removes the similarity evaluator with the given key from the repository's
   * collection.
   *
   * @param  key  Key of evaluator to remove from collection.
   * @category  collection
   */
  public void removeEvaluator(String key) {
    if (key != null)
      evaluators_.remove(key);
  }


  /**
   * Gives access to the similarity evaluators used in this merge session.
   *
   * @return  Iterator over evaluators.
   * @category  collection
   */
  public Iterator<SimilarityEvaluator> evaluatorIterator() {
    return evaluators_.values().iterator();
  }


  // Instance data ----------------------------------------------------------
  /** Before table and definition file. */
  private DecisionTable before_table_;
  private File before_file_;

  /** Conflict table and definition file. */
  private DecisionTable resolve_table_;
  private File resolve_file_;

  /** Element matching file. */
  private File match_file_;

  /** Which side of a merge is in charge. */
  private MergeSide master_side_;

  /** Strategies chosen by user for evaluation of model element similarity. */
  private Map<String, SimilarityEvaluator> evaluators_ =
      new LinkedHashMap<String, SimilarityEvaluator>();
  // End instance data ------------------------------------------------------
}

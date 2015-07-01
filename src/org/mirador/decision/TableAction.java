/* --------------------------------------------------------------------------+
   DecisionAction.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.DecisionThrowException;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import java.util.TreeMap;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.23 - Apr 13, 2010
 * @author  Stephen Barrett
 */
public abstract class TableAction {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  name  Purpose of the argument.
   */
  public TableAction(String name) {
    name_ = name;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public String getName() {
    return name_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  public void steps(int... steps) {
    steps_ = steps;
  }


  /**
   * Fetches specified change record from those that make up this transaction.
   *
   * @param  to_fetch  Index of record to retrieve from collection.
   * @return  Requested change record if exists, otherwise null.
   * @category  collection
   */
  public int step(int to_fetch) {
    return steps_[to_fetch];
  }


  public int[] getSteps() {
    return steps_;
  }


  public int size() {
    return steps_.length;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  @Override public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < steps_.length; ++i) {
      if (i != 0)
        buf.append("  ");

      buf.append(steps_[i]);
    }

    return buf.toString();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public abstract boolean doAction(ActionSeq steps, Object... objs);


  // Instance data --------------------------------------------------------
  private String name_;
  private int[] steps_;
  // End instance data ----------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.24 - Apr 14, 2010
   * @author  Stephen Barrett
   */
  static public class ActionSeq {
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
    ActionSeq(int rule_index, DecisionTable table) {
      table_ = table;
      rule_index_ = rule_index;

      for (TableAction table_action : table.getActions()) {
        int step_num = table_action.step(rule_index_);

        if (step_num != 0)
          actions_.put(step_num, table_action);
      }
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
    public TableAction getLastAction() {
      return actions_.get(last_step_);
    }


    /**
     * Gives which of the models being merged the change was executed on.
     *
     * @return  Which side of a merge the change record is from.
     * @category  getter
     */
    public MergeSide getMergeSide() {
      return merge_side_;
    }


    /**
     * Assigns which of the models being merged the change was executed on.
     *
     * @param  merge_side  Which side of a merge the change record is from.
     * @category  setter
     */
    public void setMergeSide(MergeSide merge_side) {
      merge_side_ = merge_side;
    }


    public Tristate getResult() {
      return result_;
    }


    public void setResult(Tristate result) {
      result_ = result;
    }


    public int getRuleIndex() {
      return rule_index_;
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
    public void executeAll(Object... objs) {
      int step_num = 1;

      while (step_num > 0) {
        step_num = executeStep(objs);
        Debug.dbg.print(new Integer(getRuleIndex() + 1).toString()
            + ':' + getLastAction().getName() + ' ');
      }
    }


    public int executeStep(Object... objs) {
      if (last_step_ < actions_.size()) {
        TableAction action = actions_.get(++last_step_);
        boolean rc = action.doAction(this, objs);

        if (!rc) {
          throw new DecisionThrowException(table_.getTableName(), action.name_,
              table_.getFileName(), rule_index_ + 1);
        }
      }

      return (last_step_ != actions_.size()) ? last_step_ : 0;
    }


    // Instance data --------------------------------------------------------
    private DecisionTable table_;
    private int rule_index_;
    private int last_step_ = 0;
    private Tristate result_ = Tristate.UNDEF;
    private MergeSide merge_side_ = MergeSide.NONE;

    TreeMap<Integer, TableAction> actions_ =
        new TreeMap<Integer, TableAction>();
    // End instance data ----------------------------------------------------
  }
  // End nested types -------------------------------------------------------


  // Class data -------------------------------------------------------------
  // End class data ---------------------------------------------------------
}

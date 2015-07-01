/* --------------------------------------------------------------------------+
   ChangeOpPlane.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.merge;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.MiradorException;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.decision.DecisionTable;
import ca.dsrg.mirador.decision.Tristate;
import ca.dsrg.mirador.decision.TableAction.ActionSeq;
import ca.dsrg.mirador.merge.AtomicChangeOp.Relation;
import ca.dsrg.mirador.model.AlteredEElement;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import ca.dsrg.mirador.model.ModelRepository;
import org.eclipse.emf.ecore.ENamedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.41 - Aug 30, 2010
 * @author  Stephen Barrett
 */
public class ChangeOpPlane {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  repository  Purpose of the argument.
   */
  public ChangeOpPlane(ModelRepository repository, MergeWorks works) {
    model_repo_ = repository;
    merge_works_ = works;
    initialize();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the before predicate decision table.
   *
   * @return  Before predicate decision table.
   * @category  getter
   */
  public DecisionTable getBeforeTable() {
    return merge_works_.getBeforeTable();  //??3 Move here?
  }


  /**
   * Gives the change conflict decision table.
   *
   * @return  Conflict decision table.
   * @category  getter
   */
  public DecisionTable getResolveTable() {
    return merge_works_.getResolveTable();  //??3 Move here?
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  /**
   * Predicate to test the relative ordering of two change operations. Returns
   * true if the first, or leading operation claims precedence over the second,
   * or following operation.
   *
   * @param  to_test1  Change record of operation to test for precedence.
   * @param  to_test2  Change record of operation testing against.
   * @return  true = lead claims precedence, false = no precedence claimed
   */
  private boolean isBefore(AtomicChangeOp to_test1, AtomicChangeOp to_test2) {
    boolean rc = false;
    Debug.dbg.print(" [");
    ActionSeq steps = getBeforeTable().evaluate(to_test1, to_test2);

    if (steps != null) {
      steps.executeAll(to_test1, to_test2);
      rc = steps.getResult().toFalse();
    }

    return rc;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  static public void dumpOpRelations(List<AtomicChangeOp> ops_lf,
      List<AtomicChangeOp> ops_rt) {
    Debug.dbg.println("\n\n\n\t    --- LEFT CHANGE OP PAIR RELATIONS ---");
    for (AtomicChangeOp out_op : ops_lf) {
      Debug.dbg.print(out_op);

      String rel = null;
      Map<AtomicChangeOp, Relation> relations = out_op.getRelations();
      for (Map.Entry<AtomicChangeOp, Relation> entry: relations.entrySet()) {
        AtomicChangeOp in_op = entry.getKey();

        if (rel != null)
          Debug.dbg.print(',');

        switch (entry.getValue()) {
          case BEFORE:
            rel = " < ";
          break;

          case CONFLICT:
            rel = " >< ";
          break;

          case REQUIRE:
            rel = " R ";
          break;
        }

        Debug.dbg.print(rel + in_op);
      }
      Debug.dbg.println();
    }


    Debug.dbg.println("\n\n\n\t    --- RIGHT CHANGE OP PAIR RELATIONS ---");
    for (AtomicChangeOp out_op : ops_rt) {
      Debug.dbg.print(out_op);

      String rel = null;
      Map<AtomicChangeOp, Relation> relations = out_op.getRelations();
      for (Map.Entry<AtomicChangeOp, Relation> entry: relations.entrySet()) {
        AtomicChangeOp in_op = entry.getKey();

        if (rel != null)
          Debug.dbg.print(',');

        switch (entry.getValue()) {
          case BEFORE:
            rel = " < ";
          break;

          case CONFLICT:
            rel = " >< ";
          break;

          case REQUIRE:
            rel = " R ";
          break;
        }

        Debug.dbg.print(rel + in_op);
      }
      Debug.dbg.println();
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
//  private void playChangeOps(MiradorModel merged_model) { // TODO:3 Investigate using ETL.
//    for (ChangeOp op : changes_list_) {
//      if (op.isConflict())
//        break;  // At frontier.
//    }
//  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  private void extractAtomicChangeOps() {
    changes_lf_ = new ArrayList<AtomicChangeOp>();
    changes_rt_ = new ArrayList<AtomicChangeOp>();
    MiradorModel dmodel_lf = model_repo_.getDiffModelLeft();

    for (Iterator<EcoreExtra> it = dmodel_lf.extraIterator();
        it.hasNext();) {
      EcoreExtra tar_lf = it.next();

      switch (tar_lf.getMiradorType()) {
        case ADD:
          changes_lf_.add(new AddChangeOp(tar_lf, MergeSide.LEFT));
        break;

        case DELETE:
          changes_lf_.add(new DeleteChangeOp(tar_lf, MergeSide.LEFT));
        break;

        case ALTER:  // TODO:3 One change operation for each property changed. Too much work?
          ENamedElement up_lf =
              ((AlteredEElement) tar_lf.getElement()).getUpdated();
          changes_lf_.add(new AlterChangeOp(tar_lf, dmodel_lf.getExtra(up_lf),
              MergeSide.LEFT));
        break;
      }
    }


    MiradorModel dmodel_rt = model_repo_.getDiffModelRight();
    for (Iterator<EcoreExtra> it = dmodel_rt.extraIterator();
        it.hasNext();) {
      EcoreExtra tar_rt = it.next();

      switch (tar_rt.getMiradorType()) {
        case ADD:
          changes_rt_.add(new AddChangeOp(tar_rt, MergeSide.RIGHT));
        break;

        case DELETE:
          changes_rt_.add(new DeleteChangeOp(tar_rt, MergeSide.RIGHT));
        break;

        case ALTER:
          ENamedElement up_rt =
              ((AlteredEElement) tar_rt.getElement()).getUpdated();
          changes_rt_.add(new AlterChangeOp(tar_rt, dmodel_rt.getExtra(up_rt),
              MergeSide.RIGHT));
        break;
      }
    }
  }


  private void initialize() { // TODO:3 Move reentrant stuff to merge().
    loadTableDefinitions();
    extractAtomicChangeOps();
    markPrecedence();
    partitionChanges();
    resolveConflicts();
    orderChangeOps();

    model_repo_.setChangeList(changes_list_);
    model_repo_.setChangeMap(changes_map_);
    model_repo_.setChangeSet(changes_set_);
  }


  private void loadTableDefinitions() {
    DecisionTable table =
        DecisionTable.loadTableDefinition(merge_works_.getBeforeTableFile());

    if (table == null)
      table = before_same_;

//    table.setRepository(model_repo_);
    merge_works_.setBeforeTable(table);  // Store here??
    DecisionTable.dumpTable(table, "");


    table =
        DecisionTable.loadTableDefinition(merge_works_.getResolveTableFile());

    if (table == null)
      table = conflict_main_;

//    table.setRepository(model_repo_);
    merge_works_.setResolveTable(table);
    DecisionTable.dumpTable(table, "");
  }


  // Test all change operation pairs for order and conflict.
  private void markPrecedence() {
    // Test left-left change operations for ordering (i.e., quadrant II).
    Debug.dbg.println("\n\n\n\t    --- SAME SIDE ORDER (left1, left2) ---");
    for (int i = 0; i < changes_lf_.size(); ++i) { // a of a.isBefore(b).
      AtomicChangeOp out_op = changes_lf_.get(i);

      for (int j = 0; j < changes_lf_.size(); ++j) { // b of a.isBefore(b).
        if (j == i)
          continue;

        Debug.dbg.print(out_op);
        AtomicChangeOp in_op = changes_lf_.get(j);

        if (isBefore(out_op, in_op)) {
          out_op.addRelation(in_op, Relation.BEFORE);
          Debug.dbg.print("<] ");
        }
        else
          Debug.dbg.print("~] ");

        Debug.dbg.println(in_op);
      }
    }


    // Test right-right change operations for ordering (i.e., quadrant IV).
    Debug.dbg.println("\n\n\n\t    --- SAME SIDE ORDER (right1, right2) ---");
    for (int i = 0; i < changes_rt_.size(); ++i) { // a of a.isBefore(b).
      AtomicChangeOp out_op = changes_rt_.get(i);

      for (int j = 0; j < changes_rt_.size(); ++j) { // b of a.isBefore(b).
        if (j == i)
          continue;

        Debug.dbg.print(out_op);
        AtomicChangeOp in_op = changes_rt_.get(j);

        if (isBefore(out_op, in_op)) {
          out_op.addRelation(in_op, Relation.BEFORE);
          Debug.dbg.print("<] ");
        }
        else
          Debug.dbg.print("~] ");

        Debug.dbg.println(in_op);
      }
    }


    // Test left-right change operations for ordering (i.e., quadrant I).
    Debug.dbg.println("\n\n\n\t    --- CROSS SIDE ORDER (left, right) ---");
    for (int i = 0; i < changes_lf_.size(); ++i) { // a of a.isBefore(b).
      AtomicChangeOp out_op = changes_lf_.get(i);

      for (int j = 0; j < changes_rt_.size(); ++j) { // b of a.isBefore(b)
        Debug.dbg.print(out_op);
        AtomicChangeOp in_op = changes_rt_.get(j);

        if (isBefore(out_op, in_op)) {
          out_op.addRelation(in_op, Relation.BEFORE);
          Debug.dbg.print("<] ");
        }
        else
          Debug.dbg.print("~] ");

        Debug.dbg.println(in_op);
      }
    }


    // Test right-left change operations for ordering (i.e., quadrant III).
    Debug.dbg.println("\n\n\n\t    --- CROSS SIDE ORDER (right, left) ---");
    for (int i = 0; i < changes_rt_.size(); ++i) { // a of a.isBefore(b).
      AtomicChangeOp out_op = changes_rt_.get(i);

      for (int j = 0; j < changes_lf_.size(); ++j) { // b of a.isBefore(b)
        Debug.dbg.print(out_op);
        AtomicChangeOp in_op = changes_lf_.get(j);

          // Check for conflict; indicated by true isBefore() on both sides.
        if (isBefore(out_op, in_op)) {
          if (in_op.getRelations().get(out_op) == Relation.BEFORE) {
            out_op.addRelation(in_op, Relation.CONFLICT);
            in_op.replaceRelation(out_op, Relation.CONFLICT);
            Debug.dbg.print("><] ");
          }
          else {
            out_op.addRelation(in_op, Relation.BEFORE);
            Debug.dbg.print("<] ");
          }
        }
        else
          Debug.dbg.print("~] ");

        Debug.dbg.println(in_op);
      }
    }

    dumpOpRelations(changes_lf_, changes_rt_);
  }


  private void orderChangeOps() {
    changes_list_ = new ArrayList<ChangeOp>(changes_set_);
    int sz = changes_set_.size();
    int lo_idx;
    int hi_idx;

    // Move any conflicts to bottom of change op list.
    for (lo_idx = 0, hi_idx = sz - 1; lo_idx <= hi_idx; ++lo_idx) {
      ChangeOp lo_blk = changes_list_.get(lo_idx);

      if (lo_blk.isConflict()) {
        for (; hi_idx > lo_idx; --hi_idx) {
          ChangeOp hi_blk = changes_list_.get(hi_idx);

          if (!hi_blk.isConflict()) {
            changes_list_.set(hi_idx--, lo_blk);
            changes_list_.set(lo_idx, hi_blk);
            break;
          }
        }
      }
    }


    // Move unresolved conflicts below resolved conflicts.
    for (lo_idx = hi_idx, hi_idx = sz - 1; lo_idx <= hi_idx; ++lo_idx) {
      if (!(changes_list_.get(lo_idx) instanceof ContradictChangeOp))
        continue;

      ContradictChangeOp lo_blk = (ContradictChangeOp) changes_list_.get(lo_idx);

      if (!lo_blk.isResolved()) {
        for (; hi_idx > lo_idx; --hi_idx) {
          ContradictChangeOp hi_blk = (ContradictChangeOp) changes_list_.get(hi_idx);

          if (hi_blk.isResolved()) {
            changes_list_.set(hi_idx--, lo_blk);
            changes_list_.set(lo_idx, hi_blk);
            break;
          }
        }
      }
    }
//    changes_list_.clear();
//    changes_list_.add(changes_map_.get(ops_lf_.get(5)));
//    changes_list_.add(changes_map_.get(ops_lf_.get(4)));
//    changes_list_.add(changes_map_.get(ops_rt_.get(2)));
//    changes_list_.add(changes_map_.get(ops_lf_.get(0)));
//    changes_list_.add(changes_map_.get(ops_lf_.get(3)));
//    changes_list_.add(changes_map_.get(ops_lf_.get(1)));

//    Debug.dbg.println("\n\n\n\t    --- UNORDERED CHANGE OP PARTITIONS ---");
//    for (Change blk : changes_list_)
//      Debug.dbg.println(blk);


    boolean no_change = false;
    for (int i = 1; i <= sz && !no_change; ++i) { // TODO:3 Find better exit criteria.
    no_change = true;

      for (int j = 0; j < sz; ++j) {
        ChangeOp out_blk = changes_list_.get(j);

        for (int k = j + 1; k < sz; ++k) {
          ChangeOp in_blk = changes_list_.get(k);

          if (in_blk.isBefore(out_blk)) {
            changes_list_.remove(k);
            changes_list_.add(j, in_blk);
            no_change = false;
            out_blk = in_blk;
            k = j + 1;
          }
        }
      }

      if (i == sz && sz > 1)  // FIXME: Hack to handle lists of only one conflict.
        throw new MiradorException("Change op partition has circular reference.");
    }

//    Debug.dbg.println("\n\n\n\t    --- UNORDERED CHANGE OP PARTITIONS ---");
//    for (Change blk : changes_list_)
//      Debug.dbg.println(blk);


    int top = 0;
    int bot = 1;
    for (int i = 0; i < sz; ++i) {
      if (changes_list_.get(i).isConflict()) {
        bot = i;
        break;
      }
    }


    for (int i = bot; i < sz; ++i) {
      ChangeOp out_blk = changes_list_.get(i);

      if (!out_blk.isConflict()) { // FIXME:2 Sort resolved conflicts.
        for (int j = i - 1; j >= top; --j) {
          ChangeOp in_blk = changes_list_.get(j);

          if (in_blk.isBefore(out_blk))
            break;
          else {
            changes_list_.remove(i);
            changes_list_.add(j, out_blk);
            bot = Math.max(bot, i);

            if (--i == top) {
              ++top;
              i = bot;
            }
          }
        }
      }
    }

    // TODO:2 Resolved conflicts should remain paired in moving.
    Debug.dbg.println("\n\n\n\t    --- ORDERED & RESOLVED CHANGE OPS ---");
    for (ChangeOp blk : changes_list_)
      Debug.dbg.println(blk);
  }


  private void partitionChanges() {
//    AtomicChange L1 = new AtomicChange(changes_lf_.get(0).getTargetedExtra(),
//        changes_lf_.get(0).getUpdatedExtra(), MergeSide.LEFT);
//    AtomicChange L2 = new AtomicChange(changes_lf_.get(1).getTargetedExtra(),
//        changes_lf_.get(1).getUpdatedExtra(), MergeSide.LEFT);
//    AtomicChange L3 = new AtomicChange(changes_lf_.get(2).getTargetedExtra(),
//        changes_lf_.get(2).getUpdatedExtra(), MergeSide.LEFT);
//    AtomicChange L4 = new AtomicChange(changes_lf_.get(3).getTargetedExtra(),
//        changes_lf_.get(3).getUpdatedExtra(), MergeSide.LEFT);
//    AtomicChange L5 = new AtomicChange(changes_lf_.get(4).getTargetedExtra(),
//        changes_lf_.get(4).getUpdatedExtra(), MergeSide.LEFT);
//    AtomicChange L6 = new AtomicChange(changes_lf_.get(5).getTargetedExtra(),
//        changes_lf_.get(5).getUpdatedExtra(), MergeSide.LEFT);
//    AtomicChange R1 = new AtomicChange(changes_rt_.get(0).getTargetedExtra(),
//        changes_lf_.get(0).getUpdatedExtra(), MergeSide.RIGHT);
//    AtomicChange R2 = new AtomicChange(changes_rt_.get(1).getTargetedExtra(),
//        changes_lf_.get(1).getUpdatedExtra(), MergeSide.RIGHT);
//    AtomicChange R3 = new AtomicChange(changes_rt_.get(2).getTargetedExtra(),
//        changes_lf_.get(2).getUpdatedExtra(), MergeSide.RIGHT);
//    AtomicChange R4 = new AtomicChange(changes_rt_.get(3).getTargetedExtra(),
//        changes_lf_.get(3).getUpdatedExtra(), MergeSide.RIGHT);
//    AtomicChange R5 = new AtomicChange(changes_rt_.get(5).getTargetedExtra(),
//        changes_lf_.get(5).getUpdatedExtra(), MergeSide.RIGHT);
//
//
//    L1.addRelation(L2, Relation.BEFORE);
//    L1.addRelation(R1, Relation.CONFLICT);
//    L1.addRelation(R2, Relation.CONFLICT);
//    L2.addRelation(R4, Relation.CONFLICT);
//    L3.addRelation(R2, Relation.CONFLICT);
//    L4.addRelation(R5, Relation.CONFLICT);
//    L4.addRelation(L6, Relation.BEFORE);
//    L5.addRelation(R4, Relation.BEFORE);
//
//    R1.addRelation(R2, Relation.BEFORE);
//    R1.addRelation(L1, Relation.CONFLICT);
//    R2.addRelation(L1, Relation.CONFLICT);
//    R2.addRelation(L3, Relation.CONFLICT);
//    R3.addRelation(L3, Relation.BEFORE);
//    R4.addRelation(L2, Relation.CONFLICT);
//    R5.addRelation(L4, Relation.CONFLICT);
//
//    changes_lf_.clear(); changes_lf_.add(L1); changes_lf_.add(L2);
//        changes_lf_.add(L3); changes_lf_.add(L4); changes_lf_.add(L5);
//        changes_lf_.add(L6);
//
//    changes_rt_.clear(); changes_rt_.add(R1); changes_rt_.add(R2);
//        changes_rt_.add(R3); changes_rt_.add(R4); changes_rt_.add(R5);


    changes_map_ = new HashMap<AtomicChangeOp, ChangeOp>();

    for (AtomicChangeOp out_op : changes_lf_) {
      ContradictChangeOp out_blk =  // Map only holds conflicts so far.
          (ContradictChangeOp) changes_map_.get(out_op);

      for (Iterator<Entry<AtomicChangeOp, Relation>> it =
          out_op.getRelations().entrySet().iterator(); it.hasNext();) {
        Entry<AtomicChangeOp, Relation> rel_entry = it.next();

        if (rel_entry.getValue() == Relation.CONFLICT) {
          AtomicChangeOp in_op = rel_entry.getKey();
          ContradictChangeOp in_blk =  // Map only holds conflicts so far.
              (ContradictChangeOp) changes_map_.get(in_op);

          if (in_blk == null) {
            if (out_blk == null)
              out_blk = out_op.addToPartition(null);  // Atomic(null)

            in_op.addToPartition(out_blk);  // Atomic/Conflict(Conflict)
            changes_map_.put(in_op, out_blk);
            changes_map_.put(out_op, out_blk);
          }
          else {
            out_op.addToPartition(in_blk);  // Atomic/Conflict(Conflict)
            changes_map_.put(in_op, in_blk);
            changes_map_.put(out_op, in_blk);
          }
        }
      }
    }


    for (AtomicChangeOp out_op : changes_lf_) {
      if (changes_map_.get(out_op) == null)
        changes_map_.put(out_op, out_op);
    }


    for (AtomicChangeOp out_op : changes_rt_) {
      if (changes_map_.get(out_op) == null)
        changes_map_.put(out_op, out_op);
    }


    changes_set_ = new HashSet<ChangeOp>(changes_map_.values());
    Debug.dbg.println("\n\n\n\t    --- CHANGE OP PARTITIONS ---");
    for (ChangeOp blk : changes_set_)
      Debug.dbg.println(blk);
  }


  private void resolveConflicts() {
    Debug.dbg.println("\n\n\n\t    --- AUTO RESOLVING CONFLICTS ---");

    for (ChangeOp change : changes_set_) {
      if (change instanceof ContradictChangeOp) {
        ContradictChangeOp conflict = (ContradictChangeOp) change;
        resolveConflict(conflict);
      }
    }

    changes_set_.clear();
    changes_set_.addAll(changes_map_.values());
  }


  private boolean resolveConflict(ContradictChangeOp conflict) {
      boolean rc = true;
      StringBuffer buf = new StringBuffer(conflict.getChangeLeft() + " [");
  //    List<CompositeChangeOp> to_add = new ArrayList<CompositeChangeOp>();
  //    List<CompositeChangeOp> to_remove = new ArrayList<CompositeChangeOp>();

      ActionSeq steps = getResolveTable().evaluate(conflict);
      int step_num = 1;

      if (steps != null) {
        while (step_num > 0) {
          step_num = steps.executeStep(conflict);
          buf.append(new Integer(steps.getRuleIndex() + 1).toString()
              + ':' + steps.getLastAction().getName() + ' ');

          switch (steps.getMergeSide()) {
            case BASE:
              conflict.side_ = MergeSide.BASE;
              buf.append('^');
              conflict.setResolved();
            break;

            case LEFT:
              conflict.side_ = (conflict.side_ == MergeSide.RIGHT)
                  ? MergeSide.BOTH : MergeSide.LEFT;
              buf.append('<');
              conflict.setResolved();
            break;

            case RIGHT:
              conflict.side_ = (conflict.side_ == MergeSide.LEFT)
                  ? MergeSide.BOTH : MergeSide.RIGHT;
              buf.append('>');
              conflict.setResolved();
            break;

            case BOTH:
              conflict.side_ = MergeSide.BOTH;
              buf.append("<>");
              conflict.setResolved();
            break;

            default:
              conflict.side_ = MergeSide.NONE;
              buf.append('X');
              conflict.resetResolved();
          }


          rc &= steps.getResult().toFalse();
  //        if (rc) {
  //          if (steps.getMergeSide() == MergeSide.LEFT) {
  //             to_add.add(conflict.getChangeLeft());
  //             to_remove.add(conflict.getChangeRight());
  //          }
  //          else if (steps.getMergeSide() == MergeSide.RIGHT) {
  //             to_add.add(conflict.getChangeRight());
  //             to_remove.add(conflict.getChangeLeft());
  //          }
  //        }

          buf.append(' ');
        }

        buf.deleteCharAt(buf.length() - 1);
        Debug.dbg.println(buf + "] " + conflict.getChangeRight());


  //      if (steps != null && rc) {
  //        // TODO:2 Conflicts should remain so they may be undone or reversed.
  //        for (CompositeChangeOp op_out : to_remove) { //??:3 Assumes no composite nesting.
  //          for (int i = 0; i < op_out.changesSize(); ++i) {
  //            AtomicChangeOp op_in = op_out.getChange(i);
  //            changes_map_.remove(op_in);
  //          }
  //        }


  //        for (CompositeChangeOp op_out : to_add) { //??:3 Assumes no composite nesting.
  //          for (int i = 0; i < op_out.changesSize(); ++i) {
  //            AtomicChangeOp op_in = op_out.getChange(i);
  //            changes_map_.put(op_in, op_out);
  //          }
  //        }
  //      }
      }

      return rc;
    }


  // Instance data ----------------------------------------------------------
  private ModelRepository model_repo_;
  private MergeWorks merge_works_;

  private List<AtomicChangeOp> changes_lf_;
  private List<AtomicChangeOp> changes_rt_;

  private Map<AtomicChangeOp, ChangeOp> changes_map_;
  private Set<ChangeOp> changes_set_;
  private List<ChangeOp> changes_list_;
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  // Construct default decision tables in case none get loaded.
  static private final Tristate Y = Tristate.TRUE;
  static private final Tristate N = Tristate.FALSE;
  static private final Tristate u = Tristate.UNDEF;

  static public DecisionTable before_same_ = new DecisionTable("before_same");
  static public DecisionTable before_cross_ = new DecisionTable("before_cross");
  static public DecisionTable before_error_ = new DecisionTable("before_error");

  static { // TODO:3 Synch with external tables.
    // Evaluates before(op1, op2) for same side change operations.
    before_same_.setAuxiliary(before_cross_);

    // Conditions
    before_same_.on_same_side_.states          (Y, Y, Y, u);

    before_same_.op1_is_add_.states           (Y, Y, u, u);
    before_same_.op1_is_delete_.states        (u, u, Y, u);
    before_same_.op1_is_alter_.states         (u, u, u, u);
    before_same_.op1_is_class_.states         (u, Y, u, u);

    before_same_.op2_is_add_.states           (Y, Y, u, u);
    before_same_.op2_is_delete_.states        (u, u, Y, u);
    before_same_.op2_is_alter_.states         (u, u, u, u);
    before_same_.op2_is_reference_.states     (u, Y, u, u);

    before_same_.op1_contains_op2_.states    (Y, u, u, u);
    before_same_.op2_contains_op1_.states    (u, u, Y, u);
    before_same_.op2_is_ref_to_op1_.states   (u, Y, u, u);

    before_same_.aux_match_.states             (u, u, u, Y);  // Invokes auxiliary table.

    // Actions
    before_same_.do_true_.steps                (1, 1, 1, 1);  // Put op1 before op2.


    before_same_.addCondition(before_same_.on_same_side_);
    before_same_.addCondition(before_same_.op1_is_add_);
    before_same_.addCondition(before_same_.op1_is_delete_);
    before_same_.addCondition(before_same_.op1_is_alter_);
    before_same_.addCondition(before_same_.op1_is_class_);
    before_same_.addCondition(before_same_.op2_is_add_);
    before_same_.addCondition(before_same_.op2_is_delete_);
    before_same_.addCondition(before_same_.op2_is_alter_);
    before_same_.addCondition(before_same_.op2_is_reference_);
    before_same_.addCondition(before_same_.op1_contains_op2_);
    before_same_.addCondition(before_same_.op2_contains_op1_);
    before_same_.addCondition(before_same_.op2_is_ref_to_op1_);
    before_same_.addCondition(before_same_.aux_match_);

    before_same_.addAction(before_same_.do_true_);

    before_same_.buildMasks();


    // Evaluates before(op1, op2) for cross side change operations.
    before_cross_.setAuxiliary(before_error_);

    // Conditions
    before_cross_.on_same_side_.states        (u, u, u, u, u, u, u, u, u, N, N, N, N, N, N, u);
    before_cross_.elements_match_.states      (Y, Y, Y, Y, Y, Y, Y, Y, Y, u, u, u, u, u, u, u);

    before_cross_.op1_is_add_.states          (Y, Y, u, Y, u, u, u, Y, N, u, Y, Y, u, u, u, u);
    before_cross_.op1_is_delete_.states       (u, u, Y, u, Y, u, u, u, N, Y, u, u, Y, Y, u, u);
    before_cross_.op1_is_alter_.states        (u, u, u, u, u, Y, Y, u, N, u, u, u, u, u, Y, u);

    before_cross_.op2_is_add_.states          (Y, u, Y, u, u, u, u, N, Y, Y, u, Y, u, u, u, u);
    before_cross_.op2_is_delete_.states       (u, Y, u, u, u, Y, u, N, u, u, Y, u, Y, u, Y, u);
    before_cross_.op2_is_alter_.states        (u, u, u, Y, Y, u, Y, N, u, u, u, u, u, Y, u, u);

    before_cross_.op1_contains_op2_.states    (u, u, u, u, u, u, u, u, u, Y, u, Y, Y, Y, u, u);
    before_cross_.op2_contains_op1_.states    (u, u, u, u, u, u, u, u, u, u, Y, u, u, u, Y, u);
    before_cross_.alter_same_property_.states (u, u, u, u, u, u, Y, u, u, u, u, u, u, u, u, u);

    before_cross_.aux_match_.states           (u, u, u, u, u, u, u, u, u, u, u, u, u, u, u, N);  // Invokes auxiliary table.

    // Actions
    before_cross_.do_true_.steps              (1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0);  // Put op1 before op2.
    before_cross_.do_false_.steps             (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);  // No matches in error table.


    before_cross_.addCondition(before_cross_.on_same_side_);
    before_cross_.addCondition(before_cross_.elements_match_);
    before_cross_.addCondition(before_cross_.op1_is_add_);
    before_cross_.addCondition(before_cross_.op1_is_delete_);
    before_cross_.addCondition(before_cross_.op1_is_alter_);
    before_cross_.addCondition(before_cross_.op2_is_add_);
    before_cross_.addCondition(before_cross_.op2_is_delete_);
    before_cross_.addCondition(before_cross_.op2_is_alter_);
    before_cross_.addCondition(before_cross_.op1_contains_op2_);
    before_cross_.addCondition(before_cross_.op2_contains_op1_);
    before_cross_.addCondition(before_cross_.alter_same_property_);
    before_cross_.addCondition(before_cross_.aux_match_);

    before_cross_.addAction(before_cross_.do_true_);
    before_cross_.addAction(before_cross_.do_false_);

    before_cross_.buildMasks();


    // Evaluates errors in change op input data.
    //before_error_.setAuxiliary(null);

    // Conditions
    before_error_.on_same_side_.states         (Y, Y, Y, Y, u);

    before_error_.op1_is_add_.states          (Y, Y, u, u, u);
    before_error_.op1_is_delete_.states       (u, u, Y, Y, u);
    before_error_.op1_is_alter_.states        (u, u, u, u, u);

    before_error_.op2_is_add_.states          (u, u, Y, u, u);
    before_error_.op2_is_delete_.states       (Y, u, u, u, u);
    before_error_.op2_is_alter_.states        (u, Y, u, Y, u);

    before_error_.op1_contains_op2_.states   (Y, Y, Y, Y, u);

    // Actions
    before_error_.do_throw_.steps               (1, 1, 1, 1, 0);  // Error pattern matched.
    before_error_.do_false_.steps               (0, 0, 0, 0, 1);  // No error patterns matched.


    before_error_.addCondition(before_error_.on_same_side_);
    before_error_.addCondition(before_error_.op1_is_add_);
    before_error_.addCondition(before_error_.op1_is_delete_);
    before_error_.addCondition(before_error_.op1_is_alter_);
    before_error_.addCondition(before_error_.op2_is_add_);
    before_error_.addCondition(before_error_.op2_is_delete_);
    before_error_.addCondition(before_error_.op2_is_alter_);
    before_error_.addCondition(before_error_.op1_contains_op2_);

    before_error_.addAction(before_error_.do_throw_);
    before_error_.addAction(before_error_.do_false_);

    before_error_.buildMasks();
  }


  static public DecisionTable conflict_main_ = new DecisionTable("conflict_main");
  static public DecisionTable conflict_aux_ = new DecisionTable("conflict_aux");

  static {
    conflict_main_.setAuxiliary(conflict_aux_);

    // Decision table for conflict resolution.
    // Note: resolution considers changes on both sides.
    conflict_main_.op1_is_add_.states           (Y, Y, u, u, u, u, u, u, u);
    conflict_main_.op2_is_add_.states           (Y, u, u, u, Y, u, u, u, u);
    conflict_main_.op1_is_delete_.states        (u, u, Y, Y, u, u, u, u, u);
    conflict_main_.op2_is_delete_.states        (u, Y, u, u, u, Y, u, u, u);
    conflict_main_.op1_is_alter_.states         (u, u, u, u, Y, Y, Y, Y, u);
    conflict_main_.op2_is_alter_.states         (u, u, Y, u, u, u, Y, Y, u);
  //conflict_main_.op1_depends_on_op2_.states  (u, u, u, u, u, u, u, Y, u);
  //conflict_main_.op2_depends_on_op1_.states  (u, u, u, u, u, u, u, Y, u);
  //conflict_main_.affect_same_property_.states  (u, u, u, u, u, u, Y, u, u);
    conflict_main_.on_same_side_.states          (u, u, u, u, u, u, u, Y, u);
    conflict_main_.aux_match_.states             (u, u, u, u, u, u, u, u, N);

    conflict_main_.do_true_.steps                (1, 1, 1, 1, 0, 1, 0, 0, 0);  // op1 has precedence over op2.
    conflict_main_.do_false_.steps               (0, 0, 0, 0, 1, 0, 1, 1, 1);  // op1 does not has precedence over op2.


    conflict_main_.addCondition(conflict_main_.op1_is_add_);
    conflict_main_.addCondition(conflict_main_.op2_is_add_);
    conflict_main_.addCondition(conflict_main_.op1_is_delete_);
    conflict_main_.addCondition(conflict_main_.op2_is_delete_);
    conflict_main_.addCondition(conflict_main_.op1_is_alter_);
    conflict_main_.addCondition(conflict_main_.op2_is_alter_);
  //conflict_main_.addCondition(conflict_main_.op1_depends_on_op2_);
  //conflict_main_.addCondition(conflict_main_.op2_depends_on_op1_);
  //conflict_main_.addCondition(conflict_main_.affect_same_property_);
    conflict_main_.addCondition(conflict_main_.on_same_side_);
    conflict_main_.addCondition(conflict_main_.aux_match_);

    conflict_main_.addAction(conflict_main_.do_true_);
    conflict_main_.addAction(conflict_main_.do_false_);

    conflict_main_.buildMasks();


    // Decision table for operation conflict resolution input errors.
    // Note: resolution considers changes on both sides.
    conflict_aux_.op1_is_add_.states     (u, u, u);
    conflict_aux_.op2_is_add_.states     (u, u, u);
    conflict_aux_.op1_is_delete_.states  (u, u, u);
    conflict_aux_.op2_is_delete_.states  (u, u, u);
    conflict_aux_.op1_is_alter_.states   (Y, u, u);
    conflict_aux_.op2_is_alter_.states   (u, Y, u);

    conflict_aux_.addCondition(conflict_aux_.op1_is_add_);
    conflict_aux_.addCondition(conflict_aux_.op2_is_add_);
    conflict_aux_.addCondition(conflict_aux_.op1_is_delete_);
    conflict_aux_.addCondition(conflict_aux_.op2_is_delete_);
    conflict_aux_.addCondition(conflict_aux_.op1_is_alter_);
    conflict_aux_.addCondition(conflict_aux_.op2_is_alter_);

    conflict_aux_.buildMasks();
  }
  // End class data ---------------------------------------------------------
}

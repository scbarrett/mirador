/* --------------------------------------------------------------------------+
   DecisionTable.java - High-level description of module and place in system.
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
import ca.dsrg.mirador.MiradorException;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.decision.TableAction.ActionSeq;
import ca.dsrg.mirador.decision.TableCondition.ConditionTest;
import ca.dsrg.mirador.difference.EcoreComparator;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.difference.MiradorTyper.MiradorType;
import ca.dsrg.mirador.merge.AddChangeOp;
import ca.dsrg.mirador.merge.AlterChangeOp;
import ca.dsrg.mirador.merge.AtomicChangeOp;
import ca.dsrg.mirador.merge.ContradictChangeOp;
import ca.dsrg.mirador.merge.DeleteChangeOp;
import ca.dsrg.mirador.model.EcoreExtra;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.23 - Apr 13, 2010
 * @author  Stephen Barrett
 */
public class DecisionTable {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  table_name  Purpose of the argument.
   */
  public DecisionTable(String table_name) {
    this(table_name, null);
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  table_name  Purpose of the argument.
   * @param  file_name  Purpose of the argument.
   */
  public DecisionTable(String table_name, String file_name) {
    table_name_ = table_name;
    file_name_ = file_name;
    master_side_ = MergeSide.LEFT;  // FIXME:2 Default should only be set in one place.
    initialize();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public List<TableAction> getActions() {
    return actions_;
  }


  public DecisionTable getAuxiliary() {
    return aux_table_;
  }


  public void setAuxiliary(DecisionTable auxiliary) {
    aux_table_ = auxiliary;
  }


  public List<TableCondition> getConditions() {
    return conditions_;
  }


  public String getFileName() {
    return file_name_;
  }


  public MergeSide getMasterSide() {
    return master_side_;
  }


  public void setMasterSide(MergeSide side) {
    master_side_ = side;
  }


//  public void setRepository(ModelRepository model_repo) {
//    model_repo_ = model_repo;
//
//    if (aux_table_ != null)
//      aux_table_.setRepository(model_repo);
//  }
//
//
//  public ModelRepository getRepository() {
//    return model_repo_;
//  }


  public String getTableName() {
    return table_name_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  public void addAction(TableAction action) {
    // Ensure that actions and conditions all have the same number of columns.
    if (!actions_.isEmpty())
      assert action.size() == actions_.get(0).size();
    if (!conditions_.isEmpty())
      assert action.size() == conditions_.get(0).size();

    actions_.add(action);
  }


  public void addCondition(TableCondition condition) {
    // Ensure that actions and conditions all have the same number of columns.
    if (!conditions_.isEmpty())
      assert condition.size() == conditions_.get(0).size();
    if (!actions_.isEmpty())
      assert condition.size() == actions_.get(0).size();

    conditions_.add(condition);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  static public void dumpTable(DecisionTable table, String indent) {
    if (Debug.dbg.isDebug() && table != null) {
      if (indent.equals(""))
        Debug.dbg.println("\n\n\n\t    --- PARSED DECISION TABLE ---");

      Debug.dbg.println('\n' + indent + "[Table]");
      Debug.dbg.println(indent + table.table_name_);

      Debug.dbg.printf('\n' + indent + "%-24s", "[Conditions]");
      for (int i = 1; i < table.conditions_.get(0).size() + 1; ++i)
        Debug.dbg.printf("%3d", i);

      Debug.dbg.println();

      for (TableCondition cond : table.conditions_) {
        Debug.dbg.printf(indent + "%-24s  %s\n", cond.getName(), cond);
      }

      Debug.dbg.println('\n' + indent + "[Actions]");
      for (TableAction act : table.actions_) {
        Debug.dbg.printf(indent + "%-24s  %s\n", act.getName(), act);
      }

      if (table.aux_table_ != null)
        dumpTable(table.aux_table_, indent + "   ");
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public ActionSeq evaluate(Object... objs) {
    int rule_idx = evaluate(0, objs);
    return (rule_idx >= 0) ? new ActionSeq(rule_idx, this) : null;
  }


  private int evaluate(int rule_idx, Object... objs) {
    int row_sz = apt_mask_.length;
    int col_sz = apt_mask_[0].length;

    assert 0 <= rule_idx && rule_idx < col_sz;
    int rc = -1;

    if (row_sz != 0) {
      for (int j = rule_idx; j < col_sz; ++j) {
        boolean is_hit = false;

        for (int i = 0; i < row_sz; ++i) {
          is_hit = ((apt_mask_[i][j] && conditions_.get(i).test(objs))
              == true_mask_[i][j]);

          if (!is_hit)
            break;
        }

        if (is_hit) {
          rc = j;
          break;
        }
      }
    }

    return rc;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  public void buildMasks() {
    apt_mask_ = new boolean[conditions_.size()][conditions_.get(0).size()];
    true_mask_ = new boolean[conditions_.size()][conditions_.get(0).size()];

    for (int i = 0; i < conditions_.size(); ++i) {
      Tristate[] states = conditions_.get(i).getStates();

      for (int j = 0; j < states.length; ++j) {
        apt_mask_[i][j] = (states[j].isUndef()) ? false : true;
        true_mask_[i][j] = states[j].isTrue();
      }
    }
  }


  private void initialize() {
    all_conditions_.add(alter_same_property_);
    all_conditions_.add(containers_match_);
    all_conditions_.add(names_are_same_);
    all_conditions_.add(on_same_side_);
    all_conditions_.add(op1_is_add_);
    all_conditions_.add(op1_is_delete_);
    all_conditions_.add(op1_is_alter_);
    all_conditions_.add(op1_is_attribute_);
    all_conditions_.add(op1_is_class_);
    all_conditions_.add(op1_is_reference_);
    all_conditions_.add(op1_updates_match_);
    all_conditions_.add(op1_contains_op2_);
    all_conditions_.add(op1_is_ref_to_op2_);
    all_conditions_.add(op2_is_add_);
    all_conditions_.add(op2_is_delete_);
    all_conditions_.add(op2_is_alter_);
    all_conditions_.add(op2_is_attribute_);
    all_conditions_.add(op2_is_class_);
    all_conditions_.add(op2_is_reference_);
    all_conditions_.add(op2_updates_match_);
    all_conditions_.add(op2_contains_op1_);
    all_conditions_.add(op2_is_ref_to_op1_);
    all_conditions_.add(op2_subclasses_op1_);
    all_conditions_.add(elements_match_);
    all_conditions_.add(aux_match_);

    all_conditions_.add(left_is_add_);
    all_conditions_.add(left_is_alter_);
    all_conditions_.add(left_is_delete_);
    all_conditions_.add(right_is_add_);
    all_conditions_.add(right_is_alter_);
    all_conditions_.add(right_is_delete_);
    all_conditions_.add(conflict_is_simple_);
    all_conditions_.add(conflict_with_name_);
    all_conditions_.add(conflicts_match_);
    all_conditions_.add(updates_are_equal_);
    all_conditions_.add(update_same_property_);

    all_actions_.add(do_false_);
    all_actions_.add(do_true_);
    all_actions_.add(do_assert_);
    all_actions_.add(do_throw_);

    all_actions_.add(do_master_);
    all_actions_.add(do_left_);
    all_actions_.add(do_right_);
    all_actions_.add(do_rename_);
  }


  static public DecisionTable loadTableDefinition(File file) {
    if (file == null)
      return null;

    DecisionTable table = null;
    List<String> tokens = null;

    try {
      tokens = tokenizeDefinition(file);
      table = parseDefinition(tokens, null, file.getPath());
    }
    catch (Exception ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      ex.printStackTrace();
    }

    return table;
  }


  static private TableAction parseAction(DecisionTable table,
      List<String> tokens) {
    String act_name = tokens.get(0);
    int[] act_steps = new int[tokens.size() - 1];
    int act_step;

    for (int i = 1; i < tokens.size(); ++i) {
      act_step = new Integer(tokens.get(i));
      act_steps[i - 1] = act_step;
    }


    TableAction action = null;
    for (TableAction act : table.all_actions_) {
      if (act_name.equalsIgnoreCase(act.getName())) {
        action = act;
        break;
      }
    }

    if (action != null) {
      action.steps(act_steps);
    }

    return action;
  }


  static private TableCondition parseCondition(DecisionTable table,
      List<String> tokens) {
    String cond_name = tokens.get(0);
    Tristate[] cond_states = new Tristate[tokens.size() - 1];
    Tristate cond_state;

    for (int i = 1; i < tokens.size(); ++i) {
      char ch = tokens.get(i).charAt(0);

      switch (ch) {
        case 'Y':
        case 'y':
          cond_state = Tristate.TRUE;
        break;

        case 'N':
        case 'n':
          cond_state = Tristate.FALSE;
        break;

        case '-':
          cond_state = Tristate.UNDEF;
        break;

        default:
          cond_state = Tristate.UNDEF;
      }

      cond_states[i - 1] = cond_state;
    }


    TableCondition condition = null;
    for (TableCondition cond : table.all_conditions_) {
      if (cond_name.equalsIgnoreCase(cond.getName())) {
        condition = cond;
        break;
      }
    }

    if (condition != null)
      condition.states(cond_states);

    return condition;
  }


  static private DecisionTable parseDefinition(List<String> tokens,
      ListIterator<String> iterator, String file_name) {
    if (iterator == null)
      iterator = tokens.listIterator();

    Deque<DecisionTable> tables = new ArrayDeque<DecisionTable>();
    DecisionTable table = null;

    do {
      table = parseTable(tokens, iterator, file_name);

      if (table != null) {
        table.buildMasks();
        tables.add(table);
      }
    } while (iterator.hasNext() && table != null);


    // Chain tables in same file together.
    table = tables.removeLast();
    while (!tables.isEmpty()) {
      tables.getLast().aux_table_ = table;
      table = tables.removeLast();
    }

    return table;
  }


  static private DecisionTable parseTable(List<String> tokens,
      ListIterator<String> iterator, String file_name) {
    final int NONE = 0;
    final int TABLE = 1;
    final int CONDITION = 2;
    final int ACTION = 3;

    int state = NONE;
    DecisionTable table = null;

    while (iterator.hasNext()) {
      String token = iterator.next();
      if (token.charAt(0) == '\n')
        continue;

      if (token.charAt(0) == '[') {
        int end_idx = token.length() - 1;

        if (token.charAt(end_idx) == ']') { // FIXME:3 Allow space between delimiters.
          token = token.substring(1, end_idx);

          if (token.equalsIgnoreCase("table")) {
            if (table != null) {
              iterator.previous();
              break;
            }
            state = TABLE;
          }
          else if (token.equalsIgnoreCase("conditions"))
            state = CONDITION;
          else if (token.equalsIgnoreCase("actions"))
            state = ACTION;

          continue;
        }
        else
          throw new MiradorException("Syntax error in definition file.");
      }

      switch (state) {
        case NONE:
        break;

        case TABLE:
          table = new DecisionTable(token, file_name);
        break;

        case CONDITION:
          List<String> cond_tokens = new ArrayList<String>();

          while (!token.equals("\n")) {
            cond_tokens.add(token);
            token = iterator.next();
          }

          TableCondition cond = parseCondition(table, cond_tokens);
          if (cond != null)
            table.addCondition(cond);
        break;

        case ACTION:
          List<String> act_tokens = new ArrayList<String>();

          while (!token.equals("\n")) {
            act_tokens.add(token);
            token = iterator.next();
          }

          TableAction act = parseAction(table, act_tokens);
          if (act != null)
            table.addAction(act);
        break;
      }
    }

    return table;
  }


  static private List<String> tokenizeDefinition(File file) throws Exception {
    List<String> tokens = new ArrayList<String>();

    FileReader reader = new FileReader (file);
    StreamTokenizer tokenizer = new StreamTokenizer(reader);

    tokenizer.resetSyntax();
    tokenizer.whitespaceChars(0, ' ');
    tokenizer.wordChars(33, 255);
    tokenizer.ordinaryChar('/');
    tokenizer.slashSlashComments(true);
    tokenizer.eolIsSignificant(true);

    while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
      switch (tokenizer.ttype) {
        case StreamTokenizer.TT_WORD:
          tokens.add(tokenizer.sval);
        break;

        case StreamTokenizer.TT_NUMBER:
        break;

        case StreamTokenizer.TT_EOL:
          tokens.add("\n");
        break;

        default:
          tokens.add("~");
        break;
      }
    }

    reader.close();
    return tokens;
  }


  // Instance data ----------------------------------------------------------
  private String table_name_;
  private String file_name_;
//  private ModelRepository model_repo_;
  private MergeSide master_side_;  // FIXME:3 Make an integral part of class, not a set kludge.

  private DecisionTable aux_table_ ;

  private boolean[][] apt_mask_;
  private boolean[][] true_mask_;

  private List<TableAction> actions_ = new ArrayList<TableAction>();
  private List<TableAction> all_actions_ = new ArrayList<TableAction>();

  private List<TableCondition> conditions_ = new ArrayList<TableCondition>();
  private List<TableCondition> all_conditions_ = new ArrayList<TableCondition>();


  /** Decision condition to test change same property. */
  public TableCondition alter_same_property_ =
      new TableCondition("alter_same_property", new ConditionTest() {
    /**
     * Tests if changes of received pair both affect the same property.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = affect same property, false = don't affect same property
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((AtomicChangeOp) objs[0]);
      AtomicChangeOp op1 = ((AtomicChangeOp) objs[1]);

      ENamedElement tar0 = op0.getTargeted();
      ENamedElement upd0 = op0.getUpdated();
      ENamedElement tar1 = op1.getTargeted();
      ENamedElement upd1 = op1.getUpdated();

      EcoreComparator comparer = new EcoreComparator();
      List<EStructuralFeature> feats0 = comparer.differs(upd0, tar0);
      List<EStructuralFeature> feats1 = comparer.differs(upd1, tar1);

      for (EStructuralFeature feat : feats0) {
        if (feats1.contains(feat))
          return true;
      }

      return false;
    }
  });


  /** Decision condition. */
  public TableCondition containers_match_ =
      new TableCondition("containers_match", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      EcoreExtra tar0 = ((AtomicChangeOp) objs[0]).getTargetedExtra();
      EcoreExtra tar1 = ((AtomicChangeOp) objs[1]).getTargetedExtra();

      ENamedElement cont0 = (ENamedElement) tar0.getElement().eContainer();
      ENamedElement cont1 = (ENamedElement) tar1.getElement().eContainer();

      return (tar0.getModel().getExtra(cont0).getMatch() ==
          tar1.getModel().getExtra(cont1));
    }
  });


  /** Decision condition to test if reference roles have the same name. */
  public TableCondition names_are_same_ =
      new TableCondition("names_are_same", new ConditionTest() {
    /**
     * Tests if changes of received pair both have same role name.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = affect same property, false = don't affect same property
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((AtomicChangeOp) objs[0]);
      AtomicChangeOp op1 = ((AtomicChangeOp) objs[1]);

      if (op0.getTargeted().getName().equals(op1.getTargeted().getName()))
        return true;

      return false;
    }
  });


  /** Decision condition to test for changes on same side of merge. */
  public TableCondition on_same_side_ =
      new TableCondition("on_same_side", new ConditionTest() {
    /**
     * Tests if changes of received pair are on the same side. Note that this
     * doesn't apply for redundancy checking which only considers one side.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = on same side, false = not on same side
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[0]).getMergeSide()
          == ((AtomicChangeOp) objs[1]).getMergeSide();
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op1_is_add_ =
      new TableCondition("op1_is_add", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[0]) instanceof AddChangeOp;
    }
  });


  /** Decision condition to test following CREATE_OBJECT. */
  public TableCondition op2_is_add_ =
      new TableCondition("op2_is_add", new ConditionTest() {
    /**
     * Tests if following change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[1]) instanceof AddChangeOp;
    }
  });


  /** Decision condition to test leading DESTROY_OBJECT. */
  public TableCondition op1_is_delete_ =
      new TableCondition("op1_is_delete", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a DESTROY_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is DESTROY_OBJECT, false = is not DESTROY_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[0]) instanceof DeleteChangeOp;
    }
  });


  /** Decision condition to test following DESTROY_OBJECT. */
  public TableCondition op2_is_delete_ =
      new TableCondition("op2_is_delete", new ConditionTest() {
    /**
     * Tests if following change of received pair is a DESTROY_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is DESTROY_OBJECT, false = is not DESTROY_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[1]) instanceof DeleteChangeOp;
    }
  });


  /** Decision condition to test leading ALTER_FIELD. */
  public TableCondition op1_is_alter_ =
      new TableCondition("op1_is_alter", new ConditionTest() {
    /**
     * Tests if leading change of received pair is an ALTER_FIELD kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is ALTER_FIELD, false = is not ALTER_FIELD
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[0]) instanceof AlterChangeOp;
    }
  });


  /** Decision condition to test following ALTER_FIELD. */
  public TableCondition op2_is_alter_ =
      new TableCondition("op2_is_alter", new ConditionTest() {
    /**
     * Tests if following change of received pair is an ALTER_FIELD kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is ALTER_FIELD, false = is not ALTER_FIELD
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[1]) instanceof AlterChangeOp;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op1_is_attribute_ =
      new TableCondition("op1_is_attribute", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[0]).getTargeted() instanceof EAttribute;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op2_is_attribute_ =
      new TableCondition("op2_is_attribute", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[1]).getTargeted() instanceof EAttribute;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op1_is_class_ =
      new TableCondition("op1_is_class", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[0]).getTargeted() instanceof EClass;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op2_is_class_ =
      new TableCondition("op2_is_class", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[1]).getTargeted() instanceof EClass;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op1_is_reference_ =
      new TableCondition("op1_is_reference", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[0]).getTargeted() instanceof EReference;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op2_is_reference_ =
      new TableCondition("op2_is_reference", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((AtomicChangeOp) objs[1]).getTargeted() instanceof EReference;
    }
  });


  /** Decision condition to test follow change dependent on leading change. */
  public TableCondition op1_is_ref_to_op2_ =
      new TableCondition("op1_is_ref_to_op2", new ConditionTest() {
    /**
     * Tests if following change depends on leading for its property value.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = follow is dependent, false = follow is not dependent
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      boolean rc = false;
      AtomicChangeOp op1 = (AtomicChangeOp) objs[0];
      AtomicChangeOp op2 = (AtomicChangeOp) objs[1];
      EReference ref = (EReference) op1.getTargeted();
      ENamedElement elem = op2.getTargeted();

      if (op1.getMergeSide() == op2.getMergeSide())
        rc = (ref.getEType() == elem);
      else {
        EObject match = op2.getMatch();
        rc = (match != null && ref.getEType() == match);
      }
      return rc;
    }
  });


  /** Decision condition to test follow change dependent on leading change. */
  public TableCondition op2_is_ref_to_op1_ =
      new TableCondition("op2_is_ref_to_op1", new ConditionTest() {
    /**
     * Tests if following change depends on leading for its property value.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = follow is dependent, false = follow is not dependent
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      boolean rc = false;
      AtomicChangeOp op1 = (AtomicChangeOp) objs[0];
      AtomicChangeOp op2 = (AtomicChangeOp) objs[1];
      ENamedElement elem = op1.getTargeted();
      EReference ref = (EReference) op2.getTargeted();

      if (op1.getMergeSide() == op2.getMergeSide())
        rc = (ref.getEType() == elem);
      else {
        EObject match = op1.getMatch();
        rc = (match != null && ref.getEType() == match);
      }
      return rc;
    }
  });


  /** Decision condition to test lead change dependent on following change. */
  public TableCondition op1_contains_op2_ =
      new TableCondition("op1_contains_op2", new ConditionTest() {
    /**
     * Tests if leading change depends on following for its property value.
     * Note that this doesn't apply for redundancy checking which only
     * considers one side *with* the existing order of changes.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = lead is dependent, false = lead is not dependent
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      boolean rc = false;
      AtomicChangeOp op1 = (AtomicChangeOp) objs[0];
      AtomicChangeOp op2 = (AtomicChangeOp) objs[1];
      ENamedElement elem1 = op1.getTargeted();
      ENamedElement elem2 = op2.getTargeted();

      if (op1.getMergeSide() == op2.getMergeSide())
        rc = elem2.eContainer() == elem1;
      else {
        EObject match = op1.getMatch();

        if (match != null) {
          for (TreeIterator<EObject> it =
              match.eAllContents(); it.hasNext();) {
            if (it.next() == elem2) { rc = true; break; }
          }
        }
      }

      return rc;
    }
  });


  /** Decision condition to test follow change dependent on leading change. */
  public TableCondition op2_contains_op1_ =
      new TableCondition("op2_contains_op1", new ConditionTest() {
    /**
     * Tests if following change depends on leading for its property value.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = follow is dependent, false = follow is not dependent
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      boolean rc = false;
      AtomicChangeOp op1 = (AtomicChangeOp) objs[0];
      AtomicChangeOp op2 = (AtomicChangeOp) objs[1];
      ENamedElement elem1 = op1.getTargeted();
      ENamedElement elem2 = op2.getTargeted();

      if (op1.getMergeSide() == op2.getMergeSide())
        rc = elem1.eContainer() == elem2;
      else {
        EObject match = op2.getMatch();

        if (match != null) {
          for (TreeIterator<EObject> it =
              match.eAllContents(); it.hasNext();) {
            if (it.next() == elem1) { rc = true; break; }
          }
        }
      }

      return rc;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition op2_subclasses_op1_ =
      new TableCondition("op2_subclasses_op1", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      EClass sup = (EClass) ((AtomicChangeOp) objs[0]).getTargeted();
      EClass sub = (EClass) ((AtomicChangeOp) objs[1]).getUpdated();
      return sub.getESuperTypes().contains(sup);
    }
  });


  /** Decision condition to test lead change dependent on following change. */
  public TableCondition op1_updates_match_ =
      new TableCondition("op1_updates_match", new ConditionTest() {
    /**
     * Tests if leading change depends on following for its property value.
     * Note that this doesn't apply for redundancy checking which only
     * considers one side *with* the existing order of changes.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = lead is dependent, false = lead is not dependent
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      boolean rc = false;
      ENamedElement update = ((AtomicChangeOp) objs[0]).getUpdated();
      ENamedElement match = ((AtomicChangeOp) objs[1]).getMatch();

      if (update != null && match != null)
        rc = (update == match);

      return rc;
    }
  });


  /** Decision condition to test lead change dependent on following change. */
  public TableCondition op2_updates_match_ =
      new TableCondition("op2_updates_match", new ConditionTest() {
    /**
     * Tests if leading change depends on following for its property value.
     * Note that this doesn't apply for redundancy checking which only
     * considers one side *with* the existing order of changes.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = lead is dependent, false = lead is not dependent
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      boolean rc = false;
      ENamedElement match = ((AtomicChangeOp) objs[0]).getMatch();
      ENamedElement update = ((AtomicChangeOp) objs[1]).getUpdated();

      if (update != null && match != null)
        rc = (update == match);

      return rc;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition elements_match_ =
      new TableCondition("elements_match", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      ENamedElement elem = ((AtomicChangeOp) objs[0]).getTargeted();
      ENamedElement match = ((AtomicChangeOp) objs[1]).getMatch();
      return match != null && elem == match;
    }
  });


  /** Decision condition to perform input error checking. */
  public TableCondition aux_match_ =
      new TableCondition("aux_match", new ConditionTest() {
    /**
     * Tests if any rules in an auxiliary decision table are matched, usually
     * indicating that the received change pair constitute an input error.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = input is in error, false = input is not in error
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      boolean rc = false;
      ActionSeq steps = aux_table_.evaluate(objs);

      if (steps != null) {
        steps.executeAll(objs);
        rc = steps.getResult().toFalse();
      }

      return rc;
    }
  });


  /** Decision action simply sets return code of result object to true. */
  public TableAction do_true_ = new TableAction("do_true") {
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      steps.setMergeSide(MergeSide.BASE);
      steps.setResult(Tristate.TRUE);
      return true;
    }
  };


  /** Decision action simply sets return code of result object to false. */
  public TableAction do_false_ = new TableAction("do_false") {
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      steps.setMergeSide(MergeSide.NONE);
      steps.setResult(Tristate.FALSE);
      return true;
    }
  };


  /** Decision action simply asserts an error. */
  public TableAction do_assert_ = new TableAction("do_assert") {
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      assert false;
      return false;
    }
  };


  /** Decision action returns false, thereby causing a run-time exception. */
  public TableAction do_throw_ = new TableAction("do_throw") {
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      return false;
    }
  };


  /** Decision condition. */
  public TableCondition conflict_is_simple_ =
      new TableCondition("conflict_is_simple", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      return ((ContradictChangeOp) objs[0]).isSimple();
    }
  });


  /** Decision condition. */
  public TableCondition conflict_with_name_ =
      new TableCondition("conflict_with_name", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);

      return (op0.getEcoreType() == EcoreType.REFERENCE
          && op0.getEcoreType() == EcoreType.REFERENCE)
          ? (op0.getTargeted().getName().equals(op1.getTargeted().getName()))
          : false;
    }
  });


  /** Decision condition to test leading CREATE_OBJECT. */
  public TableCondition conflicts_match_ =
      new TableCondition("conflicts_match", new ConditionTest() {
    /**
     * Tests if leading change of received pair is a CREATE_OBJECT kind.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is CREATE_OBJECT, false = is not CREATE_OBJECT
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);

      ENamedElement elem = op0.getTargeted();
      ENamedElement match = op1.getMatch();
      return match != null && elem == match;
    }
  });


  /** Decision condition. */
  public TableCondition left_is_add_ =
      new TableCondition("left_is_add", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      return (op0.getMiradorType() == MiradorType.ADD);
    }
  });


  /** Decision condition. */
  public TableCondition right_is_add_ =
      new TableCondition("right_is_add", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);
      return (op1.getMiradorType() == MiradorType.ADD);
    }
  });


  /** Decision condition. */
  public TableCondition left_is_alter_ =
      new TableCondition("left_is_alter", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      return (op0.getMiradorType() == MiradorType.ALTER);
    }
  });


  /** Decision condition. */
  public TableCondition right_is_alter_ =
      new TableCondition("right_is_alter", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);
      return (op1.getMiradorType() == MiradorType.ALTER);
    }
  });


  /** Decision condition. */
  public TableCondition left_is_delete_ =
      new TableCondition("left_is_delete", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      return (op0.getMiradorType() == MiradorType.DELETE);
    }
  });


  /** Decision condition. */
  public TableCondition right_is_delete_ =
      new TableCondition("right_is_delete", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);
      return (op1.getMiradorType() == MiradorType.DELETE);
    }
  });


  /** Decision condition. */
  public TableCondition updates_are_equal_ =
      new TableCondition("updates_are_equal", new ConditionTest() {
    /**
     * Tests if...
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = is..., false = is...
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);
      EcoreComparator comparer = new EcoreComparator();

      if (op0.getMiradorType() == MiradorType.ALTER)
        return comparer.equals(op0.getUpdated(), op1.getUpdated());
      else
        return comparer.equals(op0.getTargeted(), op1.getTargeted());
    }
  });


  /** Decision condition to test change same property. */
  // Test if changes updates the same property.
  public TableCondition update_same_property_ =
      new TableCondition("update_same_property", new ConditionTest() {
    /**
     * Tests if changes of received pair both affect the same property.
     *
     * @param  objs  Leading change and following change pair.
     * @return  true = affect same property, false = don't affect same property
     * @see TableCondition.ConditionTest#testCondition(java.lang.Object[])
     */
    @Override public boolean testCondition(Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);

      ENamedElement tar0 = op0.getTargeted();
      ENamedElement upd0 = op0.getUpdated();
      ENamedElement tar1 = op1.getTargeted();
      ENamedElement upd1 = op1.getUpdated();

      EcoreComparator comparer = new EcoreComparator();
      List<EStructuralFeature> feats0 = comparer.differs(upd0, tar0);
      List<EStructuralFeature> feats1 = comparer.differs(upd1, tar1);

      for (EStructuralFeature feat : feats0) {
        if (feats1.contains(feat))
          return true;
      }

      return false;
    }
  });


  /** Decision action resolves conflict in favor of the master side. */
  public TableAction do_master_ = new TableAction("do_master") {
    /**
     * Resolves a conflicted block by clearing all changes on the non-master
     * side. All operation can be removed, because changes of a block must
     * execute one side or the other in their entirety.
     *
     * @param  objs  Change block that is in conflict.
     * @param  steps  Outcome object - rc = true if resolved, false otherwise
     * @return  true = action succeeded, false = action failed
     * @see TableAction#doAction(ActionSeq, java.lang.Object[])
     */
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      if (getMasterSide() == MergeSide.LEFT)
        do_left_.doAction(steps, objs);
      else
        do_right_.doAction(steps, objs);

      return true;
    }
  };


  /** Decision action resolves conflict in favor of the left side. */
  TableAction do_left_ = new TableAction("do_left") {
    /**
     * Resolves a conflicted block by clearing all changes on the right side.
     * All operation can be removed, because changes of a block must execute
     * one side or the other in their entirety.
     *
     * @param  objs  Change block that is in conflict.
     * @param  steps  Outcome object - rc = true if resolved, false otherwise
     * @return  true = action succeeded, false = action failed
     * @see TableAction#doAction(ActionSeq, java.lang.Object[])
     */
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      steps.setMergeSide(MergeSide.LEFT);
      steps.setResult(Tristate.TRUE);
      return true;
    }
  };


  /** Decision action resolves conflict in favor of the master side. */
  public TableAction do_rename_ = new TableAction("do_rename") {
    /**
     * Resolves a reference role naming conflict.
     *
     * @param  objs  Change block that is in conflict.
     * @param  steps  Outcome object - rc = true if resolved, false otherwise
     * @return  true = action succeeded, false = action failed
     * @see TableAction#doAction(ActionSeq, java.lang.Object[])
     */
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      AtomicChangeOp op0 = ((ContradictChangeOp) objs[0])
          .getChangeLeft().getChange(0);
      AtomicChangeOp op1 = ((ContradictChangeOp) objs[0])
          .getChangeRight().getChange(0);

      String name0 = op0.getTargeted().getName();
      String name1 = op1.getTargeted().getName();

      op0.getTargeted().setName(name0 + "_lf");
      op1.getTargeted().setName(name1 + "_rt");

      steps.setMergeSide(MergeSide.BOTH);
      steps.setResult(Tristate.TRUE);

      return true;
    }
  };


  /** Decision action resolves conflict in favor of the right side. */
  public TableAction do_right_ = new TableAction("do_right") {
    /**
     * Resolves a conflicted block by clearing all changes on the left side.
     * All operation can be removed, because changes of a block must execute
     * one side or the other in their entirety.
     *
     * @param  objs  Change block that is in conflict.
     * @param  steps  Outcome object - rc = true if resolved, false otherwise
     * @return  true = action succeeded, false = action failed
     * @see TableAction#doAction(ActionSeq, java.lang.Object[])
     */
    @Override public boolean doAction(ActionSeq steps, Object... objs) {
      steps.setMergeSide(MergeSide.RIGHT);
      steps.setResult(Tristate.TRUE);
      return true;
    }
  };
  // End instance data ------------------------------------------------------
}

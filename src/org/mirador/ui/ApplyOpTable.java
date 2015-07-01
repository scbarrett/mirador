/* --------------------------------------------------------------------------+
   ApplyOpTable.java - Provides a data model suitable for viewing as a table.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;  // FIXME:3 Only half this class belongs in the UI.
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.difference.EcoreComparator;
import ca.dsrg.mirador.difference.EcoreTyper;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.merge.AddChangeOp;
import ca.dsrg.mirador.merge.AlterChangeOp;
import ca.dsrg.mirador.merge.AtomicChangeOp;
import ca.dsrg.mirador.merge.ChangeOp;
import ca.dsrg.mirador.merge.CompositeChangeOp;
import ca.dsrg.mirador.merge.ContradictChangeOp;
import ca.dsrg.mirador.merge.DeleteChangeOp;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import ca.dsrg.mirador.model.ModelRepository;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.73 - Dec 9, 2010
 * @author  Stephen Barrett
 */
public class ApplyOpTable {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Builds a data model from...
   *
   * @param  repo  Stuff...
   * @param  table  GUI component to populate with data.
   */
  public ApplyOpTable(ModelRepository repo, JTable table) {
    model_repo_ = repo;
    model_mg_ = model_repo_.getModelMerged();
    changes_ = model_repo_.getChangeList();
    table_ = table;
    initialize();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public DefaultTableModel getChangeModel() {
    return table_mdl_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public List<ChangeOp> getChanges() {
    return changes_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public JTable getTable() {
    return table_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  private boolean isEmpty(ENamedElement target) {
    boolean rc = false;
    EcoreType typ = EcoreTyper.typeEObject(target);


    switch (typ) {
      case ATTRIBUTE:
        rc = true;
      break;

      case CLASS:
        rc  = ((EClass)target).getEOperations().isEmpty();
        rc &= ((EClass)target).getEStructuralFeatures().isEmpty();
      break;

      case DATATYPE:
        rc = true;
      break;

      case OPERATION:
        rc = ((EOperation)target).getEParameters().isEmpty();
      break;

      case PACKAGE:
        rc = ((EPackage)target).getEClassifiers().isEmpty();
      break;

      case PARAMETER:
        rc = true;
      break;

      case REFERENCE:
        rc = true;
      break;
    }

    return rc;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public void fillTable() {
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();

    for (ChangeOp op : changes_) {
      Vector<Object> row = new Vector<Object>();

      if (!op.isConflict()) {
        if (op.getMergeSide() == MergeSide.LEFT) {
          row.add(op);
          row.add("<");
          row.add(null);
        }
        else if (op.getMergeSide() == MergeSide.RIGHT) {
          row.add(null);
          row.add(">");
          row.add(op);
        }
      }
      else {
        row.add(((ContradictChangeOp) op).getChangeLeft());

        switch (op.getMergeSide()) {
          case LEFT:
            row.add("<");
          break;

          case RIGHT:
            row.add(">");
          break;

          case BOTH:
            row.add("<>");
          break;

          default:
            row.add("X");
        }

        row.add(((ContradictChangeOp) op).getChangeRight());
      }

      data.add(row);
    }

    table_mdl_.setDataVector(data, table_headings_);
    setColumnWidths();

    if (table_.getRowCount() > 0)
      table_.setRowSelectionInterval(0, 0);
  }


  public void acceptChange() {
    int row = table_.getSelectedRow();
    ChangeOp op = changes_.get(row);

    if (!op.isConflict()) {
      if (op.getMergeSide() == MergeSide.LEFT)
        table_.setValueAt("<", row, STATUS);
      else if (op.getMergeSide() == MergeSide.RIGHT)
        table_.setValueAt(">", row, STATUS);
    }
  }


  public boolean applyChange(int i, int dir) {
    ChangeOp op = changes_.get(i);

    if (op.isConflict()) {
      if (dir == LEFT)
        op = ((ContradictChangeOp) op).getChangeLeft();
      else if (dir == RIGHT)
        op = ((ContradictChangeOp) op).getChangeRight();
      else { // Execute both sides.  // FIXME:3 Composite changes are being ignored here.
        AtomicChangeOp op_lf =
            (((ContradictChangeOp) op).getChangeLeft()).getChange(0);
        ENamedElement upd_lf = op_lf.getUpdated();

        AtomicChangeOp op_rt =
            (((ContradictChangeOp) op).getChangeRight()).getChange(0);
        ENamedElement tar_rt = op_rt.getTargeted();
        ENamedElement upd_rt = op_rt.getUpdated();

        if (upd_lf != null && upd_rt != null) {
          EcoreComparator comparer = new EcoreComparator();
          List<EStructuralFeature> feats = comparer.differs(tar_rt, upd_rt);

          for (EStructuralFeature feat : feats)
            upd_lf.eSet(feat, upd_rt.eGet(feat));

          op = op_lf;
        }
        else
          return playChange(op_lf) && playChange(op_rt);
      }
    }

    return playChange(op);
  }


  public void leftChange() {
    int row = table_.getSelectedRow();
    if (changes_.get(row).isConflict())
      table_.setValueAt("<", row, STATUS);
  }


  private boolean playChange(ChangeOp op) {
    boolean rc = false;

    if (op.isAtomic()) {
      EcoreExtra dif_ex = null;
      EcoreExtra tar_ex = null;
      EcoreExtra upd_ex = null;
      String tar_id = null;
      ENamedElement tar = null;

      AtomicChangeOp op_at = (AtomicChangeOp) op;

      if (op_at instanceof AddChangeOp) {
        dif_ex = op_at.getTargetedExtra();

        if (dif_ex != null) {
          rc = model_mg_.addToModel(dif_ex.getElement(), dif_ex);
        }
      }
      else if (op_at instanceof AlterChangeOp) {
        dif_ex = op_at.getTargetedExtra();
        upd_ex = op_at.getUpdatedExtra();

        if (dif_ex != null && upd_ex != null) {
          tar = model_mg_.getElement(upd_ex.getId());
          tar_ex = model_mg_.getExtra(tar);
          rc = model_mg_.alterInModel(dif_ex.getElement(), tar_ex,
              upd_ex.getElement());
        }
      }
      else if (op_at instanceof DeleteChangeOp) {
        dif_ex = op_at.getTargetedExtra();

        if (dif_ex != null) {
          tar_id = dif_ex.getId();
          tar = model_mg_.getElement(tar_id);
          if (isEmpty(tar))
            rc = model_mg_.deleteFromModel(dif_ex.getElement(), tar);
        }
      }
    }
    else if (op.isComposite()) {
      CompositeChangeOp op_cp = (CompositeChangeOp) op;
      for (Iterator<AtomicChangeOp> it = op_cp.changeIterator(); it.hasNext();) {
        rc = playChange(it.next());
      }
    }

    return rc;
  }


  public void rejectChange() {
    int row = table_.getSelectedRow();
    table_.setValueAt("X", row, STATUS);
  }


  public void rightChange() {
    int row = table_.getSelectedRow();
    if (changes_.get(row).isConflict())
      table_.setValueAt(">", row, STATUS);
  }


  public void undoChange() {
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private void initialize() {
    table_headings_.add("Left Model Change Operations");
    table_headings_.add("");
    table_headings_.add("Right Model Change Operations");
    table_.setModel(table_mdl_);
  }


  private void setColumnWidths() {
    DefaultTableCellRenderer renderer;
    TableColumnModel col_mdl = table_.getColumnModel();
    TableColumn col;

    col = col_mdl.getColumn(LEFT);
    col.setMinWidth(WITDH_CHANGE);
    renderer = new DefaultTableCellRenderer();
    renderer.setHorizontalAlignment(SwingConstants.RIGHT);
    col.setCellRenderer(renderer);

    col = col_mdl.getColumn(STATUS);
    col.setMaxWidth(WITDH_STATUS);
    col.setMinWidth(WITDH_STATUS);
    renderer = new DefaultTableCellRenderer();
    renderer.setHorizontalAlignment(SwingConstants.CENTER);
    col.setCellRenderer(renderer);

    col = col_mdl.getColumn(RIGHT);
    col.setMinWidth(WITDH_CHANGE);
    renderer = new DefaultTableCellRenderer();
    renderer.setHorizontalAlignment(SwingConstants.LEFT);
    col.setCellRenderer(renderer);
  }


  // Instance data ----------------------------------------------------------
  /** Table columns. */
  static public final int LEFT = 0;
  static public final int STATUS = 1;
  static public final int RIGHT = 2;

  private ModelRepository model_repo_;

  /** Model representations as used by Mirador. */
//  protected MiradorModel model_bs_;
//  protected MiradorModel model_lf_;
//  protected MiradorModel model_rt_;
  protected MiradorModel model_mg_;

  /** Model data for the table. */
  private List<ChangeOp> changes_;

  /** Table view: for use with either change record or candidate match data. */
  private JTable table_;

  /** Data models for both versions of the table. */
  private DefaultTableModel table_mdl_ = new DefaultTableModel();

  /** Column headings for both versions of the table. */
  private Vector<String> table_headings_ = new Vector<String>();

  /** Table column widths. */
  static private final int WITDH_CHANGE = 335;
  static private final int WITDH_STATUS = 45;
  // End instance data ------------------------------------------------------
}

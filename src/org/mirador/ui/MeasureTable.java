/* --------------------------------------------------------------------------+
   MeasureTable.java - Provides a data model suitable for viewing as a table.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import ca.dsrg.mirador.match.MatchStrategy;
import ca.dsrg.mirador.match.MeasureMatrix;
import ca.dsrg.mirador.match.MeasureMatrix.MeasureCell;
import ca.dsrg.mirador.match.MeasureMatrix.MeasureRow;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import java.text.DecimalFormat;
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
 * @since   v0.19 - Mar 11, 2010
 * @author  Stephen Barrett
 */
public class MeasureTable {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Builds a data model suitable for viewing as a tree, from the <i>left</i> or
   * <i>right</i> log of changes made to a common ancestor design model.
   *
   * @param  model  Ecore model to build the table data around.
   * @param  strategies  Match strategies being used by the Match Panel.
   * @param  table  GUI component to populate with data.
   */
  public MeasureTable(MiradorModel model, List<MatchStrategy> strategies,
      JTable table) {
    model_ = model;
    table_ = table;
    initialize(strategies);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public DefaultTableModel getMatchModel() {
    return match_mdl_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public MiradorModel getMiradorModel() {
    return model_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public JTable getTable() {
    return table_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public void fillTable(EcoreExtra element) {
    table_.setModel(match_mdl_);
    readColumnWidths();
    fillMatches(element, model_.getRankMatrix());
  }


  private void fillMatches(EcoreExtra from_element,
      MeasureMatrix rankings) {
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    MeasureRow cells = rankings.get(from_element.getId());
    DecimalFormat formatter = new DecimalFormat("0.000");

    if (cells != null) {
      for (MeasureCell cell : cells.values()) {
        Vector<Object> row = new Vector<Object>();

        EcoreExtra to_element = cell.getToElement();
        row.add(to_element);

        List<Float> measures = cell.getMeasures();
        for (int i = 0; i < measures.size(); ++i)
          row.add(formatter.format(measures.get(i)));

        data.add(row);
      }
    }

    match_mdl_.setDataVector(data, match_headings_);
    setColumnWidths();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**                                                                     DOCDO: Provide method overview.
   *
   */
  private void initialize(List<MatchStrategy> strategies) {
    match_headings_.add("Match Candidate");
    for (MatchStrategy strategy : strategies)
      match_headings_.add(strategy.getLabel());
  }


  private void readColumnWidths() {
    TableColumnModel col_mdl = table_.getColumnModel();
    TableColumn col;

    if (!is_first_time_) {
      for (int i = 0; i < col_mdl.getColumnCount(); ++i) {
        col = col_mdl.getColumn(i);
        match_widths_[i] = col.getPreferredWidth();
      }
    }
    else
      is_first_time_ = false;
  }


  private void setColumnWidths() {
    TableColumnModel col_mdl = table_.getColumnModel();
    TableColumn col;
    renderer_.setHorizontalAlignment(SwingConstants.RIGHT);

    for (int i = 0; i < col_mdl.getColumnCount(); ++i) {
      col = col_mdl.getColumn(i);
      col.setPreferredWidth(match_widths_[i]);

      if (i >= 1)  //??2
        col.setCellRenderer(renderer_);
    }
  }


  // Instance data ----------------------------------------------------------
  /** Model data for the table. */
  private MiradorModel model_;

  /** Table view: for use with either change record or candidate match data. */
  private JTable table_;

  /** Data models for both versions of the table. */
  private DefaultTableModel match_mdl_ = new DefaultTableModel();

  /** Column headings for both versions of the table. */
  private Vector<String> match_headings_ = new Vector<String>();

  /** Column widths for both versions of the table. */
  private int[] match_widths_ = { 130, 65, 65, 65, 65, 65, 65, 65, 65, 65 };

  /** Cell renderer for both table versions. */
  private DefaultTableCellRenderer renderer_ = new DefaultTableCellRenderer();

  /** For use by setColumnWidths() only. */
  private boolean is_first_time_ = true;
  // End instance data ------------------------------------------------------
}

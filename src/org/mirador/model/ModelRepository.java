/* --------------------------------------------------------------------------+
   ModelRepository.java - Abstraction of deposit point for the salient items
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
package ca.dsrg.mirador.model;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.difference.EcoreDifference;
import ca.dsrg.mirador.difference.EcoreTyper;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.match.MeasureMatrix;
import ca.dsrg.mirador.match.MeasureMatrix.MeasureCell;
import ca.dsrg.mirador.match.MeasureMatrix.MeasureRow;
import ca.dsrg.mirador.merge.AtomicChangeOp;
import ca.dsrg.mirador.merge.ChangeOp;
import ca.dsrg.mirador.ui.ApplyOpTable;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTable;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.45 - Oct 23, 2010
 * @author  Stephen Barrett
 */
abstract public class ModelRepository {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  model_file_bs  Purpose of the argument.
   * @param  model_file_lf  Purpose of the argument.
   * @param  model_file_rt  Purpose of the argument.
   */
  public ModelRepository(File model_file_bs, File model_file_lf,
      File model_file_rt) {
    file_bs_ = model_file_bs;
    file_lf_ = model_file_lf;
    file_rt_ = model_file_rt;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public List<ChangeOp> getChangeList() {
    return changes_list_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  changes  Set-to value for the field.
   * @category  setter
   */
  public void setChangeList(List<ChangeOp> changes) {
    changes_list_ = changes;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public Map<AtomicChangeOp, ChangeOp> getChangeMap() {
    return changes_map_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  changes  Set-to value for the field.
   * @category  setter
   */
  public void setChangeMap(Map<AtomicChangeOp, ChangeOp> changes) {
    changes_map_ = changes;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  The field's current value.
   * @category  getter
   */
  public Set<ChangeOp> getChangeSet() {
    return changes_set_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  changes  Set-to value for the field.
   * @category  setter
   */
  public void setChangeSet(Set<ChangeOp> changes) {
    changes_set_ = changes;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the Mirador representation of the left model augmented with
   * differences from the base model.
   *
   * @return  Mirador representation of left model with differences.
   * @category  getter
   */
  public MiradorModel getDiffModelLeft() {
    return dmodel_lf_;
  }


  /**
   * Gives the Mirador representation of the right model augmented with
   * differences from the base model.
   *
   * @return  Mirador representation of right model with differences.
   * @category  getter
   */
  public MiradorModel getDiffModelRight() {
    return dmodel_rt_;
  }


  /**
   * Gives the input file for the common base model.
   *
   * @return  Base model input file.
   * @category  getter
   */
  public File getFileBase() {
    return file_bs_;
  }


  /**
   * Gives the input file for the left model.
   *
   * @return  Left model input file.
   * @category  getter
   */
  public File getFileLeft() {
    return file_lf_;
  }


  /**
   * Gives the input file for the right model.
   *
   * @return  Right model input file.
   * @category  getter
   */
  public File getFileRight() {
    return file_rt_;
  }


  /**
   * Gives the Mirador representation of the base model.
   *
   * @return  Mirador representation of base model.
   * @category  getter
   */
  public MiradorModel getModelBase() {
    return model_bs_;
  }


  /**
   * Gives the Mirador representation of the left model.
   *
   * @return  Mirador representation of left model.
   * @category  getter
   */
  public MiradorModel getModelLeft() {
    return model_lf_;
  }


  /**
   * Gives the Mirador representation of the right model.
   *
   * @return  Mirador representation of right model.
   * @category  getter
   */
  public MiradorModel getModelMerged() {
    return model_mg_;
  }


  /**
   * Gives the Mirador representation of the right model.
   *
   * @return  Mirador representation of right model.
   * @category  getter
   */
  public MiradorModel getModelRight() {
    return model_rt_;
  }


  /**
   * Gives the model type and change log format.
   *
   * @return  Model type and change log format.
   * @category  getter
   */
  public ModelType getModelType() {
    return model_type_;
  }


  public String getModelTypeName() {
    String typ = "";

    switch (model_type_) {
      case EMF:
        typ = "EMF";
      break;

      case FUJABA:
        typ = "FUJABA";
      break;
    }

    return typ;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public void buildDifferenceModels() {
    EcoreDifference differ;
    String proj_id;
    EPackage proj;
    EPackage root;

    differ =
      new EcoreDifference(model_bs_.getXmiModel(), model_lf_.getXmiModel());

    proj = (EPackage) EcoreUtil.copy(model_lf_.getProject());
    proj_id = "project_left";

    dmodel_lf_ = differ.differenceElements(proj);
    dmodel_lf_.getXmiModel().setID(proj, proj_id);

    // Make new project package part of difference model...
    dmodel_lf_.addElement(proj_id, proj);
    dmodel_lf_.setProject(proj);
    dmodel_lf_.addExtra(proj, new EcoreExtra(proj, proj_id, EcoreType.NONE));

    //   ...and set pointer to the model's root.
    root = (EPackage) dmodel_lf_.getElement(model_lf_.getId(model_lf_.getRoot()));
    dmodel_lf_.setRoot(root);


    differ =
        new EcoreDifference(model_bs_.getXmiModel(), model_rt_.getXmiModel());

    proj = (EPackage) EcoreUtil.copy(model_rt_.getProject());
    proj_id = "project_right";

    dmodel_rt_ = differ.differenceElements(proj);
    dmodel_rt_.getXmiModel().setID(proj, proj_id);

    // Make new project package part of difference model...
    dmodel_rt_.addElement(proj_id, proj);
    dmodel_rt_.setProject(proj);
    dmodel_rt_.addExtra(proj, new EcoreExtra(proj, proj_id, EcoreType.NONE));

    //   ...and set pointer to the model's root.
    root = (EPackage) dmodel_rt_.getElement(model_rt_.getId(model_rt_.getRoot()));
    dmodel_rt_.setRoot(root);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  /**
   * Console dump of an entire Ecore model from the given parent on down.
   *
   * @param  model  Model whose contents are to be dumped.
   */
  static public void dumpEcoreModel(MiradorModel model) {
    XMIResource xmodel = model.getXmiModel();

    for (TreeIterator<EObject> it = xmodel.getAllContents(); it.hasNext();) {
      EObject obj = it.next();

      if (EcoreTyper.typeEObject(obj) == EcoreType.NONE)
        continue;

      EcoreExtra extra = model.getExtra((ENamedElement) obj);

      Debug.dbg.println(indent(obj) + extra.getId() + ':'
          + extra.getClassName() + " (" + extra.getName() + ')');
    }
  }


  static private String indent(EObject contained) {
    StringBuffer buf = new StringBuffer();
    EObject container;

    do {
      container = contained.eContainer();
      contained = container;
      buf.append("    ");
    } while (container != null);

    return buf.toString();
  }


  public void outputAutoReports(MergeSide side, JTable apply_tbl) {
    String name = file_lf_.getName();
    String out_file = name.substring(0, name.lastIndexOf('.'))
        + "(mirador-auto).txt";

    try {
      BufferedWriter file_buf;
      file_buf = new BufferedWriter(new FileWriter("usr/" + out_file));

      // ------------------------------------------------------------------
      // Construct and output report heading with a time stamp.
      String heading_row = String.format("%s", "Mirador exploritory merge of "
          + getModelTypeName() + " models ("
          + DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date())
          + ")\n");
      file_buf.write(heading_row);


      // ------------------------------------------------------------------
      // Input models
      file_buf.write(
          "\n+--------------------------------------------------------------- "
          + "Input Stage --------+");

      if (model_bs_ != null) {
        file_buf.write("\nBase Model: " + file_bs_.getName() + '\n');
        outputEcoreModel(model_bs_, file_buf);
      }

      if (model_lf_ != null) {
        file_buf.write("\n\nLeft Model: " + file_lf_.getName() + '\n');
        outputEcoreModel(model_lf_, file_buf);
      }

      if (model_rt_ != null) {
        file_buf.write("\n\nRight Model: " + file_rt_.getName() + '\n');
        outputEcoreModel(model_rt_, file_buf);
      }


      // ------------------------------------------------------------------
      // Element comparison results
      file_buf.write(
          "\n+--------------------------------------------------------------- "
          + "Match Stage --------+");
      file_buf.write("\nModel Element Matches:\n\n");

      if (side == MergeSide.LEFT) {
        outputElementMatches(dmodel_lf_.getMatchMatrix(), file_buf);
      }
      else if (side == MergeSide.RIGHT)
        outputElementMatches(dmodel_rt_.getMatchMatrix(), file_buf);


      // ------------------------------------------------------------------
      // Change operation application, and merged results
      file_buf.write(
          "\n+--------------------------------------------------------------- "
      	  + "Merge Stage --------+");
      file_buf.write("\nChange Operation Applications:\n\n");

      outputChangeOperations(apply_tbl, file_buf);

      if (model_mg_ != null) {
        file_buf.write("\n\nMerged Model:\n");
        outputEcoreModel(model_mg_, file_buf);
      }

      file_buf.close();
    }
    catch(IOException ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      ex.printStackTrace();
    }
  }


  static private void outputEcoreModel(MiradorModel model,
      BufferedWriter file_buf) throws IOException {
    XMIResource xmodel = model.getXmiModel();

    for (TreeIterator<EObject> it = xmodel.getAllContents(); it.hasNext();) {
      EObject obj = it.next();

      if (EcoreTyper.typeEObject(obj) == EcoreType.NONE)
        continue;
      else if (EcoreTyper.typeEObject(obj) == EcoreType.PACKAGE
          || EcoreTyper.typeEObject(obj) == EcoreType.CLASS)
        file_buf.newLine();

      EcoreExtra extra = model.getExtra((ENamedElement) obj);

      file_buf.write(indent(obj) + extra.getClassName() + ':'
          + extra.getName() + " [" + extra.getId() + "]\n");
    }
  }


  static private void outputElementMatches(MeasureMatrix rankings,
      BufferedWriter file_buf) throws IOException {
    file_buf.write(String.format("%-41s  %10s  %41s\n",
          "          Left Element", "Similarity", "Right Element          "));
    int index = rankings.getMatchStrategyIndex();

    for (MeasureRow row : rankings.values()) {
      EcoreExtra from_element = row.getFromElement();
      EcoreExtra to_element = from_element.getMatch();

      if (to_element != null) {
        String from_id = from_element.getId();
        String to_id = to_element.getId();

        MeasureCell cell = row.get(to_id);
        float rank = cell.getMeasures().get(index);

        String from_name = from_element.getClassName() + ':'
            + from_element.getName() + " [" + from_id + ']';

        String to_name = to_element.getClassName() + ':'
            + to_element.getName() + " [" + to_id + ']';

        file_buf.write(String.format("%44s  %4.2f  %s\n",
            from_name, rank, to_name));
      }
    }
  }


  static private void outputChangeOperations(JTable table,
      BufferedWriter file_buf) throws IOException {
    file_buf.write(String.format("%-43s  %5s  %43s\n",
          "          Left Change Op", "State", "Right Change Op          "));

    for (int i = 0; i <= table.getSelectedRow(); ++i) {
      ChangeOp op_lf = ((ChangeOp) table.getValueAt(i, ApplyOpTable.LEFT));
      String op_string_lf = (op_lf != null) ? op_lf.toString() : "";

      ChangeOp op_rt = ((ChangeOp) table.getValueAt(i, ApplyOpTable.RIGHT));
      String op_string_rt = (op_rt != null) ? op_rt.toString() : "";

      String sym = (String) table.getValueAt(i, ApplyOpTable.STATUS);
      if (sym.equals(">"))
        sym = "    >";
      else if (sym.equals(">>"))
        sym = "   >>";
      else if (sym.equals("<<>>"))
        sym = "<< >>";
      if (sym.equals("X"))
        sym = "  X";
      if (sym.equals("!"))
        sym = "  !";

      file_buf.write(String.format("%43s  %-5s  %-43s\n",
          op_string_lf, sym, op_string_rt));
    }
  }


  // Instance data ----------------------------------------------------------
  /** Model type and format. */
  protected ModelType model_type_ = ModelType.UNKNOWN;

  /** Model input files produced by the modeling tool. */
  protected File file_bs_;
  protected File file_lf_;
  protected File file_rt_;

  /** Model representations as used by Mirador. */
  protected MiradorModel model_bs_;
  protected MiradorModel model_lf_;
  protected MiradorModel model_rt_;
  protected MiradorModel model_mg_;

  /** Difference model representations as used by Mirador. */
  protected MiradorModel dmodel_lf_;
  protected MiradorModel dmodel_rt_;

  /** Merged trace of change operations in various forms. */
  protected Map<AtomicChangeOp, ChangeOp> changes_map_;
  protected Set<ChangeOp> changes_set_;
  protected List<ChangeOp> changes_list_;
  // End instance data ------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**
   * Tag values for the types of models Mirador can merge.
   *
   * @since   v0.9 - Feb 11, 2010
   * @author  Stephen Barrett
   */
  static public enum ModelType { UNKNOWN, EMF, FUJABA }
  // End nested types -------------------------------------------------------
}

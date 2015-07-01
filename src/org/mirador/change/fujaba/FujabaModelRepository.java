/* --------------------------------------------------------------------------+
   FujabaModelRepository.java - Concrete deposit point for the salient items of a
     merge session as drawn from Fujaba base, left, and right models.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Though a base change log is part of the repository, by their nature,
   replicated Fujaba models always contain this common ancestor model.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.Mirador;
import ca.dsrg.mirador.change.ChangeRecord.ElementType;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord.CoobraKind;
import ca.dsrg.mirador.change.fujaba.FujabaRecord.CoobraType;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import ca.dsrg.mirador.model.ModelRepository;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.45 - Oct 23, 2010
 * @author  Stephen Barrett
 */
public class FujabaModelRepository extends ModelRepository {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Creates a snapshot of the change history of the models involved in a merge
   * for later use by Mirador. These Fujaba-specific logs are then converted
   * into the common Mirador change record format.
   *
   * A null base log implies a two-way merge.
   *
   * @param  change_log_bs  Change log for the "base" model.
   * @param  change_log_lf  Change log for the "left" model.
   * @param  change_log_rt  Change log for the "right" model.
   */
  public FujabaModelRepository(File change_log_bs, File change_log_lf,
      File change_log_rt) {
    super(change_log_bs, change_log_lf, change_log_rt);
    model_type_ = ModelType.FUJABA;

    change_repo_ = new FujabaChangeRepository(file_bs_, file_lf_, file_rt_);
    model_bs_ = buildEmfModel(change_repo_.getTransactionsBase());

    List<FujabaTransaction> txs_lf =
        new ArrayList<FujabaTransaction>(change_repo_.getTransactionsBase());
    txs_lf.addAll(change_repo_.getTransactionsLeft());
    model_lf_ = buildEmfModel(txs_lf);

    List<FujabaTransaction> txs_rt =
        new ArrayList<FujabaTransaction>(change_repo_.getTransactionsBase());
    txs_rt.addAll(change_repo_.getTransactionsRight());
    model_rt_ = buildEmfModel(txs_rt);

    model_mg_ = buildEmfModel(change_repo_.getTransactionsBase());  // FIXME:1 Find better way to copy base model.
    //model_mg_ = new MiradorModel(model_bs_.getXmiModel());  // FIXME:2 Does not make complete model.
    //EObject obj = EcoreUtil.copy(model_bs_.getRoot());
    //EcoreUtil.copy(model_bs_.getRoot());

    if (Debug.dbg.isDebug()) {
      Debug.dbg.println("\n\n\n\t    --- BASE ECORE MODEL ---");
      dumpEcoreModel(model_bs_);

      Debug.dbg.println("\n\n\n\t    --- LEFT ECORE MODEL ---");
      dumpEcoreModel(model_lf_);

      Debug.dbg.println("\n\n\n\t    --- RIGHT ECORE MODEL ---");
      dumpEcoreModel(model_rt_);
    }

    if (Debug.dbg.isDebug() || Mirador.isAutoMode()) {
      String name = change_log_bs.getName();
      name = name.substring(0, name.lastIndexOf('.'));

      model_bs_.saveXmiModel("usr/" + name + "(mirador-base).ecore");
      model_lf_.saveXmiModel("usr/" + name + "(mirador-left).ecore");
      model_rt_.saveXmiModel("usr/" + name + "(mirador-right).ecore");
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  What is returned by the method.
   */
  public FujabaChangeRepository getChangeRepository() {
    return change_repo_;
  }


  static private EClass getContainerOpposite(EReference ref) {
    EClass container = null;
    EAnnotation ann = ref.getEAnnotation("container");

    if (ann != null && !ann.getReferences().isEmpty())
      container = (EClass) ann.getReferences().get(0);

    return container;
  }


  static private void setFeatureContainment(EStructuralFeature contained,
      EClass container) {
    if (container == null && contained.getClass() == EReferenceImpl.class)
      container = getContainerOpposite((EReference) contained);

    if (container != null) {
      if (!container.getEStructuralFeatures().contains(contained))
        container.getEStructuralFeatures().add(contained);
    }
  }


  static private void unsetFeatureContainment(EStructuralFeature contained,
      EClass container) {
    if (container == null && contained.getClass() == EReferenceImpl.class)
      container = getContainerOpposite((EReference) contained);

    if (container != null)
        container.getEStructuralFeatures().remove(contained);
  }


  static private EReference getReferenceOpposite(EReference ref) {
    EReference ref_op = null;
    EAnnotation ann = ref.getEAnnotation("opposite");

    if (ann != null)
      ref_op = (EReference) ann.getReferences().get(0);

    return ref_op;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  /**
   * Console dump of the effects of an element alteration change.
   *
   * @param  change  Executed change in the form of a log record.
   * @param  altered  The altered Mirador model element.
   * @param  new_value  To be assigned value of altered target field.
   */
  static private void dumpAlter(FujabaChangeRecord change,
      ENamedElement altered, String new_value) {
    if (Debug.dbg.isDebug()) {
      Debug.dbg.println(change);

      if (altered != null) {
        String cls_name = altered.getClass().getSimpleName();
        Debug.dbg.println("   Altered - " + change.getElementId() + ':'
            + cls_name + " (" + altered.getName() + "), "
            + change.getFieldName() + " <- " + new_value
            + " [" + change.getOldValue() + ']');
      }
      else
        Debug.dbg.println("   Alteration - " + change.getElementId() + ':'
            + change.getFieldName() + "  >>> ignored <<<");
    }
  }


  /**
   * Console dump of the effects of an element creation change.
   *
   * @param  change  Executed change in the form of a log record.
   * @param  created  The created Mirador model element.
   */
  static private void dumpCreate(FujabaChangeRecord change,
      ENamedElement created) {
    if (Debug.dbg.isDebug()) {
      Debug.dbg.println(change);

      if (created != null) {
        String cls_name = created.getClass().getSimpleName();
        Debug.dbg.println("   Created - " + change.getElementId() + ':'
            + cls_name + " (" + change.getElementType() + ')');
      }
      else
        Debug.dbg.println("   Creation - " + change.getElementId() + ':'
            + change.getElementType() + "  >>> ignored <<<");
    }
  }


  /**
   * Console dump of the effects of an element destruction change.
   *
   * @param  change  Executed change in the form of a log record.
   * @param  destroyed  The destroyed Mirador model element.
   */
  static private void dumpDestroy(FujabaChangeRecord change,
      ENamedElement destroyed) {
    if (Debug.dbg.isDebug()) {
      Debug.dbg.println(change);

      if (destroyed != null) {
        String cls_name = destroyed.getClass().getSimpleName();
        Debug.dbg.println("   Destroyed - " + change.getElementId() + ':'
            + cls_name + " (" + destroyed.getName() + ')');
      }
      else
        Debug.dbg.println("   Destruction - " + change.getElementId() + ':'
            + "  >>> ignored <<<");
    }
  }


  /**
   * Executes model change that alters the left role of an association of a
   * model element.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterAssociation(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    EClass assoc = (EClass) to_alter;
    String id;
    EReference ref;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      ref = (EReference) model.getElement(id);

      if (assoc.getEStructuralFeatures().isEmpty())
        assoc.getEStructuralFeatures().add(ref);
      else {
        EReference ref_op = (EReference) assoc.getEStructuralFeatures().get(0);
        assoc.getEStructuralFeatures().remove(0);

        ref.getEAnnotation("opposite").getReferences().add(ref_op);
        ref_op.getEAnnotation("opposite").getReferences().add(ref);

        ref.setEOpposite(ref_op);  // Default state is to set references
        ref_op.setEOpposite(ref);  //   to be linked in both directions.
      }

      dumpAlter(change, to_alter, id + ':'
          + ref.getClass().getSimpleName()
          + " (" + ref.getName() + ')');
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterAttribute(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EAttribute attr;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      attr = (EAttribute) model.getElement(id);

      setFeatureContainment(attr, (EClass) to_alter);

      dumpAlter(change, to_alter, id + ':' + attr.getClass().getSimpleName()
          + " (" + attr.getName() + ')');
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterAttributeType(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EClassifier type;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      type = (EClassifier) model.getElement(id);

      if (type != null && ((EAttribute) to_alter).getEAttributeType() != type) {
        ((EAttribute) to_alter).setEType(type);

        dumpAlter(change, to_alter, id + ':' + type.getClass().getSimpleName()
            + " (" + type.getName() + ')');
      }
      else
        dumpAlter(change, null, null);
    }
  }


  /**
   * Executes model change that alters the left role of an association of a
   * model element.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterCardinality(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EClass card;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      card = (EClass) model.getElement(id);

      EAnnotation ann = card.getEAnnotation("limits");
      Integer lower = new Integer(ann.getDetails().get("lower"));
      Integer upper = new Integer(ann.getDetails().get("upper"));

      ((EReference) to_alter).setLowerBound(lower);
      ((EReference) to_alter).setUpperBound(upper);

      dumpAlter(change, to_alter, lower.toString() + ".." + upper.toString());
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterClassifier(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EPackage pkg;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      pkg = (EPackage) model.getElement(id);

      if (!pkg.getEClassifiers().contains(to_alter)) {
        pkg.getEClassifiers().add((EClassifier) to_alter);

        dumpAlter(change, to_alter, id + ':'
            + pkg.getClass().getSimpleName()
            + " (" + pkg.getName() + ')');
      }
      else
        dumpAlter(change, null, null);
    }
  }


  /**
   * Executes model change that alters the left role of an association of a
   * model element.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterGenericity(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    EClass gen = (EClass) to_alter;
    String id;
    EClass cls;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      cls = (EClass) model.getElement(id);

      if (gen.getESuperTypes().isEmpty())
        gen.getESuperTypes().add(cls);
      else {
        EClass cls_op = gen.getESuperTypes().get(0);
        gen.getESuperTypes().remove(0);

        if (change.getFieldName().equals("superclass"))
          cls_op.getESuperTypes().add(cls);
        else if (change.getFieldName().equals("subclass"))
          cls.getESuperTypes().add(cls_op);
      }

      dumpAlter(change, to_alter, id + ':'
          + cls.getClass().getSimpleName()
          + " (" + cls.getName() + ')');
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterOperation(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EOperation oper;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      oper = (EOperation) model.getElement(id);

      if (!((EClass) to_alter).getEOperations().contains(oper)) {
        ((EClass) to_alter).getEOperations().add(oper);

        dumpAlter(change, to_alter, id + ':'
            + oper.getClass().getSimpleName()
            + " (" + oper.getName() + ')');
      }
      else
        dumpAlter(change, null, null);
    }
  }


  /**
   * Executes model change that alters a model element's name.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterName(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String new_name = change.getNewValue();
    if (new_name.contains("deleted {")) {  // Only affects deleted packages.
      new_name = new_name.replace("deleted {", "");
      new_name = new_name.replace("}", "");
    }

    if (!new_name.equals(to_alter.getName())) {
      to_alter.setName(new_name);

      dumpAlter(change, to_alter, to_alter.getName());
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterParameter(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EParameter param;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      param = (EParameter) model.getElement(id);

      if (!((EOperation) to_alter).getEParameters().contains(param)) {
        ((EOperation) to_alter).getEParameters().add(param);

        dumpAlter(change, to_alter, id + ':'
            + param.getClass().getSimpleName()
            + " (" + param.getName() + ')');
      }
      else
        dumpAlter(change, null, null);
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterParameterType(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EClassifier type;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      type = (EClassifier) model.getElement(id);

      if (((EParameter) to_alter).getEType() != type) {
        ((EParameter) to_alter).setEType(type);

        dumpAlter(change, to_alter, id + ':'
            + type.getClass().getSimpleName()
            + " (" + type.getName() + ')');
      }
      else
        dumpAlter(change, null, null);
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterReference(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EReference ref;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      ref = (EReference) model.getElement(id);

      if (ref == null) {
        id = change.getOldValue();
        ref = (EReference) model.getElement(id);
        EReference ref_op = getReferenceOpposite(ref);
        ref_op.getEAnnotation("container").getReferences().clear();
      }
      else {
        ref.setEType((EClass) to_alter);

        EReference ref_op = getReferenceOpposite(ref);
        if (ref_op != null) {
          ref_op.getEAnnotation("container").getReferences().add(to_alter);

          dumpAlter(change, to_alter, id + ':' + ref_op.getClass().getSimpleName()
              + " (" + ref_op.getName() + ')');
        }
        else
          dumpAlter(change, null, null);
      }
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterResultType(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id;
    EClassifier type;

    if (!change.getNewValue().equals("-")) {
      id = change.getNewValue();
      type = (EClassifier) model.getElement(id);

      if (type != null && ((EOperation) to_alter).getEType() != type) {
        ((EOperation) to_alter).setEType(type);

        dumpAlter(change, to_alter, id + ':' + type.getClass().getSimpleName()
            + " (" + type.getName() + ')');
      }
      else
        dumpAlter(change, null, null);
    }
  }


  /**
   * Executes model change that alters the left role of an association of a
   * model element.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterRoleEnd(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    EAnnotation ann = to_alter.getEAnnotation("ends");
    ann.getDetails().put("new", change.getNewValue());
    ann.getDetails().put("old", change.getOldValue());

    dumpAlter(change, to_alter, change.getNewValue());
  }


  /**
   * Executes model change that alters the root element of a model.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   * @param  model  Model to alter the root package for.
   */
  static private void alterRootPackage(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    // Fetch package that is to become root of the model.
    String root_id = change.getNewValue();
    EPackage root = (EPackage) model.getElement(root_id);

    // Set root node in the Ecore and Mirador models.
    model.getXmiModel().getContents().add(root);
    model.setRoot(root);

    // Set up a project node as well.
    String proj_id = model.getXmiModel().getID(to_alter);
    EPackage proj = (EPackage) to_alter;
    model.setProject(proj);
    EcoreExtra extra = new EcoreExtra(proj, proj_id, EcoreType.NONE);
    model.addExtra(proj, extra);

    dumpAlter(change, root, root_id + ':' + root.getClass().getSimpleName()
        + " (" + root.getName() + ')');

    for (Iterator<ENamedElement> it = model.elementIterator(); it.hasNext();) {
      ENamedElement elem = it.next();

      if (elem.getClass() == EDataTypeImpl.class) {
        EDataType type = (EDataType) elem;
        root.getEClassifiers().add(type);

        Debug.dbg.println("   Altered - " + root_id + ':'
            + root.getClass().getSimpleName() + " (" + root.getName() + "), "
            + "classifiers" + " <- " + type.getInstanceClassName() + " [-]");
      }
    }
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterStatic(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) { // FIXME:3 Forces "static" in Fujaba to be "lowerBound=1" in Ecore.
    String id = change.getElementId();
    EAttribute attr = (EAttribute) model.getElement(id);
                            //    String flag = change.getNewValue();
    attr.setLowerBound(1);  //    attr.setTransient(flag.equals("true"));

    dumpAlter(change, to_alter, change.getNewValue());
  }


  /**
   * Executes model change that alters a model element's parent.
   *
   * @param  change  Change to execute in the form of a log record.
   * @param  to_alter  Target model element of pending change.
   */
  static private void alterSubpackage(ENamedElement to_alter,
      FujabaChangeRecord change, MiradorModel model) {
    String id = change.getNewValue();
    EPackage pkg = (EPackage) model.getElement(id);

    // Guard in case of no actual change.
    if (!((EPackage) to_alter).getESubpackages().contains(pkg)) {
      ((EPackage) to_alter).getESubpackages().add(pkg);

      dumpAlter(change, to_alter, id + ':'
          + pkg.getClass().getSimpleName()
          + " (" + pkg.getName() + ')');
    }
    else
      dumpAlter(change, null, null);
  }


  /**
   * Builds an Ecore model that reflects the changes executed against the model
   * replica -- creating in effect, a new model version.
   *
   * @param  txs  List of change transactions to execute against replica.
   * @return  Ecore model reflecting the execution of all change operations.
   */
  static private MiradorModel buildEmfModel(List<FujabaTransaction> txs) {
    MiradorModel model = new MiradorModel();
    for (FujabaTransaction tx : txs)
      dispatchChanges(tx, model);

    // Clean up model.
    model.getRoot().setName(model.getProject().getName());
    model.getProject().setName("project");

    // Model is not yet fully linked...
    for (Iterator<ENamedElement> it = model.elementIterator(); it.hasNext();) {
      ENamedElement elem = it.next();
      if (elem.getClass() == EReferenceImpl.class) {
        finalizeRoleEnd((EReference) elem, model);

        // Remove scaffolding.
        elem.getEAnnotations().clear();
      }
    }

    return model;
  }


  /**
   * Creates a fake Ecore "association".
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore class.
   */
  static private EClass createAssociation(FujabaChangeRecord change,
      MiradorModel model) {
    EClass created = EcoreFactory.eINSTANCE.createEClass();

    String id = change.getElementId();
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.CLASS);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore attribute.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore attribute.
   */
  static private EAttribute createAttribute(FujabaChangeRecord change,
      MiradorModel model) {
    EAttribute created = EcoreFactory.eINSTANCE.createEAttribute();

    String id = change.getElementId();
    model.getXmiModel().setID(created, id);
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.ATTRIBUTE);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore reference to be used as a role's cardinality.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore reference.
   */
  static private EClass createCardinality(FujabaChangeRecord change,
      MiradorModel model) {
    EClass created = EcoreFactory.eINSTANCE.createEClass();

    String card = change.getFieldKey();
    String[] bounds = card.split("\\.\\.");
    String lower = "1";
    String upper = "1";

    if (bounds.length > 0)
      if (bounds[0].matches("\\d"))
        lower = bounds[0];
      else if (bounds[0].matches("[\\*nN]"))
        lower = "-1";

    if (bounds.length > 1) {
      if (bounds[1].matches("\\d"))
        upper = bounds[1];
      else if (bounds[1].matches("[\\*nN]"))
        upper = "-1";
    }

    EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
    ann.setSource("limits");
    ann.getDetails().put("lower", lower);
    ann.getDetails().put("upper", upper);
    created.getEAnnotations().add(0, ann);
    created.setName("cardinality");

    String id = change.getElementId();
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.CLASS);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore class.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore class.
   */
  static private EClass createClass(FujabaChangeRecord change,
      MiradorModel model) {
    EClass created = EcoreFactory.eINSTANCE.createEClass();

    String id = change.getElementId();
    model.getXmiModel().setID(created, id);
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.CLASS);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore reference to be used as an is-a association.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore reference.
   */
  static private EClass createGeneralization(FujabaChangeRecord change,
      MiradorModel model) {
    EClass created = EcoreFactory.eINSTANCE.createEClass();
    created.setName("general");

    String id = change.getElementId();
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.CLASS);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore class operation.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore operation.
   */
  static private EOperation createOperation(FujabaChangeRecord change,
      MiradorModel model) {
    EOperation created = EcoreFactory.eINSTANCE.createEOperation();

    String id = change.getElementId();
    model.getXmiModel().setID(created, id);
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.OPERATION);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore package.
   * Creates root of an Ecore model. The root is always a package.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore package.
   */
  static private EPackage createPackage(FujabaChangeRecord change,
      MiradorModel model) {
    EPackage created = EcoreFactory.eINSTANCE.createEPackage();

    String id = change.getElementId();
    model.getXmiModel().setID(created, id);
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.PACKAGE);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore operation parameter.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore parameter.
   */
  static private EParameter createParameter(FujabaChangeRecord change,
      MiradorModel model) {
    EParameter created = EcoreFactory.eINSTANCE.createEParameter();

    String id = change.getElementId();
    model.getXmiModel().setID(created, id);
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.PARAMETER);
    model.addExtra(created, extra);

    return created;
  }


  /**
   * Creates an Ecore reference.
   * Creates an Ecore reference for use as one side of an association.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore reference.
   */
  static private EReference createReference(FujabaChangeRecord change,
      MiradorModel model) {
    EReference created = EcoreFactory.eINSTANCE.createEReference();

    EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
    ann.setSource("opposite");
    ann.setEModelElement(created);

    ann = EcoreFactory.eINSTANCE.createEAnnotation();
    ann.setSource("container");
    ann.setEModelElement(created);

    ann = EcoreFactory.eINSTANCE.createEAnnotation();
    ann.setSource("ends");
    ann.getDetails().put("new", "0");
    ann.getDetails().put("old", "0");
    created.getEAnnotations().add(0, ann);

    String id = change.getElementId();
    model.getXmiModel().setID(created, id);
    model.addElement(id, created);

    EcoreExtra extra = new EcoreExtra(created, id, EcoreType.REFERENCE);
    model.addExtra(created, extra);

    return created;
  }


  /**
     * Creates an Ecore data type.
     *
     * @param  change  Executed change in the form of a log record.
     * @return  Wrapped Ecore data types.
     */
    static private EDataType createType(FujabaChangeRecord change,
        MiradorModel model) {
      EDataType created = null;
      Class<?> type = null;
      String type_name = change.getFieldKey();

      if (type_name.equals("Boolean"))
        type = java.lang.Boolean.class;
      else if (type_name.equals("String"))
        type = java.lang.String.class;
      else if (type_name.equals("Integer"))
        type = java.lang.Integer.class;
      else if (type_name.equals("Byte"))
        type = java.lang.Byte.class;
      else if (type_name.equals("Float"))
        type = java.lang.Float.class;
      else if (type_name.equals("Double"))
        type = java.lang.Double.class;
      else if (type_name.equals("Character"))
        type = java.lang.Character.class;

      if (type != null) {
        created = EcoreFactory.eINSTANCE.createEDataType();
        created.setName(type_name);
        created.setInstanceClass(type);
        created.setSerializable(true);

        String id = change.getElementId();
        model.getXmiModel().setID(created, id);
        model.addElement(id, created);

        EcoreExtra extra = new EcoreExtra(created, id, EcoreType.DATATYPE);
        model.addExtra(created, extra);
      }

      return created;
    }


  /**
   * Sends model change operations of a transaction to the appropriate routine
   * for execution against the supplied Ecore model.
   *
   * @param  tx  Transaction of model change operations to execute.
   * @param  model  Ecore model to execute the transaction against.
   */
  static private void dispatchChanges(FujabaTransaction tx,
      MiradorModel model) {
    Debug.dbg.println("\n\t>>> Dispatching " + tx.getMergeSide()
        + " Transaction - " + tx.getRealId() + ':'
        + tx.getName() + " <<<");

    for (FujabaRecord rec : tx.getChanges()) {
      if (rec.getRecordType() == CoobraType.CHANGE) {
        FujabaChangeRecord change = (FujabaChangeRecord) rec;

        if (change.getChangeKind() == CoobraKind.CREATE_OBJECT)
          executeCreation(change, model);
        else if (change.getChangeKind() == CoobraKind.ALTER_FIELD)
          executeAlteration(change, model);
        else if (change.getChangeKind() == CoobraKind.DESTROY_OBJECT)
          executeDestruction(change, model);
      }
    }
  }


  /**
   * Executes an alter type of model change operation against the supplied
   * Ecore model.
   *
   * @param  change  Model change operations to execute.
   * @param  model  Ecore model to execute the change operation against.
   */
  static private ENamedElement executeAlteration(FujabaChangeRecord change,
      MiradorModel model) {
    String id = change.getElementId();
    ENamedElement altered = model.getElement(id);

    if (altered != null) {
      String field = change.getFieldName();

      if (field.equals("rootPackage"))
        alterRootPackage(altered, change, model);
      else if (field.equals("name"))
        alterName(altered, change, model);
      else if (field.equals("declaredInPackage"))
        alterClassifier(altered, change, model);
      else if (field.equals("packages"))
        alterSubpackage(altered, change, model);
      else if (field.equals("attrs"))
        alterAttribute(altered, change, model);
      else if (field.equals("attrType"))
        alterAttributeType(altered, change, model);
      else if (field.equals("methods"))
        alterOperation(altered, change, model);
      else if (field.equals("resultType"))
        alterResultType(altered, change, model);
      else if (field.equals("param"))
        alterParameter(altered, change, model);
      else if (field.equals("paramType"))
        alterParameterType(altered, change, model);
      else if (field.equals("roles"))
        alterReference(altered, change, model);
      else if (field.equals("leftRole"))
        alterAssociation(altered, change, model);
      else if (field.equals("rightRole"))
        alterAssociation(altered, change, model);
      else if (field.equals("adornment"))
        alterRoleEnd(altered, change, model);
      else if (field.equals("card"))
        alterCardinality(altered, change, model);
      else if (field.equals("static"))
        alterStatic(altered, change, model);
      else if (field.equals("subclass"))
        alterGenericity(altered, change, model);
      else if (field.equals("superclass"))
        alterGenericity(altered, change, model);
      else
        dumpAlter(change, null, null);
    }

    return altered;
  }


  /**
   * Executes a creation type of model change operation against the supplied
   * Ecore model.
   *
   * @param  change  Model change operations to execute.
   * @param  model  Ecore model to execute the change operation against.
   */
  static private ENamedElement executeCreation(FujabaChangeRecord change,
      MiradorModel model) {
    ENamedElement created = null;
    ElementType type = change.getElementType();

    switch (type) {
      case PACKAGE:
        created = createPackage(change, model);
      break;

      case CLASS:
        created = createClass(change, model);
      break;

      case ATTRIBUTE:
        created = createAttribute(change, model);
      break;

      case OPERATION:
        created = createOperation(change, model);
      break;

      case PARAMETER:
        created = createParameter(change, model);
      break;

      case ASSOCIATION:
        created = createAssociation(change, model);
      break;

      case REFERENCE:
        created = createReference(change, model);
      break;

      case CARDINALITY:
        created = createCardinality(change, model);
      break;

      case GENERALIZATION:
        created = createGeneralization(change, model);
      break;

      case DATATYPE:
        created = createType(change, model);
      break;
    }

    if (created != null)
      dumpCreate(change, created);

    return created;
  }


  /**
   * Creates an Ecore operation parameter.
   *
   * @param  change  Executed change in the form of a log record.
   * @return  Wrapped Ecore parameter.
   */
  static private ENamedElement executeDestruction(FujabaChangeRecord change,
      MiradorModel model) {
    ENamedElement destroyed = model.getElement(change.getElementId());

    if (destroyed != null) {
      model.removeElement(destroyed);
      model.removeExtra(destroyed);

      model.getXmiModel().setID(destroyed, null);
      EcoreUtil.delete(destroyed, true);
      dumpDestroy(change, destroyed);
    }

    return destroyed;
  }


  static private void finalizeRoleEnd(EReference ref, MiradorModel model) {
      EReference ref_op = getReferenceOpposite(ref);

      EAnnotation ann = ref.getEAnnotation("ends");
      Integer new_end = new Integer(ann.getDetails().get("new"));

      switch (new_end) {
        case 0:  // no direction (affects near end, towards "ref")
          setFeatureContainment(ref_op, (EClass) ref.getEType());
          ref_op.setContainment(false);
        break;

        case 1:  // aggregate (affects far end, towards "ref_op")
          setFeatureContainment(ref_op, (EClass) ref.getEType());
          ref_op.setContainment(false);
        break;

        case 2:  // containment (affects far end, towards "ref_op")
          setFeatureContainment(ref_op, (EClass) ref.getEType());
          ref_op.setContainment(true);
        break;

        case 3:  // unidirectional (affects near end, towards "ref")
          unsetFeatureContainment(ref_op, null);
          ref.setEOpposite(null);
          ref_op.setEOpposite(null);
          ref_op.setContainment(false);
          model.getXmiModel().setID(ref_op, null);
        break;
      }
    }


  // Instance data ----------------------------------------------------------
  private FujabaChangeRepository change_repo_;
  // End instance data ------------------------------------------------------
}

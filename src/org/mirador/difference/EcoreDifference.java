  /* --------------------------------------------------------------------------+
   EcoreDifference.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   -------------------------------------------------------------------------*/
package ca.dsrg.mirador.difference;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.difference.MiradorTyper.MiradorType;
import ca.dsrg.mirador.model.AddedEAttribute;
import ca.dsrg.mirador.model.AddedEClass;
import ca.dsrg.mirador.model.AddedEDataType;
import ca.dsrg.mirador.model.AddedEOperation;
import ca.dsrg.mirador.model.AddedEPackage;
import ca.dsrg.mirador.model.AddedEParameter;
import ca.dsrg.mirador.model.AddedEReference;
import ca.dsrg.mirador.model.AlteredEAttribute;
import ca.dsrg.mirador.model.AlteredEClass;
import ca.dsrg.mirador.model.AlteredEDataType;
import ca.dsrg.mirador.model.AlteredEOperation;
import ca.dsrg.mirador.model.AlteredEPackage;
import ca.dsrg.mirador.model.AlteredEParameter;
import ca.dsrg.mirador.model.AlteredEReference;
import ca.dsrg.mirador.model.DeletedEAttribute;
import ca.dsrg.mirador.model.DeletedEClass;
import ca.dsrg.mirador.model.DeletedEDataType;
import ca.dsrg.mirador.model.DeletedEOperation;
import ca.dsrg.mirador.model.DeletedEPackage;
import ca.dsrg.mirador.model.DeletedEParameter;
import ca.dsrg.mirador.model.DeletedEReference;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.xmi.XMIResource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.52 - Oct 30, 2010
 * @author  Stephen Barrett
 */
public class EcoreDifference {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  original  Purpose of the argument.
   * @param  replica  Purpose of the argument.
   */
  public EcoreDifference(XMIResource original, XMIResource replica) {
    diff_model_ = new MiradorModel();
    difference_ = diff_model_.getXmiModel();
    original_ = original;
    replica_ = replica;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public MiradorModel getDiffModel() {
    return diff_model_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  private EAttribute addElement(EClass container, EAttribute contained) {
    EAttribute diff;
    String id = getId(contained);

    if (add_ids_.contains(id)) {
      diff = new AddedEAttribute();

      // Place new element in proper container.
      container.getEStructuralFeatures().add(diff);
    }
    else if (del_ids_.contains(id)) {
      diff = new DeletedEAttribute();

      id = difference_.getID(container);
      container = (difference_.getEObject(id + '_') != null)
          ? (EClass) difference_.getEObject(id + '_')
          : (EClass) difference_.getEObject(id);
      container.getEStructuralFeatures().add(diff);
    }
    else if (alt_ids_.contains(id)) {
      // Create new object to represent updated element in difference model.
      diff = EcoreFactory.eINSTANCE.createEAttribute();

      // Place updated element in proper container.
        container.getEStructuralFeatures().add(diff);

      // Create new object to represent changed element in difference model.
      AlteredEAttribute alt = new AlteredEAttribute();  // TODO:3 Change to use copy constructor.
      alt.setName(contained.getName());
      alt.setUpdated(diff);  // Link changed element to updated element.

      id = getId(contained) + '_';
      difference_.setID(alt, id);
      diff_model_.addElement(id, alt);

      EcoreExtra extra =
          new EcoreExtra(alt, id, EcoreType.ATTRIBUTE, MiradorType.ALTER);
      diff_model_.addExtra(alt, extra);

      // Place changed element in either changed container, or the model root.
      id = difference_.getID(container);
      container = (EClass) difference_.getEObject(id + '_');

      if (container != null)
        container.getEStructuralFeatures().add(alt);
      else
        difference_.getContents().add(alt);
    }
    else {
      diff = EcoreFactory.eINSTANCE.createEAttribute();

      if (container != null)
        container.getEStructuralFeatures().add(diff);
      else
        difference_.getContents().add(diff);
    }

    return diff;
  }


  private EClass addElement(EPackage container, EClass contained) {
    EClass diff;
    String id = getId(contained);

    if (add_ids_.contains(id)) {
      diff = new AddedEClass();

      // Place new element in proper container.
      container.getEClassifiers().add(diff);
    }
    else if (del_ids_.contains(id)) {
      diff = new DeletedEClass();

      id = difference_.getID(container);
      container = (difference_.getEObject(id + '_') != null)
          ? (EPackage) difference_.getEObject(id + '_')
          : (EPackage) difference_.getEObject(id);
//      container.getEClassifiers().add(diff);

      if (container != null && container instanceof DeletedEPackage)
          container.getEClassifiers().add(diff);
      else
        difference_.getContents().add(diff);
    }
    else if (alt_ids_.contains(id)) {
      // Create new object to represent updated element in difference model.
      diff = EcoreFactory.eINSTANCE.createEClass();

      // Place updated element in proper container.
      container.getEClassifiers().add(diff);

      // Create new object to represent changed element in difference model.
      AlteredEClass alt = new AlteredEClass();  // TODO:3 Change to use copy constructor.
      alt.setName(contained.getName());
      alt.setUpdated(diff);  // Link changed element to updated element.

      id = getId(contained) + '_';
      difference_.setID(alt, id);
      diff_model_.addElement(id, alt);

      EcoreExtra extra =
          new EcoreExtra(alt, id, EcoreType.CLASS, MiradorType.ALTER);
      diff_model_.addExtra(alt, extra);

      // Place changed element in either changed container, or the model root.
      id = difference_.getID(container);
      container = (EPackage) difference_.getEObject(id + '_');

      if (container != null)
        container.getEClassifiers().add(alt);
      else
        difference_.getContents().add(alt);
    }
    else {
      diff = EcoreFactory.eINSTANCE.createEClass();

      if (container != null)
        container.getEClassifiers().add(diff);
      else
        difference_.getContents().add(diff);
    }

    return diff;
  }


  private EDataType addElement(EPackage container, EDataType contained) {
    EDataType diff;
    String id = getId(contained);

    if (add_ids_.contains(id)) {
      diff = new AddedEDataType();

      // Place new element in proper container.
      container.getEClassifiers().add(diff);
    }
    else if (del_ids_.contains(id)) {
      diff = new DeletedEDataType();

      id = difference_.getID(container);
      container = (difference_.getEObject(id + '_') != null)
          ? (EPackage) difference_.getEObject(id + '_')
          : (EPackage) difference_.getEObject(id);
      container.getEClassifiers().add(diff);
    }
    else if (alt_ids_.contains(id)) {
      // Create new object to represent updated element in difference model.
      diff = EcoreFactory.eINSTANCE.createEDataType();

      // Place updated element in proper container.
      container.getEClassifiers().add(diff);

      // Create new object to represent changed element in difference model.
      AlteredEDataType alt = new AlteredEDataType();  // TODO:3 Change to use copy constructor.
      alt.setName(contained.getName());
      alt.setUpdated(diff);  // Link changed element to updated element.

      id = getId(contained) + '_';
      difference_.setID(alt, id);
      diff_model_.addElement(id, alt);

      EcoreExtra extra =
          new EcoreExtra(alt, id, EcoreType.DATATYPE, MiradorType.ALTER);
      diff_model_.addExtra(alt, extra);

      // Place changed element in either changed container, or the model root.
      id = difference_.getID(container);
      container = (EPackage) difference_.getEObject(id + '_');

      if (container != null)
        container.getEClassifiers().add(alt);
      else
        difference_.getContents().add(alt);
    }
    else {
      diff = EcoreFactory.eINSTANCE.createEDataType();

      if (container != null)
        container.getEClassifiers().add(diff);
      else
        difference_.getContents().add(diff);
    }

    return diff;
  }


  private EOperation addElement(EClass container, EOperation contained) {
    EOperation diff;
    String id = getId(contained);

    if (add_ids_.contains(id)) {
      diff = new AddedEOperation();

      // Place new element in proper container.
      container.getEOperations().add(diff);
    }
    else if (del_ids_.contains(id)) {
      diff = new DeletedEOperation();

      id = difference_.getID(container);
      container = (difference_.getEObject(id + '_') != null)
          ? (EClass) difference_.getEObject(id + '_')
          : (EClass) difference_.getEObject(id);
      container.getEOperations().add(diff);
    }
    else if (alt_ids_.contains(id)) {
      // Create new object to represent updated element in difference model.
      diff = EcoreFactory.eINSTANCE.createEOperation();

      // Place updated element in proper container.
      container.getEOperations().add(diff);

      // Create new object to represent changed element in difference model.
      AlteredEOperation alt = new AlteredEOperation();  // TODO:3 Change to use copy constructor.
      alt.setName(contained.getName());
      alt.setUpdated(diff);  // Link changed element to updated element.

      id = getId(contained) + '_';
      difference_.setID(alt, id);
      diff_model_.addElement(id, alt);

      EcoreExtra extra =
          new EcoreExtra(alt, id, EcoreType.OPERATION, MiradorType.ALTER);
      diff_model_.addExtra(alt, extra);

      // Place changed element in either changed container, or the model root.
      id = difference_.getID(container);
      container = (EClass) difference_.getEObject(id + '_');

      if (container != null)
        container.getEOperations().add(alt);
      else
        difference_.getContents().add(alt);
    }
    else {
      diff = EcoreFactory.eINSTANCE.createEOperation();

      if (container != null)
        container.getEOperations().add(diff);
      else
        difference_.getContents().add(diff);
    }

    return diff;
  }


  private EPackage addElement(EPackage container, EPackage contained) {
    EPackage diff;
    String id = getId(contained);

    if (add_ids_.contains(id)) {
      diff = new AddedEPackage();

      // Place new element in proper container.
      container.getESubpackages().add(diff);
    }
    else if (del_ids_.contains(id)) {
      diff = new DeletedEPackage();

      id = difference_.getID(container);
      container = (difference_.getEObject(id + '_') != null)
          ? (EPackage) difference_.getEObject(id + '_')
          : (EPackage) difference_.getEObject(id);
      container.getESubpackages().add(diff);
    }
    else if (alt_ids_.contains(id)) {
      // Create new object to represent updated element in difference model.
      diff = EcoreFactory.eINSTANCE.createEPackage();

      // Place updated element in proper container.
      if (container != null)  // Can only happen with root package.
        container.getESubpackages().add(diff);
      else  // e.g., project name change.
        difference_.getContents().add(diff);

      // Create new object to represent changed element in difference model.
      AlteredEPackage alt = new AlteredEPackage();  // TODO:3 Change to use copy constructor.
      alt.setName(contained.getName());
      alt.setUpdated(diff);  // Link changed element to updated element.

      id = getId(contained) + '_';
      difference_.setID(alt, id);
      diff_model_.addElement(id, alt);

      EcoreExtra extra =
          new EcoreExtra(alt, id, EcoreType.PACKAGE, MiradorType.ALTER);
      diff_model_.addExtra(alt, extra);

      // Place changed element in either changed container, or the model root.
      id = difference_.getID(container);
      container = (EPackage) difference_.getEObject(id + '_');

      if (container != null)
        container.getESubpackages().add(alt);
      else
        difference_.getContents().add(alt);
    }
    else {
      diff = EcoreFactory.eINSTANCE.createEPackage();

      if (container != null)
        container.getESubpackages().add(diff);
      else
        difference_.getContents().add(diff);
    }

    return diff;
  }


  private EParameter addElement(EOperation container, EParameter contained) {
    EParameter diff;
    String id = getId(contained);

    if (add_ids_.contains(id)) {
      diff = new AddedEParameter();

      // Place new element in proper container.
      container.getEParameters().add(diff);
    }
    else if (del_ids_.contains(id)) {
      diff = new DeletedEParameter();

      id = difference_.getID(container);
      container = (difference_.getEObject(id + '_') != null)
          ? (EOperation) difference_.getEObject(id + '_')
          : (EOperation) difference_.getEObject(id);
      container.getEParameters().add(diff);
    }
    else if (alt_ids_.contains(id)) {
      // Create new object to represent updated element in difference model.
      diff = EcoreFactory.eINSTANCE.createEParameter();

      // Place updated element in proper container.
      container.getEParameters().add(diff);

      // Create new object to represent changed element in difference model.
      AlteredEParameter alt = new AlteredEParameter();  // TODO:3 Change to use copy constructor.
      alt.setName(contained.getName());
      alt.setUpdated(diff);  // Link changed element to updated element.

      id = getId(contained) + '_';
      difference_.setID(alt, id);
      diff_model_.addElement(id, alt);

      EcoreExtra extra =
          new EcoreExtra(alt, id, EcoreType.PARAMETER, MiradorType.ALTER);
      diff_model_.addExtra(alt, extra);

      // Place changed element in either changed container, or the model root.
      id = difference_.getID(container);
      container = (EOperation) difference_.getEObject(id + '_');

      if (container != null)
        container.getEParameters().add(alt);
      else
        difference_.getContents().add(alt);
    }
    else {
      diff = EcoreFactory.eINSTANCE.createEParameter();

      if (container != null)
        container.getEParameters().add(diff);
      else
        difference_.getContents().add(diff);
    }

    return diff;
  }


  private EReference addElement(EClass container, EReference contained) {
    EReference diff;
    String id = getId(contained);

    if (add_ids_.contains(id)) {
      diff = new AddedEReference();

      // Place new element in proper container.
      container.getEStructuralFeatures().add(diff);
    }
    else if (del_ids_.contains(id)) {
      diff = new DeletedEReference();

      id = difference_.getID(container);
      container = (difference_.getEObject(id + '_') != null)
          ? (EClass) difference_.getEObject(id + '_')
          : (EClass) difference_.getEObject(id);
      container.getEStructuralFeatures().add(diff);
    }
    else if (alt_ids_.contains(id)) {
      // Create new object to represent updated element in difference model.
      diff = EcoreFactory.eINSTANCE.createEReference();

      // Place updated element in proper container.
      container.getEStructuralFeatures().add(diff);

      // Create new object to represent changed element in difference model.
      AlteredEReference alt = new AlteredEReference();  // TODO:3 Change to use copy constructor.
      alt.setName(contained.getName());
      alt.setUpdated(diff);  // Link changed element to updated element.

      id = getId(contained) + '_';
      difference_.setID(alt, id);
      diff_model_.addElement(id, alt);

      EcoreExtra extra =
          new EcoreExtra(alt, id, EcoreType.REFERENCE, MiradorType.ALTER);
      diff_model_.addExtra(alt, extra);

      // Place changed element in either changed container, or the model root.
      id = difference_.getID(container);
      container = (EClass) difference_.getEObject(id + '_');

      if (container != null)
        container.getEStructuralFeatures().add(alt);
      else
        difference_.getContents().add(alt);
    }
    else {
      diff = EcoreFactory.eINSTANCE.createEReference();

      if (container != null)
        container.getEStructuralFeatures().add(diff);
      else
        difference_.getContents().add(diff);
    }

    return diff;
  }


  // ID lists are constructed from the top of the model down.
  // A Deleted element must be held in a Changed container, and all of its
  // contents must be Deleted elements as well.
  private void categorizeElements()  {
    // Compare replica model to original in order to find added elements.
    for (TreeIterator<EObject> it = replica_.getAllContents(); it.hasNext();) {
      EObject rep = it.next();
      String id = replica_.getID(rep);  // Ecore element identifier.

      if (id == null)
        continue;  // e.g., GenericTypeImpl

      if (original_.getEObject(id) == null)
        add_ids_.add(id);  // Element was added to the replica.
    }


    // Compare original model to replica in order to find deleted elements.
    for (TreeIterator<EObject> it = original_.getAllContents(); it.hasNext();) {
      EObject org = it.next();
      String id = original_.getID(org);  // Ecore element identifier.

      if (id == null)
        continue;  // e.g., GenericTypeImpl

      // Partition elements into deleted, and changed groups.
      if (replica_.getEObject(id) == null)
        del_ids_.add(id);  // Element was removed from the replica.
      else
        alt_ids_.add(id);  // Otherwise, assume modified in the replica.
    }


    // Verify assumption of model element modifications.
    List<String> remove_ids = new ArrayList<String>();
    EcoreComparator comparer = new EcoreComparator();

    // Examine changed elements for changes from the bottom of the model up.
    for (ListIterator<String> it = alt_ids_.listIterator(alt_ids_.size())
        ; it.hasPrevious();) {
      String id = it.previous();  // Ecore element identifier.

      EObject org = original_.getEObject(id);
      EObject rep = replica_.getEObject(id);

      // Check elements for equality, and if they contain any deleted elements.
      if (comparer.equals(org, rep)) { // Shallowly equal. Go deeper.
        // Verify that the elements still reside in the same container.
        if (getId(org.eContainer()) != getId(rep.eContainer()))
          break;  // Replica model element has been moved. Leave as changed.

        // See if any direct contents of original elements have been deleted.
//        for (Iterator<EObject> in_it = org.eContents().iterator();
//            in_it.hasNext();) {
//          // Check if direct content are found in the deleted element list.
//          if (del_ids_.contains(original_.getID(in_it.next()))) {
//            id = null;  // Contained element deleted. Keep container as changed.
//            break;
//          }
//        }

        if (id != null)
          remove_ids.add(id);  // Element hasn't changed. Remove from list.
      }
    }

    // Do the unchanged element removals.
    for (String id : remove_ids)
      alt_ids_.remove(id);
  }


  // Do complete duplication after difference is built!
  private ENamedElement differenceElement(EObject container, EObject contained,
      boolean is_recurse) {
    ENamedElement diff = null;
    EcoreType etyp = EcoreTyper.typeEObject(contained, false);

    switch (etyp) {
      case ATTRIBUTE:
        diff = addElement((EClass) container, (EAttribute) contained);
     break;

      case CLASS:
        diff = addElement((EPackage) container, (EClass) contained);
      break;

      case DATATYPE:
        diff = addElement((EPackage) container, (EDataType) contained);
      break;

      case OPERATION:
        diff = addElement((EClass) container, (EOperation) contained);
      break;

      case PACKAGE:
        diff = addElement((EPackage) container, (EPackage) contained);
      break;

      case PARAMETER:
        diff = addElement((EOperation) container, (EParameter) contained);
      break;

      case REFERENCE:
        diff = addElement((EClass) container, (EReference) contained);
      break;
    }


    // Put difference element into model, and
    if (diff != null) {
      diff.setName(((ENamedElement) contained).getName());

      String id = getId(contained);
      difference_.setID(diff, id);
      diff_model_.addElement(id, diff);

      MiradorType mtyp = MiradorTyper.typeEObject(diff);
      EcoreExtra extra = new EcoreExtra(diff, id, etyp, mtyp);
      diff_model_.addExtra(diff, extra);

      // Recursively traverse container (breadth first), copying direct content.
      if (is_recurse) { // Contained becomes new container. Work its content.
        for (EObject sub : contained.eContents()) { // Copy over direct content
          differenceElement(diff, sub, true);       //   of contained element.
        }
      }
    }

    return diff;
  }


  public MiradorModel differenceElements(EPackage proj_pkg) {
    //
    categorizeElements();

//    EPackage proj = (EPackage) EcoreUtil.copy(model.getProject());
//    EPackage proj_pkg = EcoreFactory.eINSTANCE.createEPackage();
//    proj_pkg.setName(name);
    difference_.getContents().add(proj_pkg);
//??    difference_.setID(proj_pkg, name + "_id");

    // Traverse replica model top down, copying elements into difference model.
    differenceElement(proj_pkg, replica_.getContents().get(0), true);
//    differenceElement(null, replica_.getContents().get(0), true);

    // Recursively traverse deleted elements from top down.
    for (String id : del_ids_) {
      // Get deleted element, and its container.
      EObject org = original_.getEObject(id);  // Original model element.
      id = getId(org.eContainer());
      EObject diff = difference_.getEObject(id);  // Difference model element.

      // Copy original element's direct content into new difference element.
      differenceElement(diff, org, false);
    }

    //
    finalizeElements();

    if (Debug.dbg.isDebug()) {
      MiradorModel mdiff = new MiradorModel(difference_);
      String path = replica_.getContents().get(0).eResource().getURI().path();
      path = path.replaceFirst("\\(mirador", "\\(mirador-diff");
      mdiff.saveXmiModel(path);
    }

    return diff_model_;
  }


  private void finalizeElements() {
    // Finalize added and deleted elements from top of difference model down.
    for (TreeIterator<EObject> it = difference_.getAllContents(); it.hasNext();) {
      EObject tar = it.next();  // Incomplete difference model element.
      String id = difference_.getID(tar);

      if (id == null)
        continue;  // e.g., Project, GenericTypeImpl, or Altered model element.

      EObject src;
      if (id.endsWith("_"))
        src = original_.getEObject(id.substring(0, id.length() - 1));
      else {
        src = replica_.getEObject(id);  // Added model element.
        if (src == null)
          src = original_.getEObject(id);  // Deleted model element.
      }

      EcoreType typ = EcoreTyper.typeEObject(src, false);
      switch (typ) {
        case ATTRIBUTE:
          finalizeElement((EAttribute) tar, (EAttribute) src);
        break;

        case CLASS:
          finalizeElement((EClass) tar, (EClass) src);
        break;

        case DATATYPE:
          finalizeElement((EDataType) tar, (EDataType) src);
        break;

        case OPERATION:
          finalizeElement((EOperation) tar, (EOperation) src);
        break;

        case PACKAGE:
          finalizeElement((EPackage) tar, (EPackage) src);
        break;

        case PARAMETER:
          finalizeElement((EParameter) tar, (EParameter) src);
        break;

        case REFERENCE:
          finalizeElement((EReference) tar, (EReference) src);
        break;
      }
    }
  }


  private void finalizeElement(EAttribute tar, EAttribute src) {
    AlteredEAttribute.snap(tar, src);  // TODO:3 Change to use copy constructor.
    String id = getId(src.getEType());

    // Set to proper type, in correct model.
    if (id != null) {
      EClassifier typ = (EClassifier) difference_.getEObject(id);

      if (typ == null)
        typ = (EClassifier) difference_.getEObject(id + '_');

      if (typ != null)
        tar.setEType(typ);
    }
  }


  private void finalizeElement(EClass tar, EClass src) {
    AlteredEClass.snap(tar, src);  // TODO:3 Change to use copy constructor.

    for (Iterator<EClass> it = src.getESuperTypes().iterator(); it.hasNext();) {
      EClass sup = it.next();
      String id = getId(sup);

      // Set to proper superclass, in correct model.
      if (id != null) {
        EClass typ = (EClass) difference_.getEObject(id);

        if (typ == null)
          typ = (EClass) difference_.getEObject(id + '_');

        if (typ != null)
          tar.getESuperTypes().add(typ);
      }
    }
  }


  private void finalizeElement(EDataType tar, EDataType src) {
    AlteredEDataType.snap(tar, src);  // TODO:3 Change to use copy constructor.
  }


  private void finalizeElement(EOperation tar, EOperation src) {
    AlteredEOperation.snap(tar, src);  // TODO:3 Change to use copy constructor.
    String id = getId(src.getEType());

    // Set to proper type, in correct model.
    if (id != null) {
      EClassifier typ = (EClassifier) difference_.getEObject(id);

      if (typ == null)
        typ = (EClassifier) difference_.getEObject(id + '_');

      if (typ != null)
        tar.setEType(typ);
    }
  }


  private void finalizeElement(EPackage tar, EPackage src) {
    AlteredEPackage.snap(tar, src);  // TODO:3 Change to use copy constructor.
  }


  private void finalizeElement(EParameter tar, EParameter src) {
    AlteredEParameter.snap(tar, src);  // TODO:3 Change to use copy constructor.
    String id = getId(src.getEType());

    // Set to proper type, in correct model.
    if (id != null) {
      EClassifier typ = (EClassifier) difference_.getEObject(id);

      if (typ == null)
        typ = (EClassifier) difference_.getEObject(id + '_');

      if (typ != null)
        tar.setEType(typ);
    }
  }


  private void finalizeElement(EReference tar, EReference src) {
    AlteredEReference.snap(tar, src);  // TODO:3 Change to use copy constructor.
    String id = getId(src.getEType());

    // Set to proper type, in correct model.
    if (id != null) {
      EClassifier typ = (EClassifier) difference_.getEObject(id);

      if (typ == null)
        typ = (EClassifier) difference_.getEObject(id + '_');

      if (typ != null)
        tar.setEType(typ);
    }


    id = getId(src.getEOpposite());

    // Set opposite
    if (id != null) {
      EReference ref = (EReference) difference_.getEObject(id);

      if (ref == null)
        ref = (EReference) difference_.getEObject(id + '_');

      if (ref != null)
        tar.setEOpposite(ref);
    }
  }


  private String getId(EObject obj) {
    String id = replica_.getID(obj);
    if (id == null)  // If object was deleted...
      id = original_.getID(obj);

    return id;
  }


  // Instance data ----------------------------------------------------------
  private MiradorModel diff_model_;
  private XMIResource difference_;
  private XMIResource original_;
  private XMIResource replica_;

  private List<String> add_ids_ = new ArrayList<String>();
  private List<String> del_ids_ = new ArrayList<String>();
  private List<String> alt_ids_ = new ArrayList<String>();
  // End instance data ------------------------------------------------------
}

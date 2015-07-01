/* --------------------------------------------------------------------------+
   MiradorModel.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.model;
import ca.dsrg.mirador.difference.EcoreComparator;
import ca.dsrg.mirador.difference.EcoreTyper;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.match.MeasureMatrix;
import org.eclipse.emf.common.util.EList;
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
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.25 - Jun 4, 2010
 * @author  Stephen Barrett
 */
public class MiradorModel {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public MiradorModel() {
    this(null);
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  xmodel  Purpose of the argument.
   */
  public MiradorModel(XMIResource xmodel) {
    xmodel_ = (xmodel != null) ? xmodel : new XMIResourceImpl();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public String getId(ENamedElement element) {
    return xmodel_.getID(element);
  }


  public MeasureMatrix getMatchMatrix() {
    return measures_;
  }


  public void setMatchMatrix(MeasureMatrix matches) {
    measures_ = matches;
  }


  public EPackage getProject() {
    return project_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  project_pkg  Purpose of the argument.
   * @category  setter
   */
  public void setProject(EPackage project_pkg) {
    project_ = project_pkg;
  }


//  /**
//   * Gives which of the models being merged the change was executed on.
//   *
//   * @return  Which side of a merge the change record is from.
//   * @category  getter
//   */
//  public MergeSide getMergeSide() { //3 Needed??
//    return merge_side_;
//  }


//  /**
//   * Assigns which of the models being merged the change was executed on.
//   *
//   * @param  merge_side  Which side of a merge the change record is from.
//   * @category  setter
//   */
//  public void setMergeSide(MergeSide merge_side) { //3 Needed??
//    merge_side_ = merge_side;
//  }


  public MeasureMatrix getRankMatrix() {
    return rankings_;
  }


  public void setRankMatrix(MeasureMatrix rankings) {
    rankings_ = rankings;
  }


  public EPackage getRoot() {
    return root_;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @param  root_pkg  Purpose of the argument.
   * @category  setter
   */
  public void setRoot(EPackage root_pkg) {
    root_ = root_pkg;
  }


  public XMIResource getXmiModel() {
    return xmodel_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections
  /**
   * Add an Ecore element to model's collection.
   *
   * @param  to_add  Element to add to collection.
   * @category  collection
   */
  public ENamedElement addElement(String id, ENamedElement to_add) {
    if (to_add != null) {
      if (id != null && !elements_.containsKey(id)) {
        return elements_.put(id, to_add);
      }
    }
    return null;
  }


  public ENamedElement removeElement(String id) {
    if (id != null && elements_.containsKey(id)) {
      return elements_.remove(id);
    }
    return null;
  }


  public boolean removeElement(ENamedElement element) {
    if (elements_.containsValue(element)) {
      return elements_.values().remove(element);
    }
    return false;
  }


  public ENamedElement getElement(String id) {
    return elements_.get(id);
  }


  public Iterator<ENamedElement> elementIterator() {
    return elements_.values().iterator();
  }


  public EcoreExtra addExtra(ENamedElement element, EcoreExtra to_add) {
    if (to_add != null) {
      if (element != null && !extras_.containsKey(element)) {
        to_add.setModel(this);
        return extras_.put(element, to_add);
      }
    }
    return null;
  }


  public EcoreExtra removeExtra(ENamedElement element) {
    if (element != null && extras_.containsKey(element)) {
      return extras_.remove(element);
    }
    return null;
  }


  public EcoreExtra getExtra(ENamedElement element) {
    return extras_.get(element);
  }


  public EcoreExtra getExtra(String id) {
    ENamedElement elem = elements_.get(id);
    return (elem != null) ? extras_.get(elem) : null;
  }


  public Iterator<EcoreExtra> extraIterator() {
    return extras_.values().iterator();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public boolean addToModel(ENamedElement diff, EcoreExtra added_ex) {
    boolean rc = false;
    EcoreType typ = EcoreTyper.typeEObject(diff);
    EPackage pkg;
    EClass cls;
    EOperation oper;
    String id = null;

    switch (typ) {
      case ATTRIBUTE:
        cls = ((EAttribute) diff).getEContainingClass();

        if (cls != null) {
          id = (getId(cls) != null)
              ? getId(cls) : added_ex.getModel().getId(cls);

          if (id != null) {
            cls = (EClass) getElement(id);

            if (cls != null) {
              EList<EStructuralFeature> feats = cls.getEStructuralFeatures();

              if (!feats.contains(diff))
                rc = feats.add((EAttribute) diff);
              else
                rc = true;
            }
          }
        }
      break;

      case CLASS:
        pkg = ((EClass) diff).getEPackage();

        if (pkg != null) {
          id = (getId(pkg) != null)
              ? getId(pkg) : added_ex.getModel().getId(pkg);

          if (id != null) {
            pkg = (EPackage) getElement(id);

            if (pkg != null) {
              EList<EClassifier> classes = pkg.getEClassifiers();

              if (!classes.contains(diff))
                rc = classes.add((EClassifier) diff);
              else
                rc = true;
            }
          }
        }
      break;

      case DATATYPE:
        rc = true;  //??
      break;

      case OPERATION:
        cls = ((EOperation) diff).getEContainingClass();

        if (cls != null) {
          id = (getId(cls) != null)
              ? getId(cls) : added_ex.getModel().getId(cls);

          if (id != null) {
            cls = (EClass) getElement(id);

            if (cls != null) {
              EList<EOperation> opers = cls.getEOperations();

              if (!opers.contains(diff))
                rc = opers.add((EOperation) diff);
              else
                rc = true;
            }
          }
        }
      break;

      case PACKAGE:
        pkg = ((EPackage) diff).getESuperPackage();

        if (pkg != null) {
          id = (getId(pkg) != null)
              ? getId(pkg) : added_ex.getModel().getId(pkg);

          if (id != null) {
            pkg = (EPackage) getElement(id);

            if (pkg != null) {
              EList<EPackage> pkgs = pkg.getESubpackages();

              if (!pkgs.contains(diff))
                rc = pkgs.add((EPackage) diff);
              else
                rc = true;
            }
          }
        }
      break;

      case PARAMETER:
        oper = ((EParameter) diff).getEOperation();

        if (oper != null) {
          id = (getId(oper) != null)
              ? getId(oper) : added_ex.getModel().getId(oper);

          if (id != null) {
            oper = (EOperation) getElement(id);

            if (oper != null) {
              EList<EParameter> params = oper.getEParameters();

              if (!params.contains(diff))
                rc = params.add((EParameter) diff);
              else
                rc = true;
            }
          }
        }
      break;

      case REFERENCE:
        cls = ((EReference) diff).getEContainingClass();

        if (cls != null) {
          id = (getId(cls) != null)
              ? getId(cls) : added_ex.getModel().getId(cls);

          if (id != null) {
            cls = (EClass) getElement(id);

            if (cls != null) {
              EList<EStructuralFeature> feats = cls.getEStructuralFeatures();

              if (!feats.contains(diff))
                rc = feats.add((EReference) diff);
              else
                rc = true;
            }
          }
        }
      break;
    }

    if (rc) {
      addElement(added_ex.getId(), diff);
      addExtra(diff, added_ex);
      xmodel_.setID(diff, added_ex.getId());
    }

    return rc;
  }


  public boolean alterInModel(ENamedElement diff, EcoreExtra altered_ex,
      ENamedElement updated) {
    boolean rc = false;  // If AlteredEElement, do special alter merge??
    EcoreComparator comparer = new EcoreComparator();
    EcoreType typ = EcoreTyper.typeEObject(diff);

    switch (typ) {
      case ATTRIBUTE:
        rc = comparer.update(updated, altered_ex.getElement());
      break;

      case CLASS:
        rc = comparer.update(updated, altered_ex.getElement());
      break;

      case DATATYPE:
        rc = comparer.update(updated, altered_ex.getElement());
      break;

      case OPERATION:
        rc = comparer.update(updated, altered_ex.getElement());
      break;

      case PACKAGE:
        rc = comparer.update(updated, altered_ex.getElement());
      break;

      case PARAMETER:
        rc = comparer.update(updated, altered_ex.getElement());
      break;

      case REFERENCE:
        rc = comparer.update(updated, altered_ex.getElement());
      break;
    }

    if (rc)
      altered_ex.setClassName(diff.getClass().getSimpleName());

    return rc;
  }


  public boolean deleteFromModel(ENamedElement diff, ENamedElement deleted) {
    boolean rc = false;
    EcoreType typ = EcoreTyper.typeEObject(diff);
    EPackage pkg;
    EClass cls;
    EOperation oper;

    switch (typ) {
      case ATTRIBUTE:
        cls = ((EAttribute) deleted).getEContainingClass();

        if (cls != null) {
          EList<EStructuralFeature> attrs = cls.getEStructuralFeatures();
          rc = attrs.remove(deleted);
        }
      break;

      case CLASS:
        pkg = ((EClass) deleted).getEPackage();

        if (pkg != null) {
          EList<EClassifier> classes = pkg.getEClassifiers();
          rc = classes.remove(deleted);
        }
      break;

      case DATATYPE:
        pkg = ((EDataType) deleted).getEPackage();

        if (pkg != null) {
          EList<EClassifier> classes = pkg.getEClassifiers();
          rc = classes.remove(deleted);
        }
      break;

      case OPERATION:
        cls = ((EOperation) deleted).getEContainingClass();

        if (cls != null) {
          EList<EOperation> opers = cls.getEOperations();
          rc = opers.remove(deleted);
        }
      break;

      case PACKAGE:
        pkg = ((EPackage) deleted).getESuperPackage();

        if (pkg != null) {
          EList<EPackage> pkgs = pkg.getESubpackages();
          rc = pkgs.remove(deleted);
        }
      break;

      case PARAMETER:
        oper = ((EParameter) deleted).getEOperation();

        if (oper != null) {
          EList<EParameter> params = oper.getEParameters();
          rc = params.remove(deleted);
        }
      break;

      case REFERENCE:
        cls = ((EReference) deleted).getEContainingClass();

        if (cls != null) {
          EList<EStructuralFeature> feats = cls.getEStructuralFeatures();
          rc = feats.remove(deleted);
        }
      break;
    }

    if (rc) {
      removeElement(deleted);
      removeExtra(deleted);
    }

    return rc;
  }


  public XMIResource saveXmiModel(String file_name) {
    if (xmodel_ == null)
      return null;

    String path = new File(file_name).getAbsolutePath();
    xmodel_.setURI(org.eclipse.emf.common.util.URI.createFileURI(path));

    try {
      xmodel_.save(null);
    }
    catch (IOException ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      ex.printStackTrace();
    }

    return xmodel_;
  }


  // Instance data ----------------------------------------------------------
  /** Serializable Ecore model as a tree. */
  private XMIResource xmodel_;

  /** Artificial topmost element of the Ecore model. */
  private EPackage project_;

  /** Root element of the Ecore model. Contained by the project package. */
  private EPackage root_;

  private MeasureMatrix measures_;
  private MeasureMatrix rankings_;

  /** Side of merge model is from. */
//  protected MergeSide merge_side_;  //??3 Still useful?

  private Map<String, ENamedElement> elements_ =
      new LinkedHashMap<String, ENamedElement>();

  private Map<ENamedElement, EcoreExtra> extras_ =
      new LinkedHashMap<ENamedElement, EcoreExtra>();
  // End instance data ------------------------------------------------------
}

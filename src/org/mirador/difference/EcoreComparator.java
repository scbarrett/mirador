/* --------------------------------------------------------------------------+
   EcoreComparator.java - High-level description of module and place in system.
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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import java.util.ArrayList;
import java.util.List;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.53 - Nov 2, 2010
 * @author  Stephen Barrett
 */
public class EcoreComparator extends EcoreUtil.EqualityHelper {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  public List<EStructuralFeature> differs(EObject obj1, EObject obj2) {
    work(obj1, obj2, false);
    return feats_;
  }


  @Override public boolean equals(EObject obj1, EObject obj2) {
    return work(obj1, obj2, false);
  }


  public boolean update(EObject obj1, EObject obj2) {
    return work(obj1, obj2, true);
  }


  private boolean work(EObject obj1, EObject obj2,
      boolean do_update) {
    if (obj1 == null)
      return obj2 == null;

    if (obj2 == null)
      return false;

    Object eObject1MappedValue = get(obj1);
    if (eObject1MappedValue != null) // If obj1 has been compared already...
      return eObject1MappedValue == obj2;

    Object eObject2MappedValue = get(obj2);
    if (eObject2MappedValue != null) // If obj2 has been compared already...
      return eObject2MappedValue == obj1;

    if (obj1 == obj2) { // Same instance, match them and return true.
      put(obj1, obj2);
      put(obj2, obj1);
      return true;
    }

    EClass cls = obj1.eClass();
    if (cls != obj2.eClass())
      return false;

    // Assume match.
    put(obj1, obj2);
    put(obj2, obj1);

    boolean rc = false;

    if (cls.getInstanceClass() == EAttribute.class)
      rc = work((EAttribute) obj1, (EAttribute) obj2, do_update);
    else if (cls.getInstanceClass() == EClass.class)
      rc = work((EClass) obj1, (EClass) obj2, do_update);
    else if (cls.getInstanceClass() == EDataType.class)
      rc = work((EDataType) obj1, (EDataType) obj2, do_update);
    else if (cls.getInstanceClass() == EOperation.class)
      rc = work((EOperation) obj1, (EOperation) obj2, do_update);
    else if (cls.getInstanceClass() == EParameter.class)
      rc = work((EParameter) obj1, (EParameter) obj2, do_update);
    else if (cls.getInstanceClass() == EPackage.class)
      rc = work((EPackage) obj1, (EPackage) obj2, do_update);
    else if (cls.getInstanceClass() == EReference.class)
      rc = work((EReference) obj1, (EReference) obj2, do_update);

    if (!rc) {
      remove(obj1);
      remove(obj2);
    }

    return rc;
  }


  private boolean work(EAttribute obj1, EAttribute obj2, boolean do_update) {
    List<EStructuralFeature> feats = new ArrayList<EStructuralFeature>();
    EClass cls = obj1.eClass();

    for (int i = 0, size = cls.getFeatureCount(); i < size; ++i) {
      EStructuralFeature feature = cls.getEStructuralFeature(i);
      int id = feature.getFeatureID();

      if (feature.isDerived()
//        || id == EcorePackage.EATTRIBUTE__CHANGEABLE
//        || id == EcorePackage.EATTRIBUTE__DEFAULT_VALUE_LITERAL
//        || id == EcorePackage.EATTRIBUTE__DERIVED
          || id == EcorePackage.EATTRIBUTE__EANNOTATIONS
          || id == EcorePackage.EATTRIBUTE__ECONTAINING_CLASS
          || id == EcorePackage.EATTRIBUTE__EGENERIC_TYPE
//        || id == EcorePackage.EATTRIBUTE__ETYPE
          || id == EcorePackage.EATTRIBUTE__ID
//        || id == EcorePackage.EATTRIBUTE__LOWER_BOUND
//        || id == EcorePackage.EATTRIBUTE__NAME
//        || id == EcorePackage.EATTRIBUTE__ORDERED
//        || id == EcorePackage.EATTRIBUTE__TRANSIENT
//        || id == EcorePackage.EATTRIBUTE__UNIQUE
//        || id == EcorePackage.EATTRIBUTE__UNSETTABLE
//        || id == EcorePackage.EATTRIBUTE__UPPER_BOUND
//        || id == EcorePackage.EATTRIBUTE__VOLATILE
         )
        continue;

      // FIXME:2 Need three objects: targeted, updated, and original! Otherwise feature is just changed back in case of both execution.
      if (!haveEqualFeature(obj1, obj2, feature))
        if (do_update)
          obj2.eSet(feature, obj1.eGet(feature));
        else
          feats.add(feature);
    }

    feats_ = feats;
    return feats_.isEmpty();
  }


  private boolean work(EClass obj1, EClass obj2, boolean do_update) {
    List<EStructuralFeature> feats = new ArrayList<EStructuralFeature>();
    EClass cls = obj1.eClass();

    for (int i = 0, size = cls.getFeatureCount(); i < size; ++i) {
      EStructuralFeature feature = cls.getEStructuralFeature(i);
      int id = feature.getFeatureID();

      if (feature.isDerived()
//        || id == EcorePackage.ECLASS__ABSTRACT
          || id == EcorePackage.ECLASS__EANNOTATIONS
          || id == EcorePackage.ECLASS__EGENERIC_SUPER_TYPES
          || id == EcorePackage.ECLASS__EOPERATIONS
          || id == EcorePackage.ECLASS__EPACKAGE
          || id == EcorePackage.ECLASS__ESTRUCTURAL_FEATURES
//        || id == EcorePackage.ECLASS__ESUPER_TYPES
          || id == EcorePackage.ECLASS__ETYPE_PARAMETERS
//        || id == EcorePackage.ECLASS__INSTANCE_CLASS_NAME
//        || id == EcorePackage.ECLASS__INSTANCE_TYPE_NAME
//        || id == EcorePackage.ECLASS__INTERFACE
//        || id == EcorePackage.ECLASS__NAME
         )
        continue;

      if (!haveEqualFeature(obj1, obj2, feature))
        if (do_update)
          obj2.eSet(feature, obj1.eGet(feature));
        else
          feats.add(feature);
    }

    feats_ = feats;
    return feats_.isEmpty();
  }


  private boolean work(EDataType obj1, EDataType obj2, boolean do_update) {
    List<EStructuralFeature> feats = new ArrayList<EStructuralFeature>();
    EClass cls = obj1.eClass();

    for (int i = 0, size = cls.getFeatureCount(); i < size; ++i) {
      EStructuralFeature feature = cls.getEStructuralFeature(i);
      int id = feature.getFeatureID();

      if (feature.isDerived()
          || id == EcorePackage.EDATA_TYPE__EANNOTATIONS
          || id == EcorePackage.EDATA_TYPE__EPACKAGE
          || id == EcorePackage.EDATA_TYPE__ETYPE_PARAMETERS
//        || id == EcorePackage.EDATA_TYPE__INSTANCE_CLASS_NAME
//        || id == EcorePackage.EDATA_TYPE__INSTANCE_TYPE_NAME
//        || id == EcorePackage.EDATA_TYPE__NAME
//        || id == EcorePackage.EDATA_TYPE__SERIALIZABLE
         )
        continue;

      if (!haveEqualFeature(obj1, obj2, feature))
        if (do_update)
          obj2.eSet(feature, obj1.eGet(feature));
        else
          feats.add(feature);
    }

    feats_ = feats;
    return feats_.isEmpty();
  }


  private boolean work(EOperation obj1, EOperation obj2, boolean do_update) {
    List<EStructuralFeature> feats = new ArrayList<EStructuralFeature>();
    EClass cls = obj1.eClass();

    for (int i = 0, size = cls.getFeatureCount(); i < size; ++i) {
      EStructuralFeature feature = cls.getEStructuralFeature(i);
      int id = feature.getFeatureID();

      if (feature.isDerived()
          || id == EcorePackage.EOPERATION__EANNOTATIONS
          || id == EcorePackage.EOPERATION__ECONTAINING_CLASS
          || id == EcorePackage.EOPERATION__EEXCEPTIONS
          || id == EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS
          || id == EcorePackage.EOPERATION__EGENERIC_TYPE
          || id == EcorePackage.EOPERATION__EPARAMETERS
//        || id == EcorePackage.EOPERATION__ETYPE
          || id == EcorePackage.EOPERATION__ETYPE_PARAMETERS
//        || id == EcorePackage.EOPERATION__LOWER_BOUND
//        || id == EcorePackage.EOPERATION__NAME
//        || id == EcorePackage.EOPERATION__ORDERED
//        || id == EcorePackage.EOPERATION__UNIQUE
//        || id == EcorePackage.EOPERATION__UPPER_BOUND
         )
        continue;

      if (!haveEqualFeature(obj1, obj2, feature))
        if (do_update)
          obj2.eSet(feature, obj1.eGet(feature));
        else
          feats.add(feature);
    }

    feats_ = feats;
    return feats_.isEmpty();
  }


  private boolean work(EPackage obj1, EPackage obj2, boolean do_update) {
    List<EStructuralFeature> feats = new ArrayList<EStructuralFeature>();
    EClass cls = obj1.eClass();

    for (int i = 0, size = cls.getFeatureCount(); i < size; ++i) {
      EStructuralFeature feature = cls.getEStructuralFeature(i);
      int id = feature.getFeatureID();

      if (feature.isDerived()
          || id == EcorePackage.EPACKAGE__EANNOTATIONS
          || id == EcorePackage.EPACKAGE__ECLASSIFIERS
          || id == EcorePackage.EPACKAGE__EFACTORY_INSTANCE
          || id == EcorePackage.EPACKAGE__ESUBPACKAGES
          || id == EcorePackage.EPACKAGE__ESUPER_PACKAGE
//        || id == EcorePackage.EPACKAGE__NAME
//        || id == EcorePackage.EPACKAGE__NS_PREFIX
//        || id == EcorePackage.EPACKAGE__NS_URI
         )
        continue;

      if (!haveEqualFeature(obj1, obj2, feature))
        if (do_update)
          obj2.eSet(feature, obj1.eGet(feature));
        else
          feats.add(feature);
    }

    feats_ = feats;
    return feats_.isEmpty();
  }


  private boolean work(EParameter obj1, EParameter obj2, boolean do_update) {
    List<EStructuralFeature> feats = new ArrayList<EStructuralFeature>();
    EClass cls = obj1.eClass();

    for (int i = 0, size = cls.getFeatureCount(); i < size; ++i) {
      EStructuralFeature feature = cls.getEStructuralFeature(i);
      int id = feature.getFeatureID();

      if (feature.isDerived()
          || id == EcorePackage.EPARAMETER__EANNOTATIONS
          || id == EcorePackage.EPARAMETER__EGENERIC_TYPE
          || id == EcorePackage.EPARAMETER__EOPERATION
//        || id == EcorePackage.EPARAMETER__ETYPE
//        || id == EcorePackage.EPARAMETER__LOWER_BOUND
//        || id == EcorePackage.EPARAMETER__NAME
//        || id == EcorePackage.EPARAMETER__ORDERED
//        || id == EcorePackage.EPARAMETER__UNIQUE
//        || id == EcorePackage.EPARAMETER__UPPER_BOUND
         )
        continue;

      if (!haveEqualFeature(obj1, obj2, feature)) {
        if (id == EcorePackage.EPARAMETER__ETYPE) { // Don't fail match if
          EClassifier typ1 = obj1.getEType();       //   eTypes vary only in
          EClassifier typ2 = obj2.getEType();       //   their eSuperTypes.

          if ( typ1.eClass() == typ2.eClass())
            continue;
        }

        if (do_update)
          obj2.eSet(feature, obj1.eGet(feature));
        else
          feats.add(feature);
      }
    }

    feats_ = feats;
    return feats_.isEmpty();
  }


  private boolean work(EReference obj1, EReference obj2, boolean do_update) {
    List<EStructuralFeature> feats = new ArrayList<EStructuralFeature>();
    EClass cls = obj1.eClass();

    for (int i = 0, size = cls.getFeatureCount(); i < size; ++i) {
      EStructuralFeature feature = cls.getEStructuralFeature(i);
      int id = feature.getFeatureID();

      if (feature.isDerived()
//        || id == EcorePackage.EREFERENCE__CHANGEABLE
          || id == EcorePackage.EREFERENCE__CONTAINER
          || id == EcorePackage.EREFERENCE__CONTAINMENT
//        || id == EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL
//        || id == EcorePackage.EREFERENCE__DERIVED
          || id == EcorePackage.EREFERENCE__EANNOTATIONS
          || id == EcorePackage.EREFERENCE__ECONTAINING_CLASS
          || id == EcorePackage.EREFERENCE__EGENERIC_TYPE
          || id == EcorePackage.EREFERENCE__EKEYS
//        || id == EcorePackage.EREFERENCE__EOPPOSITE
//        || id == EcorePackage.EREFERENCE__ETYPE
//        || id == EcorePackage.EREFERENCE__LOWER_BOUND
//        || id == EcorePackage.EREFERENCE__NAME
//        || id == EcorePackage.EREFERENCE__ORDERED
//        || id == EcorePackage.EREFERENCE__RESOLVE_PROXIES
//        || id == EcorePackage.EREFERENCE__TRANSIENT
//        || id == EcorePackage.EREFERENCE__UNIQUE
//        || id == EcorePackage.EREFERENCE__UNSETTABLE
//        || id == EcorePackage.EREFERENCE__UPPER_BOUND
//        || id == EcorePackage.EREFERENCE__VOLATILE
         )
        continue;

      if (!haveEqualFeature(obj1, obj2, feature)) {
        if (id == EcorePackage.EPARAMETER__ETYPE) { // Don't fail match if
          EClassifier typ1 = obj1.getEType();       //   eTypes vary only in
          EClassifier typ2 = obj2.getEType();       //   their eSuperTypes.

          if ( typ1.eClass() == typ2.eClass())
            continue;
        }

        if (do_update)
          obj2.eSet(feature, obj1.eGet(feature));
        else
          feats.add(feature);
      }
    }

    feats_ = feats;
    return feats_.isEmpty();
  }


  // Instance data ----------------------------------------------------------
  private List<EStructuralFeature> feats_ = new ArrayList<EStructuralFeature>();
  // End instance data ------------------------------------------------------
}

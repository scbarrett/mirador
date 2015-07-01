/* --------------------------------------------------------------------------+
   EcoreTyper.java - High-level description of module and place in system.
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EOperationImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EParameterImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.54 - Nov 3, 2010
 * @author  Stephen Barrett
 */
public class EcoreTyper {
  static public EcoreType typeEObject(EObject obj) {
    return typeEObject(obj, true);
  }


  static public EcoreType typeEObject(EObject obj, boolean is_full) {
    EcoreType rc = EcoreType.NONE;
    Class<?> obj_typ = obj.getClass();

    if (is_full) {
      if (obj_typ == EAttributeImpl.class
          || obj_typ == AddedEAttribute.class
          || obj_typ == DeletedEAttribute.class
          || obj_typ == AlteredEAttribute.class)
        rc = EcoreType.ATTRIBUTE;
      else if (obj_typ == EClassImpl.class
          || obj_typ == AddedEClass.class
          || obj_typ == DeletedEClass.class
          || obj_typ == AlteredEClass.class)
        rc = EcoreType.CLASS;
      else if (obj_typ == EDataTypeImpl.class
          || obj_typ == AddedEDataType.class
          || obj_typ == DeletedEDataType.class
          || obj_typ == AlteredEDataType.class)
        rc = EcoreType.DATATYPE;
      else if (obj_typ == EOperationImpl.class
          || obj_typ == AddedEOperation.class
          || obj_typ == DeletedEOperation.class
          || obj_typ == AlteredEOperation.class)
        rc = EcoreType.OPERATION;
      else if (obj_typ == EPackageImpl.class
          || obj_typ == AddedEPackage.class
          || obj_typ == DeletedEPackage.class
          || obj_typ == AlteredEPackage.class)
        rc = EcoreType.PACKAGE;
      else if (obj_typ == EParameterImpl.class
          || obj_typ == AddedEParameter.class
          || obj_typ == DeletedEParameter.class
          || obj_typ == AlteredEParameter.class)
        rc = EcoreType.PARAMETER;
      else if (obj_typ == EReferenceImpl.class
          || obj_typ == AddedEReference.class
          || obj_typ == DeletedEReference.class
          || obj_typ == AlteredEReference.class)
        rc = EcoreType.REFERENCE;
    }
    else {
      if (obj_typ == EAttributeImpl.class
          || obj_typ == AlteredEAttribute.class)
        rc = EcoreType.ATTRIBUTE;
      else if (obj_typ == EClassImpl.class
          || obj_typ == AlteredEClass.class)
        rc = EcoreType.CLASS;
      else if (obj_typ == EDataTypeImpl.class
          || obj_typ == AlteredEDataType.class)
        rc = EcoreType.DATATYPE;
      else if (obj_typ == EOperationImpl.class
          || obj_typ == AlteredEOperation.class)
        rc = EcoreType.OPERATION;
      else if (obj_typ == EPackageImpl.class
          || obj_typ == AlteredEPackage.class)
        rc = EcoreType.PACKAGE;
      else if (obj_typ == EParameterImpl.class
          || obj_typ == AlteredEParameter.class)
        rc = EcoreType.PARAMETER;
      else if (obj_typ == EReferenceImpl.class
          || obj_typ == AlteredEReference.class)
        rc = EcoreType.REFERENCE;
    }

    return rc;
  }


  // Nested types -----------------------------------------------------------
  /**
   * Tag values to indicate the type of element being changed.
   *
   * @since   v0.29 - Jul 20, 2010
   * @author  Stephen Barrett
   */
  static public enum EcoreType { NONE, PACKAGE, CLASS, ATTRIBUTE, OPERATION,
    PARAMETER, REFERENCE, DATATYPE }
}
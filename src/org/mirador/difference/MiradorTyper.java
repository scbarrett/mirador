/* --------------------------------------------------------------------------+
   MiradorTyper.java - High-level description of module and place in system.
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


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.60 - Nov 12, 2010
 * @author  Stephen Barrett
 */
public class MiradorTyper {
  static public MiradorType typeEObject(EObject obj) {
    MiradorType rc = MiradorType.NONE;
    Class<?> obj_typ = obj.getClass();

    if (obj_typ == AddedEAttribute.class
        || obj_typ == AddedEClass.class
        || obj_typ == AddedEDataType.class
        || obj_typ == AddedEOperation.class
        || obj_typ == AddedEPackage.class
        || obj_typ == AddedEParameter.class
        || obj_typ == AddedEReference.class)
      rc = MiradorType.ADD;
    else if (obj_typ == DeletedEAttribute.class
        || obj_typ == DeletedEClass.class
        || obj_typ == DeletedEDataType.class
        || obj_typ == DeletedEOperation.class
        || obj_typ == DeletedEPackage.class
        || obj_typ == DeletedEParameter.class
        || obj_typ == DeletedEReference.class)
      rc = MiradorType.DELETE;
    else if (obj_typ == AlteredEAttribute.class
        || obj_typ == AlteredEClass.class
        || obj_typ == AlteredEDataType.class
        || obj_typ == AlteredEOperation.class
        || obj_typ == AlteredEPackage.class
        || obj_typ == AlteredEParameter.class
        || obj_typ == AlteredEReference.class)
      rc = MiradorType.ALTER;

    return rc;
  }


  // Nested types -----------------------------------------------------------
  /**
   * Tag values to indicate the type of element being changed.
   *
   * @since   v0.60 - Nov 12, 2010
   * @author  Stephen Barrett
   */
  static public enum MiradorType { NONE, ADD, DELETE, ALTER }
}
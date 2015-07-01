/* --------------------------------------------------------------------------+
   EMFModelRepository.java - Concrete deposit point for the salient items of a
     merge session as drawn from EMF base, left, and right models.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   As they contain no change information, change log parts of the repository
   are null or empty for EMF models.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.emf;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.Mirador;
import ca.dsrg.mirador.difference.EcoreTyper;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.model.EcoreExtra;
import ca.dsrg.mirador.model.MiradorModel;
import ca.dsrg.mirador.model.ModelRepository;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import java.io.File;


/**
 * Overall repository for the model change records, and transactional groupings
 * of the models involved in a merge. Components are identified by their "side"
 * in the merge: BASE, LEFT, or RIGHT.
 *
 * @since   v0.45 - Oct 15, 2010
 * @author  Stephen Barrett
 */
public class EMFModelRepository extends ModelRepository {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Creates a snapshot of the change history of the models involved in a merge
   * for later use by Mirador. For EMF models, there are no changes, so the
   * logs are null, and their corresponding record sets empty.
   *
   * A null base model implies a two-way merge.
   *
   * @param  emf_file_bs  XMI expressed Ecore "base" model.
   * @param  emf_file_lf  XMI expressed Ecore "left" model.
   * @param  emf_file_rt  XMI expressed Ecore "right" model.
   */
  public EMFModelRepository(File emf_file_bs, File emf_file_lf,
      File emf_file_rt) {
    super(emf_file_bs, emf_file_lf, emf_file_rt);
    model_type_ = ModelType.EMF;
    initialize(emf_file_bs, emf_file_lf, emf_file_rt);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  What is returned by the method.
   */
//  public EMFChangeRepository getChangeRepository() {
//    return change_repo_;
//  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Reads through the left and right model files gathering the change records
   * into transactions.
   *
   * Note that Fujaba models replicated from the same base model will each
   * hold a common set ancestor records.
   */
  private void initialize(File emf_file_bs, File emf_file_lf, File emf_file_rt) {
    ResourceSet res_set = initEmfResourceSet(emf_file_lf);  // FIXME:2 Make similar to Fujaba load.
    model_bs_ = loadEmfModel(emf_file_bs, res_set);
    model_lf_ = loadEmfModel(emf_file_lf, res_set);
    model_rt_ = loadEmfModel(emf_file_rt, res_set);
    model_mg_ = loadEmfModel(emf_file_bs, res_set);

    if (Debug.dbg.isDebug()) {
      Debug.dbg.println("\n\n\n\t    --- BASE ECORE MODEL ---");
      dumpEcoreModel(model_bs_);

      Debug.dbg.println("\n\n\n\t    --- LEFT ECORE MODEL ---");
      dumpEcoreModel(model_lf_);

      Debug.dbg.println("\n\n\n\t    --- RIGHT ECORE MODEL ---");
      dumpEcoreModel(model_rt_);
    }

    if (Debug.dbg.isDebug() || Mirador.isAutoMode()) {
      String name = emf_file_bs.getName();
      name = name.substring(0, name.lastIndexOf('.'));

      model_bs_.saveXmiModel("usr/" + name + "(mirador-base).ecore");
      model_lf_.saveXmiModel("usr/" + name + "(mirador-left).ecore");
      model_rt_.saveXmiModel("usr/" + name + "(mirador-right).ecore");
   }
 }


  static private ResourceSet initEmfResourceSet(File emf_file) {
    String name = emf_file.getName();
    int pos = name.lastIndexOf('.');
    assert pos >= 0;  // Must have some extension to register factory.
    String ext = name.substring(pos + 1, name.length());

    // Register an XMI resource factory to handle the model file extension.
    ResourceSet res_set = new ResourceSetImpl();
    res_set.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put(ext, new XMIResourceFactoryImpl());

    return res_set;
  }


  /**
   * Loads an EMF model that reflects the changes executed against the model
   * replica -- creating in effect, a new model version.
   *
   * @param  emf_file
   * @param  xmi_set
   * @return  Ecore model reflecting the execution of all change operations.
   */
  static private MiradorModel loadEmfModel(File emf_file, ResourceSet xmi_set) {
    // Load the Ecore model as a resource.
    XMIResource xmodel = (XMIResource)
        xmi_set.getResource(URI.createURI(emf_file.getPath()), true);

    MiradorModel model = null;
    EObject root = xmodel.getContents().get(0);


    if (root != null && root.getClass() == EPackageImpl.class) {
      model = new MiradorModel(xmodel);

      // Set root and project nodes in the Mirador model.
      model.setRoot((EPackage) root);
      EPackage proj = EcoreFactory.eINSTANCE.createEPackage();
      proj.setName("project");
      model.setProject(proj);
      EcoreExtra extra = new EcoreExtra(proj, "project_id", EcoreType.NONE);
      model.addExtra(proj, extra);


      // Ensure every element has an ID.
      TreeIterator<EObject> it = xmodel.getAllContents();
      while (it.hasNext()) {
        EObject obj = it.next();
        String id = xmodel.getID(obj);   //??3 Use Zvezdan's technique? Or require an ID?

        if (!(obj instanceof EGenericType)) { //??3 What of real Generic types?
          if (id == null) {
            // FIXME: Make common base model for EMF without IDs.
            id = EcoreUtil.generateUUID();
            xmodel.setID(obj, id);
          }

          ENamedElement named_obj = (ENamedElement) obj;
          model.addElement(id, named_obj);
          EcoreType etype = EcoreTyper.typeEObject(named_obj, false);
          extra = new EcoreExtra(named_obj, id, etype);
          model.addExtra(named_obj, extra);
        }
      }
    }

    return model;
  }


  // Instance data ----------------------------------------------------------
//  private EMFChangeRepository change_repo_;
  // End instance data ------------------------------------------------------
}

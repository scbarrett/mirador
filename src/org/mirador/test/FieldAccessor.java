/* --------------------------------------------------------------------------+
   FieldAccessor.java - Uses reflection to gain access to class fields
     regardless of protection.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import java.lang.reflect.Field;
import junit.framework.Assert;


/**
 * Provides access to private class fields for test purposes.
 *
 * @since   v0.22 - Apr 5, 2010
 * @author  Stephen Barrett
 */
public class FieldAccessor {
  static public Object getField(Object obj, String fld_name) {
    // Test argument validity.
    Assert.assertNotNull(obj);
    Assert.assertNotNull(fld_name);

    final Field fields[] = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.getName().equals(fld_name)) {
        try {
          field.setAccessible(true);
          return field.get(obj);
        }
        catch (IllegalAccessException ex) {
          Assert.fail ("IllegalAccessException on " + '\"' + fld_name + '\"');
        }
      }
    }

    Assert.fail ("Field \"" + fld_name + "\" not found.");
    return null;
  }

  static public void setField(Object obj, String fld_name, Object arg) {
    // Test argument validity.
    Assert.assertNotNull(obj);
    Assert.assertNotNull(fld_name);

    final Field fields[] = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.getName().equals(fld_name)) {
        try {
          field.setAccessible(true);
          field.set(obj, arg);
          return;
        }
        catch (IllegalAccessException ex) {
          Assert.fail ("IllegalAccessException on " + '\"' + fld_name + '\"');
        }
      }
    }

    Assert.fail ("Field \"" + fld_name + "\" not found.");
  }
}

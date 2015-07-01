/* --------------------------------------------------------------------------+
   MiradorException.java - Provides Mirador with its own runtime exception
     class for throwing in error situations.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;


/**
 * Runtime exception class for Mirador errors.
 *
 * @since   v0.12 - Feb 25, 2010
 * @author  Stephen Barrett
 */
public class MiradorException extends RuntimeException {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public MiradorException() {
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  msg  Purpose of the argument.
   */
  public MiradorException(String msg) {
    super(msg);
  }
}

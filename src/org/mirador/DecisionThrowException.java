/* --------------------------------------------------------------------------+
   DecisionThrowException.java - Directly thrown by the firing of a decision
     table rule.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;



/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.62 - Nov 13, 2010
 * @author  Stephen Barrett
 */
public class DecisionThrowException extends MiradorException {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  table  Purpose of the argument.
   * @param  action  Purpose of the argument.
   * @param  file  Purpose of the argument.
   * @param  index  Purpose of the argument.
   */
  public DecisionThrowException(String table, String action, String file,
      int index) {
    super("Thrown by rule #" + index + " of action \"" + action
        + "\" in decision table \"" + table
        + ((file != null) ? "\", as defined by file " + file + '.' : "\"."));
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  msg  Purpose of the argument.
   */
  public DecisionThrowException(String msg) {
    super(msg);
  }
}

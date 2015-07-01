/* --------------------------------------------------------------------------+
   Debug.java - Special stream for outputting debug messages.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Tests here if debug printing is enabled, rather than in the monitored code.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;
import java.io.PrintStream;


/**
 * Provides an alternative print stream for outputting debug messages.<p>
 *
 * Overrides the <i>println</i> methods of class <b>PrintStream</b> to print
 * only if the <i>is_debug_</i> flag has been set.
 *
 * @since   v0.12 - Feb 24, 2010
 * @author  Stephen Barrett
 */
public class Debug extends PrintStream {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Wraps a print stream for the purposes of conditionally outputting debug
   * messages sent from the application code.
   *
   * @param  out  Print stream to wrap.
   */
  public Debug(PrintStream out) {
    super(out);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  /**
   * Gives the debug mode, and indirectly the state of debug message printing.
   *
   * @return  true = debug mode - print, false = normal mode - no print
   * @category  getter
   */
  public boolean isDebug() {
    return is_debug_;
  }


  /**
   * Clears the debug message printing flag so as <i>not</i> to print.
   *
   * @return  true = debug was on, false = debug was off
   * @category  setter
   */
  public boolean resetDebug() {
    boolean old_debug = is_debug_;
    is_debug_ = false;
    return old_debug;
  }


  /**
   * Sets the debug message printing flag so as to print.
   *
   * @return  true = debug was on, false = debug was off
   * @category  setter
   */
  public boolean setDebug() {
    boolean old_debug = is_debug_;
    is_debug_ = true;
    return old_debug;
  }


  /**
   * Sets the debug message printing flag so as to print.
   *
   * @param  is_debug  true = turn debug on, false = turn debug off
   * @return  true = debug was on, false = debug was off
   * @category  setter
   */
  public boolean setDebug(boolean is_debug) {
    boolean old_debug = is_debug_;
    is_debug_ = is_debug;
    return old_debug;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Conditionally outputs a boolean value.
   *
   * @param  f  Boolean value to print.
   */
  @Override public void print(boolean f) {
    if (is_debug_)
      super.print(f);
  }


  /**
   * Conditionally outputs a character value.
   *
   * @param  c  Character value to print.
   */
  @Override public void print(char c) {
    if (is_debug_)
      super.print(c);
  }


  /**
   * Conditionally outputs a character array.
   *
   * @param  c  Character array to print.
   */
  @Override public void print(char[] c) {
    if (is_debug_)
      super.print(c);
  }


  /**
   * Conditionally outputs a double value.
   *
   * @param  d  Double value to print.
   */
  @Override public void print(double d) {
    if (is_debug_)
      super.print(d);
  }


  /**
   * Conditionally outputs a float value.
   *
   * @param  f  Float value to print.
   */
  @Override public void print(float f) {
    if (is_debug_)
      super.print(f);
  }


  /**
   * Conditionally outputs an integer value.
   *
   * @param  i  Integer value to print.
   */
  @Override public void print(int i) {
    if (is_debug_)
      super.print(i);
  }


  /**
   * Conditionally outputs a long value.
   *
   * @param  l  Long value to print.
   */
  @Override public void print(long l) {
    if (is_debug_)
      super.print(l);
  }


  /**
   * Conditionally outputs a string.
   *
   * @param  s  String to print.
   */
  @Override public void print(String s) {
    if (is_debug_)
      super.print(s);
  }


  /**
   * Conditionally prints an arbitrary object.
   *
   * @param  o  Object to print.
   */
  @Override public void print(Object o) {
    if (is_debug_)
      super.print(o);
  }


  /**
   * Conditionally outputs a formated string.
   *
   * @param  format  Format for printing.
   * @param  args  Objects to print.
   */
  @Override public PrintStream printf(String format, Object... args) {
    if (is_debug_)
      return super.printf(format, args);
    else
      return null;
  }


  /**
   * Conditionally outputs an empty line.
   */
  @Override public void println() {
    if (is_debug_)
      super.println();
  }


  /**
   * Conditionally outputs a boolean value as a line.
   *
   * @param  f  Boolean value to print.
   */
  @Override public void println(boolean f) {
    if (is_debug_)
      super.println(f);
  }


  /**
   * Conditionally outputs a character value as a line.
   *
   * @param  c  Character value to print.
   */
  @Override public void println(char c) {
    if (is_debug_)
      super.println(c);
  }


  /**
   * Conditionally outputs a character array as a line.
   *
   * @param  c  Character array to print.
   */
  @Override public void println(char[] c) {
    if (is_debug_)
      super.println(c);
  }


  /**
   * Conditionally outputs a double value as a line.
   *
   * @param  d  Double value to print.
   */
  @Override public void println(double d) {
    if (is_debug_)
      super.println(d);
  }


  /**
   * Conditionally outputs a float value as a line.
   *
   * @param  f  Float value to print.
   */
  @Override public void println(float f) {
    if (is_debug_)
      super.println(f);
  }


  /**
   * Conditionally outputs an integer value as a line.
   *
   * @param  i  Integer value to print.
   */
  @Override public void println(int i) {
    if (is_debug_)
      super.println(i);
  }


  /**
   * Conditionally outputs a long value as a line.
   *
   * @param  l  Long value to print.
   */
  @Override public void println(long l) {
    if (is_debug_)
      super.println(l);
  }


  /**
   * Conditionally outputs a string as a line.
   *
   * @param  s  String to print.
   */
  @Override public void println(String s) {
    if (is_debug_)
      super.println(s);
  }


  /**
   * Conditionally prints an arbitrary object as a line.
   *
   * @param  o  Object to print.
   */
  @Override public void println(Object o) {
    if (is_debug_)
      super.println(o);
  }


  // Instance data ----------------------------------------------------------
  /** Flag indicating whether the <i>println()</i> argument should be output. */
  private boolean is_debug_;
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  /** The wrapped System.out stream used by the class for its printing. */
  static public Debug dbg = new Debug(System.out);
  // End class data ---------------------------------------------------------
}

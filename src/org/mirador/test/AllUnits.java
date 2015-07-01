/* --------------------------------------------------------------------------+
   AllUnits.java - Suite consisting of all Mirador's unit test cases.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Test suite that runs the entire collection of Mirador unit tests.<p>
 *
 * May be run either from within Eclipse, or from without, i.e.:
 * <pre>  prompt> org.junit.runner.JUnitCore class [class...]</pre>
 *
 * @since   v0.13 - Feb 26, 2010
 * @author  Stephen Barrett
 */
@RunWith(Suite.class)
@SuiteClasses({TestConstants.class, TestFujabaPluginLaunch.class,
    TestFujabaRecords.class, TestFujabaRecordFactory.class,
    TestFujabaTransaction.class}) //TestEvaluators.class, TestMChangeRepository.class
public class AllUnits {
  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() {
  }
}

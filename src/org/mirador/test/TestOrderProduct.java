/* --------------------------------------------------------------------------+
   TestOrderProduct.java - Tests scenarios of the IMS Order Product use case.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
   --------------------------------------------------------------------------+
   Though run within the JUnit testing framework, the scenario tests are not
   unit tests and as such not amiable to normal assertions and failure counts.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import ca.dsrg.mirador.MiradorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestOrderProduct {
  @Before
  public void setUp() {
//    // Declare initial session objects.
//    ims = new Session();
//
//    // Declare initial domain objects.
//    store = new Inventory();
//    buyer = new Customer();
//    buyer.setName("Nicholas Claus");
//
//    // Declare external actors and control objects.
//    order_prod_scn = new CustomerScreen();
//    pay_auth_sys = new PaymentSystem();
//    seller = new Seller();
//
//    // Link up session and inventory structure...
//    ims.setUser(buyer);
//    seller.setCatalog(store);
//    seller.setSession(ims);
//    seller.setGui(order_prod_scn);
//    seller.setPas(pay_auth_sys);
//
//    // ...including stocking store with products.
//    Product p = new Product();
//    p.setName("freewheel");
//    p.setCategory("part");
//    p.setProdId("p1");
//    p.setInStock(20);
//    store.addToItems(p);
//
//    p = new Product();
//    p.setName("pedals");
//    p.setCategory("part");
//    p.setProdId("p2");
//    p.setInStock(8);
//    store.addToItems(p);
//
//    p = new Product();
//    p.setName("spoke");
//    p.setCategory("wheel");
//    p.setProdId("w1");
//    p.setInStock(37);
//    store.addToItems(p);
//
//    p = new Product();
//    p.setName("chain tool");
//    p.setCategory("tool");
//    p.setProdId("t1");
//    p.setInStock(4);
//    store.addToItems(p);
//
//    p = new Product();
//    p.setName("chain whip");
//    p.setCategory("tool");
//    p.setProdId("t2");
//    p.setInStock(14);
//    store.addToItems(p);
//
//    p = new Product();
//    p.setName("rim");
//    p.setCategory("wheel");
//    p.setProdId("w2");
//    p.setInStock(6);
//    store.addToItems(p);
//
//    p = new Product();
//    p.setName("brake set");
//    p.setCategory("part");
//    p.setProdId("p3");
//    p.setInStock(39);
//    store.addToItems(p);
  }


  @After
  public void tearDownAfterClass() {
//    ims = null;
//    store = null;
//    buyer = null;
//    seller = null;
//    porder = null;
//    prod= null;
  }

  // --------------------------------------------------------------------
  // Use case scenario tests.
  @Test
  public final void scenarioSuccess() /*throws MiradorException*/ {
//    String cat = "part";
//    String pid = "p2";
//    int qty = 5;
//
//    System.out.println("\r\n\n[Order Product - scenario: main success]");
//    System.out.print("List all products in inventory...");
//    Presenter.displayItems(store.iteratorOfItems());
//    assertEquals("Incorrect inventory size.", 7, store.sizeOfItems());
//
//    System.out.print("\r\nList products in \"" + cat + "\" category...");
//    assertTrue("Category \"" + cat + "\" is not part of inventory.",
//      seller.selectCategory(cat));
//    assertEquals("Incorrect category item count.", 3, store.sizeOfMatches());
//
//    System.out.print("\r\nSelect existing product from category...");
//    assertTrue("selectProduct() error.", seller.selectProduct(pid, qty));
//
//    Product prod = seller.getMatched();
//    assertEquals("Retrieved item's ID does not match requested ID.",
//      pid, prod.getProdId());
//
//    biller = seller.getBiller();
//    assertNotNull("Collector control object is null.", biller);
//
//    porder = biller.getBill();
//    assertNotNull("Purchase order domain object is null.", porder);
//
//    prod = porder.getItem();
//    assertNotNull("product domain object is null.", prod);
//
//    buyer = porder.getBuyer();
//    assertNotNull("Buyer domain object is null.", buyer);
//
//    assertEquals("Buyer is *not* the customer.", ims.getUser(), buyer);
//    assertEquals("Selected item is *not* the ordered item.",
//      seller.getMatched(), prod);
//
//    assertTrue("gatherPayment() error.", biller.gatherPayment());
//    assertEquals("Wrong order.", biller.getBill().getOrderNum(), "Z6669");
//    assertTrue("Wrong cost.", biller.getBill().getTotal() == 7568);
//
//    assertTrue("submitPayment() error.", biller.submitPayment());
//    assertEquals("Wrong order.", biller.getBill().getOrderNum(), "Z6669");
//    assertFalse("Payment not authorized.", biller.getBill().getConfirm().isEmpty());
//
//    assertTrue("displayConfirm() error.", biller.displayConfirm());
//
//    System.out.printf("\r\n%42s\r\n", "*** Use case ends successfully. ***");
  }


  @Test
  public final void scenarioNoCategory() {
//    String cat = "empty";
//
//    System.out.print("\r\n\n[Order Product - extension: unknown category]");
//    System.out.print("\r\nList products in \"" + cat + "\" category...");
//    assertFalse("Category \"" + cat + "\" is a part of inventory.",
//      seller.selectCategory(cat));
//    assertEquals("Incorrect category item count.", 0, store.sizeOfMatches());
//    Presenter.displayCategory(store.iteratorOfMatches());
//
//    System.out.printf("\r\n%43s\r\n",
//      "*** Use case ends unsuccessfully. ***");
  }


  @Test(expected = MiradorException.class)
  public final void scenarioNoProduct() /*throws MiradorException*/ {
//    String cat = "tool";
//    String pid = "t3";
//    int qty = 1;
//
//    System.out.print("\r\n\n[Order Product - extension: unknown product]");
//    System.out.print("\r\nList products in \"" + cat + "\" category...");
//    assertTrue("Category \"" + cat + "\" is not part of inventory.",
//      seller.selectCategory(cat));
//    assertEquals("Incorrect category item count.", 2, store.sizeOfMatches());
//
//    System.out.print("\r\nSelect non-existent product from category...");
//    try {
//      assertFalse("Non-existent product found.", seller.selectProduct(pid, qty));
//    }
//    catch (ImsException ex) {
//      if (ex.getMessage().equals("product_error")) {
//        Presenter.displayItem(seller.getMatched());
//        System.out.printf("\r\n%43s\r\n",
//          "*** Use case ends unsuccessfully. ***");
//      }
//
//      throw ex;  // Rethrow expected IMS error.
//    }
  }


  @Test(expected = MiradorException.class)
  public final void scenarioNoQuantity() /*throws MiradorException*/ {
//    String cat = "wheel";
//    String pid = "w1";
//    int qty = 64;
//
//    System.out.print("\r\n\n[Order Product - extension: unavailable product]");
//    System.out.print("\r\nList products in \"" + cat + "\" category...");
//    assertTrue("Category \"" + cat + "\" is not part of inventory.",
//      seller.selectCategory(cat));
//    assertEquals("Incorrect category item count.", 2, store.sizeOfMatches());
//
//    System.out.print("\r\nSelect unavailable product from category...");
//    try {
//      assertFalse("Product is in stock.", seller.selectProduct(pid, qty));
//    }
//    catch (ImsException ex) {
//      if (ex.getMessage().equals("quantity_error")) {
//        Presenter.displayItem(seller.getMatched());
//        System.out.printf("\r\n%43s\r\n",
//          "*** Use case ends unsuccessfully. ***");
//      }
//
//      throw ex;  // Rethrow expected IMS error.
//    }
  }


  // Class data -------------------------------------------------------------
//  private Session ims;
//  private Inventory store;
//  private Customer buyer;
//  private Product prod;
//  private Purchase porder;
//
//  private Seller seller;
//  private Collector biller;
//
//  private Observer order_prod_scn;
//  private Observer pay_auth_sys;
  // End class data ---------------------------------------------------------
}

package vn.iotstar.service;
import org.junit.*;
import static org.junit.Assert.*;
import jakarta.persistence.*;
import vn.iotstar.entity.*;
import vn.iotstar.util.JPAUtil;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
public class OrderServiceTest {
 private Book book(int stock){
  EntityManager em=JPAUtil.getEntityManager();
  try{em.getTransaction().begin();Book b=new Book();b.setTitle("Sách thử");b.setQuantity(stock);b.setPrice(new BigDecimal("12500.00"));em.persist(b);em.getTransaction().commit();return b;}finally{em.close();}
 }
 private User user(){User u=new User();u.setId(27);return u;}
 private int stock(int id){EntityManager em=JPAUtil.getEntityManager();try{return em.find(Book.class,id).getQuantity();}finally{em.close();}}
 private long orders(){EntityManager em=JPAUtil.getEntityManager();try{return em.createQuery("select count(o) from PurchaseOrder o",Long.class).getSingleResult();}finally{em.close();}}
 private void reject(Map<Integer,Integer> cart){try{new OrderService().place(user(),cart,"Người nhận","0901234567","TP HCM");fail("Must reject");}catch(IllegalArgumentException expected){}}
 @Test public void codPersistsAndDecrementsStock(){
  Book b=book(5);PurchaseOrder o=new OrderService().place(user(),Map.of(b.getBookid(),2),"Người nhận","0901234567","TP HCM");
  assertEquals(3,stock(b.getBookid()));assertEquals(new BigDecimal("25000.00"),o.getTotal());assertEquals("COD",o.getPaymentMethod());assertEquals("UNPAID",o.getPaymentStatus());assertEquals("PENDING",o.getStatus());
  assertEquals(2,new OrderService().find(o.getId(),27).getItems().get(0).getQuantity());assertNull(new OrderService().find(o.getId(),28));
 }
 @Test public void rejectsInvalidQuantityAndEmptyCart(){Book b=book(120);for(int q:new int[]{0,-1,100,Integer.MAX_VALUE})reject(Map.of(b.getBookid(),q));reject(Collections.emptyMap());assertEquals(120,stock(b.getBookid()));}
 @Test public void rollsBackWholeOrder(){Book first=book(5),second=book(0);long before=orders();reject(Map.of(first.getBookid(),2,second.getBookid(),1));assertEquals(5,stock(first.getBookid()));assertEquals(before,orders());}
 @Test public void rejectsMissingAndInvalidPrice(){Book b=book(5);EntityManager em=JPAUtil.getEntityManager();try{em.getTransaction().begin();em.find(Book.class,b.getBookid()).setPrice(null);em.getTransaction().commit();}finally{em.close();}reject(Map.of(b.getBookid(),1));reject(Map.of(Integer.MAX_VALUE,1));assertEquals(5,stock(b.getBookid()));}
 @Test public void simultaneousOrdersCannotOversell()throws Exception{
  Book b=book(1);ExecutorService pool=Executors.newFixedThreadPool(2);CountDownLatch start=new CountDownLatch(1);
  Callable<Boolean> task=()->{start.await();try{new OrderService().place(user(),Map.of(b.getBookid(),1),"Người nhận","0901234567","TP HCM");return true;}catch(IllegalArgumentException e){return false;}};
  try{Future<Boolean> a=pool.submit(task),c=pool.submit(task);start.countDown();int successes=(a.get(20,TimeUnit.SECONDS)?1:0)+(c.get(20,TimeUnit.SECONDS)?1:0);assertEquals(1,successes);assertEquals(0,stock(b.getBookid()));}finally{pool.shutdownNow();}
 }
 @Test public void quantityAndMoneyValidation(){for(String s:new String[]{null,"0","-2","1.5","abc","9999999999999"}){try{CartService.positive(s);fail();}catch(IllegalArgumentException expected){}}assertEquals(2,CartService.positive("2"));Book b=new Book();b.setPrice(new BigDecimal("12.50"));b.setQuantity(120);CartItem i=new CartItem(b,3);assertEquals(99,i.getLimit());assertEquals(new BigDecimal("37.50"),i.getSubtotal());}
}

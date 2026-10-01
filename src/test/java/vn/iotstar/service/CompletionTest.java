package vn.iotstar.service;
import org.junit.*;
import static org.junit.Assert.*;
import jakarta.persistence.*;
import vn.iotstar.entity.*;
import vn.iotstar.dao.*;
import vn.iotstar.util.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
public class CompletionTest {
 private Book book(){EntityManager em=JPAUtil.getEntityManager();try{em.getTransaction().begin();Book b=new Book();b.setTitle("Tìm kiếm _ 100%!");b.setIsbn("TEST"+UUID.randomUUID());b.setPrice(new BigDecimal("12345.00"));b.setQuantity(5);em.persist(b);em.getTransaction().commit();return b;}finally{em.close();}}
 private PurchaseOrder order(Book b){User u=new User();u.setId(24110227);return new OrderService().place(u,Map.of(b.getBookid(),2),"Thái Nhựt Huy","0901234567","Địa chỉ kiểm thử");}
 private int stock(Book b){return new BookDAO().findById(b.getBookid()).getQuantity();}
 private void reject(Runnable r){try{r.run();fail("Must reject");}catch(IllegalArgumentException expected){}}
 @Test public void cancellationRestocksOnlyOnce(){Book b=book();PurchaseOrder o=order(b);OrderService s=new OrderService();s.changeStatus(o.getId(),24110227,false,"CANCELLED");assertEquals(5,stock(b));assertEquals("CANCELLED",s.findAdmin(o.getId()).getStatus());reject(()->s.changeStatus(o.getId(),24110227,false,"CANCELLED"));assertEquals(5,stock(b));}
 @Test public void otherUsersCannotCancel(){Book b=book();PurchaseOrder o=order(b);reject(()->new OrderService().changeStatus(o.getId(),1,false,"CANCELLED"));assertEquals(3,stock(b));}
 @Test public void codIsPaidOnlyAfterDelivery(){Book b=book();PurchaseOrder o=order(b);OrderService s=new OrderService();reject(()->s.changeStatus(o.getId(),null,true,"DELIVERED"));s.changeStatus(o.getId(),null,true,"CONFIRMED");reject(()->s.changeStatus(o.getId(),24110227,false,"CANCELLED"));s.changeStatus(o.getId(),null,true,"SHIPPING");assertEquals("UNPAID",s.findAdmin(o.getId()).getPaymentStatus());s.changeStatus(o.getId(),null,true,"DELIVERED");assertEquals("PAID",s.findAdmin(o.getId()).getPaymentStatus());reject(()->s.changeStatus(o.getId(),null,true,"CANCELLED"));assertEquals(3,stock(b));}
 @Test public void simultaneousCancelCannotDoubleRestock()throws Exception{Book b=book();PurchaseOrder o=order(b);ExecutorService p=Executors.newFixedThreadPool(2);CountDownLatch start=new CountDownLatch(1);Callable<Boolean> t=()->{start.await();try{new OrderService().changeStatus(o.getId(),24110227,false,"CANCELLED");return true;}catch(IllegalArgumentException e){return false;}};try{Future<Boolean>a=p.submit(t),c=p.submit(t);start.countDown();assertEquals(1,(a.get(20,TimeUnit.SECONDS)?1:0)+(c.get(20,TimeUnit.SECONDS)?1:0));assertEquals(5,stock(b));}finally{p.shutdownNow();}}
 @Test public void searchEscapesWildcardAndClampsAvailable(){Book b=book();BookDAO dao=new BookDAO();assertTrue(dao.searchCount("100%!",true)>0);assertTrue(dao.searchCount("_",true)>0);assertTrue(dao.search("100%!",true,"price-asc",1).stream().anyMatch(x->x.getBookid().equals(b.getBookid())));assertEquals(0,dao.searchCount("no_such_book_"+UUID.randomUUID(),true));}
 @Test public void passwordHashAndLegacyUpgrade(){String hash=PasswordUtil.hash("Huy@24110227");assertTrue(PasswordUtil.matches("Huy@24110227",hash));assertFalse(PasswordUtil.matches("wrong",hash));assertTrue(PasswordUtil.matches("legacy","legacy"));assertFalse(PasswordUtil.matches("abc","pbkdf2$broken"));User u=new User();u.setEmail("test-"+UUID.randomUUID()+"@example.com");u.setPasswd("legacy");u.setFullname("Thái Nhựt Huy");new UserDAO().save(u);User logged=new UserDAO().login(u.getEmail(),"legacy");assertNotNull(logged);assertTrue(logged.getPasswd().startsWith("pbkdf2$"));assertNull(new UserDAO().login(u.getEmail(),"wrong"));}
 @Test public void validationRejectsNegativeData(){reject(()->FormUtil.price("-1"));reject(()->FormUtil.price("abc"));reject(()->FormUtil.stock("-1"));reject(()->FormUtil.stock("1.5"));assertEquals(0,FormUtil.stock("0"));assertEquals(new BigDecimal("0.00"),FormUtil.price("0.00"));}
}

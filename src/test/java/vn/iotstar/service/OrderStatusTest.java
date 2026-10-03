package vn.iotstar.service;
import org.junit.Test;
import static org.junit.Assert.*;
import jakarta.persistence.*;
import vn.iotstar.entity.*;
import vn.iotstar.util.JPAUtil;
import java.math.BigDecimal;
import java.util.*;
public class OrderStatusTest {
 private final OrderService service=new OrderService();
 private PurchaseOrder place(int userId){
  EntityManager em=JPAUtil.getEntityManager();Book b=new Book();
  try{em.getTransaction().begin();b.setTitle("Kiểm thử trạng thái");b.setQuantity(10);b.setPrice(new BigDecimal("10000"));em.persist(b);em.getTransaction().commit();}finally{em.close();}
  User u=new User();u.setId(userId);return service.place(u,Map.of(b.getBookid(),2),"Đơn thử","0901234567","TP HCM");
 }
 private void dbStatus(long id,String code){
  EntityManager em=JPAUtil.getEntityManager();
  try{em.getTransaction().begin();em.createNativeQuery("UPDATE purchase_orders SET status=:s WHERE id=:id").setParameter("s",code).setParameter("id",id).executeUpdate();em.getTransaction().commit();}finally{em.close();}
 }
 private int stock(PurchaseOrder o){
  EntityManager em=JPAUtil.getEntityManager();try{return em.find(Book.class,service.findAdmin(o.getId()).getItems().get(0).getBookId()).getQuantity();}finally{em.close();}
 }
 private void reject(Runnable action){try{action.run();fail("Invalid transition must be rejected");}catch(IllegalArgumentException expected){}}
 @Test public void allEightFiltersReflectDirectDatabaseChangesAndRespectOwner(){
  PurchaseOrder own=place(910001),other=place(910002);
  assertEquals(8,OrderStatus.values().length);
  for(OrderStatus s:OrderStatus.values()){
   dbStatus(own.getId(),s.name());dbStatus(other.getId(),s.name());
   assertEquals(1,service.count(910001,s.name()));
   assertEquals(own.getId(),service.list(910001,s.name(),1).get(0).getId());
   assertTrue(service.list(null,s.name(),1).stream().anyMatch(o->o.getId().equals(own.getId())));
   assertEquals(s.getLabel(),service.find(own.getId(),910001).getStatusLabel());
   assertEquals(s.getStep(),service.findAdmin(own.getId()).getStatusStep());
   for(OrderStatus absent:OrderStatus.values())if(absent!=s)assertEquals(0,service.count(910001,absent.name()));
  }
  assertNull(service.find(own.getId(),910002));
  assertFalse(OrderService.validStatus(null));assertFalse(OrderService.validStatus("INVALID"));
 }
 @Test public void deliveryReturnRestocksOnceAndMarksRefundPending(){
  PurchaseOrder o=place(910003);
  for(String code:List.of("CONFIRMED","PREPARING","SHIPPING","DELIVERING","DELIVERED"))service.changeStatus(o.getId(),null,true,code);
  assertEquals("PAID",service.findAdmin(o.getId()).getPaymentStatus());assertEquals(8,stock(o));
  service.changeStatus(o.getId(),null,true,"RETURNED");
  assertEquals(10,stock(o));assertEquals("REFUND_PENDING",service.findAdmin(o.getId()).getPaymentStatus());
  reject(()->service.changeStatus(o.getId(),null,true,"RETURNED"));assertEquals(10,stock(o));
 }
 @Test public void failedDeliveryReturnsWithoutClaimingCollectedPayment(){
  PurchaseOrder o=place(910004);
  for(String code:List.of("CONFIRMED","PREPARING","SHIPPING"))service.changeStatus(o.getId(),null,true,code);
  reject(()->service.changeStatus(o.getId(),910004,false,"RETURNED"));
  service.changeStatus(o.getId(),null,true,"RETURNED");
  assertEquals("UNPAID",service.findAdmin(o.getId()).getPaymentStatus());assertEquals(10,stock(o));
 }
 @Test public void preparationCanBeCancelledButStagesCannotBeSkipped(){
  PurchaseOrder o=place(910005);
  reject(()->service.changeStatus(o.getId(),null,true,"SHIPPING"));
  reject(()->service.changeStatus(o.getId(),null,true,null));
  service.changeStatus(o.getId(),null,true,"CONFIRMED");
  reject(()->service.changeStatus(o.getId(),null,true,"DELIVERING"));
  service.changeStatus(o.getId(),null,true,"PREPARING");service.changeStatus(o.getId(),null,true,"CANCELLED");
  assertEquals(10,stock(o));reject(()->service.changeStatus(o.getId(),null,true,"CONFIRMED"));
 }
}

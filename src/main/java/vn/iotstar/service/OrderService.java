package vn.iotstar.service;
import jakarta.persistence.*;
import vn.iotstar.entity.*;
import vn.iotstar.util.JPAUtil;
import java.math.BigDecimal;
import java.util.*;
public class OrderService {
 public PurchaseOrder place(User user,Map<Integer,Integer> cart,String recipient,String phone,String address){
  if(cart.isEmpty())throw new IllegalArgumentException("Giỏ hàng đang trống.");
  EntityManager em=JPAUtil.getEntityManager(); EntityTransaction tx=em.getTransaction();
  try{
   tx.begin(); PurchaseOrder order=new PurchaseOrder();
   order.setUserId(user.getId());order.setRecipient(recipient);order.setPhone(phone);order.setAddress(address);
   BigDecimal total=BigDecimal.ZERO;
   // Acquire inventory locks in consistent order.
   for(Map.Entry<Integer,Integer> e:new TreeMap<>(cart).entrySet()){
    Book b=em.find(Book.class,e.getKey(),LockModeType.PESSIMISTIC_WRITE);int q=e.getValue();
    if(b==null || b.getQuantity()==null || q<1 || q>99 || q>b.getQuantity())
     throw new IllegalArgumentException("Sách mã "+e.getKey()+" vượt tồn kho hoặc giới hạn 99 cuốn. Hãy cập nhật giỏ.");
    if(b.getPrice()==null || b.getPrice().signum()<0)throw new IllegalArgumentException("Giá sách không hợp lệ.");
    OrderItem item=new OrderItem();item.setOrder(order);item.setBookId(b.getBookid());item.setTitle(b.getTitle());item.setUnitPrice(b.getPrice());item.setQuantity(q);
    order.getItems().add(item);total=total.add(item.getSubtotal());b.setQuantity(b.getQuantity()-q);
   }
   order.setTotal(total);em.persist(order);tx.commit();return order;
  }catch(RuntimeException e){if(tx.isActive())tx.rollback();throw e;}finally{em.close();}
 }
 public PurchaseOrder find(long id,int userId){
  EntityManager em=JPAUtil.getEntityManager();
  try{PurchaseOrder o=em.find(PurchaseOrder.class,id);if(o==null || o.getUserId()!=userId)return null;o.getItems().size();return o;}
  finally{em.close();}
 }

 public List<PurchaseOrder> list(Integer userId,String status,int page){
  EntityManager em=JPAUtil.getEntityManager();
  try{
   String where=" where 1=1"+(userId==null?"":" and o.userId=:user")+(validStatus(status)?" and o.status=:status":"");
   TypedQuery<PurchaseOrder> q=em.createQuery("select o from PurchaseOrder o"+where+" order by o.id desc",PurchaseOrder.class);
   if(userId!=null)q.setParameter("user",userId);if(validStatus(status))q.setParameter("status",status);
   return q.setFirstResult((page-1)*10).setMaxResults(10).getResultList();
  }finally{em.close();}
 }
 public long count(Integer userId,String status){
  EntityManager em=JPAUtil.getEntityManager();try{
   TypedQuery<Long> q=em.createQuery("select count(o) from PurchaseOrder o where 1=1"+(userId==null?"":" and o.userId=:user")+(validStatus(status)?" and o.status=:status":""),Long.class);
   if(userId!=null)q.setParameter("user",userId);if(validStatus(status))q.setParameter("status",status);return q.getSingleResult();
  }finally{em.close();}
 }
 public PurchaseOrder findAdmin(long id){
  EntityManager em=JPAUtil.getEntityManager();try{PurchaseOrder o=em.find(PurchaseOrder.class,id);if(o!=null)o.getItems().size();return o;}finally{em.close();}
 }
 public Map<String,Object> stats(){
  EntityManager em=JPAUtil.getEntityManager();try{
   Map<String,Object> m=new HashMap<>();m.put("bookCount",em.createQuery("select count(b) from Book b",Long.class).getSingleResult());
   m.put("userCount",em.createQuery("select count(u) from User u",Long.class).getSingleResult());
   m.put("orderCount",count(null,null));m.put("pendingCount",count(null,"PENDING"));
   m.put("lowStock",em.createQuery("select b from Book b where b.quantity<=5 order by b.quantity,b.bookid",Book.class).setMaxResults(5).getResultList());
   BigDecimal revenue=em.createQuery("select sum(o.total) from PurchaseOrder o where o.paymentStatus='PAID'",BigDecimal.class).getSingleResult();
   m.put("revenue",revenue==null?BigDecimal.ZERO:revenue);return m;
  }finally{em.close();}
 }
 public static boolean validStatus(String s){return OrderStatus.from(s)!=null;}
 public void changeStatus(long id,Integer ownerId,boolean admin,String next){
  EntityManager em=JPAUtil.getEntityManager();EntityTransaction tx=em.getTransaction();
  try{
   tx.begin();PurchaseOrder o=em.find(PurchaseOrder.class,id,LockModeType.PESSIMISTIC_WRITE);
   if(o==null || (!admin && !Objects.equals(o.getUserId(),ownerId)))throw new IllegalArgumentException("Không tìm thấy đơn hàng.");
   String current=o.getStatus();
   OrderStatus state=OrderStatus.from(current), target=OrderStatus.from(next);
   boolean allowed=target!=null && (admin?state!=null && state.getNextStatuses().contains(target):"PENDING".equals(current) && "CANCELLED".equals(next));
   if(!allowed)throw new IllegalArgumentException("Không thể chuyển trạng thái đơn hàng này.");
   if("CANCELLED".equals(next) || "RETURNED".equals(next)){
    List<OrderItem> items=new ArrayList<>(o.getItems());items.sort(Comparator.comparing(OrderItem::getBookId));
    for(OrderItem item:items){Book b=em.find(Book.class,item.getBookId(),LockModeType.PESSIMISTIC_WRITE);if(b!=null)b.setQuantity(Math.addExact(b.getQuantity()==null?0:b.getQuantity(),item.getQuantity()));}
   }
   o.setStatus(next);if("DELIVERED".equals(next))o.setPaymentStatus("PAID");
   if("RETURNED".equals(next) && "PAID".equals(o.getPaymentStatus()))o.setPaymentStatus("REFUND_PENDING");tx.commit();
  }catch(RuntimeException e){if(tx.isActive())tx.rollback();throw e;}finally{em.close();}
 }
}
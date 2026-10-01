package vn.iotstar.controller;
import org.junit.*;
import static org.junit.Assert.*;
import jakarta.servlet.http.*;
import jakarta.persistence.*;
import vn.iotstar.entity.*;
import vn.iotstar.service.CartService;
import vn.iotstar.util.JPAUtil;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;
public class CartServletTest {
 private final Map<String,Object> sessionData=new HashMap<>();
 private final HttpSession session=(HttpSession)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpSession.class},(p,m,a)->{
  if(m.getName().equals("getAttribute"))return sessionData.get(a[0]);
  if(m.getName().equals("setAttribute")){sessionData.put((String)a[0],a[1]);return null;}
  if(m.getName().equals("removeAttribute")){sessionData.remove(a[0]);return null;}
  return null;
 });
 private int id;
 @Before public void seed(){EntityManager em=JPAUtil.getEntityManager();try{em.getTransaction().begin();Book b=new Book();b.setTitle("Giỏ hàng");b.setPrice(new BigDecimal("10000"));b.setQuantity(5);em.persist(b);em.getTransaction().commit();id=b.getBookid();}finally{em.close();}}
 private void post(String action,String qty,String token)throws Exception{
  Map<String,String> params=new HashMap<>();params.put("action",action);params.put("quantity",qty);params.put("token",token);params.put("bookid",String.valueOf(id));
  HttpServletRequest req=(HttpServletRequest)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpServletRequest.class},(p,m,a)->{
   if(m.getName().equals("getSession"))return session;if(m.getName().equals("getParameter"))return params.get(a[0]);if(m.getName().equals("getContextPath"))return "/shop";return null;
  });
  HttpServletResponse res=(HttpServletResponse)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpServletResponse.class},(p,m,a)->{if(m.getName().equals("sendRedirect"))assertEquals("/shop/cart",a[0]);return null;});
  new CartServlet().doPost(req,res);
 }
 @Test public void addMergeUpdateRemoveAndClear()throws Exception{
  String token=CartService.token(session);post("add","2",token);post("add","1",token);assertEquals(Integer.valueOf(3),CartService.cart(session).get(id));post("update","4",token);assertEquals(Integer.valueOf(4),CartService.cart(session).get(id));post("remove",null,token);assertTrue(CartService.cart(session).isEmpty());post("add","1",token);post("clear",null,token);assertTrue(CartService.cart(session).isEmpty());
 }
 @Test public void rejectsBadQuantityAndStockOverflow()throws Exception{
  String token=CartService.token(session);post("add","3",token);
  for(String qty:new String[]{"0","-1","1.5","abc","6","2147483647"}){post("update",qty,token);assertEquals(Integer.valueOf(3),CartService.cart(session).get(id));assertNotNull(sessionData.get("cartError"));}
  post("add","3",token);assertEquals(Integer.valueOf(3),CartService.cart(session).get(id));
 }
 @Test public void rejectsMissingTokenAndUnknownAction()throws Exception{
  post("add","1",null);assertTrue(CartService.cart(session).isEmpty());post("unknown","1",CartService.token(session));assertTrue(CartService.cart(session).isEmpty());
 }
 @Test public void consumedTokenCannotBeReused()throws Exception{
  String old=CartService.token(session);session.removeAttribute("cartToken");post("add","1",old);assertTrue(CartService.cart(session).isEmpty());
 }
}

package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import vn.iotstar.entity.*;
import vn.iotstar.service.*;
@WebServlet({"/checkout","/order-success"})
public class CheckoutServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  HttpSession s=req.getSession();User user=(User)s.getAttribute("account");
  if(user==null){s.setAttribute("checkoutAfterLogin",true);res.sendRedirect(req.getContextPath()+"/login");return;}
  if("/order-success".equals(req.getServletPath())){
   try{PurchaseOrder o=new OrderService().find(Long.parseLong(req.getParameter("id")),user.getId());
    if(o==null){res.sendError(404);return;}req.setAttribute("order",o);req.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(req,res);
   }catch(NumberFormatException e){res.sendError(400);}return;
  }
  synchronized(s){
   if(CartService.cart(s).isEmpty()){res.sendRedirect(req.getContextPath()+"/cart");return;}
   req.setAttribute("cartToken",CartService.token(s));
   try{List<CartItem> items=CartService.items(CartService.cart(s));req.setAttribute("items",items);req.setAttribute("total",CartService.total(items));}
   catch(IllegalArgumentException e){s.setAttribute("cartError",e.getMessage());res.sendRedirect(req.getContextPath()+"/cart");return;}
   req.setAttribute("error",s.getAttribute("checkoutError"));s.removeAttribute("checkoutError");req.setAttribute("delivery",s.getAttribute("delivery"));
  }req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req,res);
 }
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{
  if(!"/checkout".equals(req.getServletPath())){res.sendError(405);return;}
  req.setCharacterEncoding("UTF-8");HttpSession s=req.getSession();
  synchronized(s){
   User user=(User)s.getAttribute("account");if(user==null){s.setAttribute("checkoutAfterLogin",true);res.sendRedirect(req.getContextPath()+"/login");return;}
   try{
    CartService.validateToken(s,req.getParameter("token"));
    String recipient=text(req,"recipient",100),phone=text(req,"phone",20),address=text(req,"address",500);
    Map<String,String> d=new HashMap<>();d.put("recipient",recipient);d.put("phone",phone);d.put("address",address);s.setAttribute("delivery",d);
    if(!phone.matches("[+]?[0-9][0-9 .-]{7,18}"))throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
    if(!"COD".equals(req.getParameter("paymentMethod")))throw new IllegalArgumentException("Chỉ hỗ trợ COD.");
    PurchaseOrder o=new OrderService().place(user,new TreeMap<>(CartService.cart(s)),recipient,phone,address);
    CartService.cart(s).clear();s.removeAttribute("delivery");s.removeAttribute("cartToken");
    res.sendRedirect(req.getContextPath()+"/order-success?id="+o.getId());return;
   }catch(IllegalArgumentException e){s.setAttribute("checkoutError",e.getMessage());}
   catch(RuntimeException e){getServletContext().log("COD checkout failed",e);s.setAttribute("checkoutError","Không thể lưu đơn hàng. Vui lòng thử lại.");}
  }res.sendRedirect(req.getContextPath()+"/checkout");
 }
 private String text(HttpServletRequest req,String name,int max){
  String v=req.getParameter(name);v=v==null?"":v.trim();
  if(v.isEmpty() || v.length()>max)throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin nhận hàng, đúng độ dài cho phép.");return v;
 }
}
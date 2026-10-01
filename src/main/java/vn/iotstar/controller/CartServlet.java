package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import vn.iotstar.entity.*;
import vn.iotstar.dao.BookDAO;
import vn.iotstar.service.CartService;
@WebServlet("/cart")
public class CartServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  HttpSession s=req.getSession();
  synchronized(s){
   req.setAttribute("cartToken",CartService.token(s));req.setAttribute("error",s.getAttribute("cartError"));s.removeAttribute("cartError");
   try{List<CartItem> items=CartService.items(CartService.cart(s));req.setAttribute("items",items);req.setAttribute("total",CartService.total(items));}
   catch(IllegalArgumentException e){req.setAttribute("error",e.getMessage());req.setAttribute("invalidCart",new TreeMap<>(CartService.cart(s)));}
  }
  req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req,res);
 }
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{
  req.setCharacterEncoding("UTF-8");HttpSession s=req.getSession();
  synchronized(s){
   try{
    CartService.validateToken(s,req.getParameter("token"));Map<Integer,Integer> c=CartService.cart(s);String action=req.getParameter("action");
    if("clear".equals(action))c.clear();
    else{
     int id=CartService.positive(req.getParameter("bookid"));
     if("remove".equals(action))c.remove(id);
     else if("add".equals(action) || "update".equals(action)){
      if("update".equals(action) && !c.containsKey(id))throw new IllegalArgumentException("Sách không có trong giỏ.");
      int q=CartService.positive(req.getParameter("quantity"));long wanted="add".equals(action)?(long)c.getOrDefault(id,0)+q:q;
      Book b=new BookDAO().findById(id);if(b==null)throw new IllegalArgumentException("Sách không tồn tại.");
      int limit=Math.min(99,b.getQuantity()==null?0:Math.max(0,b.getQuantity()));
      if(wanted>limit)throw new IllegalArgumentException("Chỉ được mua tối đa "+limit+" cuốn sách này.");
      if(b.getPrice()==null || b.getPrice().signum()<0)throw new IllegalArgumentException("Giá sách không hợp lệ.");
      c.put(id,(int)wanted);
     }else throw new IllegalArgumentException("Thao tác không hợp lệ.");
    }
   }catch(IllegalArgumentException e){s.setAttribute("cartError",e.getMessage());}
  }res.sendRedirect(req.getContextPath()+"/cart");
 }
}
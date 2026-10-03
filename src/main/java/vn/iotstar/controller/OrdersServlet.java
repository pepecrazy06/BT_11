package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.entity.User;
import vn.iotstar.service.*;
import vn.iotstar.util.FormUtil;
@WebServlet("/orders")
public class OrdersServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  User u=(User)r.getSession().getAttribute("account");if(u==null){s.sendRedirect(r.getContextPath()+"/login");return;}
  r.setAttribute("statusOptions",vn.iotstar.entity.OrderStatus.values());
  OrderService service=new OrderService();String status=r.getParameter("status");long count=service.count(u.getId(),status);int pages=Math.max(1,(int)Math.ceil(count/10.0)),page=Math.min(FormUtil.page(r.getParameter("page")),pages);
  r.setAttribute("orders",service.list(u.getId(),status,page));r.setAttribute("total",count);r.setAttribute("currentPage",page);r.setAttribute("totalPage",pages);
  r.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(r,s);
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException{
  HttpSession session=r.getSession();User u=(User)session.getAttribute("account");if(u==null){s.sendRedirect(r.getContextPath()+"/login");return;}
  try{CartService.validateToken(session,r.getParameter("token"));new OrderService().changeStatus(FormUtil.id(r,"id"),u.getId(),false,"CANCELLED");session.setAttribute("flash","Đã hủy đơn và hoàn lại tồn kho.");}
  catch(IllegalArgumentException e){session.setAttribute("flash",e.getMessage());}
  catch(RuntimeException e){getServletContext().log("Cancel order failed",e);session.setAttribute("flash","Chưa thể hủy đơn. Vui lòng thử lại.");}
  s.sendRedirect(r.getContextPath()+"/orders");
 }
}
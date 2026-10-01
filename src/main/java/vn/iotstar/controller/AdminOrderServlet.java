package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.entity.*;
import vn.iotstar.service.*;
import vn.iotstar.util.FormUtil;
@WebServlet({"/admin/dashboard","/admin/orders"})
public class AdminOrderServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  OrderService service=new OrderService();
  if("/admin/dashboard".equals(r.getServletPath())){
   r.setAttribute("stats",service.stats());r.setAttribute("orders",service.list(null,null,1));r.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(r,s);return;
  }
  if(r.getParameter("id")!=null){
   try{PurchaseOrder o=service.findAdmin(FormUtil.id(r,"id"));if(o==null){s.sendError(404);return;}r.setAttribute("order",o);r.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp").forward(r,s);}catch(IllegalArgumentException e){s.sendError(400);}return;
  }
  String status=r.getParameter("status");long count=service.count(null,status);int pages=Math.max(1,(int)Math.ceil(count/10.0)),page=Math.min(FormUtil.page(r.getParameter("page")),pages);
  r.setAttribute("orders",service.list(null,status,page));r.setAttribute("total",count);r.setAttribute("totalPage",pages);r.setAttribute("currentPage",page);r.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(r,s);
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException{
  HttpSession session=r.getSession();
  try{CartService.validateToken(session,r.getParameter("token"));new OrderService().changeStatus(FormUtil.id(r,"id"),null,true,r.getParameter("status"));session.setAttribute("flash","Đã cập nhật trạng thái đơn hàng.");}
  catch(IllegalArgumentException e){session.setAttribute("flash",e.getMessage());}
  catch(RuntimeException e){getServletContext().log("Update order failed",e);session.setAttribute("flash","Chưa thể cập nhật đơn hàng.");}
  s.sendRedirect(r.getContextPath()+"/admin/orders");
 }
}
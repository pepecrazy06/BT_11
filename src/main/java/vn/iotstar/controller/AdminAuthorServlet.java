package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.dao.AuthorDAO;
import vn.iotstar.entity.Author;
import vn.iotstar.service.CartService;
import vn.iotstar.util.FormUtil;
@WebServlet("/admin/authors")
public class AdminAuthorServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  AuthorDAO dao=new AuthorDAO();
  if("add".equals(r.getParameter("action")) || "edit".equals(r.getParameter("action"))){
   if("edit".equals(r.getParameter("action"))){try{Author a=dao.findById(FormUtil.id(r,"id"));if(a==null){s.sendError(404);return;}r.setAttribute("author",a);}catch(IllegalArgumentException e){s.sendError(400);return;}}
   r.getRequestDispatcher("/WEB-INF/views/admin/author-form.jsp").forward(r,s);return;
  }
  long count=dao.count();int pages=Math.max(1,(int)Math.ceil(count/6.0)),page=Math.min(FormUtil.page(r.getParameter("page")),pages);
  r.setAttribute("authors",dao.findAll(page));r.setAttribute("total",count);r.setAttribute("totalPage",pages);r.setAttribute("currentPage",page);r.getRequestDispatcher("/WEB-INF/views/admin/authors.jsp").forward(r,s);
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  AuthorDAO dao=new AuthorDAO();
  try{
   CartService.validateToken(r.getSession(),r.getParameter("token"));
   if("delete".equals(r.getParameter("action"))){dao.delete(FormUtil.id(r,"id"));r.getSession().setAttribute("flash","Đã xóa tác giả.");s.sendRedirect(r.getContextPath()+"/admin/authors");return;}
   String id=r.getParameter("author_id");Author a=id==null || id.isBlank()?new Author():dao.findById(FormUtil.id(r,"author_id"));if(a==null)throw new IllegalArgumentException("Tác giả không tồn tại.");
   a.setAuthor_name(FormUtil.text(r,"author_name",200,true));a.setDate_of_birth(FormUtil.date(r.getParameter("date_of_birth")));dao.save(a);
   r.getSession().setAttribute("flash","Đã lưu tác giả.");s.sendRedirect(r.getContextPath()+"/admin/authors");
  }catch(RuntimeException e){
   if("delete".equals(r.getParameter("action"))){r.getSession().setAttribute("flash","Không thể xóa tác giả đang liên kết với sách.");s.sendRedirect(r.getContextPath()+"/admin/authors");return;}
   r.setAttribute("error",e instanceof IllegalArgumentException?e.getMessage():"Chưa thể lưu tác giả.");r.setAttribute("form",r.getParameterMap());r.getRequestDispatcher("/WEB-INF/views/admin/author-form.jsp").forward(r,s);
  }
 }
}
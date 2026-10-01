package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import vn.iotstar.dao.*;
import vn.iotstar.entity.*;
import vn.iotstar.service.CartService;
import vn.iotstar.util.FormUtil;
@WebServlet("/admin/books")
public class AdminBookServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  BookDAO dao=new BookDAO(); r.setAttribute("selectedAuthors",java.util.Collections.emptyList());
  if("add".equals(r.getParameter("action")) || "edit".equals(r.getParameter("action"))){
   if("edit".equals(r.getParameter("action"))){try{Book b=dao.findById(FormUtil.id(r,"id"));if(b==null){s.sendError(404);return;}r.setAttribute("book",b);r.setAttribute("selectedAuthors",b.getAuthors().stream().map(Author::getAuthor_id).toList());}catch(IllegalArgumentException e){s.sendError(400);return;}}
   r.setAttribute("authors",new AuthorDAO().findAll());r.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(r,s);return;
  }
  long count=dao.count();int pages=Math.max(1,(int)Math.ceil(count/6.0)),page=Math.min(FormUtil.page(r.getParameter("page")),pages);
  r.setAttribute("books",dao.findAllAdmin(page));r.setAttribute("total",count);r.setAttribute("totalPage",pages);r.setAttribute("currentPage",page);r.getRequestDispatcher("/WEB-INF/views/admin/books.jsp").forward(r,s);
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  r.setCharacterEncoding("UTF-8");BookDAO dao=new BookDAO(); r.setAttribute("selectedAuthors",java.util.Collections.emptyList());
  try{
   CartService.validateToken(r.getSession(),r.getParameter("token"));
   if("delete".equals(r.getParameter("action"))){dao.delete(FormUtil.id(r,"id"));r.getSession().setAttribute("flash","Đã xóa sách.");s.sendRedirect(r.getContextPath()+"/admin/books");return;}
   String id=r.getParameter("bookid");Book b=id==null || id.isBlank()?new Book():dao.findById(FormUtil.id(r,"bookid"));
   if(b==null)throw new IllegalArgumentException("Sách không tồn tại.");
   b.setTitle(FormUtil.text(r,"title",200,true));b.setIsbn(FormUtil.text(r,"isbn",255,true));b.setPublisher(FormUtil.text(r,"publisher",255,true));b.setDescription(FormUtil.text(r,"description",2000,false));
   b.setPrice(FormUtil.price(r.getParameter("price")));b.setQuantity(FormUtil.stock(r.getParameter("quantity")));b.setPublish_date(FormUtil.date(r.getParameter("publish_date")));
   String cover=FormUtil.text(r,"cover_image",255,false);if(!cover.isBlank() && (!cover.startsWith("assets/images/") || cover.contains("..") || !cover.matches("[a-zA-Z0-9_./-]+")))throw new IllegalArgumentException("Ảnh bìa cần nằm trong assets/images/.");
   b.setCover_image(cover.isBlank()?"assets/images/book-placeholder.svg":cover);
   List<Author> authors=new ArrayList<>();String[] authorIds=r.getParameterValues("authors");if(authorIds!=null)for(String aid:authorIds){Author a=new AuthorDAO().findById(CartService.positive(aid));if(a==null)throw new IllegalArgumentException("Tác giả không tồn tại.");if(authors.stream().noneMatch(x->x.getAuthor_id().equals(a.getAuthor_id())))authors.add(a);}
   b.setAuthors(authors);dao.save(b);r.getSession().setAttribute("flash","Đã lưu thông tin sách.");s.sendRedirect(r.getContextPath()+"/admin/books");
  }catch(IllegalArgumentException e){showError(r,s,e.getMessage());}
  catch(RuntimeException e){getServletContext().log("Save book failed",e);showError(r,s,"Chưa lưu được sách. Nếu xóa, hãy kiểm tra đánh giá và dữ liệu liên quan.");}
 }
 private void showError(HttpServletRequest r,HttpServletResponse s,String message)throws ServletException,IOException{
  r.setAttribute("error",message);r.setAttribute("authors",new AuthorDAO().findAll());
  if("delete".equals(r.getParameter("action"))){r.getSession().setAttribute("flash",message);s.sendRedirect(r.getContextPath()+"/admin/books");return;}
  r.setAttribute("form",r.getParameterMap());
  java.util.List<Integer> selected=new java.util.ArrayList<>();String[] ids=r.getParameterValues("authors");if(ids!=null)for(String aid:ids){try{selected.add(Integer.parseInt(aid));}catch(NumberFormatException ignored){}}r.setAttribute("selectedAuthors",selected);r.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(r,s);
 }
}
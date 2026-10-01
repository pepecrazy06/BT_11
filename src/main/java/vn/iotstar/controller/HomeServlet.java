package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.dao.*;
import vn.iotstar.util.FormUtil;
@WebServlet("/home")
public class HomeServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  String q=r.getParameter("q");q=q==null?"":q.trim();if(q.length()>100)q=q.substring(0,100);
  String sort=r.getParameter("sort");if(!java.util.Set.of("newest","price-asc","price-desc","title").contains(sort==null?"":sort))sort="newest";
  boolean available="1".equals(r.getParameter("available"));
  BookDAO dao=new BookDAO();long count=dao.searchCount(q,available);int pages=Math.max(1,(int)Math.ceil(count/8.0));
  int page=Math.min(FormUtil.page(r.getParameter("page")),pages);
  r.setAttribute("books",dao.search(q,available,sort,page));r.setAttribute("total",count);r.setAttribute("totalPage",pages);r.setAttribute("currentPage",page);r.setAttribute("q",q);r.setAttribute("sort",sort);r.setAttribute("available",available);
  r.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(r,s);
 }
}
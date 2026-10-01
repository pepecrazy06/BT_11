package vn.iotstar.util;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.entity.User;
@WebFilter("/admin/*")
public class AdminFilter implements Filter {
 public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
  HttpServletRequest r=(HttpServletRequest)request;HttpServletResponse s=(HttpServletResponse)response;
  HttpSession session=r.getSession(false);User u=session==null?null:(User)session.getAttribute("account");
  if(u==null){s.sendRedirect(r.getContextPath()+"/login");return;}
  if(!u.isAdmin()){s.sendError(403);return;}chain.doFilter(request,response);
 }
}
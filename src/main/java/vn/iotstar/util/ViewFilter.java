package vn.iotstar.util;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.service.CartService;
@WebFilter("/*")
public class ViewFilter implements Filter {
 public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
  HttpServletRequest r=(HttpServletRequest)request;
  if(!r.getServletPath().startsWith("/assets/")){
   HttpSession s=r.getSession();synchronized(s){
    r.setAttribute("cartToken",CartService.token(s));
    r.setAttribute("cartCount",CartService.cart(s).values().stream().mapToInt(Integer::intValue).sum());
    r.setAttribute("flash",s.getAttribute("flash"));s.removeAttribute("flash");
   }
  }chain.doFilter(request,response);
 }
}
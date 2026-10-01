package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.dao.UserDAO;
import vn.iotstar.entity.User;
import vn.iotstar.service.CartService;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{r.getRequestDispatcher("/login.jsp").forward(r,s);}
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  r.setCharacterEncoding("UTF-8");
  try{
   CartService.validateToken(r.getSession(),r.getParameter("token"));User u=new UserDAO().login(r.getParameter("email"),r.getParameter("password"));
   if(u==null)throw new IllegalArgumentException("Email hoặc mật khẩu chưa đúng.");
   HttpSession session=r.getSession();r.changeSessionId();session.setAttribute("account",u);
   String next=Boolean.TRUE.equals(session.getAttribute("checkoutAfterLogin"))?"/checkout":u.isAdmin()?"/admin/dashboard":"/home";
   session.removeAttribute("checkoutAfterLogin");s.sendRedirect(r.getContextPath()+next);
  }catch(IllegalArgumentException e){r.setAttribute("error",e.getMessage());r.getRequestDispatcher("/login.jsp").forward(r,s);}
  catch(RuntimeException e){getServletContext().log("Login failed",e);r.setAttribute("error","Chưa thể kết nối dữ liệu. Vui lòng thử lại.");r.getRequestDispatcher("/login.jsp").forward(r,s);}
 }
}
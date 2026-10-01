package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.dao.UserDAO;
import vn.iotstar.entity.User;
import vn.iotstar.service.CartService;
@WebServlet("/verify-otp")
public class VerifyOTPServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{r.getRequestDispatcher("/verify-otp.jsp").forward(r,s);}
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  HttpSession session=r.getSession();
  synchronized(session){
   try{
    CartService.validateToken(session,r.getParameter("token"));
    Long expires=(Long)session.getAttribute("otpExpires");Integer attempts=(Integer)session.getAttribute("otpAttempts");
    if(expires==null || expires<System.currentTimeMillis() || attempts==null || attempts>=5)throw new IllegalArgumentException("Mã đã hết hạn hoặc vượt số lần thử. Vui lòng đăng ký lại.");
    session.setAttribute("otpAttempts",attempts+1);
    if(!java.util.Objects.equals(session.getAttribute("register_otp"),r.getParameter("otp")))throw new IllegalArgumentException("Mã xác thực chưa đúng.");
    User u=new User();u.setEmail((String)session.getAttribute("register_email"));u.setFullname((String)session.getAttribute("register_fullname"));u.setPhone((String)session.getAttribute("register_phone"));u.setPasswd((String)session.getAttribute("register_password"));u.setAdmin(false);new UserDAO().save(u);
    for(String key:new String[]{"register_email","register_fullname","register_phone","register_password","register_otp","otpExpires","otpAttempts"})session.removeAttribute(key);
    session.setAttribute("flash","Tạo tài khoản thành công. Bạn có thể đăng nhập.");s.sendRedirect(r.getContextPath()+"/login");
   }catch(IllegalArgumentException e){r.setAttribute("error",e.getMessage());r.getRequestDispatcher("/verify-otp.jsp").forward(r,s);}
   catch(RuntimeException e){getServletContext().log("Save registration failed",e);r.setAttribute("error","Chưa thể tạo tài khoản. Email có thể đã được đăng ký.");r.getRequestDispatcher("/verify-otp.jsp").forward(r,s);}
  }
 }
}
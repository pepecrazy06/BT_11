package vn.iotstar.controller;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.SecureRandom;
import vn.iotstar.dao.UserDAO;
import vn.iotstar.util.*;
import vn.iotstar.service.CartService;
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{r.getRequestDispatcher("/register.jsp").forward(r,s);}
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  r.setCharacterEncoding("UTF-8");HttpSession session=r.getSession();
  try{
   CartService.validateToken(session,r.getParameter("token"));
   String email=FormUtil.text(r,"email",200,true).toLowerCase(java.util.Locale.ROOT),name=FormUtil.text(r,"fullname",100,true),phone=FormUtil.text(r,"phone",20,true),password=FormUtil.text(r,"password",100,true);
   if(!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))throw new IllegalArgumentException("Email không hợp lệ.");
   if(!phone.matches("[+]?[0-9][0-9 .-]{7,18}"))throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
   if(password.length()<8)throw new IllegalArgumentException("Mật khẩu cần ít nhất 8 ký tự.");
   if(!password.equals(r.getParameter("confirmPassword")))throw new IllegalArgumentException("Mật khẩu nhập lại chưa khớp.");
   if(new UserDAO().exists(email))throw new IllegalArgumentException("Email này đã được đăng ký.");
   String otp=String.valueOf(100000+new SecureRandom().nextInt(900000));
   MailUtil.sendOTP(email,otp);
   session.setAttribute("register_email",email);session.setAttribute("register_fullname",name);session.setAttribute("register_phone",phone);
   session.setAttribute("register_password",PasswordUtil.hash(password));session.setAttribute("register_otp",otp);
   session.setAttribute("otpExpires",System.currentTimeMillis()+300000L);session.setAttribute("otpAttempts",0);
   s.sendRedirect(r.getContextPath()+"/verify-otp");
  }catch(IllegalArgumentException e){r.setAttribute("error",e.getMessage());r.getRequestDispatcher("/register.jsp").forward(r,s);}
  catch(Exception e){getServletContext().log("Registration OTP failed",e);r.setAttribute("error","Chưa gửi được email xác thực. Vui lòng kiểm tra cấu hình email hoặc thử lại sau.");r.getRequestDispatcher("/register.jsp").forward(r,s);}
 }
}
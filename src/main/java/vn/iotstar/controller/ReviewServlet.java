package vn.iotstar.controller;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.iotstar.dao.*;
import vn.iotstar.entity.*;
import vn.iotstar.service.CartService;
import vn.iotstar.util.FormUtil;
@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException{
  HttpSession session=r.getSession();User u=(User)session.getAttribute("account");
  if(u==null){s.sendRedirect(r.getContextPath()+"/login");return;}
  int id=0;
  try{CartService.validateToken(session,r.getParameter("token"));id=FormUtil.id(r,"bookid");Book b=new BookDAO().findById(id);if(b==null)throw new IllegalArgumentException("Sách không tồn tại.");
   int stars=FormUtil.id(r,"rating");if(stars>5)throw new IllegalArgumentException("Đánh giá từ 1 đến 5 sao.");
   Rating rating=new Rating();rating.setBook(b);rating.setUser(u);rating.setRating(stars);rating.setReview_text(FormUtil.text(r,"review_text",1000,true));new RatingDAO().save(rating);
   session.setAttribute("flash","Cảm ơn bạn đã chia sẻ đánh giá.");
  }catch(IllegalArgumentException e){session.setAttribute("flash",e.getMessage());}
  catch(RuntimeException e){getServletContext().log("Review failed",e);session.setAttribute("flash","Chưa lưu được đánh giá. Vui lòng thử lại.");}
  s.sendRedirect(r.getContextPath()+(id>0?"/detail?id="+id:"/home"));
 }
}
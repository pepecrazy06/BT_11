package vn.iotstar.service;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.dao.BookDAO;
import vn.iotstar.entity.*;
import java.util.*;
import java.math.BigDecimal;
public class CartService {
 @SuppressWarnings("unchecked")
 public static Map<Integer,Integer> cart(HttpSession s){
  Map<Integer,Integer> c=(Map<Integer,Integer>)s.getAttribute("cart");
  if(c==null){c=new TreeMap<>();s.setAttribute("cart",c);} return c;
 }
 public static String token(HttpSession s){
  String t=(String)s.getAttribute("cartToken");
  if(t==null){t=UUID.randomUUID().toString();s.setAttribute("cartToken",t);} return t;
 }
 public static void validateToken(HttpSession s,String t){
  if(t==null || !t.equals(token(s)))throw new IllegalArgumentException("Phiên thao tác hết hạn. Vui lòng tải lại trang.");
 }
 public static List<CartItem> items(Map<Integer,Integer> c){
  List<CartItem> result=new ArrayList<>(); BookDAO dao=new BookDAO();
  for(Map.Entry<Integer,Integer> e:c.entrySet()){
   Book b=dao.findById(e.getKey());
   if(b==null)throw new IllegalArgumentException("Sách mã "+e.getKey()+" không còn tồn tại. Hãy xóa khỏi giỏ.");
   if(b.getPrice()==null || b.getPrice().signum()<0)throw new IllegalArgumentException("Sách chưa có giá hợp lệ.");
   result.add(new CartItem(b,e.getValue()));
  }return result;
 }
 public static BigDecimal total(List<CartItem> items){return items.stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);}
 public static int positive(String v){
  try{int n=Integer.parseInt(v);if(n>0)return n;}catch(NumberFormatException ignored){}
  throw new IllegalArgumentException("Mã sách và số lượng phải là số nguyên dương.");
 }
}
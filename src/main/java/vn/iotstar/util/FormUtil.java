package vn.iotstar.util;
import jakarta.servlet.http.HttpServletRequest;
import vn.iotstar.service.CartService;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
public final class FormUtil {
 private FormUtil() {}
 public static String text(HttpServletRequest r,String name,int max,boolean required){
  String v=r.getParameter(name);v=v==null?"":v.trim();
  if((required && v.isEmpty()) || v.length()>max)throw new IllegalArgumentException("Thông tin "+name+" không hợp lệ.");
  return v;
 }
 public static int id(HttpServletRequest r,String name){return CartService.positive(r.getParameter(name));}
 public static int page(String value){try{return Math.max(1,Integer.parseInt(value));}catch(Exception e){return 1;}}
 public static BigDecimal price(String value){try{BigDecimal n=new BigDecimal(value);if(n.signum()>=0 && n.scale()<=2 && n.precision()<=19)return n;}catch(Exception ignored){}throw new IllegalArgumentException("Giá phải là số không âm, tối đa 2 chữ số thập phân.");}
 public static int stock(String value){try{int n=Integer.parseInt(value);if(n>=0)return n;}catch(Exception ignored){}throw new IllegalArgumentException("Tồn kho phải là số nguyên không âm.");}
 public static Date date(String value){if(value==null || value.isBlank())return null;try{SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd");f.setLenient(false);return f.parse(value);}catch(Exception e){throw new IllegalArgumentException("Ngày không hợp lệ.");}}
}
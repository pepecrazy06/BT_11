package vn.iotstar.entity;
import java.math.BigDecimal;
public class CartItem {
 private final Book book; private final int quantity;
 public CartItem(Book b,int q){book=b;quantity=q;}
 public Book getBook(){return book;} public int getQuantity(){return quantity;}
 public int getLimit(){return Math.min(99,book.getQuantity()==null?0:Math.max(0,book.getQuantity()));}
 public BigDecimal getSubtotal(){return book.getPrice().multiply(BigDecimal.valueOf(quantity));}
}
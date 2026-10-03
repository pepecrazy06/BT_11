package vn.iotstar.entity;
import java.util.*;
/** Codes remain compatible with existing orders in SQL Server. */
public enum OrderStatus {
 PENDING("Đơn hàng mới",1), CONFIRMED("Đã xác nhận",2),
 PREPARING("Chuẩn bị hàng",3), SHIPPING("Vận chuyển",4),
 DELIVERING("Giao hàng",5), DELIVERED("Đã giao",6),
 CANCELLED("Đơn hàng hủy",0), RETURNED("Đơn hàng hoàn",0);
 private final String label; private final int step;
 OrderStatus(String label,int step){this.label=label;this.step=step;}
 public String getCode(){return name();}
 public String getLabel(){return label;}
 public int getStep(){return step;}
 public static OrderStatus from(String code){
  if(code==null)return null;
  try{return valueOf(code);}catch(IllegalArgumentException e){return null;}
 }
 public List<OrderStatus> getNextStatuses(){return switch(this){
  case PENDING -> List.of(CONFIRMED,CANCELLED);
  case CONFIRMED -> List.of(PREPARING,CANCELLED);
  case PREPARING -> List.of(SHIPPING,CANCELLED);
  case SHIPPING -> List.of(DELIVERING,RETURNED);
  case DELIVERING -> List.of(DELIVERED,RETURNED);
  case DELIVERED -> List.of(RETURNED);
  default -> List.of();
 };}
}

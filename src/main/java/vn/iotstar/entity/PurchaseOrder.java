package vn.iotstar.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity @Table(name="purchase_orders") @Getter @Setter
public class PurchaseOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Integer userId;
 @Column(nullable=false,columnDefinition="nvarchar(100)") private String recipient;
 @Column(nullable=false,length=20) private String phone;
 @Column(nullable=false,columnDefinition="nvarchar(500)") private String address;
 @Column(nullable=false,length=10) private String paymentMethod="COD";
 @Column(nullable=false,length=30) private String status="PENDING";
 @Column(nullable=false,length=30) private String paymentStatus="UNPAID";
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 @Column(nullable=false,precision=19,scale=2) private BigDecimal total;
 @OneToMany(mappedBy="order",cascade=CascadeType.ALL) private List<OrderItem> items=new ArrayList<>();

 @Transient public String getStatusLabel(){OrderStatus s=OrderStatus.from(status);return s==null?status:s.getLabel();}
 @Transient public String getPaymentLabel(){return "PAID".equals(paymentStatus)?"Đã thu tiền":("REFUND_PENDING".equals(paymentStatus)?"Chờ hoàn tiền":"Chưa thanh toán");}
 @Transient public int getStatusStep(){OrderStatus s=OrderStatus.from(status);return s==null?0:s.getStep();}
 @Transient public List<OrderStatus> getNextStatuses(){OrderStatus s=OrderStatus.from(status);return s==null?List.of():s.getNextStatuses();}
 @Transient public String getCreatedLabel(){return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));}
}
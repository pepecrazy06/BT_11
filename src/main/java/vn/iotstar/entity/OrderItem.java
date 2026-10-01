package vn.iotstar.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Entity @Table(name="purchase_order_items") @Getter @Setter
public class OrderItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="order_id") private PurchaseOrder order;
 @Column(nullable=false) private Integer bookId;
 @Column(nullable=false,columnDefinition="nvarchar(200)") private String title;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal unitPrice;
 @Column(nullable=false) private int quantity;
 public BigDecimal getSubtotal(){return unitPrice.multiply(BigDecimal.valueOf(quantity));}
}
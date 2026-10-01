package vn.iotstar.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="rating")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    private Integer rating;



    @Column(columnDefinition = "nvarchar(1000)")
    private String review_text;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userid")
    private User user;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bookid")
    private Book book;


}
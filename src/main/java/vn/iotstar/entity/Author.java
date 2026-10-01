package vn.iotstar.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@Entity
@Table(name="author")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer author_id;


    @Column(columnDefinition = "nvarchar(200)")
    private String author_name;


    @Temporal(TemporalType.DATE)
    private Date date_of_birth;



    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

}
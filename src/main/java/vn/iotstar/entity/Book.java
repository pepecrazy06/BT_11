package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookid;

	@Column(columnDefinition = "nvarchar(255)")
	private String isbn;

	@Column(columnDefinition = "nvarchar(200)")
	private String title;

	@Column(columnDefinition = "nvarchar(255)")
	private String publisher;

	private BigDecimal price;

	@Column(columnDefinition = "nvarchar(2000)")
	private String description;

	@Temporal(TemporalType.DATE)
	private Date publish_date;

	@Column(columnDefinition = "nvarchar(255)")
	private String cover_image;

	private Integer quantity;

	@ManyToMany(fetch = FetchType.EAGER)
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	@JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "bookid"), inverseJoinColumns = @JoinColumn(name = "author_id"))
	private List<Author> authors;

	@OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	private List<Rating> ratings;

}
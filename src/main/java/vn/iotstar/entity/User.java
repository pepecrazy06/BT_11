package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "nvarchar(100)")
    private String fullname;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String passwd;

    @Column(name = "admin")
    private boolean admin;

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;
}
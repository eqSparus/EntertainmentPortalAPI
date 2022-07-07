package ru.portal.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс сущности пользователя из БД
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "users", catalog = "db_portal", schema = "portal_shem",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        },
        indexes = {
                @Index(columnList = "user_id"),
                @Index(columnList = "username"),
                @Index(columnList = "email")
        })
public class User implements Serializable {

    @Id
    @Column(name = "user_id", unique = true)
    @GeneratedValue(generator = "increment")
    @org.hibernate.annotations.GenericGenerator(name = "increment", strategy = "increment")
    Long id;


    @Column(name = "username", length = 32, unique = true)
    String username;

    @Column(name = "email", length = 64, unique = true)
    String email;

    @Column(name = "password", length = 256)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    Role role;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "create_at", updatable = false)
    ZonedDateTime createAt;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "update_at")
    ZonedDateTime updateAt;

    public User(String username, String email,
                String password, Status status, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
    }
}

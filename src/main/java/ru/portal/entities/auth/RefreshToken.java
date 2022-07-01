package ru.portal.entities.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.portal.entities.User;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс сущности токена обновления из БД.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens", schema = "portal_shem", catalog = "db_portal",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "token")
        },
        indexes = {
                @Index(columnList = "token"),
                @Index(columnList = "token_id")
        })
public class RefreshToken implements Serializable {

    @Id
    @Column(name = "token_id", unique = true)
    @GeneratedValue(generator = "increment", strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.GenericGenerator(name = "increment", strategy = "increment")
    Long id;

    @Column(name = "token", length = 80, unique = true)
    String token;

    @Column(name = "lifetime")
    Long lifetime;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    User user;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "create_at", updatable = false)
    ZonedDateTime createAt;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "update_at")
    ZonedDateTime updateAt;

    public RefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

}

package ru.portal.entities.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.portal.entities.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс сущность токена подтверждения пользователя по электронной почты из БД.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "confirmation_tokens", schema = "portal_shem", catalog = "db_portal",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "token"),
                @UniqueConstraint(columnNames = "user_id"),
                @UniqueConstraint(columnNames = "confirmation_id")
        },
        indexes = {
                @Index(columnList = "token"),
                @Index(columnList = "confirmation_id"),
                @Index(columnList = "user_id")
        })
public class ConfirmationToken implements Serializable {

    @Id
    @Column(name = "confirmation_id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "increment")
    @org.hibernate.annotations.GenericGenerator(name = "increment", strategy = "increment")
    Long id;

    @Column(name = "token", length = 60, unique = true)
    String token;

    @Column(name = "lifetime")
    Long lifetime;

    @OneToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", updatable = false, nullable = false, unique = true)
    User user;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "create_at", updatable = false)
    ZonedDateTime createAt;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "update_at")
    ZonedDateTime updateAt;

}

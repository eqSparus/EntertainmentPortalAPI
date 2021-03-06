package ru.portal.entities.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.portal.entities.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;


/**
 * Класс сущности подсчета попыток входа из БД.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attempts_login", schema = "portal_schema",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id")
        },
        indexes = {
                @Index(columnList = "user_id"),
                @Index(columnList = "attempt_id")
        })
@org.hibernate.annotations.DynamicInsert
public class LoginAttempt implements Serializable {

    @Id
    @Column(name = "attempt_id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "increment")
    @org.hibernate.annotations.GenericGenerator(name = "increment", strategy = "increment")
    Long id;

    @Column(name = "number_attempts")
    @org.hibernate.annotations.ColumnDefault("0")
    Integer numberAttempt;

    @Column(name = "lock_time")
    @org.hibernate.annotations.ColumnDefault("0")
    Long lockTime;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "create_at", updatable = false)
    ZonedDateTime createAt;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "update_at")
    ZonedDateTime updateAt;

    @OneToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", updatable = false, nullable = false, unique = true)
    User user;

    public LoginAttempt(Integer numberAttempt, User user) {
        this.numberAttempt = numberAttempt;
        this.user = user;
    }

}

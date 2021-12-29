package ru.idc.labgatej.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность пользователя.
 *
 * @author Roman Perminov.
 */
@Entity
@Table(name = "users", schema = "driver")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "jpaDriverUserSequence",
    allocationSize = 1,
    schema = "driver",
    sequenceName = "driver_user_sequence"
)
public class User
{
    /**
     * Идентификатор экземпляра пользователя.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "jpaDriverUserSequence"
    )
    Long id;

    /**
     * Имя пользователя.
     */
    @NotBlank
    @Size(max = 20)
    private String username;

    /**
     * Электронный почтовый адрес.
     */
    @NotBlank
    @Size(max = 50)
    private String email;

    /**
     * Пароль.
     */
    @NotBlank
    @Size(max = 120)
    private String password;

    /**
     * Набор ролей.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", schema = "driver",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Создает сущность пользователя.
     *
     * @param username
     *        имя пользователя.
     * @param email
     *        электронный почтовый ящик.
     * @param password
     *        пароль.
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

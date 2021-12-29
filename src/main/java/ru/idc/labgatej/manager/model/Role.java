package ru.idc.labgatej.manager.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Сущность роли пользователя.
 */
@Entity
@Table(name = "roles", schema = "driver")
@SequenceGenerator(
    name = "jpaDriverRoleSequence",
    allocationSize = 1,
    schema = "driver",
    sequenceName = "driver_role_sequence"
)
@Data
public class Role
{
    /**
     * Идентификатор экземпляра роли.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "jpaDriverRoleSequence"
    )
    Long id;

    /**
     * Имя Роли.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
}

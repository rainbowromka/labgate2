package ru.idc.labgatej.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Сущность параметра драйвера.
 */
@Entity
@Table(name = "parameters", schema = "driver")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "jpaDriverParameterSequence",
    allocationSize = 1,
    schema = "driver",
    sequenceName = "driver_parameter_sequence"
)
public class DriverParameter
{
    /**
     * Идентификатор параметра.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "jpaDriverParameterSequence"
    )
    private Long id;

    /**
     * Имя параметра.
     */
    String name;

    /**
     * Значение параметра.
     */
    String value;
}

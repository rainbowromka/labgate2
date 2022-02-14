package ru.idc.labgatej.manager.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Сущность записи журнала.
 */
@Entity
@Table(name = "drivers_manager_log", schema = "driver")
@SequenceGenerator(
    name = "jpaDriversManagerLog",
    allocationSize = 1,
    schema = "driver",
    sequenceName = "driver_manager_log_sequence"
)
@Data
public class LogEntity
{
    /**
     * Идентификатор записи журнла.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "jpaDriversManagerLog"
    )
    Long id;

//    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp logDate;

//    Long numberString;

//    @ManyToOne(fetch = FetchType.EAGER)
    @Column(name = "driver_instance_id")
    Long driverInstance;

    String message;
}

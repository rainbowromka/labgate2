package ru.idc.labgatej.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Сущность экземпляра драйвера.
 */
@Entity
@Table(name = "entity", schema = "driver")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "jpaDriverEntitySequence",
    allocationSize = 1,
    schema = "driver",
    sequenceName = "driver_entity_sequence"
)
public class DriverEntity
{
    /**
     * Идентификатор экземпляра драйвера.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "jpaDriverEntitySequence"
    )
    Long id;

    /**
     * Имя драйвера.
     */
    String name;

    /**
     * Код драйвера.
     */
    String code;

    /**
     * Тип драйвера.
     */
    @Enumerated(EnumType.STRING)
    DriverType type;

    @Enumerated(EnumType.STRING)
    DriverStatus status;

    /**
     * Параметры драйвера.
     */
    @OneToMany(
        cascade = {CascadeType.ALL},
        orphanRemoval = true
    )
    @JoinColumn(name = "driver_entity_id")
    @MapKeyColumn(name = "name")
//    List<DriverParameter> parameters = new ArrayList<>();
    Map<String, DriverParameter> parameters = new TreeMap<>();

    public void addDriverParameter(String key, DriverParameter driverParameter)
    {
//        parameters.add(driverParameter);
        parameters.put(key, driverParameter);
//        driverParameter.setDriverEntity(this);
    }

    public void removeDriverParameter(String key/*DriverParameter driverParameter*/)
    {
        parameters.remove(key/*driverParameter*/);
//        driverParameter.setDriverEntity(null);
    }
}

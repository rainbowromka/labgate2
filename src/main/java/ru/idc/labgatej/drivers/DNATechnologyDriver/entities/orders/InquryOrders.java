package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Заявка на исследование. Элемент должен быть вложен непосредственно в корневой
 * элемент. В заказе может быть несколько заявок.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class InquryOrders
{
    /**
     * Обязательный атрибут. Идентификатор заявки внутри системы. Должен быть
     * уникален в рамках обмена, т. е. в рамках всего множества заявок,
     * передаваемых Интегратору из ЛИС. Строковое поле, макс. длина 40,
     * сaseinsensitive, без лидирующих и хвостовых пробелов.
     */
    @XmlAttribute(name = "ID", required = true)
    private String id;

    /**
     * Необязательный атрибут. Идентификатор заявки внутри ЛИС (если есть).
     * Может передаваться при настройке или поиске проблем обмена.
     */
    @XmlAttribute(name = "RequestID")
    private String requestID;

    /**
     * Необязательный атрибут.
     */
    @XmlAttribute(name = "OrgID")
    private String orgID;


    /**
     * Необязательный атрибут. Фамилия пациента.
     */
    @XmlAttribute(name = "LastName")
    private String lastName;

    /**
     * Необязательный атрибут. Имя пациента.
     */
    @XmlAttribute(name = "FirstName")
    private String firstName;

    /**
     * Необязательный атрибут. Отчество пациента.
     */
    @XmlAttribute(name = "MiddleName")
    private String middleName;

    /**
     * Необязательный атрибут. Пол пациента, 1-мужской, 2-женский.
     */
    @XmlAttribute(name = "Sex")
    private String sex;

    /**
     * Необязательный атрибут. День рождения пациента.
     */
    @XmlAttribute(name = "BirthDay")
    private String birthDay;

    /**
     * Необязательный атрибут. Месяц рождения пациента.
     */
    @XmlAttribute(name = "BirthMonth")
    private String birthMonth;

    /**
     * Необязательный атрибут. Год рождения пациента.
     */
    @XmlAttribute(name = "BirthYear")
    private String birthYear;

    /**
     * Необязательный атрибут. Зарезервировано для будущего применения (для
     * скачивания "новых" заявок (например, с веб-сервиса). В запросе отправляем
     * MAX(timestamp), и сервер присылает все заявки, созданные/обновленные
     * после указанного момента).
     */
    @XmlAttribute(name = "TimeStamp")
    private String timeStamp;


    /**
     * Описание образца.
     */
    @XmlElement(name = "Sample")
    private SampleOrder sampleOrder;
}

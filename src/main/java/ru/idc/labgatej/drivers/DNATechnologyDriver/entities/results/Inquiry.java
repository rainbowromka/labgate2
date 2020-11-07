package ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Заявка на исследование. В заказе может быть несколько заявок.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Inquiry
{
    /**
     * Идентификатор заявки в ЛИС, полученный при импорте заявки. Строковое
     * поле, макс. длина 40, сaseinsensitive, без лидирующих и хвостовых
     * пробелов.
     */
    @XmlAttribute(name = "ID")
    private String id;

    /**
     * Описание образца.
     */
    @XmlElement( name = "Sample")
    private Sample sample;

//    SourceID="151"
//    RequestID="151"
//    LastName="Louis"
//    FirstName="Natalie"
//    Sex="2"
//    PatientID="c1a37b7065d742668fbb59a508e61b2a"
//    CaseNo="78499581accd4e798e5b7ebd30f964ad"
//    Received="2020-01-27T10:58:06.613"
//    ResultsReceived="2020-01-27T11:03:07.837"
}

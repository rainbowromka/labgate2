package ru.idc.labgatej.model.KdlMax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.idc.labgatej.model.OrderClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Объект заказа тестов протокола ASTM (КДЛ-Макс)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AstmOrder extends OrderClass implements ASTMRecord
{

    /**
     * Sequence #. Порядковый номер записи (согласно протоколу ASTM)
     */
    Long sequence;

    /**
     * Specimen ID. Идентификатор образца.
     */
    String specimenID;

    /**
     * Universal Test ID. Код методики, по которой необходимо анализировать
     * образец в формате «^^^Код». Зада-ется в справочнике методик амплификации.
     */
    String universalTestID;

    /**
     * Priority. 1 – Cito; 0 – обычный приоритет
     */
    String cito;

    /**
     * Requested/Ordered Date and Time.
     */
    Date orderedDateTime;

    /**
     * Specimen Collection Date and Time
     */
    Date specimenCollectionDateTime;

    /**
     * Specimen Descriptor. Код биоматериала, как он определен в справоч-нике
     * методик амплификации
     */
    String specimenDescriptor;

    /**
     * Ordering Physician.
     */
    String orderingPhysician;

    /**
     * Users Field No. 2.
     */
    String usersFieldNo2;

    /**
     * Lab Field No. 1.
     */
    String labFieldNo1;

    /**
     * Report Types.
     */
    String reportTypes;

    /**
     * Результаты исследований.
     */
    List<TestResult> testResults;

    /**
     * Получить тип записи. Всегда возвращает "O" для данного типа.
     *
     * @return тип записи. Всегда возвращает "O" для данного типа.
     */
    @Override
    public String getType() {
        return "O";
    }

    /**
     * Получает список результатов исследований. Ленивая инициализация.
     *
     * @return список результатов исследований.
     */
    public List<TestResult> getTestResults()
    {
        if (testResults == null) {
            testResults = new ArrayList<>();
        }
        return testResults;
    }

    /**
     * Получить строку заказа тестов в формате протокола ASTM.
     *
     * @return строка заказа тестов в формате протокола ASTM.
     */
    @Override
    public String toASTM() {

        SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        String orderedDateTimeInfo = "";
        if (getOrderedDateTime() != null) {
            orderedDateTimeInfo = dtf.format(getOrderedDateTime());
        }

        String specimenCollectionDateTimeInfo = "";
        if (getSpecimenCollectionDateTime() != null) {
            specimenCollectionDateTimeInfo = df
                .format(getSpecimenCollectionDateTime());
        }

        return new StringBuilder()
            .append(getType()).append("|")
            .append(Objects.toString(getSequence(), "")).append("|")
            .append(Objects.toString(getSpecimenID(), "")).append("||")
            .append(Objects.toString(getUniversalTestID(), "")).append("|")
            .append(Objects.toString(getCito(),"")).append("|")
            .append(orderedDateTimeInfo).append("|")
            .append(specimenCollectionDateTimeInfo).append("||||||||")
            .append(Objects.toString(getSpecimenDescriptor(), "")).append("|")
            .append(Objects.toString(getOrderingPhysician(), "")).append("|||")
            .append(Objects.toString(getUsersFieldNo2(), "")).append("|")
            .append(Objects.toString(getLabFieldNo1(), "")).append("|||||")
            .append(Objects.toString(getReportTypes(), "")).append("|||||<CR>")
            .toString();
    }
}

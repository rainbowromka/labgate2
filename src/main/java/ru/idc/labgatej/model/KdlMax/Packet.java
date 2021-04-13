package ru.idc.labgatej.model.KdlMax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.idc.labgatej.model.KdlMax.parser.ASTMHeaderParser;
import ru.idc.labgatej.model.KdlMax.parser.ASTMMessageTerminationParser;
import ru.idc.labgatej.model.KdlMax.parser.ASTMOrderParser;
import ru.idc.labgatej.model.KdlMax.parser.ASTMParser;
import ru.idc.labgatej.model.KdlMax.parser.ASTMPatientParser;
import ru.idc.labgatej.model.KdlMax.parser.ASTMResultParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.idc.labgatej.base.Codes.ETB_;

/**
 * Объект всего пакета протокола ASTM (КДЛ-Макс)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Packet implements ASTMRecord
{
    /**
     * Access Password. Пароль для обмена данными.
     */
    String accessPassword;

    /**
     * Sender Name or ID. Идентификатор системы для обмена данными.
     */
    String senderNameId;

    /**
     * Receiver ID. Идентификатор ЛИС.
     */
    String receiverId;

    /**
     * Processing ID. "P".
     */
    String processingId;

    /**
     * Message Date + Time. Дата и время отправки сообщения в формате
     * YYYYMMDDHHMMSS.
     */
    Date messageDateTime;

    /**
     * Пациенты.
     */
    List<Patient> patients;

    /**
     * Завешающая часть пакета.
     */
    MessageTerminator messageTerminator;

    /**
     * Получить данные протокола из строки.
     *
     * @param lines
     *        список строк протокола.
     * @param aStrIdx
     *        номер строки, с которой необходимо продолжать анализ входящего
     *        сообщения.
     * @return объект пакета, содержащего данные протокола.
     */
    public int parseHeader(String[] lines, int aStrIdx) throws Exception {

        ASTMHeaderParser headerParser = ASTMHeaderParser
            .init(lines[aStrIdx], this);
        headerParser.parse();

        if (headerParser.getError() != ASTMParser.AstmParseError.NONE) {
            throw new Exception("Ошибка формата заголовка ["+ aStrIdx +"]");
        }

        aStrIdx++;
        boolean isFirst = true;
        while (aStrIdx < lines.length)
        {
            if ("P".equals(getRecordType(aStrIdx, lines[aStrIdx])))
            {
                isFirst = false;

                ASTMPatientParser patientParser = ASTMPatientParser
                    .init(lines[aStrIdx]);
                Patient patient = patientParser.parse();

                if (patientParser.getError() != ASTMParser.AstmParseError.NONE)
                {
                    throw new Exception("Ошибка формата записи пациента ["
                        + aStrIdx + "]");
                }
                getPatients().add(patient);

                aStrIdx = parseOrders(++aStrIdx, lines, patient);

            }
            else if (isFirst)
            {
                throw new Exception("Ожидалась запись пациента \"P\" ["
                    + aStrIdx + "]");
            }
            else
            {
                break;
            }
        }

        if ("L".equals(getRecordType(aStrIdx, lines[aStrIdx])))
        {
            ASTMMessageTerminationParser messageTerminationParser =
                ASTMMessageTerminationParser.init(lines[aStrIdx]);
            MessageTerminator terminator = messageTerminationParser.parse();
            setMessageTerminator(terminator);
        }
        else
        {
            throw new Exception("Ожидалась завершающая запись \"L\" ["
                + aStrIdx + "]");
        }

        return aStrIdx;
    }

    private int parseOrders(int aStrIdx, String[] lines, Patient patient)
    throws Exception
    {
        boolean isFirst = true;
        while (aStrIdx < lines.length)
        {
            if ("O".equals(getRecordType(aStrIdx, lines[aStrIdx])))
            {
                isFirst = false;
                ASTMOrderParser orderParser = ASTMOrderParser
                    .init(lines[aStrIdx]);
                AstmOrder astmOrder = orderParser.parse();
                if (orderParser.getError() != ASTMParser.AstmParseError.NONE)
                {
                    throw new Exception("Ошибка чтения записи заказа ["
                        + aStrIdx + "]");
                }
                patient.getAstmOrders().add(astmOrder);
                aStrIdx = parseResult(++aStrIdx, lines, astmOrder);

            }
            else if (isFirst)
            {
                throw new Exception("Ожидалась запись заказа \"O\" ["
                    + aStrIdx + "]");
            }
            else
            {
                break;
            }
        }

        return aStrIdx;
    }

    private int parseResult(int aStrIdx, String[] lines, AstmOrder astmOrder)
    throws Exception
    {
        boolean isFirst = true;
        while (aStrIdx < lines.length)
        {
            if ("R".equals(getRecordType(aStrIdx, lines[aStrIdx])))
            {
                isFirst = false;
                ASTMResultParser resultParser = ASTMResultParser
                    .init(lines[aStrIdx]);
                TestResult testResult = resultParser.parse();
                if (resultParser.getError() != ASTMParser.AstmParseError.NONE)
                {
                    throw new Exception("Ошибка чтения записи результатов " +
                        "тестов [" + aStrIdx + "]");
                }
                astmOrder.getTestResults().add(testResult);
            }
            else if (isFirst)
            {
                throw new Exception("Ожидалась запись результата \"R\" ["
                    + aStrIdx + "]");
            }
            else
            {
                break;
            }
            aStrIdx++;
        }

        return aStrIdx;
    }


    /**
     * Получить данные протокола из строки.
     *
     * @param msg
     * @return объект пакета, содержащего данные протокола.
     * @throws Exception в случае ошибки формата ASTM протокола.
     */
    public static Packet parseAstmPacket(String msg)
    throws Exception
    {
        int idx;
        while (msg.indexOf(""+ ETB_) > 0) {
            idx = msg.indexOf(""+ ETB_) + 6;
            if (idx > msg.length()) {
                idx = msg.length() - 1;
            }
            msg = msg.replace(msg.substring(msg.indexOf(""+ ETB_), idx), "");
        }

        String[] lines = msg.replace(""+ ETB_, "")
            .replace("\r\n", "\r").split("\r");

        Packet packet = new Packet();

        if (lines.length == 0) {
            return packet;
        }

        int strIdx = 0;
        if ("H".equals(getRecordType(strIdx, lines[0])))
        {
            strIdx = packet.parseHeader(lines, strIdx);
        } else {
            throw new Exception("Ошибка. Ожидается заголовок: ["
                + strIdx + "]");
        }

        return packet;
    }

    private static String getRecordType(int strIdx, String line)
    throws Exception
    {
        final Pattern pattern = Pattern.compile("^(.)", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);
        Matcher matcher;
        matcher = pattern.matcher(line);
        if (!matcher.find())
        {
            throw new Exception("[Строка: " + strIdx + "] Неверный формат" +
                " строки.");
        }
        return  matcher.group(1);
    }

    /**
     * Получить тип записи. Всегда возвращает "H" для данного типа.
     *
     * @return тип записи. Всегда возвращает "H" для данного типа.
     */
    @Override
    public String getType()
    {
        return "H";
    }

    /**
     * @return разделитель. Всегда возвращает "\^&".
     */
    public String getDelimiter()
    {
        return "\\^&";
    }

    /**
     * Получает список пациентов. Ленивая инициализация.
     *
     * @return список пациентов.
     */
    public List<Patient> getPatients()
    {
        if (patients == null) {
            patients = new ArrayList<>();
        }

        return patients;
    }

    /**
     * Получает объект завершающей части пакета.
     *
     * @return объект завершающей части пакета.
     */
    public MessageTerminator getMessageTerminator()
    {
        if (messageTerminator == null)
        {
            messageTerminator = new MessageTerminator();
        }

        return messageTerminator;
    }

    /**
     * Получить строку всего пакета в формате протокола ASTM.
     *
     * @return строка всего пакета в формате протокола ASTM.
     */
    @Override
    public String toASTM() {

        String messageDate = "";
        if (getMessageDateTime() != null) {
            messageDate = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(getMessageDateTime());
        }
//        if (getMessageDateTime() != null) {
//            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//            messageDate = dtf.format(getMessageDateTime());
//        }

        StringBuilder msg = new StringBuilder(getType()).append("|")
                .append(getDelimiter()).append("||")
                .append(Objects.toString(getAccessPassword(), "")).append("|")
                .append(Objects.toString(getSenderNameId(), "")).append("|||||")
                .append(Objects.toString(getReceiverId(), "")).append("||")
                .append(Objects.toString(getProcessingId(), "")).append("||")
                .append(messageDate).append("<CR>");

        for (Patient patient: getPatients())
        {
            msg.append(patient.toASTM());
        }

        return msg.append(getMessageTerminator().toASTM()).toString();
    }

}

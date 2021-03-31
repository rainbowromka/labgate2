package ru.idc.labgatej.model.KdlMax.parser;

import ru.idc.labgatej.model.KdlMax.Packet;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ASTMHeaderParser extends ASTMParser<Packet>
{
    protected ASTMHeaderParser(String[] records, int idx, Packet record) {
        super(records, idx, record);
    }

    public static ASTMHeaderParser init(String records)
    {
        return new ASTMHeaderParser(records.split("\\|"), 0, new Packet());
    }

    public static ASTMHeaderParser init(String records, Packet packet)
    {
        return new ASTMHeaderParser(records.split("\\|"), 0, packet);
    }

    public ASTMHeaderParser skip(int times)
    {
        for (int q = 0; q < times; q++) {
            getNextValue();
        }
        return this;
    }

    public ASTMHeaderParser checkType()
    {
        if (!"H".equals(getNextValue())) {
            error = AstmParseError.RECORD_TYPE;
        }
        return this;
    }

    private ASTMHeaderParser checkDelimiter() {
        if (!"\\^&".equals(getNextValue())) {
            error = AstmParseError.RECORD_TYPE;
        }
        return this;
    }

    public ASTMHeaderParser setAccessPassword()
    {
        record.setAccessPassword(getNextValue());
        return this;
    }

    public ASTMHeaderParser setSenderNameId()
    {
        record.setSenderNameId(getNextValue());
        return this;
    }

    public ASTMHeaderParser setReceiverId()
    {
        record.setReceiverId(getNextValue());
        return this;
    }

    public ASTMHeaderParser setProcessingId()
    {
        record.setProcessingId(getNextValue());
        return this;
    }

    public ASTMHeaderParser setMessageDateTime()
    {
        try {
            record.setMessageDateTime(new SimpleDateFormat("yyyyMMddHHmmss")
                .parse(getNextValue()));
        } catch (ParseException e) {
            setError(AstmParseError.OTHER_TYPE);
        }
       return this;
    }

    public Packet parse()
    {
        return checkType()
            .checkDelimiter().skip(1)
            .setAccessPassword()
            .setSenderNameId().skip(4)
            .setReceiverId().skip(1)
            .setProcessingId().skip(1)
            .setMessageDateTime()
            .getRecord();
    }
}

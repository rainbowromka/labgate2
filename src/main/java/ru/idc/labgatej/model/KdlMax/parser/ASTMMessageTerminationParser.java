package ru.idc.labgatej.model.KdlMax.parser;

import ru.idc.labgatej.model.KdlMax.MessageTerminator;
import ru.idc.labgatej.model.KdlMax.Patient;

public class ASTMMessageTerminationParser extends ASTMParser<MessageTerminator>
{

    protected ASTMMessageTerminationParser(String[] records, int idx, MessageTerminator record) {
        super(records, idx, record);
    }

    public static ASTMMessageTerminationParser init(String records)
    {
        return new ASTMMessageTerminationParser(records.split("\\|"), 0, new MessageTerminator());
    }

    public static ASTMMessageTerminationParser init(String records, MessageTerminator record)
    {
        return new ASTMMessageTerminationParser(records.split("\\|"), 0, record);
    }

    public ASTMMessageTerminationParser skip(int times)
    {
        for (int q = 0; q < times; q++) {
            getNextValue();
        }
        return this;
    }

    public ASTMMessageTerminationParser checkType()
    {
        if (!"L".equals(getNextValue())) {
            error = AstmParseError.RECORD_TYPE;
        }
        return this;
    }

    public ASTMMessageTerminationParser setSequence()
    {
        try
        {
            record.setSequence(Long.parseLong(getNextValue()));
        }
        catch (NumberFormatException e)
        {
            setError(AstmParseError.OTHER_TYPE);
        }
        return this;
    }

    public ASTMMessageTerminationParser setTerminationCode()
    {
        record.setTerminationCode(getNextValue());
        return this;
    }

    public MessageTerminator parse()
    {
        return checkType()
                .setSequence()
                .setTerminationCode()
                .getRecord();
    }
}

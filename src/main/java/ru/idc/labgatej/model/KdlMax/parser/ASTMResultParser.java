package ru.idc.labgatej.model.KdlMax.parser;

import ru.idc.labgatej.model.KdlMax.TestResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ASTMResultParser extends ASTMParser <TestResult>
{
    protected ASTMResultParser(String[] records, int idx, TestResult record) {
        super(records, idx, record);
    }

    public static ASTMResultParser init(String records)
    {
        return new ASTMResultParser(records.split("\\|"), 0, new TestResult());
    }

    public ASTMResultParser skip(int times)
    {
        for (int q = 0; q < times; q++) {
            getNextValue();
        }
        return this;
    }

    public ASTMResultParser checkType()
    {
        if (!Pattern.compile(
                    "\\d*R", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
                .matcher(getNextValue()).find())
        {
            error = AstmParseError.RECORD_TYPE;
        }
        return this;
    }

    public ASTMResultParser setSequence()
    {
        try
        {
            record.setSequence(Long.parseLong(getNextValue()));
        }
        catch (NumberFormatException e)
        {
            error = AstmParseError.OTHER_TYPE;
        }
        return this;
    }

    public ASTMResultParser setUniversalTestId()
    {
        record.setUniversalTestId(getNextValue());
        return this;
    }

    public ASTMResultParser setValue()
    {
        record.setValue(getNextValue());
        return this;
    }

    public ASTMResultParser setUnits()
    {
        record.setUnits(getNextValue());
        return this;
    }

    public ASTMResultParser setResultStatus()
    {
        record.setResultStatus(getNextValue());
        return this;
    }

    public ASTMResultParser setOperatorName()
    {
        record.setOperatorName(getNextValue());
        return this;
    }

    public ASTMResultParser setTestStarted()
    {
        try
        {
            String recordValue = getNextValue();
            if (recordValue != null)
            {
                record.setTestStarted(new SimpleDateFormat("yyyyMMddHHmmss")
                    .parse(recordValue));
            }
        }
        catch (ParseException e)
        {
            setError(AstmParseError.OTHER_TYPE);
        }
        return this;
    }

    public ASTMResultParser setTestCompleted()
    {
        try
        {
            String recordValue = getNextValue();
            if (recordValue != null)
            {
                record.setTestCompleted(new SimpleDateFormat("yyyyMMddHHmmss")
                    .parse(recordValue));
            }
        }
        catch (ParseException e)
        {
            setError(AstmParseError.OTHER_TYPE);
        }
        return this;
    }

    public ASTMResultParser setInstrumentId()
    {
        record.setInstrumentId(getNextValue());
        return this;
    }

    public TestResult parse()
    {
        return checkType()
                .setSequence()
                .setUniversalTestId()
                .setValue()
                .setUnits().skip(3)
                .setResultStatus().skip(1)
                .setOperatorName()
                .setTestStarted()
                .setTestCompleted()
                .setInstrumentId()
                .getRecord();
    }


}

package ru.idc.labgatej.model.KdlMax.parser;

import ru.idc.labgatej.model.KdlMax.AstmOrder;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ASTMOrderParser extends ASTMParser<AstmOrder>
{

    protected ASTMOrderParser(String[] records, int i, AstmOrder astmOrder) {
        super(records, i, astmOrder);
    }

    public static ASTMOrderParser init(String records)
    {
        return new ASTMOrderParser(records.split("\\|"), 0, new AstmOrder());
    }

    public ASTMOrderParser setSequence()
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

    public ASTMOrderParser setSpecimenID()
    {
        record.setSpecimenID(getNextValue());
        return this;
    }

    public ASTMOrderParser setUniversalTestID()
    {
        record.setUniversalTestID(getNextValue());
        return this;
    }

    public ASTMOrderParser setCito()
    {
        record.setCito(getNextValue());
        return this;
    }

    public ASTMOrderParser setOrderedDateTime()
    {
        try
        {
            record.setOrderedDateTime(new SimpleDateFormat("yyyyMMddHHmmss")
                .parse(getNextValue()));
        }
        catch (ParseException e)
        {
            setError(AstmParseError.OTHER_TYPE);
        }

        return this;
    }

    public ASTMOrderParser setSpecimenCollectionDateTime()
    {
        try
        {
            record.setSpecimenCollectionDateTime(new SimpleDateFormat("yyyyMMdd").parse(getNextValue()));
        }
        catch (ParseException e)
        {
//            setError(AstmParseError.OTHER_TYPE);
        }
        return this;
    }

    public ASTMOrderParser setSpecimenDescriptor()
    {
        record.setSpecimenDescriptor(getNextValue());
        return this;
    }

    public ASTMOrderParser setOrderingPhysician()
    {
        record.setOrderingPhysician(getNextValue());
        return this;
    }

    public ASTMOrderParser setUsersFieldNo2()
    {
        record.setUsersFieldNo2(getNextValue());
        return this;
    }

    public ASTMOrderParser setLabFieldNo1()
    {
        record.setLabFieldNo1(getNextValue());
        return this;
    }

    public ASTMOrderParser setReportTypes()
    {
        record.setReportTypes(getNextValue());
        return this;
    }


    public ASTMOrderParser skip(int times)
    {
        for (int q = 0; q < times; q++) {
            getNextValue();
        }
        return this;
    }

    public ASTMOrderParser checkType()
    {
        if (!"O".equals(getNextValue())) {
            setError(AstmParseError.RECORD_TYPE);
        }
        return this;
    }

    public AstmOrder parse()
    {
        return checkType()
            .setSequence()
            .setSpecimenID().skip(1)
            .setUniversalTestID()
            .setCito()
            .setOrderedDateTime()
            .setSpecimenCollectionDateTime().skip(7)
            .setSpecimenDescriptor()
            .setOrderingPhysician().skip(2)
            .setUsersFieldNo2()
            .setLabFieldNo1().skip(4)
            .setReportTypes()
            .getRecord();
    }
}

package ru.idc.labgatej.model.KdlMax.parser;

import ru.idc.labgatej.model.KdlMax.Patient;

public class ASTMPatientParser extends ASTMParser<Patient> {
    protected ASTMPatientParser(String[] records, int idx, Patient record) {
        super(records, idx, record);
    }

    public static ASTMPatientParser init(String records)
    {
        return new ASTMPatientParser(records.split("\\|"), 0, new Patient());
    }

    public static ASTMPatientParser init(String records, Patient patient)
    {
        return new ASTMPatientParser(records.split("\\|"), 0, patient);
    }

    public ASTMPatientParser skip(int times)
    {
        for (int q = 0; q < times; q++) {
            getNextValue();
        }
        return this;
    }

    public ASTMPatientParser checkType()
    {
        if (!"P".equals(getNextValue())) {
            error = AstmParseError.RECORD_TYPE;
        }
        return this;
    }

    public ASTMPatientParser setSequence()
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

    public Patient parse()
    {
        return checkType()
            .setSequence()
            .getRecord();
    }
}

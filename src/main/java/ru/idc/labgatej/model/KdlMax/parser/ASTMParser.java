package ru.idc.labgatej.model.KdlMax.parser;

import lombok.Data;

@Data
public abstract class ASTMParser<T> {

    private String[] records;
    private int idx;
    protected T record;
    protected ASTMOrderParser.AstmParseError error = ASTMOrderParser.AstmParseError.NONE;

    protected ASTMParser(
            String[] records,
            int idx,
            T record)
    {
        this.records = records;
        this.idx = idx;
        this.record = record;
    }

    protected String getNextValue() {
        String result = null;
        if (idx < records.length) {
            result = records[idx];
        }
        idx++;
        return result;
    }

    public enum AstmParseError {
        NONE,
        RECORD_TYPE,
        OTHER_TYPE
    }
}

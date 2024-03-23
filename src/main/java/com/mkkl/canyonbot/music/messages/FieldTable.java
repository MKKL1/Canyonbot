package com.mkkl.canyonbot.music.messages;

import discord4j.core.spec.EmbedCreateFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//For now it will stay here, but it's not really working. Columns tend to go under the table
public class FieldTable {
    private final List<String> fieldTableHeader;
    private final List<String> fieldValues;

    public FieldTable(List<String> fieldTableHeader, List<String> fieldValues) {
        this.fieldTableHeader = fieldTableHeader;
        this.fieldValues = fieldValues;
    }

    public List<EmbedCreateFields.Field> getFields() {
        List<EmbedCreateFields.Field> fields = new ArrayList<>();
        for (int i = 0; i < fieldTableHeader.size(); i++) {
            fields.add(EmbedCreateFields.Field.of(fieldTableHeader.get(i), fieldValues.get(i), true));
        }
        return fields;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<String> fieldTableHeaders = new ArrayList<>();
        private final List<List<String>> fieldTable = new ArrayList<>();

        public Builder addHeader(String header) {
            this.fieldTableHeaders.add(header);
            return this;
        }

        public Builder addHeaders(String... header) {
            this.fieldTableHeaders.addAll(List.of(header));
            return this;
        }

        public Builder addRow(String... columns) {
            return addRow(List.of(columns));
        }

        public Builder addRow(List<String> columns) {
            fieldTable.add(columns);
            return this;
        }

        public FieldTable build() {
            String[] fieldValues = new String[fieldTableHeaders.size()];
            for (int col = 0; col < fieldTableHeaders.size(); col++) {
                fieldValues[col] = "";
            }
            for (List<String> rowList : fieldTable) {
                for (int col = 0; col < fieldTableHeaders.size(); col++) {
                    fieldValues[col] += rowList.get(col) + (col != fieldTableHeaders.size()-1 ? "\n" : "");
                }
            }
            return new FieldTable(fieldTableHeaders, Arrays.asList(fieldValues));
        }
    }
}

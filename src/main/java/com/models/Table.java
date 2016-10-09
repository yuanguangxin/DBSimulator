package com.models;

import com.util.Constants;
import com.util.FileManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Table implements Serializable {
    private String name;
    private List<Column> columns;

    public Table() {
        columns = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return columns.toString();
    }

    @Override
    public boolean equals(Object object) {
        return !(object == null || !(object instanceof Table))
                && name != null
                && name.equals(((Table) object).getName());
    }

    public static Table getTableByName(String name) {
        List<String[]> list = FileManager.readCSVFile(Constants.TABLE_HOME + "//" + name.toUpperCase() + ".csv",true);
        Table table = new Table();
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Column column = new Column();
            column.setLength(Integer.parseInt(list.get(i)[0]));
            column.setName(list.get(i)[1]);
            column.setNotNull(Boolean.parseBoolean(list.get(i)[2]));
            column.setPrimaryKey(Boolean.parseBoolean(list.get(i)[3]));
            column.setType(list.get(i)[4]);
            columns.add(column);
        }
        table.setName(name);
        table.setColumns(columns);
        return table;
    }
}

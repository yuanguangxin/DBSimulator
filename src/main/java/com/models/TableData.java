package com.models;

import com.util.Constants;
import com.util.DBError;
import com.util.FileManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableData implements Serializable {
    private Table table;
    private List<List<String>> rows;

    public TableData(Table table) {
        this.table = table;
        rows = new ArrayList<>();
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getColCount() {
        if (table == null || table.getColumns() == null) {
            return -1;
        }
        return table.getColumns().size();
    }

    public void insertRow(List<net.sf.jsqlparser.schema.Column> cc, List<String> row, int indexId) throws DBError {
        if (row == null || row.size() != getColCount()) {
            throw new DBError("invalid row");
        }
        List<Column> columns = table.getColumns();
        int b = 0;
        int t = 0;
        if (cc != null) {
            for (int j = 0; j < cc.size(); j++) {
                for (int k = 0; k < columns.size(); k++) {
                    if (cc.get(j).toString().toLowerCase().equals(columns.get(k).getName().toLowerCase())) {
                        b = 1;
                        t = j;
                        break;
                    }
                }
            }
            if (b == 0) {
                throw new DBError("column", cc.get(t).toString().toLowerCase(), null);
            }
        }
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (row.get(i) != null) {
                switch (columns.get(i).getType().toLowerCase()) {
                    case "int": {
                        if (!isNumeric(row.get(i))) {
                            throw new DBError(column, row.get(i));
                        }
                        break;
                    }
                    case "char":
                    case "string":
                    case "text":
                    case "varchar": {
                        if (row.get(i).length() > column.getLength()) {
                            throw new DBError(column, row.get(i));
                        }
                        break;
                    }
                    case "double": {
                        if (!isDouble(row.get(i)) || row.get(i).length() > column.getLength()) {
                            throw new DBError(column, row.get(i));
                        }
                        break;
                    }
                    default:
                        throw new DBError();
                }
                if (column.isPrimaryKey()) {
                    for (List<String> temp : rows) {
                        if (row.get(i).equals(temp.get(i))) {
                            throw new DBError(column.getName() + " is Primary Key");
                        }
                    }
                }
            } else {
                if (column.isNotNull()) {
                    throw new DBError(column, row.get(i));
                }
            }
        }
        if (indexId == -1) {
            rows.add(row);
        } else {
            rows.add(indexId, row);
        }
    }


    public boolean isNumeric(String token) {
        if (token == null) {
            return false;
        }
        for (char c : token.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDouble(String token) {
        if (token == null) {
            return false;
        }
        int dot = 0;
        for (char c : token.toCharArray()) {
            if (c == '.') {
                dot++;
            } else {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
        }
        return dot == 1;
    }

    public static TableData getDataByName(String name) {
        Table table = Table.getTableByName(name);
        List<String[]> list = FileManager.readCSVFile(Constants.DATA_HOME + "//" + name.toUpperCase() + ".csv", false);
        TableData tableData = new TableData(table);
        List<List<String>> lists = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            List t = new ArrayList();
            for (int j = 0; j < list.get(i).length; j++) {
                t.add(list.get(i)[j]);
            }
            lists.add(t);
        }
        tableData.setRows(lists);
        return tableData;
    }
}

package com.match;

import com.translate.SqlTranslate;
import com.util.DBError;

public class SqlParse {
    private String sqlString;
    private String[] afterArray;
    private SqlTranslate sqlTranslate = new SqlTranslate();

    public SqlParse() {
    }

    public SqlParse(String sqlString) {
        this.sqlString = sqlString;
    }

    private void before() {
        sqlString = sqlString.trim();
        sqlString = sqlString.toLowerCase();
        sqlString = sqlString.replaceAll(";", "");
        afterArray = sqlString.split(" ");
    }

    public void parse() throws DBError {
        before();
        switch (afterArray[0]) {
            case "select":
                sqlTranslate.getResultViewBySelect(sqlString);
                break;
            case "update":
                sqlTranslate.getTableDataByUpdate(sqlString);
                break;
            case "create":
                sqlTranslate.getTableByCreateTable(sqlString);
                break;
            case "delete":
                sqlTranslate.getTableDataByDelete(sqlString);
                break;
            case "insert":
                sqlTranslate.getTableDataByInsert(sqlString);
                break;
            case "alter":
                sqlTranslate.getTableByAlter(sqlString);
                break;
            case "desc":
                break;
            case "drop":
                sqlTranslate.dropTable(sqlString);
                break;
            case "source":
                break;
            case "truncate":
                break;
            case "show":
                break;
            case "check":
                break;
            default:
                throw new DBError(afterArray, 0, "unknown");
        }
    }

    public void setSqlString(String sqlString) {
        this.sqlString = sqlString;
    }
}

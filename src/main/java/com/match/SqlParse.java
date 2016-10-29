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
                if (afterArray[1].toLowerCase().equals("table")) {
                    sqlTranslate.getTableByCreateTable(sqlString);
                } else if (afterArray[1].toLowerCase().equals("index")) {
                    sqlTranslate.executeCreateIndex(sqlString, true);
                }
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
            case "drop":
                if (afterArray[1].toLowerCase().equals("user")) {
                    sqlTranslate.dropUser(sqlString);
                } else {
                    sqlTranslate.drop(sqlString);
                }
                break;
            case "grant":
                sqlTranslate.grantUser(sqlString);
                break;
            case "revoke":
                sqlTranslate.revokeUser(sqlString);
                break;
            default:
                throw new DBError(afterArray, 0, "unknown");
        }
    }

    public void setSqlString(String sqlString) {
        this.sqlString = sqlString;
    }
}

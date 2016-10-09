package com.util;

import com.models.Column;

public class DBError extends Exception {
    /**
     * Error Number
     */
    private int type;

    /**
     * 00 Unknown Error
     */
    public DBError() {
        super("[Error:00] Unknown Error");
        type = 0;
    }

    /**
     * 01 User Access Error
     *
     * @param username Input UserName
     * @param password has Password
     */
    public DBError(String username, boolean password) {
        super("[Error:01] Access denied for user '"
                + username
                + "'@'localhost' (using password: "
                + (password ? "NO" : "YES")
                + ")");
        type = 1;
    }

    /**
     * 02 SQL syntax Error
     *
     * @param afterSQL Input SQL String
     * @param index    Error IndexNode
     * @param cause    Caused by
     */
    public DBError(String afterSQL[], int index, String cause) {
        super("[Error:02] You have an error in your SQL syntax; "
                + "check the manual for the right syntax to use near '"
                + afterSQL[index]
                + "' Caused by"
                + cause);
        type = 2;
    }

    /**
     * 03 Runtime Error
     *
     * @param entity    Entity name
     * @param name      Error name
     * @param container Error locate
     */
    public DBError(String entity, String name, String container) {
        super("Unknown " + entity + " '" + name + "' " + ((container == null) ? "" : "on '" + container + "'"));
        type = 3;
    }

    /**
     * 04 Default Error
     *
     * @param message Default Error Message
     */
    public DBError(String message) {
        super("[Error:04] " + message);
        type = 4;
    }

    /**
     * 05 Data Error
     *
     * @param column column object
     * @param error error data
     */
    public DBError(Column column, String error) {
        super("[Error:05] invalid data '" +
                error +
                "' for column '" +
                column.getName() +
                "' , data can " +
                (column.isNotNull() ? "not" : "") +
                " be 'null' and type should be '" +
                column.getType() +
                "' and length should be '" +
                column.getLength() +
                "'.");
        type = 5;
    }

    /**
     *
     * @param wrongSql  wrongSql input
     * @param lineNum   line index of SQL
     */
    public DBError(String wrongSql, int lineNum){
        super("[Error:02] You have an error in your SQL syntax; "
                + "check the manual for the right syntax to use near '"
                + wrongSql
                + "' at line "
                + lineNum);
        type = 6;
    }
}

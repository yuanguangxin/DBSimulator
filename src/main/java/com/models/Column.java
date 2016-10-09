package com.models;

import java.io.Serializable;

public class Column implements Serializable {
    private String name;
    private String type;
    private int length;
    private boolean notNull;
    private boolean primaryKey;

    public Column() {
    }

    public Column(String name, String type, int length) {
        this.name = name;
        this.length = length;
        this.type = type;
        this.notNull = false;
        this.primaryKey = false;
    }

    public Column(String name, String type, int length, boolean primaryKey, boolean notNull) {
        this.name = name;
        this.length = length;
        this.type = type;
        this.primaryKey = primaryKey;
        this.notNull = primaryKey || notNull;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":" + "\"" + name + "\"," +
                "\"type\":" + "\"" + type + "\"," +
                "\"length\":" + "\"" + length + "\"," +
                "\"pk\":" + "\"" + primaryKey + "\"," +
                "\"nn\":" + "\"" + notNull + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object object) {
        return !(object == null || !(object instanceof Column))
                && name != null
                && name.equals(((Column) object).getName());
    }
}

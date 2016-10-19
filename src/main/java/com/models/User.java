package com.models;

import com.util.Constants;
import com.util.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String password;
    private List<Limit> permission;

    public User() {
        permission = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Limit> getPermission() {
        return permission;
    }

    public void setPermission(List<Limit> permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"username\":").append("\"").append(username).append("\",");
        builder.append("\"password\":").append("\"").append(password).append("\",");
        if (permission != null) {
            builder.append("\"permission\":").append("{");
            for (Limit key : permission) {
                builder.append("\"").append(key.getTable()).append("\":\"").append(key.getOperator());
                builder.append("\",");
            }
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.delete(builder.length() - 1, builder.length());
            }
            builder.append("}");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.delete(builder.length() - 1, builder.length());
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public boolean equals(Object object) {
        return !(object == null || !(object instanceof User))
                && username != null
                && username.equals(((User) object).getUsername());
    }

    public static User getUserByUsername(User user) {
        List<String[]> list = FileManager.readCSVFile(Constants.USER_HOME + "//" + user.getUsername().toUpperCase() + "-" + user.getPassword() + ".csv", false);
        List<Limit> limits = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Limit limit = new Limit();
            limit.setTable(list.get(i)[1]);
            limit.setOperator(list.get(i)[0]);
            limits.add(limit);
        }
        user.setPermission(limits);
        return user;
    }
}

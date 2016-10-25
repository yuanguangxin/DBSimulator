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
    private List<List<String>> permission;

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


    public List<List<String>> getPermission() {
        return permission;
    }

    public void setPermission(List<List<String>> permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object object) {
        return !(object == null || !(object instanceof User))
                && username != null
                && username.equals(((User) object).getUsername());
    }

    public static User getUserByUsername(User user) {
        List<String[]> list = FileManager.readCSVFile(Constants.USER_HOME + "//" + user.getUsername().toUpperCase() + "-" + user.getPassword() + ".csv", false);
        List<List<String>> limits = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            List<String> limit = new ArrayList<>();
            limit.add(list.get(i)[0]);
            limit.add(list.get(i)[1]);
            limits.add(limit);
        }
        user.setPermission(limits);
        return user;
    }
}

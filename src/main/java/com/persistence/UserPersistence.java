package com.persistence;

import com.models.User;
import com.util.Constants;
import com.util.FileManager;

import java.io.File;
import java.util.List;

public class UserPersistence {

    public static void persistenceUser(User user) {
        FileManager.writeObject(String.class, user.getPermission(), Constants.USER_HOME + "//" + user.getUsername().toUpperCase() + "-" + user.getPassword() + ".csv", false);
        System.out.println("Query OK.");
    }

    public static User isUser(User user) {
        File file = new File(Constants.USER_HOME + "//" + user.getUsername().toUpperCase() + "-" + user.getPassword() + ".csv");
        if (file.exists()) {
            user = User.getUserByUsername(user);
            return user;
        } else return null;
    }

    public static User getUserByUsername(String username) {
        File file = new File(Constants.USER_HOME);
        User user = new User();
        user.setUsername(username);
        int st = 0;
        for (int i = 0; i < file.listFiles().length; i++) {
            if (file.listFiles()[i].getName().toUpperCase().indexOf(username.toUpperCase()) != -1) {
                user.setPassword(file.listFiles()[i].getName().split("-")[1].split("\\.")[0]);
                st = 1;
                break;
            }
        }
        if (st == 0) return null;
        else return user;
    }

    public static void dropUser(User user) {
        File file = new File(Constants.USER_HOME + "//" + user.getUsername().toUpperCase() + "-" + user.getPassword() + ".csv");
        if (file.exists()) {
            file.delete();
            System.out.println("Query OK.");
        }
    }

    public static boolean isAllow(User user, String tableName, String op) {
        List<List<String>> lists = user.getPermission();
        for (int i = 0;i<lists.size();i++){
            if (lists.get(i).get(0).toUpperCase().equals(tableName.toUpperCase())||lists.get(i).get(0).toUpperCase().equals("*")){
                if(lists.get(i).get(1).toUpperCase().equals(op.toUpperCase())||lists.get(i).get(1).toUpperCase().equals("*.*")){
                    return true;
                }
            }
        }
        return false;
    }
}

package com.persistence;

import com.models.IndexNode;
import com.translate.SqlTranslate;
import com.util.Constants;
import com.util.DBError;
import com.util.FileManager;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class IndexPersistence {
    public static void createIndex(List<IndexNode> nodeTree, String indexName) {
        File file = new File(Constants.INDEX_HOME + "//" + indexName.toUpperCase() + ".csv");
        if (file.exists()) {
            try {
                throw new DBError("Duplicate key name '" + indexName.split("-")[2] + "'");
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return;
            }
        } else {
            persistenceIndex(nodeTree, indexName);
            System.out.println("Query OK.");
        }
    }

    public static void dropIndex(String fileName) {
        File file = new File(Constants.INDEX_HOME);
        int st = 0;
        if (file.listFiles() != null) {
            for (int i = 0; i < file.listFiles().length; i++) {
                if (file.listFiles()[i].getName().toUpperCase().indexOf(fileName.toUpperCase()) > -1) {
                    file.listFiles()[i].delete();
                    System.out.println("Query OK.");
                    st = 1;
                    break;
                }
            }
        }
        if (st == 0) {
            try {
                throw new DBError("Can't DROP index; check that column/key exists");
            } catch (DBError dbError) {
                dbError.printStackTrace();
            }
        }
    }

    public static void updateIndex(String tableName) {
        File file = new File(Constants.INDEX_HOME);
        for (int i = 0; i < file.listFiles().length; i++) {
            if (file.listFiles()[i].getName().toUpperCase().indexOf(tableName.toUpperCase()) > -1) {
                String sql = "create index " + file.listFiles()[i].getName().split("-")[2].split(".csv")[0] + " on " + tableName + "(" + file.listFiles()[i].getName().split("-")[0] + ")";
                new SqlTranslate().executeCreateIndex(sql, false);
            }
        }
    }

    public static void persistenceIndex(List<IndexNode> nodeTree, String indexName) {
        FileManager.writeObject(IndexNode.class, nodeTree, Constants.INDEX_HOME + "//" + indexName.toUpperCase() + ".csv", false);
    }

    public static boolean isExists(String name) {
        File file = new File(Constants.INDEX_HOME);
        int st = 0;
        if (file.listFiles() != null) {
            for (int i = 0; i < file.listFiles().length; i++) {
                if (file.listFiles()[i].getName().toUpperCase().indexOf(name.toUpperCase()) > -1) {
                    st = 1;
                    break;
                }
            }
        }
        if (st == 0) return false;
        else return true;
    }

    public static String getCompleteName(String name){
        File file = new File(Constants.INDEX_HOME);
        int st = -1;
        if (file.listFiles() != null) {
            for (int i = 0; i < file.listFiles().length; i++) {
                if (file.listFiles()[i].getName().toUpperCase().indexOf(name.toUpperCase()) > -1) {
                    st = i;
                    break;
                }
            }
        }
        if (st == -1) return null;
        else return file.listFiles()[st].getName();
    }
}

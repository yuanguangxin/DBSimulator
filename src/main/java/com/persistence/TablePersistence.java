package com.persistence;

import com.models.Column;
import com.models.Table;
import com.util.Constants;
import com.util.DBError;
import com.util.FileManager;
import javafx.scene.control.Tab;

import java.io.File;

public class TablePersistence {
    public static void createTable(Table table){
        File file = new File(Constants.TABLE_HOME+"//"+table.getName().toUpperCase()+".csv");
        if(file.exists()){
            try {
                throw new DBError("The table name is either invalid or already exists in the database.");
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return;
            }
        }else {
            persistenceTable(table);
        }
    }

    public static void alterTable(Table table){
        File file = new File(Constants.TABLE_HOME+"//"+table.getName().toUpperCase()+".csv");
        if(!file.exists()){
            try {
                throw new DBError("Table '"+table.getName()+"' doesn't exist");
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return;
            }
        }else {
            persistenceTable(table);
        }
    }

    public static void dropTable(String tableName){
        File file = new File(Constants.TABLE_HOME+"//"+tableName.toUpperCase()+".csv");
        if(!file.exists()){
            try {
                throw new DBError("Table '"+tableName+"' doesn't exist");
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return;
            }
        }else {
            file.delete();
            System.out.println("Query OK.");
            DataPersistence.dropData(tableName);
        }
    }

    public static void persistenceTable(Table table){
        FileManager.writeObject(Column.class,table.getColumns(), Constants.TABLE_HOME+"//"+table.getName().toUpperCase()+".csv",true);
        System.out.println("Query OK.");
    }
}

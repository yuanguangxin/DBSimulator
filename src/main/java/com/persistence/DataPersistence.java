package com.persistence;

import com.models.TableData;
import com.util.Constants;
import com.util.DBError;
import com.util.FileManager;

import java.io.File;
import java.util.List;

public class DataPersistence {
    public static void insertData(TableData tableData){
        File file = new File(Constants.TABLE_HOME+"//"+tableData.getTable().getName().toUpperCase()+".csv");
        if(!file.exists()){
            try {
                throw new DBError("Table '"+ tableData.getTable().getName() +"' doesn't exist");
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return;
            }
        }else {
            persistenceData(tableData);
        }

    }

    public static void dropData(String tableName){
        File file = new File(Constants.DATA_HOME+"//"+tableName.toUpperCase()+".csv");
        if (file.exists()){
            file.delete();
        }
    }

    public static void persistenceData(TableData tableData){
        FileManager.writeObject(String.class,tableData.getRows(), Constants.DATA_HOME+"//"+tableData.getTable().getName().toUpperCase()+".csv",false);
        System.out.println("Query OK.");
    }
}

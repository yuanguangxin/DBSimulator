package com.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static void writeObject(Class clazz, Object obj, String path, boolean withHeader) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema;
        if (!withHeader) {
            csvSchema = csvMapper.schemaFor(clazz);
        } else {
            csvSchema = csvMapper.schemaFor(clazz).withHeader();
        }
        try (SequenceWriter writer = csvMapper.writer(csvSchema).writeValues(new File(path))) {
            writer.write(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readCSVFile(String path, boolean withHeader) {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        File csvFile = new File(path);
        List<String[]> list = new ArrayList<>();
        try {
            MappingIterator<String[]> it = mapper.readerFor(String[].class).readValues(csvFile);
            if(withHeader){
                it.next();
            }
            while (it.hasNext()) {
                String[] row = it.next();
//                for(int i=0;i<row.length;i++){
//                    System.out.print(row[i]+" ");
//                }
                list.add(row);
//                System.out.println();
            }
        } catch (Exception e) {

        }
        return list;
    }
}

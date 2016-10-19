package com.models;

import com.persistence.IndexPersistence;
import com.util.Constants;
import com.util.FileManager;
import com.util.StringCompare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IndexNode implements Comparable,Serializable {
    private String data;
    private int idx;
    @Override
    public int compareTo(Object o) {
        if(!(o instanceof IndexNode)){
            return -1;
        }
        return StringCompare.compare(data, ((IndexNode) o).getData());
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    @Override
    public String toString() {
        return "IndexNode{" +
                "data='" + data + '\'' +
                ", idx=" + idx +
                '}';
    }

    public static List<IndexNode> getIndexByName(String name) {
        List<String[]> list = FileManager.readCSVFile(Constants.INDEX_HOME + "//" + IndexPersistence.getCompleteName(name),false);
        List<IndexNode> nodeTree = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            IndexNode indexNode = new IndexNode();
            indexNode.setData(list.get(i)[0]);
            indexNode.setIdx(Integer.parseInt(list.get(i)[1]));
            nodeTree.add(indexNode);
        }
        return nodeTree;
    }
}

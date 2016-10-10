package com.models;

import com.util.StringCompare;

import java.io.Serializable;

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
}

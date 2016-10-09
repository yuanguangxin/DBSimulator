package com.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultView {
    private String[] title;
    private List<String[]> content;

    public ResultView(String[] title) {
        this.title = title;
        content = new ArrayList<>();
    }

    public boolean addRow(String[] row) {
        if (row == null || row.length != title.length) {
            System.out.println("illegal result view format at row " + Arrays.toString(row));
            return false;
        }
        content.add(row);
        return true;
    }

    @Override
    public String toString() {
        int[] count = new int[title.length];
        for (int i = 0; i < count.length; i++) {
            count[i] = title[i].length();
        }
        for (String[] row : content) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != null && row[i].length() > count[i]) {
                    count[i] = row[i].length();
                }
            }
        }
        int sum = 0;
        for (int i : count) {
            sum += i + 3;
        }
        StringBuilder builder = new StringBuilder();
        //top line
        builder.append("+");
        for (int i = 0; i < sum - 1; i++) {
            builder.append("-");
        }
        builder.append("+");
        builder.append("\n");
        //title row
        builder.append("| ");
        for (int i = 0; i < count.length; i++) {
            builder.append(title[i]);
            for (int j = 0; j < count[i] - title[i].length(); j++) {
                builder.append(" ");
            }
            builder.append(" | ");
        }
        builder.append("\n");
        //title foot
        builder.append("+");
        for (int i = 0; i < sum - 1; i++) {
            builder.append("-");
        }
        builder.append("+");
        builder.append("\n");
        //content row
        if (content.size() <= 0) {
            builder.append(" empty set \n");
        } else {
            for (String[] row : content) {
                builder.append("| ");
                for (int i = 0; i < count.length; i++) {
                    if (row[i] != null) {
                        builder.append(row[i]);
                        for (int j = 0; j < count[i] - row[i].length(); j++) {
                            builder.append(" ");
                        }
                    } else {
                        builder.append("N");
                        for (int j = 0; j < count[i] - 1; j++) {
                            builder.append(" ");
                        }
                    }
                    builder.append(" | ");
                }
                builder.append("\n");
            }
        }

        //bottom line
        builder.append("+");
        for (int i = 0; i < sum - 1; i++) {
            builder.append("-");
        }
        builder.append("+");
        builder.append("\n");
        return builder.toString();
    }
}

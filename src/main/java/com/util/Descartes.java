package com.util;

import java.util.ArrayList;
import java.util.List;

public class Descartes {

    public static List calculate(List inputList) {
        List MainList = (ArrayList) inputList.get(0);
        for (int i = 1; i < inputList.size(); i++) {
            List addList = (ArrayList) inputList.get(i);
            List temp = new ArrayList();
            for (int j = 0; j < MainList.size(); j++) {
                for (int k = 0; k < addList.size(); k++) {
                    List cut = new ArrayList();
                    if (MainList.get(j) instanceof ArrayList) {
                        cut.addAll((ArrayList) MainList.get(j));
                    } else {
                        cut.add(MainList.get(j));
                    }
                    if (addList.get(k) instanceof ArrayList) {
                        cut.addAll((ArrayList) addList.get(k));
                    } else {
                        cut.add(addList.get(k));
                    }
                    temp.add(cut);
                }
            }
            MainList = temp;
        }
        return MainList;
    }
}

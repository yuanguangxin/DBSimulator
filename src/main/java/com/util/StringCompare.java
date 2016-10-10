package com.util;

public class StringCompare {
    public static int compare(String var1,String var2){
        if(var1 == null || var2 == null || var1.equals(var2)){
            return 0;
        }
        if(var1.length() > var2.length()){
            return 1;
        }else if(var1.length() == var2.length()){
            long sum1 = sum(var1);
            long sum2 = sum(var2);
            if(sum1 >= sum2){
                return 1;
            } else {
                return -1;
            }
        }else {
            return -1;
        }
    }

    private static long sum(String token){
        long res = 0;
        for(int i = 0 ; i < token.length() ; i++){
            res += token.charAt(i);
        }
        return res;
    }
}

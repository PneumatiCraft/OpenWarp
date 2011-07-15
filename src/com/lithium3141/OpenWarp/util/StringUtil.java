package com.lithium3141.OpenWarp.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static boolean arrayContains(String[] arr, String x) {
        for(int i = 0; i < arr.length; i++) {
            if(arr[i].equals(x)) {
                return true;
            }
        }
        return false;
    }
    
    public static String[] trimArrayLeft(String[] value, String[] trim) {
        int idx = 0;
        for(idx = 0; idx < Math.min(value.length, trim.length); idx++) {
            if(!value[idx].equals(trim[idx])) {
                break;
            }
        }
        
        String[] result = new String[value.length - idx];
        for(int i = 0; i < result.length; i++) {
            result[i] = value[i + idx];
        }
        
        return result;
    }
    
    public static String arrayJoin(String[] array, String delim) {
        if(array.length == 0) {
            return "";
        }
        
        String result = array[0];
        for(int i = 1; i < array.length; i++) {
            result += delim + array[i];
        }
        
        return result;
    }
    
    public static String arrayJoin(String[] array) {
        return arrayJoin(array, " ");
    }
    
    public static List<String> trimListLeft(List<String> value, List<String> trim) {
        List<String> result = new ArrayList<String>();
        
        boolean trimming = true;
        for(int i = 0; i < value.size(); i++) {
            if(trimming && value.get(i).equals(trim.get(i))) {
                continue;
            } else {
                trimming = false;
                result.add(value.get(i));
            }
        }
        
        return result;
    }
}

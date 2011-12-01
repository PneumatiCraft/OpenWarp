package com.lithium3141.OpenWarp.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for various string operations. Primarily operates with
 * String arrays.
 */
public class StringUtil {
    /**
     * Check whether an array of String objects contains another String.
     *
     * @param arr The String array to search.
     * @param x The String to search for.
     * @return True if any element in <code>arr</code> is equal to <code>x</code>
     * using the String <code>.equals()</code> method.
     */
    public static boolean arrayContains(String[] arr, String x) {
        for(int i = 0; i < arr.length; i++) {
            if(arr[i].equals(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Trim the contents of one String array from the beginning of another. Iteratively
     * removes elements in <code>trim</code> from the array <code>value</code>, stopping
     * when <code>trim</code> is completely iterated or no longer matches <code>value</code>.
     *
     * @param value The String array to trim.
     * @param trim The String values to remove.
     * @return A new String array containing the contents of <code>value</code> not also
     * contained in <code>trim</code>.
     */
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

    /**
     * Join an array of Strings into a single String, separated by a given delimiter.
     *
     * @param array The array of Strings to join.
     * @param delim The String to intersperse in <code>array</code> when joining.
     * @return A single String containing each element in <code>array</code>, separated
     * by <code>delim</code>.
     */
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

    /**
     * Join an array of Strings into a single String, separated by a space.
     *
     * @param array The array of Strings to join.
     * @return A single String containing each element in <code>array</code>, separated
     * by a single space.
     * @see #arrayJoin(String[], String)
     */
    public static String arrayJoin(String[] array) {
        return arrayJoin(array, " ");
    }

    /**
     * Trim the contents of one String List from the beginning of another. Iteratively
     * removes elements in <code>trim</code> from the array <code>value</code>, stopping
     * when <code>trim</code> is completely iterated or no longer matches <code>value</code>.
     *
     * @param value The String List to trim.
     * @param trim The String values to remove.
     * @return A new String List containing the contents of <code>value</code> not also
     * contained in <code>trim</code>.
     */
    public static List<String> trimListLeft(List<String> value, List<String> trim) {
        List<String> result = new ArrayList<String>();

        boolean trimming = true;
        for(int i = 0; i < value.size(); i++) {
            if(i == trim.size()) {
                trimming = false;
            }

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

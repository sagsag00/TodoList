package com.tsafrir.sagi.schoolproject.projectpop.db;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Object {
    public static List<Integer> keepOnlyIntegers(List<String> originalList) {
        // Keeps only the integers.
        if(originalList == null){
            return new ArrayList<>();
        }

        List<Integer> result = new ArrayList<>();

        for (String str : originalList) {
            if (isInteger(str)) {
                result.add(Integer.valueOf(str));
            }
        }

        return result;
    }

    public static boolean isInteger(String str) {
        // Checks if integer.
        for (char ch : str.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    public static <T> List<T> parseList(String input, Function<String, T> converter) {
        // Parsing a list with converter.
        // Example for call: "splitList("1,2,3", Integer::parseInt);".
        if (input.contains("[]")) {
            return null;
        }
        String[] parts = input.replace("[", "").replace("]","").split(",");
        List<T> list = new ArrayList<>();
        for (String part : parts) {
            list.add(converter.apply(part.trim()));
        }
        return list;
    }
}

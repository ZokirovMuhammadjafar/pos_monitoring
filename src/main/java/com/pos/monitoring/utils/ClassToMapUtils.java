package com.pos.monitoring.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassToMapUtils {
    public static <T> T mapToClass(Map<String, Object> objectMap, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T objects = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object object = objectMap.get(changeLetterStyleToLower(name));
            if (object != null) {
                Type genericType = field.getGenericType();
                if (genericType.getTypeName().equals(String.class.getName())) {
                    field.set(objects, object);
                } else if (genericType.getTypeName().equals(Integer.class.getName())) {
                    field.set(objects, object);
                } else if (genericType.getTypeName().equals(Long.class.getName())) {
                    field.set(objects, Long.valueOf((String) object));
                } else if (genericType.getTypeName().equals(LocalDateTime.class.getName())) {
                    field.set(objects, object);
                } else if (genericType.getTypeName().equals(Boolean.class.getName())) {
                    field.set(objects, parseBoolean((String) object));
                }
            }
        }
        return objects;
    }

    private static Boolean parseBoolean(String object) {
        return object != null && object.equals("t");
    }

    public static <T> List<T> mapToClassList(List<Map<String, Object>> objectMapList, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        List<T> result = new ArrayList<>();
        for (Map<String, Object> objectMap : objectMapList) {
            result.add(mapToClass(objectMap, clazz));
        }
        return result;
    }

    private static String changeLetterStyleToUpper(String word) {
        int length = word.length();
        while (word.contains("_")) {
            length--;
            String previous = word.substring(0, word.indexOf("_"));
            if (length - previous.length() < 3) break;
            String following = word.substring(word.indexOf("_"));
            char upper = (char) (following.charAt(0) - 32);
            word = previous + upper + following;
        }
        return word;
    }

    private static String changeLetterStyleToLower(String word) {
        StringBuilder result = new StringBuilder();

        char c = word.charAt(0);
        result.append(Character.toLowerCase(c));

        for (int i = 1; i < word.length(); i++) {

            char ch = word.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

}

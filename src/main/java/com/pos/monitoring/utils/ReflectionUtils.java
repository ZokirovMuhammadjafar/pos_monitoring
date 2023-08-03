package com.pos.monitoring.utils;

import com.pos.monitoring.exceptions.ValidatorException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

public class ReflectionUtils {
    public static <T> T mapToClass(Map<String, Object> objectMap, Class<T> clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
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
                    field.set(objects, Integer.valueOf(object + ""));
                } else if (genericType.getTypeName().equals(Long.class.getName())) {
                    field.set(objects, Long.valueOf((String) object));
                } else if (genericType.getTypeName().equals(LocalDateTime.class.getName())) {
                    field.set(objects, object);
                } else if (genericType.getTypeName().equals(Boolean.class.getName())) {
                    field.set(objects, parseBoolean((String) object));
                } else if ((field.getType().isEnum())) {
                    Class<?> enums = Class.forName("com.pos.monitoring.entities.enums." + name.toLowerCase().substring(0, 1).toUpperCase() + name.substring(1));
                    field.set(objects, Enum.valueOf((Class<? extends Enum>) enums, (String) object));
                }
            }
        }
        return objects;
    }

    private static Boolean parseBoolean(String object) {
        return object != null && object.equals("t");
    }

    public static <T> List<T> mapToClassList(List<Map<String, Object>> objectMapList, Class<T> clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
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

    public static void loopObjectFields(Class clazz, Object a, Map<String, String> apply) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            try {
                takeMap(a, apply, declaredField);
            } catch (IllegalAccessException e) {
                throw new ValidatorException(e);
            }
        }
    }

    private static void takeMap(Object objects, Map<String, String> map, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        String fieldName = field.getName();
        if (field.getType().getName().equals(String.class.getName())) {
            String fieldValue = (String) field.get(objects);
            map.put(fieldName, fieldValue);
        } else if (field.getType().getName().equals(Double.class.getName())) {
            map.put(fieldName, String.valueOf(field.get(objects)));
        } else if (field.getType().getName().equals(Integer.class.getName())) {
            map.put(fieldName, String.valueOf(field.get(objects)));
        } else if (field.getType().getName().equals(Boolean.class.getName())) {
            map.put(fieldName, String.valueOf(field.get(objects)));
        } else if (field.getType().getName().equals(Long.class.getName())) {
            map.put(fieldName, String.valueOf(field.get(objects)));
        } else if (field.getType().isEnum()) {
            map.put(fieldName, field.get(objects).toString());
        } else if (field.getType().isPrimitive()) {
            switch (field.getType().getSimpleName()) {
                case "int" -> map.put(fieldName, String.valueOf(field.getInt(objects)));
                case "short" -> map.put(fieldName, String.valueOf(field.getShort(objects)));
                case "boolean" -> map.put(fieldName, String.valueOf(field.getBoolean(objects)));
                case "long" -> map.put(fieldName, String.valueOf(field.getLong(objects)));
                case "byte" -> map.put(fieldName, String.valueOf(field.getByte(objects)));
                case "char" -> map.put(fieldName, String.valueOf(field.getChar(objects)));
                case "double" -> map.put(fieldName, String.valueOf(field.getDouble(objects)));
            }
        }
    }


    public static List<Map<String, String>> maptoMapString(List<Map<String, Object>> report) {
        List<Map<String, String>> stringMapList = new ArrayList<>();
        report.forEach(a -> {
            stringMapList.add(parseMapObjectsToStrings(a));
        });
        return stringMapList;
    }

    private static Map<String, String> parseMapObjectsToStrings(Map<String, Object> map) {
        Map<String,String>stringMap=new HashMap<>();
        map.forEach((a,b)->{
            if(b instanceof String){
                stringMap.put(a,b+"");
            }else if(b instanceof Double){
                stringMap.put(a, String.valueOf(b+""));
            }
            else if(b instanceof Integer){
                stringMap.put(a, String.valueOf(b+""));
            }
            else if(b instanceof Short){
                stringMap.put(a, String.valueOf(b+""));
            }
            else if(b instanceof Date){
                stringMap.put(a, String.valueOf(b+""));
            } else if(!(b+"").equals("null")){
                stringMap.put(a,b+"");
            }

        });
        return stringMap;
    }
}

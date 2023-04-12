package com.pos.monitoring.dto;


import com.pos.monitoring.entities.TerminalModel;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListResponse extends Response implements Serializable {
    private int total;
    private List<Data> data;

    private ListResponse(List data, int total) {
        this.data = data;
        this.total = total;
    }

    private ListResponse(List data) {
        this.data = data;
        total = data.size();
    }

    public ListResponse() {
    }

    public static ListResponse of(Page page, Class clazz) {
        ListResponse listResponse = new ListResponse();
        listResponse.total = (int) page.getTotalElements();
        return null;
    }

    public static ListResponse of(Page page, Class clazz, Function<Object, HashMap<String, String>> function) {
        ListResponse listResponse = new ListResponse();
        listResponse.total = (int) page.getTotalElements();
        List collect = page.stream().map(a -> {
            HashMap<String, String> apply = function.apply(a);
            loopObjectFields(clazz, a, apply);
            Class loop = clazz;
            while (!loop.getSuperclass().equals(Object.class)) {
                loop = clazz.getSuperclass();
                for (Field declaredField : loop.getDeclaredFields()) {
                    loopObjectFields(loop, declaredField, apply);
                }
            }
            return apply;
        }).toList();
        listResponse.data = collect;
        return listResponse;
    }

    private static void loopObjectFields(Class clazz, Object a, Map<String, String> apply) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            try {
                takeMap(a, apply, declaredField);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
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

    public static ListResponse of(List data) {
        return new ListResponse(data);
    }

    public static ListResponse of(List data, int total) {
        return new ListResponse(data, total);
    }

    public boolean isSuccess() {
        return errors == null || errors.isEmpty();
    }

    public int getTotal() {
        return total;
    }

    public List<Data> getData() {
        return data;
    }
}
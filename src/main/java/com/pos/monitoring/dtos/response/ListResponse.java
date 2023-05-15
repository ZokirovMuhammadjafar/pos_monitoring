package com.pos.monitoring.dtos.response;


import com.pos.monitoring.dtos.base.Data;
import com.pos.monitoring.dtos.base.Response;
import org.springframework.data.domain.Page;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static com.pos.monitoring.utils.ReflectionUtils.loopObjectFields;

public final class ListResponse extends Response {
    private int total;
    private List<Data> data;

    private ListResponse(List data, int total) {
        this.data = data;
        this.total = total;
    }

    public ListResponse() {
    }

    public static ListResponse of(Page page, Class clazz) {
        ListResponse listResponse = new ListResponse();
        listResponse.total = (int) page.getTotalElements();
        List collect = page.stream().map(a -> {
            HashMap<String, String> apply = new HashMap<>();
            loopObjectFields(clazz, a, apply);
            Class loop = clazz;
            while (!loop.getSuperclass().equals(Object.class)) {
                loop = loop.getSuperclass();
                loopObjectFields(loop, a, apply);
            }
            return apply;
        }).toList();
        listResponse.data = collect;
        return listResponse;
    }

    public static ListResponse of(Page page, Class clazz, Function<Object, HashMap<String, String>> function) {
        ListResponse listResponse = new ListResponse();
        listResponse.total = (int) page.getTotalElements();
        List collect = page.stream().map(a -> {
            HashMap<String, String> apply = function.apply(a);
            loopObjectFields(clazz, a, apply);
            Class loop = clazz;
            while (!loop.getSuperclass().equals(Object.class)) {
                loop = loop.getSuperclass();
                loopObjectFields(loop, a, apply);
            }
            return apply;
        }).toList();
        listResponse.data = collect;
        return listResponse;
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
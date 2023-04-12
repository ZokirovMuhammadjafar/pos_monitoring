package com.pos.monitoring.dto;

import java.util.HashMap;
import java.util.List;

public class Data extends HashMap {
    public String getString(String code) {
        if (containsKey(code))
            return (String) get(code);
        else return null;
    }

    public List<String> getStrings(String code) {
        if (containsKey(code))
            return (List<String>) get(code);
        else return null;
    }
}
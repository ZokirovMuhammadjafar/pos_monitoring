package com.pos.monitoring.dtos.enums;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@AllArgsConstructor
public enum MachineModel {
    PAX_S90(new HashMap<>(){{
        put("333",false);put("338",false);
        put("302",false);put("339",false);
        put("307",false);put("3c1",true);
        put("308",false);put("3c2",true);
        put("310",false);put("3c3",true);
        put("312",false);put("3c4",true);
        put("313",false);put("3c5",true);
        put("314",false);put("3c6",true);
        put("315",false);put("3c7",true);
        put("316",false);put("3c8",true);
        put("317",false);put("3c9",true);
        put("318",false);put("3d0",true);
        put("346",false);put("3d9",true);
        put("347",false);put("3d2",true);
        put("348",false);put("3d3",true);
        put("349",false);put("3k2",false);
        put("3h0",false);put("3k3",false);
        put("3h7",false);put("3k4",false);
        put("3h8",false);put("325",true);
        put("3h9",false);put("326",true);
        put("3k0",false);put("327",true);
        put("3k1",false);put("3d7",true);
    }}),
    PAX_P80(Map.of(
            "215",true,
            "216",true,
            "217",true
    )),
    PAX_S920(Map.of("6k",false,"6m",false,"6",false));
    private final Map<String,Boolean>prefix;
}

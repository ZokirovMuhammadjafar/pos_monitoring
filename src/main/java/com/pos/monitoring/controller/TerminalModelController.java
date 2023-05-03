package com.pos.monitoring.controller;

import com.pos.monitoring.dto.SingleResponse;
import com.pos.monitoring.dtos.pageable.TerminalModelPageableSearch;
import com.pos.monitoring.dtos.request.TerminalModelCreateDto;
import com.pos.monitoring.dtos.request.TerminalModelUpdateDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.services.TerminalModelService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;


@RestController
@RequestMapping("/api/terminal-models")
@RequiredArgsConstructor
public class TerminalModelController {

    private final TerminalModelService terminalModelService;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @GetMapping("/{id}")
    public SingleResponse getById(@PathVariable String id) {
        return SingleResponse.of(terminalModelService.getById(Long.parseLong(id)));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @GetMapping("/prefix/{prefix}")
    public SingleResponse getByPrefix(@PathVariable String prefix) {
        return SingleResponse.of(terminalModelService.get(prefix));

    }

    @Transactional
    @PostMapping(value = "/create", produces = "application/json")
    public SingleResponse create(@RequestBody TerminalModelCreateDto createDto) {
        terminalModelService.create(createDto);
        return SingleResponse.empty();
    }

    @Transactional
    @PutMapping("/update")
    public SingleResponse update(@RequestBody TerminalModelUpdateDto updateDto) {
        terminalModelService.update(updateDto);
        return SingleResponse.empty();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @GetMapping(value = "/get-all", produces = "application/json")
    public ListResponse getAll(TerminalModelPageableSearch pageableSearch) {
        Page<TerminalModel> pageable = terminalModelService.getAll(pageableSearch);
        return new ListResponse(pageable.getTotalElements(),
                pageable.stream().map(this::getMap).toList());
    }

    @DeleteMapping("/delete")
    @Transactional
    public SingleResponse delete(@RequestBody TerminalModel terminalModel) {
        terminalModelService.deleteById(terminalModel.getId());
        return SingleResponse.empty();
    }

    private Map<String, String> getMap(TerminalModel terminalModel) {
        Map<String, String> map = new HashMap<>();
        for (Field field : terminalModel.getClass().getDeclaredFields()) {
            takeMap(terminalModel, map, field);
        }
        for (Field field : terminalModel.getClass().getSuperclass().getDeclaredFields()) {
            takeMap(terminalModel, map, field);
        }
        return map;
    }

    @SneakyThrows
    private static void takeMap(TerminalModel terminalModel, Map<String, String> map, Field field) {
        field.setAccessible(true);
        String fieldName = field.getName();
        if (field.getType().getName().equals(String.class.getName())) {
            String fieldValue = (String) field.get(terminalModel);
            map.put(fieldName, fieldValue);
        } else if (field.getType().getName().equals(Boolean.class.getName())) {
            map.put(fieldName, String.valueOf(field.get(terminalModel)));
        } else if (field.getType().getName().equals(Long.class.getName())) {
            map.put(fieldName, String.valueOf(field.get(terminalModel)));
        } else if (field.getType().getName().equals(Date.class.getName())) {
            map.put(fieldName, String.valueOf(field.get(terminalModel)));
        } else if (field.getType().isPrimitive()) {
            switch (field.getType().getSimpleName()) {
                case "int" -> map.put(fieldName, String.valueOf(field.getInt(terminalModel)));
                case "short" -> map.put(fieldName, String.valueOf(field.getShort(terminalModel)));
                case "boolean" -> map.put(fieldName, String.valueOf(field.getBoolean(terminalModel)));
                case "long" -> map.put(fieldName, String.valueOf(field.getLong(terminalModel)));
                case "byte" -> map.put(fieldName, String.valueOf(field.getByte(terminalModel)));
                case "char" -> map.put(fieldName, String.valueOf(field.getChar(terminalModel)));
                case "double" -> map.put(fieldName, String.valueOf(field.getDouble(terminalModel)));
            }
        }
    }

}

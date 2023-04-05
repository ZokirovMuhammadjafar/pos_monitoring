package com.pos.monitoring.controller;

import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.services.TerminalModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/terminal/model")
@RequiredArgsConstructor
public class TerminalModelController {

    private final TerminalModelService terminalModelService;

    @GetMapping("/{id}")
    public TerminalModel getById(@PathVariable String id) {
        return terminalModelService.getById(Long.parseLong(id));
    }

    @GetMapping("/prefix/{prefix}")
    public TerminalModel getByPrefix(@PathVariable String prefix) {
        return terminalModelService.get(prefix);
    }

    @PostMapping("/create")
    public TerminalModel create(@RequestBody TerminalModel terminalModel) {
        return terminalModelService.create(terminalModel);
    }

    @PostMapping("/update")
    public TerminalModel update(@RequestBody TerminalModel updateTerminal) {
        if (updateTerminal.getId() == null) {
            throw new ValidatorException("idsi null kelgan");
        }
        return terminalModelService.update(updateTerminal);
    }

    @GetMapping("/all/{page}/{limit}")
    public List<Map<String,String>> getAll(@PathVariable Integer limit, @PathVariable Integer page) {
        if(limit==null){
            limit=10;
        }if(page==null){
            page=0;
        }
        List<TerminalModel> all = terminalModelService.getAll(limit, page);
        return all.stream().map(a -> {
            try {
                return getMap(a);
            } catch (IllegalAccessException e) {
                throw new ValidatorException("parse qilishda xatolik");
            }
        }).collect(Collectors.toList());

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        terminalModelService.deleteById(id);
    }

    private Map<String,String>getMap(TerminalModel terminalModel) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        for (Field field : terminalModel.getClass().getDeclaredFields()) {
            takeMap(terminalModel, map, field);
        }
        for (Field field : terminalModel.getClass().getSuperclass().getDeclaredFields()) {
            takeMap(terminalModel, map, field);
        }
        return map;
    }

    private static void takeMap(TerminalModel terminalModel, Map<String, String> map, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        String fieldName = field.getName();
        if(field.getType().getName().equals(String.class.getName())){
            String fieldValue = (String) field.get(terminalModel);
            map.put(fieldName, fieldValue);
        }else if(field.getType().getName().equals(Boolean.class.getName())){
            map.put(fieldName, String.valueOf(field.get(terminalModel)));
        }else if(field.getType().getName().equals(Long.class.getName())){
            map.put(fieldName,String.valueOf(field.get(terminalModel)));
        }
    }

}

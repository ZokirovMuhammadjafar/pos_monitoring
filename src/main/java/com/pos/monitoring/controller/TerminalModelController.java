package com.pos.monitoring.controller;

import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.services.TerminalModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terminal/model")
@RequiredArgsConstructor
public class TerminalModelController {

    private final TerminalModelService terminalModelService;

    @GetMapping("/{id}")
    public TerminalModel getById(@PathVariable Long id) {
        return terminalModelService.getById(id);
    }

    @GetMapping("/{prefix}")
    public TerminalModel getByPrefix(@PathVariable String prefix) {
        return terminalModelService.get(prefix);
    }

    @PostMapping("/create")
    public TerminalModel create(@RequestBody TerminalModel terminalModel) {
        return terminalModelService.create(terminalModel);
    }

    @PatchMapping("/update")
    public TerminalModel update(@RequestBody TerminalModel updateTerminal) {
        if (updateTerminal.getId() == null) {
            throw new ValidatorException("idsi null kelgan");
        }
        return terminalModelService.update(updateTerminal);
    }

    @GetMapping("/all")
    public List<TerminalModel> getAll() {
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        terminalModelService.deleteById(id);
    }

}

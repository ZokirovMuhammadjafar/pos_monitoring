package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.pageable.TerminalModelPageableSearch;
import com.pos.monitoring.dtos.request.TerminalModelCreateDto;
import com.pos.monitoring.dtos.request.TerminalModelUpdateDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.services.TerminalModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


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
        ListResponse of = ListResponse.of(pageable, TerminalModel.class);
        return of;
    }

    @DeleteMapping("/delete")
    @Transactional
    public SingleResponse delete(@RequestBody TerminalModel terminalModel) {
        terminalModelService.deleteById(terminalModel.getId());
        return SingleResponse.empty();
    }


}

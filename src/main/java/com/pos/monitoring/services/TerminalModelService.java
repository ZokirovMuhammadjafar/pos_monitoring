package com.pos.monitoring.services;

import com.pos.monitoring.dtos.pageable.TerminalModelPageableSearch;
import com.pos.monitoring.dtos.request.TerminalModelCreateDto;
import com.pos.monitoring.dtos.request.TerminalModelUpdateDto;
import com.pos.monitoring.entities.TerminalModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TerminalModelService {

    void create(TerminalModelCreateDto createDto);

    Page<TerminalModel> getAll(TerminalModelPageableSearch pageableSearch);

    TerminalModel get(String prefix);

    TerminalModel getById(Long id);

    void deleteById(Long id);

    void update(TerminalModelUpdateDto updateDto);

}

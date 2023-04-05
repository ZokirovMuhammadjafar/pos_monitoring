package com.pos.monitoring.services;

import com.pos.monitoring.entities.TerminalModel;

import java.util.List;

public interface TerminalModelService {

    TerminalModel create(TerminalModel create) ;

    List<TerminalModel>getAll(Integer limit, Integer page);

    TerminalModel get(String prefix);

    TerminalModel getById(Long id);

    void deleteById(Long id);

    TerminalModel update(TerminalModel update);

}

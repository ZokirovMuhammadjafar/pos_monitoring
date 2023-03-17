package com.pos.monitoring.services;

import com.pos.monitoring.entities.TerminalModel;

import javax.xml.bind.ValidationException;

public interface TerminalModelService {

    TerminalModel create(TerminalModel create) ;

    TerminalModel get(String prefix);

    TerminalModel getById(Long id);

    void deleteById(Long id);

    TerminalModel update(TerminalModel update);

}

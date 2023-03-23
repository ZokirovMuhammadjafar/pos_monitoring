package com.pos.monitoring.services;

import com.pos.monitoring.entities.TerminalModel;
import jakarta.persistence.criteria.CriteriaQuery;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface TerminalModelService {

    TerminalModel create(TerminalModel create) ;

    List<TerminalModel>getAll();

    TerminalModel get(String prefix);

    TerminalModel getById(Long id);

    void deleteById(Long id);

    TerminalModel update(TerminalModel update);

}

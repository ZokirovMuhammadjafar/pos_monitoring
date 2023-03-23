package com.pos.monitoring.services.impl;

import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.TerminalModelRepository;
import com.pos.monitoring.services.TerminalModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service(value = "terminalModelService")
@RequiredArgsConstructor
public class TerminalModelServiceImpl implements TerminalModelService {
    private final TerminalModelRepository terminalModelRepository;

    @Override
    public TerminalModel create(TerminalModel create) {
        if (create.getPrefix() == null) throw new ValidatorException("prefix kiritilmagan");
        if (create.getName() == null) throw new ValidatorException("name kiritilmagan");
        TerminalModel terminalModel = terminalModelRepository.findByPrefixAndDeleted(create.getPrefix(), false);
        if (terminalModel != null && terminalModel.getPrefix().equals(create.getPrefix()) && terminalModel.getName().equals(create.getName()))
            throw new ValidatorException("bu avvaldan mavjud");
        return terminalModelRepository.save(create);
    }

    @Override
    public TerminalModel get(String prefix) {
        TerminalModel terminalModel = terminalModelRepository.findByPrefixAndDeleted(prefix, false);
        if (terminalModel == null) throw new ValidatorException("prefix xato kiritilgan");
        return terminalModel;
    }

    @Override
    public TerminalModel getById(Long id) {
        return terminalModelRepository.findById(id).orElseThrow(() -> {
            throw new ValidatorException("id xato kiritilgan" + id);
        });

    }

    @Override
    public void deleteById(Long id) {
        // TODO: 3/14/2023 buni ham yozishim kerak
    }

    @Override
    public TerminalModel update(TerminalModel update) {
        // TODO: 3/14/2023 terminal modelni update qilishini yozib qoyishim kerak
        return null;
    }
}


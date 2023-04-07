package com.pos.monitoring.services.impl;

import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.TerminalModelRepository;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.TerminalModelService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "terminalModelService")
@RequiredArgsConstructor
public class TerminalModelServiceImpl implements TerminalModelService {
    private final TerminalModelRepository terminalModelRepository;
    private final MachineService machineService;
    private final EntityManager entityManager;

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
    public List<TerminalModel> getAll(Integer limit, Integer page) {
        List<TerminalModel> all = terminalModelRepository.findAllByDeletedFalse(PageRequest.of(page / limit, limit));
        return all;
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
        if (id == null) {
            throw new ValidatorException("ID CAME NULL");
        }
        TerminalModel terminalModel = terminalModelRepository.findById(id).orElseThrow(() -> {
            throw new ValidatorException("TERMINAL MODEL NOT FOUND ID = " + id);
        });
        terminalModelRepository.deleteTerminal(terminalModel.getId());
        machineService.deleteByPrefix(terminalModel.getPrefix());
    }

    @Override
    public TerminalModel update(TerminalModel update) {
        TerminalModel terminalModel = terminalModelRepository.findById(update.getId()).orElseThrow(() -> {
            throw new ValidatorException("TERMINAL MODEL NOT FOUND ID = " + update.getId());
        });
        TerminalModel save = null;
        if (update.getName() != null) {
            terminalModel.setName(update.getName());
            save = terminalModelRepository.save(terminalModel);
        }
        if (update.getValid() != terminalModel.getValid()) {
            terminalModel.setValid(update.getValid());
            machineService.updateValid(terminalModel);
            save = terminalModelRepository.save(terminalModel);
        }
        return save;
    }
}


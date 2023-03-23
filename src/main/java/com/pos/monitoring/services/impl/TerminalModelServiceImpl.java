package com.pos.monitoring.services.impl;

import com.pos.monitoring.entities.MachineState;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.TerminalModelRepository;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.TerminalModelService;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "terminalModelService")
@RequiredArgsConstructor
public class TerminalModelServiceImpl implements TerminalModelService {
    private final TerminalModelRepository terminalModelRepository;
    private final MachineService machineService;

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
    public List<TerminalModel> getAll() {

        return null;
    }

    @Override
    public TerminalModel get(String prefix) {
        TerminalModel terminalModel = terminalModelRepository.findByPrefixAndDeleted(prefix, false);
        if(terminalModel==null)throw new ValidatorException("prefix xato kiritilgan");
        return terminalModel;
    }

    @Override
    public TerminalModel getById(Long id) {
        return terminalModelRepository.findById(id).orElseThrow(()->{
            throw new ValidatorException("id xato kiritilgan" + id);
        });

    }

    @Override
    public void deleteById(Long id) {
        TerminalModel terminalModel = terminalModelRepository.findById(id).orElseThrow(() -> {
            throw new ValidatorException("idsi topilmadi == >> " + id);
        });
        terminalModelRepository.delete(terminalModel);
        machineService.deleteByPrefix(terminalModel.getPrefix());
    }

    @Override
    public TerminalModel update(TerminalModel update) {
        TerminalModel terminalModel = terminalModelRepository.findById(update.getId()).orElseThrow(() -> {
            throw new ValidatorException("idsi topilmadi == >> " + update.getId());
        });
        if(update.getName()!=null){
            terminalModel.setName(update.getName());
        }
        if(update.getPrefix()!=null){
            terminalModel.setPrefix(update.getPrefix());
        }
        if(update.getValid()!=terminalModel.getValid()){
            terminalModel.setValid(update.getValid());
        }
        TerminalModel save = terminalModelRepository.save(terminalModel);
        return save;
    }
}


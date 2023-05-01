package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.TerminalModelPageableSearch;
import com.pos.monitoring.dtos.request.TerminalModelCreateDto;
import com.pos.monitoring.dtos.request.TerminalModelUpdateDto;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.TerminalModelRepository;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.TerminalModelService;
import com.pos.monitoring.utils.DaoUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service(value = "terminalModelService")
@RequiredArgsConstructor
public class TerminalModelServiceImpl implements TerminalModelService {
    private final TerminalModelRepository terminalModelRepository;
    private final MachineService machineService;

    @Override
    public void create(TerminalModelCreateDto createDto) {
        if (createDto.getPrefix() == null) {
            throw new ValidatorException("prefix kiritilmagan");
        }
        if (createDto.getName() == null) {
            throw new ValidatorException("name kiritilmagan");
        }
        TerminalModel terminalModel = terminalModelRepository.findByPrefixAndDeleted(createDto.getPrefix(), false);
        if (terminalModel != null && terminalModel.getPrefix().equals(createDto.getPrefix()) && terminalModel.getName().equals(createDto.getName())) {
            throw new ValidatorException("bu avvaldan mavjud");
        }
        TerminalModel create = new TerminalModel(createDto.getName(), createDto.getPrefix(), createDto.getValid());
        terminalModelRepository.save(create);
    }

    @Override
    public Page<TerminalModel> getAll(TerminalModelPageableSearch pageableSearch) {
        return terminalModelRepository.findAll((Specification<TerminalModel>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(pageableSearch.getName())) {
                predicates.add(cb.equal(root.get("nane"), pageableSearch.getName()));
            }

            if (!ObjectUtils.isEmpty(pageableSearch.getPrefix())) {
                predicates.add(cb.like(root.get("prefix"), DaoUtils.toLikeCriteria(pageableSearch.getPrefix())));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, DaoUtils.toPaging(pageableSearch));
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
    public void update(TerminalModelUpdateDto updateDto) {
        TerminalModel terminalModel = terminalModelRepository.findById(updateDto.getId()).orElseThrow(() -> {
            throw new ValidatorException("TERMINAL MODEL NOT FOUND ID = " + updateDto.getId());
        });
        if (updateDto.getName() != null) {
            terminalModel.setName(updateDto.getName());
            terminalModelRepository.save(terminalModel);
        }
        if (updateDto.getValid() != terminalModel.getValid()) {
            terminalModel.setValid(updateDto.getValid());
            machineService.updateValid(terminalModel);
            terminalModelRepository.save(terminalModel);
        }
    }
}


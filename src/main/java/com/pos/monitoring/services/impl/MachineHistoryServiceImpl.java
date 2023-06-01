package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.MachineHistoryPageableSearch;
import com.pos.monitoring.dtos.enums.MachineHistoryState;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineHistory;
import com.pos.monitoring.exceptions.ErrorCode;
import com.pos.monitoring.exceptions.LocalizedApplicationException;
import com.pos.monitoring.repositories.MachineHistoryRepository;
import com.pos.monitoring.services.MachineHistoryService;
import com.pos.monitoring.utils.DaoUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineHistoryServiceImpl implements MachineHistoryService {
    private final MachineHistoryRepository machineHistoryRepository;

    @Override
    public void createChangeInst(Machine oldMachine, Machine machine) {
        MachineHistory machineHistory = new MachineHistory();
        machineHistory.setFromInstId(oldMachine.getInstId());
        machineHistory.setToInstId(machine.getInstId());
        machineHistory.setSrNumber(machine.getSrNumber());
        machineHistory.setState(MachineHistoryState.CHANGE_INS);
        machineHistoryRepository.save(machineHistory);
    }

    @Override
    public MachineHistory createChangeMfo(Machine oldMachine, Machine machine) {
        MachineHistory machineHistory = new MachineHistory();
        machineHistory.setFromMfo(oldMachine.getBranchMfo());
        machineHistory.setToMfo(machine.getBranchMfo());
        machineHistory.setSrNumber(machine.getSrNumber());
        machineHistory.setState(MachineHistoryState.CHANGE_MFO);
        return machineHistoryRepository.save(machineHistory);
    }

    @Override
    public Page<MachineHistory> getAll(MachineHistoryPageableSearch pageableSearch) {
        return machineHistoryRepository.findAll((Specification<MachineHistory>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, DaoUtils.toPaging(pageableSearch));
    }

    @Override
    public MachineHistory get(Long id) {
        return machineHistoryRepository.findById(id).orElseThrow(() -> new LocalizedApplicationException(ErrorCode.ENTITY_NOT_FOUND));
    }
}

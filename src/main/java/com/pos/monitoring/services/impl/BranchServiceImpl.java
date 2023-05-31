package com.pos.monitoring.services.impl;

import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.services.BranchService;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public void synchronize() {

    }

    @Override
    public List<Branch> getAllByInstId(String instId) {
        Branch parentBranch = branchRepository.findByMfoAndDeletedFalse(instId);
        if(parentBranch==null){
            throw new ValidatorException("PARENT_ENTITY_NOT_FOUND");
        }
        return branchRepository.findByParentAndDeletedFalse(parentBranch);
    }

    @Override
    public List<Branch> getAllInstID() {
       return branchRepository.findIntsId();
    }


}

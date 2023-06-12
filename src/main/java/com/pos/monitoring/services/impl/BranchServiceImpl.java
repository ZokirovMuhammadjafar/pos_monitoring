package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.BranchFilterDto;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.services.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public void synchronize() {

    }

    @Override
    // TODO: 6/12/2023 qarab qoyish kerak
    public List<Branch> getBranchesByFilter(BranchFilterDto branchFilterDto) {
        if (branchFilterDto.getInstId() != null && branchFilterDto.getRegionCode() != null) {
            return getBranchesByInstId(branchFilterDto).stream().filter(a -> a.getRegionCode().equals(branchFilterDto.getRegionCode())).collect(Collectors.toList());
        } else if (branchFilterDto.getInstId() != null) {
            return getBranchesByInstId(branchFilterDto);
        }else {
            throw new ValidatorException("MALUMOT_XATO");
        }
    }

    private List<Branch> getBranchesByInstId(BranchFilterDto branchFilterDto) {
        Branch parent = branchRepository.findByMfoAndDeletedFalse(branchFilterDto.getInstId());
        List<Branch> parentBranchChilds = branchRepository.findAllParentAndDeletedFalse(branchFilterDto.getInstId());
        if (parentBranchChilds == null) {
            throw new ValidatorException("PARENT_ENTITY_NOT_FOUND");
        }
        List<Branch> secondChildsParent = branchRepository.findByBranchesDeletedFalse(parentBranchChilds);
        if (secondChildsParent != null && secondChildsParent.size() > 0) {
            secondChildsParent.add(parent);
            return secondChildsParent;
        } else {
            parentBranchChilds.add(parent);
            return parentBranchChilds;
        }
    }

    @Override
    public List<Branch> getAllInstID() {
        return branchRepository.findIntsId();
    }


}

package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.request.BranchFilterDto;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.services.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        } else {
            throw new ValidatorException("MALUMOT_XATO");
        }
    }

    public Set<Branch> getBranches(List<String> branchFilterDtos,boolean withParent) {
        if (branchFilterDtos.isEmpty()) {
            throw new ValidatorException("parent not found");
        }
        Set<Branch> branches = branchRepository.findByMfoInAndDeletedFalse(branchFilterDtos);
        if (branches.isEmpty()){
            throw new ValidatorException("entity not found");
        }
        Set<Branch> first = branchRepository.findByBranchesDeletedFalse(branches);
        if(first.isEmpty()){
            if(withParent){
                first.addAll(branches);
                return first;
            }return  first;
        }
        Set<Branch> second = branchRepository.findByBranchesDeletedFalse(first);
        second.addAll(first);
        if(withParent){
            second.addAll(branches);
            return second;
        }return  second;
    }

    private List<Branch> getBranchesByInstId(BranchFilterDto branchFilterDto) {
        Branch parent = branchRepository.findByMfoAndDeletedFalse(branchFilterDto.getInstId());
        Set<Branch> parentBranchChilds = branchRepository.findAllParentAndDeletedFalse(branchFilterDto.getInstId());
        if (parentBranchChilds == null) {
            throw new ValidatorException("PARENT_ENTITY_NOT_FOUND");
        }
        Set<Branch> secondChildsParent = branchRepository.findByBranchesDeletedFalse(parentBranchChilds);
        if (secondChildsParent != null && !secondChildsParent.isEmpty()) {
            secondChildsParent.add(parent);
            return secondChildsParent.stream().toList();
        } else {
            parentBranchChilds.add(parent);
            return new ArrayList<>(parentBranchChilds);
        }
    }


    @Override
    public List<Branch> getAllInstID() {
        return branchRepository.findIntsId();
    }


}

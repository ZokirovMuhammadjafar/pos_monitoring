package com.pos.monitoring.services;

import com.pos.monitoring.dtos.request.BranchFilterDto;
import com.pos.monitoring.entities.Branch;

import java.util.List;
import java.util.Set;

public interface BranchService {

    void synchronize();

    List<Branch> getBranchesByFilter(BranchFilterDto branchFilterDto);

    List<Branch> getAllInstID();

    Set<Branch> getBranches(List<String> branchFilterDtos, boolean withParent);
}

package com.pos.monitoring.services;

import com.pos.monitoring.dtos.pageable.BranchFilterDto;
import com.pos.monitoring.entities.Branch;

import java.util.List;

public interface BranchService {

    void synchronize();

    List<Branch> getBranchesByFilter(BranchFilterDto branchFilterDto);

    List<Branch>getAllInstID();
}

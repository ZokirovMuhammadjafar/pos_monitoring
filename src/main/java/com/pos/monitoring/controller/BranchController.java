package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.pageable.BranchFilterDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.services.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/branch")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping("/get-all-inst")
    public ListResponse getAllInstId(){
        List<Branch> allInstID = branchService.getAllInstID();
        return ListResponse.of(allInstID,Branch.class,allInstID.size());
    }

    @GetMapping("/get-all-mfos")
    public ListResponse getAllBranchByInstIdAndFilter(BranchFilterDto branchFilterDto){
        List<Branch> branchesByFilter = branchService.getBranchesByFilter(branchFilterDto);
        return ListResponse.of(branchesByFilter,branchesByFilter.size());
    }

}

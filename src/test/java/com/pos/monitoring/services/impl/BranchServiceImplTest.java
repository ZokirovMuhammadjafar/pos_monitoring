package com.pos.monitoring.services.impl;

import com.pos.monitoring.controller.BranchController;
import com.pos.monitoring.dtos.pageable.BranchFilterDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.services.BranchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BranchServiceImplTest {

    @Autowired
    BranchService branchService;
    @Autowired
    BranchController branchController;
    @Autowired
    MachineServiceImpl machineService;

    @Test
    public void bank(){
        System.out.println(branchService.getBranchesByFilter(new BranchFilterDto("09006",3L)));

//        machineService.synchronizeAuthCode();
    }

}
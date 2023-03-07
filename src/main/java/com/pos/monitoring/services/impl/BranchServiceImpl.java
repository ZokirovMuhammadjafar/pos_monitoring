package com.pos.monitoring.services.impl;

import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.services.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public void synchronize() {

    }
}

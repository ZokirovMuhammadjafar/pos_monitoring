package com.pos.monitoring.services;

import com.pos.monitoring.entities.Branch;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface BranchService {

    void synchronize();

    List<Branch>getAllByInstId(@NotNull String instId);
}

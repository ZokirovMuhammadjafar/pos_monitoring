package com.pos.monitoring;


import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.SynchronizeType;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.system.specifications.MachineSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@SpringBootTest
public class TerminalModelTest {

    @Autowired
    MachineRepository machineRepository;

    @Test
    void terminalModelTest(){

    }
    @Test
    void createTerminalModel(){

    }
}

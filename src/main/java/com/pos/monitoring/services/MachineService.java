package com.pos.monitoring.services;

public interface MachineService {
    /**
     * this method use synchronize the machines from 8005
     * update comes every day
     */
    void synchronize();
}

package com.pos.monitoring.dtos.enums;

public enum MachineState {
    //    this indicates that the pos machine is currently working and we can take data from 7003 uzcard project
    HAS_7003,
    //    this indicates that the pos machine is written and created contract but it is not used currently and pid money this machine
    HAS_NO_USED,
    //    this indicates what the pos machine is stayed in warehouse of the bank
    HAS_WAREHOUSE,
    //    this indicates error for example if there is neither terminal_id nor merchant_id
    HAS_ERROR,
    //    this indicates that the pos machine is registered in uzcard it is valid to use but it is not written contract
    NEW_7003;
}

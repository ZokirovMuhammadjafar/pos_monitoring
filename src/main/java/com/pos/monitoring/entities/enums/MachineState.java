package com.pos.monitoring.entities.enums;

public enum MachineState {
    //    this indicates that the pos machine is currently working and we can take data from 7003 uzcard project
    HAS_CONTRACT_WITH_7003,//0
    //    this indicates that the pos machine is written and created contract but it is not used currently and pay money this machine
    HAS_CONTRACT_NOT_7003,//1
    //    this indicates error for example if there is neither terminal_id nor merchant_id
    HAS_ERROR,//2
    //    this indicates that the pos machine is registered in uzcard it is valid to use but it is not written contract
    HAS_NOT_CONTRACT_WORKING_7003,//3
    HAS_CONTRACT_STAY_WAREHOUSE,//4
    HAS_NOT_CONTRACT_NOT_7003,//5
    HAS_NOT_CONTRACT_STAY_WAREHOUSE;//6
}

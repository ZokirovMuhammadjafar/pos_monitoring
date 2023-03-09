package com.pos.monitoring.entities;

public enum State {
    //    history machina state
    CHANGE_MFO,
    CHANGE_INS,
    CONFIRMED,
    REJECTED,
    //    machina state
    HAS_NOT_7003,
    HAS_7003,
    NEW_7003
}

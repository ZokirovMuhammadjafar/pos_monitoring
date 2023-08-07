package com.pos.monitoring.entities.enums;

import lombok.Getter;

@Getter
public enum SynchronizeType {
    KASSA,
    TYPICAL,
    GREATER_THEN_MILLION,
    BETWEEN_HUNDRED_THOUSAND_AND_MILLION,
    LOWER_THAN_HUNDRED_THOUSAND
}

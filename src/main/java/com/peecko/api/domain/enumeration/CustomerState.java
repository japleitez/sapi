package com.peecko.api.domain.enumeration;

import java.util.List;

/**
 * The CustomerState enumeration.
 */
public enum CustomerState {
    NEW,
    TRIAL,
    ACTIVE,
    CLOSED;

    public static final List<CustomerState> TRIAL_ACTIVE = List.of(TRIAL, ACTIVE);

}

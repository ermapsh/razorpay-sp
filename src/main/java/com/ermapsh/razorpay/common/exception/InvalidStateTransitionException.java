package com.ermapsh.razorpay.common.exception;

import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException {
    private final String fromState;
    private final String toEvent;

    public InvalidStateTransitionException(String fromState, String event){
        super("Invalid transition form " + fromState +" to with event " + event);
        this.fromState = fromState;
        this.toEvent = event;
    }
}

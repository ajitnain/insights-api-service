package com.example.insights.domain;

/** Estimated distinct customers, with its relative error. */
public record CustomerCount(long value, double relativeError) {

    /** Relative error as a fraction, so 0.02 means 2%. */
    public static CustomerCount of(long value, double relativeError) {
        return new CustomerCount(value, relativeError);
    }
}

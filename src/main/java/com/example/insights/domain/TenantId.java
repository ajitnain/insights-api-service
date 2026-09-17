package com.example.insights.domain;

/** The retailer this data belongs to. */
public record TenantId(String value) {

    public TenantId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tenant id is required");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

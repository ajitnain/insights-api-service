package com.example.insights.domain;

/** A campaign within one tenant. */
public record CampaignId(String value) {

    public CampaignId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("campaign id is required");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

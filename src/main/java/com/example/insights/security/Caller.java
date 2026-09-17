package com.example.insights.security;

import com.example.insights.domain.TenantId;

public record Caller(TenantId tenantId) {}

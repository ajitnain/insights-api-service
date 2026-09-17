package com.example.insights.security;

/** Caller for the current request. */
public final class TenantContext {

    private static final ThreadLocal<Caller> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    static void set(Caller caller) {
        CURRENT.set(caller);
    }

    static void clear() {
        CURRENT.remove();
    }

    public static Caller require() {
        Caller caller = CURRENT.get();
        if (caller == null) {
            throw new IllegalStateException("no authenticated caller on this request");
        }
        return caller;
    }
}

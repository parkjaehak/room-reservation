package com.room_reservation.security;

public class RequestUserHolder {
    private static final ThreadLocal<RequestUser> CONTEXT = new ThreadLocal<>();

    public static void set(RequestUser user) {
        CONTEXT.set(user);
    }

    public static RequestUser get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}



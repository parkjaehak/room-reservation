package com.room_reservation.security;

import com.room_reservation.exception.ForbiddenException;

public class SecurityUtil {
    public static RequestUser requireUser() {
        RequestUser user = RequestUserHolder.get();
        if (user == null || user.getRole() == Role.ANONYMOUS) {
            throw new ForbiddenException();
        }
        return user;
    }

    public static void requireAdmin() {
        RequestUser user = requireUser();
        if (!user.isAdmin()) {
            throw new ForbiddenException();
        }
    }
}



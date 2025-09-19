package com.room_reservation.security;

import com.room_reservation.exception.ForbiddenException;
import com.room_reservation.exception.UnauthorizedException;

public class SecurityUtil {
    public static RequestUser requireUser() {
        RequestUser user = RequestUserHolder.get();
        if (user == null) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        return user;
    }

    public static void requireAdmin() {
        RequestUser user = requireUser();
        if (!user.isAdmin()) {
            throw new ForbiddenException("해당 자원에 접근할 권한이 없습니다.");
        }
    }
}



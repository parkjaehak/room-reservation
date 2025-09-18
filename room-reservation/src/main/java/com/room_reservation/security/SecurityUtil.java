package com.room_reservation.security;

import com.room_reservation.exception.ForbiddenException;
import com.room_reservation.exception.UnauthorizedException;

public class SecurityUtil {
    // USER 권한 확인 - 인증되지 않은 경우 401, 권한이 없는 경우 403
    public static RequestUser requireUser() {
        RequestUser user = RequestUserHolder.get();
        if (user == null) {
            throw new UnauthorizedException("인증이 필요합니다");
        }
        if (user.role() == Role.ANONYMOUS) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다");
        }
        return user;
    }

    // ADMIN 권한 확인
    public static void requireAdmin() {
        RequestUser user = requireUser();
        if (!user.isAdmin()) {
            throw new ForbiddenException();
        }
    }
}



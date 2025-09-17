package com.room_reservation.security;

public class RequestUser {
    private final Role role;
    private final Long userId;

    public RequestUser(Role role, Long userId) {
        this.role = role;
        this.userId = userId;
    }

    public Role getRole() { return role; }
    public Long getUserId() { return userId; }

    public boolean isAdmin() { return role == Role.ADMIN; }
}



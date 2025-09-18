package com.room_reservation.security;



public record RequestUser(
        Role role, Long userId) {

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}



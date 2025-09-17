package com.chatapp.security.model;

public record JwtUser(String id,
                      String name,
                      String email,
                      String phoneNumber) {
}

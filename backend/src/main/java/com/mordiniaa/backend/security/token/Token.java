package com.mordiniaa.backend.security.token;

public interface Token {

    String getTokenName();

    String getToken();

    long getTtl();
}

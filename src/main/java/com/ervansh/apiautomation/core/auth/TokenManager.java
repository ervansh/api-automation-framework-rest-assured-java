package com.ervansh.apiautomation.core.auth;

import io.restassured.response.Response;

import java.util.concurrent.locks.ReentrantLock;

/** Thread-safe token lifecycle manager. Never logs token values. */
public final class TokenManager {
    private final AuthApiClient authApi;
    private final String username;
    private final String password;
    private final int tokenLifetimeMinutes;
    private final ReentrantLock refreshLock = new ReentrantLock();
    private volatile AuthTokens tokens;

    public TokenManager(AuthApiClient authApi, String username, String password, int tokenLifetimeMinutes) {
        if (tokenLifetimeMinutes <= 0) throw new IllegalArgumentException("Token lifetime must be positive");
        this.authApi = authApi;
        this.username = username;
        this.password = password;
        this.tokenLifetimeMinutes = tokenLifetimeMinutes;
    }

    public AuthTokens tokens() {
        if (tokens == null) {
            refreshLock.lock();
            try { if (tokens == null) tokens = login(); }
            finally { refreshLock.unlock(); }
        }
        return tokens;
    }

    public String accessToken() { return tokens().accessToken(); }

    /** Refreshes once; lock prevents concurrent callers from refreshing simultaneously. */
    public String refreshAccessToken(String rejectedAccessToken) {
        refreshLock.lock();
        try {
            if (tokens != null && !tokens.accessToken().equals(rejectedAccessToken)) {
                return tokens.accessToken();
            }
            if (tokens == null) tokens = login();
            Response response = authApi.refresh(tokens.refreshToken(), tokenLifetimeMinutes);
            response.then().statusCode(200);
            AuthTokens refreshed = response.as(AuthTokens.class);
            tokens = refreshed;
            return refreshed.accessToken();
        } finally {
            refreshLock.unlock();
        }
    }

    private AuthTokens login() {
        Response response = authApi.login(username, password, tokenLifetimeMinutes);
        response.then().statusCode(200);
        return response.as(AuthTokens.class);
    }
}
package com.company.ccm.shared.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("Unauthenticated");
        }
        return principal;
    }

    public static String currentUserId() {
        return currentUser().getId();
    }

    public static boolean hasRole(String role) {
        return currentUser().hasRole(role);
    }

    public static void requireAnyRole(String... roles) {
        UserPrincipal user = currentUser();
        for (String role : roles) {
            if (user.hasRole(role)) {
                return;
            }
        }
        throw new AccessDeniedException("Missing required role");
    }
}

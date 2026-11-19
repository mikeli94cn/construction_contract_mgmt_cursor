package com.company.ccm.organization.application;

import com.company.ccm.organization.domain.RefreshTokenEntity;
import com.company.ccm.organization.domain.RoleEntity;
import com.company.ccm.organization.domain.UserEntity;
import com.company.ccm.organization.infrastructure.RefreshTokenRepository;
import com.company.ccm.organization.infrastructure.UserRepository;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.JwtService;
import com.company.ccm.shared.security.SecurityProperties;
import com.company.ccm.shared.security.UserPrincipal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityProperties securityProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            SecurityProperties securityProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.securityProperties = securityProperties;
    }

    @Transactional
    public TokenResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return issueTokens(principal.getId(), principal.getUsername(), principal.getRoles());
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        String hash = sha256(refreshToken);
        RefreshTokenEntity stored = refreshTokenRepository
                .findByTokenHash(hash)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));
        if (stored.getRevokedAt() != null || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token expired or revoked");
        }
        stored.setRevokedAt(Instant.now());
        UserEntity user = userRepository
                .findById(stored.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<String> roles = user.getRoles().stream().map(RoleEntity::getCode).toList();
        return issueTokens(user.getId(), user.getUsername(), roles);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenHash(sha256(refreshToken)).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
        });
    }

    private TokenResponse issueTokens(String userId, String username, List<String> roles) {
        String accessToken = jwtService.createAccessToken(userId, username, roles);
        String refreshToken = randomToken();
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUserId(userId);
        entity.setTokenHash(sha256(refreshToken));
        entity.setExpiresAt(Instant.now().plusSeconds(securityProperties.getRefreshTokenDays() * 24 * 3600));
        refreshTokenRepository.save(entity);
        return new TokenResponse(accessToken, refreshToken, "Bearer", securityProperties.getAccessTokenMinutes() * 60);
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public record TokenResponse(String accessToken, String refreshToken, String tokenType, long expiresInSeconds) {}
}

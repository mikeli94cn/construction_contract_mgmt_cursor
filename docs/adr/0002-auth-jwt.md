# ADR-002: JWT Authentication

## Status
Accepted

## Context
The SPA frontend needs stateless authentication against the Spring Boot API.

## Decision
Use JWT access tokens (short-lived) plus refresh tokens stored hashed in the database. Passwords are hashed with BCrypt. Credentials come from environment variables / Docker secrets only.

## Consequences
- Stateless API scaling for access token validation.
- Refresh token revocation is possible via DB.
- Clients must handle token refresh and 401 retry.

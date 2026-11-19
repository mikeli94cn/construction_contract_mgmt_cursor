# ADR-001: Modular Monolith

## Status
Accepted

## Context
We need a construction contract management system with multiple business modules while keeping operational complexity low for a single-company deployment.

## Decision
Build a modular monolith with package-by-feature organization. Each feature module owns `api`, `application`, `domain`, and `infrastructure` packages. Cross-module references use IDs and application services, not JPA entity leakage.

## Consequences
- Single deployable artifact and simpler local development.
- Clear module boundaries support later extraction if needed.
- Requires discipline to avoid tight coupling across packages.

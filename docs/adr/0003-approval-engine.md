# ADR-003: Configurable Approval Engine

## Status
Accepted

## Context
Contracts, change orders, payments, and settlements require multi-step approval with role/user/project-role assignees.

## Decision
Implement an in-process configurable approval engine with definitions, ordered steps, instances, tasks, and append-only action history. Target aggregates react via callbacks when an instance is approved or rejected.

## Consequences
- Flexible workflows without an external BPM engine for MVP.
- One active approval instance per target.
- Reject returns the target to a editable/rejected state for resubmit.

-- Organization
CREATE TABLE departments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    parent_id VARCHAR(36) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_departments_parent FOREIGN KEY (parent_id) REFERENCES departments(id)
);

CREATE TABLE users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(64) NULL,
    department_id VARCHAR(36) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE roles (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    CONSTRAINT uk_roles_code UNIQUE (code)
);

CREATE TABLE user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE refresh_tokens (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_refresh_tokens_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- Project
CREATE TABLE projects (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    start_date DATE NULL,
    end_date DATE NULL,
    manager_user_id VARCHAR(36) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_projects_code UNIQUE (code),
    CONSTRAINT fk_projects_manager FOREIGN KEY (manager_user_id) REFERENCES users(id)
);

CREATE TABLE project_members (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    project_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    project_role VARCHAR(64) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_project_members UNIQUE (project_id, user_id),
    CONSTRAINT fk_project_members_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_project_members_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Partner
CREATE TABLE partners (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(32) NOT NULL,
    contact_name VARCHAR(128) NULL,
    contact_phone VARCHAR(64) NULL,
    tax_id VARCHAR(64) NULL,
    bank_name VARCHAR(128) NULL,
    bank_account VARCHAR(128) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_partners_code UNIQUE (code)
);

-- Contract
CREATE TABLE contracts (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    project_id VARCHAR(36) NOT NULL,
    partner_id VARCHAR(36) NOT NULL,
    contract_no VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(64) NOT NULL DEFAULT 'CONSTRUCTION',
    pricing_type VARCHAR(32) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'CNY',
    original_amount DECIMAL(19,4) NOT NULL,
    committed_amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    signed_date DATE NULL,
    effective_date DATE NULL,
    end_date DATE NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_contracts_no UNIQUE (contract_no),
    CONSTRAINT fk_contracts_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_contracts_partner FOREIGN KEY (partner_id) REFERENCES partners(id),
    CONSTRAINT fk_contracts_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_contracts_project ON contracts(project_id);
CREATE INDEX idx_contracts_status ON contracts(status);

-- Approval
CREATE TABLE approval_definitions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    target_type VARCHAR(32) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_approval_definitions_code UNIQUE (code)
);

CREATE TABLE approval_steps (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    definition_id VARCHAR(36) NOT NULL,
    step_order INT NOT NULL,
    name VARCHAR(128) NOT NULL,
    assignee_type VARCHAR(32) NOT NULL,
    assignee_value VARCHAR(128) NOT NULL,
    min_approvals INT NOT NULL DEFAULT 1,
    CONSTRAINT uk_approval_steps_order UNIQUE (definition_id, step_order),
    CONSTRAINT fk_approval_steps_definition FOREIGN KEY (definition_id) REFERENCES approval_definitions(id)
);

CREATE TABLE approval_instances (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    definition_id VARCHAR(36) NOT NULL,
    target_type VARCHAR(32) NOT NULL,
    target_id VARCHAR(36) NOT NULL,
    project_id VARCHAR(36) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'IN_PROGRESS',
    current_step_order INT NOT NULL DEFAULT 1,
    started_by VARCHAR(36) NOT NULL,
    started_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    completed_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_approval_instances_definition FOREIGN KEY (definition_id) REFERENCES approval_definitions(id),
    CONSTRAINT fk_approval_instances_started_by FOREIGN KEY (started_by) REFERENCES users(id)
);

CREATE INDEX idx_approval_instances_target ON approval_instances(target_type, target_id);
CREATE INDEX idx_approval_instances_status ON approval_instances(status);

CREATE TABLE approval_tasks (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    instance_id VARCHAR(36) NOT NULL,
    step_id VARCHAR(36) NOT NULL,
    assignee_user_id VARCHAR(36) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    comment VARCHAR(1000) NULL,
    acted_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_approval_tasks_instance FOREIGN KEY (instance_id) REFERENCES approval_instances(id),
    CONSTRAINT fk_approval_tasks_step FOREIGN KEY (step_id) REFERENCES approval_steps(id),
    CONSTRAINT fk_approval_tasks_assignee FOREIGN KEY (assignee_user_id) REFERENCES users(id)
);

CREATE INDEX idx_approval_tasks_assignee_status ON approval_tasks(assignee_user_id, status);

CREATE TABLE approval_actions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    instance_id VARCHAR(36) NOT NULL,
    task_id VARCHAR(36) NULL,
    actor_user_id VARCHAR(36) NOT NULL,
    action VARCHAR(32) NOT NULL,
    comment VARCHAR(1000) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_approval_actions_instance FOREIGN KEY (instance_id) REFERENCES approval_instances(id),
    CONSTRAINT fk_approval_actions_task FOREIGN KEY (task_id) REFERENCES approval_tasks(id),
    CONSTRAINT fk_approval_actions_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

-- Documents
CREATE TABLE documents (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    project_id VARCHAR(36) NULL,
    owner_type VARCHAR(32) NOT NULL,
    owner_id VARCHAR(36) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    size_bytes BIGINT NOT NULL,
    storage_key VARCHAR(512) NOT NULL,
    uploaded_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_documents_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_documents_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

CREATE INDEX idx_documents_owner ON documents(owner_type, owner_id);

-- Change orders
CREATE TABLE change_orders (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    contract_id VARCHAR(36) NOT NULL,
    change_no VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    amount_delta DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'CNY',
    reason VARCHAR(1000) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_change_orders_no UNIQUE (change_no),
    CONSTRAINT fk_change_orders_contract FOREIGN KEY (contract_id) REFERENCES contracts(id),
    CONSTRAINT fk_change_orders_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_change_orders_contract ON change_orders(contract_id);

-- Payments
CREATE TABLE payment_applications (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    contract_id VARCHAR(36) NOT NULL,
    payment_no VARCHAR(64) NOT NULL,
    payment_type VARCHAR(32) NOT NULL,
    period_label VARCHAR(64) NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'CNY',
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_payment_applications_no UNIQUE (payment_no),
    CONSTRAINT fk_payment_applications_contract FOREIGN KEY (contract_id) REFERENCES contracts(id),
    CONSTRAINT fk_payment_applications_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_payment_applications_contract ON payment_applications(contract_id);
CREATE INDEX idx_payment_applications_status ON payment_applications(status);

-- Settlements
CREATE TABLE settlements (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    contract_id VARCHAR(36) NOT NULL,
    settlement_no VARCHAR(64) NOT NULL,
    final_amount DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'CNY',
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    sealed_at TIMESTAMP(6) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_settlements_no UNIQUE (settlement_no),
    CONSTRAINT uk_settlements_contract UNIQUE (contract_id),
    CONSTRAINT fk_settlements_contract FOREIGN KEY (contract_id) REFERENCES contracts(id),
    CONSTRAINT fk_settlements_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Audit
CREATE TABLE audit_events (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    actor_user_id VARCHAR(36) NULL,
    action VARCHAR(64) NOT NULL,
    entity_type VARCHAR(64) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    project_id VARCHAR(36) NULL,
    detail_json JSON NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_audit_events_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

CREATE INDEX idx_audit_events_entity ON audit_events(entity_type, entity_id);
CREATE INDEX idx_audit_events_project ON audit_events(project_id);
CREATE INDEX idx_audit_events_created ON audit_events(created_at);

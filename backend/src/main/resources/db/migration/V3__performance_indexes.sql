-- Additional indexes for common query paths
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_partners_status ON partners(status);
CREATE INDEX idx_partners_type ON partners(type);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_change_orders_status ON change_orders(status);
CREATE INDEX idx_settlements_status ON settlements(status);
CREATE INDEX idx_approval_definitions_target_active ON approval_definitions(target_type, active);

-- Seed roles only; default admin user is created by DataSeeder with PasswordEncoder
INSERT INTO roles (id, code, name) VALUES
('11111111-1111-1111-1111-111111111001', 'SYSTEM_ADMIN', 'System Administrator'),
('11111111-1111-1111-1111-111111111002', 'PROJECT_ADMIN', 'Project Administrator'),
('11111111-1111-1111-1111-111111111003', 'CONTRACT_MANAGER', 'Contract Manager'),
('11111111-1111-1111-1111-111111111004', 'FINANCE', 'Finance'),
('11111111-1111-1111-1111-111111111005', 'APPROVER', 'Approver'),
('11111111-1111-1111-1111-111111111006', 'VIEWER', 'Viewer');

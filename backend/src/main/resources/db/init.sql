CREATE DATABASE IF NOT EXISTS dorm_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dorm_system;

-- 初始化管理员账号 (密码: admin123, BCrypt加密)
INSERT IGNORE INTO `user` (username, password, name, phone, email, role)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', '13800000000', 'admin@dorm.edu.cn', 'ADMIN');

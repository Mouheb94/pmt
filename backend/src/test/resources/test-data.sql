-- Désactivation des contraintes de clé étrangère
SET FOREIGN_KEY_CHECKS = 0;

-- Nettoyage des tables avant l'insertion
TRUNCATE TABLE notification;
TRUNCATE TABLE task_history;
TRUNCATE TABLE task;
TRUNCATE TABLE project_member;
TRUNCATE TABLE project;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE role;
TRUNCATE TABLE user;

-- Réactivation des contraintes de clé étrangère
SET FOREIGN_KEY_CHECKS = 1;

-- Insertion des rôles
INSERT INTO role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, name) VALUES (2, 'ROLE_ADMIN');

-- Insertion d'un utilisateur de test
INSERT INTO user (id, email, password, username, created_at, updated_at) 
VALUES (1, 'test@example.com', '$2a$10$X04fJ7V5bU6Zv5vQ5QY5Ue5QY5Ue5QY5Ue5QY5Ue5QY5Ue5QY5Ue', 'testuser', NOW(), NOW());

-- Association du rôle à l'utilisateur
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- Insertion d'un projet de test
INSERT INTO project (id, name, description, start_date, created_at, updated_at)
VALUES (1, 'Test Project', 'Test Description', NOW(), NOW(), NOW());

-- Insertion d'un membre du projet
INSERT INTO project_member (id, project_id, user_id, role)
VALUES (1, 1, 1, 'MEMBER');

-- Insertion d'une tâche de test
INSERT INTO task (id, name, description, status, priority, project_id, created_at, updated_at)
VALUES (1, 'Test Task', 'Test Description', 'TODO', 'MEDIUM', 1, NOW(), NOW());

-- Insertion d'une notification de test
INSERT INTO notification (id, message, read_not, sent_date, user_id, task_id)
VALUES (1, 'Test Notification', false, NOW(), 1, 1); 
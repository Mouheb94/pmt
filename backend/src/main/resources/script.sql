-- Création de la base de données
CREATE DATABASE IF NOT EXISTS pmt;
USE pmt;


-- Création des tables
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(id)
);

CREATE TABLE project_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    UNIQUE KEY unique_project_member (project_id, user_id)
);

CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'TODO',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    project_id BIGINT NOT NULL,
    assigned_to BIGINT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (assigned_to) REFERENCES user(id),
    FOREIGN KEY (created_by) REFERENCES user(id)
);

CREATE TABLE task_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    field_name VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_by BIGINT NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES task(id),
    FOREIGN KEY (changed_by) REFERENCES user(id)
);

-- Insertion des rôles
INSERT INTO role (name) VALUES 
('ROLE_ADMIN'),
('ROLE_MANAGER'),
('ROLE_USER');

-- Insertion des utilisateurs de test (mot de passe: 'password123')
INSERT INTO user (username, email, password, first_name, last_name) VALUES
('admin', 'admin@pmt.com', '$2a$10$rDkPvvAFV6GgJjXpYWYqXe6QZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'Admin', 'User'),
('manager', 'manager@pmt.com', '$2a$10$rDkPvvAFV6GgJjXpYWYqXe6QZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'Manager', 'User'),
('user1', 'user1@pmt.com', '$2a$10$rDkPvvAFV6GgJjXpYWYqXe6QZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'John', 'Doe'),
('user2', 'user2@pmt.com', '$2a$10$rDkPvvAFV6GgJjXpYWYqXe6QZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'Jane', 'Smith');

-- Insertion des projets de test
INSERT INTO project (name, description, start_date, end_date, status, created_by) VALUES
('Projet Web PMT', 'Développement de la plateforme de gestion de projet', '2024-01-01', '2024-06-30', 'IN_PROGRESS', 1),
('Refonte Interface', 'Modernisation de l''interface utilisateur', '2024-02-01', '2024-04-30', 'PLANNED', 2);

-- Attribution des membres aux projets
INSERT INTO project_member (project_id, user_id, role_id) VALUES
(1, 1, 1), -- Admin sur Projet Web PMT
(1, 2, 2), -- Manager sur Projet Web PMT
(1, 3, 3), -- User1 sur Projet Web PMT
(2, 2, 1), -- Manager sur Refonte Interface
(2, 4, 3); -- User2 sur Refonte Interface

-- Insertion des tâches de test
INSERT INTO task (title, description, status, priority, project_id, assigned_to, created_by) VALUES
('Configuration Backend', 'Mise en place de l''architecture Spring Boot', 'IN_PROGRESS', 'HIGH', 1, 1, 1),
('Développement Frontend', 'Création des composants Angular', 'TODO', 'MEDIUM', 1, 3, 2),
('Design UI/UX', 'Création des maquettes', 'DONE', 'LOW', 2, 4, 2);

-- Insertion d'historique de tâches
INSERT INTO task_history (task_id, field_name, old_value, new_value, changed_by) VALUES
(1, 'status', 'TODO', 'IN_PROGRESS', 1),
(2, 'priority', 'LOW', 'MEDIUM', 2),
(3, 'status', 'IN_PROGRESS', 'DONE', 2);

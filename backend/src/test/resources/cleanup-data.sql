-- Désactivation des contraintes de clé étrangère
SET FOREIGN_KEY_CHECKS = 0;

-- Nettoyage des tables après les tests
TRUNCATE TABLE user_roles;
TRUNCATE TABLE role;
TRUNCATE TABLE user;
TRUNCATE TABLE project;
TRUNCATE TABLE project_member;
TRUNCATE TABLE task;
TRUNCATE TABLE task_history;
TRUNCATE TABLE notification;

-- Réactivation des contraintes de clé étrangère
SET FOREIGN_KEY_CHECKS = 1; 
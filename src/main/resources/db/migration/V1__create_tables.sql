CREATE TABLE person (id INT, first_name VARCHAR(255), last_name VARCHAR(255), age INT);
CREATE TABLE robot (id INT, name VARCHAR(255), age INT);
CREATE TABLE address (owner_fk INT, street VARCHAR(255), zip INT, state VARCHAR(255));
CREATE TABLE troll (id INT, first_name VARCHAR(255), last_name VARCHAR(255), age INT);
CREATE TABLE user_role (id INT, user_fk INT, role_fk INT);
CREATE TABLE role (id INT, name VARCHAR(255));
CREATE TABLE role_permission (id INT, role_fk INT, permission_fk INT);
CREATE TABLE permission (id INT, name VARCHAR(255));

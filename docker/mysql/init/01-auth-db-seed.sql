
CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;


CREATE TABLE IF NOT EXISTS roles (
  role_id INT PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

INSERT INTO roles (role_id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_AGENT'),
(4, 'ROLE_MANAGER')
ON DUPLICATE KEY UPDATE name = VALUES(name);

CREATE TABLE IF NOT EXISTS users (
  user_id CHAR(36) PRIMARY KEY,
  email VARCHAR(255),
  is_active BIT,
  name VARCHAR(100),
  password VARCHAR(255),
  role_id INT,
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

INSERT INTO users (user_id, email, is_active, name, password, role_id) VALUES
('220bc5fa-14cc-4fcb-aa8e-7c08de825fb2','vsrreddy71@gmail.com',1,'vara','$2a$10$QdihXYOuLblPaMkCWn8Ze.AUzWcHeBOSBb/Cc88rJ5elYlwg9lfnK',1),
('25ef5920-dc01-4fc9-8537-237e3288613f','virupaswetha99@gmail.com',1,'Swetha','$2a$10$kntzATqTAtKnilT0QQX/P.KBsRSoA0hMaDkYMl1OGZhv6OoRaKgDa',3),
('2aa7bf14-f266-4cc8-9949-41807801b7f2','virupavaraprasa22@gmail.com',1,'vara','$2a$10$EY0WCmWpMl7QofDwM49VdOY3yBHWpIYCxWV/4SZlbXRJ/eHYB7ejK',2),
('64c1b198-bd17-4816-be67-0eb9a8bd3ec6','virupavaraprasad22@gmail.com',1,'UserVara','$2a$10$DRi6OSz2TMyvPORqKxMnD.SSZE/CcAD/gZ1N.TyoetmHesVSf46q.',2),
('da8302c9-7782-4d31-a133-cf28653c26b8','virupalikhitha@gmail.com',1,'Likhitha','$2a$10$HuAelqKjRgzctUtGuQCjUuxT6x/aXodquhHRBXbOcSplgC7/R8IGa',4);

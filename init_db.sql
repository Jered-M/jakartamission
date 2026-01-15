-- Créer les tables si elles n'existent pas

-- Table User
CREATE TABLE IF NOT EXISTS User (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    description VARCHAR(500)
);

-- Table Lieu (Place)
CREATE TABLE IF NOT EXISTS Lieu (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    latitude DOUBLE,
    longitude DOUBLE,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Insérer un utilisateur test
INSERT INTO User (username, email, password, description) 
VALUES ('admin', 'admin@example.com', '$2a$10$kPMqJYAj1lj/Aq7M6l5YHOkwNf5RWJYPM9GqjSKCPVp6ZxqVqN0u2', 'Admin User')
ON CONFLICT DO NOTHING;

-- Insérer un lieu test
INSERT INTO Lieu (name, description, latitude, longitude, user_id)
VALUES ('Bali', 'Belle île indonésienne', -8.3405, 115.1889, 
        (SELECT id FROM User WHERE email = 'admin@example.com'))
ON CONFLICT DO NOTHING;

COMMIT;

-- Create roles table
CREATE TABLE roles
(
    id   UUID PRIMARY KEY DEFAULT,
    name VARCHAR(255) NOT NULL
);

-- Create users table
CREATE TABLE users
(
    id       UUID PRIMARY KEY DEFAULT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL
);

-- Create user_roles junction table (Many-to-Many relationship)
CREATE TABLE user_roles
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS books, users, orders, order_items, authors, genres, book_authors, book_genres;

CREATE TABLE users (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('user', 'admin')  NOT NULL DEFAULT 'user',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    id             INT PRIMARY KEY AUTO_INCREMENT,
    title          VARCHAR(255)  NOT NULL,
    description    TEXT,
    isbn           VARCHAR(20) UNIQUE,
    published_year INT,
    price          DECIMAL(8, 2) NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active         BOOLEAN   DEFAULT TRUE
);
-- Added 14.11.2025
CREATE TABLE books_ja (
   id                INT PRIMARY KEY AUTO_INCREMENT,
   title_ja          VARCHAR(255)  NOT NULL,
   description_ja    TEXT,
   isbn_ja           VARCHAR(20) UNIQUE,
   published_year_ja INT,
   price_ja          DECIMAL(8, 2) NOT NULL,
   created_at_ja     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   active_ja         BOOLEAN   DEFAULT TRUE
);
-- Added 14.11.2025
CREATE TABLE books_ar (
   id                INT PRIMARY KEY AUTO_INCREMENT,
   title_ar          VARCHAR(255)  NOT NULL,
   description_ar    TEXT,
   isbn_ar           VARCHAR(20) UNIQUE,
   published_year_ar INT,
   price_ar          DECIMAL(8, 2) NOT NULL,
   created_at_ar     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   active_ar         BOOLEAN   DEFAULT TRUE
);

CREATE TABLE orders (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT            NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE order_items (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT           NOT NULL,
    book_id  INT           NOT NULL,
    quantity INT           NOT NULL DEFAULT 1,
    price    DECIMAL(8, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
);

CREATE TABLE authors (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);
-- Added 14.11.2025
CREATE TABLE authors_ja (
     id             INT PRIMARY KEY AUTO_INCREMENT,
     name_ja        VARCHAR(100) NOT NULL,
     description_ja TEXT
);
-- Added 14.11.2025
CREATE TABLE authors_ar (
     id             INT PRIMARY KEY AUTO_INCREMENT,
     name_ar        VARCHAR(100) NOT NULL,
     description_ar TEXT
);

CREATE TABLE genres (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);
-- Added 14.11.2025
CREATE TABLE genres_ja (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name_ja        VARCHAR(50) NOT NULL UNIQUE,
    description_ja TEXT
);
-- Added 14.11.2025
CREATE TABLE genres_ar (
    id             INT PRIMARY KEY AUTO_INCREMENT,
    name_ja        VARCHAR(50) NOT NULL UNIQUE,
    description_ja TEXT
);

CREATE TABLE book_authors (
    book_id   INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE
);

CREATE TABLE book_genres (
    book_id  INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (book_id, genre_id),
    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);

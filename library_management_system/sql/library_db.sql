CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    rental_rate_per_day DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_total_copies CHECK (total_copies >= 1),
    CONSTRAINT chk_available_copies CHECK (
        available_copies >= 0
        AND available_copies <= total_copies
    ),
    CONSTRAINT chk_rental_rate CHECK (rental_rate_per_day >= 0)
);

CREATE TABLE loans (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    checkout_date DATE NOT NULL,
    return_date DATE NULL,
    allowed_days INT NOT NULL DEFAULT 7,
    renewals INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_loan_book
        FOREIGN KEY (book_id)
        REFERENCES books(book_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_allowed_days CHECK (allowed_days >= 1),
    CONSTRAINT chk_renewals CHECK (renewals >= 0 AND renewals <= 2),
    CONSTRAINT chk_fine CHECK (fine >= 0)
);

-- Sample data from the original C++ project
INSERT INTO books(title, author, total_copies, available_copies, rental_rate_per_day)
VALUES
('The C++ Programming Language', 'Bjarne Stroustrup', 2, 2, 1.50),
('Clean Code', 'Robert C. Martin', 1, 1, 2.00),
('Harry Potter and the Philosopher''s Stone', 'J. K. Rowling', 3, 3, 1.00),
('Lord of the ring', 'J.R.R Toklien', 10, 10, 3.00);

-- Useful test queries
SELECT * FROM books;
SELECT * FROM loans;

SELECT
    l.loan_id,
    b.title,
    l.checkout_date,
    DATE_ADD(l.checkout_date, INTERVAL l.allowed_days DAY) AS due_date,
    l.renewals,
    l.active,
    l.fine
FROM loans l
JOIN books b ON b.book_id = l.book_id
ORDER BY l.loan_id;

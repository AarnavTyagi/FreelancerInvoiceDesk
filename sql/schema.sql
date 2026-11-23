-- Freelancer Invoice and Payment Desk (JAVA-P048)

CREATE DATABASE IF NOT EXISTS freelancer_desk;
USE freelancer_desk;

CREATE TABLE IF NOT EXISTS freelancers (
    freelancer_id INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    skill         VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS clients (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    company   VARCHAR(100) NOT NULL
);

-- Billable services offered by a freelancer, with a standard rate
CREATE TABLE IF NOT EXISTS services (
    service_id    INT AUTO_INCREMENT PRIMARY KEY,
    freelancer_id INT NOT NULL,
    description   VARCHAR(200) NOT NULL,
    unit_rate     DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (freelancer_id) REFERENCES freelancers(freelancer_id)
);

-- invoice_number is filled by the application right after the row is inserted
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id     INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(20) UNIQUE,
    freelancer_id  INT NOT NULL,
    client_id      INT NOT NULL,
    issue_date     DATE NOT NULL,
    due_date       DATE NOT NULL,
    tax_percent    DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (freelancer_id) REFERENCES freelancers(freelancer_id),
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

-- Each line of an invoice keeps its own copy of the description and rate
CREATE TABLE IF NOT EXISTS invoice_items (
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id  INT NOT NULL,
    description VARCHAR(200) NOT NULL,
    quantity    INT NOT NULL,
    unit_rate   DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id   INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id   INT NOT NULL,
    amount       DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
);

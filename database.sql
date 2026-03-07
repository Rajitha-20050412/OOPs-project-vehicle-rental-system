CREATE DATABASE vehicle_rental;

USE vehicle_rental;

CREATE TABLE vehicles (
vehicle_id VARCHAR(10) PRIMARY KEY,
brand VARCHAR(50),
model VARCHAR(50),
type VARCHAR(20),
price_per_day DOUBLE,
available BOOLEAN
);

INSERT INTO vehicles VALUES
("C101","Toyota","Camry","CAR",60,true),
("C202","BMW","X5","PREMIUM",120,true);

CREATE TABLE rentals (
id INT AUTO_INCREMENT PRIMARY KEY,
vehicle_id VARCHAR(10),
brand VARCHAR(50),
customer_name VARCHAR(50),
days INT,
total_price DOUBLE
);
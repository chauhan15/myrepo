DROP TABLE IF EXISTS Account;

CREATE TABLE Account (AccountNumber LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
AccountHolderName VARCHAR(30),
AccountBalance DECIMAL(19,4),
);

INSERT INTO Account (AccountHolderName,AccountBalance) VALUES ('Avanish',100.0000);
INSERT INTO Account (AccountHolderName,AccountBalance) VALUES ('Kumar',200.0000);
INSERT INTO Account (AccountHolderName,AccountBalance) VALUES ('David',500.0000);
INSERT INTO Account (AccountHolderName,AccountBalance) VALUES ('Albert',500.0000);
INSERT INTO Account (AccountHolderName,AccountBalance) VALUES ('Magda',500.0000);
INSERT INTO Account (AccountHolderName,AccountBalance) VALUES ('Robert',500.0000);

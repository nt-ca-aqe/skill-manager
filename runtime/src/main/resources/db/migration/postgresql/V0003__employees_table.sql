CREATE TABLE employees
(
    id      VARCHAR(36) NOT NULL,
    version INTEGER     NOT NULL,
    data    TEXT        NOT NULL,
    PRIMARY KEY (id)
);

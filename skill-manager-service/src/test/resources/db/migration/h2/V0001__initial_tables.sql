CREATE TABLE skills
(
    id       VARCHAR(36 CHAR) NOT NULL,
    version  INT NOT NULL,
    data     CLOB NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE projects
(
    id       VARCHAR(36 CHAR) NOT NULL,
    version  INT NOT NULL,
    data     CLOB NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE employees
(
    id          VARCHAR(36 CHAR) NOT NULL,
    version     INT NOT NULL,
    data        CLOB NOT NULL,
    PRIMARY KEY (id)
);

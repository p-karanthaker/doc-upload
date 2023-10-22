CREATE TABLE IF NOT EXISTS job (
    id INT NOT NULL,
    document_name VARCHAR(255),
    document_key UUID,
    status TEXT NOT NULL,
    notes VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS job_seq INCREMENT 50;

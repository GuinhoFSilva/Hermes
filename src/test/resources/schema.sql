CREATE TABLE matches (
id CHAR(36) PRIMARY KEY,
status VARCHAR(40),
created_at DATETIME,
end_at DATETIME
);

CREATE TABLE participants (
id INT PRIMARY KEY AUTO_INCREMENT,
id_match CHAR(36),
id_player CHAR(36),
FOREIGN KEY (id_match) REFERENCES matches(id)
);
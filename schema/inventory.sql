DROP TABLE inventory;

CREATE TABLE inventory (
  id INT SERIAL DEFAULT VALUE,
  name TEXT,
  quantity INT
);
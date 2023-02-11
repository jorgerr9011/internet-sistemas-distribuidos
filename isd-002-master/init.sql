CREATE
    DATABASE ws;
CREATE
    DATABASE wstest;

CREATE
    USER 'ws'@'%' IDENTIFIED BY 'ws';
CREATE
    USER 'wstest'@'%' IDENTIFIED BY 'wstest';

GRANT ALL PRIVILEGES ON *.* TO
    'ws'@'%';
GRANT ALL PRIVILEGES ON wstest.* TO
    'wstest'@'%';

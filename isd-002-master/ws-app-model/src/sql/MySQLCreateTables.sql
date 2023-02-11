DROP TABLE IF EXISTS Response;
DROP TABLE IF EXISTS Event;

-- --------------------------------- Event ------------------------------------
CREATE TABLE Event
(
    eventId         BIGINT                           NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) COLLATE latin1_bin  NOT NULL,
    description     VARCHAR(1024) COLLATE latin1_bin NOT NULL,
    celebrationDate DATETIME                         NOT NULL,
    duration        SMALLINT                         NOT NULL,
    creationDate    DATETIME                         NOT NULL,
    cancelled       BIT                              NOT NULL,
    numSi           SMALLINT                         NOT NULL,
    numNo           SMALLINT                         NOT NULL,

    CONSTRAINT EventPK PRIMARY KEY (eventId),
    CONSTRAINT validDuration CHECK ( duration > 0 )
) ENGINE = InnoDB;

-- --------------------------------- Response ------------------------------------
CREATE TABLE Response
(
    responseId   BIGINT                         NOT NULL AUTO_INCREMENT,
    eventId      BIGINT                         NOT NULL,
    userEmail    VARCHAR(40) COLLATE latin1_bin NOT NULL,
    responseDate DATETIME                       NOT NULL,
    attending    BIT                            NOT NULL,

    CONSTRAINT ResponsePK PRIMARY KEY (responseId),
    CONSTRAINT ResponseEventIdFK FOREIGN KEY (eventId)
        REFERENCES Event (eventId) ON DELETE CASCADE
) ENGINE = InnoDB;

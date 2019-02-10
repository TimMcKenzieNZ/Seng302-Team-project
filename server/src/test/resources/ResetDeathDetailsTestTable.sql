DROP TABLE `seng302-2018-team600-test`.`DeathDetails`;

CREATE TABLE `DeathDetails` (
 `dateOfDeath` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `country` varchar(30) NOT NULL,
 `region` varchar(30) NOT NULL,
 `city` varchar(30) NOT NULL,
 `ddUsername` varchar(30) NOT NULL,
 PRIMARY KEY (`ddUsername`),
 CONSTRAINT `ddUsername` FOREIGN KEY (`ddUsername`) REFERENCES `Users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8
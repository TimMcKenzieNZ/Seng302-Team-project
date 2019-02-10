DROP TABLE `seng302-2018-team600-prod`.`ContactDetails`;
DROP TABLE `seng302-2018-team600-prod`.`AffectedOrgans`;
DROP TABLE `seng302-2018-team600-prod`.`MedicationLogs`;
DROP TABLE `seng302-2018-team600-prod`.`Medications`;
DROP TABLE `seng302-2018-team600-prod`.`Illnesses`;
DROP TABLE `seng302-2018-team600-prod`.`MedicalProcedures`;
DROP TABLE `seng302-2018-team600-prod`.`LogEntries`;
DROP TABLE `seng302-2018-team600-prod`.`DonorReceivers`;
DROP TABLE `seng302-2018-team600-prod`.`Clinicians`;
DROP TABLE `seng302-2018-team600-prod`.`Administrators`;
DROP TABLE `seng302-2018-team600-prod`.`Users`;


CREATE TABLE `seng302-2018-team600-prod`.`Users` ( `username` VARCHAR(30) NOT NULL , `firstName` VARCHAR(50) NOT NULL , `middleName` VARCHAR(50) NOT NULL , `lastName` VARCHAR(50) NOT NULL , `password` VARCHAR(50) NOT NULL , `active` BOOLEAN NOT NULL DEFAULT TRUE , `creationDate` DATETIME NOT NULL, `version` INT(11) NOT NULL, `userType` VARCHAR(10) NOT NULL, PRIMARY KEY (`username`)) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`Administrators` ( `username` VARCHAR(30) NOT NULL , PRIMARY KEY (`username`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`Clinicians` ( `username` VARCHAR(30) NOT NULL , PRIMARY KEY (`username`(30)), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`DonorReceivers` ( `username` VARCHAR(30) NOT NULL , `preferredName` VARCHAR(100) NULL, `livedInUKFlag` BOOLEAN NULL , `activeFlag` BOOLEAN NULL , `dateOfDeath` DATE NULL , `dateOfBirth` DATE NOT NULL , `birthGender` CHAR NULL , `title` VARCHAR(10) NULL , `gender` CHAR NULL , `height` DOUBLE NULL , `weight` DOUBLE NULL , `bloodType` VARCHAR(3) NULL , `bloodPressure` VARCHAR(20) NULL , `smoker` BOOLEAN NULL , `alcoholConsumption` DOUBLE NULL , `bodyMassIndexFlag` BOOLEAN NULL , `dLiver` BOOLEAN NOT NULL DEFAULT FALSE , `dKidneys` BOOLEAN NOT NULL DEFAULT FALSE , `dPancreas` BOOLEAN NOT NULL DEFAULT FALSE , `dHeart` BOOLEAN NOT NULL DEFAULT FALSE , `dLungs` BOOLEAN NOT NULL DEFAULT FALSE , `dIntestine` BOOLEAN NOT NULL DEFAULT FALSE , `dCorneas` BOOLEAN NOT NULL DEFAULT FALSE , `dMiddleEars` BOOLEAN NOT NULL DEFAULT FALSE , `dSkin` BOOLEAN NOT NULL DEFAULT FALSE , `dBone` BOOLEAN NOT NULL DEFAULT FALSE , `dBoneMarrow` BOOLEAN NOT NULL DEFAULT FALSE , `dConnectiveTissue` BOOLEAN NOT NULL DEFAULT FALSE , `rLiver` BOOLEAN NOT NULL DEFAULT FALSE , `rKidneys` BOOLEAN NOT NULL DEFAULT FALSE , `rPancreas` BOOLEAN NOT NULL DEFAULT FALSE , `rHeart` BOOLEAN NOT NULL DEFAULT FALSE , `rLungs` BOOLEAN NOT NULL DEFAULT FALSE , `rIntestine` BOOLEAN NOT NULL DEFAULT FALSE , `rCorneas` BOOLEAN NOT NULL DEFAULT FALSE , `rMiddleEars` BOOLEAN NOT NULL DEFAULT FALSE , `rSkin` BOOLEAN NOT NULL DEFAULT FALSE , `rBone` BOOLEAN NOT NULL DEFAULT FALSE , `rBoneMarrow` BOOLEAN NOT NULL DEFAULT FALSE , `rConnectiveTissue` BOOLEAN NOT NULL DEFAULT FALSE , `rTimeLiver` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeKidneys` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimePancreas` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeHeart` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeLungs` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeIntestine` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeCorneas` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeMiddleEars` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeSkin` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeBone` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeBoneMarrow` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', `rTimeConnectiveTissue` DATETIME(3) NULL DEFAULT '1970-01-01 01:00:00.000', PRIMARY KEY (`username`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`LogEntries` ( `username` VARCHAR(30) NOT NULL , `valChanged` VARCHAR(200) NOT NULL , `changeTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, `modifyingAccount` VARCHAR(170) NOT NULL , `accountModified` VARCHAR(170) NOT NULL , `originalVal` VARCHAR(1000) NOT NULL , `changedVal` VARCHAR(1000) NOT NULL , PRIMARY KEY (`username`, `changeTime`, `modifyingAccount`, `valChanged`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`MedicalProcedures` ( `username` VARCHAR(30) NOT NULL , `summary` VARCHAR(100) NOT NULL , `date` DATETIME NOT NULL , `description` VARCHAR(1000) NOT NULL , PRIMARY KEY (`username`, `summary`, `date`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`Illnesses` ( `username` VARCHAR(30) NOT NULL , `name` VARCHAR(50) NOT NULL , `date` DATE NOT NULL , `cured` BOOLEAN NOT NULL , `chronic` BOOLEAN NOT NULL , PRIMARY KEY (`username`, `name`, `date`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`Medications` (`username` varchar(30) NOT NULL, `name` varchar(50) NOT NULL,`isCurrent` tinyint(1) NOT NULL, PRIMARY KEY (`username`,`name`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`MedicationLogs` ( `username` VARCHAR(30) NOT NULL , `medicationLog` VARCHAR(200) NOT NULL , PRIMARY KEY (`username`, `medicationLog`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`AffectedOrgans` ( `username` VARCHAR(30) NOT NULL , `summary` VARCHAR(100) NOT NULL , `affectedOrganName` VARCHAR(20) NOT NULL , `date` DATE NOT NULL , PRIMARY KEY (`username`, `summary`, `affectedOrganName`, `date`), FOREIGN KEY (`username`, `summary`) REFERENCES `MedicalProcedures`(`username`, `summary`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;

CREATE TABLE `seng302-2018-team600-prod`.`ContactDetails` ( `mobileNumber` VARCHAR(12) NOT NULL , `username` VARCHAR(30) NOT NULL , `homeNumber` VARCHAR(12) NULL , `email` VARCHAR(254) NULL , `streetAddressLineOne` VARCHAR(100) NULL , `streetAddressLineTwo` VARCHAR(100) NULL , `suburb` VARCHAR(100) NULL , `city` VARCHAR(100) NULL , `region` VARCHAR(100) NULL , `postCode` VARCHAR(4) NULL , `countryCode` VARCHAR(100) NULL , `emergency` BOOLEAN NOT NULL DEFAULT FALSE, PRIMARY KEY (`mobileNumber`, `username`, `emergency`), FOREIGN KEY (`username`) REFERENCES `Users`(`username`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;
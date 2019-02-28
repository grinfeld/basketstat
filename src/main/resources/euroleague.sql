-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema euroleague
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema euroleague
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `euroleague` DEFAULT CHARACTER SET utf8;
USE `euroleague`;

-- -----------------------------------------------------
-- Table `euroleague`.`commands`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`commands`
(
  `id`           INT          NOT NULL AUTO_INCREMENT,
  `command_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `command_name` (`command_name` ASC)
)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `euroleague`.`tournaments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`tournaments`
(
  `id`         INT          NOT NULL AUTO_INCREMENT,
  `tourn_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT INDEX `tourn_name_ind` (`tourn_name` ASC)
)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `euroleague`.`results`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`results`
(
  `result_id`       INT        NOT NULL AUTO_INCREMENT,
  `command_id`      INT        NOT NULL,
  `tournament_id`   INT        NOT NULL,
  `date`            DATE       NOT NULL,
  `attempts3points` INT        NULL DEFAULT 0,
  `scored3points`   INT        NULL DEFAULT 0,
  `attempts2points` INT        NULL DEFAULT 0,
  `scored2points`   INT        NULL DEFAULT 0,
  `attempts1points` INT        NULL DEFAULT 0,
  `scored1points`   INT        NULL DEFAULT 0,
  `score_in`        INT        NULL DEFAULT 0,
  `score_out`       INT        NULL DEFAULT 0,
  `home_match`      TINYINT(1) NOT NULL,
  `season`          VARCHAR(8) NOT NULL,
  INDEX `fk_results_commands_idx` (`command_id` ASC),
  INDEX `date_index` (`date` DESC),
  INDEX `date_and_command` (`command_id` ASC, `tournament_id` ASC, `date` ASC),
  INDEX `fk_results_tournaments1_idx` (`tournament_id` ASC),
  FULLTEXT INDEX `season_fs` (`season` DESC),
  INDEX `date_cmd_tour_index` (`tournament_id` ASC, `command_id` ASC, `date` ASC),
  INDEX `tourn_ind` (`tournament_id` ASC, `date` ASC),
  PRIMARY KEY (`result_id`),
  CONSTRAINT `fk_results_commands`
    FOREIGN KEY (`command_id`)
      REFERENCES `euroleague`.`commands` (`id`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,
  CONSTRAINT `fk_results_tournaments1`
    FOREIGN KEY (`tournament_id`)
      REFERENCES `euroleague`.`tournaments` (`id`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
)
  ENGINE = InnoDB;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;

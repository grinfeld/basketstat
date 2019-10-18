-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema euroleague
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema euroleague
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `euroleague` DEFAULT CHARACTER SET utf8 ;
USE `euroleague` ;

-- -----------------------------------------------------
-- Table `euroleague`.`commands`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`commands` (
                                                       `id` INT NOT NULL AUTO_INCREMENT,
                                                       `command_name` VARCHAR(255) NOT NULL,
                                                       PRIMARY KEY (`id`),
                                                       FULLTEXT INDEX `command_name` (`command_name` ASC))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `euroleague`.`tournaments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`tournaments` (
                                                          `id` INT NOT NULL AUTO_INCREMENT,
                                                          `tourn_name` VARCHAR(255) NOT NULL,
                                                          PRIMARY KEY (`id`),
                                                          FULLTEXT INDEX `tourn_name_ind` (`tourn_name` ASC))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `euroleague`.`results`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`results` (
                                                      `result_id` INT NOT NULL AUTO_INCREMENT,
                                                      `command_id` INT NOT NULL,
                                                      `tournament_id` INT NOT NULL,
                                                      `date` DATE NOT NULL,
                                                      `attempts3points` INT NULL DEFAULT 0,
                                                      `scored3points` INT NULL DEFAULT 0,
                                                      `attempts2points` INT NULL DEFAULT 0,
                                                      `scored2points` INT NULL DEFAULT 0,
                                                      `attempts1points` INT NULL DEFAULT 0,
                                                      `scored1points` INT NULL DEFAULT 0,
                                                      `score_in` INT NULL DEFAULT 0,
                                                      `score_out` INT NULL DEFAULT 0,
                                                      `home_match` TINYINT(1) NOT NULL,
                                                      `season` VARCHAR(8) NOT NULL,
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
                                                              ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `euroleague`.`matches`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`matches` (
                                                      `match_id` INT NOT NULL AUTO_INCREMENT,
                                                      `tournament_id` INT NOT NULL,
                                                      `date` DATE NOT NULL,
                                                      `home_command_id` INT NOT NULL,
                                                      `away_command_id` INT NOT NULL,
                                                      `season` VARCHAR(8) NOT NULL,
                                                      `score_home` INT NOT NULL DEFAULT 0,
                                                      `score_away` INT NOT NULL DEFAULT 0,
                                                      PRIMARY KEY (`match_id`),
                                                      UNIQUE INDEX `match_id_UNIQUE` (`match_id` ASC),
                                                      INDEX `fk_match_tournaments1_idx` (`tournament_id` ASC),
                                                      INDEX `fk_match_commands1_idx` (`home_command_id` ASC),
                                                      INDEX `fk_match_commands2_idx` (`away_command_id` ASC),
                                                      CONSTRAINT `fk_match_tournaments1`
                                                          FOREIGN KEY (`tournament_id`)
                                                              REFERENCES `euroleague`.`tournaments` (`id`)
                                                              ON DELETE NO ACTION
                                                              ON UPDATE NO ACTION,
                                                      CONSTRAINT `fk_match_commands1`
                                                          FOREIGN KEY (`home_command_id`)
                                                              REFERENCES `euroleague`.`commands` (`id`)
                                                              ON DELETE NO ACTION
                                                              ON UPDATE NO ACTION,
                                                      CONSTRAINT `fk_match_commands2`
                                                          FOREIGN KEY (`away_command_id`)
                                                              REFERENCES `euroleague`.`commands` (`id`)
                                                              ON DELETE NO ACTION
                                                              ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `euroleague`.`match_to_command_stats`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`match_to_command_stats` (
                                                                     `command_stats_id` INT NOT NULL AUTO_INCREMENT,
                                                                     `match_id` INT NOT NULL,
                                                                     `command_id` INT NOT NULL,
                                                                     `rebounds_defense` INT NOT NULL DEFAULT 0,
                                                                     `rebounds_offense` INT NOT NULL DEFAULT 0,
                                                                     `assists` INT NOT NULL DEFAULT 0,
                                                                     `fouls_defense` INT NOT NULL DEFAULT 0,
                                                                     `more_10_points` INT NOT NULL DEFAULT 0,
                                                                     `player_max_points_name` VARCHAR(255) NULL,
                                                                     `player_max_points_score` INT NULL DEFAULT 0,
                                                                     `max_lead` INT NULL,
                                                                     `max_lead_quarter` VARCHAR(3) NULL,
                                                                     `score_start5_score` INT NOT NULL DEFAULT 0,
                                                                     `score_bench_score` INT NOT NULL DEFAULT 0,
                                                                     `steals` INT NOT NULL DEFAULT 0,
                                                                     `turnovers` INT NOT NULL DEFAULT 0,
                                                                     `second_chance_attempt` INT NULL DEFAULT 0,
                                                                     PRIMARY KEY (`command_stats_id`),
                                                                     INDEX `fk_command_stats_match1_idx` (`match_id` ASC),
                                                                     INDEX `fk_command_stats_match2_idx` (`command_id` ASC),
                                                                     CONSTRAINT `fk_command_stats_match1`
                                                                         FOREIGN KEY (`match_id`)
                                                                             REFERENCES `euroleague`.`matches` (`match_id`)
                                                                             ON DELETE NO ACTION
                                                                             ON UPDATE NO ACTION,
                                                                     CONSTRAINT `fk_command_stats_match2`
                                                                         FOREIGN KEY (`command_id`)
                                                                             REFERENCES `euroleague`.`matches` (`home_command_id`)
                                                                             ON DELETE NO ACTION
                                                                             ON UPDATE NO ACTION,
                                                                     CONSTRAINT `fk_command_stats_match3`
                                                                         FOREIGN KEY (`command_id`)
                                                                             REFERENCES `euroleague`.`matches` (`away_command_id`)
                                                                             ON DELETE NO ACTION
                                                                             ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `euroleague`.`command_match_by_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `euroleague`.`command_match_by_types` (
                                                                     `command_match_by_type_id` INT NOT NULL AUTO_INCREMENT,
                                                                     `command_stats_id` INT NOT NULL,
                                                                     `quarter` VARCHAR(3) NOT NULL COMMENT '1st,2nd,3rd,4th,OT',
                                                                     `points1` INT(11) NULL DEFAULT 0,
                                                                     `attempts1` INT(11) NULL DEFAULT 0,
                                                                     `points2` INT(11) NULL DEFAULT 0,
                                                                     `attempts2` INT(11) NULL DEFAULT 0,
                                                                     `points3` INT(11) NULL DEFAULT 0,
                                                                     `attempts3` INT(11) NULL DEFAULT 0,
                                                                     PRIMARY KEY (`command_match_by_type_id`),
                                                                     INDEX `fk_command_match_by_types_match_to_command_stats1_idx` (`command_stats_id` ASC),
                                                                     CONSTRAINT `fk_command_match_by_types_match_to_command_stats1`
                                                                         FOREIGN KEY (`command_stats_id`)
                                                                             REFERENCES `euroleague`.`match_to_command_stats` (`command_stats_id`)
                                                                             ON DELETE NO ACTION
                                                                             ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

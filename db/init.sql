-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 01, 2024 at 06:22 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

CREATE DATABASE IF NOT EXISTS thitructuyen;
USE thitructuyen;

SET
SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET
time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `thitructuyen`
--


-- --------------------------------------------------------

--
-- Table structure for table `choices`
--

CREATE TABLE `choices`
(
    `id`           int(11) NOT NULL,
    `question_id`  int(11) NOT NULL,
    `choice_index` int(11) NOT NULL,
    `choice_text`  text               DEFAULT NULL,
    `image_url`    varchar(255)       DEFAULT NULL,
    `is_correct`   tinyint(1) DEFAULT NULL,
    `status`       int(11) DEFAULT 0,
    `created_at`   timestamp NOT NULL DEFAULT current_timestamp(),
    `updated_at`   timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp ()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exams`
--

CREATE TABLE `exams`
(
    `id`          int(11) NOT NULL,
    `title`       varchar(255) NOT NULL,
    `description` text                  DEFAULT NULL,
    `image_url`   varchar(255)          DEFAULT NULL,
    `created_at`  timestamp    NOT NULL DEFAULT current_timestamp(),
    `updated_at`  timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp (),
    `user_id`     int(11) DEFAULT NULL,
    `status`      int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_answers`
--

CREATE TABLE `exam_answers`
(
    `id`              int(11) NOT NULL,
    `exam_session_id` int(11) NOT NULL,
    `question_id`     int(11) NOT NULL,
    `choice_id`       int(11) DEFAULT NULL,
    `user_id`         int(11) DEFAULT NULL,
    `answer_time`     timestamp DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_sessions`
--

CREATE TABLE `exam_sessions`
(
    `id`         int(11) NOT NULL,
    `exam_id`    int(11) NOT NULL,
    `host_id`    int(11) NOT NULL,
    `start_time` timestamp  DEFAULT current_timestamp(),
    `end_time`   timestamp  DEFAULT current_timestamp(),
    `status`     int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions`
(
    `id`             int(11) NOT NULL,
    `exam_id`        int(11) NOT NULL,
    `question_index` int(11) NOT NULL,
    `question_text`  text         DEFAULT NULL,
    `image_url`      varchar(255) DEFAULT NULL,
    `time`           int(11) DEFAULT 0,
    `status`         int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users`
(
    `id`                int(11) NOT NULL,
    `username`          varchar(20)  DEFAULT NULL,
    `password`          varchar(255) DEFAULT NULL,
    `player_name`       varchar(20)  DEFAULT NULL,
    `gender`            int(11) DEFAULT NULL,
    `email`             varchar(255) DEFAULT NULL,
    `phone`             varchar(10)  DEFAULT NULL,
    `active`            int(11) DEFAULT NULL,
    `relogin_token`     varchar(255) DEFAULT NULL,
    `phone_otp`         varchar(6)   DEFAULT NULL,
    `phone_otp_time`    mediumtext   DEFAULT NULL,
    `is_phone_verified` int(11) DEFAULT NULL,
    `email_code`        varchar(6)   DEFAULT NULL,
    `email_code_time`   mediumtext   DEFAULT NULL,
    `is_email_verified` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `player_name`, `gender`, `email`, `phone`, `active`, `relogin_token`,
                     `phone_otp`, `phone_otp_time`, `is_phone_verified`, `email_code`, `email_code_time`,
                     `is_email_verified`)
VALUES (2, 'haonghai', '$2a$12$fOat4ELq3HZW.', NULL, NULL, NULL, NULL, NULL, '123456', NULL, NULL, NULL, NULL, NULL,
        NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `choices`
--
ALTER TABLE `choices`
    ADD PRIMARY KEY (`id`),
    ADD KEY `choices_questions_id_fk` (`question_id`);

--
-- Indexes for table `exams`
--
ALTER TABLE `exams`
    ADD PRIMARY KEY (`id`),
    ADD KEY `fk_exams_users` (`user_id`);

--
-- Indexes for table `exam_answers`
--
ALTER TABLE `exam_answers`
    ADD PRIMARY KEY (`id`),
    ADD KEY `fk_exam_answers_exam_sessions` (`exam_session_id`),
    ADD KEY `fk_exam_answers_questions` (`question_id`),
    ADD KEY `fk_exam_answers_choices` (`choice_id`),
    ADD KEY `exam_answers_users_id_fk` (`user_id`);

--
-- Indexes for table `exam_sessions`
--
ALTER TABLE `exam_sessions`
    ADD PRIMARY KEY (`id`),
    ADD KEY `fk_exam_sessions_exams` (`exam_id`),
    ADD KEY `fk_exam_sessions_users` (`host_id`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
    ADD PRIMARY KEY (`id`),
    ADD KEY `questions_exams_id_fk` (`exam_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `choices`
--
ALTER TABLE `choices`
    MODIFY `id` int (11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `exams`
--
ALTER TABLE `exams`
    MODIFY `id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `exam_answers`
--
ALTER TABLE `exam_answers`
    MODIFY `id` int (11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_sessions`
--
ALTER TABLE `exam_sessions`
    MODIFY `id` int (11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
    MODIFY `id` int (11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `choices`
--
ALTER TABLE `choices`
    ADD CONSTRAINT `choices_questions_id_fk` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`);

--
-- Constraints for table `exams`
--
ALTER TABLE `exams`
    ADD CONSTRAINT `exams_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `exam_answers`
--
ALTER TABLE `exam_answers`
    ADD CONSTRAINT `exam_answers_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    ADD CONSTRAINT `fk_exam_answers_choices` FOREIGN KEY (`choice_id`) REFERENCES `choices` (`id`) ON
DELETE
CASCADE,
    ADD CONSTRAINT `fk_exam_answers_exam_sessions` FOREIGN KEY (`exam_session_id`) REFERENCES `exam_sessions` (`id`) ON DELETE
CASCADE,
    ADD CONSTRAINT `fk_exam_answers_questions` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE
CASCADE;

--
-- Constraints for table `exam_sessions`
--
ALTER TABLE `exam_sessions`
    ADD CONSTRAINT `fk_exam_sessions_exams` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `fk_exam_sessions_users` FOREIGN KEY (`host_id`) REFERENCES `users` (`id`) ON
DELETE
CASCADE;

--
-- Constraints for table `questions`
--
ALTER TABLE `questions`
    ADD CONSTRAINT `questions_exams_id_fk` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

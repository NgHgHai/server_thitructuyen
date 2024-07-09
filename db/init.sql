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
-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 08, 2024 at 02:35 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


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

CREATE TABLE `choices` (
                           `id` int(11) NOT NULL,
                           `question_id` int(11) NOT NULL,
                           `choice_index` int(11) NOT NULL,
                           `choice_text` text DEFAULT NULL,
                           `image_url` varchar(255) DEFAULT NULL,
                           `is_correct` tinyint(1) DEFAULT NULL,
                           `status` int(11) DEFAULT 0,
                           `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
                           `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `choices`
--

INSERT INTO `choices` (`id`, `question_id`, `choice_index`, `choice_text`, `image_url`, `is_correct`, `status`, `created_at`, `updated_at`) VALUES
                                                                                                                                                (65, 17, 1, 'docker up', 'docker up', 0, 0, '2024-07-06 01:21:03', '2024-07-06 01:22:39'),
                                                                                                                                                (66, 17, 2, 'docker composer up', 'docker composer up', 0, 0, '2024-07-06 01:21:03', '2024-07-06 01:22:39'),
                                                                                                                                                (67, 17, 3, 'docker compose up --build --force-recreate -d', 'docker compose up --build --force-recreate -d', 0, 0, '2024-07-06 01:21:03', '2024-07-06 01:22:39'),
                                                                                                                                                (68, 17, 4, 'no answer right', 'no answer right', 1, 0, '2024-07-06 01:21:03', '2024-07-06 01:22:39'),
                                                                                                                                                (69, 18, 1, 'compine code ', 'compine code ', 1, 0, '2024-07-06 01:22:42', '2024-07-07 14:41:01'),
                                                                                                                                                (70, 18, 2, 'deploy code', 'deploy code', 1, 0, '2024-07-06 01:22:42', '2024-07-07 22:50:50'),
                                                                                                                                                (71, 18, 3, 'run code', 'run code', 0, 0, '2024-07-06 01:22:42', '2024-07-06 01:25:03'),
                                                                                                                                                (72, 18, 4, 'dev code', 'dev code', 0, 0, '2024-07-06 01:22:42', '2024-07-07 14:41:01'),
                                                                                                                                                (73, 19, 1, 'yes', 'yes', 0, 0, '2024-07-06 01:25:06', '2024-07-06 01:27:06'),
                                                                                                                                                (74, 19, 2, 'no, only support for linux', 'no, only support for linux', 0, 0, '2024-07-06 01:25:06', '2024-07-06 01:27:06'),
                                                                                                                                                (75, 19, 3, 'no, support linux and windown', 'no, support linux and windown', 0, 0, '2024-07-06 01:25:06', '2024-07-06 01:27:06'),
                                                                                                                                                (76, 19, 4, 'no only mac', 'no only mac', 1, 0, '2024-07-06 01:25:06', '2024-07-06 01:27:06'),
                                                                                                                                                (77, 20, 1, '4', '4', 0, 0, '2024-07-06 01:27:10', '2024-07-06 01:28:25'),
                                                                                                                                                (78, 20, 2, '0', '0', 0, 0, '2024-07-06 01:27:10', '2024-07-06 01:28:25'),
                                                                                                                                                (79, 20, 3, '8', '8', 0, 0, '2024-07-06 01:27:10', '2024-07-06 01:28:25'),
                                                                                                                                                (80, 20, 4, '\"null\"', '\"null\"', 1, 0, '2024-07-06 01:27:10', '2024-07-06 01:28:25'),
                                                                                                                                                (81, 21, 1, 'ha noi', 'ha noi', 1, 0, '2024-07-06 01:28:28', '2024-07-07 14:34:46'),
                                                                                                                                                (82, 21, 2, 'ho chi minh', 'ho chi minh', 0, 0, '2024-07-06 01:28:28', '2024-07-06 01:29:13'),
                                                                                                                                                (83, 21, 3, 'hue', 'hue', 0, 0, '2024-07-06 01:28:28', '2024-07-06 01:29:13'),
                                                                                                                                                (84, 21, 4, 'nha trang', 'nha trang', 0, 0, '2024-07-06 01:28:28', '2024-07-07 14:34:46'),
                                                                                                                                                (353, 89, 1, '3', '3', 0, 0, '2024-07-07 01:58:11', '2024-07-07 01:58:32'),
                                                                                                                                                (354, 89, 2, '6', '6', 1, 0, '2024-07-07 01:58:11', '2024-07-07 01:58:32'),
                                                                                                                                                (355, 89, 3, '7', '7', 0, 0, '2024-07-07 01:58:11', '2024-07-07 01:58:32'),
                                                                                                                                                (356, 89, 4, '9', '9', 0, 0, '2024-07-07 01:58:11', '2024-07-07 01:58:32'),
                                                                                                                                                (357, 90, 1, '21', '21', 1, 0, '2024-07-07 01:58:38', '2024-07-07 01:59:07'),
                                                                                                                                                (358, 90, 2, '22', '22', 0, 0, '2024-07-07 01:58:38', '2024-07-07 01:59:07'),
                                                                                                                                                (359, 90, 3, '20', '20', 0, 0, '2024-07-07 01:58:38', '2024-07-07 01:59:07'),
                                                                                                                                                (360, 90, 4, '19', '19', 0, 0, '2024-07-07 01:58:38', '2024-07-07 01:59:07'),
                                                                                                                                                (365, 92, 1, '20', '20', 1, 0, '2024-07-07 13:11:27', '2024-07-07 14:42:53'),
                                                                                                                                                (366, 92, 2, '22', '22', 0, 0, '2024-07-07 13:11:27', '2024-07-07 14:42:53'),
                                                                                                                                                (367, 92, 3, '21', '21', 0, 0, '2024-07-07 13:11:27', '2024-07-07 14:42:53'),
                                                                                                                                                (368, 92, 4, '29', '29', 0, 0, '2024-07-07 13:11:27', '2024-07-07 14:42:53'),
                                                                                                                                                (369, 93, 1, '6', '6', 0, 0, '2024-07-07 14:35:05', '2024-07-07 14:43:32'),
                                                                                                                                                (370, 93, 2, '21', '21', 1, 0, '2024-07-07 14:35:05', '2024-07-07 14:43:32'),
                                                                                                                                                (371, 93, 3, '17', '17', 0, 0, '2024-07-07 14:35:05', '2024-07-07 14:43:32'),
                                                                                                                                                (372, 93, 4, '19', '19', 0, 0, '2024-07-07 14:35:05', '2024-07-07 14:43:32'),
                                                                                                                                                (373, 94, 1, '12', '12', 0, 0, '2024-07-07 23:20:39', '2024-07-07 23:23:11'),
                                                                                                                                                (374, 94, 2, '14', '14', 0, 0, '2024-07-07 23:20:39', '2024-07-07 23:23:11'),
                                                                                                                                                (375, 94, 3, '13', '13', 0, 0, '2024-07-07 23:20:39', '2024-07-07 23:23:11'),
                                                                                                                                                (376, 94, 4, '15', '15', 1, 0, '2024-07-07 23:20:39', '2024-07-07 23:23:11'),
                                                                                                                                                (381, 96, 1, '2', '2', 0, 0, '2024-07-07 23:30:07', '2024-07-07 23:31:08'),
                                                                                                                                                (382, 96, 2, '44', '44', 0, 0, '2024-07-07 23:30:07', '2024-07-07 23:31:08'),
                                                                                                                                                (383, 96, 3, '3', '3', 0, 0, '2024-07-07 23:30:07', '2024-07-07 23:31:08'),
                                                                                                                                                (384, 96, 4, '5', '5', 0, 0, '2024-07-07 23:30:07', '2024-07-07 23:31:08');

-- --------------------------------------------------------

--
-- Table structure for table `exams`
--

CREATE TABLE `exams` (
                         `id` int(11) NOT NULL,
                         `title` varchar(255) NOT NULL,
                         `description` text DEFAULT NULL,
                         `image_url` varchar(255) DEFAULT NULL,
                         `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
                         `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                         `user_id` int(11) DEFAULT NULL,
                         `status` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `exams`
--

INSERT INTO `exams` (`id`, `title`, `description`, `image_url`, `created_at`, `updated_at`, `user_id`, `status`) VALUES
                                                                                                                     (24, 'Real exam for deploy', 'this is a 5 question of deploy docker', 'http://localhost:8080/uploads/89dc682b-9569-4df3-9131-c7a87af48326.jfif', '2024-07-06 01:21:03', '2024-07-07 13:50:57', 3, 0),
                                                                                                                     (66, 'Math', '15 minutes', 'http://localhost:8080/uploads/1ba46c43-0851-4afd-8054-926a3d3b95d5.jfif', '2024-07-07 01:28:05', '2024-07-07 10:02:17', 3, -1),
                                                                                                                     (67, 'Math', '15 minutes', 'http://localhost:8080/uploads/3076056a-4846-4aad-b7ba-3ea27bfa9153.jpg', '2024-07-07 01:58:11', '2024-07-07 10:02:27', 3, -1);

-- --------------------------------------------------------

--
-- Table structure for table `exam_answers`
--

CREATE TABLE `exam_answers` (
                                `id` int(11) NOT NULL,
                                `exam_session_id` int(11) NOT NULL,
                                `question_id` int(11) NOT NULL,
                                `choice_id` int(11) DEFAULT NULL,
                                `user_id` int(11) DEFAULT NULL,
                                `answer_time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `exam_answers`
--

INSERT INTO `exam_answers` (`id`, `exam_session_id`, `question_id`, `choice_id`, `user_id`, `answer_time`) VALUES
                                                                                                               (1, 1, 17, 68, 5, '2024-07-07 22:34:49'),
                                                                                                               (2, 1, 17, 66, 4, '2024-07-07 22:34:51'),
                                                                                                               (3, 1, 18, 72, 5, '2024-07-07 22:34:57'),
                                                                                                               (4, 1, 18, 70, 4, '2024-07-07 22:34:59'),
                                                                                                               (5, 1, 19, 76, 5, '2024-07-07 22:35:02'),
                                                                                                               (6, 1, 19, 76, 4, '2024-07-07 22:35:04'),
                                                                                                               (7, 1, 20, 80, 5, '2024-07-07 22:35:13'),
                                                                                                               (8, 1, 20, 78, 4, '2024-07-07 22:35:15'),
                                                                                                               (9, 1, 21, 82, 5, '2024-07-07 22:35:22'),
                                                                                                               (10, 1, 21, 84, 4, '2024-07-07 22:35:23'),
                                                                                                               (11, 2, 17, 66, 5, '2024-07-07 22:42:01'),
                                                                                                               (12, 2, 17, 68, 4, '2024-07-07 22:42:02'),
                                                                                                               (13, 2, 18, 72, 5, '2024-07-07 22:42:09'),
                                                                                                               (14, 2, 18, 72, 4, '2024-07-07 22:42:10'),
                                                                                                               (15, 2, 19, 74, 5, '2024-07-07 22:42:13'),
                                                                                                               (16, 2, 19, 76, 4, '2024-07-07 22:42:14'),
                                                                                                               (17, 2, 20, 80, 5, '2024-07-07 22:42:18'),
                                                                                                               (18, 2, 20, 80, 4, '2024-07-07 22:42:20'),
                                                                                                               (19, 2, 21, 84, 5, '2024-07-07 22:42:23'),
                                                                                                               (20, 2, 21, 82, 4, '2024-07-07 22:42:24'),
                                                                                                               (21, 2, 92, 368, 5, '2024-07-07 22:42:27'),
                                                                                                               (22, 2, 92, 365, 4, '2024-07-07 22:42:30'),
                                                                                                               (23, 2, 93, 370, 5, '2024-07-07 22:42:36'),
                                                                                                               (24, 2, 93, 370, 4, '2024-07-07 22:42:38'),
                                                                                                               (25, 3, 17, 68, 5, '2024-07-07 22:49:23'),
                                                                                                               (26, 3, 17, 68, 4, '2024-07-07 22:49:25'),
                                                                                                               (27, 3, 18, 72, 5, '2024-07-07 22:49:51'),
                                                                                                               (28, 3, 18, 70, 4, '2024-07-07 22:49:56'),
                                                                                                               (29, 3, 19, 76, 5, '2024-07-07 22:50:58'),
                                                                                                               (30, 3, 19, 74, 4, '2024-07-07 22:51:00'),
                                                                                                               (31, 3, 20, 80, 5, '2024-07-07 22:51:03'),
                                                                                                               (32, 3, 20, 80, 4, '2024-07-07 22:51:04'),
                                                                                                               (33, 3, 21, 82, 5, '2024-07-07 22:51:07'),
                                                                                                               (34, 3, 21, 84, 4, '2024-07-07 22:51:08'),
                                                                                                               (35, 3, 92, 368, 5, '2024-07-07 22:51:10'),
                                                                                                               (36, 3, 92, 365, 4, '2024-07-07 22:51:13'),
                                                                                                               (37, 3, 93, 370, 4, '2024-07-07 22:51:17'),
                                                                                                               (38, 3, 93, 372, 5, '2024-07-07 22:51:18'),
                                                                                                               (39, 4, 17, 68, 5, '2024-07-07 23:09:19');

-- --------------------------------------------------------

--
-- Table structure for table `exam_sessions`
--

CREATE TABLE `exam_sessions` (
                                 `id` int(11) NOT NULL,
                                 `exam_id` int(11) NOT NULL,
                                 `host_id` int(11) NOT NULL,
                                 `start_time` timestamp NOT NULL DEFAULT current_timestamp(),
                                 `end_time` timestamp NULL DEFAULT current_timestamp(),
                                 `status` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `exam_sessions`
--

INSERT INTO `exam_sessions` (`id`, `exam_id`, `host_id`, `start_time`, `end_time`, `status`) VALUES
                                                                                                 (1, 24, 3, '2024-07-07 22:34:32', '2024-07-07 22:35:25', 1),
                                                                                                 (2, 24, 3, '2024-07-07 22:41:57', '2024-07-07 22:42:41', 1),
                                                                                                 (3, 24, 3, '2024-07-07 22:49:05', '2024-07-07 22:51:21', 1),
                                                                                                 (4, 24, 3, '2024-07-07 23:09:13', '2024-07-07 23:09:33', 1);

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
                             `id` int(11) NOT NULL,
                             `exam_id` int(11) NOT NULL,
                             `question_index` int(11) NOT NULL,
                             `question_text` text DEFAULT NULL,
                             `image_url` varchar(255) DEFAULT NULL,
                             `time` int(11) DEFAULT 0,
                             `status` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`id`, `exam_id`, `question_index`, `question_text`, `image_url`, `time`, `status`) VALUES
                                                                                                                (17, 24, 1, 'what is the command to build docker compose ?', '', 30, 0),
                                                                                                                (18, 24, 2, 'Question', '', 0, 0),
                                                                                                                (19, 24, 3, 'docker only support for windown', '', 30, 0),
                                                                                                                (20, 24, 4, '4. ez to deal, 123567 , what is the number is missing in the test', '', 40, 0),
                                                                                                                (21, 24, 5, 'Question', '', 0, 0),
                                                                                                                (89, 67, 1, '1+5=?', '', 20, 0),
                                                                                                                (90, 67, 2, '10+11=', '', 20, 0),
                                                                                                                (92, 24, 6, '10+10=?', '', 0, 0),
                                                                                                                (93, 24, 7, '10+11=?', '', 0, 0),
                                                                                                                (94, 24, 8, 'đâu là số chia hết cho 5', '', 0, 0),
                                                                                                                (96, 24, 9, '1', '', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `id` int(11) NOT NULL,
                         `username` varchar(20) DEFAULT NULL,
                         `password` varchar(255) DEFAULT NULL,
                         `player_name` varchar(20) DEFAULT NULL,
                         `gender` int(11) DEFAULT NULL,
                         `email` varchar(255) DEFAULT NULL,
                         `phone` varchar(10) DEFAULT NULL,
                         `active` int(11) DEFAULT NULL,
                         `relogin_token` varchar(255) DEFAULT NULL,
                         `phone_otp` varchar(6) DEFAULT NULL,
                         `phone_otp_time` mediumtext DEFAULT NULL,
                         `is_phone_verified` int(11) DEFAULT NULL,
                         `email_code` varchar(6) DEFAULT NULL,
                         `email_code_time` mediumtext DEFAULT NULL,
                         `is_email_verified` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `player_name`, `gender`, `email`, `phone`, `active`, `relogin_token`, `phone_otp`, `phone_otp_time`, `is_phone_verified`, `email_code`, `email_code_time`, `is_email_verified`) VALUES
                                                                                                                                                                                                                                       (2, 'haonghai', '$2a$12$fOat4ELq3HZW.', NULL, NULL, NULL, NULL, 1, '123456', NULL, NULL, NULL, NULL, NULL, NULL),
                                                                                                                                                                                                                                       (3, 'ngoan', '$2a$12$lJmbjV0YxkO2TPvdrOfTe.5gLTROzrsyFedwPyrGVqv24qUzSzFfW', NULL, NULL, 'ngoan@gmail.com', NULL, 1, '3180f6d4-18e6-41cb-8df1-a8174028c7ad', NULL, NULL, NULL, NULL, NULL, 1),
                                                                                                                                                                                                                                       (4, 'chan', '$2a$12$lJmbjV0YxkO2TPvdrOfTe.5gLTROzrsyFedwPyrGVqv24qUzSzFfW', NULL, NULL, 'chan@gmail.com', NULL, 1, '93208cc6-e847-4029-a875-70c9ae39bedb', NULL, NULL, NULL, NULL, NULL, 1),
                                                                                                                                                                                                                                       (5, 'hai', '$2a$12$lJmbjV0YxkO2TPvdrOfTe.5gLTROzrsyFedwPyrGVqv24qUzSzFfW', NULL, NULL, 'hai@gmail.com', NULL, 1, '49b93773-abdd-4d39-9c9e-61fdb6887f0a', NULL, NULL, NULL, NULL, NULL, 1),
                                                                                                                                                                                                                                       (6, 'ngoan2', '$2a$12$lJmbjV0YxkO2TPvdrOfTe.5gLTROzrsyFedwPyrGVqv24qUzSzFfW', NULL, NULL, 'ngoan2@gmail.com', NULL, 1, '11d7f418-8cba-4729-8098-c193d6dcafd5', NULL, NULL, NULL, '', '1720187373853', 1);

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
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=385;

--
-- AUTO_INCREMENT for table `exams`
--
ALTER TABLE `exams`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=68;

--
-- AUTO_INCREMENT for table `exam_answers`
--
ALTER TABLE `exam_answers`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `exam_sessions`
--
ALTER TABLE `exam_sessions`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=97;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

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
    ADD CONSTRAINT `fk_exam_answers_choices` FOREIGN KEY (`choice_id`) REFERENCES `choices` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `fk_exam_answers_exam_sessions` FOREIGN KEY (`exam_session_id`) REFERENCES `exam_sessions` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `fk_exam_answers_questions` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exam_sessions`
--
ALTER TABLE `exam_sessions`
    ADD CONSTRAINT `fk_exam_sessions_exams` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `fk_exam_sessions_users` FOREIGN KEY (`host_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `questions`
--
ALTER TABLE `questions`
    ADD CONSTRAINT `questions_exams_id_fk` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

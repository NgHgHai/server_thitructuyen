-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 14, 2024 at 09:08 PM
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
CREATE DATABASE IF NOT EXISTS `thitructuyen` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `thitructuyen`;
--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `id` int(11) NOT NULL,
                         `username` varchar(20) DEFAULT NULL,
                         `password` varchar(255) DEFAULT NULL,
                         `player_name` varchar(20) DEFAULT NULL,
                         `gender` int(11) DEFAULT NULL,
                         `email` varchar(20) DEFAULT NULL,
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
    (2, 'haonghai', '$2a$12$fOat4ELq3HZW.', NULL, NULL, NULL, NULL, NULL, '123456', NULL, NULL, NULL, NULL, NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

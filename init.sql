-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: e_learning
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `e_learning`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `e_learning` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `e_learning`;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
INSERT INTO `announcement` VALUES (21,'test','2025-12-21 22:31:59.450051','ADMIN','admin1');
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `grade` double DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `subject_id` bigint DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `deadline` datetime(6) DEFAULT NULL,
  `due_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsnl7q8f7r2g7wo2k02d4abu25` (`subject_id`),
  CONSTRAINT `FKsnl7q8f7r2g7wo2k02d4abu25` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment`
--

LOCK TABLES `assignment` WRITE;
/*!40000 ALTER TABLE `assignment` DISABLE KEYS */;
INSERT INTO `assignment` VALUES (27,NULL,NULL,'test','uploads/assignments/1763603861696_1.5_Struktur_Organisasi.pdf',NULL,'test',17,'1763603861696_1.5_Struktur_Organisasi.pdf',NULL,'2025-12-05 22:23:00.000000'),(30,NULL,NULL,'test','uploads/assignments/1764814659267_Surat_Balasan_Magang_MID.pdf',NULL,'test2',17,'1764814659267_Surat_Balasan_Magang_MID.pdf',NULL,'2025-12-27 09:17:00.000000');
/*!40000 ALTER TABLE `assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `present` bit(1) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhmh4v9lugedps4ugwdbtb8a87` (`user_id`,`date`),
  CONSTRAINT `FKjcaqd29v2qy723owsdah2t8vx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (1,'2025-12-04',0x01,21),(2,'2025-12-12',0x01,21),(3,'2025-12-12',0x01,30),(4,'2025-12-22',0x01,21);
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion`
--

DROP TABLE IF EXISTS `discussion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKicysshvfbu3eeqy0n6mcxpqxk` (`sender_id`),
  CONSTRAINT `FKicysshvfbu3eeqy0n6mcxpqxk` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion`
--

LOCK TABLES `discussion` WRITE;
/*!40000 ALTER TABLE `discussion` DISABLE KEYS */;
INSERT INTO `discussion` VALUES (1,'2025-10-31 10:15:59.698108','halo',21),(2,'2025-10-31 14:44:01.914871','halo',6),(3,'2025-10-31 17:06:37.593982','test',6),(4,'2025-10-31 17:06:56.437047','test',21),(5,'2025-11-03 10:44:41.904832','test',6),(6,'2025-11-03 14:46:28.981351','hai hai',5),(7,'2025-11-06 08:37:58.797652','hai haiii',5),(9,'2025-11-14 08:48:58.058224','hai',5),(10,'2025-11-14 08:58:04.902140','tes',6),(11,'2025-11-14 08:58:34.072277','tes',5),(12,'2025-11-14 09:02:25.751783','test',5),(13,'2025-11-14 09:02:48.516402','hai',21),(14,'2025-11-14 10:06:10.303120','add',5),(15,'2025-11-18 10:48:43.749826','tes',5),(16,'2025-12-04 09:18:19.804381','test',21);
/*!40000 ALTER TABLE `discussion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussions`
--

DROP TABLE IF EXISTS `discussions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `created_at` datetime(6) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9poj0fwjbqjit2n9dt6bhe5f7` (`user_id`),
  CONSTRAINT `FK9poj0fwjbqjit2n9dt6bhe5f7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussions`
--

LOCK TABLES `discussions` WRITE;
/*!40000 ALTER TABLE `discussions` DISABLE KEYS */;
/*!40000 ALTER TABLE `discussions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_read` bit(1) NOT NULL,
  `message` text NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `recipient_id` bigint NOT NULL,
  `sender_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqqnsjxlwleyjbxlmm213jaj3f` (`recipient_id`),
  KEY `FK13vcnq3ukas06ho1yrbc5lrb5` (`sender_id`),
  CONSTRAINT `FK13vcnq3ukas06ho1yrbc5lrb5` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqqnsjxlwleyjbxlmm213jaj3f` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,0x01,'Please meet with your supervisor to discuss your progress.','2025-11-21 14:36:05.492837',30,5),(2,0x01,'Please meet with your supervisor to discuss your progress.','2025-12-04 09:16:00.215446',30,5),(3,0x01,'Please meet with your supervisor to discuss your progress.','2025-12-12 15:42:52.253159',30,6);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_subjects`
--

DROP TABLE IF EXISTS `student_subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_subjects` (
  `student_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`student_id`,`subject_id`),
  KEY `FKjuhviveo4kyikubl3suxro325` (`subject_id`),
  CONSTRAINT `FK7khxsvyb14g2myd742s2of64` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjuhviveo4kyikubl3suxro325` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_subjects`
--

LOCK TABLES `student_subjects` WRITE;
/*!40000 ALTER TABLE `student_subjects` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject_students`
--

DROP TABLE IF EXISTS `subject_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject_students` (
  `subject_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`subject_id`,`student_id`),
  KEY `FKgaanejxe28064quc65obwchbf` (`student_id`),
  CONSTRAINT `FKgaanejxe28064quc65obwchbf` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpwmwbvyaw4wdm6fypasdn8niw` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject_students`
--

LOCK TABLES `subject_students` WRITE;
/*!40000 ALTER TABLE `subject_students` DISABLE KEYS */;
/*!40000 ALTER TABLE `subject_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (17,'C001','','test'),(19,'C002','test222','test2'),(27,'C003','test3','test3');
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submissions`
--

DROP TABLE IF EXISTS `submissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `grade` double DEFAULT NULL,
  `upload_time` datetime(6) DEFAULT NULL,
  `assignment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnv7l6co7g7i5udy0qpwanmraw` (`assignment_id`),
  KEY `FK760bgu69957phd7hax608jdms` (`user_id`),
  CONSTRAINT `FK760bgu69957phd7hax608jdms` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKnv7l6co7g7i5udy0qpwanmraw` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submissions`
--

LOCK TABLES `submissions` WRITE;
/*!40000 ALTER TABLE `submissions` DISABLE KEYS */;
INSERT INTO `submissions` VALUES (28,'','1764814693725_Surat Balasan Magang MID.pdf','uploads\\submissions\\1764814693725_Surat Balasan Magang MID.pdf',50,'2025-12-04 09:18:13.737963',27,21),(34,'','1766334939968_SEQUENCE DIAGRAM_E-LEARNING.png','uploads\\submissions\\1766334939968_SEQUENCE DIAGRAM_E-LEARNING.png',100,'2025-12-21 23:35:39.985523',30,21);
/*!40000 ALTER TABLE `submissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL,
  `approved` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (5,'admin1','$2a$10$2sVCxibNW24nKa5aM0/ULOKDB7Gweg/UfD1jxknQ6G1MxtLz.sRW.','admin-mid-elearning@gmail.com','ADMIN',0x01),(6,'mentor1','$2a$10$wwTWG8w5wLmZODykf4YgPuV3bccfMfax8rXKzBYh8KP/ivn/0yEcq','mentor1-mid-elearning@gmail.com','MENTOR',0x01),(21,'edo','$2a$10$BuVamQPeXM.DsUPx.bwPn.Stq4w0tKqhNQyW9Y3gjVucoKWWt6ani','edo@mail.com','USER',0x01),(30,'test','$2a$10$U8wnTMcEbOeJeIYS.NpPrOhgxFfl6YNaGI7/3/yvBi46jx215gUUK','test@mail.com','USER',0x01);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'e_learning'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-23 12:35:27

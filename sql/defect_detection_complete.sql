-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: defect_detection
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `annotation_data`
--

DROP TABLE IF EXISTS `annotation_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `annotation_data` (
  `id` int NOT NULL AUTO_INCREMENT,
  `raw_image_id` int NOT NULL,
  `task_id` int DEFAULT NULL,
  `category` varchar(50) NOT NULL,
  `category_id` tinyint DEFAULT NULL,
  `x` decimal(10,4) NOT NULL,
  `y` decimal(10,4) NOT NULL,
  `width` decimal(10,4) NOT NULL,
  `height` decimal(10,4) NOT NULL,
  `confidence` decimal(6,3) DEFAULT '1.000',
  `is_difficult` tinyint DEFAULT '0',
  `annotator_id` int DEFAULT NULL,
  `annotator_name` varchar(32) DEFAULT NULL,
  `annotation_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_verified` tinyint DEFAULT '0',
  `verifier_id` int DEFAULT NULL,
  `verify_time` datetime DEFAULT NULL,
  `is_deleted` tinyint DEFAULT '0',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_raw_image` (`raw_image_id`),
  KEY `idx_task` (`task_id`),
  KEY `idx_category` (`category_id`),
  CONSTRAINT `annotation_data_ibfk_1` FOREIGN KEY (`raw_image_id`) REFERENCES `raw_image` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annotation_data`
--

LOCK TABLES `annotation_data` WRITE;
/*!40000 ALTER TABLE `annotation_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `annotation_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api`
--

DROP TABLE IF EXISTS `api`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api` (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_deleted` decimal(1,0) DEFAULT (0),
  `create_time` datetime DEFAULT NULL,
  `create_id` int DEFAULT NULL,
  `create_name` varchar(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_id` int DEFAULT NULL,
  `validity_period` int DEFAULT (-(1)),
  `validity_times` int DEFAULT (-(1)),
  `permission_level` tinyint DEFAULT (0),
  `api_key` varchar(128) NOT NULL,
  `status` decimal(1,0) DEFAULT (1),
  `remark` varchar(255) DEFAULT NULL,
  `counts` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `api_key` (`api_key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api`
--

LOCK TABLES `api` WRITE;
/*!40000 ALTER TABLE `api` DISABLE KEYS */;
INSERT INTO `api` VALUES (1,0,'2025-11-22 19:12:50',1,'???1','2025-11-22 19:12:50',1,-1,-1,0,'APIKEY1',1,NULL,0),(2,0,'2025-11-22 19:12:50',2,'???2','2025-11-22 19:12:50',2,-1,-1,0,'APIKEY2',1,NULL,0);
/*!40000 ALTER TABLE `api` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `defection`
--

DROP TABLE IF EXISTS `defection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `defection` (
  `id` int NOT NULL AUTO_INCREMENT,
  `l` decimal(10,4) DEFAULT NULL,
  `h` decimal(10,4) DEFAULT NULL,
  `x` decimal(10,4) DEFAULT NULL,
  `y` decimal(10,4) DEFAULT NULL,
  `score` decimal(6,3) DEFAULT NULL,
  `detect_id` int DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `category_id` tinyint DEFAULT NULL,
  `severity_level` int DEFAULT NULL,
  `repair_suggestion` varchar(500) DEFAULT NULL,
  `data_source` varchar(32) DEFAULT 'detection',
  `annotation_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `defection`
--

LOCK TABLES `defection` WRITE;
/*!40000 ALTER TABLE `defection` DISABLE KEYS */;
INSERT INTO `defection` VALUES (1,1.2340,5.6780,0.1230,0.4560,0.789,1,'????1',1,NULL,NULL,'detection',NULL),(2,3.4560,7.8900,0.7890,0.1230,0.789,2,'????2',2,NULL,NULL,'detection',NULL);
/*!40000 ALTER TABLE `defection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `defection_category`
--

DROP TABLE IF EXISTS `defection_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `defection_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `count` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `defection_category`
--

LOCK TABLES `defection_category` WRITE;
/*!40000 ALTER TABLE `defection_category` DISABLE KEYS */;
INSERT INTO `defection_category` VALUES (1,'????1',5,NULL),(2,'????2',10,NULL);
/*!40000 ALTER TABLE `defection_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detect_log`
--

DROP TABLE IF EXISTS `detect_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detect_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `defections_sum` tinyint DEFAULT (0),
  `time` datetime DEFAULT NULL,
  `work_order_id` int DEFAULT NULL,
  `storage_path` varchar(255) DEFAULT NULL,
  `data_source` varchar(32) DEFAULT 'detection',
  `raw_image_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detect_log`
--

LOCK TABLES `detect_log` WRITE;
/*!40000 ALTER TABLE `detect_log` DISABLE KEYS */;
INSERT INTO `detect_log` VALUES (1,'???1',2,'2025-11-22 19:12:50',1,'/path/to/storage1','detection',NULL),(2,'???2',1,'2025-11-22 19:12:50',2,'/path/to/storage2','detection',NULL);
/*!40000 ALTER TABLE `detect_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL,
  `mac` char(64) DEFAULT NULL,
  `ip` char(64) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `is_deleted` decimal(1,0) DEFAULT (0),
  `create_time` datetime DEFAULT NULL,
  `create_id` int DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_id` int DEFAULT NULL,
  `is_Connect` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (1,1,'00:11:22:33:44:55','127.0.0.1','IP???','????????',0,'2025-11-22 19:12:50',1,'2025-11-22 19:12:50',1,0),(2,2,'AA:BB:CC:DD:EE:FF','192.168.1.2','??2','??2',0,'2025-11-22 19:12:50',2,'2025-11-22 19:12:50',2,0);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager` (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `account` varchar(11) NOT NULL,
  `password` varchar(32) NOT NULL,
  `phone_number` char(11) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `is_Online` tinyint(1) DEFAULT (0),
  `warnings_open` tinyint(1) DEFAULT '0',
  `warnings_level` tinyint DEFAULT '0',
  `phone_Way` tinyint(1) DEFAULT '0',
  `email_way` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES (1,'admin1','e10adc3949ba59abbe56e057f20f883e','12345678901','???1','admin1@example.com',0,0,0,0,0),(2,'admin2','e10adc3949ba59abbe56e057f20f883e','98765432109','???2','admin2@example.com',0,0,0,0,0);
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `model`
--

DROP TABLE IF EXISTS `model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `model` (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `param_storage_path` varchar(128) NOT NULL,
  `is_deleted` decimal(1,0) DEFAULT (0),
  `create_time` datetime DEFAULT NULL,
  `create_id` int DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_id` int DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `model`
--

LOCK TABLES `model` WRITE;
/*!40000 ALTER TABLE `model` DISABLE KEYS */;
INSERT INTO `model` VALUES (1,'??1','/path/to/param1',0,'2025-11-22 19:12:50',1,'2025-11-22 19:12:50',1,'??1'),(2,'????','/path/to/param2',0,'2025-11-22 19:12:50',2,'2025-11-22 19:12:50',2,'??2');
/*!40000 ALTER TABLE `model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operator`
--

DROP TABLE IF EXISTS `operator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operator` (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `job_id` int DEFAULT NULL,
  `login_pwd` char(32) DEFAULT NULL,
  `op_pwd` char(32) DEFAULT NULL,
  `is_deleted` decimal(1,0) DEFAULT (0),
  `create_time` datetime DEFAULT NULL,
  `create_id` int DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_id` int DEFAULT NULL,
  `create_name` varchar(20) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `work_order_id` smallint DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_id` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operator`
--

LOCK TABLES `operator` WRITE;
/*!40000 ALTER TABLE `operator` DISABLE KEYS */;
INSERT INTO `operator` VALUES (1,NULL,'123456','abcdef',0,'2025-11-22 19:12:50',1,'2025-11-22 19:12:50',1,'???1','???1',1,'??1'),(2,NULL,'abcdef','123456',0,'2025-11-22 19:12:50',2,'2025-11-22 19:12:50',2,'???2','???2',2,'??2');
/*!40000 ALTER TABLE `operator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `raw_image`
--

DROP TABLE IF EXISTS `raw_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `raw_image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_name` varchar(128) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `image_size` bigint DEFAULT NULL,
  `image_width` int DEFAULT NULL,
  `image_height` int DEFAULT NULL,
  `upload_source` varchar(32) DEFAULT 'camera',
  `device_id` smallint DEFAULT NULL,
  `work_order_id` int DEFAULT NULL,
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `upload_user_id` int DEFAULT NULL,
  `upload_user_name` varchar(32) DEFAULT NULL,
  `status` tinyint DEFAULT '0',
  `is_deleted` tinyint DEFAULT '0',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_work_order` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `raw_image`
--

LOCK TABLES `raw_image` WRITE;
/*!40000 ALTER TABLE `raw_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `raw_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `time` datetime DEFAULT NULL,
  `operation` varchar(32) DEFAULT NULL,
  `operator` smallint DEFAULT NULL,
  `operator_type` decimal(1,0) NOT NULL,
  `target` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
INSERT INTO `sys_log` VALUES (1,'2025-11-22 19:12:50','??1',1,1,NULL),(2,'2025-11-22 19:12:50','??2',2,2,NULL);
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warnings`
--

DROP TABLE IF EXISTS `warnings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warnings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `level` tinyint DEFAULT NULL,
  `type` char(6) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warnings`
--

LOCK TABLES `warnings` WRITE;
/*!40000 ALTER TABLE `warnings` DISABLE KEYS */;
INSERT INTO `warnings` VALUES (1,'2025-11-22 19:12:50',1,'1','????1'),(2,'2025-11-22 19:12:50',2,'2','????2');
/*!40000 ALTER TABLE `warnings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_order`
--

DROP TABLE IF EXISTS `work_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_order` (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `is_deleted` decimal(1,0) DEFAULT (0),
  `create_time` datetime DEFAULT NULL,
  `create_id` int DEFAULT NULL,
  `current_num` int DEFAULT NULL,
  `detect_sum` int DEFAULT NULL,
  `is_over` decimal(1,0) DEFAULT (0),
  `finish_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_id` int DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_order`
--

LOCK TABLES `work_order` WRITE;
/*!40000 ALTER TABLE `work_order` DISABLE KEYS */;
INSERT INTO `work_order` VALUES (1,0,'2025-11-22 19:12:50',1,1,10,0,NULL,'2025-11-22 19:12:50',1,'??1'),(2,0,'2025-11-22 19:12:50',2,2,20,0,NULL,'2025-11-22 19:12:50',2,'??2');
/*!40000 ALTER TABLE `work_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'defect_detection'
--

--
-- Dumping routines for database 'defect_detection'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-22 19:25:42

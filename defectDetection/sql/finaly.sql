/*
 Navicat Premium Data Transfer

 Source Server         : v2
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : defect_detection

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 22/11/2025 19:15:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for annotation_data
-- ----------------------------
DROP TABLE IF EXISTS `annotation_data`;
CREATE TABLE `annotation_data`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `raw_image_id` int NOT NULL,
  `task_id` int NULL DEFAULT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category_id` tinyint NULL DEFAULT NULL,
  `x` decimal(10, 4) NOT NULL,
  `y` decimal(10, 4) NOT NULL,
  `width` decimal(10, 4) NOT NULL,
  `height` decimal(10, 4) NOT NULL,
  `confidence` decimal(6, 3) NULL DEFAULT 1.000,
  `is_difficult` tinyint NULL DEFAULT 0,
  `annotator_id` int NULL DEFAULT NULL,
  `annotator_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `annotation_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `is_verified` tinyint NULL DEFAULT 0,
  `verifier_id` int NULL DEFAULT NULL,
  `verify_time` datetime NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_raw_image`(`raw_image_id` ASC) USING BTREE,
  INDEX `idx_task`(`task_id` ASC) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of annotation_data
-- ----------------------------

-- ----------------------------
-- Table structure for api
-- ----------------------------
DROP TABLE IF EXISTS `api`;
CREATE TABLE `api`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_deleted` decimal(1, 0) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `create_id` int NULL DEFAULT NULL,
  `create_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `validity_period` int NULL DEFAULT -1,
  `validity_times` int NULL DEFAULT -1,
  `permission_level` tinyint NULL DEFAULT 0,
  `api_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` decimal(1, 0) NULL DEFAULT 1,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `counts` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `api_key`(`api_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of api
-- ----------------------------
INSERT INTO `api` VALUES (1, 0, '2025-11-22 19:12:50', 1, '???1', '2025-11-22 19:12:50', 1, -1, -1, 0, 'APIKEY1', 1, NULL, 0);
INSERT INTO `api` VALUES (2, 0, '2025-11-22 19:12:50', 2, '???2', '2025-11-22 19:12:50', 2, -1, -1, 0, 'APIKEY2', 1, NULL, 0);

-- ----------------------------
-- Table structure for defection
-- ----------------------------
DROP TABLE IF EXISTS `defection`;
CREATE TABLE `defection`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `l` decimal(10, 4) NULL DEFAULT NULL,
  `h` decimal(10, 4) NULL DEFAULT NULL,
  `x` decimal(10, 4) NULL DEFAULT NULL,
  `y` decimal(10, 4) NULL DEFAULT NULL,
  `score` decimal(6, 3) NULL DEFAULT NULL,
  `detect_id` int NULL DEFAULT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `category_id` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of defection
-- ----------------------------
INSERT INTO `defection` VALUES (1, 1.2340, 5.6780, 0.1230, 0.4560, 0.789, 1, '????1', 1);
INSERT INTO `defection` VALUES (2, 3.4560, 7.8900, 0.7890, 0.1230, 0.789, 2, '????2', 2);

-- ----------------------------
-- Table structure for defection_category
-- ----------------------------
DROP TABLE IF EXISTS `defection_category`;
CREATE TABLE `defection_category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `count` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of defection_category
-- ----------------------------
INSERT INTO `defection_category` VALUES (1, '????1', 5, NULL);
INSERT INTO `defection_category` VALUES (2, '????2', 10, NULL);

-- ----------------------------
-- Table structure for detect_log
-- ----------------------------
DROP TABLE IF EXISTS `detect_log`;
CREATE TABLE `detect_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `defections_sum` tinyint NULL DEFAULT 0,
  `time` datetime NULL DEFAULT NULL,
  `work_order_id` int NULL DEFAULT NULL,
  `storage_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `data_source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'detection',
  `raw_image_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of detect_log
-- ----------------------------
INSERT INTO `detect_log` VALUES (1, '???1', 2, '2025-11-22 19:12:50', 1, '/path/to/storage1', 'detection', NULL);
INSERT INTO `detect_log` VALUES (2, '???2', 1, '2025-11-22 19:12:50', 2, '/path/to/storage2', 'detection', NULL);

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device`  (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL,
  `mac` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ip` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` decimal(1, 0) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `create_id` int NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `is_Connect` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES (1, 1, '00:11:22:33:44:55', '127.0.0.1', 'IP???', '????????', 0, '2025-11-22 19:12:50', 1, '2025-11-22 19:12:50', 1, 0);
INSERT INTO `device` VALUES (2, 2, 'AA:BB:CC:DD:EE:FF', '192.168.1.2', '??2', '??2', 0, '2025-11-22 19:12:50', 2, '2025-11-22 19:12:50', 2, 0);

-- ----------------------------
-- Table structure for manager
-- ----------------------------
DROP TABLE IF EXISTS `manager`;
CREATE TABLE `manager`  (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `account` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone_number` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_Online` tinyint(1) NULL DEFAULT 0,
  `warnings_open` tinyint(1) NULL DEFAULT 0,
  `warnings_level` tinyint NULL DEFAULT 0,
  `phone_Way` tinyint(1) NULL DEFAULT 0,
  `email_way` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`account` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of manager
-- ----------------------------
INSERT INTO `manager` VALUES (1, 'admin1', 'e10adc3949ba59abbe56e057f20f883e', '12345678901', '???1', 'admin1@example.com', 0, 0, 0, 0, 0);
INSERT INTO `manager` VALUES (2, 'admin2', 'e10adc3949ba59abbe56e057f20f883e', '98765432109', '???2', 'admin2@example.com', 0, 0, 0, 0, 0);

-- ----------------------------
-- Table structure for model
-- ----------------------------
DROP TABLE IF EXISTS `model`;
CREATE TABLE `model`  (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `param_storage_path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_deleted` decimal(1, 0) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `create_id` int NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of model
-- ----------------------------
INSERT INTO `model` VALUES (1, '??1', '/path/to/param1', 0, '2025-11-22 19:12:50', 1, '2025-11-22 19:12:50', 1, '??1');
INSERT INTO `model` VALUES (2, '????', '/path/to/param2', 0, '2025-11-22 19:12:50', 2, '2025-11-22 19:12:50', 2, '??2');

-- ----------------------------
-- Table structure for operator
-- ----------------------------
DROP TABLE IF EXISTS `operator`;
CREATE TABLE `operator`  (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `job_id` int NULL DEFAULT NULL,
  `login_pwd` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `op_pwd` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` decimal(1, 0) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `create_id` int NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `create_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `work_order_id` smallint NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `job_id`(`job_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operator
-- ----------------------------
INSERT INTO `operator` VALUES (1, NULL, '123456', 'abcdef', 0, '2025-11-22 19:12:50', 1, '2025-11-22 19:12:50', 1, '???1', '???1', 1, '??1');
INSERT INTO `operator` VALUES (2, NULL, 'abcdef', '123456', 0, '2025-11-22 19:12:50', 2, '2025-11-22 19:12:50', 2, '???2', '???2', 2, '??2');

-- ----------------------------
-- Table structure for raw_image
-- ----------------------------
DROP TABLE IF EXISTS `raw_image`;
CREATE TABLE `raw_image`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `image_size` bigint NULL DEFAULT NULL,
  `image_width` int NULL DEFAULT NULL,
  `image_height` int NULL DEFAULT NULL,
  `upload_source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'camera',
  `device_id` smallint NULL DEFAULT NULL,
  `work_order_id` int NULL DEFAULT NULL,
  `upload_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `upload_user_id` int NULL DEFAULT NULL,
  `upload_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 0,
  `is_deleted` tinyint NULL DEFAULT 0,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE,
  INDEX `idx_work_order`(`work_order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of raw_image
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `time` datetime NULL DEFAULT NULL,
  `operation` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `operator` smallint NULL DEFAULT NULL,
  `operator_type` decimal(1, 0) NOT NULL,
  `target` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, '2025-11-22 19:12:50', '??1', 1, 1, NULL);
INSERT INTO `sys_log` VALUES (2, '2025-11-22 19:12:50', '??2', 2, 2, NULL);

-- ----------------------------
-- Table structure for warnings
-- ----------------------------
DROP TABLE IF EXISTS `warnings`;
CREATE TABLE `warnings`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime NULL DEFAULT NULL,
  `level` tinyint NULL DEFAULT NULL,
  `type` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warnings
-- ----------------------------
INSERT INTO `warnings` VALUES (1, '2025-11-22 19:12:50', 1, '1', '????1');
INSERT INTO `warnings` VALUES (2, '2025-11-22 19:12:50', 2, '2', '????2');

-- ----------------------------
-- Table structure for work_order
-- ----------------------------
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order`  (
  `id` smallint NOT NULL AUTO_INCREMENT,
  `is_deleted` decimal(1, 0) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `create_id` int NULL DEFAULT NULL,
  `current_num` int NULL DEFAULT NULL,
  `detect_sum` int NULL DEFAULT NULL,
  `is_over` decimal(1, 0) NULL DEFAULT 0,
  `finish_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `update_id` int NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_order
-- ----------------------------
INSERT INTO `work_order` VALUES (1, 0, '2025-11-22 19:12:50', 1, 1, 10, 0, NULL, '2025-11-22 19:12:50', 1, '??1');
INSERT INTO `work_order` VALUES (2, 0, '2025-11-22 19:12:50', 2, 2, 20, 0, NULL, '2025-11-22 19:12:50', 2, '??2');

SET FOREIGN_KEY_CHECKS = 1;

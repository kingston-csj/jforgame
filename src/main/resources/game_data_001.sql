/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : game_data_001

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-08-02 23:04:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for configplayerlevel
-- ----------------------------
DROP TABLE IF EXISTS `configplayerlevel`;
CREATE TABLE `configplayerlevel` (
  `level` int(11) DEFAULT NULL,
  `needExp` bigint(20) DEFAULT NULL,
  `vitality` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configplayerlevel
-- ----------------------------
INSERT INTO `configplayerlevel` VALUES ('1', '2345', '100');
INSERT INTO `configplayerlevel` VALUES ('2', '23450', '105');

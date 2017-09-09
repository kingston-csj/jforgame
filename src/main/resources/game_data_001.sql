/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : game_data_001

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-09-09 18:17:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for configactivity
-- ----------------------------
DROP TABLE IF EXISTS `configactivity`;
CREATE TABLE `configactivity` (
  `id` int(11) NOT NULL,
  `type` int(255) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configactivity
-- ----------------------------
INSERT INTO `configactivity` VALUES ('1', '1', '首充送好礼');

-- ----------------------------
-- Table structure for configconstant
-- ----------------------------
DROP TABLE IF EXISTS `configconstant`;
CREATE TABLE `configconstant` (
  `id` int(11) NOT NULL,
  `intValue` int(255) DEFAULT NULL,
  `StringValue` varchar(512) DEFAULT NULL,
  `descrpition` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configconstant
-- ----------------------------
INSERT INTO `configconstant` VALUES ('1', '500', '11;22', '玩家最高等级');

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

-- ----------------------------
-- Table structure for configskill
-- ----------------------------
DROP TABLE IF EXISTS `configskill`;
CREATE TABLE `configskill` (
  `id` int(11) DEFAULT '1',
  `name` varchar(64) DEFAULT '',
  `effect` varchar(128) DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configskill
-- ----------------------------
INSERT INTO `configskill` VALUES ('1', '万剑诀', '剑芒如雨直落，攻击敌方全体');
INSERT INTO `configskill` VALUES ('2', '天剑', '人剑合一，身化利剑，攻击敌方全体');

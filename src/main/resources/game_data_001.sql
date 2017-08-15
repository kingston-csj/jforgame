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
-- Table structure for ConfigPlayerLevel
-- ----------------------------
DROP TABLE IF EXISTS `ConfigPlayerLevel`;
CREATE TABLE `ConfigPlayerLevel` (
  `level` int(11) DEFAULT NULL,
  `needExp` bigint(20) DEFAULT NULL,
  `vitality` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Records of ConfigPlayerLevel
-- ----------------------------
INSERT INTO `ConfigPlayerLevel` VALUES ('1', '2345', '100');
INSERT INTO `ConfigPlayerLevel` VALUES ('2', '23450', '105');

DROP TABLE IF EXISTS `ConfigSkill`;
CREATE TABLE `ConfigSkill` (
  `id` int(11) DEFAULT 1,
  `name`  varchar(64) DEFAULT "",
  `effect` varchar(128) DEFAULT ""
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `ConfigSkill` VALUES ('1', '万剑诀', '剑芒如雨直落，攻击敌方全体');
INSERT INTO `ConfigSkill` VALUES ('2', '天剑', '人剑合一，身化利剑，攻击敌方全体');
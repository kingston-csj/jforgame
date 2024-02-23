/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : game_data_001

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-10-24 14:08:03
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
-- Table structure for configfunction
-- ----------------------------
DROP TABLE IF EXISTS `configfunction`;
CREATE TABLE `configfunction` (
  `id` int(11) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `openType` varchar(32) DEFAULT NULL,
  `openTarget` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configfunction
-- ----------------------------

-- ----------------------------
-- Table structure for confignotice
-- ----------------------------
DROP TABLE IF EXISTS `confignotice`;
CREATE TABLE `confignotice` (
  `id` int(11) NOT NULL,
  `module` varchar(255) DEFAULT NULL,
  `channel` smallint(6) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of confignotice
-- ----------------------------
INSERT INTO `confignotice` VALUES ('1001', '基础', '0', '功能暂未开放');

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
  `id` int(11) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `effect` varchar(255) DEFAULT NULL COMMENT '作用说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configskill
-- ----------------------------
INSERT INTO `configskill` VALUES ('1', '飞龙探云手', '偷取敌人东西或金钱');
INSERT INTO `configskill` VALUES ('2', '逍遥神剑', '李逍遥自创的绝招 敌方全体');
INSERT INTO `configskill` VALUES ('3', '泰山压顶', '土系高级法术');


-- ----------------------------
-- Table structure for configfunction
-- ----------------------------
DROP TABLE IF EXISTS `configfunction`;
CREATE TABLE `configfunction` (
  `id` int(11) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `openType` varchar(16) DEFAULT NULL,
  `openTarget` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for configcross
-- ----------------------------
DROP TABLE IF EXISTS `configcross`;
CREATE TABLE `configcross` (
  `id` int(11) NOT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `gamePort` int(255) DEFAULT NULL,
  `rpcPort` int(255) DEFAULT NULL,
  `crossServer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configcross
-- ----------------------------
BEGIN;
INSERT INTO `configcross` VALUES (10001, '127.0.0.1', 'Game1', 9527, 9627, 80001);
INSERT INTO `configcross` VALUES (80001, '127.0.0.1', 'Center1', 9528, 9628, 0);
COMMIT;

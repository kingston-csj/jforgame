/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : game_user_001

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-08-02 23:04:42
*/

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `Player`;
CREATE TABLE `Player` (
  `id` bigint(20) DEFAULT NULL,
  `level` int(11) DEFAULT 1,
   `name` varchar(128) ,
   `job` tinyint DEFAULT 0,
   `exp` bigint(20) DEFAULT  0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of Player
-- ----------------------------
insert Player values(10000,99,'kingston',1,12345);

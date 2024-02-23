SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for accountent
-- ----------------------------
DROP TABLE IF EXISTS `accountent`;
CREATE TABLE `accountent` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of accountent
-- ----------------------------
BEGIN;
INSERT INTO `accountent` VALUES (123, 'jforgame');
COMMIT;

-- ----------------------------
-- Table structure for playerent
-- ----------------------------
DROP TABLE IF EXISTS `playerent`;
CREATE TABLE `playerent` (
  `id` bigint(20) NOT NULL,
  `accountId` bigint(20) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `functionBox` varchar(255) DEFAULT NULL,
  `vipRight` varchar(255) DEFAULT NULL,
  `job` smallint(255) DEFAULT NULL,
  `exp` bigint(20) DEFAULT NULL,
  `lastDailyReset` bigint(20) DEFAULT NULL,
  `platform` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of playerent
-- ----------------------------
BEGIN;
INSERT INTO `playerent` VALUES (10000, 123, 99, 'winner', '', 'null', 0, 0, 1615268369455, 'null');
COMMIT;

-- ----------------------------
-- Table structure for systemrecord
-- ----------------------------
DROP TABLE IF EXISTS `systemrecord`;
CREATE TABLE `systemrecord` (
  `key` varchar(255) NOT NULL,
  `value` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of systemrecord
-- ----------------------------
BEGIN;
INSERT INTO `systemrecord` VALUES ('dailyResetTimestamp', '1615268369455');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

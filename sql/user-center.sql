CREATE DATABASE IF NOT EXISTS `user-center` DEFAULT CHARACTER SET = utf8;
Use `user-center`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户 id',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码，加密存储, admin/1234',
  `is_account_non_expired` int(0) NULL DEFAULT 1 COMMENT '帐户是否过期(1 未过期，0已过期)',
  `is_account_non_locked` int(0) NULL DEFAULT 1 COMMENT '帐户是否被锁定(1 未过期，0已过期)',
  `is_credentials_non_expired` int(0) NULL DEFAULT 1 COMMENT '密码是否过期(1 未过期，0已过期)',
  `is_enabled` int(0) NULL DEFAULT 1 COMMENT '帐户是否可用(1 可用，0 删除用户)',
  `nick_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注册手机号',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注册邮箱',
  `create_date` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `mobile`(`mobile`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (9, 'admin', '$2a$10$gKTtTWlcUvGKqtiZo4IKOOYCVdjApKF/lcZwrEheWRWc9ZUMSJ1TC', 1, 1, 1, 1, '硅谷', '16888888888', 'mengxuegu888@163.com', '2023-08-08 11:11:11', '2019-12-16 10:25:53');
INSERT INTO `sys_user` VALUES (10, 'test', '$2a$10$gKTtTWlcUvGKqtiZo4IKOOYCVdjApKF/lcZwrEheWRWc9ZUMSJ1TC', 1, 1, 1, 1, '测试', '16888886666', 'test11@qq.com', '2023-08-08 11:11:11', '2023-08-08 11:11:11');

SET FOREIGN_KEY_CHECKS = 1;

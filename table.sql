/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.27 : Database - zjut-live
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`zjut-live` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `zjut-live`;

/*Table structure for table `block` */

DROP TABLE IF EXISTS `block`;

CREATE TABLE `block` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `target_user_id` bigint NOT NULL COMMENT '被拉黑用户id',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='拉黑表';

/*Table structure for table `circle` */

DROP TABLE IF EXISTS `circle`;

CREATE TABLE `circle` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `creator_id` bigint NOT NULL COMMENT '圈主id',
  `name` varchar(12) NOT NULL COMMENT '圈子名称',
  `avatar` varchar(100) NOT NULL COMMENT '圈子头像',
  `resume` varchar(12) NOT NULL DEFAULT '' COMMENT '一句话描述',
  `description` varchar(255) NOT NULL COMMENT '详细描述',
  `activity_count` int unsigned NOT NULL DEFAULT '0' COMMENT '活跃度',
  `join_count` int unsigned NOT NULL DEFAULT '0' COMMENT '已加入用户数',
  `post_count` int unsigned NOT NULL DEFAULT '0' COMMENT '动态数',
  `category_id` int unsigned NOT NULL COMMENT '圈子类别id',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='圈子表';

/*Table structure for table `circle_category` */

DROP TABLE IF EXISTS `circle_category`;

CREATE TABLE `circle_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(12) NOT NULL COMMENT '圈子种类名',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='圈子种类表';

/*Table structure for table `circle_join` */

DROP TABLE IF EXISTS `circle_join`;

CREATE TABLE `circle_join` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `circle_id` bigint NOT NULL COMMENT '圈子id',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='加入圈子记录表';

/*Table structure for table `coin_record` */

DROP TABLE IF EXISTS `coin_record`;

CREATE TABLE `coin_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `resource_id` bigint DEFAULT NULL COMMENT '资源id',
  `resource_type` char(1) DEFAULT '0' COMMENT '资源类型，0帖子，1评论',
  `count` int unsigned NOT NULL COMMENT '操作数',
  `operation_type` tinyint NOT NULL DEFAULT '0' COMMENT '操作类型，0为加，1为减',
  `remain` int unsigned NOT NULL DEFAULT '0' COMMENT '剩余数',
  `note` varchar(256) NOT NULL COMMENT '备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='积分记录表';

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(256) NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '创建用户id',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '评论类型，0评论 1回复 2置顶评论',
  `reply_comment_id` bigint DEFAULT NULL COMMENT '回复的评论id (type=1时有效)',
  `reply_user_id` bigint DEFAULT NULL COMMENT '回复的用户id (type=1时有效)',
  `post_id` bigint NOT NULL COMMENT '动态id',
  `comment_id` bigint DEFAULT NULL COMMENT '父级评论id（type=1时有效）',
  `ip` varchar(128) DEFAULT NULL COMMENT '评论ip',
  `reply_count` int unsigned NOT NULL DEFAULT '0' COMMENT '回复数',
  `thumb_count` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `is_delete` tinyint DEFAULT '0' COMMENT '是否删除（0正常 1删除）',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';

/*Table structure for table `follow` */

DROP TABLE IF EXISTS `follow`;

CREATE TABLE `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `follow_user_id` bigint NOT NULL COMMENT '被关注用户id',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户关注表';

/*Table structure for table `level` */

DROP TABLE IF EXISTS `level`;

CREATE TABLE `level` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(12) NOT NULL COMMENT '等级名',
  `count` int unsigned NOT NULL COMMENT '所需经验值',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='等级表';

/*Table structure for table `message` */

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `send_user_id` bigint NOT NULL COMMENT '发送用户id',
  `receive_user_id` bigint NOT NULL COMMENT '接收用户id',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '消息类型，0文字，1图片',
  `content` varchar(1024) NOT NULL COMMENT '内容',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读，0未读，1已读',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息表';

/*Table structure for table `notification` */

DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `send_user_id` bigint NOT NULL COMMENT '发送用户id',
  `receive_user_id` bigint NOT NULL COMMENT '接收用户id',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '消息类型，0系统，1评论，2回复，3点赞',
  `title` varchar(64) DEFAULT NULL COMMENT '标题',
  `content` varchar(1024) NOT NULL COMMENT '内容',
  `url` varchar(1024) DEFAULT NULL COMMENT 'url地址',
  `cover_resource_id` varchar(32) DEFAULT NULL COMMENT '封面图资源id',
  `resource_id` bigint DEFAULT NULL COMMENT '资源id',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读，0未读，1已读',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知表';

/*Table structure for table `post` */

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `type` char(2) NOT NULL DEFAULT '00' COMMENT '动态类型，00普通贴 01视频贴 02投票贴 03回复可见贴 04活动贴 05收费贴 90公告贴 ',
  `content` text NOT NULL COMMENT '内容',
  `circle_id` bigint NOT NULL DEFAULT '0' COMMENT '圈子id',
  `comment_count` int unsigned DEFAULT '0' COMMENT '评论数',
  `coin_count` int unsigned DEFAULT '0' COMMENT '获得金币数',
  `thumb_count` int unsigned DEFAULT '0' COMMENT '点赞数',
  `read_count` int unsigned DEFAULT '0' COMMENT '阅读量',
  `is_essence` tinyint DEFAULT '0' COMMENT '是否精华贴,0否 1是',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶,0否 1是',
  `status` char(1) DEFAULT '0' COMMENT '状态,0审核中 1正常 2评论被锁定',
  `weight` float DEFAULT '1' COMMENT '计算分数权重，默认为1',
  `is_delete` tinyint DEFAULT '0' COMMENT '是否被删除，0否 1是',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态表';

/*Table structure for table `r_post_topic` */

DROP TABLE IF EXISTS `r_post_topic`;

CREATE TABLE `r_post_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` bigint NOT NULL COMMENT '动态id',
  `topic_id` bigint NOT NULL COMMENT '话题id',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='动态-话题关系表';

/*Table structure for table `resource` */

DROP TABLE IF EXISTS `resource`;

CREATE TABLE `resource` (
  `id` varchar(32) NOT NULL COMMENT '资源id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `type` char(1) NOT NULL DEFAULT '0' COMMENT '文件类型，0图片 1视频 2文件',
  `size` varchar(12) DEFAULT NULL COMMENT '文件大小',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '状态，0正在审核 1审核通过 2审核不通过',
  `url` varchar(256) NOT NULL COMMENT '真实地址',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资源表';

/*Table structure for table `topic` */

DROP TABLE IF EXISTS `topic`;

CREATE TABLE `topic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(12) NOT NULL COMMENT '话题名',
  `creator_id` bigint NOT NULL COMMENT '创建用户id',
  `description` varchar(255) NOT NULL COMMENT '话题描述',
  `post_count` int unsigned NOT NULL DEFAULT '0' COMMENT '动态数',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话题表';

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uid',
  `nick_name` varchar(30) NOT NULL COMMENT '昵称',
  `user_name` varchar(30) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `user_type` char(1) DEFAULT '1' COMMENT '0管理员，1学生，2教师',
  `gender` char(1) DEFAULT '0' COMMENT '用户性别（0未知 1男 2女）',
  `phone` varchar(30) DEFAULT NULL COMMENT '电话号码',
  `avatar` varchar(100) DEFAULT NULL COMMENT '头像地址',
  `status` char(1) DEFAULT '2' COMMENT '帐号状态（0正常 1停用 2未激活）',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `hometown` varchar(30) DEFAULT NULL COMMENT '故乡',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `sign` varchar(100) DEFAULT NULL COMMENT '个性签名',
  `college` varchar(30) DEFAULT NULL COMMENT '学院名',
  `major` varchar(30) DEFAULT NULL COMMENT '专业名',
  `grade` int unsigned DEFAULT NULL COMMENT '年级',
  `coin_count` int unsigned DEFAULT '0' COMMENT '金币数',
  `level_count` int unsigned DEFAULT '0' COMMENT '经验数',
  `post_count` int unsigned DEFAULT '0' COMMENT '动态数',
  `like_count` int unsigned DEFAULT '0' COMMENT '点赞数',
  `fan_count` int unsigned DEFAULT '0' COMMENT '粉丝数',
  `follow_count` int unsigned DEFAULT '0' COMMENT '关注数',
  `is_delete` tinyint DEFAULT '0' COMMENT '是否删除（0正常 1删除）',
  `sign_count` int unsigned DEFAULT '0' COMMENT '连续签到天数',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '上次登录ip',
  `login_date` datetime DEFAULT NULL COMMENT '上次登录时间',
  `cx_id` bigint DEFAULT NULL COMMENT '超星用户id',
  `wejh_id` bigint DEFAULT NULL COMMENT '微精弘id',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10127 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

/*Table structure for table `user_tag` */

DROP TABLE IF EXISTS `user_tag`;

CREATE TABLE `user_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `name` varchar(12) NOT NULL COMMENT '标签名',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户标签表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

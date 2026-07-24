# 数据库重构计划

> 分析时间: 2026-07-19
> 基于当前 DDD 架构对 vela 数据库的评估与优化方案

---

## 一、现有问题清单

### 🔴 严重问题

| # | 问题 | 影响 | 位置 |
|---|------|------|------|
| 1 | 字符集使用 `utf8` 而非 `utf8mb4` | 无法存储 emoji/特殊符号，插入 emoji 会报错 | 全部 12 张表 |
| 2 | `vela_message_body.message_body` 字段 `varchar(5000)` | 长消息/图文消息/文件消息会被截断 | im_message_body |
| 3 | `app_user` 表冗余 | 代码中实体映射的是 `vela_user_data`，`app_user` 无人使用 | app_user |

### 🟡 中等问题

| # | 问题 | 影响 | 位置 |
|---|------|------|------|
| 4 | `vela_friendship.extra` 注释错误 | 写成了"来源"，实为"扩展字段" | im_friendship |
| 5 | `vela_message_history.message_time` 注释错误 | 写成了"来源"，实为"消息发送时间" | im_message_history |
| 6 | `vela_group_message_history.message_time` 注释错误 | 同上 | im_group_message_history |
| 7 | 缺少 `sequence` 字段索引 | 按 sequence 查询好友关系/群组时全表扫描 | im_friendship, im_group |
| 8 | 所有表的 `create_time`/`update_time` 使用 `bigint(20)` 时间戳 | 不直观，不易读，建议改为 `datetime` | 全部表 |

### 🟢 建议优化

| # | 问题 | 建议 |
|---|------|------|
| 9 | 缺少 `deleted` 逻辑删除统一字段 | 统一使用 `del_flag` 或 `is_deleted` |
| 10 | 测试数据混入建表 SQL | `vela-send.sql` 中包含 `INSERT INTO` 测试数据 |
| 11 | 好友分组表 `vela_friendship_group_member` 缺少 `app_id` | 多租户场景下无法区分 app |
| 12 | 引擎全部为 `InnoDB` | ✅ 正确，无需修改 |
| 13 | 字符集不统一 | `SET NAMES utf8mb4` 但表定义是 `utf8`，矛盾 |

---

## 二、重构后的完整 SQL

```sql
-- =====================================================
-- 数据库: vela
-- 字符集: utf8mb4 (支持 emoji)
-- 引擎: InnoDB
-- 版本: 2.0 (DDD 重构版)
-- =====================================================

CREATE DATABASE IF NOT EXISTS `vela` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `vela`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户数据表
-- ----------------------------
DROP TABLE IF EXISTS `vela_user_data`;
CREATE TABLE `vela_user_data` (
  `user_id`       VARCHAR(50)  NOT NULL COMMENT '用户ID',
  `app_id`        INT(11)      NOT NULL COMMENT '应用ID',
  `nick_name`     VARCHAR(100)          DEFAULT NULL COMMENT '昵称',
  `password`      VARCHAR(255)          DEFAULT NULL COMMENT '密码(bcrypt加密)',
  `photo`         VARCHAR(255)          DEFAULT NULL COMMENT '头像URL',
  `user_sex`      TINYINT(4)            DEFAULT NULL COMMENT '性别: 0-未知 1-男 2-女',
  `birth_day`     DATE                  DEFAULT NULL COMMENT '生日',
  `location`      VARCHAR(100)          DEFAULT NULL COMMENT '地址',
  `self_signature` VARCHAR(255)         DEFAULT NULL COMMENT '个性签名',
  `friend_allow_type` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '加好友验证: 1-无需验证 2-需要验证',
  `forbidden_flag` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '禁用: 0-正常 1-禁用',
  `disable_add_friend` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '禁止加好友: 0-允许 1-禁止',
  `silent_flag`   TINYINT(4) NOT NULL DEFAULT 0 COMMENT '禁言: 0-正常 1-禁言',
  `user_type`     TINYINT(4) NOT NULL DEFAULT 1 COMMENT '用户类型: 1-普通 2-客服 3-机器人',
  `del_flag`      TINYINT(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-删除',
  `extra`         VARCHAR(1000)         DEFAULT NULL COMMENT '扩展字段',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `user_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_nick_name` (`nick_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户数据表';

-- ----------------------------
-- 2. 好友关系表
-- ----------------------------
DROP TABLE IF EXISTS `vela_friendship`;
CREATE TABLE `vela_friendship` (
  `app_id`          INT(20)     NOT NULL COMMENT '应用ID',
  `from_id`         VARCHAR(50) NOT NULL COMMENT '发起方用户ID',
  `to_id`           VARCHAR(50) NOT NULL COMMENT '接收方用户ID',
  `remark`          VARCHAR(100)         DEFAULT NULL COMMENT '备注名',
  `status`          TINYINT(4)           DEFAULT NULL COMMENT '好友状态: 1-正常 2-删除',
  `black`           TINYINT(4)           DEFAULT 1 COMMENT '黑名单: 1-正常 2-拉黑',
  `add_source`      VARCHAR(20)          DEFAULT NULL COMMENT '好友来源',
  `friend_sequence` BIGINT(20)           DEFAULT NULL COMMENT '好友关系序列号',
  `black_sequence`  BIGINT(20)           DEFAULT NULL COMMENT '黑名单操作序列号',
  `extra`           VARCHAR(1000)        DEFAULT NULL COMMENT '扩展字段',
  `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `from_id`, `to_id`),
  INDEX `idx_to_id` (`to_id`),
  INDEX `idx_friend_sequence` (`app_id`, `from_id`, `friend_sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友关系表';

-- ----------------------------
-- 3. 好友分组表
-- ----------------------------
DROP TABLE IF EXISTS `vela_friendship_group`;
CREATE TABLE `vela_friendship_group` (
  `app_id`      INT(20)     NOT NULL COMMENT '应用ID',
  `from_id`     VARCHAR(50) NOT NULL COMMENT '用户ID',
  `group_id`    BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '分组ID',
  `group_name`  VARCHAR(50) NOT NULL COMMENT '分组名称',
  `sequence`    BIGINT(20)           DEFAULT NULL COMMENT '序列号',
  `del_flag`    TINYINT(4)  NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-删除',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_id`),
  UNIQUE INDEX `uk_app_user_group` (`app_id`, `from_id`, `group_name`),
  INDEX `idx_from_id` (`from_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友分组表';

-- ----------------------------
-- 4. 好友分组成员表
-- ----------------------------
DROP TABLE IF EXISTS `vela_friendship_group_member`;
CREATE TABLE `vela_friendship_group_member` (
  `app_id`   INT(20)     NOT NULL COMMENT '应用ID',
  `group_id` BIGINT(20)  NOT NULL COMMENT '分组ID',
  `to_id`    VARCHAR(50) NOT NULL COMMENT '成员用户ID',
  PRIMARY KEY (`group_id`, `to_id`),
  INDEX `idx_to_id` (`to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友分组成员表';

-- ----------------------------
-- 5. 好友请求表
-- ----------------------------
DROP TABLE IF EXISTS `vela_friendship_request`;
CREATE TABLE `vela_friendship_request` (
  `id`             BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '请求ID',
  `app_id`         INT(20)     NOT NULL COMMENT '应用ID',
  `from_id`        VARCHAR(50) NOT NULL COMMENT '发起方用户ID',
  `to_id`          VARCHAR(50) NOT NULL COMMENT '接收方用户ID',
  `remark`         VARCHAR(100)         DEFAULT NULL COMMENT '备注',
  `read_status`    TINYINT(4)           DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
  `add_source`     VARCHAR(20)          DEFAULT NULL COMMENT '好友来源',
  `add_wording`    VARCHAR(200)         DEFAULT NULL COMMENT '好友验证信息',
  `approve_status` TINYINT(4)           DEFAULT 0 COMMENT '审批状态: 0-待处理 1-同意 2-拒绝',
  `sequence`       BIGINT(20)           DEFAULT NULL COMMENT '序列号',
  `create_time`    DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_from_id` (`from_id`),
  INDEX `idx_to_id` (`to_id`),
  INDEX `idx_sequence` (`app_id`, `to_id`, `sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友请求表';

-- ----------------------------
-- 6. 群组表
-- ----------------------------
DROP TABLE IF EXISTS `vela_group`;
CREATE TABLE `vela_group` (
  `app_id`           INT(20)     NOT NULL COMMENT '应用ID',
  `group_id`         VARCHAR(50) NOT NULL COMMENT '群组ID',
  `owner_id`         VARCHAR(50) NOT NULL COMMENT '群主ID',
  `group_type`       TINYINT(4)           DEFAULT NULL COMMENT '群类型: 1-私有群 2-公开群',
  `group_name`       VARCHAR(100)         DEFAULT NULL COMMENT '群名称',
  `mute`             TINYINT(4)           DEFAULT 0 COMMENT '全员禁言: 0-不禁言 1-全员禁言',
  `apply_join_type`  TINYINT(4)           DEFAULT 0 COMMENT '加群方式: 0-禁止加入 1-需要审批 2-自由加入',
  `photo`            VARCHAR(300)         DEFAULT NULL COMMENT '群头像URL',
  `max_member_count` INT(20)              DEFAULT 500 COMMENT '群成员上限',
  `introduction`     VARCHAR(200)         DEFAULT NULL COMMENT '群简介',
  `notification`     VARCHAR(1000)        DEFAULT NULL COMMENT '群公告',
  `status`           TINYINT(4)           DEFAULT 0 COMMENT '群状态: 0-正常 1-解散',
  `sequence`         BIGINT(20)           DEFAULT NULL COMMENT '序列号',
  `extra`            VARCHAR(1000)        DEFAULT NULL COMMENT '扩展字段',
  `create_time`      DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `group_id`),
  INDEX `idx_owner_id` (`owner_id`),
  INDEX `idx_sequence` (`app_id`, `sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群组表';

-- ----------------------------
-- 7. 群组成员表
-- ----------------------------
DROP TABLE IF EXISTS `vela_group_member`;
CREATE TABLE `vela_group_member` (
  `group_member_id` BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `app_id`          INT(20)     NOT NULL COMMENT '应用ID',
  `group_id`        VARCHAR(50) NOT NULL COMMENT '群组ID',
  `member_id`       VARCHAR(50) NOT NULL COMMENT '成员用户ID',
  `role`            TINYINT(4)           DEFAULT 0 COMMENT '角色: 0-普通成员 1-管理员 2-群主 3-禁言 4-已移除',
  `speak_date`      BIGINT(20)           DEFAULT NULL COMMENT '禁言截止时间戳',
  `alias`           VARCHAR(100)         DEFAULT NULL COMMENT '群昵称',
  `join_type`       VARCHAR(50)          DEFAULT NULL COMMENT '加入类型',
  `join_time`       DATETIME             DEFAULT NULL COMMENT '加入时间',
  `leave_time`      DATETIME             DEFAULT NULL COMMENT '离开时间',
  `extra`           VARCHAR(1000)        DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`group_member_id`),
  UNIQUE INDEX `uk_group_member` (`app_id`, `group_id`, `member_id`),
  INDEX `idx_member_id` (`member_id`),
  INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群组成员表';

-- ----------------------------
-- 8. 消息体表
-- 分表策略: message_body_0 ~ message_body_63 (按 message_key 哈希)
-- ----------------------------
DROP TABLE IF EXISTS `vela_message_body`;
CREATE TABLE `vela_message_body` (
  `app_id`      INT(10)      NOT NULL COMMENT '应用ID',
  `message_key` BIGINT(50)   NOT NULL COMMENT '消息唯一Key',
  `message_body` MEDIUMTEXT           DEFAULT NULL COMMENT '消息体内容(支持长文本)',
  `security_key` VARCHAR(100)         DEFAULT NULL COMMENT '安全校验Key',
  `message_time` DATETIME             DEFAULT NULL COMMENT '消息发送时间',
  `del_flag`    TINYINT(4)            DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-撤回/删除',
  `extra`       VARCHAR(1000)         DEFAULT NULL COMMENT '扩展字段',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`message_key`),
  INDEX `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息体表';

-- ----------------------------
-- 9. 单聊消息历史表
-- 分表策略: message_history_0 ~ message_history_63 (按 owner_id 哈希)
-- ----------------------------
DROP TABLE IF EXISTS `vela_message_history`;
CREATE TABLE `vela_message_history` (
  `app_id`        INT(20)     NOT NULL COMMENT '应用ID',
  `owner_id`      VARCHAR(50) NOT NULL COMMENT '消息所属用户ID',
  `from_id`       VARCHAR(50) NOT NULL COMMENT '发送方用户ID',
  `to_id`         VARCHAR(50) NOT NULL COMMENT '接收方用户ID',
  `message_key`   BIGINT(50)  NOT NULL COMMENT '消息体Key',
  `sequence`      BIGINT(20)           DEFAULT NULL COMMENT '消息序列号',
  `message_random` INT(20)             DEFAULT NULL COMMENT '消息随机码(去重)',
  `message_time`  DATETIME             DEFAULT NULL COMMENT '消息发送时间',
  `create_time`   DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`app_id`, `owner_id`, `message_key`),
  INDEX `idx_sequence` (`app_id`, `owner_id`, `sequence`),
  INDEX `idx_from_id` (`from_id`),
  INDEX `idx_to_id` (`to_id`),
  INDEX `idx_message_time` (`message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单聊消息历史表';

-- ----------------------------
-- 10. 群聊消息历史表
-- 分表策略: group_message_0 ~ group_message_63 (按 group_id 哈希)
-- ----------------------------
DROP TABLE IF EXISTS `vela_group_message_history`;
CREATE TABLE `vela_group_message_history` (
  `app_id`        INT(20)     NOT NULL COMMENT '应用ID',
  `group_id`      VARCHAR(50) NOT NULL COMMENT '群组ID',
  `from_id`       VARCHAR(50) NOT NULL COMMENT '发送方用户ID',
  `message_key`   BIGINT(50)  NOT NULL COMMENT '消息体Key',
  `sequence`      BIGINT(20)           DEFAULT NULL COMMENT '消息序列号',
  `message_random` INT(20)             DEFAULT NULL COMMENT '消息随机码(去重)',
  `message_time`  DATETIME             DEFAULT NULL COMMENT '消息发送时间',
  `create_time`   DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`app_id`, `group_id`, `message_key`),
  INDEX `idx_sequence` (`app_id`, `group_id`, `sequence`),
  INDEX `idx_from_id` (`from_id`),
  INDEX `idx_message_time` (`message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群聊消息历史表';

-- ----------------------------
-- 11. 会话设置表
-- ----------------------------
DROP TABLE IF EXISTS `vela_conversation_set`;
CREATE TABLE `vela_conversation_set` (
  `conversation_id`  VARCHAR(255) NOT NULL COMMENT '会话ID(格式: type_fromId_toId)',
  `app_id`           INT(10)      NOT NULL COMMENT '应用ID',
  `conversation_type` TINYINT(4)           DEFAULT 0 COMMENT '会话类型: 0-单聊 1-群聊 2-机器人 3-公众号',
  `owner_id`         VARCHAR(50)  NOT NULL COMMENT '会话所属用户ID',
  `to_id`            VARCHAR(50)           DEFAULT NULL COMMENT '对方ID(单聊为对方用户ID，群聊为群组ID)',
  `is_mute`          TINYINT(4)            DEFAULT 0 COMMENT '免打扰: 0-关闭 1-开启',
  `is_top`           TINYINT(4)            DEFAULT 0 COMMENT '置顶: 0-否 1-是',
  `sequence`         BIGINT(20)            DEFAULT NULL COMMENT '会话序列号',
  `readed_sequence`  BIGINT(20)            DEFAULT NULL COMMENT '已读消息最大序列号',
  `create_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `conversation_id`),
  INDEX `idx_owner_id` (`owner_id`),
  INDEX `idx_sequence` (`app_id`, `owner_id`, `sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话设置表';

-- ----------------------------
-- 12. 删除冗余表
-- ----------------------------
-- app_user 表已废弃，代码使用 im_user_data
-- 如有数据请迁移后执行:
-- DROP TABLE IF EXISTS `app_user`;

SET FOREIGN_KEY_CHECKS = 1;
```

---

## 三、变更对照表

| 表名 | 变更内容 | 影响范围 |
|------|---------|---------|
| 全部表 | 字符集 `utf8` → `utf8mb4` | 需重建表或 ALTER |
| 全部表 | `bigint` 时间戳 → `DATETIME` | 需修改代码中的时间字段类型 |
| `vela_message_body` | `message_body` `varchar(5000)` → `MEDIUMTEXT` | 需修改实体字段类型 |
| `vela_conversation_set` | `from_id` → `owner_id`（语义修正） | 需修改代码实体字段名 |
| `vela_friendship` | `extra` 注释修正 | 仅文档 |
| `vela_friendship_group_member` | 新增 `app_id` 字段 | 需修改代码实体 |
| `vela_group_member` | 新增 `UNIQUE INDEX(app_id, group_id, member_id)` | 防止重复添加 |
| 全部表 | 补充 `create_time`/`update_time` 默认值 | 代码层无需改 |
| `app_user` | 建议删除（冗余） | 无影响 |

---

## 四、迁移步骤

### Step 1: 备份
```sql
-- 导出旧数据
mysqldump -u root -p vela > vela-backup-20260719.sql
```

### Step 2: 执行新 SQL
```bash
mysql -u root -p vela < database-refactor-plan.sql
```

### Step 3: 迁移数据
```sql
-- 迁移 im_user_data 数据（字符集转换）
ALTER TABLE im_user_data CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 迁移其他表同理...
```

### Step 4: 验证
```sql
-- 检查字符集
SELECT TABLE_NAME, TABLE_COLLATION FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'vela';

-- 插入 emoji 测试
INSERT INTO im_user_data (user_id, app_id, nick_name) VALUES ('test', 10000, '😊🔥🎉');
```
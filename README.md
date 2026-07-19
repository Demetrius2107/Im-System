# IM-System — 基于 DDD 六边形架构的即时通讯系统

> **企业级即时通讯系统** | 基于 SpringBoot + Netty 实现的高性能即时通讯系统

---

## 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [架构设计](#架构设计)
- [模块说明](#模块说明)
- [功能特性](#功能特性)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [开发规范](#开发规范)
- [更新日志](#更新日志)

---

## 项目概述

IM-System 是一个基于 DDD（领域驱动设计）六边形架构的高性能即时通讯系统。系统采用 **网关 + 业务服务 + 存储服务** 分离的微服务架构，支持 TCP 和 WebSocket 双协议接入，提供完整的消息推送、好友关系、群组管理、会话管理等功能。

### 核心设计理念

- **DDD 四层架构**: 接口适配层 → 应用层 → 领域层 → 基础设施层
- **六边形架构**: 入站适配器（TCP/WebSocket/HTTP）与出站适配器（Redis/MQ/MySQL/ZK）围绕核心业务逻辑
- **事件驱动**: 通过 RabbitMQ 实现网关与业务服务的异步解耦
- **多端同步**: 支持手机/电脑/Web 多端同时在线与消息同步

---

## 技术栈

| 类别 | 技术 | 用途 |
|------|------|------|
| 开发语言 | Java 8 | 核心开发语言 |
| 框架 | SpringBoot 2.3.2 | 业务服务容器 |
| 网络框架 | Netty 4.1.35 | TCP/WebSocket 长连接 |
| ORM | MyBatis-Plus 3.4.2 | 数据库访问 |
| 缓存 | Redis / Redisson 3.15.6 | Session 管理、离线消息、序列生成 |
| 消息队列 | RabbitMQ | 异步消息、事件驱动 |
| 服务注册 | ZooKeeper | TCP 网关节点注册与发现 |
| 序列化 | Protostuff | 自定义 TCP 协议编解码 |
| 路由算法 | 一致性哈希 / 轮询 / 随机 | 多网关负载均衡 |
| 构建工具 | Maven | 项目管理 |
| 数据库 | MySQL | 持久化存储 |

---

## 架构设计

### DDD 六边形架构总览

```
┌─────────────────────────────────────────────────────────────────┐
│                      入站适配器 (Inbound Adapters)                │
│  ┌─────────┐  ┌──────────┐  ┌────────────┐  ┌──────────────┐  │
│  │  TCP    │  │WebSocket │  │  HTTP REST │  │  MQ Consumer │  │
│  │ (Netty) │  │ (Netty)  │  │ (SpringMV) │  │  (RabbitMQ)  │  │
│  └────┬────┘  └────┬─────┘  └─────┬──────┘  └──────┬───────┘  │
└───────┼────────────┼──────────────┼─────────────────┼──────────┘
        │            │              │                 │
        ▼            ▼              ▼                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    应用层 (Application Layer)                     │
│           ┌──────────────────────────────┐                      │
│           │  应用服务 / DTO / Assembler   │                      │
│           └──────────────────────────────┘                      │
├─────────────────────────────────────────────────────────────────┤
│                      领域层 (Domain Layer)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │  领域实体     │  │  值对象      │  │  领域服务 / 仓储接口  │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                  基础设施层 (Infrastructure Layer)                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌───────────────┐ │  │
│  │  MySQL   │  │  Redis   │  │RabbitMQ  │  │  ZooKeeper   │  │  │
│  │ (MyBatis)│  │(Redisson)│  │(Producer)│  │  (Registry)  │  │  │
│  └──────────┘  └──────────┘  └──────────┘  └───────────────┘ │  │
└─────────────────────────────────────────────────────────────────┘
```

### 数据流

```
Client ──TCP/WS──→ im-tcp(网关) ──Feign/MQ──→ im-service(业务) ──MQ──→ im-message-store(存储)
                        │                                              │
                        ├── Redis (Session/缓存)                       ├── MySQL (持久化)
                        ├── RabbitMQ (事件)                            └── RabbitMQ (消费)
                        └── ZooKeeper (注册)
```

---

## 模块说明

### 1. `im-common` — 共享内核层 (Shared Kernel)

**路径**: `com.lip.im.shared`

DDD 分层中的共享内核，包含所有领域通用的值对象、枚举、异常、工具类等。

| 子包 | 说明 |
|------|------|
| `base` | 基础类 (ResponseVO) |
| `types` | 值对象、枚举、消息类型 |
| `exception` | 异常定义 |
| `config` | 配置类 |
| `constants` | 常量定义 |
| `route` | 路由策略（一致性哈希/轮询/随机） |
| `utils` | 工具类 |

### 2. `im-codec` — 基础设施层：协议编解码 (Infrastructure)

**路径**: `com.lip.im.codec`

自定义 TCP 协议和 WebSocket 协议的编解码器。

| 子包 | 说明 |
|------|------|
| `protocol` | 消息编解码器 + 协议定义 |
| `pack` | 各领域消息包 DTO |
| `config` | 启动配置 |
| `utils` | 工具类 |

### 3. `im-tcp` — 接口适配层：入站适配器 (Inbound Adapter)

**路径**: `com.lip.im.tcp`

Netty TCP/WebSocket 服务器，是系统的入站网关。

| 子包 | 说明 |
|------|------|
| `interfaces/handler` | Netty 处理器（心跳/消息处理） |
| `interfaces/server` | TCP/WebSocket 服务器 |
| `interfaces/publish` | MQ 消息生产者 |
| `interfaces/reciver` | MQ 消息消费者 |
| `interfaces/fegin` | Feign 服务调用 |
| `infrastructure/redis` | Redis 客户端 |
| `infrastructure/register` | ZooKeeper 注册 |
| `infrastructure/utils` | Session 管理 |

### 4. `im-service` — 核心业务层 (Core Business)

**路径**: `com.lip.im.service`

系统的核心业务逻辑，按 DDD 有界上下文划分为 5 个领域。

#### 用户域 (User)

| 层次 | 组件 | 功能 |
|------|------|------|
| `interfaces/rest` | ImUserController, ImUserDataController | 用户 REST API |
| `interfaces/mq` | UserOnlineStatusReceiver | 在线状态 MQ 消费 |
| `application/dto` | 请求/响应 DTO | 数据传输对象 |
| `domain/entity` | ImUserDataEntity | 用户领域实体 |
| `domain/service` | ImUserService, ImUserStatusService | 领域服务接口 |
| `infrastructure/...` | Mapper + 服务实现 | 基础设施 |

#### 好友关系域 (Friendship)

| 功能 | 说明 |
|------|------|
| 好友管理 | 添加/删除/更新好友 |
| 好友请求 | 发送/审批/已读好友请求 |
| 黑名单 | 添加/移除黑名单 |
| 好友分组 | 创建/删除/成员管理 |
| 关系链同步 | 基于 Sequence 的增量同步 |

#### 群组域 (Group)

| 功能 | 说明 |
|------|------|
| 群组管理 | 创建/解散/更新群信息 |
| 群成员管理 | 添加/移除/角色设置 |
| 群禁言 | 全员禁言/成员禁言 |
| 群消息 | 群聊消息发送/离线/同步 |

#### 消息域 (Message)

| 功能 | 说明 |
|------|------|
| P2P 消息 | 单聊消息发送/ACK/同步 |
| 群聊消息 | 群成员遍历分发 |
| 消息已读 | 单聊/群聊已读回执 |
| 消息撤回 | 超时校验(120s) |
| 离线消息 | Redis ZSet 存储 |
| 发送校验 | 禁言/禁用/好友关系校验 |

#### 会话域 (Conversation)

| 功能 | 说明 |
|------|------|
| 会话管理 | 标记已读/删除 |
| 会话设置 | 置顶/免打扰 |
| 会话同步 | 基于 Sequence 增量拉取 |

### 5. `im-message-store` — 基础设施层：持久化适配器

**路径**: `com.lip.im.store`

消息持久化独立服务，通过 RabbitMQ 异步消费消息存储任务。

| 子包 | 说明 |
|------|------|
| `interfaces/mq` | MQ 消息消费者 |
| `application/service` | 消息存储应用服务 |
| `application/dto` | 存储 DTO |
| `domain/entity` | 持久化实体 |
| `infrastructure/persistence` | MyBatis Mapper |
| `infrastructure/config` | 配置类 |

---

## 功能特性

### ✅ 已完成功能

- [x] 用户注册/登录/信息管理
- [x] 好友关系链管理（增删改查、黑名单、分组）
- [x] 好友请求审批流程
- [x] 群组管理（创建/解散/更新/禁言/转让）
- [x] 群成员管理（添加/移除/角色）
- [x] P2P 单聊消息（ACK/去重/同步）
- [x] 群聊消息（群成员分发/离线存储）
- [x] 消息已读回执
- [x] 消息撤回（120s 超时）
- [x] 离线消息同步（Redis ZSet）
- [x] 多端登录模式（4种策略）
- [x] TCP + WebSocket 双协议支持
- [x] 服务注册与发现（ZooKeeper）
- [x] 路由策略（一致性哈希/轮询/随机）
- [x] 接口鉴权（userSig）
- [x] 回调机制（用户/好友/群组/消息变更回调）

### 🚧 待完善功能

- [ ] 完整的 Web 客户端
- [ ] Docker 容器化部署
- [ ] 单元测试覆盖
- [ ] 消息搜索功能
- [ ] 文件/图片消息
- [ ] 语音/视频通话

---

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis 3.2+
- RabbitMQ 3.8+
- ZooKeeper 3.5+

### 启动步骤

#### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `im-core` DEFAULT CHARACTER SET utf8mb4;

-- 导入表结构
source docs/MySQL/im-core-study.sql;
```

#### 2. 配置中间件

根据实际情况修改各模块的配置文件：

| 模块 | 配置文件 |
|------|----------|
| im-tcp | `im-tcp/src/main/resources/config.yml` |
| im-service | `im-service/src/main/resources/application.yml` |
| im-message-store | `im-message-store/src/main/resources/application.yml` |

关键配置项：
- MySQL 连接地址/账号/密码
- Redis 连接地址
- RabbitMQ 连接地址
- ZooKeeper 连接地址

#### 3. 编译项目

```bash
mvn clean compile
```

#### 4. 启动顺序

1. 启动 Redis、RabbitMQ、ZooKeeper、MySQL
2. 启动 `im-service`（业务服务，端口 8000）
3. 启动 `im-message-store`（消息存储服务）
4. 启动 `im-tcp`（TCP/WebSocket 网关，TCP 端口 9000，WS 端口 19000）

---

## 项目结构

```
IM-System/
├── im-common/               # 共享内核层
│   └── src/main/java/com/lip/im/shared/
├── im-codec/                # 基础设施：协议编解码
│   └── src/main/java/com/lip/im/codec/
├── im-tcp/                  # 接口适配层：TCP/WebSocket 网关
│   └── src/main/java/com/lip/im/tcp/
├── im-service/              # 核心业务层
│   └── src/main/java/com/lip/im/service/
│       ├── user/            # 用户域
│       ├── friendship/      # 好友关系域
│       ├── group/           # 群组域
│       ├── message/         # 消息域
│       ├── conversation/    # 会话域
│       ├── interfaces/      # 拦截器
│       ├── infrastructure/  # 配置/序列
│       ├── application/     # 工具服务
│       └── exception/       # 全局异常
├── im-message-store/        # 基础设施：消息持久化
│   └── src/main/java/com/lip/im/store/
├── docs/                    # 文档
│   ├── MySQL/               # SQL 脚本
│   └── markdown/            # 知识文档
├── .atomcode/               # AI 编码辅助配置
└── .github/workflows/       # CI 配置
```

---

## 开发规范

### DDD 分层依赖规则

```
interfaces → application → domain ← infrastructure
```

- 接口层依赖应用层和领域层
- 应用层依赖领域层
- 领域层不依赖任何外部框架
- 基础设施层实现领域层定义的接口

### 注释规范

所有类文件需包含标准的类级注释模板：

```java
/**
 * <p>Title: ClassName</p>
 * <p>Description: 类功能描述</p>
 * <p>项目名称: IM-System</p>
 *
 * @author author
 * @since version
 * @createTime 2025-xx-xx
 * @updateTime 2025-xx-xx
 *
 * Copyright © 2025 author All rights reserved
 */
```

详细规范参见 `.atomcode/skills/comment-style.md`。

---

## 更新日志

### 2026-07-19 — DDD 六边形架构重构

- **架构重构**: 全项目按 DDD 四层六边形架构重构
- **模块重组**:
  - `im-common` → 共享内核层 (`com.lip.im.shared`)
  - `im-codec` → 基础设施层 (`com.lip.im.codec`)
  - `im-tcp` → 接口适配层 (`com.lip.im.tcp`)
  - `im-service` → 核心业务层 (`com.lip.im.service`)
  - `im-message-store` → 基础设施层 (`com.lip.im.store`)
- **包迁移**: 所有 Java 包名更新为 DDD 分层结构
- **CI/CD**: 新增 GitHub Actions Maven 构建流程
- **规范**: 引入 `.atomcode` 编码规范和注释模板

### 2025-03-06 — 服务端功能完善

- TCP 网关完成，测试通过
- 编码配置优化

### 2025-01-07

- app-server 端基础完善
- app-client 端搭建

### 2024-12-24

- 实现好友模块基本功能
- 实现好友关系链请求基本功能

---

## 许可证

Copyright © 2025. All rights reserved.
# IM-System 项目开发规范

## 项目架构
- 项目采用 DDD 四层六边形架构
- 5个Maven模块: im-common(共享内核)、im-codec(基础设施编解码)、im-tcp(接口适配器)、im-service(核心业务)、im-message-store(持久化适配器)

## 可用技能 (Skills)
- `comment-style`: 注释规范 (Javadoc 模板)
- `code-review`: 代码审查检查清单
- `commit-message`: Git 提交信息规范
- `im-architecture-deep-dive`: **IM 架构深度设计自检清单** — 对任意核心模块进行五层深度设计（主流程 → 异常边界 → 高并发 → 容灾降级 → 决策论证）。调用方式：`use_skill("im-architecture-deep-dive", "module=消息可靠投递")`
- `global-codex`: **全局编码规范与工作流指令** — 第一性原理、根因解决、挑战用户思路、先理解代码再改、信任 AI 自主探索等。调用方式：`use_skill("global-codex")`

## 包命名规范
- 基础包: `com.lip.im.{module}`
- 共享内核: `com.lip.im.shared.{base|exception|types|constants|config|route|utils}`
- 编解码: `com.lip.im.codec.{protocol|pack|config|utils}`
- TCP网关: `com.lip.im.tcp.{interfaces|infrastructure}`
- 业务服务: `com.lip.im.service.{domain}.{interfaces|application|domain|infrastructure}`
- 消息存储: `com.lip.im.store.{interfaces|application|domain|infrastructure}`

## 代码规范
- 类名: PascalCase, 实体类加 Im 前缀 (如 ImUserDataEntity)
- 方法名: camelCase
- 常量: UPPER_SNAKE_CASE
- 枚举: PascalCase 后缀 Enum
- 接口: 无前缀/后缀, 实现类加 Impl 后缀
- 使用 Lombok 简化 POJO
- 使用 Slf4j 日志
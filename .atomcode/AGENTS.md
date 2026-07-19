# IM-System 项目开发规范

## 项目架构
- 项目采用 DDD 四层六边形架构
- 5个Maven模块: im-common(共享内核)、im-codec(基础设施编解码)、im-tcp(接口适配器)、im-service(核心业务)、im-message-store(持久化适配器)

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
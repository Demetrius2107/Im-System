# Git 提交信息规范

## 格式
```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

## Type 类型
- `feat`: 新功能
- `fix`: 修复bug
- `refactor`: 重构
- `docs`: 文档
- `style`: 代码格式
- `test`: 测试
- `chore`: 构建/工具

## Scope 范围
- `im-common`: 共享内核模块
- `im-codec`: 编解码模块
- `im-tcp`: TCP网关模块
- `im-service`: 业务服务模块 (含子域)
- `im-message-store`: 消息存储模块
- `im-service-user`: 用户域
- `im-service-friendship`: 好友关系域
- `im-service-group`: 群组域
- `im-service-message`: 消息域
- `im-service-conversation`: 会话域

## 示例
```
refactor(im-service-user): 用户域重构为DDD四层架构
```
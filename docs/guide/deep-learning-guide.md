# Vela IM 系统深度学习指南

> 从"代码规范"到"业务深度"的学习方法
> 创建时间: 2026-07-20

---

## 一、核心认知

### 1.1 两个维度

```
代码规范（怎么写）             业务深度（为什么这么写）
────────────────────────────────────────────────
  • 注释格式                      • 消息怎么保证不丢不重
  • 注入方式                      • 群聊 1000 人的扩散策略
  • 告警消除                      • 离线消息 ZSet 容量极限
  • 命名规范                      • 撤回 120 秒的分布式一致性
  • import 优化                   • 已读回执的存储成本

边际收益递减                    边际收益递增
看起来统一了                    真正理解了系统
```

### 1.2 关键转变

```
改代码 → 读代码 → 理解 → 记录 → 只改你真正理解的部分
```

**原则：** 每轮只聚焦一个业务场景，深挖到底，而不是批量改全项目的格式。

---

## 二、选题优先级

### 2.1 第一梯队（面试必问，必须深挖）

| 优先级 | 选题 | 核心文件 | 深度问题 |
|--------|------|---------|---------|
| 🔴 P0 | **消息可靠投递** | P2PMessageService, MessageStoreService, MessageProducer | 消息怎么保证不丢不重不乱序？ACK 丢失怎么办？ |
| 🔴 P0 | **群聊扩散策略** | GroupMessageService, ImGroupMemberService | 写扩散还是读扩散？1000 人 vs 10 万人的策略区别？ |
| 🔴 P0 | **消息存储策略** | MessageStoreService, ImMessageBodyMapper, ImMessageHistoryMapper | 分表分库？冷热分离？归档策略？ |

### 2.2 第二梯队（面试加分）

| 优先级 | 选题 | 核心文件 | 深度问题 |
|--------|------|---------|---------|
| 🟡 P1 | **离线消息 ZSet** | MessageStoreService | ZSet 容量上限？性能退化点？降级策略？ |
| 🟡 P1 | **已读回执** | MessageSyncService, ConversationService | 群聊已读怎么聚合？存储成本怎么算？ |
| 🟡 P1 | **多通道推送** | MessageProducer, 网关模块 | 在线优先？离线转 APNs？通道切换？ |

### 2.3 第三梯队（架构深度）

| 优先级 | 选题 | 核心文件 | 深度问题 |
|--------|------|---------|---------|
| 🟢 P2 | **消息撤回** | MessageSyncService | 分布式下 120 秒窗口怎么精确控制？ |
| 🟢 P2 | **多端同步** | MessageSyncService, UserSessionUtils | 增量同步 sequence 怎么设计？ |
| 🟢 P2 | **服务端容灾** | 全局 | Redis/MQ/DB 挂了怎么办？ |

---

## 三、执行方法

### 3.1 每轮执行节奏（3 天）

```
Day 1: 读主流程
  - 读核心方法的完整代码
  - 画出数据流转图
  - 标出每个环节的"如果...怎么办"

Day 2: 深挖异常/性能/容灾
  - 针对"如果...怎么办"看代码怎么处理的
  - 没处理 → 这就是你的改进点
  - 处理了 → 理解为什么这么处理

Day 3: 输出 + 改进
  - 写一篇自己的理解文档（50 行就够了）
  - 或者直接改代码（只改你读透的那个点）
```

### 3.2 读代码清单

读每个方法时，逐条问自己：

```
□ 这个方法的输入是什么？输出是什么？
□ 正常流程走哪条路径？
□ 异常流程走哪条路径？
□ 如果这一步失败了，用户看到什么？
□ 如果这一步失败了，数据会不一致吗？
□ 如果这一步高并发，哪个点是瓶颈？
□ 如果这一步流量放大 10 倍，怎么调整？
□ 为什么这么设计？有其他方案吗？
```

### 3.3 输出模板

每轮深挖后，用这个模板输出：

```markdown
# 场景：XXX

## 数据流
```
客户端 → 网关 → MQ → 业务服务 → 存储 → 推送
```

## 关键路径
1. 正常路径：...
2. 异常路径：...
3. 边界条件：...

## 如果...怎么办
| 场景 | 当前处理 | 改进建议 |
|------|---------|---------|
| 网络超时 | ... | ... |
| 服务挂了 | ... | ... |
| 数据超限 | ... | ... |

## 设计决策
- 为什么选 A 不选 B：...
- 当前方案的代价：...
- 什么条件下会换方案：...
```

---

## 四、推荐选题路线图

```
Week 1:  消息可靠投递
          读：P2PMessageService.process() → MessageStoreService → MessageProducer
          画：消息从发送到送达的完整链路图
          问：ACK 丢失怎么办？消息重复怎么办？

Week 2:  群聊扩散策略
          读：GroupMessageService.process() → dispatchMessage() → storeGroupOfflineMessage()
          画：100 人 vs 1000 人 vs 10000 人的扩散策略
          问：Vela 当前是写扩散还是读扩散？为什么？

Week 3:  离线消息 ZSet
          读：MessageStoreService.storeOfflineMessage() → evictIfExceeded() → syncOfflineMessage()
          画：ZSet 的 key/score/value 设计
          问：ZSet 容量上限 1000 条够吗？超限后怎么降级？

Week 4:  已读回执
          读：MessageSyncService.readMark() → groupReadMark() → ConversationService.messageMarkRead()
          画：单聊已读 vs 群聊已读的流程差异
          问：1000 人群聊的已读回执会产生多少条记录？
```

---

## 五、提醒

```
1. 不要批量改全项目 → 只改你正在读的那个文件
2. 不要为了改而改 → 读懂了再改，不改也行
3. 用问题驱动，不是代码驱动
4. 每轮输出一份文档，哪怕只有 50 行
5. 消耗的 token 应该花在"理解"上，不是"格式化"上
```

---

## 附录：当前项目关键文件索引

### 消息域（message）

| 文件 | 说明 | 行数 |
|------|------|------|
| P2PMessageService.java | 单聊消息处理（发送/ACK/同步） | ~340 |
| GroupMessageService.java | 群聊消息处理（发送/分发/离线） | ~270 |
| MessageSyncService.java | 消息同步/已读/撤回/离线拉取 | ~390 |
| MessageStoreService.java | 消息存储/离线消息 ZSet | ~400 |
| CheckSendMessageService.java | 发送校验（禁言/好友/群组） | ~180 |
| MessageController.java | 消息 REST 接口 | ~50 |
| ChatOperateReceiver.java | 单聊 MQ 消费者 | ~96 |

### 群组域（group）

| 文件 | 说明 | 行数 |
|------|------|------|
| ImGroupServiceImpl.java | 群组管理实现 | ~511 |
| ImGroupMemberServiceImpl.java | 群成员管理实现 | ~635 |
| GroupMessageService.java | 群聊消息处理 | ~270 |
| GroupChatOperateReceiver.java | 群聊 MQ 消费者 | ~85 |

### 好友域（friendship）

| 文件 | 说明 | 行数 |
|------|------|------|
| ImFriendServiceImpl.java | 好友关系实现 | ~663 |
| ImFriendShipRequestServiceImpl.java | 好友请求实现 | ~206 |
| ImFriendShipGroupServiceImpl.java | 好友分组实现 | ~179 |

### 会话域（conversation）

| 文件 | 说明 | 行数 |
|------|------|------|
| ConversationService.java | 会话管理（已读/删除/更新/同步） | ~199 |

### 用户域（user）

| 文件 | 说明 | 行数 |
|------|------|------|
| ImUserServiceImpl.java | 用户管理实现 | ~240 |
| ImUserController.java | 用户 REST 接口 | ~122 |

### 基础设施

| 文件 | 说明 | 行数 |
|------|------|------|
| IdentityCheck.java | 用户签名校验 | ~137 |
| GateWayInterceptor.java | 网关拦截器 | ~102 |
| MessageProducer.java | MQ 消息生产者 | ~113 |
| RedisSeq.java | Redis 序列号生成器 | ~18 |
| UserSessionUtils.java | 用户 Session 工具 | ~70 |
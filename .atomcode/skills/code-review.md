# 代码审查技能

## 审查检查清单
1. 遵循 DDD 分层架构，职责清晰
2. 包名符合规范
3. 类级注释包含 @description、@author、@version、@date
4. 方法注释包含 @description、@param、@return、@throws
5. 无硬编码常量，使用枚举或常量类
6. 异常处理恰当，使用统一 ResponseVO 返回
7. 事务注解 @Transactional 使用正确
8. 依赖注入使用构造器注入（推荐）或 @Autowired
9. 跨模块引用只允许：interfaces → application → domain ← infrastructure
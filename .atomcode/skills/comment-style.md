# 注释规范技能

## 类级注释模板
```java
/**
 * <p>Title: ${CLASS_NAME}</p>
 * <p>Description: ${类功能描述}</p>
 * <p>项目名称: IM-System</p>
 *
 * @author ${author}
 * @since ${version}
 * @createTime ${date}
 * @updateTime ${date}
 *
 * Copyright © ${year} ${author} All rights reserved
 */
```

## 方法注释模板
```java
/**
 * ${方法描述}
 *
 * @param ${paramName} ${参数说明}
 * @return ${返回值说明}
 * @throws ${异常类型} ${异常说明}
 */
```

## 字段注释模板
```java
/** ${字段说明} */
private ${type} ${fieldName};
```

## 接口注释模板
```java
/**
 * @description: ${接口功能描述}
 * @author: ${author}
 * @version: 1.0
 */
```

## 注意事项
- 方法注释必须描述业务逻辑，而非代码实现细节
- DTO/Req/Resp 类字段必须有注释说明
- 枚举类每个枚举值必须有注释
- 保留现有中文注释，新增注释使用英文或中文保持统一
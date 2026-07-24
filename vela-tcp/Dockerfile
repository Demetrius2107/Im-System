# =============================================================================
# Vela IM — vela-tcp (TCP/WebSocket 网关) Dockerfile
# 多阶段构建：Maven 编译 → JRE 运行
# =============================================================================

# ---- Build stage ----
FROM maven:3.6.3-jdk-8 AS build

WORKDIR /build

# 先复制 POM 文件，利用 Docker 缓存加速依赖下载
COPY pom.xml ./
COPY vela-common/pom.xml vela-common/pom.xml
COPY vela-codec/pom.xml vela-codec/pom.xml
COPY vela-service/pom.xml vela-service/pom.xml
COPY vela-tcp/pom.xml vela-tcp/pom.xml
COPY vela-message-store/pom.xml vela-message-store/pom.xml

# 下载依赖（利用缓存：只要 pom.xml 不变，这步不重跑）
RUN mvn dependency:go-offline -B || true

# 复制全部源码并编译
COPY . .
RUN mvn clean package -DskipTests -B -pl vela-tcp -am

# ---- Run stage ----
FROM openjdk:8-jre-slim

WORKDIR /app

# 从 build stage 复制编译产物
COPY --from=build /build/vela-tcp/target/vela-tcp-*.jar app.jar

# 时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

EXPOSE 9000 19000

ENTRYPOINT ["java", "-jar", "app.jar"]

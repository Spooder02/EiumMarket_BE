# SpringBoot/Dockerfile (Optimized Version)

# --- Build Stage ---
FROM gradle:jdk21 AS builder
WORKDIR /app

# 1. 의존성 정보가 담긴 파일들만 먼저 복사합니다.
# 이 파일들이 변경되지 않는 한, 아래 RUN 명령어는 캐시를 사용하게 됩니다.
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# 2. 의존성을 미리 다운로드하여 별도의 레이어에 캐시합니다. (핵심)
# 이 명령어는 소스 코드가 없어도 의존성만 다운로드할 수 있습니다.
RUN ./gradlew dependencies --info

# 3. 자주 변경되는 소스 코드는 나중에 복사합니다.
# 이 부분만 변경되면, 위 단계들은 캐시를 사용하므로 매우 빠르게 넘어갑니다.
COPY src ./src

# 4. 이제 컴파일만 빠르게 수행합니다.
RUN ./gradlew build -x test

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
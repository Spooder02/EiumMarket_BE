# SpringBoot/Dockerfile (Final & Safest Version)

# --- Build Stage ---
# 가장 범용적인 Gradle 이미지를 사용합니다.
FROM gradle:jdk21 AS builder

# 소스코드 전체를 복사할 명시적인 디렉토리를 만듭니다.
WORKDIR /sourcecode

# COPY . . 대신, 더 명시적으로 모든 것을 복사합니다.
COPY . .

# Gradle 빌드를 실행합니다.
# 만약 build.gradle에 문제가 없다면, JAR 파일은 /sourcecode/build/libs/ 에 생성됩니다.
RUN chmod +x ./gradlew && ./gradlew build -x test

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre

# 실행을 위한 별도의 디렉토리를 만듭니다.
WORKDIR /app

# builder 스테이지의 /sourcecode/build/libs/ 디렉토리에서
# 이름이 무엇이든 상관없이 .jar로 끝나는 첫 번째 파일을 app.jar로 복사합니다.
# 이 방법이 가장 강력하고 확실하게 JAR 파일을 찾아 복사합니다.
COPY --from=builder /sourcecode/build/libs/*.jar app.jar

# 최종적으로 /app/app.jar 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
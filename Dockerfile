# 1단계: Gradle을 사용하여 애플리케이션 빌드
# gradle:8.5-jdk17-jammy 이미지를 build 스테이지로 사용합니다.
FROM gradle:8.5-jdk17-jammy AS build

# 작업 디렉토리를 설정합니다.
WORKDIR /home/gradle/src

# 소스 코드를 복사합니다.
COPY --chown=gradle:gradle . .

# Gradle을 실행하여 Jar 파일을 빌드하되, 테스트는 건너뜁니다 (-x test 추가).
RUN gradle bootJar -x test

# =======================================================

# 2단계: 실제 실행을 위한 작은 이미지 생성
# JRE만 포함된 더 가벼운 이미지를 사용합니다.
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 1단계(build)에서 생성된 JAR 파일을 2단계 이미지로 복사합니다.
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# 컨테이너가 시작될 때 Jar 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
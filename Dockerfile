# 1단계: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.5-jdk17-jammy AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle bootJar

# 2단계: 실제 실행을 위한 작은 이미지 생성
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# 빌드 단계에서 생성된 JAR 파일을 실행 단계로 복사
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
# 1단계: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.5-jdk17-jammy AS build

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . .

RUN gradle bootJar -x test

# =======================================================

# 2단계: 실제 실행을 위한 작은 이미지 생성
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# ★★★ 수정된 부분 ★★★
# build 스테이지의 정확한 파일 경로를 지정합니다.
COPY --from=build /home/gradle/src/src/main/resources/firebase-service-account-key.json /app/firebase-service-account-key.json

ENTRYPOINT ["java", "-jar", "app.jar"]
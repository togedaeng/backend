# 1단계: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.5-jdk17-jammy AS build

WORKDIR /home/gradle/src

# ★★★ 추가된 부분 1: 빌드 인자를 받도록 선언 ★★★
ARG FIREBASE_KEY_JSON

COPY --chown=gradle:gradle . .

# ★★★ 추가된 부분 2: 컨테이너 안에서 직접 파일 생성 ★★★
RUN echo "${FIREBASE_KEY_JSON}" > ./src/main/resources/firebase-service-account.json

RUN gradle bootJar -x test

# =======================================================

# 2단계: 실제 실행을 위한 작은 이미지 생성
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# build 스테이지에서 생성된 파일을 복사
COPY --from=build /home/gradle/src/src/main/resources/firebase-service-account.json /app/firebase-service-account.json

ENTRYPOINT ["java", "-jar", "app.jar"]

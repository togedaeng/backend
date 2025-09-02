# 1단계: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.5-jdk17-jammy AS build

WORKDIR /home/gradle/src

# ★★★ 이 라인 삭제 ★★★
# ARG FIREBASE_KEY_JSON

COPY --chown=gradle:gradle . .

# ★★★ 이 라인 삭제 ★★★
# RUN echo "${FIREBASE_KEY_JSON}" > ./src/main/resources/firebase-service-account.json

RUN gradle bootJar -x test

# =======================================================

# 2단계: 실제 실행을 위한 작은 이미지 생성
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# ★★★ 이 라인도 삭제 ★★★
# COPY --from=build /home/gradle/src/src/main/resources/firebase-service-account.json /app/firebase-service-account.json

ENTRYPOINT ["java", "-jar", "app.jar"]
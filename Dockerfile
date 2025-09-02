# 1단계: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.5-jdk17-jammy AS build

WORKDIR /home/gradle/src

# 소스 코드를 복사합니다.
COPY --chown=gradle:gradle . .

# Gradle을 사용하여 테스트를 제외하고 애플리케이션을 빌드합니다.
RUN gradle bootJar -x test

# =======================================================

# 2단계: 실제 실행을 위한 작은 이미지 생성
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 빌드 단계에서 생성된 jar 파일을 복사합니다.
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# entrypoint.sh 스크립트를 이미지 안으로 복사하고 실행 권한을 부여합니다.
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

# 컨테이너가 시작될 때 entrypoint.sh 스크립트를 실행합니다.
ENTRYPOINT ["./entrypoint.sh"]

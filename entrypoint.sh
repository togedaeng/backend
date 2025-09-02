#!/bin/sh

# [DEBUG] 스크립트 실행 시작
echo "--- [DEBUG] Running entrypoint.sh script ---"

# Render가 생성한 읽기 전용 Secret File을
# 애플리케이션 작업 디렉토리로 '복사(cp)'합니다.
cp /etc/secrets/firebasekey /app/firebase-service-account.json

# [DEBUG] 복사가 잘 되었는지 확인하기 위해 /app 폴더 목록을 출력합니다.
echo "--- [DEBUG] Listing contents of /app directory after copy:"
ls -l /app

# Spring Boot 애플리케이션을 실행합니다.
echo "--- [DEBUG] Starting Spring Boot application... ---"
exec java -jar app.jar


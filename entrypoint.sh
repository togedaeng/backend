#!/bin/sh

# 이 스크립트는 컨테이너가 시작될 때 자동으로 실행됩니다.

# Render의 Secret File 기능으로 생성된 파일을
# 애플리케이션이 읽을 수 있는 위치와 이름으로 옮깁니다.
# 원본 경로: /etc/secrets/firebasekey
# 대상 경로: /app/firebase-service-account.json
mv /etc/secrets/firebasekey /app/firebase-service-account.json

# Spring Boot 애플리케이션을 실행합니다.
exec java -jar app.jar

# 🐕 Togedaeng - 반려견 커스터마이징 플랫폼

AI 기반 반려견 커스터마이징 이미지 생성 서비스의 백엔드 API

## 🎯 프로젝트 소개

사용자가 자신의 반려견을 등록하고 개성에 맞는 AI 커스터마이징 이미지를 생성받을 수 있는 서비스입니다.
- 반려견 정보 등록 및 관리
- AI 기반 커스터마이징 이미지 생성 요청
- 실시간 푸시 알림 및 상태 관리
- 관리자 대시보드를 통한 요청 처리

## 🛠 기술 스택

**Backend**
- Spring Boot 3.2.3, Spring Security, Spring Data JPA
- JWT 기반 인증, OAuth 2.0 (Google, Naver)

**Database & Storage**  
- MySQL 8.0 (TiDB Cloud), AWS S3

**Infrastructure & DevOps**
- Docker, Jenkins CI/CD, Firebase Cloud Messaging

**Documentation**
- Swagger/OpenAPI 3.0

## 🚀 핵심 기능

### 🔐 사용자 인증
- **OAuth 2.0** 소셜 로그인 (Google, Naver)
- **JWT** 기반 토큰 인증 시스템
- **역할 기반 접근 제어** (USER/ADMIN)

### 🐕 반려견 관리
- 반려견 정보 등록 및 프로필 관리
- **이미지 업로드** (AWS S3 연동)
- 반려견-사용자 소유 관계 관리

### 🎨 AI 커스터마이징
- 커스터마이징 요청 생성 및 관리
- **실시간 상태 추적** (대기/진행중/완료/보류)
- **Firebase FCM** 푸시 알림

### 📞 고객 지원
- 문의사항 등록 및 답변 시스템
- 공지사항 관리 (카테고리별 분류)
- 이미지 첨부 지원

## 🏗 아키텍처

**도메인 중심 설계 (DDD)** 적용
```
backend/
├── domain/           # 비즈니스 로직
│   ├── user/        # 사용자 관리
│   ├── dog/         # 반려견 관리  
│   ├── custom/      # 커스터마이징
│   ├── inquiry/     # 문의사항
│   └── notice/      # 공지사항
└── global/          # 공통 설정
    ├── auth/        # JWT 인증
    ├── config/      # 보안/DB/클라우드 설정
    └── exception/   # 전역 예외 처리
```

**주요 설계 특징**
- **계층형 아키텍처**: Controller → Service → Repository
- **엔티티 관계**: User ↔ Dog (N:M), Dog → Custom (1:N)
- **보안**: JWT + OAuth 2.0 + 역할 기반 접근 제어

## 🚀 배포 및 운영

**CI/CD 파이프라인** (Jenkins)
```
GitHub → Jenkins → Docker Build → AWS EC2 배포
```

**컨테이너 기반 배포**
- Docker 컨테이너로 패키징
- AWS EC2에서 운영
- Spring Cloud Config Server 연동

## 📊 주요 API

| 기능 | Method | 엔드포인트 | 권한 |
|------|--------|-----------|------|
| 사용자 정보 조회 | GET | `/user/me` | USER |
| 반려견 등록 | POST | `/api/dog/create` | USER |
| 커스터마이징 요청 | GET | `/api/custom` | ADMIN |
| 문의사항 관리 | POST | `/api/inquiry` | USER |
| OAuth 로그인 | GET | `/auth/google` | 공개 |

**API 문서**: Swagger UI (`/swagger-ui.html`)

## 💡 기술적 성과

### 🔧 백엔드 개발
- **Spring Boot 3.2.3** 기반 RESTful API 설계
- **JWT + OAuth 2.0** 인증 시스템 구현
- **도메인 중심 설계(DDD)** 적용한 모듈형 아키텍처

### ☁️ 클라우드 & DevOps
- **AWS S3** 파일 저장소 연동
- **Firebase FCM** 실시간 푸시 알림 시스템
- **Jenkins CI/CD** 파이프라인 구축
- **Docker** 컨테이너 기반 배포

### 📊 데이터베이스 설계
- **MySQL** 기반 정규화된 DB 설계
- **JPA/Hibernate** ORM 활용
- 효율적인 엔티티 관계 설정 (User ↔ Dog N:M, Dog → Custom 1:N)

---

### 📈 프로젝트 성과
- 사용자 인증부터 AI 이미지 생성까지 **전체 백엔드 시스템** 설계 및 구현
- **확장 가능한 아키텍처**로 향후 기능 추가 용이
- **자동화된 배포 파이프라인**으로 개발 효율성 향상

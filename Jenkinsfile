pipeline {
    agent any // 어떤 Jenkins 에이전트에서든 실행

    // 환경 변수 설정
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') // Docker Hub 인증 정보 ID
        DOCKERHUB_USERNAME = DOCKERHUB_CREDENTIALS_USR
        DOCKERHUB_PASSWORD = DOCKERHUB_CREDENTIALS_PSW
        DOCKER_IMAGE_NAME = "${DOCKERHUB_USERNAME}/my-backend-app" // Docker Hub ID/이미지 이름
        APP_NAME = "my-spring-app" // 컨테이너 이름
    }

    stages {
        // 1. 소스코드 복제
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-token', // GitHub 인증 정보 ID
                    url: 'https://github.com/togedaeng/backend.git' // 본인 GitHub 리포지토리 주소
            }
        }

        // 2. Gradle 빌드
        stage('Build') {
            steps {
                sh "./gradlew clean build -x test" // test 제외하고 빌드
            }
        }

        // 3. Docker 이미지 빌드 및 푸시
        stage('Build & Push Docker Image') {
            steps {
                script {
                    def dockerImage = docker.build(DOCKER_IMAGE_NAME, ".") // Dockerfile이 있는 현재 디렉토리에서 빌드
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        dockerImage.push("latest")
                    }
                }
            }
        }

        // 4. EC2에 배포
        stage('Deploy to EC2') {
            steps {
                // sshagent를 사용하여 Private Key 인증 정보 로드
                sshagent(credentials: ['deploy-ssh-key']) { // EC2 SSH 인증 정보 ID
                    sh '''
                        ssh -o StrictHostKeyChecking=no ec2-user@43.203.225.6 '
                            # 기존 컨테이너 중지 및 삭제
                            if [ $(docker ps -a -q -f name=${APP_NAME}) ]; then
                                docker stop ${APP_NAME}
                                docker rm ${APP_NAME}
                            fi

                            # 기존 이미지 삭제 (선택 사항)
                            if [ $(docker images -q ${DOCKER_IMAGE_NAME}:latest) ]; then
                                docker rmi ${DOCKER_IMAGE_NAME}:latest
                            fi

                            # 최신 이미지 받기
                            docker pull ${DOCKER_IMAGE_NAME}:latest

                            # 새 컨테이너 실행
                            docker run -d -p 8080:8080 --name ${APP_NAME} ${DOCKER_IMAGE_NAME}:latest
                        '
                    '''
                }
            }
        }
    }

    // 파이프라인 종료 후 정리 작업
    post {
        always {
            cleanWs() // 작업 공간(workspace) 정리
        }
    }
}
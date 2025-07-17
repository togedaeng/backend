pipeline {
    agent any

    environment {
        DOCKERHUB_ID      = "gaeun2593"
        DOCKER_IMAGE_NAME = "${DOCKERHUB_ID}/my-backend-app"
        APP_NAME          = "my-spring-app"
    }

    stages {
        // 1. 소스코드 복제
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-token',
                    url: 'https://github.com/togedaeng/backend.git'
            }
        }

        // 2. Gradle 빌드
        stage('Build') {
            steps {
                sh "./gradlew clean build -x test"
            }
        }

        // 3. Docker 이미지 빌드 및 푸시
        stage('Build & Push Docker Image') {
            steps {
                script {
                    // docker.build는 현재 디렉토리의 Dockerfile을 사용해 이미지를 빌드합니다.
                    def dockerImage = docker.build(DOCKER_IMAGE_NAME, ".")

                    // withRegistry 블록이 자동으로 Docker Hub 로그인/로그아웃을 처리합니다.
                    // 'dockerhub-credentials' ID를 가진 인증 정보를 사용합니다.
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        dockerImage.push("latest")
                    }
                }
            }
        }

        // 4. EC2에 배포
        stage('Deploy to EC2') {
            steps {
                sshagent(credentials: ['deploy-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ec2-user@43.203.225.6 '
                            if [ $(docker ps -a -q -f name=${APP_NAME}) ]; then
                                docker stop ${APP_NAME}
                                docker rm ${APP_NAME}
                            fi
                            if [ $(docker images -q ${DOCKER_IMAGE_NAME}:latest) ]; then
                                docker rmi ${DOCKER_IMAGE_NAME}:latest
                            fi
                            docker pull ${DOCKER_IMAGE_NAME}:latest
                            docker run -d -p 8080:8080 --name ${APP_NAME} ${DOCKER_IMAGE_NAME}:latest
                        '
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
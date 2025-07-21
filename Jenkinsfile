pipeline {
    agent any

    environment {
        DOCKERHUB_ID      = "gaeun2593"
        DOCKER_IMAGE_NAME = "${DOCKERHUB_ID}/my-backend-app"
        APP_NAME          = "my-backend-app"
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
                    def dockerImage = docker.build(DOCKER_IMAGE_NAME, ".")

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
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@43.201.51.65 '
                            # 1. 실행 중인 컨테이너의 ID를 정확히 찾습니다.
                            CONTAINER_ID=\$(docker ps -q --filter "name=${APP_NAME}")

                            # 2. 컨테이너 ID가 존재할 경우에만 중지 및 삭제를 실행합니다.
                            if [ -n "\$CONTAINER_ID" ]; then
                                echo "--> Found running container: \$CONTAINER_ID. Stopping and removing it."
                                docker stop \$CONTAINER_ID
                                docker rm \$CONTAINER_ID
                            else
                                echo "--> No running container named '${APP_NAME}' found."
                            fi

                            # 3. 불필요한 이전 이미지를 정리합니다.
                            echo "--> Pruning old docker images."
                            docker image prune -f

                            # 4. 최신 이미지를 받고 새 컨테이너를 실행합니다.
                            echo "--> Pulling latest image and starting new container."
                            docker pull ${DOCKER_IMAGE_NAME}:latest

                            # 포트를 8081로 변경합니다.
                            docker run -d -p 8081:8080 -e "SPRING_CONFIG_IMPORT=configserver:http://3.36.89.89:8888" -e "SPRING_PROFILES_ACTIVE=dev" -e "SPRING_APPLICATION_NAME=backend" --name ${APP_NAME} ${DOCKER_IMAGE_NAME}:latest

                            echo "--> Deployment complete."
                        '
                    """
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
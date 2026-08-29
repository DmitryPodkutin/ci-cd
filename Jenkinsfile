// Пайплайн Jenkins: сборка и деплой на тот же сервер, что и GitHub Actions,
// но с другим CI-провайдером в сообщении и на другом порту.
//
// Предварительные требования на Jenkins-агенте:
//   - JDK 17 (входит в образ jenkins/jenkins:lts-jdk17);
//   - Gradle wrapper (./gradlew) уже в репозитории, системный Gradle не нужен;
//   - ssh/scp (входят в кастомный образ, см. jenkins/Dockerfile);
//   - плагин SSH Agent и credential 'jenkins-deploy-key' с приватным SSH-ключом.

pipeline {
    agent any

    environment {
        APP_PORT = '8082'
        CI_PROVIDER = 'Jenkins CI'
        APP_JAR_NAME = 'app-jenkins.jar'
        // Параметры деплоя (не секреты — ключ хранится отдельно в credential).
        DEPLOY_SERVER = '79.137.203.54'
        DEPLOY_USER = 'deploy'
        DEPLOY_PATH = '/home/deploy/app'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./gradlew && ./gradlew build -x test'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['jenkins-deploy-key']) {
                    sh '''
                        set -e
                        scp -o StrictHostKeyChecking=accept-new build/libs/*-SNAPSHOT.jar "${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_PATH}/${APP_JAR_NAME}"
                        ssh "${DEPLOY_USER}@${DEPLOY_SERVER}" "pkill -f '${DEPLOY_PATH}/app-[j]enkins.jar' || true"
                        ssh "${DEPLOY_USER}@${DEPLOY_SERVER}" "cd '${DEPLOY_PATH}' && SERVER_PORT=${APP_PORT} CI_PROVIDER='${CI_PROVIDER}' nohup java -jar ${APP_JAR_NAME} > ${APP_JAR_NAME}.log 2>&1 </dev/null &"
                    '''
                }
            }
        }
    }
}

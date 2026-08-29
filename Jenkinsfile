// Пайплайн Jenkins: сборка и деплой на тот же сервер, что и GitHub Actions,
// но с другим CI-провайдером в сообщении и на другом порту.
//
// Предварительные требования на Jenkins-агенте:
//   - JDK 17 (выставьте JAVA_HOME или настройте инструмент JDK в Jenkins);
//   - Gradle 8.x в PATH (либо используйте ./gradlew после `gradle wrapper`);
//   - плагин SSH Agent и credential 'jenkins-deploy-key' с приватным SSH-ключом;
//   - переменные окружения DEPLOY_SERVER, DEPLOY_USER, DEPLOY_PATH
//     (через Credentials Binding или глобальные настройки Jenkins).

pipeline {
    agent any

    environment {
        APP_PORT = '8082'
        CI_PROVIDER = 'Jenkins CI'
        APP_JAR_NAME = 'app-jenkins.jar'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'gradle build -x test'
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

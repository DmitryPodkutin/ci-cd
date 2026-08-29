#!/bin/bash
# Скрипт перезапуска инстанса приложения на сервере.
# Вызывается из CI-пайплайнов (GitHub Actions / Jenkins) по SSH.
# Usage: restart.sh <jar-name> <port> <ci-provider>
#
# Пример: restart.sh app-github.jar 8081 "GitHub CI"

set -e

JAR_NAME="$1"
PORT="$2"
CI_PROVIDER="$3"
DEPLOY_PATH="/home/deploy/app"

cd "$DEPLOY_PATH"

# Остановить старый процесс
pkill -f "$JAR_NAME" || true
sleep 2

# Запустить новый инстанс
SERVER_PORT="$PORT" CI_PROVIDER="$CI_PROVIDER" \
    nohup java -jar "$JAR_NAME" > "$JAR_NAME.log" 2>&1 </dev/null &

# Отсоединить фоновый процесс от шелла — shell не будет его ждать,
# и SSH-соединение закроется сразу после запуска.
disown 2>/dev/null || true

# Принудительно выйти с кодом 0
exit 0

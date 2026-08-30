# Кастомный образ Jenkins для CI/CD-стенда.
# Добавляет:
#   - ssh/scp (openssh-client) — для деплоя по SSH
#   - docker CLI — для сборки образов и push в регистри
# Базовый образ уже содержит JDK 17.
# Docker CLI обращается к хостовому демону через смонтированный /var/run/docker.sock.

FROM jenkins/jenkins:lts-jdk17

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends openssh-client docker.io \
    && rm -rf /var/lib/apt/lists/*
USER jenkins

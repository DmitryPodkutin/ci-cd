# Кастомный образ Jenkins для CI/CD-стенда.
# Добавляет:
#   - ssh/scp (openssh-client) — для деплоя по SSH
#   - docker CLI — для сборки образов и push в регистри
# Базовый образ уже содержит JDK 17.
# Docker CLI обращается к хостовому демону через смонтированный /var/run/docker.sock.

# Stage 1: берём только docker CLI из официального образа.
FROM docker:cli AS docker-cli

# Stage 2: образ Jenkins + docker CLI + ssh.
FROM jenkins/jenkins:lts-jdk17

COPY --from=docker-cli /usr/local/bin/docker /usr/local/bin/docker

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends openssh-client \
    && rm -rf /var/lib/apt/lists/*
USER jenkins

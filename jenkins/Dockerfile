# Кастомный образ Jenkins для CI/CD-стенда.
# Добавляет ssh/scp (openssh-client), которые нужны пайплайну для деплоя по SSH.
# Базовый образ уже содержит JDK 17.

FROM jenkins/jenkins:lts-jdk17

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends openssh-client \
    && rm -rf /var/lib/apt/lists/*
USER jenkins

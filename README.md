# ci-cd

Демонстрационное Spring Boot-приложение для изучения CI/CD-процессов:
сборки и деплоя на сторонний сервер через пайплайны **GitHub Actions** и **Jenkins**.

## Цель проекта

Учебный стенд, на котором можно на практике освоить настройку CI/CD-пайплайнов:
сборку приложения, доставку артефакта на удалённый сервер по SSH и запуск
нескольких инстансов, управляемых разными CI-системами. Каждый инстанс сообщает
в ответе `GET /status`, какой именно CI его запустил и на каком порту он работает.

## Что внутри

- `GET /status` — возвращает сообщение с указанием CI-провайдера, даты и времени старта.
- Слой сервиса `StatusService` фиксирует время запуска при создании и подставляет его в шаблон сообщения.
- Шаблон сообщения хранится в `src/main/resources/application.yml` под ключом `app.status.message`;
  дата и время подставляются через плейсхолдеры `%s` и `String.format` (без `replace`).
- Контроллер `StatusController` только делегирует вызов сервису, сам подстановку не делает.

## Конфигурируемые параметры

Оба параметра задаются переменными окружения при запуске инстанса — единообразно:

| Параметр | Переменная окружения | По умолчанию | Назначение |
|---|---|---|---|
| Порт приложения | `SERVER_PORT` | `8080` | позволяет запустить несколько инстансов на разных портах |
| CI-провайдер в сообщении | `CI_PROVIDER` | `локальной сборки` | попадает в ответ `/status`, чтобы отличить инстансы |

В `application.yml`:
```yaml
server:
  port: ${SERVER_PORT:8080}
app:
  ci-provider: ${CI_PROVIDER:локальной сборки}
```

## Запуск нескольких инстансов

На одном сервере одновременно работают два инстанса, управляемых разными CI:

| Инстанс | CI | Порт | Имя JAR | Пайплайн |
|---|---|---|---|---|
| GitHub | GitHub Actions | `8081` | `app-github.jar` | `.github/workflows/deploy.yml` |
| Jenkins | Jenkins | `8082` | `app-jenkins.jar` | `Jenkinsfile` |

Пример ответа `/status`:

- GitHub: `Приложение собрано с помощью GitHub CI, запущено <дата> в <время> и находится в рабочем состоянии`
- Jenkins: `Приложение собрано с помощью Jenkins CI, запущено <дата> в <время> и находится в рабочем состоянии`

## Требования

- JDK 17+
- Gradle 8.x (в GitHub Actions ставится автоматически через `gradle/actions/setup-gradle`)

## Локальная сборка и запуск

```bash
gradle bootRun
```

Чтобы локально увидеть в сообщении конкретного провайдера:

```bash
CI_PROVIDER="GitHub CI" gradle bootRun
```

Чтобы запустить на нестандартном порту:

```bash
SERVER_PORT=8090 gradle bootRun
```

Оба параметра вместе:

```bash
SERVER_PORT=8090 CI_PROVIDER="Jenkins CI" gradle bootRun
```

## Проверка

```bash
curl http://localhost:8080/status
```

(порт по умолчанию `8080`; при запуске с другим `SERVER_PORT` подставьте его).

## CI/CD

### GitHub Actions (`.github/workflows/deploy.yml`)

Срабатывает на push в `main`. Собирает JAR, копирует артефакт на сервер по SCP
и перезапускает инстанс на порту `8081` с `CI_PROVIDER="GitHub CI"`.

Секреты репозитория: `SSH_PRIVATE_KEY`, `DEPLOY_SERVER`, `DEPLOY_USER`, `DEPLOY_PATH`.

### Jenkins (`Jenkinsfile`)

Аналогичный пайплайн: собирает JAR, деплоит и запускает инстанс
на порту `8082` с `CI_PROVIDER="Jenkins CI"`.

Предварительные требования на Jenkins-агенте:

- JDK 17 (через `JAVA_HOME` или инструмент Jenkins);
- Gradle 8.x в `PATH` (либо обёртка `./gradlew` после `gradle wrapper`);
- плагин **SSH Agent** и credential с приватным ключом (`jenkins-deploy-key`);
- переменные окружения `DEPLOY_SERVER`, `DEPLOY_USER`, `DEPLOY_PATH`
  (через Credentials Binding или глобальные настройки).

## Подключение к серверу

Параметры подключения хранятся локально в `.env` (gitignored):
`DEPLOY_SERVER`, `DEPLOY_USER`, `DEPLOY_PATH`, `SSH_PRIVATE_KEY_PATH`.
Приватный SSH-ключ — в `.deploy/deploy_key` (gitignored, права `600`).

```bash
ssh -i .deploy/deploy_key -o StrictHostKeyChecking=accept-new $DEPLOY_USER@$DEPLOY_SERVER
```

`.env` и `.deploy/` никогда не коммитятся.

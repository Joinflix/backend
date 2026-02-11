# build
#jdk: java development kit
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 1. Gradle Wrapper 및 설정 파일 복사 (의존성 캐싱을 위해 먼저 수행)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 2. 실행 권한 부여 및 의존성 다운로드 (소스 코드 변경 시에도 이 단계는 캐시됨)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 3. 소스 코드 복사 및 빌드 (소스 변경 시 이 단계부터 다시 실행됨)
COPY src src
RUN ./gradlew build -x test --no-daemon

# run
#jre: java run evironment
FROM eclipse-temurin:21-jre
WORKDIR /app

# 4. 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 5. 실행 환경 설정
RUN mkdir -p /app/uploads
EXPOSE 8080

# CMD : 명령어 덮어쓰기
# ENTRYPOINT : 무조건 해당 명령어로 실행 (덮어쓰기 X)
ENTRYPOINT ["java", "-jar", "app.jar"]
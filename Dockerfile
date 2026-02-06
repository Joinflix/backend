# build
#jdk: java development kit
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies -x test --no-daemon

COPY src src
RUN ./gradlew build -x test --no-daemon

# run
#jre: java run evironment
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

RUN mkdir -p /app/uploads
EXPOSE 8080
# CMD : 명령어 덮어쓰기
# ENTRYPOINT : 무조건 해당 명령어로 실행 (덮어쓰기 X)
ENTRYPOINT ["java", "-jar", "app.jar"]
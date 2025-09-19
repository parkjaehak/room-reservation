# 빌드 단계
FROM gradle:8.10.2-jdk17-alpine AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

# 실행 단계
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV TZ=UTC
COPY --from=build /home/gradle/project/build/libs/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


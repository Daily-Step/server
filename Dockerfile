# 빌드 이미지로 OpenJDK 17 & Gradle을 지정
FROM gradle:7.6.1-jdk17 AS build

# 소스코드를 복사할 작업 디렉토리를 생성
WORKDIR /app

# 빌드 시 전달받은 환경 변수 정의
# GitHub Actions에서 전달된 빌드 변수들을 ARG로 정의
ARG KAKAO_API_KEY
ARG KAKAO_REDIRECT_URI
ARG JWT_SECRET

# 빌드 시 사용할 환경 변수로 설정
# 아래 ENV는 런타임에서는 사용되지 않으며, 빌드 과정에서만 활용됨
ENV KAKAO_API_KEY=${KAKAO_API_KEY}
ENV KAKAO_REDIRECT_URI=${KAKAO_REDIRECT_URI}
ENV JWT_SECRET=${JWT_SECRET}

# 라이브러리 설치에 필요한 파일만 복사
COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

# 호스트 머신의 소스코드를 작업 디렉토리로 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle clean build --no-daemon

# 런타임 이미지로 OpenJDK 17-jre 이미지 지정
FROM eclipse-temurin:17-jre

# 애플리케이션을 실행할 작업 디렉토리를 생성
WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/challenge.jar

EXPOSE 8080
# bash -c를 사용하여 nohup과 백그라운드 실행을 처리
CMD ["bash", "-c", "nohup java -jar challenge.jar > .output.log 2>&1 &"]

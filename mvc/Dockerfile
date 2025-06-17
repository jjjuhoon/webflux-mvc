# jdk 17사용
FROM openjdk:17
# 컨테이너속 저장 위치
WORKDIR /app
# 빌드된 JAR 파일 경로, 복사할 위치
COPY ./build/libs/helloworld-mvc-0.0.1-SNAPSHOT.jar /app/helloworld-mvc.jar
EXPOSE 8080
# 복사한 JAR 파일을 실행하도록 설정
CMD ["java", "-jar", "helloworld-mvc.jar"]

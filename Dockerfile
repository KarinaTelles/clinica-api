# Etapa 1: compila e empacota o jar
FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -q package -DskipTests

# Etapa 2: só o JRE e o jar, rodando com usuário sem privilégios
FROM eclipse-temurin:25-jre-noble
WORKDIR /app
RUN useradd --system --uid 10001 clinica
COPY --from=build /app/target/clinica-api-*.jar app.jar
USER clinica
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

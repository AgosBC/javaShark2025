# Etapa 1: Build
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=acreditaciones_db \
    DB_USER=postgres \
    DB_PASSWORD=postgres \
    REDIS_HOST=redis \
    REDIS_PORT=6379 \
    SERVER_PORT=8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

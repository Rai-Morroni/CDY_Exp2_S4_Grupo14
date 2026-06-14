# ============================================================
# Dockerfile - Sistema de Inscripción de Cursos
# ============================================================

# Etapa 1: Build con Maven
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de producción liviana
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear directorio de montaje EFS
RUN mkdir -p /app/efs

# Copiar JAR generado
COPY --from=builder /app/target/inscripcion-1.0.0.jar app.jar

# Puerto de la aplicación
EXPOSE 8080

# Variables de entorno configurables
ENV AWS_REGION=us-east-1
ENV EFS_MOUNT_PATH=/app/efs

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

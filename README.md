# Inscripcion Service

Servicio Spring Boot para gestionar inscripciones de cursos, generar resúmenes en PDF y almacenar los archivos en AWS S3 con respaldo temporal en EFS.

## Descripción

Esta aplicación:

- Expone una API REST para crear, actualizar, descargar y eliminar inscripciones.
- Genera un PDF de resumen de inscripción usando iText.
- Almacena el PDF en un bucket de S3 configurado en `application.properties`.
- Usa EFS como almacenamiento temporal durante el proceso de generación y subida.

## Tecnologías

- Java 17
- Spring Boot 3.2
- Maven
- AWS SDK v2 (S3, STS)
- iText 8
- Docker

## Estructura principal

- `src/main/java/com/plataforma/educativa/InscripcionApplication.java` — clase principal de Spring Boot.
- `src/main/java/com/plataforma/educativa/controller/InscripcionController.java` — API REST.
- `src/main/java/com/plataforma/educativa/service/InscripcionService.java` — lógica de negocio.
- `src/main/java/com/plataforma/educativa/service/PdfGeneratorService.java` — generación de PDFs.
- `src/main/java/com/plataforma/educativa/service/S3Service.java` — operación con S3.
- `src/main/java/com/plataforma/educativa/service/EfsService.java` — gestión de almacenamiento temporal en EFS.
- `src/main/java/com/plataforma/educativa/config/AwsConfig.java` — configuración del cliente AWS S3.

## Configuración

Propiedades principales en `src/main/resources/application.properties`:

```properties
spring.application.name=inscripcion-service
server.port=8080
aws.region=us-east-1
aws.s3.bucket=cnative-semana4
efs.mount.path=/app/efs
logging.level.com.transportista=DEBUG
logging.level.software.amazon.awssdk=WARN
```

### Variables de entorno

El contenedor Docker define estas variables de entorno:

- `AWS_REGION` — región AWS, valor predeterminado `us-east-1`.
- `EFS_MOUNT_PATH` — ruta de montaje para EFS, valor predeterminado `/app/efs`.

### Credenciales AWS

El cliente S3 usa el `DefaultCredentialsProvider`, por lo que el servicio espera credenciales válidas en el entorno del contenedor o del host:

- variables de entorno `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN` (si aplica)
- perfil de AWS en `~/.aws/credentials`
- roles de instancia/EC2

## Build y ejecución local

### Con Maven

```bash
mvn clean package
java -jar target/inscripcion-1.0.0.jar
```

### Con Docker

```bash
docker build -t inscripcion-service .
docker run -p 8080:8080 \
  -e AWS_REGION=us-east-1 \
  -e AWS_ACCESS_KEY_ID=<tu_access_key> \
  -e AWS_SECRET_ACCESS_KEY=<tu_secret_key> \
  -e EFS_MOUNT_PATH=/app/efs \
  inscripcion-service
```

> Asegúrate de montar un volumen EFS si necesitas persistencia real entre contenedores.

## Endpoints

Base URL: `http://localhost:8080/api/inscripciones`

### Crear inscripción

`POST /api/inscripciones`

Cuerpo JSON de ejemplo:

```json
{
  "nombreEstudiante": "Juan Pérez",
  "emailEstudiante": "juan.perez@example.com",
  "cursosSeleccionados": ["Cloud Native", "Spring Boot"],
  "costoTotal": 1200.50
}
```

Respuesta esperada:

```json
{
  "mensaje": "Resumen generado y subido a S3 exitosamente",
  "resumenId": "ABC12345",
  "rutaS3": "ABC12345/resumen_ABC12345.pdf"
}
```

### Descargar resumen

`GET /api/inscripciones/{id}/descargar`

Devuelve el PDF del resumen como `application/pdf`.

### Actualizar inscripción

`PUT /api/inscripciones/{id}`

Cuerpo JSON de ejemplo:

```json
{
  "cursosSeleccionados": ["Cloud Native", "Arquitectura de Microservicios"],
  "costoTotal": 1350.75
}
```

### Eliminar inscripción

`DELETE /api/inscripciones/{id}`

Elimina el resumen de S3 y del repositorio en memoria.

## Notas importantes

- El almacenamiento de inscripciones en esta versión es en memoria (`ConcurrentHashMap`), por lo que los datos se pierden al reiniciar la aplicación.
- El PDF se genera en memoria y se sube a S3; EFS se usa solo como paso temporal.
- El identificador de resumen es un UUID corto generado en `InscripcionService`.

## Mejoras sugeridas

- Persistir inscripciones en una base de datos relacional o NoSQL.
- Validar datos de entrada con anotaciones de Spring (`@Valid`).
- Añadir pruebas unitarias e integración.
- Manejar mejor errores de AWS y reintentos.

## Autor

Proyecto de la Semana 4 de `Desarrollo Cloud Native`.

# Guía de Inicio Rápido

## Inicio Rápido con Docker (3 pasos)

### 1. Clonar/Descargar el proyecto
``` git clone https://github.com/AgosBC/javaShark2025.git ```

### 2. Iniciar con Docker Compose
```powershell
docker-compose up -d
```

Esto iniciará:
- PostgreSQL (puerto 5432)
- Redis (puerto 6379)
- API (puerto 8080)

### 3. Verificar que funciona
```powershell

# Probar endpoint
Invoke-RestMethod -Uri "http://localhost:8080/api/puntos-venta" -Method Get
```

## Acceder a la Documentación

Abrir navegador en: http://localhost:8080/swagger-ui.html

## Detener la aplicación

```powershell
docker-compose down
```

## Compilación Local (sin Docker)

### Requisitos
- Java 21 JDK
- Maven 3.9+
- PostgreSQL 16 ejecutándose en localhost:5432
- Redis ejecutándose en localhost:6379

### Compilar
```powershell
mvn clean package
```

### Ejecutar
```powershell
java -jar target/puntos-venta-api-1.0.0.jar
```

## Ejecutar Tests

```powershell
mvn test
```

## Ver Reporte de Cobertura

```powershell
mvn clean test jacoco:report
start target/site/jacoco/index.html
```

## Endpoints Principales

| Recurso | Método | URL |
|---------|--------|-----|
| Puntos de Venta | GET | http://localhost:8080/api/puntos-venta |
| Camino Mínimo | GET | http://localhost:8080/api/charges/camino-minimo?origen=1&destino=5 |
| Acreditaciones | POST | http://localhost:8080/api/acreditaciones |
| Swagger UI | GET | http://localhost:8080/swagger-ui.html |


# Ver logs
docker-compose logs -f app

# Puntos de Venta API

API REST desarrollada en Java 21 con Spring Boot para la gestión de puntos de venta, costos entre puntos y acreditaciones.

## 📋 Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Características](#características)
- [Arquitectura](#arquitectura)
- [Compilación](#compilación)
- [Ejecución](#ejecución)
- [Testing](#testing)
- [Endpoints](#endpoints)
- [Patrones de Diseño](#patrones-de-diseño)
- [Features de Java Moderno](#features-de-java-moderno)
- [Diagramas](#diagramas)
- [Supuestos](#supuestos)

## 🚀 Tecnologías

- **Java 21** - LTS version con features modernas
- **Spring Boot 3.2.0** - Framework principal
- **Maven 3.9+** - Gestión de dependencias
- **PostgreSQL 16** - Base de datos relacional
- **Redis 7** - Caché distribuida
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **JaCoCo** - Cobertura de código
- **Docker** - Containerización
- **Swagger/OpenAPI 3** - Documentación de API

## ✨ Características

### 1. Caché de Puntos de Venta con Redis
- **Caché distribuido** usando Redis como backend
- Spring Cache con anotaciones `@Cacheable`, `@CachePut`, `@CacheEvict`
- Datos iniciales **precargados en Redis** al inicio
- TTL de 24 horas para entradas

### 2. Caché de Costos (Grafo) en Redis
- Implementación de grafo no dirigido ponderado **almacenado en Redis**
- Algoritmo de Dijkstra para camino mínimo 
- Thread-safety garantizada por Redis (operaciones atómicas)
- Consultas de adyacencias desde Redis
- **Precarga automática** al iniciar la aplicación

### 3. Acreditaciones
- Persistencia en PostgreSQL
- Enriquecimiento automático con fecha y nombre del punto de venta
- Consultas por punto de venta

## 🏗️ Arquitectura

### Capas de la Aplicación

**Controller Layer**: Expone endpoints REST, validación de entrada
**Service Layer**: Lógica de negocio, orquestación
**Repository Layer**: Acceso a datos (JPA)
**Cache Layer**: Almacenamiento en memoria thread-safe

## 🔨 Compilación

### Requisitos del Host

- **Java 21** (JDK)
- **Maven 3.9+**
- **Docker** y **Docker Compose** (opcional, para ejecución containerizada)

### Compilar el Proyecto

#### Opción 1: Maven Local

```powershell
# Compilar sin tests
mvn clean package -DskipTests

# Compilar con tests
mvn clean package

# Solo tests
mvn test

# Generar reporte de cobertura
mvn clean test jacoco:report
```

El archivo JAR se generará en: `target/puntos-venta-api-1.0.0.jar`

El reporte de cobertura estará en: `target/site/jacoco/index.html`

#### Opción 2: Docker Build

```powershell
# Construir imagen Docker
docker build -t puntos-venta-api:1.0.0 .
```

## ▶️ Ejecución

### Opción 1: Ejecución Local

**Requisitos**: PostgreSQL y Redis ejecutándose localmente

```powershell
# Configurar variables de entorno
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="acreditaciones_db"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgres"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"

# Ejecutar la aplicación
java -jar target/puntos-venta-api-1.0.0.jar
```

La aplicación estará disponible en: `http://localhost:8080`

### Opción 2: Docker Compose (RECOMENDADO)

```powershell
# Iniciar todos los servicios (PostgreSQL, Redis, API)
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

### Opción 3: Solo Base de Datos en Docker

```powershell
# Iniciar solo PostgreSQL y Redis
docker-compose up -d postgres redis

# Ejecutar la aplicación localmente
mvn spring-boot:run
```

## 🧪 Testing

### Ejecutar Tests Unitarios

```powershell
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte de cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura (abrir en navegador)
start target/site/jacoco/index.html
```

### Cobertura de Código

```powershell
# Verificar cobertura mínima
mvn verify
```

## 📡 Endpoints

### Swagger UI

Acceder a la documentación interactiva:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

### Puntos de Venta

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/puntos-venta` | Recuperar todos |
| GET | `/api/puntos-venta/{id}` | Recuperar por ID |
| POST | `/api/puntos-venta` | Crear nuevo |
| PUT | `/api/puntos-venta/{id}` | Actualizar |
| DELETE | `/api/puntos-venta/{id}` | Eliminar |

### Costos

| Método | Endpoint                                             | Descripción           |
|--------|------------------------------------------------------|-----------------------|
| POST | `/api/costos`                                        | Cargar nuevo costo    |
| DELETE | `/api/costos?idA={id}&idB={id}`                      | Remover costo         |
| GET | `/api/costos/adyacentes/{id}`                        | Consultar adyacencias |
| GET | `/api/costos/camino-minimo?origen={id}&destino={id}` | Camino mínimo         |

### Acreditaciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/acreditaciones` | Procesar acreditación |
| GET | `/api/acreditaciones` | Obtener todas |
| GET | `/api/acreditaciones/punto-venta/{id}` | Por punto de venta |

## 🆕 Algunas Features de Java

### Records
- **Ubicación**: `PuntoVenta`, `Costo`, todos los DTOs
- **Beneficio**: Clases inmutables concisas, equals/hashCode/toString automáticos

### Text Blocks
- **Ubicación**: `OpenApiConfig` descripción
- **Beneficio**: Strings multilínea legibles

### instanceof 
- **Beneficio**: Casting automático después de instanceof
- **Potencial uso**: Validaciones de tipos

### Testing con Swagger UI

1. Acceder a `http://localhost:8080/swagger-ui.html`
2. Explorar los endpoints disponibles
3. Probar directamente desde la interfaz web

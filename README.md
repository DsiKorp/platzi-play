# Platzi Play API

Backend REST para gestión de películas construido con **Spring Boot 4**, **Java
21**, **PostgreSQL**, **JPA**, **MapStruct**, **Swagger/OpenAPI** y una capa de
recomendaciones con **LangChain4j + OpenAI**.

## Tabla de contenido

- [Descripción general](#descripción-general)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Funcionalidades](#funcionalidades)
- [Servicios y capas](#servicios-y-capas)
- [MapStruct](#mapstruct)
- [API y endpoints](#api-y-endpoints)
- [Swagger / OpenAPI](#swagger--openapi)
- [Librerías principales](#librerías-principales)
- [Configuración por perfiles](#configuración-por-perfiles)
- [Variables de entorno](#variables-de-entorno)
- [Ejecución local](#ejecución-local)
- [Docker (PostgreSQL)](#docker-postgresql)
- [Datos iniciales](#datos-iniciales)
- [Manejo de errores](#manejo-de-errores)
- [Pruebas](#pruebas)

## Descripción general

Esta API expone operaciones CRUD de películas y un endpoint de sugerencias
basado en IA.\
La aplicación levanta con perfil `dev` por defecto y publica la API bajo el
contexto:

`/platzi-play/api`

En desarrollo, el puerto configurado es `8090`.

---

## Arquitectura del proyecto

Se sigue una estructura por capas:

- `web`: controladores REST y manejo global de excepciones.
- `domain`: DTOs, servicios de negocio, contratos (repositorio), enums y
  excepciones de dominio.
- `persistence`: entidades JPA, repositorio de infraestructura y mappers.

Estructura principal:

```text
src/main/java/com/platzi/play
├── web
│   ├── controller
│   └── exception
├── domain
│   ├── dto
│   ├── services
│   ├── repository
│   └── exception
└── persistence
		├── entity
		├── crud
		└── mapper
```

---

## Funcionalidades

- Consultar todas las películas.
- Consultar una película por ID.
- Crear película.
- Actualizar película (con validaciones).
- Eliminar película.
- Generar sugerencias de películas con IA (`/movies/suggest`).
- Generar saludo de bienvenida con IA (`/hello`).

---

## Servicios y capas

### Controladores (`web/controller`)

- `MovieController`
  - Endpoints CRUD de películas.
  - Endpoint de sugerencias con IA.
- `HelloController`
  - Endpoint `/hello` para saludo generado por IA usando
    `spring.application.name`.

### Servicios de dominio (`domain/services`)

- `MovieService`
  - Orquesta operaciones de negocio para películas.
  - Expone `getAll()` como herramienta (`@Tool`) para integración con agentes
    LangChain4j.
- `PlatziPlayAiService`
  - Interfaz anotada con `@AiService`.
  - Define prompts de sistema y usuario para:
    - saludo corto de bienvenida
    - sugerencia de hasta 3 películas

### Persistencia (`persistence`)

- `MovieEntityRepository`
  - Implementa el contrato `MovieRepository` del dominio.
  - Integra `CrudMovieEntity` (Spring Data) + `MovieMapper` (MapStruct).
  - Lanza excepciones de dominio (`MovieNotFoundException`,
    `MovieAlreadyExistsException`).
- `MovieEntity`
  - Entidad JPA mapeada a tabla `platzi_play_peliculas`.
- `CrudMovieEntity`
  - `CrudRepository<MovieEntity, Long>` con búsqueda por título.

---

## MapStruct

El proyecto usa MapStruct para desacoplar DTOs de entidades y evitar mapeo
manual repetitivo.

### Mapper principal

- `MovieMapper`
  - Convierte `MovieEntity` ↔ `MovieDto`.
  - Soporta actualización parcial desde `UpdateMovieDto` hacia una entidad
    existente (`@MappingTarget`).
  - Se registra como bean Spring con `componentModel = "spring"`.

### Mappers auxiliares

- `GenreMapper`
  - Traduce valores de género entre string persistido y enum de dominio.
- `StateMapper`
  - Traduce estado persistido (`D/N`) a boolean y viceversa.

Beneficios en este proyecto:

- Menos código boilerplate.
- Mapeos explícitos y mantenibles.
- Actualizaciones controladas sobre entidad existente.

---

## API y endpoints

Base URL en desarrollo:

`http://localhost:8090/platzi-play/api`

### Movies

- `GET /movies` → lista películas.
- `GET /movies/{id}` → obtiene película por ID.
- `POST /movies` → crea película.
- `PUT /movies/{id}` → actualiza película (valida título, fecha y rating).
- `DELETE /movies/{id}` → elimina película.
- `POST /movies/suggest` → recibe preferencias y retorna sugerencias por IA.

### Otros

- `GET /hello` → saludo generado por IA.

---

## Swagger / OpenAPI

Dependencia utilizada:

- `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14`

Documentación interactiva:

- `http://localhost:8090/platzi-play/api/swagger-ui/index.html`

OpenAPI JSON:

- `http://localhost:8090/platzi-play/api/v3/api-docs`

> En logs de arranque aparece advertencia para deshabilitar Swagger/OpenAPI en
> producción si no se requiere exposición pública.

---

## Librerías principales

Incluidas en `build.gradle`:

- **Spring Boot Web MVC**
- **Spring Data JPA**
- **PostgreSQL Driver**
- **Spring Validation**
- **Springdoc OpenAPI + Swagger UI**
- **MapStruct + mapstruct-processor**
- **LangChain4j Spring Boot Starter + OpenAI Starter**
- **Spring Boot DevTools**
- **Spring Boot Docker Compose**
- **spring-dotenv**

Versión de Java configurada: **21**.

---

## Configuración por perfiles

### `application.properties`

- Perfil activo por defecto: `dev`.
- Context path: `/platzi-play/api`.
- Configuración base de datasource y LangChain4j.

### `application-dev.properties`

- Puerto: `8090`.
- Conexión a PostgreSQL por variables de entorno.
- `spring.jpa.hibernate.ddl-auto=update`.
- Carga de `data.sql` habilitada.
- Logging detallado para llamadas de IA.

### `application-prod.properties`

- Puerto: `8080`.

---

## Variables de entorno

Variables esperadas para entorno `dev`:

```bash
DB_HOST=localhost
DB_PORT=5432
POSTGRES_DATABASE=platzi_play_db
POSTGRES_USER_DEV=juan
POSTGRES_PASSWORD_DEV=juan.platzi
SPRING_DATASOURCE_DEV_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${POSTGRES_DATABASE}
```

Para Docker Compose también se usan variables `POSTGRES_DATABASE`,
`POSTGRES_PASSWORD_DEV` y `POSTGRES_USER`.

> Nota: revisa que el valor de usuario en Docker Compose (`POSTGRES_USER`)
> coincida con el usuario que utiliza la app (`POSTGRES_USER_DEV`) para evitar
> errores de conexión.

---

## Ejecución local

### 1) Levantar PostgreSQL

```bash
docker compose up -d
```

### 2) Ejecutar la aplicación

```bash
./gradlew bootRun
```

En Windows (PowerShell):

```powershell
.\gradlew.bat bootRun
```

### 3) Probar API

- Swagger UI: `http://localhost:8090/platzi-play/api/swagger-ui/index.html`

---

## Docker (PostgreSQL)

El archivo `docker-compose.yaml` define un servicio:

- `postgres` con imagen `postgres:latest`.
- Puerto expuesto `5432:5432`.
- Volumen persistente `pgdata`.
- Reinicio `unless-stopped`.

Comandos útiles:

```bash
docker compose up -d
docker compose ps
docker compose logs -f postgres
docker compose down
```

---

## Datos iniciales

El archivo `src/main/resources/data.sql` inserta películas de ejemplo con
`ON CONFLICT (titulo) DO NOTHING`, lo que permite relanzar la app sin duplicados
por título.

---

## Manejo de errores

`RestExceptionHandler` centraliza respuestas de error para:

- Película ya existente.
- Película no encontrada.
- Errores de validación (`MethodArgumentNotValidException`).
- Errores no controlados.

La respuesta usa un objeto `Error` (o lista de errores para validación) con
código y mensaje.

---

## Pruebas

Ejecutar tests:

```bash
./gradlew test
```

En Windows:

```powershell
.\gradlew.bat test
```

---

## Recomendaciones

- Deshabilitar Swagger/OpenAPI en producción si no es necesario.
- Mantener credenciales fuera del repositorio (`.env` / variables del entorno).
- Considerar migraciones versionadas (Flyway/Liquibase) para entornos
  productivos.

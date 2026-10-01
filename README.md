# productos-api

API REST para la gestión de productos y categorías, con autenticación y autorización basada en JWT. Proyecto desarrollado como ejercicio de aprendizaje de Spring Boot, cubriendo desde los fundamentos del framework hasta seguridad, documentación automática y contenerización.

## Stack técnico

- **Java 21** (Eclipse Temurin)
- **Spring Boot 4.1.1**
  - Spring Web (MVC)
  - Spring Data JPA (Hibernate)
  - Spring Security
  - Bean Validation
- **Maven** (con Maven Wrapper `mvnw`)
- **SQL Server** (producción) / **H2** (tests)
- **JWT** (io.jsonwebtoken / jjwt 0.12.6)
- **springdoc-openapi** (Swagger UI)
- **Docker** (build multi-stage)
- **JUnit 5 + MockMvc** (tests)

## Arquitectura

El proyecto sigue una separación en capas:

```
Controller  →  Service  →  Repository  →  Base de datos
   (web)        (service)    (repository)
```

- **`web`**: controladores REST, delgados, solo delegan en la capa de servicio.
- **`service`**: lógica de negocio, transacciones (`@Transactional`), conversión entre entidades y DTOs.
- **`repository`**: interfaces `JpaRepository` con métodos derivados y consultas `@Query` (JPQL).
- **`model`**: entidades JPA (`Producto`, `Categoria`, `Usuario`, enum `Rol`).
- **`dto`**: DTOs de request/response por entidad, para no exponer las entidades directamente ni permitir que el cliente envíe campos sensibles (como `id` o `rol`).
- **`config`**: configuración de seguridad (`SecurityConfig`), JWT (`JwtService`, `JwtAuthFilter`), `UserDetailsService` y documentación OpenAPI.
- **`exception`**: manejo global de errores con `@RestControllerAdvice`.

## Funcionalidades

- CRUD de **productos**, con relación a **categorías** (`@ManyToOne` / `@OneToMany`).
- Paginación y ordenamiento (`Pageable`) en los listados.
- Búsqueda de productos por nombre (ignorando mayúsculas/minúsculas) y por categoría + precio máximo.
- Validaciones con Bean Validation (`@NotBlank`, `@Positive`) y manejo centralizado de errores (400, 404, 409).
- **Registro de usuarios** y **login** con emisión de token JWT.
- **Autorización por roles** (`USER`, `ADMIN`) — por ejemplo, eliminar productos requiere rol `ADMIN`.
- Contraseñas hasheadas con `BCrypt`.
- Documentación interactiva de la API vía **Swagger UI**, con soporte para autenticación Bearer/JWT.
- Tests unitarios y de integración (`MockMvc`, flujo completo de autenticación JWT).

## Requisitos previos

- JDK 21
- Una instancia de SQL Server accesible (o Docker, si se corre en contenedor)
- Variable de entorno `JWT_SECRET` (string de al menos 32 caracteres) — **no tiene valor por defecto**, la aplicación no arranca sin ella.

## Configuración

En `src/main/resources/application.properties` se definen, entre otras:

```properties
jwt.secret=${JWT_SECRET}
spring.datasource.url=jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=productos_db;encrypt=false
spring.datasource.username=...
spring.datasource.password=...
spring.jpa.hibernate.ddl-auto=update
```

Las credenciales de base de datos y el secreto JWT deben configurarse como variables de entorno o en un `application-local.properties`.

## Cómo ejecutar

### Localmente (Maven)

```bash
export JWT_SECRET="una-clave-secreta-de-al-menos-32-caracteres"
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

### Con Docker

```bash
docker build -t productos-api .

docker run -p 8080:8080 \
  -e JWT_SECRET="una-clave-secreta-de-al-menos-32-caracteres" \
  -e SPRING_DATASOURCE_URL="jdbc:sqlserver://host.docker.internal;instanceName=SQLEXPRESS;databaseName=productos_db;encrypt=false" \
  -e SPRING_DATASOURCE_USERNAME="..." \
  -e SPRING_DATASOURCE_PASSWORD="..." \
  productos-api
```

`host.docker.internal` permite que el contenedor acceda a una instancia de SQL Server corriendo en el host.

### Tests

```bash
./mvnw test
```

Los tests usan un perfil separado (`application-test.properties`) con base de datos H2 en memoria, independiente de la configuración de producción.

## Documentación de la API (Swagger)

Con la aplicación corriendo:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Los endpoints protegidos requieren autenticación. Desde Swagger UI, usar el botón **Authorize** e ingresar el token obtenido en `/login` con el prefijo `Bearer `.

## Endpoints principales

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/usuarios/registro` | Público | Registro de usuario (rol `USER` por defecto) |
| POST | `/login` | Público | Autenticación, retorna token JWT |
| GET | `/productos` | Autenticado | Listado paginado de productos |
| GET | `/productos/{id}` | Autenticado | Obtener producto por id |
| GET | `/productos/buscar?nombre=` | Autenticado | Búsqueda de productos por nombre |
| POST | `/productos` | Autenticado | Crear producto |
| PUT | `/productos/{id}` | Autenticado | Actualizar producto |
| DELETE | `/productos/{id}` | Rol `ADMIN` | Eliminar producto |
| GET | `/categorias` | Autenticado | Listado paginado de categorías |
| POST | `/categorias` | Autenticado | Crear categoría |

## Seguridad

- Autenticación **stateless** basada en JWT (sin sesiones de servidor).
- Filtro `JwtAuthFilter` que valida el token en cada request y construye el contexto de seguridad.
- Separación entre errores de autenticación (401) y de autorización (403) mediante `authenticationEntryPoint` y `accessDeniedHandler`.
- El endpoint de registro no permite al cliente elegir su propio rol: el rol `ADMIN` se asigna manualmente en la base de datos.

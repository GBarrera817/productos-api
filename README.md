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
- **`model`**: entidades JPA (`Producto`, `Categoria`, `Usuario`, `RefreshToken`, enum `Rol`).
- **`dto`**: DTOs de request/response por entidad, para no exponer las entidades directamente ni permitir que el cliente envíe campos sensibles (como `id` o `rol`).
- **`config`**: configuración de seguridad (`SecurityConfig`), JWT (`JwtService`, `JwtAuthFilter`), `UserDetailsService` y documentación OpenAPI.
- **`service`** también contiene `AuthService` (login y refresh) y `RefreshTokenService` (creación y rotación de refresh tokens).
- **`exception`**: manejo global de errores con `@RestControllerAdvice`.

## Funcionalidades

- CRUD de **productos**, con relación a **categorías** (`@ManyToOne` / `@OneToMany`).
- Paginación y ordenamiento (`Pageable`) en los listados.
- Búsqueda de productos por nombre (ignorando mayúsculas/minúsculas) y por categoría + precio máximo.
- Validaciones con Bean Validation (`@NotBlank`, `@Positive`) y manejo centralizado de errores (400, 401, 404, 405, 409), con logging mediante SLF4J.
- **Registro de usuarios** y **login** con emisión de access token (JWT de corta duración) y refresh token.
- **Renovación de sesión** con refresh tokens opacos: rotación en cada uso y detección de reutilización.
- **Autorización por roles** (`USER`, `ADMIN`) — por ejemplo, eliminar productos requiere rol `ADMIN`.
- Contraseñas hasheadas con `BCrypt`.
- Documentación interactiva de la API vía **Swagger UI**, con soporte para autenticación Bearer/JWT.
- Tests unitarios y de integración (`MockMvc`, flujo completo de autenticación JWT).

## Requisitos previos

- JDK 21
- Una instancia de SQL Server accesible (o Docker, si se corre en contenedor)
- Variables de entorno `JWT_SECRET` (string de al menos 32 caracteres), `DB_USERNAME` y `DB_PASSWORD` — **no tienen valor por defecto**, la aplicación no arranca sin ellas.

## Configuración

En `src/main/resources/application.properties` se definen, entre otras:

```properties
jwt.secret=${JWT_SECRET}
spring.datasource.url=jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=productos_db;encrypt=false
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
jwt.expiration-ms=900000
jwt.refresh-expiration-days=7
```

Propiedades opcionales (con valor por defecto):

| Propiedad | Variable de entorno | Por defecto | Descripción |
|---|---|---|---|
| `jwt.expiration-ms` | `JWT_EXPIRATION_MS` | `900000` (15 min) | Duración del access token |
| `jwt.refresh-expiration-days` | `JWT_REFRESH_EXPIRATION_DAYS` | `7` | Duración del refresh token |
| `logging.level.com.example.productos_api` | `LOG_LEVEL` | `INFO` | Nivel de log de la aplicación |

Las credenciales de base de datos y el secreto JWT se leen desde variables de entorno (`DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`) y nunca deben quedar hardcodeadas ni subirse al repositorio. Para desarrollo local también se puede usar un `application-local.properties`, que está excluido por `.gitignore`.

## Cómo ejecutar

### Localmente (Maven)

```bash
export DB_USERNAME="tu_usuario"
export DB_PASSWORD="tu_password"
export JWT_SECRET="una-clave-secreta-de-al-menos-32-caracteres"
./mvnw spring-boot:run
```

En PowerShell (Windows):

```powershell
$env:DB_USERNAME="tu_usuario"
$env:DB_PASSWORD="tu_password"
$env:JWT_SECRET="una-clave-secreta-de-al-menos-32-caracteres"
.\mvnw.cmd spring-boot:run
```

Estas variables valen solo para la sesión de la terminal actual. Para dejarlas guardadas, usar `setx` y reiniciar la terminal (y el IDE).

La API queda disponible en `http://localhost:8080`.

### Con Docker

```bash
docker build -t productos-api .

docker run -p 8080:8080 \
  -e JWT_SECRET="una-clave-secreta-de-al-menos-32-caracteres" \
  -e SPRING_DATASOURCE_URL="jdbc:sqlserver://host.docker.internal;instanceName=SQLEXPRESS;databaseName=productos_db;encrypt=false" \
  -e DB_USERNAME="tu_usuario" \
  -e DB_PASSWORD="tu_password" \
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

Los endpoints protegidos requieren autenticación. Desde Swagger UI, usar el botón **Authorize** e ingresar el access token obtenido en `/login` (campo `token`) con el prefijo `Bearer `.

## Endpoints principales

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/usuarios/registro` | Público | Registro de usuario (rol `USER` por defecto) |
| POST | `/login` | Público | Autenticación; retorna `token` (access token) y `refreshToken` |
| POST | `/auth/refresh` | Público | Recibe `{"refreshToken": "..."}`; retorna un par `token` / `refreshToken` nuevo y revoca el anterior |
| POST | `/auth/logout` | Público | Recibe `{"refreshToken": "..."}`; revoca ese refresh token. Responde 204 siempre (idempotente) |
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

### Refresh tokens

- El **access token** (JWT) dura 15 minutos por defecto. El **refresh token** es un valor aleatorio de 32 bytes (`SecureRandom`), no un JWT.
- En la base de datos solo se guarda el **hash SHA-256** del refresh token, nunca el valor en claro. Un hash rápido es suficiente porque el token tiene alta entropía (a diferencia de una contraseña).
- **Rotación:** cada refresh token sirve una sola vez. `/auth/refresh` lo marca como revocado y entrega un par nuevo.
- **Detección de reutilización:** si se presenta un refresh token ya usado, se asume que fue robado: se revocan todos los refresh tokens del usuario y se responde 401. Esa revocación se confirma aunque se lance la excepción (`noRollbackFor` en `RefreshTokenService.rotar`).
- Un refresh token expirado, desconocido o revocado responde 401 y el usuario debe iniciar sesión de nuevo.
- **Logout:** `/auth/logout` revoca el refresh token recibido y responde 204 incluso si el token no existe o ya estaba revocado (idempotente, y no revela qué tokens existen). No exige access token, para que un usuario con el access token expirado pueda igualmente cerrar sesión. El access token ya emitido sigue siendo válido hasta que expire (15 minutos por defecto), por ser stateless.
- Presentar después del logout un refresh token ya revocado se trata como reutilización (revoca todos los tokens del usuario).
- Los intentos de login con credenciales incorrectas responden 401 con un mensaje genérico, para no revelar si el usuario existe.

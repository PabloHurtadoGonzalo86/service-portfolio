# Service Portfolio - Repo Analyzer API

## Qué es este Proyecto

API REST en Spring Boot que analiza repositorios de GitHub y genera automáticamente:
1. **Descripción profesional** del proyecto (para CV/portfolio)
2. **README.md completo** con IA (badges, instalación, uso, etc.)
3. **Commit automático** del README al repositorio del usuario

El usuario pasa una URL de GitHub → la API lee el repo → Spring AI (GPT-4.1) analiza el código → devuelve documentación generada → opcionalmente hace commit del README.

## Stack Técnico

- **Lenguaje:** Kotlin 2.2.21
- **Framework:** Spring Boot 4.0.2
- **Java:** 24
- **Build:** Gradle con Kotlin DSL (`./gradlew`)
- **AI:** Spring AI 2.0.0-M2 con OpenAI GPT-4.1
- **GitHub API:** hub4j/github-api (lectura de repos, commits)
- **Web:** WebMVC (WebFlux presente como dependencia transitiva de Spring AI para WebClient)
- **Seguridad:** Spring Security + OAuth2 (Client + Resource Server)
- **Auth GitHub:** OAuth App (usuario) + GitHub App (lectura de repos)
- **Validación:** Spring Validation
- **Monitoreo:** Spring Boot Actuator
- **DB:** PostgreSQL + Spring Data JPA + H2 (tests)
- **JSON:** Jackson Kotlin Module
- **Tests:** JUnit 5 + Mockito-Kotlin + Spring Security Test

## Arquitectura

```
controller/
├── AnalysisController    → POST /analyze, GET /analyses, GET /analyses/{id}, POST /readme/commit
└── HomeController        → GET / (health check)
services/
├── GitHubRepoService     → Lectura de repos con hub4j (GitHub App token)
├── AiAnalysisService     → Análisis con Spring AI ChatClient (Structured Output)
└── ReadmeCommitService   → Commit del README al repo del usuario (OAuth token)
client/
└── GitHubAppTokenService → Gestión de tokens de GitHub App (JWT → installation token)
config/
├── AiConfig              → Bean ChatClient de Spring AI
├── GitHubConfig          → Bean GitHub (hub4j) con GitHub App
├── GitHubAppProperties   → @ConfigurationProperties para GitHub App
└── SecurityConfig        → Spring Security + OAuth2 + CORS configurable
dtos/                 → AnalyzeRepoRequest, AnalysisResponse, ReadmeCommitRequest, ReadmeCommitResponse
models/               → RepoAnalysis, RepoContext
entities/             → AnalysisResult (JPA entity)
repositories/         → AnalysisResultRepository (Spring Data JPA)
util/                 → GitHubUrlParser (parsing de URLs de GitHub)
exceptions/           → GlobalExceptionHandler + excepciones custom
```

## Endpoints

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/api/v1/repos/analyze` | Público | Analiza repo y genera README |
| GET | `/api/v1/repos/analyses` | Público | Lista todos los análisis guardados |
| GET | `/api/v1/repos/analyses/{id}` | Público | Obtiene un análisis por ID |
| POST | `/api/v1/repos/readme/commit` | OAuth | Hace commit del README al repo del usuario |
| GET | `/actuator/health` | Público | Health check |

## Flujo de Datos

```
Usuario → POST /analyze { repoUrl }
  → GitHubRepoService: lee metadatos + árbol de archivos + contenido clave
  → AiAnalysisService: construye prompt → ChatClient → Structured Output
  → Devuelve: { projectName, description, readmeContent, metadata }

Usuario → POST /readme/commit { repoUrl, readmeContent } + OAuth token
  → ReadmeCommitService: commit al repo usando token OAuth del usuario
  → Devuelve: { commitSha, commitUrl }
```

## Autenticación (doble)

- **GitHub App** (installation token): para LECTURA de repos. Permisos granulares, tokens de 1 hora.
- **OAuth App** (user token): para ESCRITURA (commits). El commit se atribuye al usuario.

## Convenciones

- Paquete base: `com.example.serviceportfolio`
- Configuraciones en paquete `config/`
- Controllers con `@RestController` + `@RequestMapping("/api/v1")`
- Kotlin idiomático: data classes para DTOs, extension functions
- Tests con JUnit 5 + `@SpringBootTest`, `@WebMvcTest` para controllers
- Nombres de variables/funciones en inglés, comentarios en español
- Validación con `@Valid` + anotaciones de Jakarta Validation
- Manejo de errores global con `@RestControllerAdvice`

## Comandos

```bash
./gradlew build      # Compilar
./gradlew bootRun    # Ejecutar
./gradlew test       # Tests
./gradlew clean      # Limpiar
```

## Variables de Entorno

| Variable | Descripción |
|----------|-------------|
| `OPENAI_API_KEY` | API key de OpenAI |
| `GITHUB_OAUTH_CLIENT_ID` | Client ID de la OAuth App |
| `GITHUB_OAUTH_CLIENT_SECRET` | Client Secret de la OAuth App |
| `GITHUB_APP_CLIENT_ID` | App ID de la GitHub App |
| `GITHUB_APP_INSTALLATION_ID` | Installation ID de la GitHub App |
| `GITHUB_APP_PRIVATE_KEY_PATH` | Ruta a la clave privada RSA de la GitHub App |
| `APP_CORS_ALLOWED_ORIGINS` | Orígenes CORS permitidos (comma-separated). Default: `http://localhost:3000,http://localhost:5173` |

## Estado Actual

Backend API completado con todas las funcionalidades core:
- Endpoint de análisis de repos con IA (Spring AI + OpenAI GPT-4.1)
- Persistencia de análisis en PostgreSQL (Spring Data JPA)
- Commit automático de README al repo del usuario (hub4j + OAuth)
- Autenticación doble: GitHub App (lectura) + OAuth App (escritura)
- CORS configurable por entorno
- 22 tests (unit + controller) pasando
- CI/CD con GitHub Actions, Docker, y manifiestos Kubernetes
- Desplegado en: `serviceportfolioapi.pablohgdev.com`

### Mejoras futuras
- `@EnableAsync` + `@Async` para operaciones AI no-bloqueantes
- Rate limiting
- Caché de análisis

## Notas para Agentes

- **MODO APRENDIZAJE**: El usuario está aprendiendo Spring Boot. NO codificar por él. Explicar con detalle y dar enlaces a docs oficiales.
- No modificar `application.yaml` sin consultar (contiene secrets via env vars)
- El `.gitignore` excluye `application.yaml` local
- Spring AI 2.0.0-M2 es milestone (no GA) - verificar API en docs oficiales
- Netty forzado a 4.2.9.Final por resolución de dependencias
- WebFlux es dependencia transitiva de Spring AI (para WebClient), no conflicto real con WebMVC

## Documentación Oficial de Referencia

- Spring Boot: https://docs.spring.io/spring-boot/reference/
- Spring AI: https://docs.spring.io/spring-ai/reference/
- Spring Security: https://docs.spring.io/spring-security/reference/
- Spring Security OAuth2: https://docs.spring.io/spring-security/reference/servlet/oauth2/
- Kotlin: https://kotlinlang.org/docs/home.html
- hub4j github-api: https://github-api.kohsuke.org/
- GitHub REST API: https://docs.github.com/en/rest

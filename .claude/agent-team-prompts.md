# Prompts para Equipos de Agentes - Service Portfolio

Colección de prompts listos para usar. Incluye agentes individuales de aprendizaje
y equipos coordinados para tareas grandes.

---

## Agentes Individuales (Modo Aprendizaje)

Estos agentes están en `.claude/agents/`. Se activan automáticamente según el contexto
o puedes invocarlos directamente. Están diseñados para que TÚ escribas el código
mientras ellos te guían.

| Agente | Archivo | Cuándo usarlo |
|--------|---------|---------------|
| Mentor Spring Boot | `spring-boot-mentor.md` | Dudas conceptuales, explicaciones detalladas |
| Revisor de Código (mentor) | `code-reviewer-mentor.md` | Después de escribir código, para revisión guiada |
| Arquitecto Backend (mentor) | `backend-architect-mentor.md` | Decisiones de estructura y patrones |
| Especialista Seguridad | `security-specialist.md` | OAuth2, Spring Security, protección |
| API Documenter (mentor) | `api-documenter-mentor.md` | Documentación de API, OpenAPI specs |
| DevOps Engineer (mentor) | `devops-engineer-mentor.md` | CI/CD, Docker, K8s, deployment |
| Frontend Developer (mentor) | `frontend-developer-mentor.md` | Frontend architecture, React, UI |

### Reglas comunes de agentes de APRENDIZAJE (mentores):
- **NO escriben código por ti** (solo muestran ejemplos en sus respuestas)
- **NO alucinan** (verifican en documentación oficial antes de afirmar)
- **Siempre dan enlaces** a documentación oficial de Spring
- **Explican exhaustivamente** cada concepto
- **Español** para explicaciones, **inglés** para términos técnicos

---

## Agentes de Implementación (Templates)

Estos agentes SÍ pueden escribir código. Instalados desde `claude-code-templates`.
Tienen contexto específico del proyecto service-portfolio.

| Agente | Archivo | Cuándo usarlo |
|--------|---------|---------------|
| Database Architect | `database-architect.md` | Flyway migrations, schema design, JPA entities |
| Debugger | `debugger.md` | Bug diagnosis, root cause analysis, stack traces |
| Prompt Engineer | `prompt-engineer.md` | Spring AI prompts, ChatClient optimization |
| Fullstack Developer | `fullstack-developer.md` | End-to-end features, API + frontend |
| AI Engineer | `ai-engineer.md` | Spring AI architecture, model integration |
| Search Specialist | `search-specialist.md` | Web research, docs verification |
| Code Reviewer | `code-reviewer.md` | PR review, code quality, security audit |
| Backend Architect | `backend-architect.md` | API design, service architecture |
| API Documenter | `api-documenter.md` | OpenAPI specs, endpoint docs |
| DevOps Engineer | `devops-engineer.md` | CI/CD pipelines, Docker, K8s manifests |
| Frontend Developer | `frontend-developer.md` | React/Next.js UI, components |

---

## Equipos Coordinados (para tareas grandes)

Estos prompts son para cuando necesites múltiples agentes trabajando juntos.
Úsalos copiando el prompt en Claude Code.

### 1. Investigación de Arquitectura (Solo lectura)

```
Crea un equipo de agentes para investigar cómo debería estructurarse
service-portfolio. Importante: NADIE escribe código, solo investigan y reportan.
- Un arquitecto: analiza la estructura actual, investiga patrones recomendados
  para Spring Boot 4 + Kotlin, y propone estructura de paquetes con pros/contras.
  Debe consultar la documentación oficial de Spring Boot.
- Un especialista en Spring AI: investiga las capacidades de Spring AI 2.0.0-M2,
  qué APIs están disponibles, y cómo se debería integrar en la arquitectura.
  Debe consultar https://docs.spring.io/spring-ai/reference/
- Un especialista en seguridad: investiga cómo configurar OAuth2 Resource Server
  con Spring Boot 4, qué cambió respecto a versiones anteriores, y qué
  configuración mínima se necesita.
  Debe consultar https://docs.spring.io/spring-security/reference/
Que cada uno reporte con enlaces a documentación oficial verificada.
No realicen cambios en código, solo investigación con fuentes.
```

### 2. Revisión Completa del Proyecto

```
Crea un equipo de agentes para revisar el estado actual del proyecto.
REGLA PRINCIPAL: No modifiquen NINGÚN archivo. Solo lean y reporten.
REGLA DE FUENTES: Cada hallazgo debe incluir un enlace a documentación oficial.
- Un revisor de arquitectura: analiza la estructura, identifica problemas
  (ej: Application class como controller), y propone mejoras citando
  las guías oficiales de Spring Boot.
- Un revisor de seguridad: analiza dependencias y configuración de seguridad,
  identifica lo que falta, consultando la documentación oficial de Spring Security.
- Un revisor de Kotlin: verifica si el código sigue las convenciones
  idiomáticas de Kotlin, consultando https://kotlinlang.org/docs/coding-conventions.html
Que cada uno reporte con severidad (crítico/medio/bajo) y SIEMPRE con enlace
a la documentación que respalda el hallazgo.
```

### 3. Investigación de Spring AI

```
Crea un equipo de agentes para investigar qué puede hacer Spring AI 2.0.0-M2.
REGLA: Solo investigación, no escribir código. Verificar todo en docs oficiales.
- Un investigador de ChatClient: explora las APIs de ChatClient, streaming,
  advisors, y structured output. Consulta https://docs.spring.io/spring-ai/reference/
- Un investigador de function calling: investiga cómo Spring AI permite
  que el modelo llame a funciones Java/Kotlin, con ejemplos de la doc oficial.
- Un investigador de testing: investiga cómo testear componentes de Spring AI,
  mocks de ChatClient, y patrones de testing recomendados.
Que hablen entre sí y creen un resumen con enlaces verificados a la doc oficial.
```

### 4. Depuración con Hipótesis Competidoras

```
Los tests fallan al arrancar el contexto de Spring. Genera 3 compañeros
de equipo para investigar diferentes hipótesis:
- Hipótesis 1: conflicto entre WebFlux y WebMVC (ambos están como dependencia).
  Consultar docs oficiales sobre compatibilidad.
- Hipótesis 2: configuración de seguridad OAuth2 requiere propiedades
  adicionales en application.yaml. Verificar en docs de Spring Security.
- Hipótesis 3: Spring AI 2.0.0-M2 tiene incompatibilidades con Spring Boot 4.0.2.
  Verificar en release notes y docs de Spring AI.
REGLA: Cada hipótesis debe verificarse contra documentación oficial.
No asuman nada, demuestren con fuentes.
Haz que hablen entre sí para refutar las teorías de cada uno.
```

---

## Equipos de Implementación SaaS

Estos prompts usan los agentes de IMPLEMENTACIÓN (templates instalados)
que SÍ pueden escribir código. Para la transformación SaaS multi-tenant.

### 5. SaaS Database Design (Fase 1)

```
Crea un equipo para diseñar e implementar la tabla users y migración Flyway V2.
- database-architect: diseña la tabla users (UUID PK, github_id, tokens encriptados,
  plan, usage counters), FKs en analysis_results y portfolios, índices.
  Crea V2__add_users_table.sql y actualiza schema.sql para H2.
- security-specialist (mentor): revisa el diseño de encriptación de tokens en DB
  usando Spring TextEncryptor. NO codifica, solo valida el enfoque.
- backend-architect: implementa la entidad User.kt, UserRepository.kt,
  EncryptionConfig.kt y TokenEncryptor.kt. Valida relaciones FK con entidades existentes.
Los tests existentes (./gradlew test) deben seguir pasando después de los cambios.
```

### 6. OAuth2 Authentication Flow (Fase 2)

```
Crea un equipo para implementar el flujo OAuth2 login → user upsert.
- security-specialist (mentor): investiga CustomOAuth2UserService en Spring Security 7,
  verifica en docs oficiales que DefaultOAuth2UserService existe y cómo extenderlo.
  NO codifica, solo guía.
- debugger: investiga por qué oauth2_authorized_client está siempre vacía,
  analiza el flujo actual de tokens en SecurityConfig.kt.
- backend-architect: implementa CustomOAuth2UserService.kt, CustomOAuth2User.kt,
  OAuth2LoginSuccessHandler.kt, SecurityUtils.kt, y AuthController.kt.
  Actualiza SecurityConfig.kt para usar el nuevo userService.
Verificar: login en /oauth2/authorization/github crea row en tabla users,
token encriptado, GET /api/v1/auth/status retorna authenticated.
```

### 7. SaaS Multi-Tenant Implementation (Fases 3-5)

```
Crea un equipo para implementar ownership, endpoints de usuario, y protección.
- fullstack-developer: implementa UserController.kt (GET /user/me, DELETE /user/account),
  UserProfileResponse.kt, PlanLimitsConfig.kt. Agrega endpoints /analyses/mine
  y /portfolio/mine con filtrado por user_id.
- database-architect: agrega queries por userId en repositories,
  implementa OwnershipVerifier.kt para verificación de propiedad.
- code-reviewer: revisa seguridad de cada endpoint (403 ownership check),
  valida que análisis anónimos (user_id=null) sigan funcionando.
  Verifica que POST endpoints requieran auth y GET permanezcan públicos.
Tests deben pasar. Endpoints protegidos retornan 401 (no redirect) para requests API.
```

### 8. Usage Limits + Token Commit (Fases 6-7)

```
Crea un equipo para implementar límites por plan y commit con token de DB.
- backend-architect: implementa UsageLimitService.kt (FREE: 3 análisis/mes,
  1 portfolio/mes; PRO: ilimitado), UsageResetScheduler.kt (cron 1er día del mes),
  UsageLimitExceededException. Integra en controllers.
- fullstack-developer: refactoriza commitReadme() para usar token de DB
  en vez de @RegisteredOAuth2AuthorizedClient. Elimina JdbcOAuth2AuthorizedClientService.
- code-reviewer: revisa TOCTOU en usage limits, valida que el 4to análisis
  en cuenta FREE retorna 403, y que commit usa token desencriptado de DB.
Tests deben pasar. Verificar end-to-end cada flujo.
```

---

## Notas de Uso

### Atajos de teclado (modo in-process)
- `Shift+Arriba/Abajo` → Seleccionar compañero de equipo
- `Shift+Tab` → Modo delegado (líder solo coordina)
- `Ctrl+T` → Ver/ocultar lista de tareas
- `Enter` en un compañero → Ver su sesión
- `Escape` → Interrumpir turno actual

### Tips
- Empieza con los **agentes individuales** para dudas puntuales
- Usa **equipos coordinados** solo para tareas grandes de investigación
- Siempre verifica los enlaces que te den los agentes
- Si un agente dice algo que no suena bien, pregúntale: "¿Puedes verificar eso en la documentación oficial?"
- Para limpiar un equipo: `Limpia el equipo`

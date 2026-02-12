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
| Revisor de Código | `code-reviewer.md` | Después de escribir código, para revisión |
| Arquitecto Backend | `backend-architect.md` | Decisiones de estructura y patrones |
| Especialista Seguridad | `security-specialist.md` | OAuth2, Spring Security, protección |

### Reglas comunes de todos los agentes:
- **NO escriben código por ti** (solo muestran ejemplos en sus respuestas)
- **NO alucinan** (verifican en documentación oficial antes de afirmar)
- **Siempre dan enlaces** a documentación oficial de Spring
- **Explican exhaustivamente** cada concepto
- **Español** para explicaciones, **inglés** para términos técnicos

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

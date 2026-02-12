package com.example.serviceportfolio.services

import com.example.serviceportfolio.exceptions.AiAnalysisException
import com.example.serviceportfolio.models.DeveloperPortfolio
import com.example.serviceportfolio.models.RepoAnalysis
import com.example.serviceportfolio.models.RepoContext
import com.example.serviceportfolio.models.RepoSummary
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class AiAnalysisService(
    private val chatClient: ChatClient
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun analyze(repoContext: RepoContext): RepoAnalysis {
        logger.info("Iniciando análisis de repositorio: {}", repoContext.name)

        try {
            val userPrompt = buildPrompt(repoContext)

            val analysis = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .entity(RepoAnalysis::class.java)
                ?: throw AiAnalysisException("La IA no devolvió un análisis válido para ${repoContext.name}")

            logger.info("Análisis completado para {}. Tech stack: {}",
                repoContext.name, analysis.techStack.joinToString(", "))

            return analysis

        } catch (e: AiAnalysisException) {
            throw e
        } catch (e: Exception) {
            logger.error("Error al analizar repositorio {}: {}", repoContext.name, e.message, e)
            throw AiAnalysisException(
                "No se pudo completar el análisis del repositorio ${repoContext.name}",
                e
            )
        }
    }


    private fun buildPrompt(context: RepoContext): String = buildString {
        appendLine("Analiza el siguiente repositorio y proporciona una breve " +
                "descripción del proyecto, la pila tecnológica utilizada, " +
                "las características detectadas y el contenido del README en formato Markdown." +
                "Piensa que esto servirá para el frontend de un repositorio de una persona , asi que aparte tienes que seleccionar los " +
                "repositorios de esta persona , que contengan tecnologías diferentes y proporcionar la un alista , con todo esto " +
                "y una descripción , que contenga todo lo técnico que tengan estos repositorios, de tal manera que " +
                "cuando un recruiter lo vea , llame la atención , para potenciar una posible contratación , para una oferta del sector")
        appendLine()

        //Metadata
        appendLine("## Información del Repositorio")
        appendLine("- Nombre: ${context.name}")
        if (context.description != null) {
            appendLine("- Descripción: ${context.description}")
        }
        appendLine()

        // Lenguajes con porcentaje
        if (context.languages.isNotEmpty()) {
            appendLine("## Lenguajes")
            val total = context.languages.values.sum().toDouble()
            context.languages.entries
                .sortedByDescending { it.value }
                .forEach { (lang, bytes) ->
                    val percentage = (bytes / total * 100).toInt()
                    appendLine("- $lang: $percentage%")
                }
            appendLine()
        }

        // Estructura de archivos (limitada a 50)
        appendLine("## Estructura de Archivos")
        if (context.fileTree.isNotEmpty()) {
            context.fileTree.take(50).forEach { path ->
                appendLine("- $path")
            }
            if (context.fileTree.size > 50) {
                appendLine("... y ${context.fileTree.size - 50} archivos más")
            }
        }
        appendLine()

        // README existente
        if (context.readmeContent != null) {
            appendLine("## README Existente")
            appendLine("```")
            appendLine(context.readmeContent.take(2000))
            appendLine("```")
            appendLine()
        }

        // Archivos clave (build files, configs, etc.)
        if (context.keyFiles.isNotEmpty()) {
            appendLine("## Archivos Clave")
            context.keyFiles.forEach { (path, content) ->
                appendLine("### $path")
                appendLine("```")
                appendLine(content.take(1500))
                appendLine("```")
                appendLine()
            }
        }

        // Instrucciones de output
        appendLine("## Tarea")
        appendLine("Genera:")
        appendLine("1. **projectName**: Nombre profesional del proyecto")
        appendLine("2. **shortDescription**: Descripción de 1-2 líneas para portfolio")
        appendLine("3. **techStack**: Lista de tecnologías detectadas")
        appendLine("4. **detectedFeatures**: Funcionalidades clave del código")
        appendLine("5. **readmeMarkdown**: README.md completo con badges, instalación, uso, estructura")
    }

    fun generatePortfolio(username: String, repos: List<RepoSummary>): DeveloperPortfolio {
        logger.info("Generating portfolio for GitHub user: {}", username)

        try {
            val prompt = buildPortfolioPrompt(username, repos)

            val portfolio = chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(DeveloperPortfolio::class.java)
                ?: throw AiAnalysisException("AI did not return a valid portfolio for $username")

            logger.info("Portfolio generated for {}. Selected {} projects, top skills: {}",
                username, portfolio.selectedProjects.size, portfolio.topSkills.take(5).joinToString(", "))

            return portfolio

        } catch (e: AiAnalysisException) {
            throw e
        } catch (e: Exception) {
            logger.error("Error generating portfolio for {}: {}", username, e.message, e)
            throw AiAnalysisException("Failed to generate portfolio for $username", e)
        }
    }

    private fun buildPortfolioPrompt(username: String, repos: List<RepoSummary>): String = buildString {
        appendLine("Eres un experto en analisis de perfiles de desarrollador y creacion de portfolios tecnicos profesionales.")
        appendLine()
        appendLine("Analiza los siguientes repositorios publicos del usuario de GitHub '$username' y genera un portfolio profesional.")
        appendLine("El objetivo es que un recruiter del sector tech vea este portfolio y quede impresionado por las habilidades tecnicas del desarrollador.")
        appendLine()
        appendLine("## Instrucciones")
        appendLine()
        appendLine("1. **developerName**: Nombre profesional del desarrollador (usa el username si no hay mas informacion)")
        appendLine()
        appendLine("2. **professionalSummary**: Escribe 2-3 parrafos detallados que describan al desarrollador como profesional.")
        appendLine("   - Menciona su experiencia con diferentes tecnologias y stacks")
        appendLine("   - Describe los patrones y buenas practicas que se observan en sus proyectos")
        appendLine("   - Destaca que tipo de problemas resuelve y en que dominios trabaja")
        appendLine("   - El tono debe ser profesional, tecnico y atractivo para un recruiter")
        appendLine()
        appendLine("3. **selectedProjects**: Selecciona los 6-10 repositorios mas representativos e interesantes.")
        appendLine("   - Prioriza DIVERSIDAD tecnologica (no elegir 5 proyectos del mismo stack)")
        appendLine("   - Para cada proyecto:")
        appendLine("     - **repoName**: Nombre exacto del repositorio (tal como aparece abajo)")
        appendLine("     - **repoUrl**: URL exacta del repositorio (tal como aparece abajo)")
        appendLine("     - **description**: 2 parrafos detallados explicando que hace el proyecto, que problemas resuelve, que arquitectura usa, y que decisiones tecnicas interesantes tiene")
        appendLine("     - **techStack**: Lista de tecnologias especificas usadas (inferidas del lenguaje y descripcion)")
        appendLine("     - **whyNotable**: Una frase explicando por que este proyecto destaca en el portfolio")
        appendLine("     - **category**: Una de: Backend, Frontend, Full-Stack, DevOps, Data, Mobile, Library, Tool, Other")
        appendLine()
        appendLine("4. **topSkills**: Las 10-15 tecnologias/habilidades principales, ordenadas por relevancia y dominio aparente")
        appendLine()
        appendLine("5. **skillsByCategory**: Agrupa TODAS las tecnologias detectadas en categorias como:")
        appendLine("   Languages, Frameworks, Databases, DevOps, Cloud, Testing, Tools, etc.")
        appendLine()
        appendLine("6. **profileHighlights**: 3-5 puntos clave que un recruiter deberia notar")
        appendLine("   (ej: 'Desarrollador full-stack con proyectos en 5 lenguajes diferentes')")
        appendLine()
        appendLine("## Repositorios del desarrollador")
        appendLine()
        appendLine("Username: $username")
        appendLine("Total repositorios publicos (no fork, no archived): ${repos.size}")
        appendLine()

        repos.forEachIndexed { index, repo ->
            appendLine("### ${index + 1}. ${repo.name}")
            appendLine("- URL: ${repo.url}")
            appendLine("- Lenguaje principal: ${repo.primaryLanguage ?: "No especificado"}")
            appendLine("- Stars: ${repo.stars} | Forks: ${repo.forks}")
            if (repo.description != null) {
                appendLine("- Descripcion: ${repo.description}")
            }
            appendLine()
        }
    }
}
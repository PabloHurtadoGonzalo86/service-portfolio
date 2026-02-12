package com.example.serviceportfolio.services

import com.example.serviceportfolio.exceptions.AiAnalysisException
import com.example.serviceportfolio.models.RepoAnalysis
import com.example.serviceportfolio.models.RepoContext
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

}
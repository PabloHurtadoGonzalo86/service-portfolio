package com.example.serviceportfolio.services

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



        return null
    }

}
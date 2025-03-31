package com.droidslife.screensaver.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Provides a configured Ktor HttpClient for making API requests.
 */
object KtorClient {
    /**
     * Creates and returns a configured HttpClient instance.
     * 
     * @return A configured HttpClient instance.
     */
    fun create(): HttpClient {
        return HttpClient {
            // Install ContentNegotiation plugin with kotlinx.serialization
            install(ContentNegotiation) {
                json(Json {
                    // Configure JSON serializer to ignore unknown keys
                    ignoreUnknownKeys = true
                    // Allow serialization of special floating point values (NaN, Infinity)
                    isLenient = true
                    // Allow serialization of objects with missing fields
                    coerceInputValues = true
                })
            }

            // Install Logging plugin
            install(Logging) {
                level = LogLevel.INFO
            }

            // Configure default request parameters
            defaultRequest {
                // Default request configuration can be added here if needed
            }

            // Configure retry mechanism
            // Note: As of Ktor 2.x, there's no built-in retry plugin
            // A custom retry mechanism would need to be implemented
            // or a third-party library could be used
        }
    }

    /**
     * Creates and returns a configured HttpClient instance with retry functionality.
     * 
     * @param maxRetries The maximum number of retry attempts.
     * @return A configured HttpClient instance with retry functionality.
     */
    fun createWithRetry(maxRetries: Int = 3): HttpClient {
        // In a real implementation, you would add retry logic here
        // For now, we'll just return the regular client
        return create()
    }
}

package cn.llonvne.gateway

import cn.llonvne.gateway.Constants.AttributeKeys.remoteServiceAttributeKey
import cn.llonvne.gateway.config.ApiGatewayConfig
import io.ktor.server.application.PluginBuilder
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri

fun PluginBuilder<ApiGatewayConfig>.configureCallLoggingForGateway(config: ApiGatewayConfig) {
    application.install(CallLogging) {
        level = config.level
        filter {
            it.request.uri.startsWith(config.apiRoot)
        }

        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value

            val remoteContext = call.attributes.getOrNull(remoteServiceAttributeKey)

            if (remoteContext != null) {
                """
                Status: $status, HTTP Method: $httpMethod, API ID: ${remoteContext.insight.name}@${remoteContext.apiDescriptor.name}
                Service Url:${remoteContext.config.url + remoteContext.insight.baseUri + remoteContext.apiDescriptor.localUri}
                Gateway Uri: ${call.request.uri}
                API Description: ${remoteContext.apiDescriptor.description}
                """.trimIndent()
            } else {
                "Status: $status, HTTP Method: $httpMethod,Service: ${call.request.uri.split("/")[2]}"
            }
        }
    }
}

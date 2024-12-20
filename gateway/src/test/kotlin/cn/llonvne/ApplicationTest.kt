package cn.llonvne

import cn.llonvne.gateway.ApiDescriptor
import cn.llonvne.gateway.ApiWebSocketPacket
import cn.llonvne.gateway.HttpMethod
import cn.llonvne.gateway.Ping
import cn.llonvne.gateway.ServiceInsight
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Duration
import kotlin.test.Test
import kotlin.time.toKotlinDuration

@Serializable
class Hello(
    val message: String,
)

class ApplicationTest {
    @Test
    fun testService() {
        embeddedServer(Netty, port = 8081) {
            install(ContentNegotiation) {
                json(
                    Json,
                )
            }

            val client =
                HttpClient(CIO) {
                    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                        json(Json)
                    }
                    install(WebSockets) {
                        contentConverter = KotlinxWebsocketSerializationConverter(Json)
                        maxFrameSize = Long.MAX_VALUE
                    }
                }

            GlobalScope.launch {
                client.webSocket(
                    method = io.ktor.http.HttpMethod.Get,
                    host = "localhost",
                    port = 8080,
                    path = "/websocket/api",
                    request = {
                        headers.append("service_name", "TestService")
                    },
                ) {
                    launch {
                        while (this@webSocket.isActive) {
                            sendSerialized(Ping("TestService") as ApiWebSocketPacket)
                            delay(Duration.ofSeconds(10).toKotlinDuration())
                        }
                    }

                    for (frame in incoming) {
                        println(frame)
                    }
                }
            }

            routing {
                route("/api") {
                    val resp =
                        ServiceInsight(
                            "TestService",
                            "/api",
                            namespace = "test",
                            listOf(
                                ApiDescriptor(
                                    "hello",
                                    "/hello",
                                    description = "Hello",
                                    method = HttpMethod.GET,
                                    contentType = "application/json",
                                ),
                            ),
                        )
                    get {
                        call.respond(resp)
                    }

                    get("/hello") {
                        call.respond(Hello("Hello From TEst"))
                    }
                }
            }
        }.start(wait = true)
    }

    @Test
    fun testRoot() =
        testApplication {
            testService()

            application {
                module()
            }

            val client =
                createClient {
                }
        }
}

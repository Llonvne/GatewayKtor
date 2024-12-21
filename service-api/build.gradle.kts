plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.ktor.plugin")
}

dependencies {
    // LOG
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // PROJECT
    api(project(":gateway-api"))

    // KTOR CLIENT
    api("io.ktor:ktor-client-core")
    api("io.ktor:ktor-client-cio")
    api("io.ktor:ktor-client-content-negotiation")
    api("io.ktor:ktor-serialization-kotlinx-json")
    api("io.ktor:ktor-client-websockets")

    // KTOR SERVER
    api("io.ktor:ktor-server-core-jvm")
    api("io.ktor:ktor-server-content-negotiation-jvm")
    api("io.ktor:ktor-serialization-kotlinx-json-jvm")
    api("io.ktor:ktor-server-netty-jvm")
}

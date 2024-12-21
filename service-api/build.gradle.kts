plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.ktor.plugin")
}

dependencies {
    // LOG
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // PROJECT
    implementation(project(":gateway-api"))

    // KTOR CLIENT
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-client-websockets")
}

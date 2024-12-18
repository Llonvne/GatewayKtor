plugins {
    kotlin("jvm") version "2.1.0" apply false
    id("io.ktor.plugin") version "3.0.2" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
}

repositories {
    mavenCentral()
}

group = "com.example"
version = "0.0.1"

subprojects {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.ktor.plugin")
}

dependencies {

    // PROJECT
    implementation(project(":service-api"))

    val hibernateVersion = "7.0.0.Beta3"
    val agroalVersion = "2.1"
    val log4jVersion = "2.24.1"
    val postgresqlVersion = "42.7.2"

    // Hibernate Core & Extensions
    implementation("org.hibernate.orm:hibernate-core:$hibernateVersion")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")
    implementation("org.hibernate.orm:hibernate-agroal:$hibernateVersion")
    implementation("io.agroal:agroal-pool:$agroalVersion")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")

    // Annotation Processors
    annotationProcessor("org.hibernate.orm:hibernate-processor:$hibernateVersion")
    // 注释掉的处理器如果将来可能用到，可以保留并加以说明
    // annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:$hibernateVersion") // 可能未来需要用于生成元模型信息

    // Database Driver
    implementation("org.postgresql:postgresql:$postgresqlVersion")
}
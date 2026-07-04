plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    kotlin("plugin.serialization") version libs.versions.kotlin
}

group = "nu.westlin.jobexecutor"
version = "0.0.1-SNAPSHOT"
description = "ContextParametersSpringTx"

repositories {
    mavenCentral()
}

dependencies {
    // Registrera Spring Boot BOM på samtliga relevanta konfigurationer
    @Suppress("AvoidDuplicateDependencies")
    implementation(platform(libs.spring.boot.bom))
    @Suppress("AvoidDuplicateDependencies")
    developmentOnly(platform(libs.spring.boot.bom))
    @Suppress("AvoidDuplicateDependencies")
    testImplementation(platform(libs.spring.boot.bom))

    implementation("org.jetbrains.exposed:exposed-spring-boot4-starter:${libs.versions.exposed.get()}")
    implementation("org.jetbrains.exposed:exposed-json:${libs.versions.exposed.get()}")
    implementation("org.jetbrains.exposed:exposed-java-time:${libs.versions.exposed.get()}")
    // 1.11.0 finns
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // Applikationsberoenden
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    developmentOnly(libs.spring.boot.docker.compose) // Ändrad till .compose
    runtimeOnly(libs.postgresql)

    // Testberoenden
    testImplementation(libs.spring.boot.starter.jdbc.test) // Ändrad till .test
    testImplementation(libs.spring.boot.starter.webmvc.test) // Ändrad till .test
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly(libs.junit.platform.launcher)

    testImplementation("com.ninja-squad:springmockk:5.0.1")
}

kotlin {
    jvmToolchain(25)

    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
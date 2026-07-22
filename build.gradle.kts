import dev.detekt.gradle.Detekt
import org.gradle.kotlin.dsl.withType

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    kotlin("plugin.serialization") version libs.versions.kotlin
    id("dev.detekt") version "2.0.0-alpha.5"
}

group = "nu.westlin.jobexecutor"
version = "0.0.1-SNAPSHOT"
description = "ContextParametersSpringTx"

repositories {
    mavenCentral()
    //maven { url = uri("https://jitpack.io") }
}

dependencies {
    //detektPlugins("com.github.pwestlin:detekt-rules:1.0")
    detektPlugins("io.github.pwestlin:detekt-rules:1.0")
    detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:2.0.0-alpha.5")


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

detekt {
    // Gör att din detekt.yml ärver alla standardregler istället för att skriva över dem helt
    buildUponDefaultConfig = true
    autoCorrect = true

    config.setFrom(files("src/main/detekt/detekt.yml"))
}

/*
configurations.named("detektPlugins") {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}*/

// pwestlin
// "check" är kopplat till det gamla tasket "detekt" som inte hittar mina regler.

// 1. Tvinga 'check' (och därmed CI) att köra de typ-säkra analyserna
tasks.named("check") {
    dependsOn("detektMain", "detektTest")
}

// 2. Konfigurera om det korta bekvämlighetskommandot './gradlew detekt'
tasks.named("detekt") {
    // Låt kommandot delegera direkt vidare till de typ-säkra taskerna...
    dependsOn("detektMain", "detektTest")

    // ...men stäng av själva exekveringen av den generiska scannern.
    // Det gör att den bara markeras som SKIPPED och inte gör något dubbelarbete utan typanalys.
    enabled = false
}

tasks.withType<Detekt>().configureEach {
    reports {
        // Aktivera HTML-rapporten
        html.required.set(true)

        // Inaktivera övriga rapportformat i Detekt 2.0
        checkstyle.required.set(false)
        markdown.required.set(false)
        sarif.required.set(false)
    }
}

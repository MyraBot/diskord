@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kommons: String by project
val ktor: String by project
val kotlinx: String by project
val kotlinxSerialization: String by project

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    `maven-publish`
}

val id = "Diskord"
group = "bot.myra"
version = "2.62"

repositories {
    mavenCentral()
    maven("https://systems.myra.bot/releases/") {
        credentials {
            username = property("REPO_NAME")?.toString()
            password = property("REPO_SECRET")?.toString()
        }
    }
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("bot.myra:kommons:$kommons")

    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-okhttp:$ktor")
    implementation("io.ktor:ktor-client-websockets:$ktor")
    implementation("io.ktor:ktor-network:$ktor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerialization") // Serializer

    // Reflections
    compileOnly(kotlin("reflect"))
    compileOnly("org.reflections:reflections:0.10.2")

    // Voice encryption
    implementation("com.codahale:xsalsa20poly1305:0.11.0")

    testImplementation("bot.myra:kommons:$kommons")
    testImplementation(kotlin("reflect"))
    testImplementation("org.reflections:reflections:0.10.2")
    testImplementation("com.github.walkyst:lavaplayer-fork:original-SNAPSHOT")
}

// Publishing
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        publications {
            create<MavenPublication>("repo") {
                group = project.group as String
                version = project.version as String
                artifactId = id
                from(components["java"])
            }
        }
        maven {
            url = uri("https://systems.myra.bot/releases/")
            name = "repo"
            credentials {
                username = property("REPO_NAME")?.toString()
                password = property("REPO_SECRET")?.toString()
            }
            authentication { create<BasicAuthentication>("basic") }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.OptIn"
    )
}

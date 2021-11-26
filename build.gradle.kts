import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlinx_version: String by project
val logging_version: String by project

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.30"
    `maven-publish`
}

group = "com.github.myraBot"
version = "0.70"
val id = "Diskord"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0") // Serializer
    // Reflections
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    compileOnly("org.reflections:reflections:0.9.11")

    compileOnly("ch.qos.logback:logback-classic:$logging_version")

    testImplementation("ch.qos.logback:logback-classic:$logging_version")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    testImplementation("org.reflections:reflections:0.9.11")
}

// Publishing
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        maven {
            name = "jfrog"
            url = uri("https://m5rian.jfrog.io/artifactory/java")
            credentials {
                username = System.getenv("JFROG_USERNAME")
                password = System.getenv("JFROG_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("jfrog") {
            from(components["java"])

            group = project.group as String
            version = project.version as String
            artifactId = id

            artifact(sourcesJar)
        }
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}
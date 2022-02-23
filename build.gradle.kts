import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kommons: String by project
val ktor_version: String by project
val kotlinx_version: String by project
val logging_version: String by project
val kotlinx_serialization_version: String by project

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.30"
    `maven-publish`
}

val id = "Diskord"
group = "com.github.myraBot"
version = "1.23"

repositories {
    mavenCentral()
    maven(url = "https://systems.myra.bot/releases/") {
        credentials {
            username = System.getenv("REPO_NAME")
            password = System.getenv("REPO_SECRET")
        }
    }
}

dependencies {
    compileOnly("bot.myra:kommons:$kommons")

    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version") // Serializer

    // Reflections
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    compileOnly("org.reflections:reflections:0.10.2")

    testImplementation("bot.myra:kommons:$kommons")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    testImplementation("org.reflections:reflections:0.10.2")
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
            url = uri( "https://systems.myra.bot/releases/")
            name = "repo"
            credentials {
                username = System.getenv("REPO_NAME")
                password = System.getenv("REPO_SECRET")
            }
            authentication { create<BasicAuthentication>("basic") }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}
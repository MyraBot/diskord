import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kommons: String by project
val ktor: String by project
val kotlinx: String by project
val kotlinxSerialization: String by project

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

    implementation("io.ktor:ktor-websockets:$ktor")
    implementation("io.ktor:ktor-server-netty:$ktor")
    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerialization") // Serializer

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
            url = uri("https://systems.myra.bot/releases/")
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
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.OptIn"
    )
}
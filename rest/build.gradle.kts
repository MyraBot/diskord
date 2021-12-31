import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val diskordVersion: String by project
val slasherVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "cloud.mallne.dicentra.weaver"
version = project.findProperty("VERSION_NAME") ?: "1.0.0-SNAPSHOT"
description = "The core compponents of DiCentra Weaver."

plugins {
    alias(libs.plugins.kmp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()
    android {
        namespace = project.group.toString()
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_25)
        }
    }
    js {
        nodejs()
        browser()
    }
    wasmJs {
        browser()
        nodejs()
        d8()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    sourceSets {
        val commonMain = getByName("commonMain") {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.http)
                api(project(":tokenizer"))
            }
        }
    }
    jvmToolchain(25)
}


mavenPublishing {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                pom {
                    name = "DiCentra Weaver Core"
                    description =
                        "Essential Interfaces and Methods for DiCentra Weaver"
                    inceptionYear = "2025"
                    developers {
                        developer {
                            name = "Mallne"
                            url = "mallne.cloud"
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = "DiCentraArtefacts"
                url = uri("https://registry.mallne.cloud/repository/DiCentraArtefacts/")
                credentials {
                    username = providers.environmentVariable("NEXUS_USERNAME").getOrElse("")
                    password = providers.environmentVariable("NEXUS_PASSWORD").getOrElse("")
                }
            }
        }
    }


    coordinates(group.toString(), project.name)
}
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)

    id("multiplatform.target.jvm")
    id("multiplatform.target.wasmJs")
    id("multiplatform.target.js")
}

kotlin {
    explicitApi()

    js {
        browser {
            testTask {
                enabled = false
            }
        }
    }

    wasmJs {
        browser {
            testTask {
                enabled = false
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.permissions)
            implementation(projects.util)
            
            implementation(libs.kotlinx.serialization.core)
        }

        commonTest.dependencies {
            implementation(projects.kotestAssertions)
            implementation(libs.kotest.assertions)
            implementation(libs.kotlin.test)

            implementation(libs.kotlinx.serialization.json)
        }
    }
}

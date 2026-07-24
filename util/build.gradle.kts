@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)

    id("multiplatform.target.jvm")
    id("multiplatform.target.wasmJs")
    id("multiplatform.target.js")
}

kotlin {
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
            implementation(libs.kotlinx.serialization.core)
        }
    }
}

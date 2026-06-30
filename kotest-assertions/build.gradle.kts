plugins {
    alias(libs.plugins.kotlin.multiplatform)

    id("multiplatform.target.jvm")
    id("multiplatform.target.wasmJs")
    id("multiplatform.target.js")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotest.assertions)
            implementation(projects.permissions)
        }
    }
}

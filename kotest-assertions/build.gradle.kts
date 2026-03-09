plugins {
    alias(libs.plugins.kotlin.multiplatform)

    id("multiplatform-convention")
}

kotlin {
    configureLibrary(libs.versions.jvm)
    configureTargets()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotest.assertions)
            implementation(projects.permissions)
        }
    }
}

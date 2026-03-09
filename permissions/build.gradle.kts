plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)

    id("multiplatform-convention")
}

kotlin {
    configureLibrary(libs.versions.jvm)
    configureTargets()

    sourceSets {
        commonMain.dependencies {
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

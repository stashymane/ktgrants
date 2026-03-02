plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
    explicitApi()

    jvm()

    js {
        nodejs()
    }
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        nodejs()
    }


    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
        }

        commonTest.dependencies {
            implementation(projects.kotestAssertions)
            implementation(libs.kotest.assertions)
            implementation(libs.kotlin.test)
        }

        all {
            languageSettings.enableLanguageFeature("ContextParameters")
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=check")
    }
}

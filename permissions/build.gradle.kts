plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
    explicitApi()

    jvm()
    @Suppress("OPT_IN_USAGE")
    wasmJs()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
        }
        commonTest.dependencies {
            implementation(libs.kotest.assertions)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=check")
    }
}

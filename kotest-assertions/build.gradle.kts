plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
            implementation(libs.kotest.assertions)
            implementation(projects.permissions)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=check")
    }
}

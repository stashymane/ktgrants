plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())

    jvm()
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        nodejs()
    }

    sourceSets.commonMain {
        dependencies {
            implementation(projects.permissions)
        }
    }
}

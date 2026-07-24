plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        nodejs()
    }

    sourceSets.commonMain {
        dependencies {
            implementation(projects.permissions)
            implementation(projects.authorization)
        }
    }
}

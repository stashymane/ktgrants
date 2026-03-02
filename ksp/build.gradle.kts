plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
    explicitApi()

    sourceSets.all {
        languageSettings.enableLanguageFeature("ContextParameters")
    }

    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=check")
    }
}

dependencies {
    implementation(projects.permissions)
    
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet.core)
    implementation(libs.kotlinpoet.ksp)
}

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.configureShared(toolchainVersion: Provider<String>) {
    jvmToolchain(toolchainVersion.get().toInt())

    compilerOptions {
        optIn.addAll("kotlin.time.ExperimentalTime", "kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.addAll(
            "-Xreturn-value-checker=check",
            "-Xcontext-parameters",
            "-Xcontext-sensitive-resolution"
        )
    }
}

fun KotlinMultiplatformExtension.configureLibrary(toolchainVersion: Provider<String>) {
    configureShared(toolchainVersion)
    explicitApi()
}

fun KotlinMultiplatformExtension.configureTargets() {
    jvm()

    js {
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }
}

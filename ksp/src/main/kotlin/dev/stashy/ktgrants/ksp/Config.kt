package dev.stashy.ktgrants.ksp

public data class Config(
    public val generatedPackage: String
) {
    public constructor(options: Map<String, String>) : this(
        generatedPackage = options["ktgrants.package"] ?: "dev.stashy.ktgrants.generated"
    )
}

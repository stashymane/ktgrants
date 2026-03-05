package dev.stashy.ktgrants.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import dev.stashy.ktgrants.annotations.PermissionObject
import dev.stashy.ktgrants.ksp.data.GrantObjectMetadata
import dev.stashy.ktgrants.ksp.generators.generateGrantAccessors
import dev.stashy.ktgrants.ksp.generators.generatePermissionContext

public class Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val config: Config
) : SymbolProcessor {
    private var grantObjectMetadata: GrantObjectMetadata? = null

    override fun process(resolver: Resolver): List<KSAnnotated> = context(codeGenerator, config) {
        val symbols = resolver.getSymbolsWithAnnotation(PermissionObject::class.qualifiedName!!)
            .filter(KSAnnotated::validate)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        if (symbols.isEmpty())
            return emptyList()

        if (symbols.size > 1)
            throw IllegalStateException("Only one object can be annotated with `@GrantObject`")

        val grantObject = symbols.first()

        // Store metadata for finish stage
        grantObjectMetadata = GrantObjectMetadata(
            objectName = grantObject.simpleName.asString(),
            objectPackage = grantObject.packageName.asString(),
            objectQualifiedName = grantObject.qualifiedName!!.asString()
        )

        generateGrantAccessors(grantObject)

        return emptyList()
    }

    override fun finish(): Unit = context(codeGenerator, config) {
        val metadata = grantObjectMetadata ?: return

        generatePermissionContext(metadata)
    }
}

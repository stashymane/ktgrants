package dev.stashy.ktgrants.ksp.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import dev.stashy.ktgrants.ksp.Config
import dev.stashy.ktgrants.permissions.Grant

/**
 * Generate Accessors.kt that mimics @GrantContainer marked class
 */
context(codeGenerator: CodeGenerator, config: Config)
internal fun generateGrantAccessors(grantObject: KSClassDeclaration) {
    val grantObjectName = grantObject.simpleName.asString()
    val accessorName = "${grantObjectName}Accessor"
    val grantObjectPackage = grantObject.packageName.asString()

    // Build the accessor interface
    val accessorInterface = TypeSpec.interfaceBuilder(accessorName)
        .addModifiers(KModifier.SEALED)
        .apply {
            addAccessorMembers(this, grantObject, grantObjectPackage)
        }
        .build()

    val fileSpec = FileSpec.builder(config.generatedPackage, "Accessors")
        .addType(accessorInterface)
        .addImport(grantObjectPackage, grantObjectName)
        .build()

    fileSpec.writeTo(codeGenerator, Dependencies(true, grantObject.containingFile!!))
}

private fun addAccessorMembers(
    typeBuilder: TypeSpec.Builder,
    declaration: KSClassDeclaration,
    grantObjectPackage: String,
    pathPrefix: String = ""
) {
    val grantObjectName = declaration.simpleName.asString()
    val fullPath = if (pathPrefix.isEmpty()) grantObjectName else "$pathPrefix.$grantObjectName"

    declaration.declarations
        .filterIsInstance<KSPropertyDeclaration>()
        .forEach { property ->
            val propertyName = property.simpleName.asString()
            val propertyType = property.type.resolve().declaration

            when {
                propertyType.qualifiedName?.asString() == Grant::class.qualifiedName!! -> {
                    typeBuilder.addProperty(
                        PropertySpec.builder(
                            propertyName,
                            Grant::class.asClassName()
                        )
                            .getter(
                                FunSpec.getterBuilder()
                                    .addCode("return %L.$propertyName", fullPath)
                                    .build()
                            )
                            .build()
                    )
                }
            }
        }

    // Process nested objects
    declaration.declarations
        .filterIsInstance<KSClassDeclaration>()
        .forEach { nestedClass ->
            val nestedName = nestedClass.simpleName.asString()
            val nestedAccessorName = "${nestedName}Accessor"

            // Create nested accessor object
            val nestedAccessor = TypeSpec.objectBuilder(nestedAccessorName)
                .apply {
                    addAccessorMembers(this, nestedClass, grantObjectPackage, fullPath)
                }
                .build()

            // Add the nested accessor to the parent
            typeBuilder.addType(nestedAccessor)

            // Add property to access the nested accessor
            typeBuilder.addProperty(
                PropertySpec.builder(
                    nestedName,
                    ClassName("", nestedAccessorName)
                )
                    .getter(
                        FunSpec.getterBuilder()
                            .addCode("return $nestedAccessorName")
                            .build()
                    )
                    .build()
            )
        }
}

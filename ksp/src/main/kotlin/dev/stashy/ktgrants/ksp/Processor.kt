package dev.stashy.ktgrants.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import dev.stashy.ktgrants.annotations.GrantContainer

public class Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val config: Config
) : SymbolProcessor {
    private var containerMetadata: ContainerMetadata? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(GrantContainer::class.qualifiedName!!)
            .filter(KSAnnotated::validate)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        if (symbols.isEmpty()) {
            return emptyList()
        }

        if (symbols.size > 1) {
            throw IllegalStateException("Only one object can be annotated with `@GrantContainer`")
        }

        val container = symbols.first()

        // Store metadata for finish stage
        containerMetadata = ContainerMetadata(
            containerName = container.simpleName.asString(),
            containerPackage = container.packageName.asString(),
            containerQualifiedName = container.qualifiedName!!.asString()
        )

        // Generate Accessors.kt that mimics @GrantContainer marked class
        generateAccessors(container)

        return emptyList()
    }

    override fun finish() {
        val metadata = containerMetadata ?: return

        // Write PermissionContext object with appropriate implementations
        generatePermissionContext(metadata)
    }

    private fun generateAccessors(container: KSClassDeclaration) {
        val containerName = container.simpleName.asString()
        val accessorName = "${containerName}Accessor"
        val containerPackage = container.packageName.asString()

        // Build the accessor interface
        val accessorInterface = TypeSpec.interfaceBuilder(accessorName)
            .addModifiers(KModifier.SEALED)
            .apply {
                addAccessorMembers(this, container, containerPackage)
            }
            .build()

        val fileSpec = FileSpec.builder(config.generatedPackage, "Accessors")
            .addType(accessorInterface)
            .addImport(containerPackage, containerName)
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(true, container.containingFile!!))
    }

    private fun addAccessorMembers(
        typeBuilder: TypeSpec.Builder,
        declaration: KSClassDeclaration,
        containerPackage: String,
        pathPrefix: String = ""
    ) {
        val containerName = declaration.simpleName.asString()
        val fullPath = if (pathPrefix.isEmpty()) containerName else "$pathPrefix.$containerName"

        declaration.declarations
            .filterIsInstance<KSPropertyDeclaration>()
            .forEach { property ->
                val propertyName = property.simpleName.asString()
                val propertyType = property.type.resolve().declaration

                when {
                    // If it's a Grant property
                    propertyType.qualifiedName?.asString() == "dev.stashy.ktgrants.permissions.data.Grant" -> {
                        typeBuilder.addProperty(
                            PropertySpec.builder(
                                propertyName,
                                ClassName("dev.stashy.ktgrants.permissions.data", "Grant")
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
                        addAccessorMembers(this, nestedClass, containerPackage, fullPath)
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

    private fun generatePermissionContext(metadata: ContainerMetadata) {
        val accessorInterfaceName = "${metadata.containerName}Accessor"

        // Create PermissionContext object
        val permissionContextObject = TypeSpec.objectBuilder("PermissionContext")
            .addSuperinterface(ClassName("dev.stashy.ktgrants.permissions.api", "PermissionDsl"))
            .addSuperinterface(ClassName(config.generatedPackage, accessorInterfaceName))
            .build()

        // Create the permission() function
        val permissionFunction = FunSpec.builder("permission")
            .addModifiers(KModifier.INLINE)
            .addParameter(
                ParameterSpec.builder(
                    "fn",
                    LambdaTypeName.get(
                        receiver = ClassName(config.generatedPackage, "PermissionContext"),
                        returnType = ClassName("dev.stashy.ktgrants.permissions.data", "Permission")
                    )
                ).build()
            )
            .returns(ClassName("dev.stashy.ktgrants.permissions.data", "Permission"))
            .addCode("return fn(PermissionContext)")
            .build()

        val fileSpec = FileSpec.builder(config.generatedPackage, "Context")
            .addType(permissionContextObject)
            .addFunction(permissionFunction)
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(false))
    }

    private data class ContainerMetadata(
        val containerName: String,
        val containerPackage: String,
        val containerQualifiedName: String
    )
}

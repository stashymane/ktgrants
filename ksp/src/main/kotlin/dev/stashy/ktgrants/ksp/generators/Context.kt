package dev.stashy.ktgrants.ksp.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import dev.stashy.ktgrants.ksp.Config
import dev.stashy.ktgrants.ksp.data.GrantObjectMetadata
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.api.PermissionDsl

context(codeGenerator: CodeGenerator, config: Config)
internal fun generatePermissionContext(metadata: GrantObjectMetadata) {
    val accessorInterfaceName = "${metadata.objectName}Accessor"

    // Create PermissionContext object
    val permissionContextObject = TypeSpec.objectBuilder("PermissionContext")
        .addSuperinterface(PermissionDsl::class.asClassName())
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
                    returnType = Permission::class.asClassName()
                )
            ).build()
        )
        .returns(Permission::class.asClassName())
        .addCode("return fn(PermissionContext)")
        .build()

    val fileSpec = FileSpec.builder(config.generatedPackage, "Context")
        .addType(permissionContextObject)
        .addFunction(permissionFunction)
        .build()

    fileSpec.writeTo(codeGenerator, Dependencies(false))
}

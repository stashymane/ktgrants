package dev.stashy.ktgrants.ksp.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import dev.stashy.ktgrants.ksp.Config
import dev.stashy.ktgrants.ksp.Names
import dev.stashy.ktgrants.ksp.data.GrantObjectMetadata
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.api.PermissionOwner
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl

context(codeGenerator: CodeGenerator, config: Config)
internal fun generatePermissionContext(metadata: GrantObjectMetadata) {
    val accessorInterfaceName = "${metadata.objectName}Accessor"

    // Create PermissionContext object
    val permissionContextObject = TypeSpec.objectBuilder(Names.CONTEXT_OBJECT)
        .addSuperinterface(GrantDsl::class)
        .addSuperinterface(ClassName(config.generatedPackage, accessorInterfaceName))
        .build()
    val permissionContextName = ClassName(config.generatedPackage, Names.CONTEXT_OBJECT)

    // Create the permission() function
    val permissionFunction = FunSpec.builder(Names.PERMISSION_FUNCTION)
        .addModifiers(KModifier.INLINE)
        .addParameter(
            ParameterSpec.builder(
                "fn",
                LambdaTypeName.get(
                    receiver = permissionContextName,
                    returnType = Permission::class.asClassName()
                )
            ).build()
        )
        .returns(Permission::class)
        .addCode("return fn(${Names.CONTEXT_OBJECT})")
        .build()

    val permissionExtension = FunSpec.builder("hasPermission").apply {
        modifiers += KModifier.INLINE
        receiver(PermissionOwner::class)
        parameters += ParameterSpec.builder(
            "fn",
            LambdaTypeName.get(
                receiver = permissionContextName,
                returnType = Permission::class.asClassName()
            )
        ).build()
        returns(Boolean::class)
        addCode("return hasPermission(fn(${Names.CONTEXT_OBJECT}))")
    }.build()

    val fileSpec = FileSpec.builder(config.generatedPackage, "Context")
        .addType(permissionContextObject)
        .addFunction(permissionFunction)
        .addFunction(permissionExtension)
        .build()

    fileSpec.writeTo(codeGenerator, Dependencies(false))
}

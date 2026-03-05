package dev.stashy.ktgrants.permissions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object PermissionSerializer : KSerializer<Permission> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.stashy.ktgrants.permissions.Permission", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Permission
    ) {
        encoder.encodeString(value.asString())
    }

    override fun deserialize(decoder: Decoder): Permission {
        val string = decoder.decodeString()
        return Permission.parse(string)
    }
}

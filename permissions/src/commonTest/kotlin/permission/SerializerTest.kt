package permission

import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.Scope
import dev.stashy.ktgrants.permissions.Subject
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class SerializerTest {
    val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun serialize() {
        val permission = Permission(Scope("scope"), Subject("subject"), Grant("grant"))
        val serialized = json.encodeToString(permission)
        serialized shouldBe jsonString("scope:subject:grant")
    }

    @Test
    fun `serialize wildcard`() {
        val permission = Permission(Scope("*"), Subject("*"), Grant("*"))
        val serialized = json.encodeToString(permission)
        serialized shouldBe jsonString("*:*:*")
    }

    @Test
    fun deserialize() {
        val serialized = jsonString("scope:subject:grant")
        val permission = json.decodeFromString<Permission>(serialized)
        permission shouldBe Permission(Scope("scope"), Subject("subject"), Grant("grant"))
    }

    @Test
    fun feedback() {
        val permission = Permission(Scope("scope"), Subject("subject"), Grant("grant"))
        val serialized = json.encodeToString(permission)
        val deserialized = json.decodeFromString<Permission>(serialized)
        permission shouldBe deserialized
    }

    private fun jsonString(value: String) = "\"$value\""
}

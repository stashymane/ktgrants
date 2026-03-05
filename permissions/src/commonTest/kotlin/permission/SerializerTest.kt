package permission

import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.Group
import dev.stashy.ktgrants.permissions.Permission
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
        val permission = Permission(Group("group"), Subject("subject"), Grant("grant"))
        val serialized = json.encodeToString(permission)
        serialized shouldBe jsonString("group:subject:grant")
    }

    @Test
    fun `serialize wildcard`() {
        val permission = Permission(Group("*"), Subject("*"), Grant("*"))
        val serialized = json.encodeToString(permission)
        serialized shouldBe jsonString("*:*:*")
    }

    @Test
    fun deserialize() {
        val serialized = jsonString("group:subject:grant")
        val permission = json.decodeFromString<Permission>(serialized)
        permission shouldBe Permission(Group("group"), Subject("subject"), Grant("grant"))
    }

    @Test
    fun feedback() {
        val permission = Permission(Group("group"), Subject("subject"), Grant("grant"))
        val serialized = json.encodeToString(permission)
        val deserialized = json.decodeFromString<Permission>(serialized)
        permission shouldBe deserialized
    }

    private fun jsonString(value: String) = "\"$value\""
}

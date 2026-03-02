package permission

import Foo
import Grants
import Id
import dev.stashy.ktgrants.permissions.api.PermissionDsl.Companion.any
import dev.stashy.ktgrants.permissions.api.PermissionDsl.Companion.on
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DslTest {
    @Test
    fun `dsl on object type`() {
        val perm = Grants.Read any Foo

        Foo.group shouldBe perm.group
        Grants.Read shouldBe perm.grant
    }

    @Test
    fun `dsl on object instance`() {
        val foo = Foo(Id("hello"), "bar")
        val perm = Grants.Read on foo

        foo.group shouldBe perm.group
        foo.subject shouldBe perm.subject
        Grants.Read shouldBe perm.grant
    }
}

package permission

import Bar
import Foo
import Grants.Create
import Grants.Delete
import Grants.FullControl
import Grants.Owner
import Grants.Read
import Grants.Write
import Id
import dev.stashy.ktgrants.kotest.shouldInclude
import dev.stashy.ktgrants.kotest.shouldNotInclude
import dev.stashy.ktgrants.permissions.PermissionModel
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.any
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.on
import kotlin.test.Test

class PermissionGraphTest {
    @Test
    fun `grant expansion`() {
        val model = PermissionModel.build {
            graph {
                FullControl provides setOf(Read, Write, Create, Delete)
            }
        }
        val foo = Foo(Id("hello"), "bar")

        val parentGrant = FullControl
        val childGrant = Read
        val unrelatedGrant = Owner

        val permissions = model.process(sequenceOf(parentGrant on foo))

        permissions shouldInclude (childGrant on foo)
        permissions shouldInclude (parentGrant on foo)
        permissions shouldNotInclude (unrelatedGrant on foo)
    }

    @Test
    fun `subject wildcard`() {
        val model = PermissionModel.build {
            wildcard {
                subject = true
            }
        }

        val foo = Foo(Id("foo1"), "bar")
        val foo2 = Foo(Id("foo2"), "baz")
        val bar = Bar(Id("bar"), "bar")

        val wildcardPermission = Read any Foo
        val permissions = model.process(sequenceOf(wildcardPermission))

        permissions shouldInclude (Read on foo)
        permissions shouldInclude (Read on foo2)
        permissions shouldInclude wildcardPermission

        permissions shouldNotInclude (Read on bar)
        permissions shouldNotInclude (Write on foo)
    }
}

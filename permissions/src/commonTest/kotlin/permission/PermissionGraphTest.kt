package permission

import Bar
import Foo
import Grants.Access
import Grants.Admin
import Grants.Create
import Grants.Delete
import Grants.FullControl
import Grants.Owner
import Grants.Read
import Grants.Write
import Id
import System
import dev.stashy.ktgrants.kotest.shouldInclude
import dev.stashy.ktgrants.kotest.shouldNotInclude
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionPolicy
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.any
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.on
import kotlin.test.Test

class PermissionGraphTest {
    @Test
    fun `grant expansion`() {
        val model = PermissionPolicy.build {
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
        val model = PermissionPolicy.build {
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

    @Test
    fun `resolver order`() {
        val defaultPermission = Permission(System.scope, Subject.Any, Access)
        val accessFooPermission = Permission(Foo.scope, Subject.Any, Access)
        val readFooPermission = Permission(Foo.scope, Subject.Any, Read)
        val accessSpecificFooPermission = Permission(Foo.scope, Subject("identifier"), Access)
        val readSpecificFooPermission = Permission(Foo.scope, Subject("identifier"), Read)

        val model = PermissionPolicy.build {
            defaults = setOf(Permission(System.scope, Subject.Any, Access))

            graph {
                FullControl provides setOf(Access, Read, Write, Create, Delete)
                Admin provides FullControl
            }

            wildcard {
                subject = true
            }

            generator { permission ->
                yield(permission)

                if (permission.scope == System.scope) {
                    sequenceOf(Foo.scope, Bar.scope).forEach {
                        yield(permission.copy(scope = it))
                    }
                }
            }
        }

        val defaultUser = model.process(sequenceOf())
        val adminUser = model.process(sequenceOf(Permission(System.scope, Subject.Any, Admin)))

        defaultUser shouldInclude defaultPermission
        adminUser shouldInclude defaultPermission

        defaultUser shouldInclude accessFooPermission
        adminUser shouldInclude accessFooPermission

        defaultUser shouldNotInclude readFooPermission
        adminUser shouldInclude readFooPermission

        defaultUser shouldInclude accessSpecificFooPermission
        adminUser shouldInclude accessSpecificFooPermission

        defaultUser shouldNotInclude readSpecificFooPermission
        adminUser shouldInclude readSpecificFooPermission
    }
}

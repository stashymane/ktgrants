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
import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.any
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.on
import kotlin.test.Test

class PermissionGraphTest {
    @Test
    fun `grant expansion`() {
        val model = PermissionResolver.build {
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
        val model = PermissionResolver.build {
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
        val defaultPermission = Permission(System.group, Subject.Any, Access)
        val accessFooPermission = Permission(Foo.group, Subject.Any, Access)
        val readFooPermission = Permission(Foo.group, Subject.Any, Read)
        val accessSpecificFooPermission = Permission(Foo.group, Subject("identifier"), Access)
        val readSpecificFooPermission = Permission(Foo.group, Subject("identifier"), Read)

        val model = PermissionResolver.build {
            defaults = setOf(Permission(System.group, Subject.Any, Access))

            graph {
                FullControl provides setOf(Access, Read, Write, Create, Delete)
                Admin provides FullControl
            }

            wildcard {
                subject = true
            }

            generator { permission ->
                yield(permission)

                if (permission.group == System.group) {
                    sequenceOf(Foo.group, Bar.group).forEach {
                        yield(permission.copy(group = it))
                    }
                }
            }
        }

        val defaultUser = model.process(sequenceOf())
        val adminUser = model.process(sequenceOf(Permission(System.group, Subject.Any, Admin)))

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

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
import dev.stashy.ktgrants.permissions.Scope
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.any
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.on
import dev.stashy.ktgrants.permissions.config.provides
import kotlin.test.Test

class PermissionGraphTest {
    @Test
    fun `grant expansion`() {
        val model = PermissionPolicy.build {
            grants {
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
    fun `scope and subject expansion`() {
        val model = PermissionPolicy.build {
            scopes {
                Scope("parent") provides setOf(Scope("child"))
            }
            subjects {
                Subject("parent") provides setOf(Subject("child"))
            }
        }

        val parentScope = Scope("parent")
        val childScope = Scope("child")
        val parentSubject = Subject("parent")
        val childSubject = Subject("child")
        val grant = Read

        val permissions = model.process(sequenceOf(Permission(parentScope, parentSubject, grant)))

        permissions shouldInclude Permission(parentScope, parentSubject, grant)
        permissions shouldInclude Permission(childScope, parentSubject, grant)
        permissions shouldInclude Permission(parentScope, childSubject, grant)
        permissions shouldInclude Permission(childScope, childSubject, grant)
    }

    @Test
    fun `typed expansion`() {
        val model = PermissionPolicy.build {
            scopes {
                System provides setOf(Scope("bar"))
            }
            subjects {
                Id("parent") provides setOf(Id("child"))
            }
            grants {
                FullControl provides setOf(Read, Write)
            }
        }

        val permissions = model.process(
            sequenceOf(
                Permission(System.toScope(), Id("parent").toSubject(), FullControl)
            )
        )

        permissions shouldInclude Permission(System.toScope(), Id("parent").toSubject(), FullControl)
        permissions shouldInclude Permission(Scope("bar"), Id("parent").toSubject(), Read)
        permissions shouldInclude Permission(System.toScope(), Id("child").toSubject(), Write)
        permissions shouldInclude Permission(Scope("bar"), Id("child").toSubject(), Read)
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

            grants {
                FullControl provides setOf(Access, Read, Write, Create, Delete)
                Admin provides setOf(FullControl)
            }

            scopes {
                System provides setOf(Foo, Bar)
            }

            wildcard {
                subject = true
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

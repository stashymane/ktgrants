package dev.stashy.ktgrants.permissions.data

import dev.stashy.ktgrants.annotations.GrantContainer
import dev.stashy.ktgrants.permissions.api.Permissible
import dev.stashy.ktgrants.permissions.api.PermissionDsl

// actual user code
@GrantContainer
private object Grants {
    public val Read: Grant = Grant("read")
    public val Write: Grant = Grant("write")

    public object Admin {
        public val System: Grant = Grant("system")

        public object Test {
            public val Foo: Grant = Grant("test")
        }
    }
}

// generated accessors (Accessors.kt)
// top-most level must be an interface that the later PermissionContext will inherit
// inner levels can be objects with fields that reference them
// only generate accessors for Grants and sub-objects
private sealed interface GrantsAccessor {
    public val Read: Grant get() = Grants.Read
    public val Write: Grant get() = Grants.Write
    public val Admin: AdminAccessor get() = AdminAccessor

    public object AdminAccessor {
        public val System: Grant get() = Grants.Admin.System
        public val Test: TestAccessor get() = TestAccessor

        public object TestAccessor {
            public val Foo: Grant get() = Grants.Admin.Test.Foo
        }
    }
}

// generated permission context (Context.kt)
// inherits static PermissionDsl interface (non-generated), as well as accessors generated in earlier stages
private object PermissionContext : PermissionDsl, GrantsAccessor

private inline fun permission(fn: PermissionContext.() -> Permission): Permission =
    fn(PermissionContext)

// consumer example - only `permission {}` should need to be imported, *nothing* else
private fun usage() {
    permission {
        Read any Permissible.Type.Snapshot(Group("hi"))
    }
    permission {
        Admin.System any Group("*")
    }
    permission {
        Admin.Test.Foo any Group("*")
    }
}

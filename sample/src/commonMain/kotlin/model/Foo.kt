package model

import dev.stashy.ktgrants.permissions.api.Permissible

data class Foo(
    val id: Id,
    val content: String
) : Permissible.Entity by Foo.withSubject(id) {
    companion object : Permissible.Type by Permissible<Foo>()
}

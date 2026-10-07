package authorization

import Id
import dev.stashy.ktgrants.authorization.api.Authorized
import dev.stashy.ktgrants.authorization.api.Authorizer
import dev.stashy.ktgrants.authorization.or
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.Permissible
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AuthorizedTest {
    @Test
    fun `authorizer getter forwards to authorizer object`() {
        val foo = AuthorizedFoo(Id("foo"), "test")

        assertSame(AuthorizedFooAuthorizer, foo.authorizer)
    }

    @Test
    fun `composes with permissible entity`() {
        val foo = AuthorizedFoo(Id("foo"), "test")

        assertEquals(Subject("foo"), foo.subject)
        assertEquals(AuthorizedFoo.scope, foo.scope)
    }
}

private object AuthorizedFooAuthorizer : Authorizer<AuthorizedFoo, AuthModel> by Authorizer({
    on(Reading or Creating) {
        ensure { true }
    }
}), Authorized<AuthorizedFoo, AuthModel> {
    override val authorizer get() = this
}

private data class AuthorizedFoo(
    val id: Id,
    val name: String,
) : Permissible.Entity by AuthorizedFoo.withSubject(id),
    Authorized<AuthorizedFoo, AuthModel> by AuthorizedFooAuthorizer {
    companion object : Permissible.Type by Permissible.Type<AuthorizedFoo>()
}

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
        val foo = ContextuallyAuthorizedFoo(Id("foo"), "test")

        assertSame(ContextuallyAuthorizedFooAuthorizer, foo.authorizer)
    }

    @Test
    fun `composes with permissible entity`() {
        val foo = ContextuallyAuthorizedFoo(Id("foo"), "test")

        assertEquals(Subject("foo"), foo.subject)
        assertEquals(ContextuallyAuthorizedFoo.scope, foo.scope)
    }
}

private object ContextuallyAuthorizedFooAuthorizer : Authorizer<ContextuallyAuthorizedFoo, AuthModel> by Authorizer({
    on(Reading or Creating) {
        ensure { true }
    }
}), Authorized<ContextuallyAuthorizedFoo, AuthModel> {
    override val authorizer get() = this
}

private data class ContextuallyAuthorizedFoo(
    val id: Id,
    val name: String,
) : Permissible.Entity by ContextuallyAuthorizedFoo.withSubject(id),
    Authorized<ContextuallyAuthorizedFoo, AuthModel> by ContextuallyAuthorizedFooAuthorizer {
    companion object : Permissible.Type by Permissible.Type<ContextuallyAuthorizedFoo>()
}

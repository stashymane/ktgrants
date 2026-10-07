package authorization

import Id
import dev.stashy.ktgrants.authorization.api.Authorized
import dev.stashy.ktgrants.authorization.api.Authorizer
import dev.stashy.ktgrants.authorization.or
import dev.stashy.ktgrants.permissions.api.Permissible
import dev.stashy.ktgrants.permissions.api.hasPermission
import dev.stashy.ktgrants.permissions.dsl.GrantDsl.Companion.on
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthorizableTest {
    private val foo = AuthorizableFoo(Id("foo"), "test")
    private val config = AuthModel

    @Test
    fun `authorize delegates to entity authorizer`() {
        val actor = config.actor(foo, setOf(config.Read on foo))

        with(config) {
            assertTrue(actor.authorize(Reading, foo))
            assertTrue(actor.authorize(Creating, foo))
            assertFalse(actor.authorize(Writing, foo))
        }
    }
}

private object AuthorizableFooAuthorizer : Authorizer<AuthorizableFoo, AuthModel> by Authorizer({
    on(Reading or Creating) {
        ensure { actor.hasPermission { Read on target } }
    }
}), Authorized<AuthorizableFoo, AuthModel> {
    override val authorizer get() = this
}

private data class AuthorizableFoo(
    val id: Id,
    val name: String,
) : Permissible.Entity by AuthorizableFoo.withSubject(id),
    Authorized<AuthorizableFoo, AuthModel> by AuthorizableFooAuthorizer {
    companion object : Permissible.Type by Permissible.Type<AuthorizableFoo>()
}

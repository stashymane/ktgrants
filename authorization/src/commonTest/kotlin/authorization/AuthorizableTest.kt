package authorization

import Foo
import Id
import dev.stashy.ktgrants.authorization.api.authorize
import dev.stashy.ktgrants.permissions.dsl.GrantDsl.Companion.on
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthorizableTest {
    private val foo = Foo(Id("foo"), "test")
    private val config = AuthModel

    @Test
    fun `authorize delegates to entity authorizer`() {
        val actor = config.actor(foo, setOf(config.Read on foo))

        context(config) {
            assertTrue(actor.authorize { Reading the foo })
            assertTrue(actor.authorize { Creating the foo })
            assertFalse(actor.authorize { Writing the foo })
        }
    }
}

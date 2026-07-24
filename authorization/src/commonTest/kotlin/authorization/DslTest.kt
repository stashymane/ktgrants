package authorization

import Foo
import Id
import dev.stashy.ktgrants.authorization.api.Actionable
import dev.stashy.ktgrants.authorization.api.ContextualActionable
import dev.stashy.ktgrants.authorization.or
import dev.stashy.ktgrants.permissions.api.hasPermission
import dev.stashy.ktgrants.permissions.dsl.GrantDsl.Companion.on
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DslTest {
    private val foo = Foo(Id("foo"), "test")
    private val config = AuthModel

    @Test
    fun `actionable verification`() {
        val authorizer = Actionable<Foo, AuthModel> {
            on(Reading or Creating) {
                ensure { actor.hasPermission { Read on target } }
            }
        }
        val actor = config.actor(foo, setOf(config.Read on foo))

        with(config) {
            assertTrue(authorizer.verifyAction(foo, actor, Reading))
            assertTrue(authorizer.verifyAction(foo, actor, Creating))
            assertFalse(authorizer.verifyAction(foo, actor, Writing))
        }
    }

    @Test
    fun `contextual actionable verification`() {
        val authorizer =
            ContextualActionable<Foo, AuthModel, String> { reason ->
                on(Writing) {
                    ensure { reason == "allowed" }
                }
            }
        val actor = config.actor(foo, emptySet())

        with(config) {
            assertTrue(authorizer.verifyAction(foo, actor, Writing, "allowed"))
            assertFalse(authorizer.verifyAction(foo, actor, Writing, "denied"))
        }
    }
}
